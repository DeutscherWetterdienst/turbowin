package format101;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class Encoder {

    private Encoder() {}

    public record EncodedMessage(
            String stationIdRaw,
            String stationId,
            String template,
            byte[] payloadText,
            byte[] payloadOctets,
            int payloadBits
    ) {
        public String toHpkLine() {
            return stationIdRaw + Codec6.latin1String(payloadText);
        }
    }

    public static EncodedMessage encodeFromFormat101Txt(Path piloteCsv, Path format101Txt, String stationId) throws IOException {
        stationId = stationId.trim();
        if (stationId.length() < 1 || stationId.length() > 7) {
            throw new IllegalArgumentException("station_id must be 1..7 characters");
        }
        String stationIdRaw = String.format("%7s", stationId);

        List<PilotEntry> all = SpecLoader.loadPiloteCsv(piloteCsv);
        List<PilotEntry> pilote = SpecLoader.skipOperatingMode000000(all);

        Format101TxtParser.ParsedLine[] values = Format101TxtParser.parse(format101Txt, pilote.size());

        // Locate indices dynamically (robust against minor pilote differences)
        int idxSst = -1;
        for (int i = 0; i < pilote.size(); i++) {
            if ("022042".equals(pilote.get(i).bufr())) {
                idxSst = i;
                break;
            }
        }
        if (idxSst < 0) {
            throw new IllegalArgumentException("Pilote does not contain 022042 (SST) field");
        }

        int idxVisualMarker = idxSst + 1; // 410000
        if (idxVisualMarker >= pilote.size() || !"410000".equals(pilote.get(idxVisualMarker).bufr())) {
            throw new IllegalArgumentException("Pilote does not contain 410000 visual marker at expected position");
        }

        // Find the first and second 408000 markers after the visual fields
        int idxFirst408000 = -1;
        int idxSecond408000 = -1;
        for (int i = idxVisualMarker + 1; i < pilote.size(); i++) {
            if ("408000".equals(pilote.get(i).bufr())) {
                if (idxFirst408000 < 0) idxFirst408000 = i;
                else {
                    idxSecond408000 = i;
                    break;
                }
            }
        }
        if (idxFirst408000 < 0 || idxSecond408000 < 0) {
            throw new IllegalArgumentException("Pilote does not contain two 408000 markers as expected");
        }

        // Legacy block sizes (TurboWin legacy S-AWS-101)
        final int VISUAL_COUNT = 10;
        final int WAVE_COUNT = 8;
        final int ICE_COUNT = 8;

        int visualFieldsStart = idxVisualMarker + 1;
        int visualFieldsEnd = visualFieldsStart + VISUAL_COUNT; // exclusive
        if (visualFieldsEnd != idxFirst408000) {
            throw new IllegalArgumentException(
                    "Unexpected pilote layout: visual block size does not match legacy (expected 10 fields)"
            );
        }

        int waveFieldsStart = idxFirst408000 + 1;
        int waveFieldsEnd = waveFieldsStart + WAVE_COUNT; // exclusive
        if (waveFieldsEnd != idxSecond408000) {
            throw new IllegalArgumentException(
                    "Unexpected pilote layout: wave block size does not match legacy (expected 8 fields)"
            );
        }

        int iceFieldsStart = idxSecond408000 + 1;
        int iceFieldsEnd = iceFieldsStart + ICE_COUNT; // exclusive
        if (iceFieldsEnd != pilote.size()) {
            throw new IllegalArgumentException(
                    "Unexpected pilote layout: ice block size does not match legacy (expected 8 fields at end)"
            );
        }

        BitWriter bw = new BitWriter();

        // Helper: encode a normal pilote entry from format_101.txt line (present/value)
        java.util.function.IntConsumer encodeEntry = (int i) -> {
            PilotEntry e = pilote.get(i);
            boolean present = values[i].present();
            Double v = values[i].value();

            int raw;
            if (!present || v == null) {
                raw = (1 << e.nbits()) - 1;
            } else {
                raw = quantize(e, v.doubleValue());
            }
            bw.writeBits(e.nbits(), raw);
        };

        // Helper: marker value (0/1) from format_101.txt marker line
        java.util.function.IntFunction<Integer> marker01 = (int idx) -> {
            boolean present = values[idx].present();
            Double v = values[idx].value();
            if (!present || v == null) return 0;
            return (int) v.doubleValue() != 0 ? 1 : 0;
        };

        // Encode main block up to SST inclusive
        for (int i = 0; i <= idxSst; i++) {
            encodeEntry.accept(i);
        }

        // Visual marker
        int mVisual = marker01.apply(idxVisualMarker);
        bw.writeBits(pilote.get(idxVisualMarker).nbits(), mVisual);
        if (mVisual == 0) {
            return finalizeAndBuild(stationIdRaw, stationId, bw);
        }

        // Visual fields
        for (int i = visualFieldsStart; i < visualFieldsEnd; i++) {
            encodeEntry.accept(i);
        }

        // Chain marker (first 408000) controlling whether wave+ice blocks follow
        int mChain = marker01.apply(idxFirst408000);
        bw.writeBits(pilote.get(idxFirst408000).nbits(), mChain);
        if (mChain == 0) {
            return finalizeAndBuild(stationIdRaw, stationId, bw);
        }

        // Wave fields
        for (int i = waveFieldsStart; i < waveFieldsEnd; i++) {
            encodeEntry.accept(i);
        }

        // Ice marker (second 408000)
        int mIce = marker01.apply(idxSecond408000);
        bw.writeBits(pilote.get(idxSecond408000).nbits(), mIce);
        if (mIce == 0) {
            return finalizeAndBuild(stationIdRaw, stationId, bw);
        }

        // Ice fields
        for (int i = iceFieldsStart; i < iceFieldsEnd; i++) {
            encodeEntry.accept(i);
        }

        return finalizeAndBuild(stationIdRaw, stationId, bw);
    }

    private static EncodedMessage finalizeAndBuild(String stationIdRaw, String stationId, BitWriter bw) {
        bw.finalizeLegacyPadding();
        int payloadBits = bw.bitsOffset();
        byte[] payloadOctets = bw.toByteArrayWholeBytes();
        byte[] payloadText = Codec6.encodePayloadBitsToTurboWinText(payloadOctets, payloadBits);

        return new EncodedMessage(
                stationIdRaw,
                stationId,
                "S-AWS-101",
                payloadText,
                payloadOctets,
                payloadBits
        );
    }

    private static int quantize(PilotEntry entry, double value) {
        int raw;
        if (entry.factor() == 0.0) {
            raw = 0;
        } else {
            raw = (int) Math.round((value - entry.offset()) / entry.factor());
        }
        if (raw < 0) raw = 0;
        if (raw > entry.codmax()) raw = entry.codmax();
        return raw;
    }
}

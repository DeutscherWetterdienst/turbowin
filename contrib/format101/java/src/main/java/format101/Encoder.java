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

    private static String hexTail(byte[] b, int n) {
        int start = Math.max(0, b.length - n);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < b.length; i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(String.format("%02x", b[i] & 0xFF));
        }
        return sb.toString();
    }

    public static EncodedMessage encodeFromFormat101Txt(Path piloteCsv, Path format101Txt, String stationId) throws IOException {
        stationId = stationId.trim();
        if (stationId.length() < 1 || stationId.length() > 7) {
            throw new IllegalArgumentException("station_id must be 1..7 characters");
        }
        String stationIdRaw = String.format("%7s", stationId);

        List<PilotEntry> all = SpecLoader.loadPiloteCsv(piloteCsv);
        List<PilotEntry> pilote = SpecLoader.skipOperatingMode000000(all);

        // Debug: show pilote structure
        int sumBits = 0;
        String lastKey = "";
        for (PilotEntry e : pilote) {
            sumBits += e.nbits();
            lastKey = e.key();
        }
        System.err.println("encoder: pilote_path=" + piloteCsv);
        System.err.println("encoder: pilote_entries=" + pilote.size() + " sum_nbits=" + sumBits + " last_key=" + lastKey);

        Format101TxtParser.ParsedLine[] values = Format101TxtParser.parse(format101Txt, pilote.size());

        BitWriter bw = new BitWriter();

        // Fixed layout: write all fields
        for (int i = 0; i < pilote.size(); i++) {
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
        }

        // finalize padding (legacy)
        bw.finalizeLegacyPadding();

        int payloadBits = bw.bitsOffset();
        byte[] payloadOctets = bw.toByteArrayWholeBytes();
        byte[] payloadText = Codec6.encodePayloadOctetsToTurboWinText(payloadOctets);

        // Debug info for cross-checking against reference vectors
        System.err.println("encoder: payload_bits=" + payloadBits);
        System.err.println("encoder: payload_octets_len=" + payloadOctets.length + " tail16=" + hexTail(payloadOctets, 16));
        System.err.println("encoder: payload_text_len=" + payloadText.length + " tail16=" + hexTail(payloadText, 16));
        byte[] roundTripOctets = Codec6.decodeTurboWinTextToOctets(payloadText);
        boolean rt = java.util.Arrays.equals(roundTripOctets, payloadOctets);
        System.err.println("encoder: text_roundtrip_octets=" + rt + " rt_octets_len=" + roundTripOctets.length);

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

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

package format101;

import java.nio.charset.StandardCharsets;

public final class Codec6 {

    private Codec6() {}

    public static byte[] compact6BitWordsToOctets(byte[] sixBitWords) {
        java.util.ArrayList<Byte> octets = new java.util.ArrayList<>();
        int buf = 0;
        int[] rss = {0, 4, 0, 2, 0, 0};
        int[] lss = {2, 0, 4, 0, 6, 0};
        int[] bcs = {6, 2, 4, 4, 2, 6};
        int i = 0;
        int oc = 0;
        int bc = 0;
        while (oc < sixBitWords.length) {
            bc += bcs[i];
            int b = sixBitWords[oc] & 0x3F;
            buf |= (((b >> rss[i]) << lss[i]) & 0xFF);
            if (i == 1 || i == 3 || i == 5) {
                octets.add((byte) buf);
                buf = 0;
            }
            i++;
            if (i == 6) {
                i = 0;
            }
            if (bc == 6) {
                bc = 0;
                oc++;
            }
        }
        byte[] out = new byte[octets.size()];
        for (int j = 0; j < octets.size(); j++) {
            out[j] = octets.get(j);
        }
        return out;
    }

    public static byte[] decodeTurboWinTextToOctets(byte[] payloadText) {
        byte[] six = new byte[payloadText.length];
        for (int i = 0; i < payloadText.length; i++) {
            int b = payloadText[i] & 0xFF;
            six[i] = (byte) (((b - 0x40) & 0x3F));
        }
        return compact6BitWordsToOctets(six);
    }

    public static byte[] encodeOctetsTo6BitWords(byte[] octets) {
        if (octets.length == 0) {
            return new byte[0];
        }

        byte[] out = new byte[((octets.length / 3) * 4) + (octets.length % 3 == 0 ? 0 : (octets.length % 3 == 1 ? 2 : 3))];

        int oi = 0;
        int wi = 0;

        while (oi + 3 <= octets.length) {
            int o0 = octets[oi] & 0xFF;
            int o1 = octets[oi + 1] & 0xFF;
            int o2 = octets[oi + 2] & 0xFF;

            int w0 = (o0 >> 2) & 0x3F;
            int w1 = ((o0 & 0x03) << 4) | ((o1 >> 4) & 0x0F);
            int w2 = ((o1 & 0x0F) << 2) | ((o2 >> 6) & 0x03);
            int w3 = o2 & 0x3F;

            out[wi++] = (byte) w0;
            out[wi++] = (byte) w1;
            out[wi++] = (byte) w2;
            out[wi++] = (byte) w3;

            oi += 3;
        }

        int rem = octets.length - oi;
        if (rem == 1) {
            int o0 = octets[oi] & 0xFF;
            int w0 = (o0 >> 2) & 0x3F;
            int w1 = ((o0 & 0x03) << 4);
            out[wi++] = (byte) w0;
            out[wi++] = (byte) w1;
        } else if (rem == 2) {
            int o0 = octets[oi] & 0xFF;
            int o1 = octets[oi + 1] & 0xFF;
            int w0 = (o0 >> 2) & 0x3F;
            int w1 = ((o0 & 0x03) << 4) | ((o1 >> 4) & 0x0F);
            int w2 = ((o1 & 0x0F) << 2);
            out[wi++] = (byte) w0;
            out[wi++] = (byte) w1;
            out[wi++] = (byte) w2;
        }

        return out;
    }

    public static byte[] encodePayloadOctetsToTurboWinText(byte[] octets) {
        byte[] words = encodeOctetsTo6BitWords(octets);
        byte[] out = new byte[words.length];
        for (int i = 0; i < words.length; i++) {
            out[i] = (byte) ((words[i] & 0x3F) + 0x40);
        }
        return out;
    }

    public static String latin1String(byte[] bytes) {
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }
}

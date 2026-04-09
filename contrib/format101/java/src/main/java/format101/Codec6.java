package format101;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    public static byte[] encodeOctetsTo6BitTextBase(byte[] octets) {
        if (octets.length == 0) {
            return new byte[0];
        }
        int nbits = octets.length * 8;
        int nwords = (nbits + 5) / 6;
        byte[] out = new byte[nwords];

        int bitpos = 0;
        for (int w = 0; w < nwords; w++) {
            int val = 0;
            for (int i = 0; i < 6; i++) {
                if (bitpos >= nbits) {
                    val <<= 1;
                } else {
                    int byteIdx = bitpos / 8;
                    int bitInByte = bitpos % 8;
                    int bit = (octets[byteIdx] >> (7 - bitInByte)) & 1;
                    val = (val << 1) | bit;
                    bitpos++;
                }
            }
            out[w] = (byte) ((val & 0x3F) + 0x40);
        }
        return out;
    }

    private static byte[] minimizeTextLength(byte[] base, byte[] targetOctets) {
        byte[] cur = base;
        while (cur.length > 0) {
            byte[] shorter = Arrays.copyOf(cur, cur.length - 1);
            if (Arrays.equals(decodeTurboWinTextToOctets(shorter), targetOctets)) {
                cur = shorter;
                continue;
            }
            break;
        }
        return cur;
    }

    private static byte[] findLexicographicallySmallestTail(byte[] prefix, int k, byte[] targetOctets) {
        if (k == 1) {
            for (int w0 = 0; w0 < 64; w0++) {
                byte[] cand = new byte[prefix.length + 1];
                System.arraycopy(prefix, 0, cand, 0, prefix.length);
                cand[cand.length - 1] = (byte) (0x40 + w0);
                if (Arrays.equals(decodeTurboWinTextToOctets(cand), targetOctets)) {
                    return cand;
                }
            }
            return null;
        }
        if (k == 2) {
            for (int w0 = 0; w0 < 64; w0++) {
                for (int w1 = 0; w1 < 64; w1++) {
                    byte[] cand = new byte[prefix.length + 2];
                    System.arraycopy(prefix, 0, cand, 0, prefix.length);
                    cand[cand.length - 2] = (byte) (0x40 + w0);
                    cand[cand.length - 1] = (byte) (0x40 + w1);
                    if (Arrays.equals(decodeTurboWinTextToOctets(cand), targetOctets)) {
                        return cand;
                    }
                }
            }
            return null;
        }
        throw new IllegalArgumentException("Unsupported tail length: " + k);
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

    public static byte[] encodePayloadOctetsToTurboWinText(byte[] octets) {
        byte[] base = encodeOctetsTo6BitTextBase(octets);
        if (base.length == 0) {
            System.err.println("codec6: empty base");
            return base;
        }

        boolean baseRoundTrip = Arrays.equals(decodeTurboWinTextToOctets(base), octets);
        System.err.println("codec6: base_len=" + base.length + " base_roundtrip=" + baseRoundTrip);
        System.err.println("codec6: base_tail16=" + hexTail(base, 16));

        if (!baseRoundTrip) {
            System.err.println("codec6: base does not round-trip, returning base");
            return base;
        }

        byte[] minimized = minimizeTextLength(base, octets);
        System.err.println("codec6: minimized_len=" + minimized.length);
        System.err.println("codec6: minimized_tail16=" + hexTail(minimized, 16));

        byte[] cand1 = null;
        byte[] cand2 = null;

        if (minimized.length >= 1) {
            byte[] prefix = Arrays.copyOf(minimized, minimized.length - 1);
            cand1 = findLexicographicallySmallestTail(prefix, 1, octets);
        }
        if (minimized.length >= 2) {
            byte[] prefix = Arrays.copyOf(minimized, minimized.length - 2);
            cand2 = findLexicographicallySmallestTail(prefix, 2, octets);
        }

        System.err.println("codec6: cand1=" + (cand1 != null) + " cand2=" + (cand2 != null));

        byte[] chosen;
        if (cand1 != null) {
            chosen = cand1;
        } else if (cand2 != null) {
            chosen = cand2;
        } else {
            chosen = minimized;
        }

        System.err.println("codec6: chosen_len=" + chosen.length);
        System.err.println("codec6: chosen_tail16=" + hexTail(chosen, 16));
        return chosen;
    }

    public static String latin1String(byte[] bytes) {
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }
}

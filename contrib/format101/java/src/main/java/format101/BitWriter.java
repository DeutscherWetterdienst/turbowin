package format101;

public final class BitWriter {

    private final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
    private int bitsOffset = 0;

    public int bitsOffset() {
        return bitsOffset;
    }

    public byte[] toByteArrayWholeBytes() {
        int neededBytes = (bitsOffset + 7) / 8;
        byte[] buf = out.toByteArray();
        if (buf.length == neededBytes) {
            return buf;
        }
        byte[] trimmed = new byte[neededBytes];
        System.arraycopy(buf, 0, trimmed, 0, Math.min(buf.length, neededBytes));
        return trimmed;
    }

    private void ensureSize(int neededBytes) {
        int cur = out.size();
        if (cur < neededBytes) {
            int add = neededBytes - cur;
            for (int i = 0; i < add; i++) {
                out.write(0);
            }
        }
    }

    public void writeBits(int nbits, int value) {
        if (nbits <= 0) {
            return;
        }
        int mask = (nbits >= 31) ? -1 : ((1 << nbits) - 1);
        value &= mask;

        int endBit = bitsOffset + nbits;
        int neededBytes = (endBit + 7) / 8;
        ensureSize(neededBytes);

        byte[] buf = out.toByteArray();
        // We will mutate via a fresh array then reset stream
        for (int i = 0; i < nbits; i++) {
            int bitVal = (value >> (nbits - 1 - i)) & 1;
            int absBit = bitsOffset + i;
            int byteIdx = absBit / 8;
            int bitInByte = absBit % 8;
            int shift = 7 - bitInByte;
            if (bitVal == 1) {
                buf[byteIdx] = (byte) (buf[byteIdx] | (1 << shift));
            } else {
                buf[byteIdx] = (byte) (buf[byteIdx] & ~(1 << shift));
            }
        }

        out.reset();
        try {
            out.write(buf);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e); // should never happen for ByteArrayOutputStream
        }

        bitsOffset += nbits;
    }

    public void padToByteWithZeros() {
        int pad8 = (-bitsOffset) & 7;
        if (pad8 != 0) {
            writeBits(pad8, 0);
        }
    }

    public void finalizeLegacyPadding() {
        int r = bitsOffset & 7;
        if (r == 4) {
            writeBits(2, 0b11);
        } else if (r == 2) {
            writeBits(1, 0b1);
        }
        padToByteWithZeros();
    }

    private static final class IOExceptionImpossible extends RuntimeException {}
}

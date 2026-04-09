package format101;

public record PilotEntry(
        String bufr,
        String ref,
        int nbits,
        double factor,
        double offset,
        int codmax
) {
    public String key() {
        if (ref == null || ref.isEmpty()) {
            return bufr;
        }
        return bufr + "_" + ref;
    }
}

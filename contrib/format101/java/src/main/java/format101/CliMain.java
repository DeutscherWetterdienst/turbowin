package format101;

import java.nio.file.Path;

public final class CliMain {

    private static void usage() {
        System.out.println("format101-java CLI (WIP)");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  encode --pilote <path> --format101 <path> --station-id <id>");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }

        String cmd = args[0];

        if ("encode".equals(cmd)) {
            Path pilote = null;
            Path format101 = null;
            String stationId = null;

            for (int i = 1; i < args.length; i++) {
                String a = args[i];
                if ("--pilote".equals(a) && i + 1 < args.length) {
                    pilote = Path.of(args[++i]);
                } else if ("--format101".equals(a) && i + 1 < args.length) {
                    format101 = Path.of(args[++i]);
                } else if ("--station-id".equals(a) && i + 1 < args.length) {
                    stationId = args[++i];
                } else {
                    System.err.println("Unknown or incomplete argument: " + a);
                    usage();
                    System.exit(2);
                }
            }

            if (pilote == null || format101 == null || stationId == null) {
                System.err.println("Missing required arguments");
                usage();
                System.exit(2);
            }

            String hpkLine = Encoder.encodeFromFormat101Txt(pilote, format101, stationId).toHpkLine();
            System.out.println(hpkLine);
            return;
        }

        System.err.println("Unknown command: " + cmd);
        usage();
        System.exit(2);
    }
}

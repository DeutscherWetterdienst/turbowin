"""Command-line entry point for the PEUMB simulator."""

from __future__ import annotations

import argparse
import socket
import sys
import time

from .simulation import PEUMBSimulator


def _parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="peumb-simulator",
        description="Send changing synthetic $PEUMB telegrams to TurboWin.",
    )
    parser.add_argument("--host", default="127.0.0.1", help="UDP destination host")
    parser.add_argument("--port", type=int, default=60004, help="UDP destination port")
    parser.add_argument(
        "--interval",
        type=float,
        default=1.0,
        metavar="SECONDS",
        help="seconds between telegrams (default: 1)",
    )
    parser.add_argument("--count", type=int, metavar="N", help="stop after N telegrams")
    parser.add_argument(
        "--duration",
        type=float,
        metavar="SECONDS",
        help="stop after the specified number of seconds",
    )
    parser.add_argument(
        "--latitude", type=float, default=52.405, help="initial latitude"
    )
    parser.add_argument(
        "--longitude", type=float, default=5.338, help="initial longitude"
    )
    parser.add_argument(
        "--speed", type=float, default=2.5, help="initial speed over ground in m/s"
    )
    parser.add_argument(
        "--heading", type=float, default=192.0, help="initial heading in degrees"
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="print telegrams instead of sending UDP datagrams",
    )
    return parser


def main(argv: list[str] | None = None) -> int:
    parser = _parser()
    args = parser.parse_args(argv)

    # Validate CLI arguments.
    if not 1 <= args.port <= 65535:
        parser.error("--port must be between 1 and 65535")

    if args.interval < 0:
        parser.error("--interval must be non-negative")

    if args.count is not None and args.count < 1:
        parser.error("--count must be at least 1")

    if args.duration is not None and args.duration <= 0:
        parser.error("--duration must be greater than 0")

    if not -90 <= args.latitude <= 90:
        parser.error("--latitude must be between -90 and 90")

    if not -180 <= args.longitude <= 180:
        parser.error("--longitude must be between -180 and 180")

    if args.speed < 0:
        parser.error("--speed must be non-negative")

    if not 0 <= args.heading <= 360:
        parser.error("--heading must be between 0 and 360")

    # Initialize simulator.
    try:
        simulator = PEUMBSimulator(
            latitude=args.latitude,
            longitude=args.longitude,
            speed=args.speed,
            heading=args.heading,
            interval=args.interval,
        )
    except ValueError as error:
        parser.error(str(error))

    # Initialize UDP socket unless running in dry-run mode.
    sock: socket.socket | None = None

    if not args.dry_run:
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

            # Configure multicast when requested.
            if args.host.startswith("239."):
                sock.setsockopt(
                    socket.IPPROTO_IP,
                    socket.IP_MULTICAST_TTL,
                    1,
                )
                sock.setsockopt(
                    socket.IPPROTO_IP,
                    socket.IP_MULTICAST_LOOP,
                    1,
                )

        except OSError as error:
            print(
                f"Could not initialize UDP socket: {error}",
                file=sys.stderr,
            )
            if sock is not None:
                sock.close()
            return 1

    # Announce startup.
    if args.dry_run:
        print("Running in dry-run mode.", file=sys.stderr)
    else:
        print(
            f"Sending $PEUMB telegrams to "
            f"{args.host}:{args.port} "
            f"every {args.interval}s. Press Ctrl+C to stop.",
            file=sys.stderr,
        )

    generated = 0
    start_time = time.monotonic()

    try:
        while True:
            # Check duration limit.
            if (
                args.duration is not None
                and time.monotonic() - start_time >= args.duration
            ):
                break

            # Check telegram count limit.
            if args.count is not None and generated >= args.count:
                break

            sentence = simulator.next_message().sentence()

            if args.dry_run:
                print(sentence, flush=True)
            else:
                assert sock is not None

                # TurboWin accumulates LAN input until a line terminator is
                # received, so retain the OMC-140-style CRLF framing on the
                # wire even though ``sentence`` itself has no terminator.
                try:
                    sock.sendto(
                        (sentence + "\r\n").encode("ascii"),
                        (args.host, args.port),
                    )
                except OSError as error:
                    print(
                        f"UDP send failed ({args.host}:{args.port}): {error}",
                        file=sys.stderr,
                    )
                    return 1

            generated += 1

            if args.count is not None and generated >= args.count:
                break

            if (
                args.duration is not None
                and time.monotonic() - start_time >= args.duration
            ):
                break

            time.sleep(args.interval)

    except KeyboardInterrupt:
        print(file=sys.stderr)

    finally:
        if sock is not None:
            sock.close()

    print(
        f"Stopped. Generated {generated} telegram(s).",
        file=sys.stderr,
    )

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

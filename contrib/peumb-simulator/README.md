# PEUMB simulator

This small `uv` project generates synthetic `$PEUMB` AWS telegrams for local
TurboWin demonstrations and integration tests. It uses only Python's standard
library and does not need an OMC-140 device.

By default, telegrams are sent as ASCII UDP datagrams to `127.0.0.1:60004`
(localhost). This is suitable for demonstrations where TurboWin and the
simulator run on the same machine.

The simulator also supports sending telegrams to another machine or using
the OMC-140 AWS LAN multicast endpoint (`239.192.0.4:60004`).

Configure TurboWin for the OMC-140/AWS LAN input and make sure a separate
external GPS input is not enabled at the same time: the simulator includes
changing GPS position, course and speed in each telegram.

## Requirements

- Python 3.11 or newer
- [uv](https://docs.astral.sh/uv/)

No third-party Python dependency is required.

## Run it

From this directory:

```console
uv run peumb-simulator
```

The default destination is `127.0.0.1:60004`, and the default interval is
one second, which is useful for a live demo.

To inspect telegrams without sending UDP datagrams:

```console
uv run peumb-simulator --dry-run --count 3
```

### Sending to another machine

Specify the destination IP address:

```console
uv run peumb-simulator --host 192.168.1.100 --port 60004
```

Replace `192.168.1.100` with the IP address of the machine running TurboWin.

### Multicast

To send telegrams to the OMC-140 AWS LAN multicast endpoint:

```console
uv run peumb-simulator --host 239.192.0.4 --port 60004
```

The simulator configures multicast TTL to 1, keeping multicast traffic
within the local network, and enables multicast loopback.

Make sure the network interface and network infrastructure support multicast.
If running inside a virtual machine, bridged networking may be required.
Standard NAT networking may not forward multicast traffic correctly.

Configure TurboWin to receive OMC-140/AWS LAN multicast telegrams on
`239.192.0.4:60004`.

### Useful options

```text
--host HOST          UDP destination (default: 127.0.0.1)
--port PORT          UDP destination port (default: 60004)
--interval SECONDS   Time between packets (default: 1)
--count N            Stop after N packets (otherwise run until Ctrl+C)
--duration SECONDS   Stop after the specified number of seconds
--latitude DEGREES   Initial latitude (default: 52.405)
--longitude DEGREES  Initial longitude (default: 5.338)
--speed M/S          Initial speed over ground (default: 2.5)
--heading DEGREES    Initial course and heading (default: 192)
--dry-run            Print telegrams instead of sending UDP datagrams
```

### Examples

Send 10 telegrams to localhost:

```console
uv run peumb-simulator --count 10
```

Run a demonstration for 60 seconds:

```console
uv run peumb-simulator --duration 60
```

Send telegrams twice per second:

```console
uv run peumb-simulator --interval 0.5
```

Use a slower one-minute interval:

```console
uv run peumb-simulator --interval 60
```

Print three telegrams without sending them:

```console
uv run peumb-simulator --dry-run --count 3
```

Send 10 telegrams to a local unicast test receiver:

```console
uv run peumb-simulator --host 127.0.0.1 --port 60004 --count 10
```

Send multicast telegrams:

```console
uv run peumb-simulator --host 239.192.0.4 --port 60004
```

## Telegram format

The final `*HH` is the NMEA-style XOR checksum over all characters after
the leading `$` and before `*`.

UDP packets are terminated with `CRLF`, as expected by TurboWin's
OMC-140 LAN receiver.

Each message contains 26 comma-separated fields:
time, position, motion, pressure, temperature/humidity, sea-surface and wind
measurements, power/device status, and three reserved fields.

The simulator generates changing synthetic measurements to provide a
continuous data stream for demonstrations and integration tests.

## Development

Install dependencies and synchronize the environment:

```console
uv sync
```

Run the tests:

```console
uv run pytest
```

"""Formatting and checksum handling for OMC-140 ``$PEUMB`` telegrams."""

from __future__ import annotations

from dataclasses import dataclass


def checksum(body: str) -> str:
    """Return the two-digit XOR checksum for a sentence body.

    ``body`` must include the leading ``$`` and must not include ``*`` or a
    checksum. This is the NMEA-style checksum used by the OMC LAN telegram.
    """

    if not body.startswith("$"):
        raise ValueError("PEUMB body must start with '$'")
    if "*" in body:
        raise ValueError("PEUMB body must not already contain a checksum")
    value = 0
    for character in body[1:]:
        value ^= ord(character)
    return f"{value:02X}"


@dataclass(frozen=True)
class PEUMBMessage:
    """One complete 26-field ``$PEUMB`` telegram.

    Values are kept as strings at this boundary so formatting remains explicit
    and the packet can be inspected exactly as it is sent on the wire.
    """

    date: str
    time: str
    latitude: str
    longitude: str
    cog: str
    sog: str
    heading: str
    sensor_pressure: str
    msl_pressure: str
    pressure_tendency: str
    pressure_characteristic: str
    air_temperature: str
    relative_humidity: str
    sea_surface_temperature: str
    relative_wind_speed: str
    relative_wind_direction: str
    true_wind_speed: str
    true_wind_direction: str
    gust_speed: str
    gust_direction: str
    supply_voltage: str
    device_temperature: str
    vot_status: str
    reserve_1: str = ""
    reserve_2: str = ""
    reserve_3: str = ""

    def fields(self) -> tuple[str, ...]:
        """Return the 26 comma-separated data fields in protocol order."""

        return (
            self.date,
            self.time,
            self.latitude,
            self.longitude,
            self.cog,
            self.sog,
            self.heading,
            self.sensor_pressure,
            self.msl_pressure,
            self.pressure_tendency,
            self.pressure_characteristic,
            self.air_temperature,
            self.relative_humidity,
            self.sea_surface_temperature,
            self.relative_wind_speed,
            self.relative_wind_direction,
            self.true_wind_speed,
            self.true_wind_direction,
            self.gust_speed,
            self.gust_direction,
            self.supply_voltage,
            self.device_temperature,
            self.vot_status,
            self.reserve_1,
            self.reserve_2,
            self.reserve_3,
        )

    def body(self) -> str:
        """Return the sentence without ``*HH``."""

        return "$PEUMB," + ",".join(self.fields())

    def sentence(self) -> str:
        """Return the complete ASCII sentence including its checksum."""

        body = self.body()
        return f"{body}*{checksum(body)}"

    def __str__(self) -> str:
        return self.sentence()

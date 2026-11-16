"""Plausible, smoothly changing synthetic AWS observations."""

from __future__ import annotations

import math
from dataclasses import dataclass
from datetime import UTC, datetime, timedelta

from .message import PEUMBMessage

EARTH_RADIUS_M = 6_371_000.0


def _clamp(value: float, lower: float, upper: float) -> float:
    return max(lower, min(upper, value))


def _angle(value: float) -> float:
    return value % 360.0


def _format_angle(value: float) -> str:
    return f"{_angle(value):.1f}"


@dataclass
class PEUMBSimulator:
    """Generate a moving vessel and changing meteorological observations.

    ``interval`` is also used as the simulated time step. This means a
    ``--dry-run --count`` invocation produces visibly different timestamps and
    values even when packets are generated faster than real time.
    """

    latitude: float = 52.405
    longitude: float = 5.338
    speed: float = 2.5
    heading: float = 192.0
    interval: float = 1.0
    start_time: datetime | None = None

    def __post_init__(self) -> None:
        if not -90.0 <= self.latitude <= 90.0:
            raise ValueError("latitude must be between -90 and 90 degrees")
        if not -180.0 <= self.longitude <= 180.0:
            raise ValueError("longitude must be between -180 and 180 degrees")
        if self.speed < 0:
            raise ValueError("speed must be non-negative")
        if self.interval < 0:
            raise ValueError("interval must be non-negative")
        if self.start_time is None:
            self.start_time = datetime.now(UTC)
        elif self.start_time.tzinfo is None:
            raise ValueError("start_time must be timezone-aware")
        else:
            self.start_time = self.start_time.astimezone(UTC)
        # A zero wall-clock interval is useful for fast dry-runs. Keep a
        # one-second synthetic step in that case so consecutive packets still
        # contain changing observations.
        self._simulation_step = max(self.interval, 1.0)
        self._sample = 0

    @property
    def sample(self) -> int:
        """Number of messages generated so far."""

        return self._sample

    def next_message(self) -> PEUMBMessage:
        """Advance the simulation and return the next telegram."""

        assert self.start_time is not None

        t = self._sample * self._simulation_step
        timestamp = self.start_time + timedelta(seconds=t)

        # Slowly turn and vary speed. The first sample uses the requested
        # initial values; subsequent samples visibly move on a chart.
        heading = self.heading + 7.0 * math.sin(t / 90.0)
        sog = max(0.0, self.speed + 0.55 * math.sin(t / 35.0 + 0.2))
        cog = heading + 1.8 * math.sin(t / 24.0)

        if self._sample:
            distance = sog * self._simulation_step
            heading_rad = math.radians(heading)
            self.latitude += math.degrees(
                math.cos(heading_rad) * distance / EARTH_RADIUS_M
            )
            latitude_rad = math.radians(_clamp(self.latitude, -89.9, 89.9))
            self.longitude += math.degrees(
                math.sin(heading_rad)
                * distance
                / (EARTH_RADIUS_M * max(0.05, math.cos(latitude_rad)))
            )
            self.latitude = _clamp(self.latitude, -90.0, 90.0)
            self.longitude = ((self.longitude + 180.0) % 360.0) - 180.0

        sensor_pressure = 1005.5 + 2.8 * math.sin(t / 180.0) + 0.35 * math.sin(t / 31.0)
        msl_pressure = sensor_pressure + 2.6 + 0.2 * math.sin(t / 70.0)
        tendency = 0.9 * math.cos(t / 180.0) - 0.15 * math.sin(t / 31.0)
        characteristic = int((self._sample // 4) % 9)

        air_temperature = 14.0 + 2.2 * math.sin(t / 240.0) + 0.35 * math.sin(t / 29.0)
        relative_humidity = _clamp(77.0 + 12.0 * math.sin(t / 200.0 + 1.0), 20.0, 100.0)
        sea_surface_temperature = 9.2 + 0.45 * math.sin(t / 700.0)

        relative_wind_speed = max(0.0, 4.0 + 0.9 * math.sin(t / 43.0 + 0.5))
        relative_wind_direction = _angle(128.0 + 24.0 * math.sin(t / 76.0))
        true_wind_speed = max(0.0, 5.4 + 1.3 * math.sin(t / 57.0 + 0.8))
        true_wind_direction = _angle(218.0 + 28.0 * math.sin(t / 91.0 + 0.4))
        gust_speed = true_wind_speed + 1.4 + 0.8 * abs(math.sin(t / 17.0))
        gust_direction = _angle(true_wind_direction + 4.0 * math.sin(t / 19.0))

        supply_voltage = 24.1 + 0.35 * math.sin(t / 300.0)
        device_temperature = 26.0 + 1.5 * math.sin(t / 170.0 + 0.4)
        vot_status = self._sample % 16

        message = PEUMBMessage(
            date=timestamp.strftime("%Y%m%d"),
            time=timestamp.strftime("%H%M%S"),
            latitude=f"{self.latitude:.5f}",
            longitude=f"{self.longitude:.5f}",
            cog=_format_angle(cog),
            sog=f"{sog:.2f}",
            heading=_format_angle(heading),
            sensor_pressure=f"{sensor_pressure:.1f}",
            msl_pressure=f"{msl_pressure:.1f}",
            pressure_tendency=f"{tendency:.2f}",
            pressure_characteristic=str(characteristic),
            air_temperature=f"{air_temperature:.1f}",
            relative_humidity=f"{relative_humidity:.1f}",
            sea_surface_temperature=f"{sea_surface_temperature:.1f}",
            relative_wind_speed=f"{relative_wind_speed:.2f}",
            relative_wind_direction=_format_angle(relative_wind_direction),
            true_wind_speed=f"{true_wind_speed:.2f}",
            true_wind_direction=_format_angle(true_wind_direction),
            gust_speed=f"{gust_speed:.2f}",
            gust_direction=_format_angle(gust_direction),
            supply_voltage=f"{supply_voltage:.2f}",
            device_temperature=f"{device_temperature:.1f}",
            vot_status=str(vot_status),
        )
        self._sample += 1
        return message

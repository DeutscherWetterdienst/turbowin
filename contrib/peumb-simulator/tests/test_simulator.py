import unittest
from datetime import UTC, datetime

from peumb_simulator.message import PEUMBMessage, checksum
from peumb_simulator.simulation import PEUMBSimulator


class MessageTests(unittest.TestCase):
    def test_message_has_26_fields_and_valid_checksum(self) -> None:
        message = PEUMBMessage(*(["value"] * 23))
        # The three reserved fields have defaults, so this is 26 fields total.
        self.assertEqual(len(message.fields()), 26)
        sentence = message.sentence()
        body, supplied = sentence.rsplit("*", 1)
        self.assertEqual(len(supplied), 2)
        self.assertEqual(supplied, checksum(body))
        self.assertEqual(body.count(","), 26)
        self.assertTrue(sentence.startswith("$PEUMB,"))


class SimulationTests(unittest.TestCase):
    def test_values_change_between_samples(self) -> None:
        simulator = PEUMBSimulator(
            interval=10.0,
            start_time=datetime(2026, 10, 12, 12, 0, 0, tzinfo=UTC),
        )
        first = simulator.next_message()
        second = simulator.next_message()

        self.assertNotEqual(first.sentence(), second.sentence())
        self.assertNotEqual(first.latitude, second.latitude)
        self.assertNotEqual(first.air_temperature, second.air_temperature)
        self.assertNotEqual(first.relative_humidity, second.relative_humidity)
        self.assertNotEqual(first.true_wind_direction, second.true_wind_direction)

    def test_starting_position_is_used(self) -> None:
        simulator = PEUMBSimulator(latitude=48.12345, longitude=11.54321)
        message = simulator.next_message()
        self.assertEqual(message.latitude, "48.12345")
        self.assertEqual(message.longitude, "11.54321")

    def test_zero_interval_still_advances_synthetic_time(self) -> None:
        simulator = PEUMBSimulator(interval=0)
        first = simulator.next_message()
        second = simulator.next_message()
        self.assertNotEqual(first.sentence(), second.sentence())
        self.assertNotEqual(first.latitude, second.latitude)

# EUCAWS observation input

When the observation format is set to `EUCAWS connected`, TurboWin composes an SMD observation
message for the EUCAWS device. The message starts with `$PTBWP` and contains comma-separated
fields. It is terminated with CR/LF when sent over the serial connection.

The upload setting controls who sends the observation: the EUCAWS device through its Iridium
modem, or TurboWin through its own upload path. It does not change the field layout described
below.

## Fields sent to EUCAWS

The following fields are sent in this order. Empty fields remain empty so that the comma positions
are preserved.

| Position | SMD field | TurboWin source | Unit or conversion |
| ---: | --- | --- | --- |
| 0 | Identifier | Fixed value `$PTBWP` | — |
| 1 | `sHH` | Difference between summer load line and water line (`diff_sll_wl`) | metres; entered in `Maintenance -> Station data` or `Input -> Wind` |
| 2 | `WDT` | Manual true-wind direction | degrees; variable wind is sent as `0` |
| 3 | `WS.s` | Manual true-wind speed | m/s; knots are converted to m/s and rounded to one decimal |
| 4 | `sTA.a` | Manual air temperature | °C; rounded to one decimal |
| 5 | `UUU` | Manual relative humidity | percent; rounded to an integer |
| 6 | `TW.w` | Manual sea-surface temperature | °C; rounded to one decimal |
| 7 | `VV` | Visibility code | EUCAWS/BUFR code |
| 8 | `WW` | Present-weather code | BUFR table 020003 |
| 9–10 | `W1`, `W2` | Past-weather codes | BUFR table 020004 |
| 11 | `N` | Total cloud cover | Code |
| 12 | `Nh` | Cloud amount | BUFR table 020011 |
| 13–15 | `Cl`, `Cm`, `Ch` | Low, middle, and high cloud types | Converted to BUFR table 020012 using the low/middle/high offsets |
| 16 | `H` | Height of the lowest cloud base | FM13 cloud-height code, not metres |
| 17 | `Pw` | Wind-wave period | Code; unavailable value is omitted |
| 18 | `Hw.w` | Wind-wave height | FM13 half-metre code converted to metres |
| 19–21 | `DSW1`, `PW1`, `HW1.w1` | First swell direction, period, and height | Direction is converted to degrees; height is converted to metres |
| 22–24 | `DSW2`, `PW2`, `HW2.w2` | Second swell direction, period, and height | Direction is converted to degrees; height is converted to metres |
| 25 | `E.EE` | Ice-deposit thickness | FM13 centimetres converted to metres, two decimals |
| 26 | `R` | Rate of ice accretion | EUCAWS code |
| 27 | `I` | Cause of ice accretion | Converted to the EUCAWS complementary code table |
| 28 | `CC` | Sea-ice concentration | EUCAWS code; TurboWin's unknown value becomes `14` |
| 29 | `BB` | Amount and type of ice | EUCAWS code; TurboWin's unknown value becomes `14` |
| 30 | `ZZ` | Ice situation | EUCAWS code; TurboWin's unknown value becomes `30` |
| 31 | `SS` | Ice development | EUCAWS code; TurboWin's unknown value becomes `30` |
| 32 | `DDD` | Bearing of the ice edge | Converted to degrees; unsupported/unknown directions are omitted |
| 33 (optional) | `OBS` | Observer ID | Added only when `EUCAWS SMD input -> add observer initials (OBS_ID) to SMD input` is enabled and a valid three-character observer ID exists |

## Important interactions

### EUCAWS-owned sensor values are not sent back

When the incoming EUCAWS sensor data contains a value, TurboWin marks that parameter as
EUCAWS-provided and leaves the corresponding outgoing field empty. This applies to:

- true-wind direction;
- true-wind speed;
- air temperature;
- relative humidity; and
- sea-surface temperature.

The value is displayed and used locally, but it is expected to be supplied by the device itself.
This is why a value visible in TurboWin may not appear in the manually composed `$PTBWP` message.

Air pressure is handled similarly but more strictly: pressure is read from EUCAWS for the local
display and IMMT processing, but there is no pressure field in the `$PTBWP` observation message.
It cannot be inserted manually in EUCAWS mode.

### Height-related fields are different concepts

The station-data field `diff_sll_wl` is the direct physical height-related value sent in the
message. It means the difference between summer load line and water line, in metres. TurboWin
currently validates it as a rounded integer from `-10` to `50` metres.

Other height-like station fields are not sent in this message:

- barometer height above SLL;
- keel-to-SLL distance; and
- maximum height of deck cargo.

Cloud-base height is transmitted as an FM13 code, while wave heights and ice-deposit thickness are
converted from their TurboWin/FM13 input units as shown in the table.

### Missing values

Missing or unsupported values are represented by empty CSV fields. TurboWin omits FM13 sentinel
values such as `/`, `//`, and `99` where the target EUCAWS field cannot represent them. The field
position is retained by the surrounding commas.

Position, date, and time are not part of the outgoing `$PTBWP` string; they come from the EUCAWS
device/input workflow and are used separately by TurboWin for display and logging.

## EUCAWS operating cycle

The serial connection and the Iridium transmission are two different communication paths:

1. The EUCAWS supplies current sensor data to TurboWin over the serial connection approximately
   once per minute. TurboWin checks for new AWS data every 60 seconds, stores the incoming record,
   and refreshes the displayed sensor values. Incoming data older than the configured freshness
   limit is shown as obsolete.
2. The EUCAWS normally transmits its scheduled observation over Iridium once per hour, at the
   full hour. This transmission is performed by the EUCAWS, not by TurboWin.
3. The EUCAWS includes a visual-observation trigger (VOT) in its incoming data. When the VOT says
   that an observation may be entered, TurboWin changes the status text to green and enables
   `Output -> Obs to AWS`. Before that point, the status is red and the EUCAWS-specific output
   action is disabled. The exact lead time is supplied by the EUCAWS; in normal operation it is
   commonly about 20 minutes before the scheduled transmission.
4. The optional visual-observation reminder window is controlled by the reminder settings. The
   current implementation opens the reminder during the last 10 minutes before the configured
   transmission hour (the interval itself can be configured, for example, hourly, every three
   hours, or every six hours). It is therefore not a fixed five-minute rule.
5. After `Output -> Obs to AWS` is used, TurboWin clears the observation fields after the send
   attempt. This both prevents the same manual values from being submitted again and provides a
   visible indication that the observation cycle was processed. In EUCAWS serial mode TurboWin
   also performs a safety reset at 00:01 UTC of each hour.
6. At the next minute update, the current sensor values received from the EUCAWS are displayed
   again. They are not the old manual observation values; they are a new incoming sensor record.

The fully manual observation workflow is separate from this EUCAWS-triggered cycle. It can be
used at any time when TurboWin is operating in a manual observation mode. The EUCAWS-specific
VOT restriction applies to `Output -> Obs to AWS` when the EUCAWS device is responsible for the
scheduled Iridium transmission.

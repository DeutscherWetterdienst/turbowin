# FM13 and Inmarsat Special Access Code 41

TurboWin supports the WMO FM13 SHIP observation format. The application composes the FM13 text
and can hand it to the configured server, e-mail workflow, file output, or clipboard workflow.

## Inmarsat SAC 41

In the operational setup, FM13 observations may be sent using the Inmarsat Special Access Code
(SAC) `41`. The usual understanding is that this access code identifies meteorological-service
traffic whose communication costs are paid by the responsible weather service rather than by the
ship. This payment and routing arrangement belongs to the Inmarsat/provider account and the
external mail or messaging service.

SAC `41` is not an application setting in TurboWin. The repository contains no Inmarsat modem
driver, SAC field, SAC validation, or billing logic. TurboWin therefore cannot determine from its
own configuration whether a message will use SAC `41` or who will be charged. That must be
configured and verified in the external communication equipment or service.

The SAC arrangement is intended for FM13 traffic. It should not be confused with the observation
format: selecting FM13 creates the required observation text, but it does not automatically select
SAC `41`. Format 101 and EUCAWS/SMD messages follow their own transport arrangements and should
not be sent through an FM13/SAC-41 path unless the receiving service explicitly supports that
combination.

# Runtime View

The following scenarios describe implemented workflows. An observation is normally prepared
from the current shared form state, not reloaded from a database before each output operation.

## Application startup

1. The main window creates its components and delegates non-generated initialization to
   [StartupCoordinator](../src/main/java/turbowin/StartupCoordinator.java).
2. Startup initializes log date formats, resets observation values, detects paths/mode, creates
   offline directories, and prepares communication and graph state.
3. The command-line startup route opens the single-instance socket (default port `12345`, with
   a command-line override). A theme restart skips opening a second instance socket.
4. [ConfigurationReadWorkflow](../src/main/java/turbowin/ConfigurationReadWorkflow.java) reads
   the selected `configuration.txt` in a `SwingWorker` and applies its values to global state.
   Missing-file I/O errors are tolerated; this is not a guarantee that all globals are reset.
5. Its completion callback calls
   [StartupFinalizationWorkflow](../src/main/java/turbowin/StartupFinalizationWorkflow.java)
   to perform startup logging/cleanup, metadata checks, connection initialization, and menu and
   toolbar updates.

Constructor completion and asynchronous startup finalization are different milestones. Startup
can also display modal warning/date-time dialogs. The current startup mode is the installed
desktop ("offline") mode; email and server transmission can still require network access.

## Entering and validating an observation

1. The observer enters station, date/time, position, and weather values through the main window
   and specialized forms. Their handlers update shared values and coded/display fields.
2. Output preparation composes an observation and applies the checks required by that output
   path. File, manual email, and server workflows check required metadata, the log directory,
   position/time sequence, and the land/sea mask, with additional destination settings as needed.
3. The [clipboard workflow](../src/main/java/turbowin/ObservationClipboardWorkflow.java)
   explicitly invokes level-two consistency checks and level-three confirmation checks before
   its sequence/mask checks. Do not assume every output path invokes the identical validation
   chain merely because these checks exist.
4. If selected, `FORMAT_101` prepares the compressed observation through the in-process Java
   encoder. FM13 composition also supplies information used by pre-output checks.
5. The chosen output workflow determines writing, logging, and reset behavior. Merely entering
   a valid field does not durably save the whole observation.

Validation can reject input or ask the observer to confirm an unusual value. It does not promise
that every warning represents an error that must be corrected.

## Manual observation email

1. [ObservationEmailManualWorkflow](../src/main/java/turbowin/ObservationEmailManualWorkflow.java)
   composes and checks the current observation, verifies destination settings, and prepares
   Format 101 data when selected.
2. Its completion callback dispatches to the configured desktop, Jakarta, or Python path.
3. The Jakarta/Python workflows prepare an `ObservationEmailRequest` and invoke their transport
   in background work. `ObservationEmailTransportExecutor` uses a virtual-thread executor and
   waits for the transport result within that background operation.
4. The email worker's `done()` callback handles the returned status or invocation exception.
   Reaching the transport call is not completion of observation logging/reset.

| Custom-email outcome | Completion behavior |
| --- | --- |
| Success | Schedule IMMT observation logging, reset observation values, and notify for a manual send. |
| Generic transport failure or invalid send mode | Also log the observation and reset it; report the failure according to the handler and manual/automatic mode. |
| Empty observation | Reset and report to the system log, but do not write an IMMT observation record. |
| Invocation exception | The invocation-failure handler logs the error and notifies in manual mode; it does not invoke the observation-log/reset actions. |
| Python module discovery/copy failure | Its dedicated handler logs the observation and resets it; diagnostics also involve the Python log. |

See the [success](../src/main/java/turbowin/ObservationEmailSuccessHandler.java),
[generic failure](../src/main/java/turbowin/ObservationEmailGenericFailureHandler.java), and
[invocation failure](../src/main/java/turbowin/ObservationEmailInvocationFailureHandler.java)
handlers for the distinct contracts. Desktop email delegates to the installed mail client and
also has logging/reset side effects; opening that client is not confirmation of delivery.
IMMT logging composes the record and starts a separate background writer; the email completion
callback does not wait for that file write to finish.
Neither a successful SMTP operation nor a desktop handoff proves that the meteorological
service has received or accepted the observation.

## Server and file output

Server output dispatches to [Fm13ServerWorkflow](../src/main/java/turbowin/Fm13ServerWorkflow.java)
or [Format101ServerWorkflow](../src/main/java/turbowin/Format101ServerWorkflow.java). HTTP response
codes drive the result handling. The Format 101 client has bounded retries/backoff for connection
failures; this is not a general retry guarantee for all transports. The server workflows offer
manual retry dialogs. Success, and failure paths where retry is declined, can log/reset the
observation. These response codes are not an end-to-end delivery acknowledgment.

File output asks for a destination and writes the appropriate representation. Its normal
completion path logs/resets after reporting the write result, including a false write result.
The [Format 101 file workflow](../src/main/java/turbowin/Format101FileOutputWorkflow.java) also
retains legacy handling of an empty encoded line. Do not equate an output completion notification,
an IMMT entry, or cleared input fields with a successfully written/transmitted observation.

## Automatic instrument reporting

Device-specific workflows read serial input and/or saved sensor files, apply format and freshness
checks, update observation/dashboard values, and dispatch APR/APTR/AWSR output according to
configuration and scheduling decisions. They are not simply the manual output flow on a timer.

Historical Mintaka positions are selected for the requested timestamp using file order. An invalid
matching duplicate clears an earlier valid position; a later valid match can restore it. A short
physical record also clears it, while unrelated timestamps do not replace it. GPS, wind, and WOW/APR
history workflows use this policy. See [record selection](08_concepts.md#sensor-record-contracts).

## Statistics, graphs, and failures

Observation statistics process local IMMT records for a selected period; graph and dashboard
workflows read their relevant observation/sensor files. Maps and exports use the existing view
workflows. There is no single independent analytics engine or query service behind these views.

Failure handling is local to each workflow: a dialog, system log entry, standard-output diagnostic,
default value, cleared field, or silent rejection may be intentional. There is no central mechanism
that guarantees all exceptions are logged, all state is retained, or every failure is recoverable.

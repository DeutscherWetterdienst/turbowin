# JavaFX in the Swing application

The About, temperature, wind, waves, visibility, present-weather, past-weather, icing, low-cloud, middle-cloud, high-cloud, and cloud-cover windows are embedded JavaFX views.
The rest of TurboWin continues to use Swing.
The OpenJFX Gradle plugin supplies JavaFX 25.0.2 controls and Swing integration for the build
machine's operating system and architecture. Build each distribution on its target platform, as
the existing `jlink` and `jpackage` scripts already do. Linux deployments also need the native
desktop libraries required by JavaFX, including GTK. The Linux package scripts declare the GTK 3
dependency. Stripping JavaFX native libraries during `jlink` also requires `objcopy` from binutils.
Java 25 may log a native-access warning when JavaFX loads its native libraries; the dialog still
works. A future Java upgrade should review the required `--enable-native-access` launcher option.

`AboutDialog` captures the application information on the Swing event dispatch thread and passes
it to `AboutView`. `FxSwingDialog` creates a Swing dialog and `JFXPanel` on that thread. Creating
the first panel starts JavaFX; scene creation and cleanup run on the JavaFX application thread.
JavaFX remains initialized after closing the dialog so it can be opened again. JavaFX actions
that affect Swing dispatch back to the Swing event thread. The existing application shutdown
continues to terminate the process.

To migrate another window, keep its data gathering separate from the JavaFX view, invoke
`FxSwingDialog.show` on the Swing event thread, and create its scene in the supplied factory.
Return Swing actions to the event dispatch thread; do not access Swing components on the JavaFX
thread. Keep its CSS and images in application resources.

The cloud-cover view snapshots its initial values and height advice on the Swing event thread.
Its selection actions return to that thread for validation, observation updates, and navigation;
the JavaFX view does not directly update Swing components or shared observation state.
The low-, middle-, and high-cloud views use the same boundary for cloud selections, thumbnail help, and
navigation. Cm7's three distinct codes remain available to existing observation workflows.
The temperature view snapshots its input and station settings on the Swing thread. Validation,
meteorological calculations, and sequence navigation remain on that thread. Asynchronous sensor
readings are copied back to the JavaFX controls on the JavaFX thread.
The wind view also snapshots station settings and returns its inputs to the Swing thread, where
the existing wind calculations, validation, and observation sequence remain.
The waves view returns its readings and swell choice to that thread for validation, encoding,
and navigation to the existing swell forms.
The visibility view returns the chosen range and code to the Swing thread for observation updates
and sequence navigation.
The present-weather view keeps the original order of general and specific conditions and returns
the chosen condition to the Swing thread for validation, observation updates, and navigation.
The past-weather view returns its checked conditions in priority order to the Swing thread, which
records the first two conditions and duplicates a single condition for the observation.
The icing view snapshots the observation format and selected values, then returns inputs to the
Swing thread for thickness validation and observation updates.

`AboutDialogFxTest` requires a graphical display and skips itself in a headless environment. On
Linux it can be run with `xvfb-run -a ./gradlew test --tests turbowin.AboutDialogFxTest`; the
usual `TURBOWIN_EMAIL_*` build environment variables are also required. It checks scene creation,
resources, a link action, closing and reopening. Validate installers on each supported platform,
because JavaFX includes native code and cannot be fully tested by another platform's build.

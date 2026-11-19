# JavaFX in the Swing application

The About window is the first embedded JavaFX view. The rest of TurboWin continues to use Swing.
The OpenJFX Gradle plugin supplies JavaFX 25.0.1 controls and Swing integration for the build
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

`AboutDialogFxTest` requires a graphical display and skips itself in a headless environment. On
Linux it can be run with `xvfb-run -a ./gradlew test --tests turbowin.AboutDialogFxTest`; the
usual `TURBOWIN_EMAIL_*` build environment variables are also required. It checks scene creation,
resources, a link action, closing and reopening. Validate installers on each supported platform,
because JavaFX includes native code and cannot be fully tested by another platform's build.

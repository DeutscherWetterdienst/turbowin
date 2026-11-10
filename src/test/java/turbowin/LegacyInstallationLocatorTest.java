package turbowin;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LegacyInstallationLocatorTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void windowsDiscoveryIncludesBothProgramFilesLayoutsOutsideWorkingDirectory()
      throws Exception {
    Path working = temporaryFolder.newFolder("unrelated-working-directory").toPath();
    Path x86 = temporaryFolder.newFolder("Program Files (x86)").toPath();
    Path x64 = temporaryFolder.newFolder("Program Files").toPath();

    List<Path> found =
        LegacyInstallationLocator.find(
            working.toString(),
            "WINDOWS",
            Map.of(
                "ProgramFiles(x86)",
                x86.toString(),
                "ProgramW6432",
                x64.toString(),
                "ProgramFiles",
                x64.toString()),
            temporaryFolder.getRoot().toPath());

    assertEquals(
        List.of(working, x86.resolve("TurboWin+/bin"), x64.resolve("TurboWin+/bin")),
        found.subList(0, 3));
    assertEquals(1, found.stream().filter(x64.resolve("TurboWin+/bin")::equals).count());
  }

  @Test
  public void workingDirectoryKeepsPriorityAndIsDeduplicated() throws Exception {
    Path programFiles = temporaryFolder.newFolder("Program Files (x86)").toPath();
    Path bin = programFiles.resolve("TurboWin+/bin");

    List<Path> found =
        LegacyInstallationLocator.find(
            bin.resolve("../bin").toString(),
            "WINDOWS",
            Map.of("ProgramFiles(x86)", programFiles.toString()),
            temporaryFolder.getRoot().toPath());

    assertEquals(bin, found.get(0));
    assertEquals(1, found.stream().filter(bin::equals).count());
  }

  @Test
  public void discoversVersionedRpmInstallationsInNumericOrderAndDebianLayout() throws Exception {
    Path unixBin = temporaryFolder.newFolder("usr-local-bin").toPath();
    for (String version : List.of("4.5.0", "4.6.0", "4.9.0", "4.10.0", "4.10.0-beta")) {
      Files.createDirectories(unixBin.resolve("TurboWin+-" + version + "/turbowin_jws/bin"));
    }
    Files.createDirectories(unixBin.resolve("TurboWin+-backup/turbowin_jws/bin"));
    Files.createFile(unixBin.resolve("TurboWin+-99.0.0"));
    Files.createDirectories(unixBin.resolve("turbowin_jws/bin"));

    List<Path> found = LegacyInstallationLocator.find("", "LINUX", Map.of(), unixBin);

    assertEquals(
        List.of(
            unixBin.resolve("TurboWin+-4.10.0/turbowin_jws/bin"),
            unixBin.resolve("TurboWin+-4.10.0-beta/turbowin_jws/bin"),
            unixBin.resolve("TurboWin+-4.9.0/turbowin_jws/bin"),
            unixBin.resolve("TurboWin+-4.6.0/turbowin_jws/bin"),
            unixBin.resolve("TurboWin+-4.5.0/turbowin_jws/bin"),
            unixBin.resolve("turbowin_jws/bin")),
        found);
  }

  @Test
  public void missingOrNonDirectoryUnixRootDoesNotPreventStartup() throws Exception {
    Path notDirectory = temporaryFolder.newFile("not-a-directory").toPath();
    assertEquals(
        List.of(notDirectory.resolve("turbowin_jws/bin")),
        LegacyInstallationLocator.find(null, "LINUX", Map.of(), notDirectory));
  }
}

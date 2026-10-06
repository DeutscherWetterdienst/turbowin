package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LogFilesZipWorkflowTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void includesFilesAndExcludesDirectories() throws IOException {
    File tempLogsDirectory = temporaryFolder.newFolder("logs");
    write(new File(tempLogsDirectory, "immt.log"), "observations");
    temporaryFolder.newFolder("logs", "nested");

    LogFilesZipWorkflow.zip(tempLogsDirectory.getPath(), "ship", "logs.zip");

    File archive = new File(tempLogsDirectory, "ship logs.zip");
    Map<String, String> entries = new HashMap<>();
    try (ZipFile zip = new ZipFile(archive)) {
      zip.stream()
          .forEach(
              entry -> {
                try {
                  entries.put(
                      entry.getName(),
                      new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8));
                } catch (IOException ex) {
                  throw new RuntimeException(ex);
                }
              });
    }

    assertTrue(archive.isFile());
    assertEquals(1, entries.size());
    assertEquals("observations", entries.get("immt.log"));
    assertFalse(entries.containsKey("nested"));
  }

  private static void write(File file, String content) throws IOException {
    try (FileOutputStream output = new FileOutputStream(file)) {
      output.write(content.getBytes(StandardCharsets.UTF_8));
    }
  }
}

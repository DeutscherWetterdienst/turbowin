package turbowin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

final class LogFileCopyWorkflow {

  private LogFileCopyWorkflow() {}

  static String copy(String source, String destination) {
    try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
        FileChannel destinationChannel = new FileOutputStream(destination).getChannel()) {
      destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
      return "OK";
    } catch (IOException ex) {
      return "NOT_OK";
    }
  }
}

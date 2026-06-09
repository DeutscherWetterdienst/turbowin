package turbowin;

public class OSDetector {
  public enum OSType {
    WINDOWS,
    MACOS,
    LINUX,
    OTHER
  }

  public static OSType detect_OS() {
    // source:
    // http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
    OSType detectedOS;

    String OS_property_name = System.getProperty("os.name", "generic").toLowerCase();
    // String OS_property_arch = System.getProperty("os.arch", "generic").toLowerCase();  // zal bij
    // RaspBerry en Linux veelal ARM geven (kan niet gebruikt worden om te onderscheiden)

    if ((OS_property_name.indexOf("mac") >= 0) || (OS_property_name.indexOf("darwin") >= 0)) {
      detectedOS = OSType.MACOS;
    } else if (OS_property_name.indexOf("win") >= 0) {
      detectedOS = OSType.WINDOWS;
    } else if (OS_property_name.indexOf("nux") >= 0) {
      // NB could also be on a RaspberryPi (unfortunately not possible to distinguish)
      detectedOS = OSType.LINUX;
    } else {
      detectedOS = OSType.OTHER;
    }

    return detectedOS;
  }

  public static String getOSString() {
    OSType ostype = detect_OS();
    String os;
    switch (ostype) {
      case WINDOWS:
        os = "WINDOWS";
        break;
      case MACOS:
        os = "MACOS";
        break;
      case LINUX:
        os = "LINUX";
        break;
      case OTHER:
        os = "OTHER";
        break;
      default:
        os = "OTHER";
        break;
    }
    return os;
  }
}

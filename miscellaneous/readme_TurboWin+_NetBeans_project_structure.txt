TURBOWIN+ NETBEANS PROJECT STRUCTURE



REQUIRED
Java (JDK): https://jdk.java.net/
NetBeans: https://netbeans.apache.org/
jSerialComm: https://fazecast.github.io/jSerialComm/



EXAMPLE PROJECT STRUCTURE ON DEVELOPMENT PC
C:\NetBeansProjects\turbowin_jws\                                               // file manifest.mf (https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/manifest)
                                \build\classes\turbowin\icons                   // contents all files in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/icons
                                \build\classes\turbowin\format_101              // contents all files and dirs in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/format_101
                                \build\classes\turbowin\format_101\log          // empty
                                \build\classes\turbowin\format_101\temp         // empty
                                \build_classes\turbowin\OSM                     // contents all files and dirs in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/OSM
                                \build_classes\turbowin\python                  // contents all files in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/python
                                \dist                                           //
                                \nbproject                                      // contents all files and dirs in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/nbproject
                                \src\                                           // contents file module-info.java (https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/module_file)
                                \src\turbowin                                   // contents all files in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/src_version_x_x
                                \test                                           //
                                \backup\software\jserialcomm                    // contents e.g. jSerialComm-2.6.2.jar (https://fazecast.github.io/jSerialComm/ and https://gitlab.com/-/ide/project/KNMI-OSS/turbowin/turbowin/tree/master/-/miscellaneous/libraries/)
                                \backup\software\jnlp_jmod                      // contents java.jnlp.jar (https://gitlab.com/-/ide/project/KNMI-OSS/turbowin/turbowin/tree/master/-/miscellaneous/libraries/)
                                



NOTE
When running in IDE: TurboWin+ message "internal error [read_muffin()], persistenceService and or basicService not available"
after packaging with installer (GA releases for community) or via turbowin_launcher.bat (for developer) this error wil not pop up




last updated: 3-August-2021

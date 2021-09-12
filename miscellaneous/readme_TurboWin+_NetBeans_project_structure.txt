TURBOWIN+ NETBEANS PROJECT STRUCTURE



REQUIRED
Java (JDK): https://jdk.java.net/
NetBeans: https://netbeans.apache.org/
jSerialComm: https://fazecast.github.io/jSerialComm/



EXAMPLE PROJECT STRUCTURE ON DEVELOPMENT PC
C:\NetBeansProjects\turbowin_jws\                                              // file manifest.mf (https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/manifest)
                                \build\classes\turbowin\icons                  // contents, all files in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/icons
                                \build\classes\turbowin\format_101             // contents, all files and dirs in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/format_101
                                \build\classes\turbowin\format_101\log         // empty
                                \build\classes\turbowin\format_101\temp        // empty
                                \build_classes\turbowin\OSM                    // contents, all files and dirs in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/OSM
                                \build_classes\turbowin\python                 // contents, all files in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/python
                                \dist                                          //
                                \nbproject                                     // contents, all files and dirs in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/nbproject
                                \src\                                          // contents, file module-info.java (https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/miscellaneous/module_file)
                                \src\turbowin                                  // contents, all files in: https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/src_version_x_x
                                \test                                          //
                                \backup\software\jserialcomm                   // contents, jSerialComm-x.x.x.jar (https://fazecast.github.io/jSerialComm/ and https://gitlab.com/-/ide/project/KNMI-OSS/turbowin/turbowin/tree/master/-/miscellaneous/libraries/)
                                \backup\software\jnlp_jmod                     // contents, file java.jnlp.jar (https://gitlab.com/-/ide/project/KNMI-OSS/turbowin/turbowin/tree/master/-/miscellaneous/libraries/)
                                



NOTES
1] When running in IDE: TurboWin+ message "internal error [read_muffin()], persistenceService and or basicService not available"
   after packaging with installer (GA releases for community) or via turbowin_launcher.bat (for developer) this error wil not pop up

2] Source code python email module: email_tbw.py                               // https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/blob/master/src_python/email_tbw.py
   For updating and (re)compiling of this email module is required:
   - Python 3.8 or higher: https://www.python.org/ (NB Python 32bit version for convenience to be compatible if observers pc is Windows 32bit)
   - Python IDE (e.g. https://github.com/thonny/thonny/wiki/Windows)
   - Pyinstaller: http://www.pyinstaller.org/

3] Insert the appropriate values for the following variables [myemailsettings.java] (not necessary for EUCAWS): 
   characterEncoding, encryptionKey, cipherTransformation and aesEncryptionAlgorithem 
    
4] For final version packaging 
   - make sure help files will be installed in help dir below TurboWin+ bin dir// https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/tree/master/help_files* (e.g. on client pc after install: C:\Program Files (x86)\TurboWin+\bin\help)
         * TurboWin+ version 4.3 or higher, older TurboWin+ versions help files from previous help file system
   - place file TurboWin+_install.ico in root when packaging                   // https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/blob/master/miscellaneous/install_ico/TurboWin+_install.ico





last updated: 12-September-2021

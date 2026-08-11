package turbowin;

import static turbowin.main.*;

/** Updates enabled states for manual observation output menu items. */
final class OutputMenuStateUpdater {

  private OutputMenuStateUpdater() {}

  static void update() {
    // initialisation (because some could be disabled before and must now enabled (see Function
    // OK_button_actionPerformed() [main_RS232_RS422.java])
    jMenuItem20.setEnabled(true); // Obs to server
    jMenuItem23.setEnabled(true); // Email default
    // jMenuItem74.setEnabled(true);    // Email SMTP host
    // jMenuItem72.setEnabled(true);    // Email Gmail
    // jMenuItem73.setEnabled(true);    // Email Yahoo
    jMenuItem80.setEnabled(true); // Email Custom
    jMenuItem24.setEnabled(true); // Obs to file
    jMenuItem46.setEnabled(true); // Obs to AWS
    jMenuItem48.setEnabled(true); // Obs to clipbord

    // AWS (EUCAWS or OMC-140 etc.) connection
    //       NB so OMC-140, AMOS2X can never send manually output observations! (only in AWSR mode)
    if (RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      jMenuItem20.setEnabled(false); // Obs to server
      jMenuItem23.setEnabled(false); // Obs by E-mail (default)
      jMenuItem24.setEnabled(false); // Obs to file
      jMenuItem48.setEnabled(false); // Obs to clipboard

      // jMenuItem72.setEnabled(false);                                 // Obs by E-mail (Gmail)
      // jMenuItem73.setEnabled(false);                                 // Obs by E-mail (Yahoo)
      // jMenuItem74.setEnabled(false);                                 // Obs by E-mail (local
      // host)
      jMenuItem80.setEnabled(false); // Obs by E-mail (Custom)
    }

    // if not EUCAWS connected
    if (RS232_connection_mode != 3) // not EUCAWS connected mode
    {
      // disable the "Obs to AWS" menu item
      jMenuItem46.setEnabled(false); // Obs to AWS (so also disabled for OMC-140!!)
    }

    // EUCAWS connected but uploads via TurboWin+
    if ((RS232_connection_mode == 3) && (eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN))) {
      // disable the "Obs to AWS" menu item
      jMenuItem46.setEnabled(false);
    }

    // if not EUCAWS and not OMC-140 and not AMOS2X connected
    // Email options
    if ((RS232_connection_mode != 3)
        && (RS232_connection_mode != 9)
        && (RS232_connection_mode != 10)
        && (RS232_connection_mode
            != 11)) // not EUCAWS and not OMC-140 and not AMOS2X connected mode
    {
      // [ALL]           // NB the basics are these two items (recipient and subject) that must be
      // present always
      if ((obs_email_recipient.equals("")) || (obs_email_subject.equals(""))) {
        jMenuItem23.setEnabled(false); // Obs by E-mail (default)
        // jMenuItem72.setEnabled(false);                              // Obs by E-mail (Gmail)
        // jMenuItem73.setEnabled(false);                              // Obs by E-mail (Yahoo)
        // jMenuItem74.setEnabled(false);                              // Obs by E-mail (local host)
        jMenuItem80.setEnabled(false); // Obs by E-mail (Custom)
      } else // recipient and subject ok
      {
        // [DEFAULT]
        if (obs_format.equals(main.FORMAT_101)) {
          if (obs_101_email.equals("")) // obs_101_email: body or attachment
          {
            jMenuItem23.setEnabled(false); // Obs by E-mail (default)
          } else {
            jMenuItem23.setEnabled(true); // Obs by E-mail (default)
          } // else
        } // if (obs_format.equals(main.FORMAT_101))
        else // FM13
        {
          jMenuItem23.setEnabled(true); // Obs by E-mail (default)
        }

        // [CUSTOM]
        // if ( (your_custom_address.equals("")) || (custom_password.equals("")) ||
        // (custom_security.equals("")) || (custom_email_server.equals("")) ||
        // (custom_port.equals("")))
        if ((your_custom_address.equals(""))
            || (custom_security.equals(""))
            || (custom_email_server.equals(""))
            || (custom_port.equals(""))) {
          jMenuItem80.setEnabled(false); // Obs by E-mail (Custom)
        } else {
          jMenuItem80.setEnabled(true); // Obs by E-mail (Custom)
        }
      } // else (recipient and subject ok)
    } // if (RS232_connection_mode != 3)

    // Obs to server
    //
    if ((obs_format.equals(FORMAT_FM13)) && (offline_mode == false)) {
      // FM13 + Java Web Start (TurboWeb): by default always a valid FM13 URL avaialble (the URL
      // were TurboWeb it was downloaded from!!)
      jMenuItem20.setEnabled(true);
    } else if ((upload_URL.equals("") || upload_URL == null)) {
      // if not FM13 + Java Web Start (TurboWeb) then upload URL must be available
      jMenuItem20.setEnabled(false);
    }

    // APR and AWSR
    //
    if (APR || AWSR) // NB APR and APTR could never be checked together
    {
      // in APR and AWSR mode disable all output menu items (in APR and AWSR mode the output method
      // is set in 'Maintenance -> APR/APTR/AWSR settings')
      //    NB at the beginning of this function all output menu options were enabled(true)
      //    NB in OMC-140 mode these menu items were already disabled (so is not strict necessary do
      // do it here again)
      jMenuItem20.setEnabled(false); // Obs to server
      jMenuItem23.setEnabled(false); // Email default
      // jMenuItem74.setEnabled(false);    // Email SMTP host
      // jMenuItem72.setEnabled(false);    // Email Gmail
      // jMenuItem73.setEnabled(false);    // Email Yahoo
      jMenuItem24.setEnabled(false); // Obs to file
      jMenuItem46.setEnabled(false); // Obs to AWS
      jMenuItem48.setEnabled(false); // Obs to clipboard
      jMenuItem80.setEnabled(false); // Email Custom
    } // if (APR || APTR)
  }
}

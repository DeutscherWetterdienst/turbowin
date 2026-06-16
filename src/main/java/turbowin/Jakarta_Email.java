package turbowin;

// import com.sun.mail.smtp.SMTPAddressFailedException;
// import com.sun.mail.smtp.SMTPAddressSucceededException;
// import com.sun.mail.smtp.SMTPSendFailedException;
// import com.sun.mail.smtp.SMTPTransport;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.eclipse.angus.mail.smtp.SMTPAddressSucceededException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.eclipse.angus.mail.smtp.SMTPTransport;

public class Jakarta_Email {

  public Jakarta_Email() {
    props = new Properties();
  }

  private void set_jakarta_email_props(
      final String smtp_mode,
      final String smtp_host_local,
      final String smtp_password_local,
      final String send_from,
      final String smtp_port_local) {
    // NB
    // https://eclipse-ee4j.github.io/mail/docs/api/jakarta.mail/jakarta/mail/package-summary.html

    // NB --- not necessary to use smtps ---!
    // The Transport.send method will use the default transport protocol, which remains "smtp".
    // To enable SMTP connections over SSL, set the "mail.smtp.ssl.enable" property to "true". This
    // is usually the easiest approach.

    // NB
    // mail.smtp.connectiontimeout is the timeout (in milliseconds) for establishing the SMTP
    // connection and
    // mail.smtp.timeout is the timeout (also milliseconds) for sending the mail messages.
    // You can start by setting them both to the same reasonable value depending on how slow your
    // servers tend to be and then tune them for the best user experience.
    //

    // NB testing 29-August 2022
    // TLSv1.3: if  AUTH=on ("mail.smtp.auth"="true" + Session session = Session.getInstance(props,
    // auth);) ->  YAHOO not ok and Gmail not ok
    // TLSv1.3: if  AUTH=off -> YAHOO ok but Gmail not ok
    //
    // TLS + STARTTLS: props.put("mail.smtp.ssl.protocols", "TLSv1.3 TLSv1.2 TLSv1.2 TLSv1.1
    // TLSv1"); ok for Yahoo end Ziggo but not for GMail
    // SSL           : props.put("mail.smtp.ssl.protocols", "TLSv1.3 TLSv1.2 TLSv1.1 TLSv1 SSLv3
    // SSLv2Hello"); ok for Yahoo end Ziggo but not for GMail
    // SSL + STARTTLS: props.put("mail.smtp.ssl.protocols", "TLSv1.3 TLSv1.2 TLSv1.1 TLSv1 SSLv3
    // SSLv2Hello"); ok for Yahoo end Ziggo but not for GMail
    //
    //

    boolean debug = false;

    // Properties props = new Properties();
    props.put("mail.smtp.timeout", "60000"); // 1000 = 1 sec; 60000 = 1 min
    props.put("mail.smtp.connectiontimeout", "60000");
    props.put(
        "mail.from",
        send_from); // The return email address of the current user, used by the InternetAddress
    // method getLocalAddress.
    props.put(
        "mail.smtp.host",
        smtp_host_local); // The host name of the mail server for the specified protocol. Overrides
    // the mail.host property.
    props.put(
        "mail.smtp.port",
        smtp_port_local); // The port number of the mail server for the specified protocol. If not
    // specified the protocol's default port number is used.
    props.put(
        "mail.smtp.user",
        send_from); // The user name to use when connecting to mail servers using the specified
    // protocol. Overrides the mail.user property.
    props.put("mail.smtp.password", smtp_password_local);
    // props.put("mail.smtp.auth", "true");
    if (debug) {
      props.put("mail.debug", "true");
    }

    if (smtp_mode.equals(main.CUSTOM_SSL)) {
      props.put(
          "mail.smtp.ssl.enable",
          "true"); // If set to true, use SSL to connect and use the SSL port by default

      if ((smtp_host_local != null)
          && (smtp_host_local.toLowerCase().contains("gmail")
              || smtp_host_local.toLowerCase().contains("outlook"))) {
        props.put(
            "mail.smtp.ssl.protocols",
            "TLSv1.2 TLSv1.1 TLSv1 SSLv3 SSLv2Hello"); // (test 26-aug-2022: via gmail: TLsv1.3:
        // handshake_failure)
      } else {
        props.put(
            "mail.smtp.ssl.protocols",
            "TLSv1.3 TLSv1.2 TLSv1.1 TLSv1 SSLv3 SSLv2Hello"); // sequence is important! // (test
        // 26-aug-2022: via gmail: TLsv1.3:
        // handshake_failure)
      }
      props.put(
          "mail.smtp.ssl.trust",
          smtp_host_local); // If set, and a socket factory hasn't been specified, enables use of a
      // MailSSLSocketFactory. If set to "*", all hosts are trusted. If set to
      // a whitespace separated list of hosts, those hosts are trusted.
      // Otherwise, trust depends on the certificate the server presents.
    } else if (smtp_mode.equals(main.CUSTOM_SSL_STARTTLS)) {
      props.put(
          "mail.smtp.ssl.enable",
          "true"); // If set to true, use SSL to connect and use the SSL port by default
      if ((smtp_host_local != null)
          && (smtp_host_local.toLowerCase().contains("gmail")
              || smtp_host_local.toLowerCase().contains("outlook"))) {
        props.put(
            "mail.smtp.ssl.protocols",
            "TLSv1.2 TLSv1.1 TLSv1 SSLv3 SSLv2Hello"); // (test 26-aug-2022: via gmail: TLsv1.3:
        // handshake_failure)
      } else {
        props.put(
            "mail.smtp.ssl.protocols",
            "TLSv1.3 TLSv1.2 TLSv1.1 TLSv1 SSLv3 SSLv2Hello"); // sequence is important!
      }
      props.put(
          "mail.smtp.ssl.trust",
          smtp_host_local); // If set, and a socket factory hasn't been specified, enables use of a
      // MailSSLSocketFactory. If set to "*", all hosts are trusted. If set to
      // a whitespace separated list of hosts, those hosts are trusted.
      // Otherwise, trust depends on the certificate the server presents.
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.starttls.required", "true");
    } else if (smtp_mode.equals(main.CUSTOM_TLS)) {
      if ((smtp_host_local != null)
          && (smtp_host_local.toLowerCase().contains("gmail")
              || smtp_host_local.toLowerCase().contains("outlook"))) {
        props.put(
            "mail.smtp.ssl.protocols",
            "TLSv1.2 TLSv1.1 TLSv1"); // to avoid exception: "Could not convert socket to TLS" //
        // (test 26-aug-2022: via gmail: TLsv1.3: handshake_failure)
      } else {
        props.put("mail.smtp.ssl.protocols", "TLSv1.3 TLSv1.2 TLSv1.1 TLSv1");
      }
      props.put(
          "mail.smtp.ssl.trust",
          smtp_host_local); // to avoid exception: "Could not convert socket to TLS" // If set, and
      // a socket factory hasn't been specified, enables use of a
      // MailSSLSocketFactory. If set to "*", all hosts are trusted. If set to
      // a whitespace separated list of hosts, those hosts are trusted.
      // Otherwise, trust depends on the certificate the server presents.
    } else if (smtp_mode.equals(main.CUSTOM_TLS_STARTTLS)) {
      if ((smtp_host_local != null)
          && (smtp_host_local.toLowerCase().contains("gmail")
              || smtp_host_local
                  .toLowerCase()
                  .contains(
                      "outlook"))) // (test 26-aug-2022: via gmail: TLsv1.3: handshake_failure)
      {
        props.put(
            "mail.smtp.ssl.protocols",
            "TLSv1.2 TLSv1.1 TLSv1"); // to avoid exception: "Could not convert socket to TLS" //
        // (test 26-aug-2022: via gmail: TLsv1.3: handshake_failure)
      } else {
        props.put("mail.smtp.ssl.protocols", "TLSv1.3 TLSv1.2 TLSv1.1 TLSv1");
      }
      props.put(
          "mail.smtp.ssl.trust",
          smtp_host_local); // to avoid exception: "Could not convert socket to TLS" // If set, and
      // a socket factory hasn't been specified, enables use of a
      // MailSSLSocketFactory. If set to "*", all hosts are trusted. If set to
      // a whitespace separated list of hosts, those hosts are trusted.
      // Otherwise, trust depends on the certificate the server presents.
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.starttls.required", "true");
    }

    // extra info only for console
    //
    String protocols;
    try {
      protocols =
          String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols());
      System.out.println("--- in this openJDK supported protocols: " + protocols);
    } catch (NoSuchAlgorithmException ex) {
      System.out.println("--- in this openJDK supported protocols: " + ex);
    }
  }

  private int check_jakarta_email_parameters(
      final String smtp_host_local,
      final String send_to,
      final String send_from,
      final String subject,
      final String body,
      final String smtp_port_local) {
    int checkStatus = 0;

    if (smtp_host_local == null || smtp_host_local.equals("")) {
      checkStatus = 1;
      main.log_turbowin_system_message(
          "[EMAIL] not possible to send: "
              + "Email server name not inserted (Maintenance -> Email settings)");
    }

    // NB smtp_password_local could be blank!
    // else if (smtp_password_local == null)
    // {
    //   exitStatus = 1;
    //   main.log_turbowin_system_message("[EMAIL] not possible to send: " + "Email password not
    // inserted (Maintenance -> Email settings)");
    // }
    else if (send_to == null || send_to.equals("")) {
      checkStatus = 1;
      main.log_turbowin_system_message(
          "[EMAIL] not possible to send: "
              + "Email address recipient not inserted (Maintenance -> Email settings)");
    } else if (send_from == null || send_from.equals("")) {
      checkStatus = 1;
      main.log_turbowin_system_message(
          "[EMAIL] not possible to send: "
              + "your Email address not inserted (Maintenance -> Email settings)");
    } else if (subject == null || subject.equals("")) {
      checkStatus = 1;
      main.log_turbowin_system_message(
          "[EMAIL] not possible to send: "
              + "Email subject not inserted (Maintenance -> Email settings)");
    } else if (body == null || body.equals("")) {
      checkStatus = 1;
      main.log_turbowin_system_message("[EMAIL] not possible to send: " + "Email body empty");
    } else if (smtp_port_local == null || smtp_port_local.equals("")) {
      checkStatus = 1;
      main.log_turbowin_system_message(
          "[EMAIL] not possible to send: "
              + "Email port not inserted (Maintenance -> Email settings)");
    }

    return checkStatus;
  }

  public int send_jakarta_email_obs(
      final String smtp_mode,
      final String smtp_host_local,
      final String smtp_password_local,
      final String send_to,
      final String send_from,
      final String subject,
      final String body,
      final String send_cc,
      final String smtp_port_local,
      final String attachment) {
    // called from: - Output_obs_by_email_jakarta_FM13_format_101() ---doInBackground--- [main.java]
    //

    // NB https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html

    // NB --- not necessary to use smtps ---!
    // The Transport.send method will use the default transport protocol, which remains "smtp".
    // To enable SMTP connections over SSL, set the "mail.smtp.ssl.enable" property to "true". This
    // is usually the easiest approach.

    boolean debug = false; // true;
    int exitStatus = 0;

    // first check the required parameters
    exitStatus =
        check_jakarta_email_parameters(
            smtp_host_local, send_to, send_from, subject, body, smtp_port_local);

    if (exitStatus == 0) {
      // NB
      // https://eclipse-ee4j.github.io/mail/docs/api/jakarta.mail/jakarta/mail/package-summary.html
      //

      set_jakarta_email_props(
          smtp_mode, smtp_host_local, smtp_password_local, send_from, smtp_port_local);

      Session session = Session.getInstance(props, null);
      /*
               Authenticator authenticator = new Authenticator()
               {
                  @Override
                  protected PasswordAuthentication getPasswordAuthentication()
                  {
                     //return new PasswordAuthentication(USERNAME, PASSWORD);
                     return new PasswordAuthentication("turbowin.observations@outlook.com", smtp_password_local);
                  }
               };

               Session session = Session.getInstance(props, authenticator);
      */

      if (debug) {
        session.setDebug(true);
      }

      try {
        // create a message
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(send_from)); // set From email field
        // InternetAddress[] address = {new InternetAddress(send_to)};                          //
        // The Address class is an abstract class that models the addresses (To and From addresses)
        // in an email message; its subclasses support the actual implementations. Usually, its
        // javax.mail.internet.InternetAddress subclass, which denotes an Internet email address, is
        // commonly used.
        // msg.setRecipients(Message.RecipientType.TO, address);                                //
        // setRecipients(Message.RecipientType type, String addresses) it s used to set the stated
        // recipient type to the provided addresses. The possible defined address types are TO
        // (Message.RecipientType.TO), CC (Message.RecipientType.CC), and BCC
        // (Message.RecipientType.BCC).
        msg.setRecipients(
            Message.RecipientType.TO, InternetAddress.parse(send_to, false)); // set To email field
        if (!send_cc.equals("null")) {
          msg.setRecipients(
              Message.RecipientType.CC,
              InternetAddress.parse(send_cc, false)); // set cc email field
        }
        msg.setSubject(subject); // set email subject field
        msg.setSentDate(new Date());
        // If the desired charset is known, you can use
        // setText(text, charset)
        // mail.smtp.charset=UTF-8

        if (attachment.equals("yes")) {
          // NB in TurboWin+ an attachment only possible for format101 not possible if format = FM13

          // e.g.
          // "C:\NetBeansProjects\turbowin_jws\dist\jlink\turbowin_jws\bin\logs\format_101\temp\HPK_format_101.txt"
          //       NB "HPK_" + main.FORMAT_101_INPUT_FILE; (file for format 101 (NB outputfile is
          // automatically "HPK_" + input file))
          final File attachment_file =
              new File(
                  main.logs_dir
                      + java.io.File.separator
                      + main.FORMAT_101_ROOT_DIR
                      + java.io.File.separator
                      + main.FORMAT_101_TEMP_DIR
                      + java.io.File.separator
                      + "HPK_"
                      + main.FORMAT_101_INPUT_FILE);

          if (attachment_file.exists()) {
            // Attach the specified file.
            // We need a multipart message to hold the attachment.
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(body);

            MimeBodyPart mbp2 = new MimeBodyPart();
            try {
              mbp2.attachFile(attachment_file.toString());
              MimeMultipart mp = new MimeMultipart();
              mp.addBodyPart(mbp1);
              mp.addBodyPart(mbp2);
              msg.setContent(mp);
            } catch (IOException ex) {
              main.log_turbowin_system_message(
                  "[EMAIL] obs send failed (no attachment found): " + ex);
              exitStatus = 1;
            }
          } // if (attachment_file.exists())
          else {
            main.log_turbowin_system_message(
                "[EMAIL] obs send failed (no attachment found): " + attachment_file.toString());
            exitStatus = 1;
          }
        } // if (attachment.equals("yes"))
        else {
          msg.setText(body);
        }

        // send
        if (smtp_password_local == null || smtp_password_local.equals("")) {
          // the simple send method
          Transport.send(msg);
        } else {
          // the extendent send method
          SMTPTransport transp =
              (SMTPTransport) session.getTransport("smtp"); // NB "smtp" AND NOT "SMTP"!!
          try {
            transp.connect(smtp_host_local, send_from, smtp_password_local);
            transp.sendMessage(msg, msg.getAllRecipients());
          } finally {
            System.out.println("--- last email server response: " + transp.getLastServerResponse());
            transp.close();
          }
        } // else

        // System.out.println("--- send_jakarta_email module: Mail was sent successfully.");
        main.log_turbowin_system_message("[EMAIL] obs email sent successfully");

      } catch (MessagingException e) {
        // System.out.println("\n--Exception handling in send_jakarta_email.java: " + e);

        // Handle SMTP-specific exceptions.
        //
        if (e instanceof SendFailedException) {
          MessagingException sfe = (MessagingException) e;
          if (sfe instanceof SMTPSendFailedException) {
            SMTPSendFailedException ssfe = (SMTPSendFailedException) sfe;

            // to get everthing on one single line in he log
            String info = ssfe.getMessage();
            Throwable cause = ssfe.getCause();
            if (cause != null) {
              info = info + ". cause: " + cause.getMessage();
            }
            info = info.replace("\n", ". ");
            main.log_turbowin_system_message("[EMAIL] obs send failed: " + info);

            exitStatus = 1;
          } else {
            // to get everthing on one single line in he log
            String info = sfe.getMessage();
            Throwable cause = sfe.getCause();
            if (cause != null) {
              info = info + ". cause: " + cause.getMessage();
            }
            info = info.replace("\n", ". ");
            main.log_turbowin_system_message("[EMAIL] obs send failed: " + info);

            exitStatus = 1;
          }
          Exception ne;
          while ((ne = sfe.getNextException()) != null && ne instanceof MessagingException) {
            sfe = (MessagingException) ne;
            if (sfe instanceof SMTPAddressFailedException) {
              SMTPAddressFailedException ssfe = (SMTPAddressFailedException) sfe;

              // to get everthing on one single line in the log
              String info = ssfe.getMessage();
              Throwable cause = ssfe.getCause();
              if (cause != null) {
                info = info + ". cause: " + cause.getMessage();
              }
              info = info.replace("\n", ". ");
              main.log_turbowin_system_message("[EMAIL] obs address failed: " + info);

              exitStatus = 1;
            } else if (sfe instanceof SMTPAddressSucceededException) {
              // System.out.println("ADDRESS SUCCEEDED:");
              SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) sfe;

              // to get everthing on one single line in the log
              String info = ssfe.getMessage();
              Throwable cause = ssfe.getCause();
              if (cause != null) {
                info = info + ". cause: " + cause.getMessage();
              }
              info = info.replace("\n", ". ");
              main.log_turbowin_system_message("[EMAIL] obs address succeeded: " + info);

              // NB for this one do not change the exitStatus
            }
          } // while
        } else {
          // to get everthing in one single line in the log
          String msg = e.getMessage();
          Throwable cause = e.getCause();
          if (cause != null) {
            msg = msg + ". cause: " + cause.getMessage();
          }
          msg = msg.replace("\n", ". ");
          main.log_turbowin_system_message("[EMAIL] obs exception: " + msg);

          exitStatus = 1;
        } // else
      } // catch (MessagingException e)
    } // if exitStatus = 0

    return exitStatus;
  }

  public int send_jakarta_email_logs(
      final String smtp_mode,
      final String smtp_host_local,
      final String smtp_password_local,
      final String send_to,
      final String send_from,
      final String subject,
      final String body,
      final String smtp_port_local,
      final String attachment) {
    // called from: - Maintenance_Move_log_files_by_email_actionPerformed() --- doInBackground ---
    // [main.java]
    //

    // NB https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html

    // NB --- not necessary to use smtps ---!
    // The Transport.send method will use the default transport protocol, which remains "smtp".
    // To enable SMTP connections over SSL, set the "mail.smtp.ssl.enable" property to "true". This
    // is usually the easiest approach.

    boolean debug = false; // true;
    int exitStatus = 0; // NB exitStatus 1-100 reserved by function
    // Maintenance_Move_log_files_by_email_actionPerformed() --- doInBackground ---
    // [main.java]

    // check the required parameters
    exitStatus =
        check_jakarta_email_parameters(
            smtp_host_local, send_to, send_from, subject, body, smtp_port_local);

    if (exitStatus == 0) {
      // NB
      // https://eclipse-ee4j.github.io/mail/docs/api/jakarta.mail/jakarta/mail/package-summary.html

      set_jakarta_email_props(
          smtp_mode, smtp_host_local, smtp_password_local, send_from, smtp_port_local);

      Session session = Session.getInstance(props, null);

      if (debug) {
        session.setDebug(true);
      }

      try {
        // create a message
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(send_from)); // set From email field
        // InternetAddress[] address = {new InternetAddress(send_to)};                 // The
        // Address class is an abstract class that models the addresses (To and From addresses) in
        // an email message; its subclasses support the actual implementations. Usually, its
        // javax.mail.internet.InternetAddress subclass, which denotes an Internet email address, is
        // commonly used.
        // msg.setRecipients(Message.RecipientType.TO, address);                       //
        // setRecipients(Message.RecipientType type, String addresses) it s used to set the stated
        // recipient type to the provided addresses. The possible defined address types are TO
        // (Message.RecipientType.TO), CC (Message.RecipientType.CC), and BCC
        // (Message.RecipientType.BCC).
        msg.setRecipients(
            Message.RecipientType.TO, InternetAddress.parse(send_to)); // set To email field
        msg.setSubject(subject); // set email subject field
        msg.setSentDate(new Date());

        if (attachment.equals("yes")) {
          // e.g.: C:\NetBeansProjects\turbowin_jws\dist\jlink\turbowin_jws\bin\logs\temp\Happy
          // Sailor VIII logs.zip
          final File attachment_file =
              new File(
                  main.logs_dir
                      + java.io.File.separator
                      + "temp"
                      + java.io.File.separator
                      + main.ship_name
                      + " "
                      + main.LOGS_ZIP);

          if (attachment_file.exists()) {
            // Attach the specified file.
            // We need a multipart message to hold the attachment.
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(body);

            MimeBodyPart mbp2 = new MimeBodyPart();
            try {
              mbp2.attachFile(attachment_file.toString());
              MimeMultipart mp = new MimeMultipart();
              mp.addBodyPart(mbp1);
              mp.addBodyPart(mbp2);
              msg.setContent(mp);
            } catch (IOException ex) {
              main.log_turbowin_system_message(
                  "[EMAIL] meteo logs send failed (no attachment found): " + ex);
              exitStatus = 101;
            }
          } // if (attachment_file.exists())
          else {
            main.log_turbowin_system_message(
                "[EMAIL] meteo logs send failed (no attachment found): "
                    + attachment_file.toString());
            exitStatus = 101;
          }
        } // if (attachment.equals("yes"))
        else {
          msg.setText(body);
        }

        // send
        if (smtp_password_local == null || smtp_password_local.equals("")) {
          // the simple send method
          Transport.send(msg);
        } else {
          // the extendent send method
          SMTPTransport transp =
              (SMTPTransport) session.getTransport("smtp"); // NB "smtp" AND NOT "SMTP"!!
          try {
            transp.connect(smtp_host_local, send_from, smtp_password_local);
            transp.sendMessage(msg, msg.getAllRecipients());
          } finally {
            System.out.println("--- last email server response: " + transp.getLastServerResponse());
            transp.close();
          }
        } // else

        main.log_turbowin_system_message(
            "[EMAIL] meteo logs successfully sent from " + send_from + " to " + send_to);
        exitStatus = 200; // success
      } catch (MessagingException e) {
        // System.out.println("\n--Exception handling in send_jakarta_email.java: " + e);

        // Handle SMTP-specific exceptions.
        //
        if (e instanceof SendFailedException) {
          MessagingException sfe = (MessagingException) e;
          if (sfe instanceof SMTPSendFailedException) {
            SMTPSendFailedException ssfe = (SMTPSendFailedException) sfe;

            // to get everthing on one single line in he log
            String info = ssfe.getMessage();
            Throwable cause = ssfe.getCause();
            if (cause != null) {
              info = info + ". cause: " + cause.getMessage();
            }
            info = info.replace("\n", ". ");
            main.log_turbowin_system_message("[EMAIL] meteo logs send failed: " + info);

            exitStatus = 102;
          } else {
            // to get everthing on one single line in he log
            String info = sfe.getMessage();
            Throwable cause = sfe.getCause();
            if (cause != null) {
              info = info + ". cause: " + cause.getMessage();
            }
            info = info.replace("\n", ". ");
            main.log_turbowin_system_message("[EMAIL] meteo logs send failed: " + info);

            exitStatus = 102;
          }
          Exception ne;
          while ((ne = sfe.getNextException()) != null && ne instanceof MessagingException) {
            sfe = (MessagingException) ne;
            if (sfe instanceof SMTPAddressFailedException) {
              SMTPAddressFailedException ssfe = (SMTPAddressFailedException) sfe;

              // to get everthing on one single line in the log
              String info = ssfe.getMessage();
              Throwable cause = ssfe.getCause();
              if (cause != null) {
                info = info + ". cause: " + cause.getMessage();
              }
              info = info.replace("\n", ". ");
              main.log_turbowin_system_message("[EMAIL] meteo logs address failed: " + info);

              exitStatus = 102;
            } else if (sfe instanceof SMTPAddressSucceededException) {
              // System.out.println("ADDRESS SUCCEEDED:");
              SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) sfe;

              // to get everthing on one single line in the log
              String info = ssfe.getMessage();
              Throwable cause = ssfe.getCause();
              if (cause != null) {
                info = info + ". cause: " + cause.getMessage();
              }
              info = info.replace("\n", ". ");
              main.log_turbowin_system_message("[EMAIL] meteo logs address succeeded: " + info);

              // NB for this one do not change the exitStatus
            }
          } // while
        } else {
          // to get everthing in one single line in the log
          String msg = e.getMessage();
          Throwable cause = e.getCause();
          if (cause != null) {
            msg = msg + ". cause: " + cause.getMessage();
          }
          msg = msg.replace("\n", ". ");
          main.log_turbowin_system_message("[EMAIL] meteo logs exception: " + msg);

          exitStatus = 102;
        } // else
      } // catch (MessagingException e)
    } // if exitStatus = 0

    return exitStatus;
  }

  // variables section
  Properties props;
}

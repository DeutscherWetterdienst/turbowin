package turbowin;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class main_RS232_RS422 {

  public void RS422_Check_Default_Serial_Port() {
    // called from: RS422_init_new_aws_data_received_check_timer() [main_RS232_RS422.java]
    //
    // AWS connection (not used if barometer connected)

    SerialPortResetWorkflow.reset(
        main.serialPort,
        "AWS",
        () -> {
          main.defaultPort = null;
          main.defaultPort_descriptive = null;
        });
  }

  public void RS422_Check_Serial_Ports(int completed_checks_serial_ports) {
    // called from - main_RS232_RS422.RS422_initComponents() [main_RS232_RS422.java]
    //             - RS422_init_new_aws_data_received_check_timer() [main_RS232_RS422.java]
    //
    // AWS connection (not used if individual sensor like barometer/thermometer/GPS connected)

    int teller = -1;
    // String[] serial_ports_portid_array                = new String[main.NUMBER_COM_PORTS];
    SerialPort[] serial_ports_portid_array = new SerialPort[main.NUMBER_COM_PORTS];
    String info = "[AWS] start up: no AWS found";
    String hulp_parity = ""; // for message writing on java console

    // initialisation
    for (int i = 0; i < main.NUMBER_COM_PORTS; i++) {
      serial_ports_portid_array[i] = null;
    }

    // initialisation
    main.defaultPort = null;
    main.defaultPort_descriptive = null;

    if (main.prefered_COM_port.equals("AUTOMATICALLY")) {
      // Temporary message box, (pop-up for only a short time) automatically disappears
      //
      if (completed_checks_serial_ports
          == 0) // only show this temporary meassagebox at first port-checking-round
      {
        final JOptionPane pane_begin =
            new JOptionPane(
                "[AWS] checking serial com ports (this may take a while)",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_begin_dialog = pane_begin.createDialog(main.APPLICATION_NAME);

        Timer timer_begin =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    checking_ports_begin_dialog.dispose();
                  }
                });
        timer_begin.setRepeats(false);
        timer_begin.start();
        checking_ports_begin_dialog.setVisible(true);
      }

      // Get sorted array of serial ports in the system
      // main.portList = SerialPort.getCommPorts();//main.portList = SerialPortList.getPortNames();
      // main.serialPorts = SerialPort.getCommPorts();
      SerialPort[] serialPorts =
          SerialPort.getCommPorts(); // main.portList = SerialPortList.getPortNames();

      for (int portNo = 0;
          portNo < serialPorts.length;
          portNo++) // for (int i = 0; i < main.portList.length && i < main.NUMBER_COM_PORTS; i++)
      {
        teller++;
        SerialPort serialPort_test =
            SerialPort.getCommPort(serialPorts[portNo].getSystemPortName());
        serialPort_test.openPort();

        if (serialPort_test.isOpen()) {
          serial_ports_portid_array[teller] = serialPorts[portNo];
          serialPort_test.closePort();
          String message =
              "[AWS] found serial port (ok): " + serialPorts[portNo].getDescriptivePortName();
          main.log_turbowin_system_message(message);
        } // if (serialPort_test.isOpen())
        else {
          serial_ports_portid_array[teller] = null;
          System.out.println("couldn't open: " + serialPorts[portNo].getDescriptivePortName());
        } // else

        // NB opening a port cannot be done in SwingWorker, so must be done here !!

      } //  for (int i = 0; i < main.portList.length && i < main.NUMBER_COM_PORTS; i++)

      for (int i = 0; i < main.NUMBER_COM_PORTS; i++) // max number ports to open
      {
        if (serial_ports_portid_array[i] != null) // so serial port present (and also not in use)
        {
          SerialPort serialPort_test =
              SerialPort.getCommPort(
                  serial_ports_portid_array[i]
                      .getSystemPortName()); // SerialPort serialPort_test = new
          // SerialPort(serial_ports_portid_array[i]);

          serialPort_test.openPort();
          if (serialPort_test.isOpen()) {
            if (main.parity == 0) {
              hulp_parity = "none";
            } else if (main.parity == 1) {
              hulp_parity = "odd";
            } else if (main.parity == 2) {
              hulp_parity = "even";
            }

            System.out.println(
                "[AWS] trying to open with "
                    + String.valueOf(main.bits_per_second)
                    + " "
                    + hulp_parity
                    + " "
                    + String.valueOf(main.data_bits)
                    + " "
                    + String.valueOf(main.stop_bits)
                    + " "
                    + serial_ports_portid_array[i].getDescriptivePortName());
            serialPort_test.setComPortParameters(
                main.bits_per_second, main.data_bits, main.stop_bits, main.parity);
            serialPort_test.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            int timeout = 1000;
            serialPort_test.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,
                timeout,
                timeout);

            if (main.RS232_connection_mode == 3) // EUCAWS
            {
              int written_bytes = 0;
              System.out.println(
                  "[AWS] Writing \""
                      + "?"
                      + "\" to "
                      + serial_ports_portid_array[i].getDescriptivePortName());
              String messageString_info = "?\r\n"; // or "?\r"
              byte[] bytes_message =
                  messageString_info.getBytes(StandardCharsets.UTF_8); // Java 7+ only
              serialPort_test.writeBytes(bytes_message, bytes_message.length); // Write data to port
              written_bytes =
                  serialPort_test.writeBytes(
                      bytes_message,
                      bytes_message.length); // determine the number of actually written bytes

              try {
                Thread.sleep(2000); // 1000 seems too short on a few systems
              } catch (InterruptedException ex) {
              }

              // the response
              //
              // String response = serialPort_test.readString();
              // System.out.println(response);

              if (written_bytes == 0) {
                System.out.println("");
                info =
                    "[AWS] no AWS found ("
                        + serial_ports_portid_array[i].getDescriptivePortName()
                        + " timed out)"; // for console window (all messages wil be displayed)
                System.out.println("--- " + info);
                System.out.println("");
                serialPort_test.closePort();
                info =
                    "[AWS] no AWS found"; // for system log (only last recorded info will be written
                // to the log)
              } else // written_bytes != 0
              {
                String response = "";
                serialPort_test.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
                try {
                  byte[] readBuffer = new byte[64];
                  serialPort_test.readBytes(readBuffer, readBuffer.length);
                  response = new String(readBuffer, StandardCharsets.UTF_8);
                  System.out.println(response);
                } catch (Exception e) {
                  System.out.println(e);
                }

                if (SerialPortResponseMatcher.contains(response, "EUCAWS")) {
                  info =
                      "[AWS] found AWS on port: "
                          + serial_ports_portid_array[i].getDescriptivePortName();
                  System.out.print("--- ");
                  main.log_turbowin_system_message(info);
                  System.out.println("");

                  serialPort_test.closePort();
                  main.defaultPort = serial_ports_portid_array[i].getSystemPortName();
                  main.defaultPort_descriptive =
                      serial_ports_portid_array[i].getDescriptivePortName();
                  break;
                } else {
                  info = "[AWS] no AWS found";
                  System.out.println("--- " + info);
                  System.out.println("");
                  serialPort_test.closePort();
                }
              } // else (written_bytes != 0)
            } // if (main.RS232_connection_mode == 3)   // EUCAWS serial
            else if (main.RS232_connection_mode == 9) // OMC-140 serial
            {
              try {
                Thread.sleep(2000); // 1000 seems too short on a few systems
              } catch (InterruptedException ex) {
              }

              // String response = serialPort_test.readString();
              // System.out.println(response);

              // the response
              String response = "";
              // NB The TIMEOUT_READ_SEMI_BLOCKING mode specifies that the corresponding call will
              // block until either newReadTimeout milliseconds of inactivity have elapsed or at
              // least 1 byte of data can be read.
              //
              // if testing OMC-140 with the EUCAWS simulator then set newReadTimeout >= 60000
              // because data send every minute
              // in a real situation the OMC-140 will send several messages (not only PEUMB) every
              // minute
              // so newReadTimeout set to 20000 will be ok
              //
              // serialPort_test.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);
              // // till 11-06-2018
              serialPort_test.setComPortTimeouts(
                  SerialPort.TIMEOUT_READ_BLOCKING, 20000, 0); // from 11-06-2018
              try {
                // while (true) //NB while(true) doesn't work (idefinite loop, despite the
                // newReadTimeout...) !!!!!!!!!
                // {
                byte[] readBuffer =
                    new byte
                        [1024]; // NB keep size 1024 because first part of incoming data is rubish
                // int numRead = serialPort_test.readBytes(readBuffer, readBuffer.length);
                serialPort_test.readBytes(readBuffer, readBuffer.length);
                // System.out.println("Read " + numRead + " bytes.");
                response = new String(readBuffer, StandardCharsets.UTF_8);
                System.out.println(response);
                // }
              } catch (Exception e) {
                System.out.println(e);
              }

              // OMC-140 fills the 1024-byte buffer with leading rubbish and several messages; the
              // "$" check is deliberately broad because the response of interest is the last line.
              if (SerialPortResponseMatcher.contains(response, "$")) {
                info =
                    "[AWS] found AWS on port: "
                        + serial_ports_portid_array[i].getDescriptivePortName();
                System.out.print("--- ");
                main.log_turbowin_system_message(info);
                System.out.println("");

                serialPort_test.closePort();
                main.defaultPort = serial_ports_portid_array[i].getSystemPortName();
                main.defaultPort_descriptive =
                    serial_ports_portid_array[i].getDescriptivePortName();
                break;
              } else {
                serialPort_test.closePort();
                info = "[AWS] no AWS found";
                System.out.println("--- " + info);
                System.out.println("");
              }
            } // else if (main.RS232_connection_mode == 9)
            else if (main.RS232_connection_mode == 11) // AMOS2X
            {
              int written_bytes = 0;
              System.out.println(
                  "[AWS] Writing \""
                      + "?"
                      + "\" to "
                      + serial_ports_portid_array[i].getDescriptivePortName());
              String messageString_info = "?\r\n"; // or "?\r"
              byte[] bytes_message =
                  messageString_info.getBytes(StandardCharsets.UTF_8); // Java 7+ only
              serialPort_test.writeBytes(bytes_message, bytes_message.length); // Write data to port
              written_bytes =
                  serialPort_test.writeBytes(
                      bytes_message,
                      bytes_message.length); // determine the number of actually written bytes

              try {
                Thread.sleep(2000); // 1000 seems too short on a few systems
              } catch (InterruptedException ex) {
              }

              // the response
              //
              // String response = serialPort_test.readString();
              // System.out.println(response);

              if (written_bytes == 0) {
                System.out.println("");
                info =
                    "[AWS] no AWS found ("
                        + serial_ports_portid_array[i].getDescriptivePortName()
                        + " timed out)"; // for console window (all messages wil be displayed)
                System.out.println("--- " + info);
                System.out.println("");
                serialPort_test.closePort();
                info =
                    "[AWS] no AWS found"; // for system log (only last recorded info will be written
                // to the log)
              } else // written_bytes != 0
              {
                String response = "";
                serialPort_test.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
                try {
                  byte[] readBuffer = new byte[64];
                  serialPort_test.readBytes(readBuffer, readBuffer.length);
                  response = new String(readBuffer, StandardCharsets.UTF_8);
                  System.out.println(response);
                } catch (Exception e) {
                  System.out.println(e);
                }

                if (SerialPortResponseMatcher.contains(response, "AMOS")) {
                  info =
                      "[AWS] found AWS on port: "
                          + serial_ports_portid_array[i].getDescriptivePortName();
                  System.out.print("--- ");
                  main.log_turbowin_system_message(info);
                  System.out.println("");

                  serialPort_test.closePort();
                  main.defaultPort = serial_ports_portid_array[i].getSystemPortName();
                  main.defaultPort_descriptive =
                      serial_ports_portid_array[i].getDescriptivePortName();
                  break;
                } else {
                  info = "[AWS] no AWS found";
                  System.out.println("--- " + info);
                  System.out.println("");
                  serialPort_test.closePort();
                }
              } // else (written_bytes != 0)
            } // if (main.RS232_connection_mode == 11)   // AMOS2X serial
          } // if (serialPort_test.isOpen())
          else {
            System.out.println("[AWS] couldn't open: " + serial_ports_portid_array[i]);
          }
        } // if (serial_ports_portid_array[i] != null)
      } // for (int i = 0; i < NUMBER_COM_PORTS; i++)

      if (main.defaultPort != null) {
        // temporary message box (AWS was found)
        final JOptionPane pane_end =
            new JOptionPane(
                info,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

        Timer timer_end =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    // checking_ports_end_dialog.setVisible(false);
                    // or maybe you'll need dialog.dispose() instead?
                    checking_ports_end_dialog.dispose();
                  }
                });
        timer_end.setRepeats(false);
        timer_end.start();
        checking_ports_end_dialog.setVisible(true);
      } else {
        // No com port with an AWS connected found

        // logging and blocking message box (no AWS found); but ony show message if it is the last
        // port-checking-round
        if (completed_checks_serial_ports == MAX_COMPLETED_AWS_PORT_CHECKS - 1) {
          main.log_turbowin_system_message(
              info); // default info used here: "[AWS] start up: no AWS found";

          if (main.obsolate_data_flag == false) {
            // NB so only show this blocking messagebox the first time at start up but not in
            // retrying to establish a new connection mode
            //    see also: RS422_init_new_aws_data_received_check_timer() [main_RS232_RS422.java]
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
        }
      } // else

    } // if (main.prefered_COM_port.equals("AUTOMATICALLY"))
    else // fixed COM port selected
    {
      try {
        SerialPort serialPort_test =
            SerialPort.getCommPort(main.prefered_COM_port); // SerialPort serialPort_test = new
        // SerialPort(main.prefered_COM_port);
        serialPort_test.openPort();

        if (serialPort_test.isOpen()) {
          serialPort_test.closePort();

          // OK, no problems encoutered
          main.defaultPort = main.prefered_COM_port;
          main.defaultPort_descriptive =
              main.prefered_COM_port; // NB this is a work around this is not the exact descriptive
          // term as obtained via the AUTOMTICALLY branch
          info = "[AWS] " + main.prefered_COM_port + " serial COM port available";
          main.log_turbowin_system_message(info);

          final JOptionPane pane_begin =
              new JOptionPane(
                  info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog checking_ports_begin_dialog =
              pane_begin.createDialog(main.APPLICATION_NAME);

          Timer timer_begin =
              new Timer(
                  2500,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      checking_ports_begin_dialog.dispose();
                    }
                  });
          timer_begin.setRepeats(false);
          timer_begin.start();
          checking_ports_begin_dialog.setVisible(true);
        }
        // catch (SerialPortException ex)
        else {
          // error opening predefined (by the user)port, reset/warnings; logging and blocking
          // messagebox
          // System.out.println(ex);
          info = "[AWS] " + main.prefered_COM_port + " serial COM port not available";
          main.log_turbowin_system_message(info);
          if (main.obsolate_data_flag == false) {
            // NB so only show this blocking messagebox the first time at start up but not in
            // retrying to establish a new connection mode
            //    see also: RS422_init_new_aws_data_received_check_timer() [main_RS232_RS422.java]
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
          // System.out.println(info);
          main.defaultPort = null;
          main.defaultPort_descriptive = null;
        }
      } // try
      catch (SerialPortInvalidPortException | HeadlessException e) {
        // NB with fixed COM port on Windows systems: reconnecting will give in most situations no
        // problem (same USB-COM port)
        //                        on Linux systems: big chance the USB port is changed (especially
        // if via USB-hub)
        //
        //    so on Linux with fixed COM port big chance tise catch will be invoked but not on
        // Windows

        info = "[AWS] " + e;
        main.log_turbowin_system_message(info);
      } // catch
    } // else (fixed COM port selected)
  }

  public void RS232_GPS_NMEA_0183_Check_Default_Serial_Port() {
    // called from: RS232_GPS_NMEA_0183_init_new_data_received_check_timer() [main_RS232_RS422.java]
    //
    // NB after power break or temporarily device failure the (default/starting)serial port is still
    // open but the listener must be restarted

    SerialPortResetWorkflow.reset(
        main.GPS_serialPort,
        "GPS",
        () -> {
          GPS_defaultPort = null;
          GPS_defaultPort_descriptive = null;
        });
  }

  public void RS232_Check_Default_Serial_Port_II() {
    SerialPortResetWorkflow.reset(
        main.serialPort_II,
        "THERMOMETER",
        () -> {
          main.defaultPort_II = null;
          main.defaultPort_descriptive_II = null;
        });
  }

  public void RS232_Check_Default_Serial_Port() {
    // called from: RS232_And_WiFi_init_new_sensor_data_received_check_timer()
    // [main_RS232_RS422.java]
    //
    // NB after power break or temporarily device failure the (default/starting)serial port is still
    // open but the listener must be restarted

    SerialPortResetWorkflow.reset(
        main.serialPort,
        "BAROMETER",
        () -> {
          main.defaultPort = null;
          main.defaultPort_descriptive = null;
        });
  }

  public void RS232_Check_Serial_Ports_8(int completed_checks_serial_ports) {
    // called from: main_RS232_RS424.RS232_initComponents()
    //

    ///////// only used for PTB220/PTB330/Mintaka Duo/MintakaStar USB barometers (not for EUCAWS)
    // ////////

    // SerialPort serialPort_test                        = null;
    int teller = -1;
    // String[] serial_ports_portid_array                = new String[main.NUMBER_COM_PORTS];
    SerialPort[] serial_ports_portid_array = new SerialPort[main.NUMBER_COM_PORTS];
    String info = "[BAROMETER] start up: no barometer found";
    String hulp_parity = ""; // for message writing on java console

    // initialisation
    for (int i = 0; i < main.NUMBER_COM_PORTS; i++) {
      serial_ports_portid_array[i] = null;
    }

    // initialisation
    main.defaultPort = null;
    main.defaultPort_descriptive = null;

    ////////// automatically detecting COM port ///////////
    //
    if (main.prefered_COM_port.equals("AUTOMATICALLY")) {

      // Temporary message box, (pop-up for only a short time) automatically disappears
      //
      if (completed_checks_serial_ports
          == 0) // only show this temporary meassagebox at first port-checking-round
      {
        final JOptionPane pane_begin =
            new JOptionPane(
                "[BAROMETER] checking serial com ports (this may take a while)",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_begin_dialog = pane_begin.createDialog(main.APPLICATION_NAME);

        Timer timer_begin =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    checking_ports_begin_dialog.dispose();
                  }
                });
        timer_begin.setRepeats(false);
        timer_begin.start();
        checking_ports_begin_dialog.setVisible(true);
      }

      // list all the available ports
      SerialPort[] serialPorts =
          SerialPort.getCommPorts(); // main.portList = SerialPortList.getPortNames();

      for (int portNo = 0;
          portNo < serialPorts.length;
          portNo++) // for (int i = 0; i < main.portList.length && i < main.NUMBER_COM_PORTS; i++)
      {
        // NB opening a port cannot be done in SwingWorker, so must be done here !!

        teller++;
        // SerialPort serialPort_test =
        // SerialPort.getCommPort(serialPorts[portNo].getDescriptivePortName());
        SerialPort serialPort_test =
            SerialPort.getCommPort(serialPorts[portNo].getSystemPortName());
        serialPort_test.openPort();

        if (serialPort_test.isOpen()) {
          // serial_ports_portid_array[teller] = serialPorts[portNo].getSystemPortName();
          serial_ports_portid_array[teller] = serialPorts[portNo];
          serialPort_test.closePort();
          String message =
              "[BAROMETER] found serial port (ok): " + serialPorts[portNo].getDescriptivePortName();
          main.log_turbowin_system_message(message);
        } // if (serialPort_test.isOpen())
        else {
          serial_ports_portid_array[teller] = null;
          System.out.println(
              "[BAROMETER] couldn't open: " + serialPorts[portNo].getDescriptivePortName());
        } // else
      } // for (int portNo = 0; portNo < serialPorts.length; portNo++)

      for (int i = 0; i < main.NUMBER_COM_PORTS; i++) // max aantal te scannen poorten
      {
        if (serial_ports_portid_array[i]
            != null) // dus serial port aanwezig (en ook niet in gebruik)
        {
          // SerialPort serialPort_test =
          // SerialPort.getCommPort(serial_ports_portid_array[i]);//serialPort_test = new
          // SerialPort(serial_ports_portid_array[i]);
          SerialPort serialPort_test =
              SerialPort.getCommPort(serial_ports_portid_array[i].getSystemPortName());
          serialPort_test.openPort();
          if (serialPort_test.isOpen()) {
            serialPort_test.openPort(); // Open serial port

            if (main.parity == 0) {
              hulp_parity = "none";
            } else if (main.parity == 1) {
              hulp_parity = "odd";
            } else if (main.parity == 2) {
              hulp_parity = "even";
            }

            System.out.println(
                "[BAROMETER] trying to open with "
                    + String.valueOf(main.bits_per_second)
                    + " "
                    + hulp_parity
                    + " "
                    + String.valueOf(main.data_bits)
                    + " "
                    + String.valueOf(main.stop_bits)
                    + " "
                    + serial_ports_portid_array[i].getDescriptivePortName());
            serialPort_test.setComPortParameters(
                main.bits_per_second, main.data_bits, main.stop_bits, main.parity);
            serialPort_test.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            int timeout = 1000;
            serialPort_test.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,
                timeout,
                timeout); // to prevent blocking

            if (main.RS232_connection_mode == 1
                || main.RS232_connection_mode == 2) // PTB220 or PTB330
            {
              // send data to the barometer
              //
              int written_bytes = 0;
              for (int k = 0;
                  k < 5;
                  k++) // NB more times sending "s" absolutely necessary ? (see also HMP155 user's
              // guide; paragraph Serial Line Communication)
              {
                // send data to the barometer
                //
                System.out.println(
                    "[BAROMETER] Writing \""
                        + "S"
                        + "\" to "
                        + serial_ports_portid_array[i].getDescriptivePortName());
                String messageString_stop = "s\r";
                byte[] bytes_message_stop =
                    messageString_stop.getBytes(StandardCharsets.UTF_8); // Java 7+ only
                serialPort_test.writeBytes(
                    bytes_message_stop, bytes_message_stop.length); // Write data to port
                written_bytes =
                    serialPort_test.writeBytes(
                        bytes_message_stop,
                        bytes_message_stop
                            .length); // determine the number of actually written bytes

                try {
                  // Thread.sleep(2000);
                  Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
              } // for (int k = 0; k < 5; k++)

              if (written_bytes == 0) {
                System.out.println("");
                info =
                    "[BAROMETER] no barometer found ("
                        + serial_ports_portid_array[i].getDescriptivePortName()
                        + " timed out)"; // for console window (all messages wil be displayed)
                System.out.println("--- " + info);
                System.out.println("");
                serialPort_test.closePort();
                info = "[BAROMETER] no barometer found"; // for system log (only last recorded info
                // will be written to the log)
              } else // else (written_bytes != 0)
              {
                // System.out.println("[BAROMETER] Writing \"" + "VERS" + "\" to " +
                // serialPort_test.getDescriptivePortName());
                System.out.println(
                    "[BAROMETER] Writing \""
                        + "VERS"
                        + "\" to "
                        + serial_ports_portid_array[i].getDescriptivePortName());
                String messageString_info = "VERS\r";
                // serialPort_test.writeBytes(messageString_info.getBytes());                     //
                // Write data to port
                byte[] bytes_message_info =
                    messageString_info.getBytes(StandardCharsets.UTF_8); // Java 7+ only
                serialPort_test.writeBytes(
                    bytes_message_info, bytes_message_info.length); // Write data to port

                try {
                  Thread.sleep(2000); // 1000 seems too short on a few systems
                } catch (InterruptedException ex) {
                }

                // the response
                //
                // String response = serialPort_test.readString();
                // System.out.println(response);

                String response = "";
                serialPort_test.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
                try {
                  // while (true)
                  // {
                  byte[] readBuffer = new byte[64];
                  serialPort_test.readBytes(readBuffer, readBuffer.length);
                  response = new String(readBuffer, StandardCharsets.UTF_8);
                  System.out.println(response);
                  // }
                } catch (Exception e) {
                  System.out.println(e);
                }
                // serialPort_test.closePort();

                // if (response.indexOf("PTB220") >= 0)
                if (SerialPortResponseMatcher.contains(response, "PTB220")) {
                  info =
                      "[BAROMETER] found PTB220 on port: "
                          + serial_ports_portid_array[i].getDescriptivePortName();
                  System.out.print("--- ");
                  main.log_turbowin_system_message(info);
                  System.out.println("");

                  serialPort_test.closePort();
                  main.defaultPort = serial_ports_portid_array[i].getSystemPortName();
                  main.defaultPort_descriptive =
                      serial_ports_portid_array[i].getDescriptivePortName();
                  break;
                }
                // else if (response.indexOf("PTB330") >= 0)
                else if (SerialPortResponseMatcher.contains(response, "PTB330")) {
                  // NB after command "?" a BTB330 barometer will always return 4800 bit rate (or
                  // bit rate set for user port)
                  //    despite the PTB330 was opened via its service port (19200 bit rate)

                  info =
                      "[BAROMETER] found PTB330 on port: "
                          + serial_ports_portid_array[i].getDescriptivePortName();
                  System.out.print("--- ");
                  main.log_turbowin_system_message(info);
                  System.out.println("");

                  serialPort_test.closePort();
                  main.defaultPort = serial_ports_portid_array[i].getSystemPortName();
                  main.defaultPort_descriptive =
                      serial_ports_portid_array[i].getDescriptivePortName();
                  // main.defaultPort = serial_ports_portid_array[i];
                  break;
                } else {
                  info = "[BAROMETER] no barometer found";
                  System.out.println("--- " + info);
                  System.out.println("");
                  serialPort_test.closePort();
                }
              } // else (written_bytes != 0)
            } // if (main.RS232_connection_mode == 1 || main.RS232_connection_mode == 2)
            else if (main.RS232_connection_mode == 4
                || main.RS232_connection_mode == 5
                || main.RS232_connection_mode
                    == 7) // Mintaka Duo or MintakaStar USB or Mintaka StarX USB
            {
              // send data to the barometer
              //
              int written_bytes = 0;
              System.out.println(
                  "[BAROMETER] Writing \""
                      + "asq"
                      + "\" to "
                      + serial_ports_portid_array[i].getDescriptivePortName());
              String messageString_stop = "asq\r";
              // String messageString_stop = "asq\r\n";
              // serialPort_test.writeBytes(messageString_stop.getBytes());                     //
              // Write data to port
              byte[] bytes_message_stop =
                  messageString_stop.getBytes(StandardCharsets.UTF_8); // Java 7+ only
              serialPort_test.writeBytes(
                  bytes_message_stop, bytes_message_stop.length); // Write data to port
              written_bytes =
                  serialPort_test.writeBytes(
                      bytes_message_stop,
                      bytes_message_stop.length); // determine tthe number of actually written bytes

              try {
                Thread.sleep(2000);
              } catch (InterruptedException ex) {
              }

              if (written_bytes == 0) {
                System.out.println("");
                info =
                    "[BAROMETER] no barometer found ( "
                        + serial_ports_portid_array[i].getDescriptivePortName()
                        + " timed out)"; // for console window (all messages wil be displayed)
                System.out.println("--- " + info);
                System.out.println("");
                serialPort_test.closePort();
                info = "[BAROMETER] no barometer found"; // for system log (only last recorded info
                // will be written to the log)
              } else // (written_bytes != 0)
              {
                System.out.println(
                    "[BAROMETER] Writing \""
                        + "dm ma"
                        + "\" to "
                        + serial_ports_portid_array[i].getDescriptivePortName());
                String messageString_info = "dm ma\r";
                // serialPort_test.writeBytes(messageString_info.getBytes());                     //
                // Write data to port
                byte[] bytes_message_info =
                    messageString_info.getBytes(StandardCharsets.UTF_8); // Java 7+ only
                serialPort_test.writeBytes(
                    bytes_message_info, bytes_message_info.length); // Write data to port

                try {
                  Thread.sleep(2000); // 1000 seems too short on a few systems
                } catch (InterruptedException ex) {
                }

                // the response
                //
                // String response = serialPort_test.readString();
                // System.out.println(response);
                String response = "";
                serialPort_test.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
                try {
                  // while (true)
                  // {
                  byte[] readBuffer = new byte[1024];
                  // int numRead = serialPort_test.readBytes(readBuffer, readBuffer.length);
                  serialPort_test.readBytes(readBuffer, readBuffer.length);
                  // System.out.println("Read " + numRead + " bytes.");
                  response = new String(readBuffer, StandardCharsets.UTF_8);
                  System.out.println(response);
                  // }
                } catch (Exception e) {
                  System.out.println(e);
                }

                if (SerialPortResponseMatcher.contains(response, "MintakaDuo")) {
                  info =
                      "[BAROMETER] found Mintaka Duo on port: "
                          + serial_ports_portid_array[i].getDescriptivePortName();
                  System.out.print("--- ");
                  main.log_turbowin_system_message(info);
                  System.out.println("");

                  serialPort_test.closePort();
                  main.defaultPort = serial_ports_portid_array[i].getSystemPortName();
                  main.defaultPort_descriptive =
                      serial_ports_portid_array[i].getDescriptivePortName();
                  // main.defaultPort = serial_ports_portid_array[i];
                  break;
                } else if (SerialPortResponseMatcher.contains(response, "Mintaka Star")) {
                  info =
                      "[BAROMETER] found Mintaka Star on port: "
                          + serial_ports_portid_array[i].getDescriptivePortName();
                  System.out.print("--- ");
                  main.log_turbowin_system_message(info);
                  System.out.println("");

                  serialPort_test.closePort();
                  main.defaultPort = serial_ports_portid_array[i].getSystemPortName();
                  main.defaultPort_descriptive =
                      serial_ports_portid_array[i].getDescriptivePortName();
                  // main.defaultPort = serial_ports_portid_array[i];
                  break;
                } else {
                  info = "[BAROMETER] no barometer found";
                  System.out.println("--- " + info);
                  System.out.println("");
                  serialPort_test.closePort();
                }
              } // else (written_bytes != 0)
            } //  else if (main.RS232_connection_mode == 4 || main.RS232_connection_mode == 5)

          } // if
          // catch (SerialPortException ex)
          else {
            // System.out.println(ex);
            System.out.println(
                "[BAROMETER] couldn't open: "
                    + serial_ports_portid_array[i].getDescriptivePortName());
          }
        } // if (serial_ports_portid_array[i] != null)
      } // for (int i = 0; i < main.NUMBER_COM_PORTS; i++)

      if (main.defaultPort != null) {
        // temporary message box (barometer was found)
        final JOptionPane pane_end =
            new JOptionPane(
                info,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

        Timer timer_end =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    // checking_ports_end_dialog.setVisible(false);
                    // or maybe you'll need dialog.dispose() instead?
                    checking_ports_end_dialog.dispose();
                  }
                });
        timer_end.setRepeats(false);
        timer_end.start();
        checking_ports_end_dialog.setVisible(true);
      } else {
        // No com port with barometer attached found

        // blocking message box + logging (no barometer found); but only show message if it is the
        // last port-checking-round
        if (completed_checks_serial_ports == MAX_COMPLETED_BAROMETER_PORT_CHECKS - 1) {
          main.log_turbowin_system_message(
              info); // default info = "[BAROMETER] start up: no barometer found";
          if (main.obsolate_data_flag == false) // eg at start up of this application
          {
            // NB no blocking messagebox if in "trying to restart the RS232 listener" mode (see
            // Function: RS232_And_WiFi_init_new_sensor_data_received-check_timer()
            // [main_RS232_RS422.java]
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
        }
      } // else
    } // if (main.prefered_COM_port.equals("AUTOMATICALLY"))

    ///////// fixed COM port name or number ///////
    //
    else // fixed COM port selected
    {
      try {
        // serialPort_test = new SerialPort(main.prefered_COM_port);
        SerialPort serialPort_test =
            SerialPort.getCommPort(main.prefered_COM_port); // SerialPort serialPort_test = new
        // SerialPort(main.prefered_COM_port);
        serialPort_test.openPort();
        if (serialPort_test.isOpen()) {
          serialPort_test.closePort();

          // OK, no problems
          main.defaultPort =
              main.prefered_COM_port; // this construction is ok (prefered COM port is the same as
          // getSystemPortName())
          main.defaultPort_descriptive =
              main.prefered_COM_port; // NB this is a work around this is not the exact descriptive
          // term as obtained via the AUTOMTICALLY branch
          info = "[BAROMETER] " + main.prefered_COM_port + ", serial com port available";

          main.log_turbowin_system_message(info);

          final JOptionPane pane_begin =
              new JOptionPane(
                  info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog checking_ports_begin_dialog =
              pane_begin.createDialog(main.APPLICATION_NAME);

          Timer timer_begin =
              new Timer(
                  1000,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      checking_ports_begin_dialog.dispose();
                    }
                  });
          timer_begin.setRepeats(false);
          timer_begin.start();
          checking_ports_begin_dialog.setVisible(true);
        }
        // catch (SerialPortException ex)
        else {
          // error opening predefined (by the user)port, reset/warnings; logging and blocking
          // messagebox
          info = "[BAROMETER] " + main.prefered_COM_port + ", serial com port not available";
          main.log_turbowin_system_message(info);
          if (main.obsolate_data_flag == false) // eg at start up of this application
          {
            // NB no blocking messagebox if in "trying to restart the RS232 listener" mode (see
            // Function: RS232_And_WiFi_init_new_sensor_data_received-check_timer()
            // [main_RS232_RS422.java]
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
          main.defaultPort = null;
          main.defaultPort_descriptive = null;
          // System.out.println(info);
        } // else
      } // try
      catch (SerialPortInvalidPortException | HeadlessException e) {
        // NB with fixed COM port on Windows systems: reconnecting will give in most situations no
        // problem (same USB-COM port)
        //                        on Linux systems: big chance the USB port is changed (especially
        // if via USB-hub)
        //
        //    so on Linux with fixed COM port big chance this catch will be invoked but not on
        // Windows

        info = "[BAROMETER] " + e;
        main.log_turbowin_system_message(info);
      } // catch
    } // else (fixed COM port selected)
  }

  public void RS232_Check_Serial_Ports_8_II(int completed_checks_serial_ports_II) {
    // called from: main_RS232_RS424.RS232_initComponents_II()
    //

    //////////////////// TEST START ///////////////
    // main.prefered_COM_port_II = "AUTOMATICALLY";
    // main.bits_per_second_II   = 4800;
    // main.data_bits_II         = 7;
    // main.stop_bits_II         = 1;
    // main.parity_II            = 2;  // even

    //////////////////// TEST END ///////////////

    ///////// only used for HMP155 ////////

    // SerialPort serialPort_test                        = null;
    int teller = -1;
    // String[] serial_ports_portid_array                = new String[main.NUMBER_COM_PORTS];
    SerialPort[] serial_ports_portid_array = new SerialPort[main.NUMBER_COM_PORTS];
    String info = "[THERMOMETER] start up: no thermometer found";
    String hulp_parity = ""; // for message writing on java console

    // initialisation
    for (int i = 0; i < main.NUMBER_COM_PORTS; i++) {
      serial_ports_portid_array[i] = null;
    }

    // initialisation
    main.defaultPort_II = null;
    main.defaultPort_descriptive_II = null;

    ////////// automatically detecting COM port ///////////
    //
    if (main.prefered_COM_port_II.equals("AUTOMATICALLY")) {

      // Temporary message box, (pop-up for only a short time) automatically disappears
      //
      if (completed_checks_serial_ports_II
          == 0) // only show this temporary meassagebox at first port-checking-round
      {
        final JOptionPane pane_begin =
            new JOptionPane(
                "[THERMOMETER] checking serial com ports (this may take a while)",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_begin_dialog = pane_begin.createDialog(main.APPLICATION_NAME);

        Timer timer_begin =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    checking_ports_begin_dialog.dispose();
                  }
                });
        timer_begin.setRepeats(false);
        timer_begin.start();
        checking_ports_begin_dialog.setVisible(true);
      }

      // list all the available ports
      SerialPort[] serialPorts =
          SerialPort.getCommPorts(); // main.portList = SerialPortList.getPortNames();

      for (int portNo = 0;
          portNo < serialPorts.length;
          portNo++) // for (int i = 0; i < main.portList.length && i < main.NUMBER_COM_PORTS; i++)
      {
        // NB opening a port cannot be done in SwingWorker, so must be done here !!

        teller++;
        // SerialPort serialPort_test =
        // SerialPort.getCommPort(serialPorts[portNo].getDescriptivePortName());
        SerialPort serialPort_test =
            SerialPort.getCommPort(serialPorts[portNo].getSystemPortName());
        serialPort_test.openPort();

        if (serialPort_test.isOpen()) {
          // serial_ports_portid_array[teller] = serialPorts[portNo].getSystemPortName();
          serial_ports_portid_array[teller] = serialPorts[portNo];
          serialPort_test.closePort();
          String message =
              "[THERMOMETER] found serial port (ok): "
                  + serialPorts[portNo].getDescriptivePortName();
          main.log_turbowin_system_message(message);
        } // if (serialPort_test.isOpen())
        else {
          serial_ports_portid_array[teller] = null;
          System.out.println(
              "[THERMOMETER] couldn't open: " + serialPorts[portNo].getDescriptivePortName());
        } // else
      } // for (int portNo = 0; portNo < serialPorts.length; portNo++)

      boolean doorgaan_testing;
      for (int i = 0; i < main.NUMBER_COM_PORTS; i++) // max number ports to scan
      {
        if (serial_ports_portid_array[i]
            != null) // so ports really present (and also but not in use)
        {
          doorgaan_testing = true;

          // NB check if already allocated by 1st meteo instrument; note for safety because most
          // times already "couldn't open" because
          // already openened by first RS232 thread, but if not or this thread is slow than this
          // will be a good safety measure
          if (main.defaultPort != null) {
            // System.out.println("[THERMOMETER] main.defaultPort = " + main.defaultPort);
            // System.out.println("[THERMOMETER] serial_ports_portid_array[i].getSystemPortName() =
            // " + serial_ports_portid_array[i].getSystemPortName());

            if (main.defaultPort.contains(serial_ports_portid_array[i].getSystemPortName()))
            // if (serial_ports_portid_array[i].getDescriptivePortName().contains(main.defaultPort))
            {
              System.out.println(
                  "[THERMOMETER] "
                      + serial_ports_portid_array[i].getDescriptivePortName()
                      + " already allocated by 1st meteo instrument");
              // System.out.println("[THERMOMETER] main.defaultPort = " + main.defaultPort);
              doorgaan_testing = false;
              // NB 19-Aug-2019 tested ok
            }
          }
          // else if (main.GPS_defaultPort.equals(serial_ports_portid_array[i].getSystemPortName()))
          // {
          //    info = "[THERMOMETER] " + serial_ports_portid_array[i].getDescriptivePortName() + "
          // already allocted by GPS";
          // }

          if (doorgaan_testing) {
            // SerialPort serialPort_test =
            // SerialPort.getCommPort(serial_ports_portid_array[i]);//serialPort_test = new
            // SerialPort(serial_ports_portid_array[i]);
            SerialPort serialPort_test =
                SerialPort.getCommPort(serial_ports_portid_array[i].getSystemPortName());
            serialPort_test.openPort();
            if (serialPort_test.isOpen()) {
              serialPort_test.openPort(); // Open serial port

              if (main.parity_II == 0) {
                hulp_parity = "none";
              } else if (main.parity_II == 1) {
                hulp_parity = "odd";
              } else if (main.parity_II == 2) {
                hulp_parity = "even";
              }

              System.out.println(
                  "[THERMOMETER] trying to open with "
                      + String.valueOf(main.bits_per_second_II)
                      + " "
                      + hulp_parity
                      + " "
                      + String.valueOf(main.data_bits_II)
                      + " "
                      + String.valueOf(main.stop_bits_II)
                      + " "
                      + serial_ports_portid_array[i].getDescriptivePortName());
              serialPort_test.setComPortParameters(
                  main.bits_per_second_II, main.data_bits_II, main.stop_bits_II, main.parity_II);
              serialPort_test.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
              int timeout = 1000;
              serialPort_test.setComPortTimeouts(
                  SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,
                  timeout,
                  timeout);

              if (main.RS232_connection_mode_II == 1) // HMP 155
              {
                // .....SMODE STOP
                // System.out.println("[THERMOMETER] Writing \"" + "SMODE STOP" + "\" to " +
                // serial_ports_portid_array[i].getDescriptivePortName());
                // String messageString_smode_stop = "SMODE STOP\r";
                // byte[] bytes_message_smode_stop =
                // messageString_smode_stop.getBytes(StandardCharsets.UTF_8); // Java 7+ only
                // serialPort_test.writeBytes(bytes_message_smode_stop,
                // bytes_message_smode_stop.length);       // Write data to port
                // try
                // {
                //  Thread.sleep(2000);
                // }
                // catch (InterruptedException ex){ }

                int written_bytes = 0;
                for (int k = 0;
                    k < 5;
                    k++) // NB more times sending "s" absolutely necessary (see also HMP155 user's
                // guide; paragraph Serial Line Communication)
                {
                  // send data to the thermomter
                  //
                  System.out.println(
                      "[THERMOMETER] Writing \""
                          + "S"
                          + "\" to "
                          + serial_ports_portid_array[i].getDescriptivePortName());
                  String messageString_stop = "s\r";
                  byte[] bytes_message_stop =
                      messageString_stop.getBytes(StandardCharsets.UTF_8); // Java 7+ only
                  serialPort_test.writeBytes(
                      bytes_message_stop, bytes_message_stop.length); // Write data to port
                  written_bytes =
                      serialPort_test.writeBytes(
                          bytes_message_stop,
                          bytes_message_stop
                              .length); // determine the number of bytes actually written

                  try {
                    // Thread.sleep(2000);
                    Thread.sleep(1000);
                  } catch (InterruptedException ex) {
                  }
                } // for (int k = 0; k < 5; k++)

                if (written_bytes == 0) {
                  System.out.println("");
                  info =
                      "[THERMOMETER] no thermometer found ("
                          + serial_ports_portid_array[i].getDescriptivePortName()
                          + " timed out)"; // for console window (all messages wil be displayed)
                  System.out.println("--- " + info);
                  System.out.println("");
                  serialPort_test.closePort();
                  info = "[THERMOMETER] no thermometer found"; // for system log (only last recorded
                  // info will be written to the log)
                } else // written_bytes != 0;
                {
                  // System.out.println("[BAROMETER] Writing \"" + "VERS" + "\" to " +
                  // serialPort_test.getDescriptivePortName());
                  System.out.println(
                      "[THERMOMETER] Writing \""
                          + "VERS"
                          + "\" to "
                          + serial_ports_portid_array[i].getDescriptivePortName());
                  String messageString_info = "vers\r";
                  // serialPort_test.writeBytes(messageString_info.getBytes());
                  // // Write data to port
                  byte[] bytes_message_info =
                      messageString_info.getBytes(StandardCharsets.UTF_8); // Java 7+ only
                  serialPort_test.writeBytes(
                      bytes_message_info, bytes_message_info.length); // Write data to port

                  try {
                    Thread.sleep(2000); // 1000 seems too short on a few systems
                  } catch (InterruptedException ex) {
                  }

                  // the response
                  //
                  // String response = serialPort_test.readString();
                  // System.out.println(response);

                  String response = "";
                  serialPort_test.setComPortTimeouts(
                      SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
                  try {
                    byte[] readBuffer = new byte[64];
                    serialPort_test.readBytes(readBuffer, readBuffer.length);
                    response = new String(readBuffer, StandardCharsets.UTF_8);
                    System.out.println(response);
                  } catch (Exception e) {
                    System.out.println(e);
                    response = null;
                  }
                  // serialPort_test.closePort();

                  if (SerialPortResponseMatcher.contains(response, "HMP155")) {
                    info =
                        "[THERMOMETER] found HMP155 on port: "
                            + serial_ports_portid_array[i].getDescriptivePortName();
                    System.out.print("--- ");
                    main.log_turbowin_system_message(info);
                    System.out.println("");

                    serialPort_test.closePort();
                    main.defaultPort_II = serial_ports_portid_array[i].getSystemPortName();
                    main.defaultPort_descriptive_II =
                        serial_ports_portid_array[i].getDescriptivePortName();
                    break;
                  } else {
                    info = "[THERMOMETER] no thermometer found";
                    System.out.println("--- " + info);
                    System.out.println("");
                    serialPort_test.closePort();
                  }
                } // else (written_bytes != 0)
              } // if (main.RS232_connection_mode_II == 1)
            } // if (serialPort_test.isOpen())
            // catch (SerialPortException ex)
            else {
              // System.out.println(ex);
              System.out.println(
                  "[THERMOMETER] couldn't open: "
                      + serial_ports_portid_array[i].getDescriptivePortName());
            }
          } // if (doorgaan_testing)
        } // if (serial_ports_portid_array[i] != null)
      } // for (int i = 0; i < main.NUMBER_COM_PORTS; i++)

      if (main.defaultPort_II != null) {
        // temporary message box (thermometer was found)
        final JOptionPane pane_end =
            new JOptionPane(
                info,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

        Timer timer_end =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    // checking_ports_end_dialog.setVisible(false);
                    // or maybe you'll need dialog.dispose() instead?
                    checking_ports_end_dialog.dispose();
                  }
                });
        timer_end.setRepeats(false);
        timer_end.start();
        checking_ports_end_dialog.setVisible(true);
      } else {
        // No com port with thermometer attached found

        // blocking message box + logging (no barometer found); but only show message if it is the
        // last port-checking-round
        if (completed_checks_serial_ports_II == MAX_COMPLETED_THERMOMETER_PORT_CHECKS - 1) {
          main.log_turbowin_system_message(
              info); // default info = "[THERMOMETER] start up: no thermometer found";
          if (main.obsolate_data_flag_II == false) // eg at start up of this application
          {
            // NB no blocking messagebox if in "trying to restart the RS232 listener" mode (see
            // Function: RS232_And_WiFi_init_new_sensor_data_received-check_timer()
            // [main_RS232_RS422.java]
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
        }
      } // else
    } // if (main.prefered_COM_port.equals("AUTOMATICALLY"))

    ///////// fixed COM port name or number ///////
    //
    else // fixed COM port selected
    {
      try {
        // serialPort_test = new SerialPort(main.prefered_COM_port);
        SerialPort serialPort_test =
            SerialPort.getCommPort(main.prefered_COM_port_II); // SerialPort serialPort_test = new
        // SerialPort(main.prefered_COM_port);
        serialPort_test.openPort();
        if (serialPort_test.isOpen()) {
          serialPort_test.closePort();

          // OK, no problems
          main.defaultPort_II =
              main.prefered_COM_port_II; // this construction is ok (prefered COM port is the same
          // as getSystemPortName())
          main.defaultPort_descriptive_II =
              main.prefered_COM_port_II; // NB this is a work around this is not the exact
          // descriptive term as obtained via the AUTOMTICALLY branch
          info = "[THERMOMETER] " + main.prefered_COM_port_II + ", serial com port available";

          main.log_turbowin_system_message(info);

          final JOptionPane pane_begin =
              new JOptionPane(
                  info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog checking_ports_begin_dialog =
              pane_begin.createDialog(main.APPLICATION_NAME);

          Timer timer_begin =
              new Timer(
                  1000,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      checking_ports_begin_dialog.dispose();
                    }
                  });
          timer_begin.setRepeats(false);
          timer_begin.start();
          checking_ports_begin_dialog.setVisible(true);
        }
        // catch (SerialPortException ex)
        else {
          // error opening predefined (by the user)port, reset/warnings; logging and blocking
          // messagebox
          info = "[THERMOMETER] " + main.prefered_COM_port_II + ", serial com port not available";
          main.log_turbowin_system_message(info);
          if (main.obsolate_data_flag_II == false) // eg at start up of this application
          {
            // NB no blocking messagebox if in "trying to restart the RS232 listener" mode (see
            // Function: RS232_And_WiFi_init_new_sensor_data_received-check_timer()
            // [main_RS232_RS422.java]
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
          main.defaultPort_II = null;
          main.defaultPort_descriptive_II = null;
          // System.out.println(info);
        } // else
      } // try
      catch (SerialPortInvalidPortException | HeadlessException e) {
        // NB with fixed COM port on Windows systems: reconnecting will give in most situations no
        // problem (same USB-COM port)
        //                        on Linux systems: big chance the USB port is changed (especially
        // if via USB-hub)
        //
        //    so on Linux with fixed COM port big chance this catch will be invoked but not on
        // Windows

        info = "[THERMOMETER] " + e;
        main.log_turbowin_system_message(info);
      } // catch
    } // else (fixed COM port selected)
  }

  public main_RS232_RS422() {}

  public void RS232_Delete_Sensor_Data_Files_II() {
    new Rs232DeleteSensorDataFilesIiWorkflow().start();
  }

  public void RS232_Delete_Sensor_Data_Files() {
    new Rs232DeleteSensorDataFilesWorkflow().start();
  }

  public void RS232_write_sensor_data_to_file_II() {
    // called from: class RS232_Class_Receive_Sensor_Data_II() [main_RS232_RS422.java]
    //
    //
    // for: HMP155

    // NB PTB220:
    // er wordt uiteindelijk geschreven bv:
    // $3  996.87 ****.** ****.** 996.87 0   3$
    //
    // maar eigelijk worden er bv 3 stukken string geschreven!!!! bv:
    // $3  996.87 ***
    // *.** ****.**
    // 996.87 0   3 #r #n$
    //
    // NB Dit is vrij normaal voor een serieele aansluiting 14 bytes per keer ingelezen
    //    "De veel gebruikte UART chip type 16550 bevat een buffer van 14 bytes voor ontvangen en
    //    16 bytes voor zenden, zodat de CPU van de PC niet voor iedere byte aandacht aan de poort
    // hoeft te besteden."
    //    (http://nl.wikipedia.org/wiki/Seri%C3%ABle_poort)
    // NB via laptop virtuele seriele poort (USB) lijkt dat er veel minder bytes per keer wordt
    // ingelezen!
    //
    //
    // NB geen Swingworker hier gebruiken (worden dan alleen stukken geschreven)
    //

    // BufferedWriter out                                        = null;
    String file_naam = "sensor_data_II_" + main.sdf3.format(new Date()) + ".txt";
    String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + file_naam;
    SensorDataFileWriter.append(
        main.logs_dir,
        volledig_path_sensor_data,
        main.total_string_II,
        "",
        () -> main.sdf4.format(new Date()),
        () -> test_record_II = main.sdf_tsl_2.format(new Date()) + " UTC",
        "[THERMOMETER] ");
  }

  public void RS232_write_sensor_data_to_file() {
    // called from: class RS232_Class_Receive_Sensor_Data() [main_RS232_RS422.java]
    //              class WiFi_Class_Receive_UDP() [main_RS232_RS422.java]
    //
    //
    // for: PTB220, PTB330, Mintaka Duo, Mintaka Star USB, Mintaka Star WiFi, Mintaka StarX USB,
    // Mintaka StarX WiFi ///////

    // NB PTB220:
    // er wordt uiteindelijk geschreven bv:
    // $3  996.87 ****.** ****.** 996.87 0   3$
    //
    // maar eigelijk worden er bv 3 stukken string geschreven!!!! bv:
    // $3  996.87 ***
    // *.** ****.**
    // 996.87 0   3 #r #n$
    //
    // NB Dit is vrij normaal voor een serieele aansluiting 14 bytes per keer ingelezen
    //    "De veel gebruikte UART chip type 16550 bevat een buffer van 14 bytes voor ontvangen en
    //    16 bytes voor zenden, zodat de CPU van de PC niet voor iedere byte aandacht aan de poort
    // hoeft te besteden."
    //    (http://nl.wikipedia.org/wiki/Seri%C3%ABle_poort)
    // NB via laptop virtuele seriele poort (USB) lijkt dat er veel minder bytes per keer wordt
    // ingelezen!
    //
    // NB PTB330 via service port alleen een luchtdruk bv 1020.33
    //    PTB330 via user port kan verschillend zijn?? (maar er is wel een default??)
    //
    //
    // NB geen Swingworker hier gebruiken (worden dan alleen stukken geschreven)
    //

    // boolean total_string_ok                                   = false;

    String file_naam = "sensor_data_" + main.sdf3.format(new Date()) + ".txt";
    String volledig_path_sensor_data = main.logs_dir + java.io.File.separator + file_naam;
    SensorDataFileWriter.append(
        main.logs_dir,
        volledig_path_sensor_data,
        main.total_string,
        "",
        () -> main.sdf4.format(new Date()),
        () -> test_record = main.sdf_tsl_2.format(new Date()) + " UTC",
        "[barometer] ");
  }

  public static void RS232_read_dasboard_values() {
    Rs232DashboardValuesWorkflow.start();
  }

  static void RS232_compute_dasboard_values(
      final String date_time_last_update, final String last_update_record) {
    Rs232DashboardValuesComputationWorkflow.start(date_time_last_update, last_update_record);
  }

  // private static String Mintaka_Star_Checksum(String record
  public static String Mintaka_Star_Checksum(String record) {
    // called from: - RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs()
    // [main_RS232_RS422.java]
    //              - RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
    // [main_RS232_RS422.java]
    //              - RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs()
    // [main_RS232_RS422.java]
    //              - RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs()
    // [main_RS232_RS422.java]
    //              - RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs()
    // [main_RS232_RS422.java]
    //              - RS232_compute_dasboard_values()[main_RS232_RS422.java]
    //              - Read_Sensor_Data_Files_For_Air_Temp_Mintaka_StarX() [RS232_view.java]
    //              -
    // Read_Sensor_Data_Files_For_Barograph_Mintaka_Duo_Or_Mintaka_Star_Or_StarX()[RS232_view.java]
    //
    // NB used for string data checking of the Mintaka Star USB/Wifi and the Mintaka StarX USB/Wifi

    String computed_checksum = "";
    int checksum = 0;

    // cheksum computed between first pos and '*'
    int start_pos = 0;
    int end_pos = record.indexOf('*');

    // cheksum ok?
    //
    if (start_pos != -1 && end_pos != -1) {
      for (int i = start_pos; i < end_pos; i++) {
        checksum = checksum ^ record.charAt(i);
      }

      computed_checksum = Integer.toHexString(checksum);
      if (computed_checksum.length() == 1) {
        computed_checksum = "0" + computed_checksum;
      }
    } // if (start_pos != -1 && end_pos != -1)

    return computed_checksum = computed_checksum.toUpperCase();
  }

  public void RS422_write_sensor_data_to_file() {
    ///// EUCAWS ONLY //////

    // called from class RS422_V1_Class_Receive_Sensor_Data
    //

    // NB PTB220:
    // er wordt uiteindelijk geschreven bv:
    // $3  996.87 ****.** ****.** 996.87 0   3$
    //
    // maar eigelijk worden er bv 3 stukken string geschreven!!!! bv:
    // $3  996.87 ***
    // *.** ****.**
    // 996.87 0   3 #r #n$
    //
    // NB Dit is vrij normaal voor een serieele aansluiting 14 bytes per keer ingelezen
    //    "De veel gebruikte UART chip type 16550 bevat een buffer van 14 bytes voor ontvangen en
    //    16 bytes voor zenden, zodat de CPU van de PC niet voor iedere byte aandacht aan de poort
    // hoeft te besteden."
    //    (http://nl.wikipedia.org/wiki/Seri%C3%ABle_poort)
    // NB via laptop virtuele seriele poort (USB) lijkt dat er veel minder bytes per keer wordt
    // ingelezen!
    //
    // NB PTB330 via service port alleen een luchtdruk bv 1020.33
    //    PTB330 via user port kan verschillend zijn?? (maar er is wel een default??)
    //
    //
    // NB geen Swingworker hier gebruiken (worden dan alleen stukken geschreven)
    //

    // if time is 57, 58 or 59 sec round to next whole minute
    //
    // to prevent such situations:
    //
    // $PEUMB,130815,121900,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815121859
    // $PEUMB,130815,122000,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815121959
    // $PEUMB,130815,122100,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122100
    // $PEUMB,130815,122200,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122200
    // $PEUMB,130815,122300,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122259
    // $PEUMB,130815,122400,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122400
    // $PEUMB,130815,122500,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122459
    // $PEUMB,130815,122600,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122559
    // $PEUMB,130815,122700,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122700
    // $PEUMB,130815,122800,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122800
    // $PEUMB,130815,122900,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122900
    // $PEUMB,130815,123000,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123000
    // $PEUMB,130815,123100,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123100
    // $PEUMB,130815,123200,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123200
    // $PEUMB,130815,123300,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123300
    // $PEUMB,130815,123400,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123359
    //
    // NB seconds display at the end of every record only during testing  (eg 20130815122900,
    // operational this will be 01308151229)

    cal_sensor_datum_tijd = new GregorianCalendar();
    SensorDataTimestamp.roundToNextMinuteWhenLate(cal_sensor_datum_tijd);

    // String file_naam = "sensor_data_" + main.sdf3.format(new Date()) + ".txt";
    String file_naam = "sensor_data_" + main.sdf3.format(cal_sensor_datum_tijd.getTime()) + ".txt";

    // String volledig_path_sensor_data = logs_dir + java.io.File.separator + SENSOR_DATA_SUB_DIR +
    // java.io.File.separator + file_naam;
    String volledig_path_sensor_data =
        main.logs_dir + java.io.File.separator + java.io.File.separator + file_naam;

    // A complete AWS record updates test_record for the Test action on the instrument settings
    // page and appends the cal_sensor_datum_tijd minute as a UTC sdf4 timestamp; fragments are
    // written without a timestamp until the complete record arrives.
    // A complete record can also be followed by RS422_Read_AWS_Sensor_Data() to disable input for
    // parameters already present in the saved sensor data.
    SensorDataFileWriter.append(
        main.logs_dir,
        volledig_path_sensor_data,
        main.total_string,
        ",", // AWS records are saved with comma-separated fields.
        () -> main.sdf4.format(cal_sensor_datum_tijd.getTime()),
        () -> test_record = main.sdf_tsl_2.format(new Date()) + " UTC",
        "[AWS] ");
  }

  public void RS422_V3_write_sensor_data_to_file() {
    ///// EUCAWS serial and OMC-140 serial and OMC-140 LAN  //////

    // called from - class RS422_V3_Class_Receive_Sensor_Data
    //             - class Ethernet_Class_Receive_UDP

    // NB PTB220:
    // er wordt uiteindelijk geschreven bv:
    // $3  996.87 ****.** ****.** 996.87 0   3$
    //
    // maar eigelijk worden er bv 3 stukken string geschreven!!!! bv:
    // $3  996.87 ***
    // *.** ****.**
    // 996.87 0   3 #r #n$
    //
    // NB Dit is vrij normaal voor een serieele aansluiting 14 bytes per keer ingelezen
    //    "De veel gebruikte UART chip type 16550 bevat een buffer van 14 bytes voor ontvangen en
    //    16 bytes voor zenden, zodat de CPU van de PC niet voor iedere byte aandacht aan de poort
    // hoeft te besteden."
    //    (http://nl.wikipedia.org/wiki/Seri%C3%ABle_poort)
    // NB via laptop virtuele seriele poort (USB) lijkt dat er veel minder bytes per keer wordt
    // ingelezen!
    //
    // NB PTB330 via service port alleen een luchtdruk bv 1020.33
    //    PTB330 via user port kan verschillend zijn?? (maar er is wel een default??)
    //
    //
    // NB geen Swingworker hier gebruiken (worden dan alleen stukken geschreven)
    //

    // BufferedWriter out         = null;
    boolean doorgaan = false;
    boolean peumb_update = false;
    boolean retry = false;

    // if time is 57, 58 or 59 sec round to next whole minute
    //
    // to prevent such situations:
    //
    // $PEUMB,130815,121900,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815121859
    // $PEUMB,130815,122000,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815121959
    // $PEUMB,130815,122100,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122100
    // $PEUMB,130815,122200,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122200
    // $PEUMB,130815,122300,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122259
    // $PEUMB,130815,122400,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122400
    // $PEUMB,130815,122500,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122459
    // $PEUMB,130815,122600,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122559
    // $PEUMB,130815,122700,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122700
    // $PEUMB,130815,122800,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122800
    // $PEUMB,130815,122900,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815122900
    // $PEUMB,130815,123000,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123000
    // $PEUMB,130815,123100,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123100
    // $PEUMB,130815,123200,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123200
    // $PEUMB,130815,123300,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123300
    // $PEUMB,130815,123400,...........................4.2,350.3,7.8,358.3,23.1,28.3,-38,,,,20130815123359
    //
    // NB seconds display at the end of every record only during testing  (eg 20130815122900,
    // operational this will be 01308151229)

    cal_sensor_datum_tijd = new GregorianCalendar();
    SensorDataTimestamp.roundToNextMinuteWhenLate(cal_sensor_datum_tijd);

    // String file_naam = "sensor_data_" + main.sdf3.format(new Date()) + ".txt";
    String file_naam = "sensor_data_" + main.sdf3.format(cal_sensor_datum_tijd.getTime()) + ".txt";

    // String volledig_path_sensor_data = logs_dir + java.io.File.separator + SENSOR_DATA_SUB_DIR +
    // java.io.File.separator + file_naam;
    String volledig_path_sensor_data =
        main.logs_dir + java.io.File.separator + java.io.File.separator + file_naam;

    if ((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0)) {
      // NB in Function RS422_initComponents() there was already checked on dir logs_dir
      doorgaan = true;
    }

    if (doorgaan) {
      try (BufferedWriter out =
          new BufferedWriter(new FileWriter(volledig_path_sensor_data, true))) {
        for (int b = 0; b < main.total_string.length(); b++) {
          if (main.total_string.charAt(b) == '\r' || main.total_string.charAt(b) == '\n') {
            // if (message_peumb.toString().contains("$PEUMB"))

            // NB in case of OMC-140 LAN:
            //
            // UdPbC\0\s:D263G4,s:WI4263*31\$PEUMB,20180411,060028,52.5,4.9,257.2,4.0,259.2,1006.6,991.5,0.7,4,19.6,28.9,3.9,24.4,131.8,28.6,139.9,34.6,73.3,,,,,,*57
            //    stared as:
            // $PEUMB,20180411,060028,52.5,4.9,257.2,4.0,259.2,1006.6,991.5,0.7,4,19.6,28.9,3.9,24.4,131.8,28.6,139.9,34.6,73.3,,,,,,*57
            //
            // but will have no effect on OMC-140 serial and EUCAWS serial
            //
            String peumbRecord = PeumbRecordExtractor.extract(message_peumb.toString());
            if (peumbRecord != null) {
              // test_record: for dispaying on main screen date/ime of this last received data
              // update
              test_record =
                  main.sdf_tsl_2.format(new Date()) + " UTC"; // // new Date() -> always in UTC

              out.write(peumbRecord);
              out.write(
                  ","); // NB "," : typically for AWS connection (record data separated by commas)

              out.write(main.sdf4.format(cal_sensor_datum_tijd.getTime()));
              out.newLine();

              peumb_update = true;
            }
            message_peumb.setLength(0);
          } else {
            message_peumb.append(main.total_string.charAt(b));
          }
        } // for (int b = 0; b < main.total_string.length(); b++)

        // NB Alleen onderstaande zou al voldoende zijn als er geen aparte datum/tijd toegevoegd zou
        // hoeven te worden
        // out.write(main.total_string);

      } // try
      catch (IOException ex) {
        String info = ex + "; unable to write to: " + volledig_path_sensor_data;
        main.log_turbowin_system_message("[AWS] " + info);
      }
    } // if (doorgaan)

    if (peumb_update) {
      // read the AWS data which was saved before by TurboWin+ from a sensor data file
      //
      // NB DIT ZOU BETER KUNNEN DOOR GELIJK UIT DE message_peumb string te lezen !!!!!!!
      //
      RS422_Read_AWS_Sensor_Data_For_Display();

      // update main screen with application name + mode and date time last received data (single
      // info line botton screen)
      String info = "";
      if ((main_RS232_RS422.test_record.equals(""))
          || (main_RS232_RS422.test_record
              == null)) // test_rcord was set in RS422_write_sensor_data_to_file()
      {
        info = "no data";
      } else {
        info = main_RS232_RS422.test_record;
      }
      main.jLabel4.setText(
          main.APPLICATION_NAME
              + " "
              + main.application_mode
              + ", last received AWS data: "
              + info);

      // for checking that the displayed data is not obsolete (i.e. not updated last 5 minutes)
      last_new_data_received_TimeMillis = System.currentTimeMillis(); // see also Function:
      // RS422_init_new_aws_data_received_check_timer()
      // [main_RS232_422.java]

      // AWSR (Automatic Weather Station Reporting)
      if (main.AWSR == true) {
        cal_AWSR_system_date_time =
            new GregorianCalendar(
                new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
        cal_AWSR_system_date_time.getTime(); // now effective
        int AWSR_system_hour_local =
            cal_AWSR_system_date_time.get(
                Calendar.HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at
        // 10:04:15.250 PM the HOUR_OF_DAY is 22.
        int AWSR_system_minute_local = cal_AWSR_system_date_time.get(Calendar.MINUTE);
        int AWSR_reporting_interval = Integer.valueOf(main.AWSR_reporting_interval);

        if (AwsrScheduleDecision.isScheduledUpload(
            AWSR_system_hour_local, AWSR_system_minute_local, AWSR_reporting_interval)) {
          retry = false;

          // logging
          String message = "[AWSR] scheduled upload";
          main.log_turbowin_system_message(message);

          // checking
          boolean AWSR_settings_ok = RS422_check_AWSR_settings();
          boolean GPS_date_time_ok = true;

          GPS_date_time_ok = true;
          // NB in function RS422_Read_And_Send_Sensor_Data_For_AWSR() the date time of the last
          // saved record will be checked, see below

          if (AWSR_settings_ok
              && GPS_date_time_ok) // GPS date-time was compared with system date-time
          {
            // NB in this stage we are sure the date-time is correct! + we are sure position is
            // available!

            // read the AWS data which was saved before by TurboWin+ from a sensor data file
            RS422_Read_And_Send_Sensor_Data_For_AWSR(
                retry); // nB in this function also the date-time of the last saved record will be
            // checked
          } // if (AWSR_settings_ok && GPS_date_time_ok)
        } // if ((AWSR_system_minute_local == 0) etc.
        else if (AwsrScheduleDecision.isRetryUpload(
            AWSR_server_response_code,
            main.RESPONSE_NO_INTERNET,
            AWSR_system_hour_local,
            AWSR_system_minute_local,
            AWSR_reporting_interval,
            AWSR_RETRY_MINUTES)) {
          retry = true;

          // logging
          String message = "[AWSR] retry scheduled upload";
          main.log_turbowin_system_message(message);

          // checking
          boolean AWSR_settings_ok = RS422_check_AWSR_settings();
          boolean GPS_date_time_ok = true;

          // NB Mintaka StarX saved data do not contain date/time information so it is not possible
          // to check this
          //    so it is made always true
          GPS_date_time_ok = true;

          if (AWSR_settings_ok
              && GPS_date_time_ok) // GPS date-time was compared with system date-time
          {
            // NB in this stage we are sure the date-time is correct! + we are sure position is
            // available!
            RS422_Read_And_Send_Sensor_Data_For_AWSR(retry);
          } // if (APR_settings_ok && GPS_date_time_ok)

          // reset (to be sure)
          AWSR_server_response_code = 0;

        } // else if ((APR_server_response_code = main.RESPONSE_NO_INTERNET) etc.
        else if (AwsrScheduleDecision.isRetryPending(
            AWSR_server_response_code,
            main.RESPONSE_NO_INTERNET,
            AWSR_system_hour_local,
            AWSR_system_minute_local,
            AWSR_reporting_interval,
            AWSR_RETRY_MINUTES)) {
          // text will be visible only a few minutes (till the retry upload)
          main.jTextField4.setText("[AWSR] retry AWS report upload within a few minutes");
        } else {
          // text with next AWSR update bottom status line TurboWin+ main screen (updated every
          // minute)
          Integer nextUploadHour =
              AwsrNextUploadCalculator.nextUploadHour(
                  AWSR_system_hour_local, AWSR_reporting_interval);
          if (nextUploadHour != null) {
            cal_AWSR_system_date_time.add(
                Calendar.HOUR_OF_DAY, nextUploadHour - AWSR_system_hour_local);
            cal_AWSR_system_date_time.getTime();

            String next_AWSR_string =
                "[AWSR] next automated AWS report upload: "
                    + cal_AWSR_system_date_time.get(Calendar.DAY_OF_MONTH)
                    + " "
                    + main.convert_month(cal_AWSR_system_date_time.get(Calendar.MONTH))
                    + " "
                    + cal_AWSR_system_date_time.get(Calendar.YEAR)
                    + " "
                    + cal_AWSR_system_date_time.get(Calendar.HOUR_OF_DAY)
                    + ".00 UTC";

            main.jTextField4.setText(next_AWSR_string);
          }
        } // else

        cal_AWSR_system_date_time = null;
      } // if (main.AWSR == true)
    } // if (peumb_update)
  }

  public void RS232_Format_Thermometer_Output() {
    SerialPort serialPort_form = null;
    String hulp_parity = ""; // only for message writing in java console

    if (main.defaultPort_II == null) {
      String info =
          "[THERMOMETER] selected serial port [function: RS232_Format_Thermometer_Output()] "
              + main.defaultPort_descriptive_II
              + " not found";
      System.out.println(info);
      // JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error",
      // JOptionPane.WARNING_MESSAGE);
    }

    if (main.defaultPort_II != null) {
      serialPort_form =
          SerialPort.getCommPort(
              main.defaultPort_II); // serialPort_form = new SerialPort(main.defaultPort);
      serialPort_form.openPort();
      if (serialPort_form.isOpen()) {
        hulp_parity = SerialPortConfigurationWorkflow.parityDescription(main.parity_II);

        System.out.println(
            "[THERMOMETER] opening with "
                + String.valueOf(main.bits_per_second_II)
                + " "
                + hulp_parity
                + " "
                + String.valueOf(main.data_bits_II)
                + " "
                + String.valueOf(main.stop_bits_II)
                + " "
                + main.defaultPort_descriptive_II);
        SerialPortConfigurationWorkflow.configure(
            serialPort_form,
            main.bits_per_second_II,
            main.data_bits_II,
            main.stop_bits_II,
            main.parity_II);

        // STOP
        for (int k = 0;
            k < 5;
            k++) // NB more times sending "s" absolutely necessary (see also HMP155 user's guide;
        // paragraph Serial Line Communication)
        {
          System.out.println(
              "[THERMOMETER] Writing \"" + "S" + "\" to " + main.defaultPort_descriptive_II);
          String messageString_stop = "s\r";
          SerialPortCommandWorkflow.write(serialPort_form, messageString_stop);

          try {
            // Thread.sleep(2000);       // gaat eigenlijk ook altijd goed bij 1000
            Thread.sleep(1000);
          } catch (InterruptedException ex) {
            System.out.println(
                "[THERMOMETER] InterruptedException [function: RS232_Format_Thermometer_Output()] "
                    + ex);
          }
        } // for (int k = 0; k < 5; k++)

        // ECHO OFF
        System.out.println(
            "[THERMOMETER] Writing \"" + "ECHO OFF" + "\" to " + main.defaultPort_descriptive_II);
        String messageString_echo = "ECHO OFF\r";
        SerialPortCommandWorkflow.write(serialPort_form, messageString_echo);

        // FORM
        System.out.println(
            "[THERMOMETER] Writing \""
                + "form 6.1 Ta \" \" 6.1 Tw \" \" 6.1 Td \" \" 6.1 RH \" \" #r #n"
                + "\" to "
                + main.defaultPort_descriptive_II);
        String messageString_format =
            "form 6.1 ta \" \" 6.1 tw \" \" 6.1 td \" \" 6.1 rh \" \" #r #n\r";
        SerialPortCommandWorkflow.write(serialPort_form, messageString_format);

        // NB INTV 60 s niet voor het form commando uitvoeren!!!!!!!
        System.out.println(
            "[THERMOMETER] Writing \"" + "INTV 60 s" + "\" to " + main.defaultPort_descriptive_II);
        String messageString_int = "intv 60 s\r";
        SerialPortCommandWorkflow.write(serialPort_form, messageString_int);

        // UNIT m (metric)
        System.out.println(
            "[THERMOMETER] Writing \"" + "unit m" + "\" to " + main.defaultPort_descriptive_II);
        String messageString_unit = "unit m\r";
        SerialPortCommandWorkflow.write(serialPort_form, messageString_unit);

        // RUN
        System.out.println(
            "[THERMOMETER] Writing \"" + "R" + "\" to " + main.defaultPort_descriptive_II);
        String messageString_cont_R = "r\r";
        SerialPortCommandWorkflow.write(serialPort_form, messageString_cont_R);

        try {
          Thread.sleep(1000); // gaat eigenlijk ook altijd goed bij 1000
        } catch (InterruptedException ex) {
          System.out.println(
              "[THERMOMETER] InterruptedException [function: RS232_Format_Thermometer_Output()] "
                  + ex);
        }

        // close the serial port
        serialPort_form.closePort();

      } // if (serialPort_form.isOpen())
      else {
        System.out.println(
            "[THERMOMETER] Couldn't open (for formatting) serial port: "
                + main.defaultPort_descriptive_II);
      }
    } // if (main.defaultPort_II != null)
  }

  public void RS232_Format_Barometer_Output_3() {

    // NB Indien Vaisala barometer in POLL mode dan moeilijk deze hier uit te halen (zie manual
    // aantekeningen)
    //    (open 0 <cr> -> smode run <cr> -> reset <cr>)
    //    Dit moet dan met putty of hyperterminal o.i.d. (in principe zal een barometer nooit in
    // poll mode staan)

    SerialPort serialPort_form = null;
    String hulp_parity = ""; // only for message writing in java console

    if (main.defaultPort == null) {
      String info =
          "[BAROMETER] selected serial port [function: Format_Barometer_Output_3()] "
              + main.defaultPort_descriptive
              + " not found";
      System.out.println(info);
      // JOptionPane.showMessageDialog(null, info, APPLICATION_NAME + " error",
      // JOptionPane.WARNING_MESSAGE);
    }

    if (main.defaultPort != null) {
      serialPort_form =
          SerialPort.getCommPort(
              main.defaultPort); // serialPort_form = new SerialPort(main.defaultPort);
      serialPort_form.openPort();
      if (serialPort_form.isOpen()) {
        hulp_parity = SerialPortConfigurationWorkflow.parityDescription(main.parity);

        System.out.println(
            "[BAROMETER] opening with "
                + String.valueOf(main.bits_per_second)
                + " "
                + hulp_parity
                + " "
                + String.valueOf(main.data_bits)
                + " "
                + String.valueOf(main.stop_bits)
                + " "
                + main.defaultPort_descriptive);
        SerialPortConfigurationWorkflow.configure(
            serialPort_form, main.bits_per_second, main.data_bits, main.stop_bits, main.parity);

        if (main.RS232_connection_mode == 1 || main.RS232_connection_mode == 2) // PTB220 or PRB330
        {
          // System.out.println("[BAROMETER] Writing \"" + "S" + "\" to " +
          // serialPort_form.getDescriptivePortName());
          System.out.println(
              "[BAROMETER] Writing \"" + "S" + "\" to " + main.defaultPort_descriptive);
          String messageString_stop = "S\r";
          SerialPortCommandWorkflow.write(serialPort_form, messageString_stop);

          // NB reset niet nodig (heeft geen effect)

        } // if (main.RS232_connection_mode == 1 || main.RS232_connection_mode == 2)
        else if (main.RS232_connection_mode == 4
            || main.RS232_connection_mode == 5) // Mintaka Duo or Mintaka Star USB
        {
          System.out.println(
              "[BAROMETER] Writing \"" + "asq" + "\" to " + main.defaultPort_descriptive);
          String messageString_stop = "asq\r"; // asq = terminate autosampling
          SerialPortCommandWorkflow.write(serialPort_form, messageString_stop);
        } // else if (main.RS232_connection_mode == 4 || main.RS232_connection_mode == 5)

        try {
          Thread.sleep(2000); // gaat eigenlijk ook altijd goed bij 1000
        } catch (InterruptedException ex) {
          System.out.println(
              "[BAROMETER] InterruptedException [function: RS232_Format_Barometer_Output_3()] "
                  + ex);
        }

        // flush ???

        if ((main.RS232_connection_mode == 1)
            || (main.RS232_connection_mode == 2)) // PTB220 or PTB330
        {
          System.out.println(
              "[BAROMETER] Writing \"" + "ECHO OFF" + "\" to " + main.defaultPort_descriptive);
          String messageString_echo = "ECHO OFF\r";
          SerialPortCommandWorkflow.write(serialPort_form, messageString_echo);
        }

        if (main.RS232_connection_mode == 1) // PTB220
        {
          // NB LET OP DE TE VERZENDEN FORMAT STRING NAAR PTB220 MAG NIET TE LANG ZIJN (ongeveer 50
          // char -100 bytes-)!!!!
          System.out.println(
              "[BAROMETER] Writing \""
                  + "form 4.2 P \" \" 4.2 HCP \" \" 3.1 TREND \" \" A \" \" #r #n"
                  + "\" to "
                  + main.defaultPort_descriptive);
          String messageString_format2 =
              "form 4.2 P \" \" 4.2 HCP \" \" 3.1 TREND \" \" A \" \" #r #n\r";
          // String messageString_format2 = "form P \" \" P \" \" P \" \" P \" \" P \" \" P \" \" P
          // \" \" #r #n\r";
          // String messageString_format2 = "form \"0123456789 0123456789 01234567 \" P \" \" #r
          // #n\r"; // deze gaat noog net goed
          SerialPortCommandWorkflow.write(serialPort_form, messageString_format2);
        } // if (barometer_type.equals(PTB220))
        else if (main.RS232_connection_mode == 2) // PTB330
        {
          // test voor bxpm171 - bxpm80
          // String messageString_format = "form 4.2 P \" \" 4.2 HCP \" \" 4.2 QNH \" \" 3.1 P3H \"
          // \" 1.1 A3H \" \" DIT IS EEN TEST 1234567890 1234567890 1234567890
          // wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
          // QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ X\r\n";
          // String messageString_format = "123456789";
          System.out.println(
              "[BAROMETER] Writing \""
                  + "form 4.2 P \" \" 4.2 HCP \" \" 4.2 QNH \" \" 3.1 P3H \" \" 1.1 A3H \" \" #r #n"
                  + "\" to "
                  + main.defaultPort_descriptive);
          String messageString_format =
              "form 4.2 P \" \" 4.2 HCP \" \" 4.2 QNH \" \" 3.1 P3H \" \" 1.1 A3H \" \" #r #n\r";
          SerialPortCommandWorkflow.write(serialPort_form, messageString_format);

        } //  else if (barometer_type.equals(PTB330_USER_PORT) || etc
        else if (main.RS232_connection_mode == 4) // Mintaka Duo
        {
          System.out.println(
              "[BAROMETER] Writing \""
                  + "as 60 TurboWin"
                  + "\" to "
                  + main.defaultPort_descriptive);
          String messageString_format = "as 60 TurboWin\r\n";
          // serialPort_form.writeBytes(messageString_format.getBytes());                    //
          // Write data to port
          SerialPortCommandWorkflow.write(serialPort_form, messageString_format);

        } // else if (main.RS232_connection_mode == 4)
        else if (main.RS232_connection_mode == 5) // Mintaka Star USB
        {
          System.out.println(
              "[BAROMETER] Writing \""
                  + "as 60 TurboWinG"
                  + "\" to "
                  + main.defaultPort_descriptive);
          String messageString_format = "as 60 TurboWinG\r\n"; // note: turbowing !!
          // serialPort_form.writeBytes(messageString_format.getBytes());                    //
          // Write data to port
          SerialPortCommandWorkflow.write(serialPort_form, messageString_format);

        } // else if (main.RS232_connection_mode == 5)
        else if (main.RS232_connection_mode == 7) // Mintaka Star + StarX USB
        {
          System.out.println(
              "[BAROMETER] Writing \""
                  + "as 60 TURBOWINH"
                  + "\" to "
                  + main.defaultPort_descriptive);
          String messageString_format = "as 60 TURBOWINH\r\n"; // note: turbowinH !!
          // serialPort_form.writeBytes(messageString_format.getBytes());                    //
          // Write data to port
          SerialPortCommandWorkflow.write(serialPort_form, messageString_format);

        } // else if (main.RS232_connection_mode == 7)
        else {
          System.out.println(
              "[BAROMETER] barometer type unknown [function: RS232_Format_Barometer_Output()_3]");
        }

        if ((main.RS232_connection_mode == 1)
            || (main.RS232_connection_mode == 2)) // PTB220 or PTB330
        {
          // NB INTV 60 s niet voor het form commando uitvoeren!!!!!!!
          System.out.println(
              "[BAROMETER] Writing \"" + "INTV 60 s" + "\" to " + main.defaultPort_descriptive);
          String messageString_int = "INTV 60 s\r";
          SerialPortCommandWorkflow.write(serialPort_form, messageString_int);

          // flush??
        } // if ((main.RS232_connection_mode == 1) || (main.RS232_connection_mode == 2))    //
        // PTB220 or PTB330

        if ((main.RS232_connection_mode == 1)
            || (main.RS232_connection_mode == 2)) // PTB220 or PTB330
        {
          System.out.println(
              "[BAROMETER] Writing \"" + "R" + "\" to " + main.defaultPort_descriptive);
          String messageString_cont_R = "R\r";
          SerialPortCommandWorkflow.write(serialPort_form, messageString_cont_R);

          // flush ??
        } // if ((main.RS232_connection_mode == 1) || (main.RS232_connection_mode == 2))     //
        // PTB220 or PTB330

        try {
          Thread.sleep(1000); // gaat eigenlijk ook altijd goed bij 1000
        } catch (InterruptedException ex) {
          System.out.println(
              "[BAROMETER] InterruptedException [function: Format_Barometer_Output()] " + ex);
        }

        // close the serial port
        serialPort_form.closePort();

      } // if (serialPort_form.isOpen())
      else {
        // System.out.println("[BAROMETER] Function: RS232_Format_Barometer_Output_3() " + ex);
        System.out.println(
            "[BAROMETER] Couldn't open (for formatting) serial port:  "
                + main.defaultPort_descriptive);
      }
    } // if (defaultPort != null)
  }

  private void RS422_init_new_aws_data_received_check_timer() {
    /////// NB AWS (EUCAWS and OMC-140) only (not for APR ////////
    //
    // called from: - RS422_initComponents() [main_RS232_RS422.java]
    //              - Ethernet_initComponents() [main_RS232_RS422.java]

    ActionListener check_new_data_action =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if ((System.currentTimeMillis() - last_new_data_received_TimeMillis)
                > MAX_AGE_AWS_DATA) // MAX_AGE_AWS_DATA e.g. 30000 msec (5 minutes)
            {
              // NB see function: RS422_initComponents() [main_RS232_RS422.java] for setting of var
              // "last_new_data_received_TimeMillis"

              // gray meteo parameters data on main screen

              // System.out.println("+++ System.currentTimeMillis() -
              // last_new_data_received_TimeMillis) > MAX_AGE_AWS_DATA");

              // TEST
              // jTextField3.setForeground(main.input_color_from_aws);

              // ONDERSTAANDE FUNCTIES AANPASSEN VOOT GRAY
              // display aws data values on main screen
              main.displayed_aws_data_obsolate = true; // for DASHBOARD

              main.date_time_fields_update();
              main.position_fields_update();
              main.barometer_fields_update();
              main.barograph_fields_update();
              main.temperatures_fields_update();
              main.wind_fields_update();

              // OOK INDIVIDUELE meteo parameter (bv wind) INVUL SCHERMEN GRAY MAKEN ??????

              // VOT (display on on main screen)
              // main.jTextField4.setForeground(Color.GRAY);
              main.jTextField4.setForeground(main.obsolate_color_data_from_aws);
              main.jTextField4.setText("AWS data obsolete");
              main.jMenuItem46.setEnabled(false); // Obs to AWS

              // NIET VERGETEN WEER AANTE ZETTEN ALS DATA WEER WORDT ONTVANGEN
              // OF GAAT AUTOMATISCH WEER GOED? (via VOT check)
              // see: function: RS422_Read_AWS_Sensor_Data_For_Display()
              // main.jMenuItem46.setEnabled(true);                  // Obs to AWS

              // MOET DEZE ACTIONLISTNER EXPLICIET GESTOPT WORDEN ALS EEN EXIT DOOR GEBRUIKER WORDT
              // GEGEVEN ????????? (check in console op vreemde foutmeldingen na afsluiten
              // TurboWin+)

              // MOET DEZE ACTIONLISTNER EXPLICIET GESTOPT WORDEN ALS EEN EXIT DOOR GEBRUIKER WORDT
              // GEGEVEN ????????? (check in console op vreemde foutmeldingen na afsluiten
              // TurboWin+)

              // message obsolete data only the first time
              if (main.obsolate_data_flag == false) {
                String message = "[AWS] sensor data obsolete (lost connection?)";
                main.log_turbowin_system_message(message);
              } // if (main.obsolate_data_flag == false)
              else // data obsolete but after > 5 minutes
              {

                if (main.RS232_connection_mode == 10) // OMC-140 ethernet
                {
                  // do nothing port 0.0.0.0:60004 will alway stay open as long as TurboWin+/Web is
                  // running (Windows power shell command "netstat -aon")
                } //  if (main.RS232_connection_mode == 10)
                else if ((main.RS232_connection_mode == 3)
                    || (main.RS232_connection_mode == 9)
                    || (main.RS232_connection_mode
                        == 11)) // EUCAWS serial or OMC-140 serial or AMOS2X serial (but no OMC-140
                // ethernet)
                {

                  // NB RS422 listener restart every hour
                  cal_RS422_system_date_time =
                      new GregorianCalendar(
                          new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
                  cal_RS422_system_date_time.getTime(); // now effective
                  int RS422_system_minute_local = cal_RS422_system_date_time.get(Calendar.MINUTE);

                  // cancel RS422 listener thread
                  if (RS422_system_minute_local == CANCEL_AWS_THREAD) {
                    RS422_worker.cancel(true);
                    String message = "[AWS] sensor data listener thread cancelling";
                    main.log_turbowin_system_message(message);
                  }

                  // restart RS422 listener thread
                  if (RS422_system_minute_local == START_AWS_THREAD) {
                    // NB in case of power failure the com port of connected AWS is still ok (still
                    // open) but listener must be restarted
                    //    so we cannot use Function: RS422_Check_Serial_Ports() because then the
                    // present default port, which is ok, will be reported as occupied!
                    //    so first Function: RS422_Check_Default_Serial_Port(), see below
                    RS422_Check_Default_Serial_Port(); // in this function:: main.defaultPort will
                    // be set to null if present default comm
                    // port was not valid anymore

                    if (main.defaultPort == null) {
                      // so default port not valid anymore (most likely there was no power break)
                      // now first checking/looking for the correct com port
                      // checking/looking for the correct com port
                      int completed_checks_serial_ports = 0;
                      main.defaultPort = null; // reset !
                      while ((main.defaultPort == null)
                          && (completed_checks_serial_ports < MAX_COMPLETED_AWS_PORT_CHECKS)) {
                        RS422_Check_Serial_Ports(completed_checks_serial_ports);
                        completed_checks_serial_ports++;
                      }

                      if (main.defaultPort == null) {
                        String message =
                            "[AWS] no AWS found (defaultPort = null); listener thread not restarted";
                        main.log_turbowin_system_message(message);
                      }
                    } // if (main.defaultPort == null)

                    if (main.defaultPort != null) {
                      main.serialPort = SerialPort.getCommPort(main.defaultPort);

                      // restart RS422 listener thread
                      RS422_worker = new RS422_V3_Class_Receive_Sensor_Data(); // RS422_worker = new
                      // RS422_Class_Receive_Sensor_Data();
                      RS422_worker.execute();
                      String message = "[AWS] sensor data listener thread restarting";
                      main.log_turbowin_system_message(message);
                    } // if (main.defaultPort != null)
                  } // if (RS422_system_minute_local == START_AWS_THREAD)
                } // else if ((main.RS232_connection_mode == 3) || (main.RS232_connection_mode == 9)
                // || (main.RS232_connection_mode == 11))
              } // data obsolete but after > 5 minutes

              main.obsolate_data_flag = true;

            } // if ((System.currentTimeMillis() - last_new_data_received_TimeMillis) >
            // MAX_AGE_AWS_DATA)
            else // so aws data NOT obsolate (data < 5 minutes old)
            {
              main.displayed_aws_data_obsolate = false; // for DASHBOARD

              // NB in this case: in function: RS422_Read_AWS_Sensor_Data_For_Display() the fields
              // on the main screen will be updated
              // NB main.date_time_fields_update() etc. and VOT will be updated there

              // reset message if before was written data obsolate
              // NB probably the message below will never be written because disconnected serial
              // connection must be reconnected by a program restart (contraru to WiFi connection)
              if (main.obsolate_data_flag) {
                main.jTextField4.setForeground(Color.BLACK); // reset to black (default) color
                String message = "[AWS] start receiving sensor data again";
                main.log_turbowin_system_message(message);
              } // if (main.obsolate_data_flag)

              main.obsolate_data_flag = false;

              // to depress "AWS data obsolete" on status line in the case of a connected AWS
              // without VOT parameter (eg OMC-140)
              if ((main.RS232_connection_mode == 9 || main.RS232_connection_mode == 10)
                  && (main.AWSR
                      == false)) // OMC-140 serial or internet but not if AWSR because then next
              // upload is set to blank every time
              {
                main.jTextField4.setText("");
              }
            } // else

            // initialisation
            // last_new_data_received_TimeMillis = 0;

            // for updating message "visual data adding is yes/no advised" (jLabel39)
            if (main.AWSR) // NOT FOR EUCAWS
            {
              if (main.obsolate_data_flag) {
                main.jLabel39.setText("");
              } else {
                cal_AWSR_system_dt =
                    new GregorianCalendar(
                        new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
                cal_AWSR_system_dt.getTime(); // now effective
                int AWSR_system_hour =
                    cal_AWSR_system_dt.get(
                        Calendar
                            .HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at
                // 10:04:15.250 PM the HOUR_OF_DAY is 22.
                int AWSR_system_minutes = cal_AWSR_system_dt.get(Calendar.MINUTE);

                if (((AWSR_system_hour + 1) % Integer.valueOf(main.AWSR_reporting_interval)) == 0) {
                  int AWSR_minutes_for_upload = 60 - AWSR_system_minutes;
                  if (AWSR_minutes_for_upload <= 30) {
                    // less the 30 minutes (= 1800000 msec) before next AWSR upload
                    // NB "-1" too be sure, thread is always behind
                    main.jLabel39.setForeground(new Color(0, 153, 0)); // dark green
                    main.jLabel39.setText(
                        "--- "
                            + (AWSR_minutes_for_upload - 1)
                            + " minutes to go before next automated upload, you can now add observation data ---");
                  } else {
                    main.jLabel39.setForeground(Color.RED);
                    main.jLabel39.setText(
                        "--- more than 30 minutes to go before next automated upload, please do not add observation data --- ");
                  }
                } // if ( ((AWSR_system_hour + 1) % Integer.valueOf(main.AWSR_reporting_interval))
                // == 0)
                else {
                  main.jLabel39.setForeground(Color.RED);
                  main.jLabel39.setText(
                      "--- more than 30 minutes to go before next automated upload, please do not add observation data --- ");
                } // else
              } // no obsolete data
            } // if (main.AWSR)

            // AWS (EUCAWS and OMC-140) clearing display and reseting core meteo parmaters
            //
            cal_AWS_system_dt =
                new GregorianCalendar(
                    new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
            cal_AWS_system_dt.getTime(); // now effective
            // int AWS_system_hour = cal_AWS_system_dt.get(Calendar.HOUR_OF_DAY);              //
            // HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250 PM the HOUR_OF_DAY
            // is 22.
            int AWS_system_minutes = cal_AWS_system_dt.get(Calendar.MINUTE);

            // EUCAWS cleaning display + resetting core meteo parameters
            //
            if (main.RS232_connection_mode == 3) // EUCAWS serial
            {
              // NB in EUCAWS mode TurboWin+ will not upload/send the data itself so no autmatically
              // cleaning/resetting of the display/parameters
              //    as long as the user do not select: "Output -> obs to AWS" so this automatic
              // reset at every whole hour to be sure
              //
              // reset all meteo parameters at exactly 1 minute past the whole hour (ps 1 minute to
              // be safe, maybe at 0 minutes it is also ok)
              if (AWS_system_minutes == 1) {
                main_RS232_RS422
                    .RS422_initialise_AWS_Sensor_Data_For_Display(); // must be called before:
                // Reset_all_meteo_parameters();
                main.Reset_all_meteo_parameters();
              }
            } // if (main.RS232_connection_mode == 3)

            // AWS (EUCAWS or OMC-140): for system invoked pop-up (reminder visual obs) screen and
            // system 'delete' pop-up screen
            //

            if (main.pop_up_screen) // EUCAWS or OMC-140
            {
              Activate_Pop_Up_Screen_APR_AWS(); // pop-up screen reminder add visual obs
            }
          } // public void actionPerformed(ActionEvent e)
        };

    /* main loop for checking when new AWS data arrives */
    check_new_data_timer_delay =
        DELAY_NEW_DATA_CHECK_LOOP; // delay between two new aws data checks (e.g. 1 minute)
    check_new_data_timer = new Timer(check_new_data_timer_delay, check_new_data_action);
    check_new_data_timer.setRepeats(true); // false = only one action
    check_new_data_timer.setInitialDelay(
        INITIAL_DELAY_NEW_DATA_CHECK_LOOP); // time in millisec to wait after timer is started to
    // fire first event
    check_new_data_timer.setCoalesce(true); // to be sure
    check_new_data_timer.start();
  }

  public void RS422_initComponents() {
    /////// NB EUCAWS serial or OMC-140 serial only ////////
    //
    // called from specific_connection_initComponents() [main.java]
    //

    // http://www.snip2code.com/Snippet/1044/Java--read-from-USB-using-RXTX-library
    // FOR LINUX:
    // #==================================================================#
    // WARNING! - DO NOT SET THE FOLLOWING PROPERTY WITH RXTX LIBRARY, IT
    // CAUSES A PROGRAM LOCK:
    // serialPort.notifyOnOutputEmpty(true);
    // #==================================================================#

    // start timer for checking every 1 minute if there is new AWS meteo sensor data available
    // (because if last data > 5 minutes old 'gray' data on main screen)
    // last_new_data_received_TimeMillis = 0;
    // RS422_init_new_aws_data_received_check_timer();

    // for dispaying VOT (Visual Observation Trigger) line in bottom text field on main screen
    color_dark_green = new Color(76, 153, 0);

    // for sensor data files (in the file name itself) (note also used in RS232_view.java)
    main.sdf3 =
        new SimpleDateFormat(
            "yyyyMMddHH"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf3.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for date-time strings/remarks in the sensor data records (note also used in RS232_view.java)
    main.sdf4 =
        new SimpleDateFormat(
            "yyyyMMddHHmm"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf4.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for function: RS422_write_sensor_data_to_file() and RS422_V3_write_sensor_data_to_file()
    sdf7 = new SimpleDateFormat("ss");
    sdf7.setTimeZone(TimeZone.getTimeZone("UTC"));

    // dir for storage sensor data
    // if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
    if ((main.logs_dir == null) || (main.logs_dir.compareTo("") == 0)) {
      String info =
          "logs folder (for sensor data storage) unknown, select: Maintenance -> Log files settings";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
    }

    // checking/looking for the correct com port
    int completed_checks_serial_ports = 0;
    while ((main.defaultPort == null)
        && (completed_checks_serial_ports < MAX_COMPLETED_AWS_PORT_CHECKS)) {
      RS422_Check_Serial_Ports(completed_checks_serial_ports);
      completed_checks_serial_ports++;
    }

    // delete old sensor data files
    main.log_turbowin_system_message("[GENERAL] deleting old (AWS) sensor data files");
    RS232_Delete_Sensor_Data_Files(); // also used by RS422 (AWS)

    if (main.defaultPort != null) {
      // info text on (bottom) main screen
      main.jLabel4.setText(
          main.APPLICATION_NAME
              + " "
              + main.application_mode
              + ", last received AWS data via serial communication: no data"); // application mode
      // was set in
      // initComponents2()
      // [main.java]

      main.serialPort = SerialPort.getCommPort(main.defaultPort);

      RS422_worker = new RS422_V3_Class_Receive_Sensor_Data(); // RS422_worker = new
      // RS422_Class_Receive_Sensor_Data();
      RS422_worker.execute();

      String message = "[AWS] start listening";
      main.log_turbowin_system_message(message);

      // set start-up sequence finished flag
      // main.turbowin_start_up_sequence_finished = true;

      // start timer for checking every 1 minute if there is new AWS sensor data available (because
      // if last data > 5 minutes old 'gray' data on main screen)
      // NB invoking after: RS422_worker = new RS422_Class_Receive_Sensor_Data(); and
      // RS422_worker.execute();
      last_new_data_received_TimeMillis = 0;
      RS422_init_new_aws_data_received_check_timer();
    }
  }

  private class RS422_V3_Class_Receive_Sensor_Data extends SwingWorker<String, String> {
    /////////////////// AWS serial only /////////////////
    //
    // called from: RS422_initComponents()

    //   new SwingWorker<String, String>()
    //   {
    @Override
    protected String doInBackground() throws Exception {
      main.serialPort.openPort();
      if (main.serialPort.isOpen()) {
        // main.serialPort.openPort();               // NB will be closed in Function:
        // File_Exit_menu_actionPerformd() [main.java]
        // main.serialPort.setParams(main.bits_per_second, main.data_bits, main.stop_bits,
        // main.parity);
        // main.serialPort.setFlowControlMode(main.flow_control);
        main.serialPort.setComPortParameters(
            main.bits_per_second, main.data_bits, main.stop_bits, main.parity);
        main.serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

        // Preparing a mask. In a mask, we need to specify the types of events that we want to
        // track.
        // Well, for example, we need to know what came some data, thus in the mask must have the
        // following value: MASK_RXCHAR. If we, for example, still need to know about changes in
        // states
        // of lines CTS and DSR, the mask has to look like this: SerialPort.MASK_RXCHAR +
        // SerialPort.MASK_CTS + SerialPort.MASK_DSR
        // int mask = SerialPort.MASK_RXCHAR;

        // Set the prepared mask
        // main.serialPort.setEventsMask(mask);

      } // if (serialPort.isOpen())
      else {
        System.out.println(
            "+++ " + "[AWS] Couldn't open " + main.serialPort.getDescriptivePortName());
      }

      if (main.serialPort.isOpen()) {
        main.serialPort.addDataListener(
            new SerialPortDataListener() {
              @Override
              public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
              }

              @Override
              public void serialEvent(SerialPortEvent event) {
                String ontvangen_SMD_string = "";

                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;

                // byte[] newData = new byte[main.serialPort.bytesAvailable()];
                // int numRead = main.serialPort.readBytes(newData, newData.length);
                int numRead;
                byte[] newData = null;
                try {
                  newData = new byte[main.serialPort.bytesAvailable()];
                  numRead = main.serialPort.readBytes(newData, newData.length);
                } catch (NegativeArraySizeException ex) {
                  numRead = -1;
                  // NB don't modify the GUI anywhere except for on the event-dispatch-thread.!! (so
                  // not here in the function doinBackground())
                }

                // ontvangen_SMD_string = main.serialPort.readString(event.getEventValue());
                if (numRead > 0) {
                  ontvangen_SMD_string = new String(newData, StandardCharsets.UTF_8);

                  // System.out.println("Read " + numRead + " bytes.");

                  // publish: Sends data chunks to the process(java.util.List) method. This method
                  // is to be used from inside the doInBackground method to deliver intermediate
                  // results for processing on the Event Dispatch Thread inside the process method.
                  //          Because the process method is invoked asynchronously on the Event
                  // Dispatch Thread  multiple invocations to the publish method might occur before
                  // the process method is executed. For performance purposes all these invocations
                  // are coalesced into one invocation with concatenated arguments.
                  //
                  // For example:
                  //
                  // publish("1");
                  // publish("2", "3");
                  // publish("4", "5", "6");
                  //
                  // might result in: process("1", "2", "3", "4", "5", "6")
                  //
                  publish(new String[] {ontvangen_SMD_string});
                } // if (numRead > 0)
                else if (numRead == -1) {
                  publish(new String[] {"interruption or excecution error\n"});
                }
              } // public void serialEvent(SerialPortEvent event)
            });
      } // if (serialPort.isOpen())

      return null;
    } // protected Void doInBackground() throws Exception

    @Override
    protected void process(List<String> data) {
      // process: Receives data chunks from the publish method asynchronously on the Event Dispatch
      // Thread.
      for (String ontvangen_SMD_string : data) {
        // NB altijd in for loop omdat meerdere ontbvangen_SMD_string's verzameld kunnen zijn
        // voordat het hier procesed wordt(inherent aan SwingWorker)
        // System.out.println("ontvangen_SMD_string = " + ontvangen_SMD_string);

        main.total_string = ontvangen_SMD_string;

        // NB Onderstaande om i.p.v. elke minuut de data per 5 minuten op te slaan
        //    werkt hier niet (door de 'brokken' record maar bij AWS zou het wel een mogelijkheid
        // kunnen zijn
        // if (total_string.indexOf("3$") != -1)
        // {
        //   String minuten = sdf4.format(new Date()).substring(10, 10 + 2);  // sdf4: yyyyMMddHHmm
        //   int int_minuten = Integer.parseInt(minuten.trim());
        //
        //   if (int_minuten % 5 == 0   );
        //
        System.out.print(main.total_string);
        // System.out.println(total_string); // NB zo zie je ruwweg hoeveel bytes dat telkens
        // beschikbaar zijn

        // error logging
        if (main.total_string.indexOf("error") != -1) {
          String message = "[AWS] " + "interruption or execution exception";
          main.log_turbowin_system_message(message);

          // main.obsolate_data_flag = true;  // see also Function
          // RS422_init_new_aws_data_received_check_timer() [main_RS232_RS422.java]

        } //  if (main.total_string.indexOf("error") != -1)

        // write sensor data to sensor log file
        RS422_V3_write_sensor_data_to_file();
        // update the date and time on the main screen (and the global date time var's)
        // NB if during start up the date and time displayed in the pupup message was not comfired
        // by the user
        //    it is assumed that the pc is not running on the correct time so then do not update the
        // date time automatically
        if (main.use_system_date_time_for_updating == true) {
          set_datetime_while_collecting_sensor_data();
        }
      }
    }
  }

  private void RS232_GPS_NMEA_0183_Check_Serial_Ports(int completed_checks_serial_ports) {
    int teller = -1;
    // String[] serial_ports_portid_array                = new String[main.NUMBER_COM_PORTS];
    SerialPort[] serial_ports_portid_array = new SerialPort[main.NUMBER_COM_PORTS];
    String info = "[GPS] start up: no GPS found";
    String hulp_parity = ""; // for message writing on java console

    // initialisation
    for (int i = 0; i < main.NUMBER_COM_PORTS; i++) {
      serial_ports_portid_array[i] = null;
    }

    // initialisation
    GPS_defaultPort = null;
    GPS_defaultPort_descriptive = null;

    if (main.prefered_GPS_COM_port.equals("AUTOMATICALLY")) {
      // Temporary message box, (pop-up for only a short time) automatically disappears
      //
      if (completed_checks_serial_ports
          == 0) // only show this temporary meassagebox at first port-checking-round
      {
        final JOptionPane pane_begin =
            new JOptionPane(
                "[GPS] checking serial com ports (this may take a while)",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_begin_dialog = pane_begin.createDialog(main.APPLICATION_NAME);

        Timer timer_begin =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    checking_ports_begin_dialog.dispose();
                  }
                });
        timer_begin.setRepeats(false);
        timer_begin.start();
        checking_ports_begin_dialog.setVisible(true);
      }

      // Get sorted array of serial ports in the system
      // main.portList = SerialPortList.getPortNames();
      SerialPort[] serialPorts = SerialPort.getCommPorts();

      // for (int i = 0; i < main.portList.length && i < main.NUMBER_COM_PORTS; i++)
      for (int portNo = 0; portNo < serialPorts.length; portNo++) {
        teller++;
        SerialPort serialPort_test =
            SerialPort.getCommPort(serialPorts[portNo].getSystemPortName());
        serialPort_test.openPort();

        if (serialPort_test.isOpen()) {
          serial_ports_portid_array[teller] = serialPorts[portNo];
          serialPort_test.closePort();
          String message =
              "[GPS] found serial com port (ok): " + serialPorts[portNo].getDescriptivePortName();
          main.log_turbowin_system_message(message);
        } // if (serialPort_test.isOpen())
        else {
          serial_ports_portid_array[teller] = null;
          System.out.println(
              "[GPS] couldn't open: " + serialPorts[portNo].getDescriptivePortName());
        } // else

        // NB opening a port cannot be done in SwingWorker, so must be done here !!

      } //  for (int i = 0; i < main.portList.length && i < main.NUMBER_COM_PORTS; i++)

      for (int i = 0; i < main.NUMBER_COM_PORTS; i++) // max number ports to open
      {
        if (serial_ports_portid_array[i] != null) // so serial port present (and also not in use)
        {
          // SerialPort serialPort_test = new SerialPort(serial_ports_portid_array[i]);
          SerialPort serialPort_test =
              SerialPort.getCommPort(serial_ports_portid_array[i].getSystemPortName());
          serialPort_test.openPort();
          if (serialPort_test.isOpen())
          // try
          {
            serialPort_test.openPort(); // Open serial port

            if (GPS_NMEA_0183_parity == 0) {
              hulp_parity = "none";
            } else if (GPS_NMEA_0183_parity == 1) {
              hulp_parity = "odd";
            } else if (GPS_NMEA_0183_parity == 2) {
              hulp_parity = "even";
            }

            System.out.println(
                "[GPS] trying to open with "
                    + String.valueOf(main.GPS_bits_per_second)
                    + " "
                    + hulp_parity
                    + " "
                    + String.valueOf(GPS_NMEA_0183_data_bits)
                    + " "
                    + String.valueOf(GPS_NMEA_0183_stop_bits)
                    + " "
                    + serial_ports_portid_array[i].getDescriptivePortName());
            // serialPort_test.setParams(main.GPS_bits_per_second, GPS_NMEA_0183_data_bits,
            // GPS_NMEA_0183_stop_bits, GPS_NMEA_0183_parity);
            serialPort_test.setComPortParameters(
                main.GPS_bits_per_second,
                GPS_NMEA_0183_data_bits,
                GPS_NMEA_0183_stop_bits,
                GPS_NMEA_0183_parity);
            serialPort_test.setFlowControl(GPS_NMEA_0183_flow_control);

            try {
              Thread.sleep(2000); // 1000 seems too short on a few systems
            } catch (InterruptedException ex) {
            }

            // String response = serialPort_test.readString();
            // System.out.println(response);

            // the response
            String response = "";
            serialPort_test.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);
            try {
              // while (true)
              // {
              byte[] readBuffer =
                  new byte[1024]; // NB keep size 1024 because first part of incoming data is rubish
              // int numRead = serialPort_test.readBytes(readBuffer, readBuffer.length);
              serialPort_test.readBytes(readBuffer, readBuffer.length);
              // System.out.println("Read " + numRead + " bytes.");
              response = new String(readBuffer, StandardCharsets.UTF_8);
              System.out.println(response);
              // }
            } catch (Exception e) {
              System.out.println(e);
            }

            if (SerialPortResponseMatcher.containsAny(response, "$GP", "$GL", "$GN")) {
              info =
                  "[GPS] found GPS on port: "
                      + serial_ports_portid_array[i].getDescriptivePortName();
              System.out.print("--- ");
              main.log_turbowin_system_message(info);
              System.out.println("");

              serialPort_test.closePort();
              GPS_defaultPort = serial_ports_portid_array[i].getSystemPortName();
              GPS_defaultPort_descriptive = serial_ports_portid_array[i].getDescriptivePortName();
              break;
            } else {
              serialPort_test.closePort();
              info = "[GPS] no GPS found";
              System.out.println("--- " + info);
              System.out.println("");
            }
          } // if (serialPort_test.isOpen())
          // catch (SerialPortException ex)
          else {
            // System.out.println(ex);
            System.out.println(
                "[GPS] couldn't open: " + serial_ports_portid_array[i].getDescriptivePortName());
          }
        } // if (serial_ports_portid_array[i] != null)
      } // for (int i = 0; i < NUMBER_COM_PORTS; i++)

      if (GPS_defaultPort != null) {
        // temporary message box (GPS (NMEA 0183) was found)
        final JOptionPane pane_end =
            new JOptionPane(
                info,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog checking_ports_end_dialog = pane_end.createDialog(main.APPLICATION_NAME);

        Timer timer_end =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    // checking_ports_end_dialog.setVisible(false);
                    // or maybe you'll need dialog.dispose() instead?
                    checking_ports_end_dialog.dispose();
                  }
                });
        timer_end.setRepeats(false);
        timer_end.start();
        checking_ports_end_dialog.setVisible(true);
      } else {
        // No com port with an GPS (NMEA 0183) connected found

        // blocking message box (no GPS NMEA 0183 found); but only show message if it is the last
        // port-checking-round
        if (completed_checks_serial_ports == MAX_COMPLETED_GPS_PORT_CHECKS - 1) {
          main.log_turbowin_system_message(info);

          if (main.obsolate_GPS_data_flag == false) // eg at start up of this application
          {
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
        }
      } // else

    } // if (main.prefered_COM_port.equals("AUTOMATICALLY"))
    else // fixed GPS com port set by user
    {
      try {
        // SerialPort serialPort_test = new SerialPort(main.prefered_GPS_COM_port);
        SerialPort serialPort_test = SerialPort.getCommPort(main.prefered_GPS_COM_port);
        serialPort_test.openPort();

        if (serialPort_test.isOpen()) {
          serialPort_test.closePort();

          // OK, no port problems encoutered
          GPS_defaultPort = main.prefered_GPS_COM_port;
          GPS_defaultPort_descriptive =
              main.prefered_COM_port; // NB this is a work around this is not the exact descriptive
          // term as obtained via the AUTOMTICALLY branch
          info = "[GPS] " + main.prefered_GPS_COM_port + ", serial com port available";

          // file logging
          main.log_turbowin_system_message(info);

          final JOptionPane pane_begin =
              new JOptionPane(
                  info,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog checking_ports_begin_dialog =
              pane_begin.createDialog(main.APPLICATION_NAME);

          Timer timer_begin =
              new Timer(
                  1000,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      checking_ports_begin_dialog.dispose();
                    }
                  });
          timer_begin.setRepeats(false);
          timer_begin.start();
          checking_ports_begin_dialog.setVisible(true);
        } // if (serialPort_test.isOpen())
        else {
          // error opening predefined (by the user)port, reset/warnings
          info = "[GPS] " + main.prefered_GPS_COM_port + ", serial com port not available";
          main.log_turbowin_system_message(info);

          if (main.obsolate_GPS_data_flag == false) // eg at start up of this application
          {
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          }
          // System.out.println(info);
          GPS_defaultPort = null;
          GPS_defaultPort_descriptive = null;
        } // else
      } // try
      catch (SerialPortInvalidPortException | HeadlessException e) {
        // NB with fixed COM port on Windows systems: reconnecting will give in most situations no
        // problem (same USB-COM port)
        //                        on Linux systems: big chance the USB port is changed (especially
        // if via USB-hub)
        //
        //    so on Linux with fixed COM port big chance this catch will be invoked but not on
        // Windows

        info = "[GPS] " + e;
        main.log_turbowin_system_message(info);
      } // catch
    }
  }

  public static boolean RS232_GPS_NMEA_0183_Date_Position_Parsing(String mode) {
    // mode options: - MINUTE_UPDATE
    //               - "APR"
    //               - "MANUAL"
    //
    //
    // GPS_date: 230394       Date - 23rd of March 1994
    // GPS_time: 092751.000   time 09:27:51                                             // nb
    // fix_time could also be: 092751
    //

    // called from:
    //              - initSynopparameters() [myposition.java]
    //              - RS232_GPS_NMEA_0183_init_new_data_received_check_timer()
    // [main_RS232_RS422.java]
    //              - WiFi_Class_Receive_UDP
    //              - RS232_Class_Receive_Sensor_Data_II
    //              - RS232_Class_Receive_Sensor_Data
    //
    //

    String message_fix_date_time = "";
    String message_fix_position = "";
    String mode_retrieval = "";
    long store_fix_date_minutes =
        Long.MAX_VALUE; // for storage of date-time for calculation of obs course end speed
    double store_fix_lat =
        Double.MAX_VALUE; // for storage of positions for calculation of obs course end speed
    double store_fix_lon =
        Double.MAX_VALUE; // for storage of positions for calculation of obs course end speed
    double double_fix_latitude_minutes =
        0; // for storage of positions for calculation of obs course end speed
    double double_fix_longitude_minutes =
        0; // for storage of positions for calculation of obs course end speed

    // myposition.COG_APR = Double.MAX_VALUE;
    // myposition.SOG_APR = Double.MAX_VALUE;
    // myposition.COG_APR_wind = Double.MAX_VALUE;
    // myposition.SOG_APR_wind = Double.MAX_VALUE;

    GpsDateTimeValidator.Result dateTimeResult =
        GpsDateTimeValidator.validate(
            GPS_date, GPS_time, System::currentTimeMillis, TIMEDIFF_GPS_DATA);
    boolean GPS_date_time_ok = dateTimeResult.valid();
    String message = dateTimeResult.message();
    if (GPS_date_time_ok) {
      store_fix_date_minutes = dateTimeResult.minuteTimestamp();
    }

    if (GPS_date_time_ok) {
      //
      /////////// date ///////////////
      //

      // year
      String fix_year = "20" + GPS_date.substring(4);

      // month
      String fix_month = GPS_date.substring(2, 4);

      // day
      String fix_day = GPS_date.substring(0, 2);

      //
      ////////// Time (eg 092751) /////////
      //

      // hour
      String fix_hour = GPS_time.substring(0, 2);

      // minutes
      String fix_minute = GPS_time.substring(2, 4);

      // System.out.println("GPS date/time (dd-mm-yyyy hh.mm UTC): " + fix_day + "-" + fix_month +
      // "-" + fix_year + " " +  fix_hour + "." + fix_minute + " UTC");
      message_fix_date_time =
          "GPS date/time (dd-mm-yyyy hh.mm UTC): "
              + fix_day
              + "-"
              + fix_month
              + "-"
              + fix_year
              + " "
              + fix_hour
              + "."
              + fix_minute
              + " UTC";

      //
      //////// latitude (eg 4804.7041778) /////////
      //

      GpsCoordinateParser.Result coordinate =
          GpsCoordinateParser.parse(
              GPS_latitude, GPS_latitude_hemisphere, GPS_longitude, GPS_longitude_hemisphere);

      myposition.latitude_degrees = coordinate.latitudeDegrees;
      myposition.latitude_minutes = coordinate.latitudeMinutes;
      myposition.latitude_hemisphere = coordinate.latitudeHemisphere;
      if (coordinate.latitudeDegreesParsed) {
        myposition.int_latitude_degrees = coordinate.latitudeDegreesNumber;
      }
      if (coordinate.latitudeMinutesParsed) {
        myposition.int_latitude_minutes = coordinate.latitudeMinutesNumber;
      }
      myposition.lalala_code = coordinate.latitudeCode;
      myposition.longitude_degrees = coordinate.longitudeDegrees;
      myposition.longitude_minutes = coordinate.longitudeMinutes;
      myposition.longitude_hemisphere = coordinate.longitudeHemisphere;
      if (coordinate.longitudeDegreesParsed) {
        myposition.int_longitude_degrees = coordinate.longitudeDegreesNumber;
      }
      if (coordinate.longitudeMinutesParsed) {
        myposition.int_longitude_minutes = coordinate.longitudeMinutesNumber;
      }
      myposition.lolololo_code = coordinate.longitudeCode;
      if (coordinate.quadrant != null) {
        myposition.Qc_code = coordinate.quadrant;
      }
      GPS_date_time_ok = coordinate.valid;

      if (GPS_date_time_ok) {
        store_fix_lat = coordinate.signedLatitude;
        store_fix_lon = coordinate.signedLongitude;
      } else {
        message = "[GPS] formatting error";
        message_fix_position = "";
      }

      if (GPS_date_time_ok) {
        message_fix_position =
            "GPS position (dd-mm [N/S] ddd-mm [E/W]): "
                + myposition.latitude_degrees
                + "-"
                + myposition.latitude_minutes
                + " "
                + myposition.latitude_hemisphere.substring(0, 1)
                + " "
                + myposition.longitude_degrees
                + "-"
                + myposition.longitude_minutes
                + " "
                + myposition.longitude_hemisphere.substring(0, 1);
      }
    } // if (GPS_date_time_ok)

    if (GPS_date_time_ok == false) {
      // NB if mode.equals("MINUTE_UPDATE") -> do nothing, otherwise every minute an extra line in
      // the log in case of bad connection

      if (mode.equals("APR")) // e.g. every 1, 3 or 6 hours
      {
        // message (was set before)
        main.log_turbowin_system_message(message);
      } else if (mode.equals("MANUAL")) {
        // message (was set before)
        main.log_turbowin_system_message(message);

        // temporary message box (eg mismatch GPS date-time and computer date-time)
        String info = "no reliable GPS position available";

        final JOptionPane pane_end =
            new JOptionPane(
                info,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);
        final JDialog temp_dialog = pane_end.createDialog(main.APPLICATION_NAME);

        Timer timer_end =
            new Timer(
                2500,
                new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    temp_dialog.dispose();
                  }
                });
        timer_end.setRepeats(false);
        timer_end.start();
        temp_dialog.setVisible(true);
      } // else if (mode.equals("MANUAL"))
    } //  if (GPS_date_time_ok == false)
    else // GPS_date_time_ok == true
    {

      if (mode.equals("APR")
          || mode.equals(
              "MANUAL")) // only in APR mode (1, 3, 6 hours) or manual mode, otherwise every minute
      // message lines in the log
      {
        main.log_turbowin_system_message("[GPS] date-time parsing ok; " + message_fix_date_time);
        main.log_turbowin_system_message("[GPS] position parsing ok; " + message_fix_position);
      }

      // Save the GPS data in array's for computing obs course and speed (based on 3 hrs if FM13 and
      // 10 minutes if format#101)
      //
      RS232_GPS_NMEA_0183_Array_Store(store_fix_lat, store_fix_lon, store_fix_date_minutes);

      // determine the latitude and longitude from the track to be considered
      //
      mode_retrieval = "";
      double[] positions = RS232_GPS_NMEA_0183_Array_Retrieval(mode_retrieval);
      // NB double_lat_start = positions[0];
      // NB double_lon_start = positions[1];
      // NB double_lat_end = positions[2];
      // NB double_lon_end = positions[3];

      Rs232GpsNmeaCogSogWorkflow.start(mode, positions);
      // in case FM13: in APR mode we also need the SOG and COG over 10 minutes for the APTR meteo
      // radar dashboard wind calculations (relative wind)
      //
      // if (main.obs_format.equals(main.FORMAT_FM13) && (main.APR))
      if (main.APR) {
        // determine the latitude and longitude from the track to be considered
        if (main.obs_format.equals(main.FORMAT_FM13)) {
          mode_retrieval = WIND_FM13_DASHBOARD;
        } else if (main.obs_format.equals(main.FORMAT_101)) {
          mode_retrieval = WIND_101_DASHBOARD;
        }
        double[] positions_SOG_COG_wind = RS232_GPS_NMEA_0183_Array_Retrieval(mode_retrieval);
        // NB double_lat_start = positions_SOG_COG_wind[0];
        // NB double_lon_start = positions_SOG_COG_wind[1];
        // NB double_lat_end = positions_SOG_COG_wind[2];
        // NB double_lon_end = positions_SOG_COG_wind[3];

        Rs232GpsNmeaWindCogSogWorkflow.start(mode, positions_SOG_COG_wind);
      } // if (main.obs_format.equals(main.FORMAT_FM13))

      // by default for format_101
      //
      // if (main.obs_format.equals(main.FORMAT_101) && (main.APR))
      // {
      //   myposition.COG_APR_wind = myposition.COG_APR;
      //   myposition.SOG_APR_wind = myposition.SOG_APR;
      // }

    } // else (GPS_date_time_ok == true)

    return GPS_date_time_ok;
  }

  private static double[] RS232_GPS_NMEA_0183_Array_Retrieval(String mode_retrieval) {
    long timeIntervalMinutes;
    if (mode_retrieval.equals(WIND_FM13_DASHBOARD) || mode_retrieval.equals(WIND_101_DASHBOARD)) {
      timeIntervalMinutes = 10;
    } else if (main.obs_format.equals(main.FORMAT_101)) {
      timeIntervalMinutes = 10;
    } else if (main.obs_format.equals(main.FORMAT_FM13)) {
      timeIntervalMinutes = 180;
    } else {
      return GPS_TRACK_HISTORY.retrieve(Integer.MAX_VALUE);
    }
    return GPS_TRACK_HISTORY.retrieve(timeIntervalMinutes);
  }

  private static void RS232_GPS_NMEA_0183_Array_Store(
      final double store_fix_lat, final double store_fix_lon, final long store_fix_date_minutes) {
    GPS_TRACK_HISTORY.store(store_fix_lat, store_fix_lon, store_fix_date_minutes);
  }

  private void RS232_GPS_NMEA_0183_GGA(String ontvangen_GPS_string) {
    // called from: RS232_GPS_Class_Receive_Data

    // NB if an AWS is connected a GPS cannot be connected [see RS232_settings.java]
    //
    //
    //
    // source: http://www.gpsinformation.org/dale/nmea.htm#RMC

    // GGA - essential fix data which provide 3D location and accuracy data.
    //
    // $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47
    //
    // Where:
    //     GGA          Global Positioning System Fix Data
    //     123519       Fix taken at 12:35:19 UTC
    //     4807.038,N   Latitude 48 deg 07.038' N
    //     01131.000,E  Longitude 11 deg 31.000' E
    //     1            Fix quality: 0 = invalid
    //                               1 = GPS fix (SPS)
    //                               2 = DGPS fix
    //                               3 = PPS fix
    //			                        4 = Real Time Kinematic
    //		                           5 = Float RTK
    //                               6 = estimated (dead reckoning) (2.3 feature)
    //			                        7 = Manual input mode
    //			                        8 = Simulation mode
    //     08           Number of satellites being tracked
    //     0.9          Horizontal dilution of position
    //     545.4,M      Altitude, Meters, above mean sea level
    //     46.9,M       Height of geoid (mean sea level) above WGS84
    //                      ellipsoid
    //    (empty field) time in seconds since last DGPS update
    //    (empty field) DGPS station ID number
    //     *47          the checksum data, always begins with *
    //
    //
    // source: https://en.wikipedia.org/wiki/NMEA_0183
    // $GPGGA,092750.000,5321.6802,N,00630.3372,W,1,8,1.03,61.7,M,55.2,M,,*76

    String record_checksum = "";
    String computed_checksum = "";
    boolean checksum_ok = false;
    int number_read_commas = 0;
    int pos_comma = 0;
    int start_fix_time = 0;
    int end_fix_time = 0;
    int start_fix_latitude = 0;
    int end_fix_latitude = 0;
    int start_fix_latitude_hemisphere = 0;
    int end_fix_latitude_hemisphere = 0;
    int start_fix_longitude = 0;
    int end_fix_longitude = 0;
    int start_fix_longitude_hemisphere = 0;
    int end_fix_longitude_hemisphere = 0;
    int start_fix_quality = 0;
    int end_fix_quality = 0;

    // checksum check
    computed_checksum = RS232_GPS_NMEA_Checksum(ontvangen_GPS_string);
    record_checksum = ontvangen_GPS_string.substring(ontvangen_GPS_string.length() - 2);

    if (computed_checksum.equals(record_checksum)) {
      // System.out.println("checksum ok");
      checksum_ok = true;
    } else {
      // System.out.println("record checksum = " + record_checksum);
      // System.out.println("computed checksum = " + computed_checksum);
      System.out.println(ontvangen_GPS_string);
      System.out.println("checksum NOT ok");
    }

    // eg $GPGGA,092750.000,5321.6802,N,00630.3372,W,1,8,1.03,61.7,M,55.2,M,,*76
    if ((checksum_ok)
        && (ontvangen_GPS_string.length()
            > 40)) // for savety, 40 arbitrary number (but will cover latitude, longitude etc)
    {
      do {
        pos_comma = ontvangen_GPS_string.indexOf(",", pos_comma + 1);
        if (pos_comma > 0) // "," found
        {
          // System.out.println("pos_comma = " + pos_comma);
          number_read_commas++;

          if (number_read_commas == 1) {
            start_fix_time = pos_comma + 1;
          } else if (number_read_commas == 2) {
            end_fix_time = pos_comma;
            start_fix_latitude = pos_comma + 1;
          } else if (number_read_commas == 3) {
            end_fix_latitude = pos_comma;
            start_fix_latitude_hemisphere = pos_comma + 1;
          } else if (number_read_commas == 4) {
            end_fix_latitude_hemisphere = pos_comma;
            start_fix_longitude = pos_comma + 1;
          } else if (number_read_commas == 5) {
            end_fix_longitude = pos_comma;
            start_fix_longitude_hemisphere = pos_comma + 1;
          } else if (number_read_commas == 6) {
            end_fix_longitude_hemisphere = pos_comma;
            start_fix_quality = pos_comma + 1;
          } else if (number_read_commas == 7) {
            end_fix_quality = pos_comma;
          }
        } // if (pos_comma > 0)
      } while (pos_comma > 0);

      // fix time
      String hulp_fix_time = ontvangen_GPS_string.substring(start_fix_time, end_fix_time);

      // fix quality (fix quality "0" = invalid)
      String fix_quality = ontvangen_GPS_string.substring(start_fix_quality, end_fix_quality);

      // continue if status ok en seconds = 00
      if ((Integer.parseInt(fix_quality) != 0)
          && (hulp_fix_time
              .substring(4, 6)
              .equals("00"))) // quality=0 = invalid and seconds "00" (= every whole minute)
      {
        System.out.println(ontvangen_GPS_string);

        // raw storage, only for the device log
        RS232_GPS_NMEA_0183_storage_device_log(ontvangen_GPS_string);

        // date (eg 280515)
        //
        // NB Because there is no date in the GGA sentence we can only compare the system time of
        // the GPS and the computer time
        //    For a generic approach the GPS_date is artificial set as computer date
        //    Theroretically an outdated GPS position can be unfairly marked as ok only a few
        // minutes per day
        //    eg 18-12-2015 03.14 UTC last GPS date. Now current date-time 19-12-2015 02:00 UTC so
        // GPS position set invalid by TurboWin+
        //    but if current date-time 19-12-2015 03:13 UTC -> TurboWin+ set it ok because only
        // times (and not date) can be compared
        //
        GregorianCalendar cal_systeem_datum_tijd_NMEA_GGA =
            new GregorianCalendar(
                new SimpleTimeZone(0, "UTC")); // gives system date time in UTC of this moment
        SimpleDateFormat sdf10;
        sdf10 = new SimpleDateFormat("ddMMyy"); // e.g "MMMM dd, yyyy" -> februari 27, 2010
        GPS_date = sdf10.format(cal_systeem_datum_tijd_NMEA_GGA.getTime());

        // time (eg 181908.00)
        GPS_time =
            hulp_fix_time; // now GPS_time always holds the last valid value (passed the "A" status
        // check) of the whole minute

        // update date and time on main screen (and date time input form)
        // main.date_time_fields_update();

        // latitude complete (eg 3404.7041778)
        GPS_latitude = ontvangen_GPS_string.substring(start_fix_latitude, end_fix_latitude);

        // latitude hemisphere
        GPS_latitude_hemisphere =
            ontvangen_GPS_string.substring(
                start_fix_latitude_hemisphere, end_fix_latitude_hemisphere);

        // longitude complete (eg 07044.3966270)
        GPS_longitude = ontvangen_GPS_string.substring(start_fix_longitude, end_fix_longitude);

        // longitude hemisphere
        GPS_longitude_hemisphere =
            ontvangen_GPS_string.substring(
                start_fix_longitude_hemisphere, end_fix_longitude_hemisphere);

        // for timer checking the GPS data is not obsolete (see Function:
        // RS232_GPS_NMEA_0183_init_new_data_received_check_timer)
        last_new_GPS_data_received_TimeMillis = System.currentTimeMillis();

        // update the position fields on the main screen  (so every whole minute)
        // main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("MINUTE_UPDATE");  // NB we do
        // nothing with the return boolean of this function (this call was only for updating
        // position on main screen)

      } // if ( (fix_status.toUpperCase().equals("A")) && (fix_time.substring(4, 6).equals("00")) )
    } // if ((checksum_ok) && (ontvangen_GPS_string.length() > 40))

    // NB in function RS232_GPS_NMEA_0183_Date_Position_Parsing() these GPS vars are
    // inserted/compared with obs position values

  }

  private String RS232_GPS_NMEA_Checksum(String ontvangen_GPS_string) {
    // called from: - RS232_GPS_NMEA_0183_RMC()
    //              - RS232_GPS_NMEA_0183_GGA()

    String computed_checksum = "";
    int checksum = 0;

    // cheksum computed between '$' and '*'
    int start_pos = ontvangen_GPS_string.indexOf('$');
    int end_pos = ontvangen_GPS_string.indexOf('*');

    // cheksum ok? (compulsory for RMA, RMB, and RMC messages)
    //
    if (start_pos != -1 && end_pos != -1) {
      for (int i = start_pos + 1; i < end_pos; i++) {
        checksum = checksum ^ ontvangen_GPS_string.charAt(i);
      }

      computed_checksum = Integer.toHexString(checksum);
      if (computed_checksum.length() == 1) {
        computed_checksum = "0" + computed_checksum;
      }
    } // if (start_pos != -1 && end_pos != -1)

    return computed_checksum = computed_checksum.toUpperCase();
  }

  private void RS232_GPS_NMEA_0183_storage_device_log(String ontvangen_GPS_string) {
    // memory storage only for the device log
    //
    //
    // called from: - RS232_GPS_NMEA_0183_GGA()
    //              - RS232_GPS_NMEA_0183_RMC()
    //
    // storing GPS data from a separate (mini)GPS (not for Mintaka StarX with integrated GPS) in a
    // array for showing in device log
    //
    //
    // array for raw incoming GPS records
    // array [0]   -> most recent GPS string
    // array [1]   -> second last received GPS string
    // array [2]   -> third last received GPS string
    // etc...
    //
    // theoretically due to disruption etc. e.g.
    // array[0] -> just received
    // array[1] -> 25 minutes ago
    // array[2] -> 26 minutes ago
    // array[3] -> 40 minutes ago
    // etc.
    //
    // but also sometimes same info in two consecutive array places!!
    // array[0] -> just received
    // array[1] -> 1 minutes ago
    // array[2] -> 2 minutes ago !!
    // array[3] -> 2 minutes ago !! // same GPS data as arry indice 2
    // array[4] -> 3 minutes ago
    // etc
    //
    // See above therefore MAX number array places not 180 (theoretical 3 hours = 180 minutes) but
    // 200 (MAX_GPS_ARRAY)
    //

    // shift them all 1 array place
    //
    // m = MAX_GPS_ARRAY: oldest
    // m = 0: youngest (just received)
    //
    // On the youngest, just received record, a date-time stamp (YYYYMMDDHHMM) will be added
    //
    //
    for (int m = MAX_GPS_ARRAY; m > 0; m--) {
      GPS_array_device_log[m] = GPS_array_device_log[m - 1];
    }

    // NB
    //    in this application the added timestamp is mostly according: main.sdf4.format(new Date());
    //  [format sdf4: YYYMMDDHHMM]
    //    But 'new date()' do not round up the seconds, this is in most cases no problem
    //    but here, concerning the GPS sentences, it could look odd because many GPS sentences
    // contain the time of the fix
    //    Then it is possible that you see in the record a time of e.g. 091100.000 and a added
    // timestamp of 202404100910,
    //    so suggesting the added timestamp was 1 minute earlier then the GPS fix itself
    //    To avoid this, the added GPS timestamp will be rounded up the the next whole minute
    //

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, 30);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    Date date = calendar.getTime();

    String timestamp = main.sdf4.format(date);
    GPS_array_device_log[0] = ontvangen_GPS_string + timestamp;
  }

  private void RS232_GPS_NMEA_0183_RMC(String ontvangen_GPS_string) {
    // called from: RS232_GPS_Class_Receive_Data

    // NB if an AWS is connected a GPS cannot be connected [see RS232_settings.java]
    //
    //
    //
    // source: http://www.gpsinformation.org/dale/nmea.htm#RMC
    //
    // RMC - NMEA has its own version of essential gps pvt (position, velocity, time) data. It is
    // called RMC, The Recommended Minimum, which will look similar to:
    //
    // $GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A
    //
    // Where:
    //     RMC          Recommended Minimum sentence C
    //     123519       Fix taken at 12:35:19 UTC
    //     A            Status A=active or V=Void.
    //     4807.038,N   Latitude 48 deg 07.038' N
    //     01131.000,E  Longitude 11 deg 31.000' E
    //     022.4        Speed over the ground in knots
    //     084.4        Track angle in degrees True
    //     230394       Date - 23rd of March 1994
    //     003.1,W      Magnetic Variation
    //     *6A          The checksum data, always begins with *
    // Note that, as of the 2.3 release of NMEA, there is a new field in the RMC sentence at the end
    // just prior to the checksum. For more information on this field see here.
    //
    //
    //
    // source: https://en.wikipedia.org/wiki/NMEA_0183
    //
    // $GPRMC,092751.000,A,5321.6802,N,00630.3371,W,0.06,31.66,280511,,,A*45
    //   NB LET OP FIX TAKEN AT 092751.000 COULD ALSO 092751
    //
    ////
    // - Each message's starting character is a dollar sign.
    // - The next five characters identify the talker (two characters) and the type of message
    // (three characters).
    // - All data fields that follow are comma-delimited.
    // - Where data is unavailable, the corresponding field remains blank (it contains no character
    // before the next delimiter \u00B0 see Sample file section below).
    // - The first character that immediately follows the last data field character is an asterisk,
    // but it is only included if a checksum is supplied.
    // - The asterisk is immediately followed by a checksum represented as a two-digit hexadecimal
    // number. The checksum is the bitwise exclusive OR of ASCII codes of all characters between the
    // $ and *.
    //        According to the official specification, the checksum is optional for most data
    // sentences, but is compulsory for RMA, RMB, and RMC (among others).
    // - <CR><LF> ends the message.
    //
    // Typical Baud rate	4800 (There is a variation of the standard called NMEA-0183HS that
    // specifies a baud rate of 38,400. This is in general use by AIS devices.)
    // Data bits	      8
    // Parity	         None
    // Stop bits	      1
    // Handshake	      None
    //

    String record_checksum = "";
    String computed_checksum = "";
    int number_read_commas = 0;
    int pos_comma = 0;
    int start_fix_time = 0;
    int end_fix_time = 0;
    int start_fix_status = 0;
    int end_fix_status = 0;
    int start_fix_latitude = 0;
    int end_fix_latitude = 0;
    int start_fix_latitude_hemisphere = 0;
    int end_fix_latitude_hemisphere = 0;
    int start_fix_longitude = 0;
    int end_fix_longitude = 0;
    int start_fix_longitude_hemisphere = 0;
    int end_fix_longitude_hemisphere = 0;
    int start_fix_date = 0;
    int end_fix_date = 0;
    boolean checksum_ok = false;

    // checksum check
    computed_checksum = RS232_GPS_NMEA_Checksum(ontvangen_GPS_string);
    record_checksum = ontvangen_GPS_string.substring(ontvangen_GPS_string.length() - 2);

    if (computed_checksum.equals(record_checksum)) {
      // System.out.println("checksum ok");
      checksum_ok = true;
    } else {
      // System.out.println("record checksum = " + record_checksum);
      // System.out.println("computed checksum = " + computed_checksum);
      System.out.println(ontvangen_GPS_string);
      System.out.println("checksum NOT ok");
    }

    // process the received GPS RMC string
    // eg $GPRMC,092751.000,A,5321.6802,N,00630.3371,W,0.06,31.66,280511,,,A*45
    //
    if ((checksum_ok)
        && (ontvangen_GPS_string.length()
            > 40)) // for savety, 40 arbitrary number (but will cover latitude, longitude etc)
    {
      do {
        pos_comma = ontvangen_GPS_string.indexOf(",", pos_comma + 1);
        if (pos_comma > 0) // "," found
        {
          // System.out.println("pos_comma = " + pos_comma);
          number_read_commas++;

          if (number_read_commas == 1) {
            start_fix_time = pos_comma + 1;
          } else if (number_read_commas == 2) {
            end_fix_time = pos_comma;
            start_fix_status = pos_comma + 1;
          } else if (number_read_commas == 3) {
            end_fix_status = pos_comma;
            start_fix_latitude = pos_comma + 1;
          } else if (number_read_commas == 4) {
            end_fix_latitude = pos_comma;
            start_fix_latitude_hemisphere = pos_comma + 1;
          } else if (number_read_commas == 5) {
            end_fix_latitude_hemisphere = pos_comma;
            start_fix_longitude = pos_comma + 1;
          } else if (number_read_commas == 6) {
            end_fix_longitude = pos_comma;
            start_fix_longitude_hemisphere = pos_comma + 1;
          } else if (number_read_commas == 7) {
            end_fix_longitude_hemisphere = pos_comma;
          } else if (number_read_commas == 9) {
            start_fix_date = pos_comma + 1;
          } else if (number_read_commas == 10) {
            end_fix_date = pos_comma;
          }
        } // if (pos_comma > 0)
      } while (pos_comma > 0);

      // http://gpsworld.com/what-exactly-is-gps-nmea-data/
      //      181908.00 is the time stamp: UTC time in hours, minutes and seconds.
      //      3404.7041778 is the latitude in the DDMM.MMMMM format. Decimal places are variable.
      //      07044.3966270 is the longitude in the DDDMM.MMMMM format. Decimal places are variable.

      // fix time
      String hulp_fix_time = ontvangen_GPS_string.substring(start_fix_time, end_fix_time);

      // fix status ("A" = active/ok; V = void/not ok)
      String fix_status = ontvangen_GPS_string.substring(start_fix_status, end_fix_status);

      // continue if status ok en seconds = 00
      if ((fix_status.toUpperCase().equals("A"))
          && (hulp_fix_time
              .substring(4, 6)
              .equals("00"))) // status "A" and seconds "00" (= every whole minute)
      {
        System.out.println(ontvangen_GPS_string);

        // raw storage, only for the device log
        RS232_GPS_NMEA_0183_storage_device_log(ontvangen_GPS_string);

        // date (eg 280515)
        GPS_date = ontvangen_GPS_string.substring(start_fix_date, end_fix_date);

        // time (eg 181908.00)
        GPS_time =
            hulp_fix_time; // now GPS_time always holds the last valid value (passed the "A" status
        // check) of the whole minute

        // update date and time on main screen (and date time input form)
        // main.date_time_fields_update();

        // latitude complete (eg 3404.7041778)
        GPS_latitude = ontvangen_GPS_string.substring(start_fix_latitude, end_fix_latitude);

        // latitude hemisphere
        GPS_latitude_hemisphere =
            ontvangen_GPS_string.substring(
                start_fix_latitude_hemisphere, end_fix_latitude_hemisphere);

        // longitude complete (eg 07044.3966270)
        GPS_longitude = ontvangen_GPS_string.substring(start_fix_longitude, end_fix_longitude);

        // longitude hemisphere
        GPS_longitude_hemisphere =
            ontvangen_GPS_string.substring(
                start_fix_longitude_hemisphere, end_fix_longitude_hemisphere);

        // for timer checking the GPS data is not obsolete (see Function:
        // RS232_GPS_NMEA_0183_init_new_data_received_check_timer)
        last_new_GPS_data_received_TimeMillis = System.currentTimeMillis();

        // update the position fields on the main screen (so every whole minute)
        // main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("MINUTE_UPDATE");  // NB we do
        // nothing with the return boolean of this function (this call was only for updating
        // position on main screen)

      } // if ( (fix_status.toUpperCase().equals("A")) && (fix_time.substring(4, 6).equals("00")) )
    } // if ((checksum_ok) && (ontvangen_GPS_string.length() > 40))

    // NB in function RS232_GPS_NMEA_0183_Date_Position_Parsing() these GPS vars are
    // inserted/compared with obs position values

  }

  public void RS232_GPS_NMEA_0183_initComponents() {
    // called from: - specific_connection_initComponents()

    // checking/looking for the correct com port
    //
    int completed_checks_serial_ports = 0;
    while ((GPS_defaultPort == null)
        && (completed_checks_serial_ports < MAX_COMPLETED_GPS_PORT_CHECKS)) {
      RS232_GPS_NMEA_0183_Check_Serial_Ports(completed_checks_serial_ports); // GPS
      completed_checks_serial_ports++;
    }

    if (GPS_defaultPort != null) {
      // System.out.println("+++ GPS_defaultPort = " + GPS_defaultPort);

      // info text on (bottom) main screen
      main.jLabel6.setText(
          main.APPLICATION_NAME + " receiving GPS data via serial communication.....");

      // GPS_serialPort = new SerialPort(GPS_defaultPort);  // NB GPS_defaultPort was determined in
      // Function: RS232_GPS_NMEA_0183_Check_Serial_Ports()
      // final SerialPort GPS_serialPort  = SerialPort.getCommPort(GPS_defaultPort); // // NB
      // GPS_defaultPort was determined in Function: RS232_GPS_NMEA_0183_Check_Serial_Ports()
      main.GPS_serialPort = SerialPort.getCommPort(GPS_defaultPort);

      RS232_GPS_worker = new RS232_GPS_Class_Receive_Data();
      RS232_GPS_worker.execute();

      String message = "[GPS] start listening";
      main.log_turbowin_system_message(message);

      // start timer for checking every 1 minute if there is new barometer sensor data available
      // (because if last data > 5 minutes old 'gray' data on main screen)
      // NB invoking after: RS232_GPS_worker = new RS232_GPS_Class_Receive_Data(); and
      // RS232_GPS_worker.execute();
      last_new_GPS_data_received_TimeMillis = 0;
      RS232_GPS_NMEA_0183_init_new_data_received_check_timer();

      // GPS array's initialisation (used for course and speed last 10 minutes/3 hrs calculation)
      GPS_TRACK_HISTORY.clear();
    } // if (GPS_defaultPort != null)
  }

  private class RS232_GPS_Class_Receive_Data extends SwingWorker<String, String> {
    // called from: - RS232_GPS_NMEA_0183_initComponents()
    //              - RS232_GPS_NMEA_0183_init_new_data_received_check_timer()

    // NB This class use doInBackground() and done() and not doInBackground(), publish() and
    // process() (see outcommentd class RS232_GPS_Class_Receive_Data_2)
    //    This because here, for the GPS connection, there is a continous input streem and not one
    // string every minute. So better to handle everthing in the background
    //    Disadvantage you cannot savely update the GUI in the doInbackground() eg to report the
    // last updated date-time of the received data on label main screen

    // new SwingWorker<String, String>()
    // {
    @Override
    protected String doInBackground() throws Exception {
      main.GPS_serialPort
          .openPort(); // NB will NOT be closed in Function:  File_Exit_menu_actionPerformd()
      // [main.java] ????
      if (main.GPS_serialPort.isOpen()) {
        String hulp_parity = "";
        if (GPS_NMEA_0183_parity == 0) {
          hulp_parity = "none";
        } else if (GPS_NMEA_0183_parity == 1) {
          hulp_parity = "odd";
        } else if (GPS_NMEA_0183_parity == 2) {
          hulp_parity = "even";
        }
        // System.out.println("[GPS] " + GPS_serialPort.getPortName() + " trying to open with " +
        // String.valueOf(main.GPS_bits_per_second) + " " + hulp_parity + " " +
        // String.valueOf(GPS_NMEA_0183_data_bits) + " " + String.valueOf(GPS_NMEA_0183_stop_bits));
        System.out.println(
            "[GPS] "
                + GPS_defaultPort_descriptive
                + " trying to open with "
                + String.valueOf(main.GPS_bits_per_second)
                + " "
                + hulp_parity
                + " "
                + String.valueOf(GPS_NMEA_0183_data_bits)
                + " "
                + String.valueOf(GPS_NMEA_0183_stop_bits));
        main.GPS_serialPort.setComPortParameters(
            main.GPS_bits_per_second,
            GPS_NMEA_0183_data_bits,
            GPS_NMEA_0183_stop_bits,
            GPS_NMEA_0183_parity);
        main.GPS_serialPort.setFlowControl(GPS_NMEA_0183_flow_control);

      } // if (GPS_serialPort.isOpen())
      // catch (SerialPortException ex)
      else {
        System.out.println("[GPS] Couldn't open " + GPS_defaultPort_descriptive);
      }

      if (main.GPS_serialPort.isOpen()) {
        main.GPS_serialPort.addDataListener(
            new SerialPortDataListener() {
              @Override
              public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
              }

              StringBuilder message = new StringBuilder();

              @Override
              public void serialEvent(SerialPortEvent event) {
                String ontvangen_GPS_string = "";

                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                  return;
                }

                int numRead;
                byte[] newData = null;
                try {
                  newData = new byte[main.GPS_serialPort.bytesAvailable()];
                  numRead = main.GPS_serialPort.readBytes(newData, newData.length);
                } catch (NegativeArraySizeException ex) {
                  numRead = -1;
                  // NB don't modify the GUI anywhere except for on the event-dispatch-thread.!! (so
                  // not here in the function doinBackground())
                }

                if (numRead > 0) {
                  for (byte b : newData) {
                    if ((b == '\r' || b == '\n') && message.length() > 0) {
                      ontvangen_GPS_string = message.toString();

                      // NB mail Henry Kleta 22-10-2015
                      //    ...And if you have the choice: Go for the NMEA RMC sentence.
                      //    One sentence, everyting in there and one of the view sentences that
                      // (nearly) all GPSes provide.
                      if (main.RS232_GPS_sentence == 1) // RMC
                      {
                        // GP for a GPS unit, GL for a GLONASS, GN for satellites from multiple
                        // systems (e.g. https://docs.novatel.com/OEM7/Content/Logs/GPRMC.htm)
                        if ((ontvangen_GPS_string.indexOf("$GPRMC") != -1)
                            || (ontvangen_GPS_string.indexOf("$GLRMC") != -1)
                            || (ontvangen_GPS_string.indexOf("$GNRMC") != -1)) {
                          RS232_GPS_NMEA_0183_RMC(ontvangen_GPS_string);
                        }
                      } // if (main.RS232_GPS_sentence == 1)
                      else if (main.RS232_GPS_sentence
                          == 2) // GGA; could be an alternative in case of NMEA 2000 + converter
                      {
                        // GP for a GPS unit, GL for a GLONASS
                        if ((ontvangen_GPS_string.indexOf("$GPGGA") != -1)
                            || (ontvangen_GPS_string.indexOf("$GLGGA") != -1)) {
                          RS232_GPS_NMEA_0183_GGA(ontvangen_GPS_string);
                        }
                      } // else if (main.RS232_GPS_sentence == 2)

                      message.setLength(0);
                    } // if ((b == '\r' || b == '\n') && message.length() > 0)
                    else {
                      message.append((char) b);
                    }
                  } // for (byte b: newData)
                } // if (numRead > 0)
                else if (numRead == -1) {
                  String message_a =
                      "[GPS] " + "interruption or execution error (NegativeArraySizeException)";
                  main.log_turbowin_system_message(message_a);
                }
              } // public void serialEvent(SerialPortEvent spe)
            });
      } // if (GPS_serialPort.isOpen())

      return null;
    } // protected Void doInBackground() throws Exception

    //
    // NB url:
    // https://stackoverflow.com/questions/6523623/graceful-exception-handling-in-swing-worker
    //         You should NOT try to catch exceptions in the background thread but rather let them
    // pass through to the SwingWorker itself,
    //         and then you can get them in the done() method by calling get()which normally returns
    // the result of doInBackground()
    //         (Voidin your situation). If an exceptionwas thrown in the background thread then
    // get() will throw it, wrapped inside an ExecutionException.
    // NB see above: when testing it seems that the NegativeArraySizeException wasn't passed to the
    // Done method!!
    // NB not sure the program will ever execute the code below
    //
    // Executed in EDT
    //
    @Override
    protected void done() {
      try {
        // get();
        String message = get();

        if (message != null) {
          if (message.length() > 0) {
            main.log_turbowin_system_message(message);
          }
        }
      } catch (ExecutionException ex) {
        String message = "[GPS] " + "execution excption";
        main.log_turbowin_system_message(message);
      } catch (InterruptedException ex) {
        String message = "[GPS] " + "interruption exception";
        main.log_turbowin_system_message(message);
      }
    }
  }

  private class WiFi_Class_Receive_UDP extends SwingWorker<String, String> {
    // called from: WiFi_initComponents() [main_RS232_RS422.java]
    //

    // initialisation
    boolean retry = false;
    boolean data_0_minutes_present =
        false; // to detemine if incoming sensor data from 00 minutes time is present, because this

    // is directly linked to sending/uploading of the obs

    @Override
    protected String doInBackground() throws Exception {
      String connection_result =
          ""; // to report connection error (as a return) on the Event Dispatch Thread after the
      // doInBackground method is finished.

      if (!main.lan_ip_address.equals(
          "")) // specific IP address was inserted (Mintaka ENet box with fixed IP listening
      // address, introduced by Maersk fot next-gen firewalls)
      {
        final String INET_ADDR = main.lan_ip_address; // e.g. 192.168.1.12
        final int PORT = 10110;

        try (DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName(INET_ADDR))) {
          String ontvangen_SMD_string = "";
          byte[] buf = new byte[1024];

          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          while (true) {
            ontvangen_SMD_string = "";

            socket.receive(packet);

            int endidx = 0;
            for (; endidx < buf.length; endidx++) {
              if (buf[endidx] == '*') break;
            }

            endidx += 3;
            int idx = 0;
            for (int i = 0; i < endidx; i++) {
              ontvangen_SMD_string += (char) buf[idx];
              idx++;
            }
            ontvangen_SMD_string += "\n";

            publish(new String[] {ontvangen_SMD_string});
          } //  while (true)
        } // try
        catch (SocketException e) {
          String message = "[LAN] connection error (" + main.lan_ip_address + "): " + e;
          main.log_turbowin_system_message(message);
          connection_result = message;
        } catch (IOException e) {
          // System.out.println("Timeout. Client is closing.");
          String message = "[LAN] IO error (" + main.lan_ip_address + "): " + e;
          main.log_turbowin_system_message(message);
          connection_result = message;
        }
      } // if (!main.lan_ip_address.equals(""))
      else // no specific IP address was inserted
      {
        // no specific IP address was inserted, so now listing to Mintaka broadcasts
        try (DatagramSocket socket = new DatagramSocket(null)) // unbound!
        {
          // NB info:
          // https://download.java.net/java/early_access/jdk17/docs/api/java.base/java/net/DatagramSocket.html
          //          A DatagramSocket that is created with the intent of receiving multicast
          // datagrams should be created unbound.
          //          Before binding the socket, setReuseAddress(true) should be configured:
          //          (with DatagramSocket socket = new DatagramSocket(10110,
          // InetAddress.getByName("0.0.0.0")); it is bound directly, so no option for
          // setReuseAddress)

          socket.setReuseAddress(true);
          socket.bind(new InetSocketAddress(10110));

          // try (DatagramSocket socket = new DatagramSocket(10110,
          // InetAddress.getByName("0.0.0.0"));)
          // {
          String ontvangen_SMD_string = "";

          socket.setBroadcast(true);

          // System.out.println("Listen on " + socket.getLocalAddress() + " from " +
          // socket.getInetAddress() + " port " + socket.getBroadcast());
          byte[] buf = new byte[512];
          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          while (true) {
            ontvangen_SMD_string = "";
            // System.out.println("Waiting for data");
            socket.receive(packet);

            // System.out.println("Data received: ");

            int endidx = 0;
            for (; endidx < buf.length; endidx++) {
              if (buf[endidx] == '*') break;
            }

            endidx += 3;
            int idx = 0;
            for (int i = 0; i < endidx; i++) {
              ontvangen_SMD_string += (char) buf[idx];
              // System.out.print((char) buf[idx]);
              idx++;
            }
            ontvangen_SMD_string += "\n";
            // System.out.println();

            publish(new String[] {ontvangen_SMD_string});
          } //  while (true)
        } // try
        catch (SocketException e) {
          String message = "[WIFI] connection error: " + e;
          main.log_turbowin_system_message(message);
          connection_result = message;
        } catch (IOException e) {
          // System.out.println("Timeout. Client is closing.");
          String message = "[WIFI] IO error: " + e;
          main.log_turbowin_system_message(message);
          connection_result = message;
        }
      } // else (no specific IP address was inserted)

      return connection_result;
    } // protected String doInBackground() throws Exception

    @Override
    protected void process(List<String> data) {
      // process: Receives data chunks from the publish method asynchronously on the Event Dispatch
      // Thread.
      for (String ontvangen_SMD_string : data) {
        // NB altijd in for loop omdat meerdere ontbvangen_SMD_string's verzameld kunnen zijn
        // voordat het hier procesed wordt(inherent aan SwingWorker)
        // System.out.println("ontvangen_SMD_string = " + ontvangen_SMD_string);

        main.total_string = ontvangen_SMD_string;

        System.out.print(main.total_string);
        // System.out.println(total_string); // NB zo zie je ruwweg hoeveel bytes dat telkens
        // beschikbaar zijn

        RS232_write_sensor_data_to_file(); // PTB220, PTB330, MintakaDuo, Mintaka Star USB, Mintaka
        // Star WiFi

        // update main screen with application name + mode and date time last received data
        String info = "";
        if ((main_RS232_RS422.test_record.equals("")) || (main_RS232_RS422.test_record == null)) {
          info = "no data";
        } else {
          info = main_RS232_RS422.test_record;
        }

        if (!main.lan_ip_address.equals(
            "")) // specific IP address was inserted (Mintaka ENet box with fixed IP listening
        // address, introduced by Maersk fot next-gen firewalls)
        {
          main.jLabel4.setText(
              main.APPLICATION_NAME
                  + " "
                  + main.application_mode
                  + ", last received barometer data via LAN: "
                  + info);
        } else {
          main.jLabel4.setText(
              main.APPLICATION_NAME
                  + " "
                  + main.application_mode
                  + ", last received barometer data via WiFi: "
                  + info);
        }

        // for timer checking the data is not obsolate (see Function:
        // RS232_WiFi_init_new_barometer_received_check_timer)
        last_new_data_received_TimeMillis = System.currentTimeMillis();

        // update the date and time on the main screen (and the global date time var's)
        // NB if during start up the date and time displayed in the pupup message was not comfirmed
        // by the user
        //    it is assumed that the pc is not running on the correct time so then do not update the
        // date time automatically
        if (main.use_system_date_time_for_updating == true) {
          set_datetime_while_collecting_sensor_data();
        }

        // WOW enabled (and not 'only publish if manual obs was send') and a complete sensor data
        // string was received
        if ((main.WOW
                == true) /*&& (!main.WOW_reporting_interval.equals(main.WOW_REPORTING_INTERVAL_MANUAL))*/
            && (main.total_string.indexOf("\n") != -1)) {
          cal_WOW_systeem_datum_tijd =
              new GregorianCalendar(
                  new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
          cal_WOW_systeem_datum_tijd.getTime(); // now effective
          int WOW_system_minute_local = cal_WOW_systeem_datum_tijd.get(Calendar.MINUTE);
          cal_WOW_systeem_datum_tijd = null;

          // if (WOW_system_minute_local % Integer.valueOf(main.WOW_reporting_interval) == 0)
          // // e.g. every 10 minutes (WOW_reporting_interval);
          // NB avoid: retrieving sensor data exactly the same time as APR!!!! -> issue at whole
          // hours
          if ((WOW_system_minute_local - 1) % Integer.valueOf(main.WOW_reporting_interval)
              == 0) // e.g. every 10 minutes (WOW_reporting_interval);  e.g: 1, 11, 21, 31 etc min
          // past every hour
          {
            // logging
            String message = "[WOW] scheduled upload";
            main.log_turbowin_system_message(message);

            // checking
            boolean WOW_settings_ok = RS232_check_WOW_settings();

            if (WOW_settings_ok) {
              // read the barometer data which was saved before by TurboWin+ from a sensor data file
              if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR("WOW", retry);
              } else if (main.RS232_connection_mode == 4) // MintakaDuo
              {
                RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR("WOW", retry);
              } else if (main.RS232_connection_mode == 5
                  || main.RS232_connection_mode == 6) // Mintaka Star (USB or WiFi)
              {
                // RS232_Mintaka_Star_Read_And_Send_Sensor_Data_For_WOW_APR("WOW", retry);
                boolean StarX = false;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "WOW", retry, StarX);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX (USB or WiFi)
              {
                boolean StarX = true;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "WOW", retry, StarX);
              }
            } // if (WOW_settings_ok)
          }
        } // if ((main.WOW == true) &&
        // (!main.WOW_reporting_interval.equals(main.WOW_REPORTING_INTERVAL_MANUAL)) &&
        // (main.total_string.indexOf("\n") != -1))

        // APR (Automated Pressure Reporting)
        if ((main.APR == true) && (main.total_string.indexOf("\n") != -1)) {
          cal_APR_system_date_time =
              new GregorianCalendar(
                  new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
          cal_APR_system_date_time.getTime(); // now effective
          int APR_system_hour_local =
              cal_APR_system_date_time.get(
                  Calendar.HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at
          // 10:04:15.250 PM the HOUR_OF_DAY is 22.
          int APR_system_minute_local = cal_APR_system_date_time.get(Calendar.MINUTE);

          if ((APR_system_minute_local == 0)
              && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                  == 0)) // e.g. every 1 hour (APR_reporting_interval);
          {
            data_0_minutes_present =
                true; // to detemine if incoming sensor data from 00 minutes time is present,
            // because this is directly linked to sending/uploading of the obs
            retry = false;

            // logging
            String message = "[APR] scheduled upload";
            main.log_turbowin_system_message(message);

            // checking
            boolean only_2nd_instrument = false;
            boolean APR_settings_ok = RS232_check_APR_settings(only_2nd_instrument);
            boolean GPS_date_time_ok = true;

            if (main.RS232_connection_mode == 5
                || main.RS232_connection_mode == 6
                || main.RS232_connection_mode == 7
                || main.RS232_connection_mode == 8) // Mintaka Star or StarX  (USB or WiFi)
            {
              // NB Mintaka Star and StarX saved data do not contain date/time information so it is
              // not possible to check this
              //    so it is made always true
              GPS_date_time_ok = true;
            } else {
              GPS_date_time_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("APR");
            }

            if (GPS_date_time_ok == false) {
              main.Reset_all_meteo_parameters();
            }

            if (APR_settings_ok
                && GPS_date_time_ok) // GPS date-time was compared with system date-time
            {
              // NB in this stage we are sure the date-time is correct! + we are sure position is
              // available!

              // read the barometer data which was saved before by TurboWin+ from a sensor data file
              if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 4) // MintakaDuo
              {
                RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 5
                  || main.RS232_connection_mode == 6) // Mintaka Star (USB or WiFi)
              {
                // RS232_Mintaka_Star_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
                boolean StarX = false;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX (USB or WiFi)
              {
                boolean StarX = true;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              }
            } // if (APR_settings_ok && GPS_date_time_ok)
          } // if ((APR_system_minute_local == 0) etc.

          if ((data_0_minutes_present == false)
              && (APR_system_minute_local == 1)
              && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval) == 0)) {
            // NB sometimes the incoming data of exactly 0 minutes is not received, then also no obs
            // (server)upload / (email)sending
            //    so then try again at 1 minute past the (APR interval)hour

            retry = false;

            // logging
            String message = "[APR] scheduled upload";
            main.log_turbowin_system_message(message);

            // checking
            boolean only_2nd_instrument = false;
            boolean APR_settings_ok = RS232_check_APR_settings(only_2nd_instrument);
            boolean GPS_date_time_ok = true;

            if (main.RS232_connection_mode == 5
                || main.RS232_connection_mode == 6
                || main.RS232_connection_mode == 7
                || main.RS232_connection_mode == 8) // Mintaka Star or StarX  (USB or WiFi)
            {
              // NB Mintaka Star and StarX saved data do not contain date/time information so it is
              // not possible to check this
              //    so it is made always true
              GPS_date_time_ok = true;
            } else {
              GPS_date_time_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("APR");
            }

            if (GPS_date_time_ok == false) {
              main.Reset_all_meteo_parameters();
            }

            if (APR_settings_ok
                && GPS_date_time_ok) // GPS date-time was compared with system date-time
            {
              // NB in this stage we are sure the date-time is correct! + we are sure position is
              // available!

              // read the barometer data which was saved before by TurboWin+ from a sensor data file
              if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 4) // MintakaDuo
              {
                RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 5
                  || main.RS232_connection_mode == 6) // Mintaka Star (USB or WiFi)
              {
                // RS232_Mintaka_Star_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
                boolean StarX = false;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX (USB or WiFi)
              {
                boolean StarX = true;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              }
            } // if (APR_settings_ok && GPS_date_time_ok)
          } // if ( (data_0_minutes_present == false) && (APR_system_minute_local == 1) &&
          // (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval) == 0))

          // else if ((APR_server_response_code == main.RESPONSE_NO_INTERNET) &&
          // (APR_system_minute_local == APR_RETRY_MINUTES) && (APR_system_hour_local %
          // Integer.valueOf(main.APR_reporting_interval) == 0))   // e.g. every 1 hour
          // (APR_reporting_interval);
          if ((APR_server_response_code == main.RESPONSE_NO_INTERNET)
              && (APR_system_minute_local == APR_RETRY_MINUTES)
              && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                  == 0)) // e.g. every 1 hour (APR_reporting_interval);
          {
            retry = true;

            // logging
            String message = "[APR] retry scheduled upload";
            main.log_turbowin_system_message(message);

            // checking
            boolean only_2nd_instrument = false;
            boolean APR_settings_ok = RS232_check_APR_settings(only_2nd_instrument);
            boolean GPS_date_time_ok = true;

            if (main.RS232_connection_mode == 5
                || main.RS232_connection_mode == 6
                || main.RS232_connection_mode == 7
                || main.RS232_connection_mode == 8) // Mintaka Star or StarX (USB or WiFi)
            {
              // NB Mintaka Star and StarX saved data do not contain date/time information so it is
              // not possible to check this
              //    so it is made always true
              GPS_date_time_ok = true;
            } else {
              GPS_date_time_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("APR");
            }

            if (APR_settings_ok
                && GPS_date_time_ok) // GPS date-time was compared with system date-time
            {
              // NB in this stage we are sure the date-time is correct! + we are sure position is
              // available!

              // read the barometer data which was saved before by TurboWin+ from a sensor data file
              if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 4) // MintakaDuo
              {
                RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 5
                  || main.RS232_connection_mode == 6) // Mintaka Star (USB or WiFi)
              {
                // RS232_Mintaka_Star_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
                boolean StarX = false;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX (USB or WiFi)
              {
                boolean StarX = true;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              }
            } // if (APR_settings_ok && GPS_date_time_ok)

            // reset (to be sure)
            APR_server_response_code = 0;

          } // else if ((APR_server_response_code = main.RESPONSE_NO_INTERNET) etc.
          else if ((APR_server_response_code == main.RESPONSE_NO_INTERNET)
              && (APR_system_minute_local < APR_RETRY_MINUTES)
              && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                  == 0)) // e.g. every 1 hour (APR_reporting_interval);
          {
            // text will be visible only a few minutes (till the retry upload)
            main.jTextField4.setText(
                "[APR] retry automated pressure report upload within a few minutes");
          } else {
            // text with next APR update bottom status line TurboWin+ main screen (updated every
            // minute, directly linked to incoming sensor data of every minute)
            for (int i = (APR_system_hour_local + 1);
                i < (APR_system_hour_local + 1 + Integer.valueOf(main.APR_reporting_interval));
                i++) {
              // 24 hours or higher makes no difference for this computation
              if (i % Integer.valueOf(main.APR_reporting_interval) == 0) {
                cal_APR_system_date_time.add(
                    Calendar.HOUR_OF_DAY, i - APR_system_hour_local); // add 1 - 5 hours
                cal_APR_system_date_time.getTime();

                String next_APR_string =
                    "[APR] next automated meteo report upload: "
                        + cal_APR_system_date_time.get(Calendar.DAY_OF_MONTH)
                        + " "
                        + main.convert_month(cal_APR_system_date_time.get(Calendar.MONTH))
                        + " "
                        + cal_APR_system_date_time.get(Calendar.YEAR)
                        + " "
                        + cal_APR_system_date_time.get(Calendar.HOUR_OF_DAY)
                        + ".00 UTC";

                main.jTextField4.setText(next_APR_string);
                break;
              }
            } // for (int i = (APR_system_hour_local + 1); etc.
          } // else

          cal_APR_system_date_time = null;

          // reset (but not too early!)
          // if ( (APR_system_minute_local != 0) && (APR_system_minute_local != 1) )
          if (APR_system_minute_local != 0)
          // if (APR_system_minute_local >= 2 && APR_system_minute_local <= 59)
          {
            data_0_minutes_present = false;
          }
        } // if ( (main.APR == true) && (main.total_string.indexOf("\n") != -1))
      } //  for (String ontvangen_SMD_string: data)
    } // protected void process(List<String> data)

    @Override
    protected void done() {
      try {
        String result = get(); // get the return value of the doInBackground()
        if (!result.equals("")) // so there is a result/return message
        {
          // NB Avoiding interference with the 'Date-Time' openings messageBox (which is always on
          // top) !!
          //    Because they are both created in different threads. Therefore this construction
          // below
          //
          final JOptionPane pane_begin =
              new JOptionPane(
                  result,
                  JOptionPane.WARNING_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  null,
                  new Object[] {},
                  null);
          final JDialog lan_error_dialog = pane_begin.createDialog(main.APPLICATION_NAME);

          lan_error_dialog.setLocationRelativeTo(
              main.jLabel4); // jLabel4 had public access (private access will not do)

          Timer timer_begin =
              new Timer(
                  5000,
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      lan_error_dialog.dispose();
                    }
                  });
          timer_begin.setRepeats(false);
          timer_begin.start();
          lan_error_dialog.setVisible(true);
          lan_error_dialog.setModal(
              false); // setModal(true or false) seems not important here in this specific situation
        }
      } catch (InterruptedException | ExecutionException ex) {
        System.out.println("--- WiFi_Class_Receive_UDP error: " + ex);
      }
    } // protected void done()
  }

  private class Ethernet_Class_Receive_UDP extends SwingWorker<Void, String> {
    // AWS ethernet (LAN) only
    //
    // called from: - Ethernet_initComponents()
    //              - RS422_init_new_aws_data_received_check_timer()
    //
    //
    // NB OMC-140 LAN uses port 60004 and multicast ip adress 239.192.0.4
    // eg
    // https://examples.javacodegeeks.com/core-java/net/multicastsocket-net/java-net-multicastsocket-example/
    //    http://www.baeldung.com/java-broadcast-multicast
    //    https://en.wikipedia.org/wiki/Multicast_address
    //

    @Override
    protected Void doInBackground() throws Exception {
      final String INET_ADDR = "239.192.0.4"; // multicast IP group OMC-140 LAN
      final int PORT = 60004;

      // NB see below new implementation tested succesfully on 18-Oct-2021 (with "Com Port Data
      // Emulator" running on a second pc)

      NetworkInterface netIf = null;
      InetAddress mcastaddr = InetAddress.getByName(INET_ADDR);
      InetSocketAddress group = null;

      // network interface
      try {
        netIf = NetworkInterface.getByName(INET_ADDR);
      } catch (SocketException | NullPointerException e) {
        String message = "[AWS] connection error: " + e;
        main.log_turbowin_system_message(message);
      }

      // multicast group
      try {
        group = new InetSocketAddress(mcastaddr, PORT);
      } catch (IllegalArgumentException | SecurityException e) {
        String message = "[AWS] connection error: " + e;
        main.log_turbowin_system_message(message);
      }

      try (MulticastSocket socket_udp = new MulticastSocket(PORT)) {
        socket_udp.joinGroup(group, netIf);

        // byte[] buf = new byte[512
        byte[] buf =
            new byte
                [2048]; // NB 512 is not enough!! must cover the length of the whole $PEUMB string
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (true) {
          socket_udp.receive(packet);
          String ontvangen_SMD_string = new String(packet.getData(), 0, packet.getLength());

          publish(new String[] {ontvangen_SMD_string});
        } // while (true)
      } // try
      catch (SocketException e) {
        String message = "[AWS] connection error: " + e;
        main.log_turbowin_system_message(message);
      } catch (IOException e) {
        String message = "[AWS] IO error: " + e;
        main.log_turbowin_system_message(message);
      }

      return null;
    } // protected Void doInBackground() throws Exception

    @Override
    protected void process(List<String> data) {
      // process: Receives data chunks from the publish method asynchronously on the Event Dispatch
      // Thread.
      for (String ontvangen_SMD_string : data) {
        // NB altijd in for loop omdat meerdere ontbvangen_SMD_string's verzameld kunnen zijn
        // voordat het hier procesed wordt(inherent aan SwingWorker)
        // System.out.println("ontvangen_SMD_string = " + ontvangen_SMD_string);

        main.total_string = ontvangen_SMD_string;

        System.out.print(main.total_string);

        RS422_V3_write_sensor_data_to_file(); // NB also main.jLabel4 will be updated in this
        if (main.use_system_date_time_for_updating == true) {
          set_datetime_while_collecting_sensor_data();
        }
      } // for (String ontvangen_SMD_string: data)
    } // protected void process(List<String> data)

    @Override
    protected void done() {} // protected void done()
  }

  private void RS232_GPS_NMEA_0183_init_new_data_received_check_timer() {
    // called from: RS232_GPS_NMEA_0183_initComponents() [main_RS232_RS422.java]

    ActionListener check_new_data_action =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // NB currentTimeMillis(): returns the difference,in milliseconds, between the current
            // system time and midnight, January 1, 1970 UTC
            // NB last_new_GPS_data_received_TimeMillis: returns the difference,in milliseconds
            // between the last GPS data receipt system time and midnight, January 1, 1970 UTC.
            if ((System.currentTimeMillis() - last_new_GPS_data_received_TimeMillis)
                > MAX_AGE_GPS_DATA) // MAX_AGE_GPS_DATA e.g. 60000 msec (10 minutes)
            {
              // NB see RS232_GPS_NMEA_0183_RMC() and RS232_GPS_NMEA_0183_GCC()
              // [main_RS232_RS422.java] for setting of var "last_new_GPS_data_received_TimeMillis"

              // reset APTR dashboard vars
              myposition.COG_APR = Double.MAX_VALUE;
              myposition.SOG_APR = Double.MAX_VALUE;
              myposition.COG_APR_wind = Double.MAX_VALUE;
              myposition.SOG_APR_wind = Double.MAX_VALUE;

              // message obsolete GPS data only the first time
              if (main.obsolate_GPS_data_flag == false) {
                String message = "[GPS] data obsolete (lost connection?)";
                main.log_turbowin_system_message(message);

                // info text on (bottom) main screen
                main.jLabel6.setText(
                    main.APPLICATION_NAME
                        + " receiving GPS data via serial communication: stopped");

              } // if (main.obsolate_GPS_data_flag == false)
              else {
                // NB RS232_GPS listener restart every hour
                cal_RS232_GPS_system_date_time =
                    new GregorianCalendar(
                        new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
                cal_RS232_GPS_system_date_time.getTime(); // now effective
                int RS232_GPS_system_minute_local =
                    cal_RS232_GPS_system_date_time.get(Calendar.MINUTE);

                // cancel RS232_GPS listener thread
                if (RS232_GPS_system_minute_local == CANCEL_GPS_THREAD) {
                  RS232_GPS_worker.cancel(true);
                  String message = "[GPS] data listener thread cancelling";
                  main.log_turbowin_system_message(message);
                } // if (WiFi_system_minute_local == 30)

                // restart RS232_GPS listener thread
                if (RS232_GPS_system_minute_local == START_GPS_THREAD) {
                  // NB in case of power failure GPS com port is still ok (still open) but listener
                  // must be restarted
                  //    so we cannot use Function: RS232_GPS_NMEA_0183_Check_Serial_Ports(),
                  // bbecause then the present GPS default port, which is ok, will be reported as
                  // occupied!
                  //    so first Function: RS232_GPS_NMEA_0183_Check_Default_Serial_Port(), see
                  // below
                  RS232_GPS_NMEA_0183_Check_Default_Serial_Port(); // in this function::
                  // GPS_defaultPort will be set to
                  // null if present default GPS
                  // com port was not valid anymore

                  if (GPS_defaultPort == null) {
                    // checking/looking for the correct GPS com port
                    int completed_checks_serial_ports = 0;
                    GPS_defaultPort = null; // reset !
                    while ((GPS_defaultPort == null)
                        && (completed_checks_serial_ports < MAX_COMPLETED_GPS_PORT_CHECKS)) {
                      RS232_GPS_NMEA_0183_Check_Serial_Ports(completed_checks_serial_ports); // GPS
                      completed_checks_serial_ports++;
                    }

                    if (GPS_defaultPort == null) {
                      String message =
                          "[GPS] no GPS found (GPS_defaultPort = null); listener thread not restarted";
                      main.log_turbowin_system_message(message);
                    }
                  } // if (GPS_defaultPort == null)

                  if (GPS_defaultPort != null) {
                    main.GPS_serialPort = SerialPort.getCommPort(GPS_defaultPort);

                    // restart RS232_GPS listener thread
                    RS232_GPS_worker = new RS232_GPS_Class_Receive_Data();
                    RS232_GPS_worker.execute();
                    String message = "[GPS] data listener thread restarting";
                    main.log_turbowin_system_message(message);
                  } // if (GPS_defaultPort != null)
                } // if (RS232_gPS_system_minute_local == 33)
              } // else

              main.obsolate_GPS_data_flag = true;

              if (main.APR == true) {
                // update the position field on main screen, but only in APR mode
                // main.jTextField5.setForeground(main.obsolete_color_data_from_apr);
                main.position_fields_update();
              } // if (main.APR == true)

            } // if ((System.currentTimeMillis() - last_new_GPS_data_received_TimeMillis) >
            // MAX_AGE_GPS_DATA)
            else // so GPS data NOT obsolete (data < ? minutes old)
            {
              // reset message if before was written data obsolete
              if (main.obsolate_GPS_data_flag) {
                String message = "[GPS] start receiving data again";
                main.log_turbowin_system_message(message);

                // info text on (bottom) main screen
                main.jLabel6.setText(
                    main.APPLICATION_NAME + " receiving GPS data via serial communication.....");
              } // if (main.obsolate_GPS_data_flag)

              main.obsolate_GPS_data_flag = false;

              if (main.APR == true) {
                // update the position field on main screen, but only in APR mode
                String mode = "MINUTE_UPDATE"; // update main screen position every minute
                boolean GPS_date_time_ok =
                    RS232_GPS_NMEA_0183_Date_Position_Parsing(
                        mode); // also SOG_APR, COG_APR and if applicable SOG_APR_wind and
                // COG_APR_wind will be updated
                if (GPS_date_time_ok) {
                  main.position_fields_update();
                }
              } // if (main. APR == true)
            } // else (so GPS data NOT obsolate)
          } // public void actionPerformed(ActionEvent e)
        };

    // main loop for checking when new GPS data arrives
    check_new_GPS_data_timer_delay =
        DELAY_NEW_DATA_CHECK_LOOP; // delay between two new GPS data checks (e.g. 1 minute)
    check_new_GPS_data_timer = new Timer(check_new_GPS_data_timer_delay, check_new_data_action);
    check_new_GPS_data_timer.setRepeats(true); // false = only one action
    check_new_GPS_data_timer.setInitialDelay(
        INITIAL_DELAY_NEW_DATA_CHECK_LOOP); // time in millisec to wait after timer is started to
    // fire first event
    check_new_GPS_data_timer.setCoalesce(true); // to be sure
    check_new_GPS_data_timer.start();
  }

  private void RS232_And_WiFi_init_new_sensor_data_received_check_timer_II() {
    /////// for serial link/USB and WiFi connected thermometer  ///////

    // called from: RS232_initComponents_II()

    ActionListener check_new_data_action =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // System.out.println("+++ System.currentTimeMillis() = " + System.currentTimeMillis());
            // System.out.println("+++ last_new_data_received_TimeMillis = " +
            // last_new_data_received_TimeMillis);
            // System.out.println("+++ System.currentTimeMillis() -
            // last_new_data_received_TimeMillis = " + (System.currentTimeMillis() -
            // last_new_data_received_TimeMillis));

            // NB currentTimeMillis(): returns the difference,in milliseconds, between the current
            // system time and midnight, January 1, 1970 UTC
            // NB last_new_data_received_TimeMillis_II: returns the difference,in milliseconds
            // between the last thermometer data receipt system time and midnight, January 1, 1970
            // UTC.
            if ((System.currentTimeMillis() - last_new_data_received_TimeMillis_II)
                > MAX_AGE_AWS_DATA) // MAX_AGE_AWS_DATA e.g. 30000 msec (5 minutes), same for
            // barometer and thermometer data
            {
              main.displayed_thermometer_data_obsolete = true; // for DASHBOARD

              // MOET DEZE ACTIONLISTNER EXPLICIET GESTOPT WORDEN ALS EEN EXIT DOOR GEBRUIKER WORDT
              // GEGEVEN ????????? (check in console op vreemde foutmeldingen na afsluiten
              // TurboWin+)

              // message obsolete data only the first time
              if (main.obsolate_data_flag_II == false) {
                if (main.RS232_connection_mode_II == 1) // HMP155
                {
                  String message = "[THERMOMETER] sensor data obsolete (lost connection?)";
                  main.log_turbowin_system_message(message);
                }

              } // if (main.obsolate_data_flag_II == false)
              else // data obsolete but after > 5 minutes
              {
                if (main.RS232_connection_mode_II == 1) // HMP155
                {
                  // NB RS232 listener restart every hour
                  cal_RS232_system_date_time_II =
                      new GregorianCalendar(
                          new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
                  cal_RS232_system_date_time_II.getTime(); // now effective
                  int RS232_system_minute_local =
                      cal_RS232_system_date_time_II.get(Calendar.MINUTE);

                  // cancel RS232 listener thread
                  if (RS232_system_minute_local == CANCEL_THERMOMETER_THREAD) {
                    RS232_worker_II.cancel(
                        true); // Returns: false if the task could not be cancelled, typically
                    // because it has already completed normally; true otherwise
                    String message = "[THERMOMETER] sensor data listener thread cancelling";
                    main.log_turbowin_system_message(message);
                  }

                  // restart RS232 listener thread
                  if (RS232_system_minute_local == START_THERMOMETER_THREAD) {
                    // in case of power failure com port of connected thermometer is still ok (still
                    // open) but listener must be restarted
                    // NB so we cannot use Function: RS232_Check_Serial_Ports_8_II(because then the
                    // present default port, which is ok, will be reported as occupied!
                    //    so first Function: RS232_Check_Default_Serial_Port_II(), see below
                    RS232_Check_Default_Serial_Port_II(); // in this function:: main.defaultPort_II
                    // will be set to null if present default
                    // comm port II was not valid anymore

                    if (main.defaultPort_II != null) {
                      // NB expierenced during testing: at least for the PTB330 this (Function:
                      // RS232_Format_Barometer_Output_3()) is essential
                      //    same for HMP155 ???
                      RS232_Format_Thermometer_Output(); // to be sure, send format and start string
                      // to thermometer
                    }

                    if (main.defaultPort_II == null) {
                      // so default port II not valid anymore (most likely there was no power break)
                      // now first checking/looking for the correct com port
                      int completed_checks_serial_ports = 0;
                      while ((main.defaultPort_II == null)
                          && (completed_checks_serial_ports
                              < MAX_COMPLETED_THERMOMETER_PORT_CHECKS)) {
                        RS232_Check_Serial_Ports_8_II(completed_checks_serial_ports); // HMP155
                        completed_checks_serial_ports++;
                      }

                      // send serial commands to the thermometer
                      if (main.defaultPort_II != null) {
                        RS232_Format_Thermometer_Output(); // HMP155
                      } else {
                        String message =
                            "[THERMOMETER] no thermometer found (defaultPort_II = null); listener thread not restarted";
                        main.log_turbowin_system_message(message);
                      }
                    } // if (main.defaultPort_II == null)

                    if (main.defaultPort_II != null) {
                      main.serialPort_II = SerialPort.getCommPort(main.defaultPort_II);

                      // restart 2nd RS232 listener thread
                      RS232_worker_II = new RS232_Class_Receive_Sensor_Data_II();
                      RS232_worker_II.execute();
                      String message = "[THERMOMETER] sensor data listener thread restarting";
                      main.log_turbowin_system_message(message);
                    } // if (main.defaultPort_II != null)
                  } // if (RS232_system_minute_local == START_BAROMETER_THREAD)
                } // else if (main.RS232_connection_mode_II == 1)
              } // else (data obsolate but after > 5 minutes)

              main.obsolate_data_flag_II = true;

              if (main.APR == true) {
                // update the temp fields
                if (main.RS232_connection_mode_II == 1) // HMP155 conncted
                {
                  main.temperatures_fields_update();
                }
              } // if (main.APR == true)

            } // if ((System.currentTimeMillis() - last_new_data_received_TimeMillis_II) >
            // MAX_AGE_AWS_DATA)
            else // so sensor data NOT obsolete (data < 5 minutes old)
            {
              main.displayed_thermometer_data_obsolete = false; // for DASHBOARD

              // reset message if before was written data obsolete
              if (main.obsolate_data_flag_II) {
                if (main.RS232_connection_mode_II == 1) // HMP155
                {
                  String message = "[THERMOMETER] start receiving sensor data again";
                  main.log_turbowin_system_message(message);

                  // NB in the case of a PTB330 this could be a false message ("[BAROMETER] start
                  // receiving sensor data again";)
                  //    because if the power was off and on the PTB330 reports "PTB330 / 1.02" and
                  // the listener eports this as receiving data again
                  // same for thermometer HMP155 ?
                }
              } // if (main.obsolate_data_flag_II)

              main.obsolate_data_flag_II = false;

              if (main.APR == true) {
                if (main.RS232_connection_mode_II == 1) // HMP155 connected
                {
                  String destination = "MAIN_SCREEN";
                  boolean retry =
                      false; // NB 'retry' will not be used if destination == "MAIN_SCREEN"

                  RS232_vaisala.RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR(
                      destination, retry);
                  // main.temperatures_fields_update();                       // update the temp
                  // fields on main screen (but only in APR mode)
                } // if (main.RS232_connection_mode_II == 1)
              } // if (main. APR == true)
            } // else

            // initialisation
            // last_new_data_received_TimeMillis_II = 0;

          } // public void actionPerformed(ActionEvent e)
        };

    /* main loop for checking when new thermometer data arrives */
    check_new_data_timer_delay_II =
        DELAY_NEW_DATA_CHECK_LOOP; // delay between two new aws data checks (e.g. 1 minute)
    check_new_data_timer_II = new Timer(check_new_data_timer_delay_II, check_new_data_action);
    check_new_data_timer_II.setRepeats(true); // false = only one action
    check_new_data_timer_II.setInitialDelay(
        INITIAL_DELAY_NEW_DATA_CHECK_LOOP); // time in millisec to wait after timer is started to
    // fire first event
    check_new_data_timer_II.setCoalesce(true); // to be sure
    check_new_data_timer_II.start();
  }

  private void RS232_And_WiFi_init_new_sensor_data_received_check_timer() {
    /////// for serial link and WiFi connected barometers (NB not EUCAWS) ////////

    // called from: RS232_initComponents()
    //              WiFi_initComponents()

    ActionListener check_new_data_action;
    check_new_data_action =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if ((System.currentTimeMillis() - last_new_data_received_TimeMillis)
                > MAX_AGE_AWS_DATA) // MAX_AGE_AWS_DATA e.g. 30000 msec (5 minutes), same for
            // barometer data
            {
              main.displayed_barometer_data_obsolate = true; // for DASHBOARD

              // MOET DEZE ACTIONLISTNER EXPLICIET GESTOPT WORDEN ALS EEN EXIT DOOR GEBRUIKER WORDT
              // GEGEVEN ????????? (check in console op vreemde foutmeldingen na afsluiten
              // TurboWin+)

              // message obsolete data only the first time
              if (main.obsolate_data_flag == false) {
                if (main.RS232_connection_mode == 6
                    || main.RS232_connection_mode == 8) // Mintaka Star WiFi or Mintaka StarX Wifi
                {
                  String message = "";
                  if (!main.lan_ip_address.equals(
                      "")) // lan_ip_address available (eg Mintaka ENet box with fixed ip address to
                  // listen to)
                  {
                    message = "[LAN] sensor data obsolete (lost connection?)";
                    main.log_turbowin_system_message(message);
                  } else {
                    message = "[WIFI] sensor data obsolete (lost connection?)";
                    main.log_turbowin_system_message(message);
                  }
                } else if (main.RS232_connection_mode == 1
                    || main.RS232_connection_mode == 2
                    || main.RS232_connection_mode == 4
                    || main.RS232_connection_mode
                        == 5) // PTB220, PTB330, Mintaka Duo, Mintaka Star USB
                {
                  String message = "[BAROMETER] sensor data obsolete (lost connection?)";
                  main.log_turbowin_system_message(message);
                }
              } // if (main.obsolate_data_flag == false)
              else // data obsolete but after > 5 minutes
              {
                if (main.RS232_connection_mode == 6
                    || main.RS232_connection_mode == 8) // Mintaka Star WiFi or Mintaka StarX Wifi
                {
                  // do nothing the opened UDP port (e.g. 0.0.0.0:60004) will alway stay open as
                  // long as TurboWin+/Web is running (Windows power shell command "netstat -aon")
                  /*
                  // NB WiFi listener restart every hour
                  cal_WiFi_system_date_time = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));   // system date time in UTC of this moment
                  cal_WiFi_system_date_time.getTime();                                               // now effective
                  int WiFi_system_minute_local = cal_WiFi_system_date_time.get(Calendar.MINUTE);

                  // cancel WiFi listener thread
                  if (WiFi_system_minute_local == CANCEL_BAROMETER_THREAD)
                  {
                  WiFi_worker.cancel(true);
                  //WiFi_worker = null;     // NB null do not prevent a Bindexception,;
                  // NB2 a Bindexception is not an issue because it means TurboWin+ is still bind to (UDP)WiFi,
                  //     it would be an issue if it was bind to onother process/application
                  String message = "[WIFI] sensor data listener thread cancelling";
                  main.log_turbowin_system_message(message);
                  }

                  // restart WiFi listener thread
                  if (WiFi_system_minute_local == START_BAROMETER_THREAD)
                  {
                  WiFi_worker = new WiFi_Class_Receive_UDP();
                  WiFi_worker.execute();
                  String message = "[WIFI] sensor data listener thread restarting";
                  main.log_turbowin_system_message(message);
                  }
                  */
                } //  if (main.RS232_connection_mode == 6)
                else if (main.RS232_connection_mode == 1
                    || main.RS232_connection_mode == 2
                    || main.RS232_connection_mode == 4
                    || main.RS232_connection_mode == 5
                    || main.RS232_connection_mode
                        == 7) // PTB220, PTB330, Mintaka Duo, Mintaka Star USB, Mintaka StarX USB
                {
                  // NB RS232 listener restart every hour
                  cal_RS232_system_date_time =
                      new GregorianCalendar(
                          new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
                  cal_RS232_system_date_time.getTime(); // now effective
                  int RS232_system_minute_local = cal_RS232_system_date_time.get(Calendar.MINUTE);

                  // cancel RS232 listener thread
                  if (RS232_system_minute_local == CANCEL_BAROMETER_THREAD) {
                    RS232_worker.cancel(
                        true); // Returns: false if the task could not be cancelled, typically
                    // because it has already completed normally; true otherwise
                    String message = "[BAROMETER] sensor data listener thread cancelling";
                    main.log_turbowin_system_message(message);
                  }

                  // restart RS232 listener thread
                  if (RS232_system_minute_local == START_BAROMETER_THREAD) {
                    // in case of power failure com port of connected barometer is still ok (still
                    // open) but listener must be restarted
                    // NB so we cannot use Function: RS232_Check_Serial_Ports_8(because then the
                    // present default port, which is ok, will be reported as occupied!
                    //    so first Function: RS232_Check_Default_Serial_Port(), see below
                    RS232_Check_Default_Serial_Port(); // in this function:: main.defaultPort will
                    // be set to null if present default comm
                    // port was not valid anymore

                    if (main.defaultPort != null) {
                      // NB expierenced during testing: at least for the PTB330 this (Function:
                      // RS232_Format_Barometer_Output_3()) is essential
                      RS232_Format_Barometer_Output_3(); // to be sure, send format and start string
                      // to barometer
                    }

                    if (main.defaultPort == null) {
                      // so default port not valid anymore (most likely there was no power break)
                      // now first checking/looking for the correct com port
                      int completed_checks_serial_ports = 0;
                      while ((main.defaultPort == null)
                          && (completed_checks_serial_ports
                              < MAX_COMPLETED_BAROMETER_PORT_CHECKS)) {
                        RS232_Check_Serial_Ports_8(
                            completed_checks_serial_ports); // PTB220, PTB330, MintakaDuo,
                        // MintakaStar USB
                        completed_checks_serial_ports++;
                      }

                      // send Vaisala/Mintaka serial commands to the barometer
                      if (main.defaultPort != null) {
                        RS232_Format_Barometer_Output_3(); // PTB220, PTB330, MintakaDuo,
                        // MintakaStar USB
                      } else {
                        String message =
                            "[BAROMETER] no barometer found (defaultPort = null); listener thread not restarted";
                        main.log_turbowin_system_message(message);
                      }
                    } // if (main.defaultPort == null)

                    if (main.defaultPort != null) {
                      main.serialPort = SerialPort.getCommPort(main.defaultPort);

                      // restart RS232 listener thread
                      RS232_worker = new RS232_Class_Receive_Sensor_Data();
                      RS232_worker.execute();
                      String message = "[BAROMETER] sensor data listener thread restarting";
                      main.log_turbowin_system_message(message);
                    } // if (main.defaultPort != null)
                  } // if (RS232_system_minute_local == START_BAROMETER_THREAD)
                } // else if (main.RS232_connection_mode == 1 etc.
              } // else (data obsolate but after > 5 minutes)

              main.obsolate_data_flag = true;

              if ((main.RS232_connection_mode == 5)
                  || (main.RS232_connection_mode == 6)
                  || (main.RS232_connection_mode == 7)
                  || (main.RS232_connection_mode
                      == 8)) // Mintaka Star USB / Mintaka Srar Wifi/ Star + StarX USB/ Star + StarX
              // WiFi
              {
                // Star with integrated GPS
                main.obsolate_GPS_data_flag = true;
              }

              if (main.APR == true) {
                // update the air pressure reading / MSL fields on main screen, but only in APR mode
                main.barometer_fields_update();
                main.barograph_fields_update();

                // update the position fields
                if ((main.RS232_connection_mode == 5)
                    || (main.RS232_connection_mode == 6)
                    || (main.RS232_connection_mode == 7)
                    || (main.RS232_connection_mode
                        == 8)) // Mintaka Star USB / Mintaka Srar Wifi/ Star + StarX USB/ Star +
                // StarX WiFi
                {
                  // Star with integrated GPS
                  main.position_fields_update();
                }

                // update the barograph fields (pressure tendency and characteristic)
                // if ((main.RS232_connection_mode == 5) || (main.RS232_connection_mode == 6) ||
                // (main.RS232_connection_mode == 7) || (main.RS232_connection_mode == 8)) //
                // Mintaka Star USB / Mintaka Srar Wifi/ Star + StarX USB/ Star + StarX WiFi
                // {
                //   main.barograph_fields_update();
                // }

                // update the temp fields
                if ((main.RS232_connection_mode_II == 1)
                    || ((main.RS232_connection_mode == 7)
                        || (main.RS232_connection_mode == 8))) // HMP155 or StarX connected
                {
                  main.temperatures_fields_update();
                }
              } // if (main.APR == true)
            } else // so sensor data NOT obsolete (data < x minutes old)
            {
              main.displayed_barometer_data_obsolate = false; // for DASHBOARD

              // reset message if before was written data obsolete
              if (main.obsolate_data_flag) {
                if (main.RS232_connection_mode == 6
                    || main.RS232_connection_mode == 8) // Mintaka Star WiFi or StarX
                {
                  String message = "";
                  if (!main.lan_ip_address.equals(
                      "")) // lan_ip_address available (eg Mintaka ENet box with fixed ip address to
                  // listen to)
                  {
                    message = "[LAN] sensor data receiving again";
                    main.log_turbowin_system_message(message);
                  } else {
                    message = "[WIFI] sensor data receiving again";
                    main.log_turbowin_system_message(message);
                  }
                } else if (main.RS232_connection_mode == 1
                    || main.RS232_connection_mode == 2
                    || main.RS232_connection_mode == 4
                    || main.RS232_connection_mode
                        == 5) // PTB220, PTB330, Mintaka Duo, Mintaka Star USB
                {
                  String message = "[BAROMETER] start receiving sensor data again";
                  main.log_turbowin_system_message(message);

                  // NB in the case of a PTB330 this could be a false message ("[BAROMETER] start
                  // receiving sensor data again";)
                  //    because if the power was off and on the PTB330 reports "PTB330 / 1.02" and
                  // the listener eports this as receiving data again
                }
              } // if (main.obsolate_data_flag)

              main.obsolate_data_flag = false;

              if ((main.RS232_connection_mode == 5)
                  || (main.RS232_connection_mode == 6)
                  || (main.RS232_connection_mode == 7)
                  || (main.RS232_connection_mode
                      == 8)) // Mintaka Star USB / Mintaka Srar Wifi/ Star + StarX USB/ Star + StarX
              // WiFi
              {
                // Star with integrated GPS
                main.obsolate_GPS_data_flag = false;
              }

              if (main.APR == true) {
                String destination = "MAIN_SCREEN";
                boolean retry =
                    false; // NB 'retry' will not be used if destination == "MAIN_SCREEN"

                if ((main.RS232_connection_mode == 1)
                    || (main.RS232_connection_mode == 2)) // Vaisala PTB330 or PTB220
                {
                  RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR(
                      destination, retry);
                } else if (main.RS232_connection_mode == 4) // Mintaka Duo
                {
                  RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR(
                      destination, retry);
                } else if ((main.RS232_connection_mode == 5)
                    || (main.RS232_connection_mode == 6)) // Mintaka Star USB / Mintaka Star WiFi
                {
                  boolean StarX = false;
                  RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                      destination, retry, StarX);
                } else if ((main.RS232_connection_mode == 7)
                    || (main.RS232_connection_mode
                        == 8)) // Mintaka Star USB + SrarX / Mintaka Star WiFi + StarX
                {
                  boolean StarX = true;
                  RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                      destination, retry, StarX);
                }
              } // if (main. APR == true)

              // NB in this case: in function: RS422_Read_AWS_Sensor_Data_For_Display() the fields
              // on the main screen will be updated
              // NB main.date_time_fields_update() etc. and VOT will be updated there
            }

            // initialisation
            // last_new_data_received_TimeMillis = 0;

            // APR: for updating message "visual data adding is yes/no advised" (jLabel39)
            //
            if (main.APR) {
              if (main.obsolate_data_flag) {
                main.jLabel39.setText("");
              } else {
                cal_APR_system_dt =
                    new GregorianCalendar(
                        new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
                cal_APR_system_dt.getTime(); // now effective
                int APR_system_hour =
                    cal_APR_system_dt.get(
                        Calendar
                            .HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at
                // 10:04:15.250 PM the HOUR_OF_DAY is 22.
                int APR_system_minutes = cal_APR_system_dt.get(Calendar.MINUTE);

                if (((APR_system_hour + 1) % Integer.valueOf(main.APR_reporting_interval)) == 0) {
                  // NB currentTimeMillis(): returns the difference,in milliseconds, between the
                  // current system time and midnight, January 1, 1970 UTC
                  // NB cal_APR_system_date_time.computeTime(): Converts calendar field values to
                  // the time value (millisecond offset from the Epoch).( Epoch, January 1, 1970
                  // 00:00:00.000 GMT (Gregorian))

                  int APR_minutes_for_upload = 60 - APR_system_minutes;
                  if (APR_minutes_for_upload <= 30) {
                    // less the 30 minutes (= 1800000 msec) before next APR upload
                    // NB "-1" too be sure, thread is always behind
                    main.jLabel39.setForeground(new Color(0, 153, 0)); // dark green
                    main.jLabel39.setText(
                        "--- "
                            + (APR_minutes_for_upload - 1)
                            + " minutes to go before next automated upload, you can now add observation data ---");
                  } else {
                    main.jLabel39.setForeground(Color.RED);
                    main.jLabel39.setText(
                        "--- more than 30 minutes to go before next automated upload, please do not add observation data --- ");
                  }
                } // if ( ((APR_system_hour + 1) % Integer.valueOf(main.APR_reporting_interval)) ==
                // 0)
                else {
                  main.jLabel39.setForeground(Color.RED);
                  main.jLabel39.setText(
                      "--- more than 30 minutes to go before next automated upload, please do not add observation data --- ");
                } // else
              } // no obsolete data
            } // if (main.APR)

            // APR: for system invoked pop-up screen (add visual observation) or system 'delete'
            // pop-up screen (but only in APR mode)
            //
            //////// BEGIN TEST //////
            // main.APR = true;
            // main.pop_up_screen = true;
            // main.pop_up_screen_interval = "1";
            //////// END TEST ///////

            if (main.APR && main.pop_up_screen) {
              Activate_Pop_Up_Screen_APR_AWS(); // pop-up screen reminder add visual obs
            } // if (main.APR && main.pop_up_screen)
          } // public void actionPerformed(ActionEvent e)
        };

    // main loop for checking when new AWS data arrives
    check_new_data_timer_delay =
        DELAY_NEW_DATA_CHECK_LOOP; // delay between two new aws data checks (e.g. 1 minute)
    check_new_data_timer = new Timer(check_new_data_timer_delay, check_new_data_action);
    check_new_data_timer.setRepeats(true); // false = only one action
    check_new_data_timer.setInitialDelay(
        INITIAL_DELAY_NEW_DATA_CHECK_LOOP); // time in millisec to wait after timer is started to
    // fire first event
    check_new_data_timer.setCoalesce(true); // to be sure
    check_new_data_timer.start();
  }

  private void Activate_Pop_Up_Screen_APR_AWS() {
    // called from: RS232_And_WiFi_init_new_sensor_data_received_check_timer()
    // [main_RS232_RS422.java]
    //              RS422_init_new_aws_data_received_check_timer() [main_RS232_RS422.java]
    //

    cal_APR_AWS_system_dt =
        new GregorianCalendar(
            new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
    cal_APR_AWS_system_dt.getTime(); // now effective
    int APR_AWS_system_hour =
        cal_APR_AWS_system_dt.get(
            Calendar
                .HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250
    // PM the HOUR_OF_DAY is 22.
    int APR_AWS_system_minutes = cal_APR_AWS_system_dt.get(Calendar.MINUTE);

    if (((APR_AWS_system_hour + 1) % Integer.valueOf(main.pop_up_screen_interval)) == 0) {
      int APR_AWS_minutes_before_screen_pop_up =
          60 - APR_AWS_system_minutes; // e.g. 60 - 55 = 5 minutes
      if (APR_AWS_minutes_before_screen_pop_up
          <= main.APR_AWS_SCREEN_POP_UP_MINUTES_TO_HOUR) // e.g. APR_SCREEN_POP_UP_MINUTES_TO_HOUR =
      // 10
      {
        // SYSTEM INVOKED SCREEN POP-UP TIME!
        //
        // NB less then xx minutes before whole hour; whole hour could be every hour, intermediate
        // hours or main hours
        //    (depends on variable setting: pop_up_screen_interval [function:
        // OK_button_actionPerformed() in WOW_APR_settings.java])
        // NB keep in mind, thread is always slightly 'behind' in time

        in_screen_pop_up_period = true;

        if ((main.pop_up_form == null)
            && (main.screen_was_manually_closed_in_pop_up_period == false)
            && (main.turbowin_start_up_sequence_finished == true)) {
          try {
            main.pop_up_form = new pop_up_screen(in_screen_pop_up_period);
          } catch (IOException ex) {
            /* ... */
          }
          main.pop_up_form.setVisible(true);
          // NB main.pop_up_form.setAlwaysOnTop(true); : could block the TurboWIn+ app is another
          // messageBox requires some action (like OK button to indicate that APR not valid etc.)
        }
      } // if (APR_AWS_minutes_before_screen_pop_up <= main.APR_AWS_SCREEN_POP_UP_MINUTES_TO_HOUR)
    } // if (((APR_AWS_system_hour + 1) % Integer.valueOf(main.pop_up_screen_interval)) == 0)

    int APR_AWS_minutes_before_whole_hour = 60 - APR_AWS_system_minutes; // e.g. 60 - 2 = 58
    if (APR_AWS_minutes_before_whole_hour > main.APR_AWS_SCREEN_POP_UP_MINUTES_TO_HOUR) {
      // SYSTEM INVOKED SCREEN POP-UP DELETE TIME!

      in_screen_pop_up_period = false;

      if (main.pop_up_form != null) {
        main.pop_up_form.dispose();
        main.pop_up_form = null;
      }

      // set this flag always to false if outside 'screen pop-up period'
      main.screen_was_manually_closed_in_pop_up_period = false;
    }
  }

  public void WiFi_initComponents() {
    // called from specific_connection_initComponents() [main.java]

    // for sensor data files (in the file name itself) (note also used in RS232_view.java)
    main.sdf3 =
        new SimpleDateFormat(
            "yyyyMMddHH"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf3.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for date-time strings/remarks in the sensor data records (note also used in RS232_view.java)
    main.sdf4 =
        new SimpleDateFormat(
            "yyyyMMddHHmm"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf4.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for pop-up air pressure when clicking tray icon (TurboWin+ minimized/iconfied)
    sdf8 = new SimpleDateFormat("dd MMM yyyy HH.mm");
    sdf8.setTimeZone(TimeZone.getTimeZone("UTC")); // <----- DEZE = ESSENTIEEL

    // dir for storage sensor data
    if ((main.logs_dir == null) || (main.logs_dir.compareTo("") == 0)) {
      String info =
          "logs folder (for sensor data storage) unknown, select: Maintenance -> Log files settings";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
    }

    // delete old sensor data files (whether or not a barometer was found)
    main.log_turbowin_system_message("[GENERAL] deleting old sensor data files");
    RS232_Delete_Sensor_Data_Files();

    // check wifi connection
    // WiFi_Check_Connection_b();

    // WiFi_Receive_UDP();
    // WiFi_Class_Receive_UDP.execute();
    WiFi_worker = new WiFi_Class_Receive_UDP();
    WiFi_worker.execute();

    String message = "";
    if (!main.lan_ip_address.equals(
        "")) // user inserted a custom ip address (e.g. 192.168.4.1) TurboWin+ should listening to
    // (Mintaka ENet box in Maersk mode -fixed ip address-)
    {
      message = "[LAN] start listening on " + main.lan_ip_address;
    } else {
      message = "[WIFI] start listening";
    }

    main.log_turbowin_system_message(message);

    // start timer for checking every 1 minute if there is new barometer sensor data available
    // (because if last data > 5 minutes old 'gray' data on main screen)
    // NB invoking after: WiFi_worker = new WiFi_Class_Receive_UDP(); and WiFi_worker.execute();
    last_new_data_received_TimeMillis = 0;
    RS232_And_WiFi_init_new_sensor_data_received_check_timer();
  }

  public void Ethernet_initComponents() {
    // called from specific_connection_initComponents() [main.java]

    // for sensor data files (in the file name itself) (note also used in RS232_view.java)
    main.sdf3 =
        new SimpleDateFormat(
            "yyyyMMddHH"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf3.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for date-time strings/remarks in the sensor data records (note also used in RS232_view.java)
    main.sdf4 =
        new SimpleDateFormat(
            "yyyyMMddHHmm"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf4.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for function: RS422_write_sensor_data_to_file() and RS422_V3_write_sensor_data_to_file()
    sdf7 = new SimpleDateFormat("ss");
    sdf7.setTimeZone(TimeZone.getTimeZone("UTC"));

    // dir for storage sensor data
    if ((main.logs_dir == null) || (main.logs_dir.compareTo("") == 0)) {
      String info =
          "logs folder (for sensor data storage) unknown, select: Maintenance -> Log files settings";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
    }

    // delete old sensor data files (whether or not a barometer was found)
    main.log_turbowin_system_message("[GENERAL] deleting old sensor data files");
    RS232_Delete_Sensor_Data_Files();

    Ethernet_worker = new Ethernet_Class_Receive_UDP();
    Ethernet_worker.execute();

    String message = "[AWS] start listening";
    main.log_turbowin_system_message(message);

    // set start-up sequence finished flag
    // main.turbowin_start_up_sequence_finished = true;

    // start timer for checking every 1 minute if there is new AWS data available (because if last
    // data > 5 minutes old 'gray' data on main screen)
    // NB invoking after: Ethernet_worker = new Ethernet_Class_Receive_UDP(); and
    // Ethernet_worker.execute();
    last_new_data_received_TimeMillis = 0;
    RS422_init_new_aws_data_received_check_timer();
  }

  private class RS232_Class_Receive_Sensor_Data_II extends SwingWorker<String, String> {
    boolean retry = false;

    @Override
    protected String doInBackground() throws Exception {
      main.serialPort_II.openPort();
      if (main.serialPort_II.isOpen()) {
        main.serialPort_II.setComPortParameters(
            main.bits_per_second_II, main.data_bits_II, main.stop_bits_II, main.parity_II);
        main.serialPort_II.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
      } // if (serialPort_II.isOpen())
      else {
        System.out.println(
            "+++ " + "[THERMOMETER] Couldn't open " + main.serialPort_II.getDescriptivePortName());
      }

      if (!TurboWinAppInfo.APPLICATION_VERSION.contains("[64-bit]")) // so 32-bit
      {
        String message = "[THERMOMETER] " + "using 32-bit receive method";
        main.log_turbowin_system_message(message);

        if (main.serialPort_II.isOpen()) {
          main.serialPort_II.addDataListener(
              new SerialPortDataListener() {
                // NB IMPLEMENTATION BELOW WORKS IN THE JPMS (64 bit) version  and on 32 bit (but
                // the other method works better on 64bit) !!!!

                @Override
                public int getListeningEvents() {
                  return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                  String ontvangen_SMD_string = "";

                  if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                  }

                  int numRead = 0;
                  byte[] newData = null;
                  try {
                    newData = new byte[main.serialPort_II.bytesAvailable()];
                    numRead = main.serialPort_II.readBytes(newData, newData.length);
                  } catch (NegativeArraySizeException ex) {
                    numRead = -1;
                    // NB don't modify the GUI anywhere except for on the event-dispatch-thread.!!
                    // (so not here in the function doinBackground())
                  }

                  if (numRead > 0) {
                    ontvangen_SMD_string = new String(newData, StandardCharsets.UTF_8);

                    // System.out.println("Read " + numRead + " bytes.");

                    // publish: Sends data chunks to the process(java.util.List) method. This method
                    // is to be used from inside the doInBackground method to deliver intermediate
                    // results for processing on the Event Dispatch Thread inside the process
                    // method.
                    //          Because the process method is invoked asynchronously on the Event
                    // Dispatch Thread  multiple invocations to the publish method might occur
                    // before the process method is executed. For performance purposes all these
                    // invocations are coalesced into one invocation with concatenated arguments.
                    //
                    // For example:
                    //
                    // publish("1");
                    // publish("2", "3");
                    // publish("4", "5", "6");
                    //
                    // might result in: process("1", "2", "3", "4", "5", "6")
                    //
                    publish(new String[] {ontvangen_SMD_string});
                  } // if (numRead > 0)
                  else if (numRead == -1) {
                    publish(new String[] {"interruption or excecution error\n"});
                  }
                } // public void serialEvent(SerialPortEvent event)
              });
        } // if (serialPort.isOpen())
      } // if (!TurboWinAppInfo.APPLICATION_VERSION.contains("[64-bit]"))
      else // 64-bit methode
      {
        // System.out.println("thermometer data via 64 bit method");
        String message = "[THERMOMETER] " + "using 64-bit receive method";
        main.log_turbowin_system_message(message);

        if (main.serialPort_II.isOpen()) {
          main.serialPort_II.addDataListener(
              new SerialPortDataListener() {
                // NB IMPLEMENTATION BELOW WORKS WELL IN THE JPMS (64 bit) version  but NOT on 32
                // bit !!!!

                @Override
                public int getListeningEvents() {
                  return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                  String ontvangen_SMD_string = "";
                  int numRead = 0;
                  byte[] newData = null;

                  try {
                    newData = event.getReceivedData();
                    numRead = newData.length;

                    // System.out.println("Received data of size: " + newData.length);
                    // for (int i = 0; i < newData.length; ++i)
                    //      System.out.print((char)newData[i]);
                    // System.out.println("\n");
                  } catch (NegativeArraySizeException ex) {
                    numRead = -1;
                    // NB don't modify the GUI anywhere except for on the event-dispatch-thread.!!
                    // (so not here in the function doinBackground())
                  }

                  if (numRead > 0) {
                    ontvangen_SMD_string = new String(newData, StandardCharsets.UTF_8);

                    // System.out.println("Read " + numRead + " bytes.");

                    // publish: Sends data chunks to the process(java.util.List) method. This method
                    // is to be used from inside the doInBackground method to deliver intermediate
                    // results for processing on the Event Dispatch Thread inside the process
                    // method.
                    //          Because the process method is invoked asynchronously on the Event
                    // Dispatch Thread  multiple invocations to the publish method might occur
                    // before the process method is executed. For performance purposes all these
                    // invocations are coalesced into one invocation with concatenated arguments.
                    //
                    // For example:
                    //
                    // publish("1");
                    // publish("2", "3");
                    // publish("4", "5", "6");
                    //
                    // might result in: process("1", "2", "3", "4", "5", "6")
                    //
                    publish(new String[] {ontvangen_SMD_string});
                  } // if (numRead > 0)
                  else if (numRead == -1) {
                    publish(new String[] {"interruption or excecution error\n"});
                  }
                } // public void serialEvent(SerialPortEvent event)
              });
        } // if (serialPort.isOpen())
      } // 64-bit methode

      return null;
    } // protected Void doInBackground() throws Exception

    @Override
    protected void process(List<String> data) {
      // process: Receives data chunks from the publish method asynchronously on the Event Dispatch
      // Thread.
      for (String ontvangen_SMD_string : data) {
        // NB altijd in for loop omdat meerdere ontbvangen_SMD_string's verzameld kunnen zijn
        // voordat het hier procesed wordt(inherent aan SwingWorker)
        // System.out.println("ontvangen_SMD_string = " + ontvangen_SMD_string);

        main.total_string_II = ontvangen_SMD_string;
        System.out.print(main.total_string_II);
        // System.out.println(total_string); // NB zo zie je ruwweg hoeveel bytes dat telkens
        // beschikbaar zijn

        // error logging
        if (main.total_string_II.indexOf("error") != -1) {

          String message = "[THERMOMETER] " + "interruption or execution exception";
          main.log_turbowin_system_message(message);
        } //  if (main.total_string_II.indexOf("error") != -1)

        // update main screen with application name + mode and date time last received data
        String info = "";
        if ((main_RS232_RS422.test_record_II.equals(""))
            || (main_RS232_RS422.test_record_II == null)) {
          info = "no data";
        } else {
          info = main_RS232_RS422.test_record_II;
        }

        //     if (main.RS232_connection_mode == 4 || main.RS232_connection_mode == 5 ||
        // main.RS232_connection_mode == 7)  // Mintaka Duo or Mintaka Star USB or StarX USB
        //     {
        //        main.jLabel4.setText(main.APPLICATION_NAME + " " + main.application_mode  + ",
        // last received barometer data via USB: " + info);
        //     }
        //     else
        //     {
        //        main.jLabel4.setText(main.APPLICATION_NAME + " " + main.application_mode  + ",
        // last received barometer data via serial communication: " + info);
        //     }
        main.jLabel41.setText(
            main.APPLICATION_NAME
                + " "
                + main.application_mode
                + ", last received thermometer data via USB: "
                + info);

        // save the received data to file
        RS232_write_sensor_data_to_file_II(); // HMP155

        // for timer checking the data is not obsolete; see Function:
        // RS232_And_WiFi_init_new_barometer_received_check_timer()[main_RS232_RS422.java]
        last_new_data_received_TimeMillis_II = System.currentTimeMillis();

        //  NB below only for thermometer if no barometer or AWS connected
        //     update the date and time on the main screen (and the global date time var's)
        //     if during start up the date and time displayed in the pupup message was not comfirmed
        // by the user
        //     it is assumed that the pc is not running on the correct time so then do not update
        // the date time automatically
        if (main.RS232_connection_mode == 0) // no 1st meteo instrument (barometer or AWS) connected
        {
          if (main.use_system_date_time_for_updating == true) {
            set_datetime_while_collecting_sensor_data();
          }

          // NB so only a 2nd meteo instrument; note: if there was also a first meteo instrument
          // then the APR updates goes to that first instrument !!
          if ((main.APR == true) && (main.total_string_II.indexOf("\n") != -1)) {
            cal_APR_system_date_time =
                new GregorianCalendar(
                    new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
            cal_APR_system_date_time.getTime(); // now effective
            int APR_system_hour_local =
                cal_APR_system_date_time.get(
                    Calendar.HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at
            // 10:04:15.250 PM the HOUR_OF_DAY is 22.
            int APR_system_minute_local = cal_APR_system_date_time.get(Calendar.MINUTE);

            if ((APR_system_minute_local == 0)
                && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                    == 0)) // e.g. every 1 hour (APR_reporting_interval);
            {
              retry = false;

              // logging
              String message = "[APR] scheduled upload";
              main.log_turbowin_system_message(message);

              // checking
              boolean only_2nd_instrument = true;
              boolean APR_settings_ok = RS232_check_APR_settings(only_2nd_instrument);
              boolean GPS_date_time_ok = true;

              // NB because here, in this branch, the first meteo instrument is 0 (not present) the
              // date time will always be retrieved from the NMEA GPS
              GPS_date_time_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("APR");

              if (GPS_date_time_ok == false) {
                main.Reset_all_meteo_parameters();
              }

              if (APR_settings_ok
                  && GPS_date_time_ok) // GPS date-time was compared with system date-time
              {
                // NB in this stage we are sure the date-time is correct! + we are sure position is
                // available!

                // read the thermometer data which was saved before by TurboWin+ from a sensor data
                // II file
                RS232_vaisala.RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR(
                    "APR", retry);
              } // if (APR_settings_ok && GPS_date_time_ok)
            } // if ((APR_system_minute_local == 0) etc.
            else if ((APR_server_response_code == main.RESPONSE_NO_INTERNET)
                && (APR_system_minute_local == APR_RETRY_MINUTES)
                && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                    == 0)) // e.g. every 1 hour (APR_reporting_interval);
            {
              // NB retry option will only be used in case of uploading to server (not if uloading
              // by email, in case email will never enter this code paragraph)

              retry =
                  true; // only used if automatically "to server" not for automatically email upload

              // logging
              String message = "[APR] retry scheduled upload";
              main.log_turbowin_system_message(message);

              // checking
              boolean only_2nd_instrument = true;
              boolean APR_settings_ok = RS232_check_APR_settings(only_2nd_instrument);
              boolean GPS_date_time_ok = true;

              // NB because here, in this branch, the first meteo instrument is 0 (not present) the
              // date time will always be retrieved from the NMEA GPS
              GPS_date_time_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("APR");

              if (APR_settings_ok
                  && GPS_date_time_ok) // GPS date-time was compared with system date-time
              {
                // NB in this stage we are sure the date-time is correct! + we are sure position is
                // available!

                // read the thermometer data which was saved before by TurboWin+ from a sensor data
                // II file
                RS232_vaisala.RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR(
                    "APR", retry);
              } // if (APR_settings_ok && GPS_date_time_ok)

              // reset (to be sure)
              APR_server_response_code = 0;

            } // else if ((APR_server_response_code = main.RESPONSE_NO_INTERNET) etc.
            else if ((APR_server_response_code == main.RESPONSE_NO_INTERNET)
                && (APR_system_minute_local < APR_RETRY_MINUTES)
                && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                    == 0)) // e.g. every 1 hour (APR_reporting_interval);
            {
              // NB retry option will only be used in cse of uploading to server (not if uloading by
              // email, in case email will never enter this code paragraph)

              // text will be visible only a few minutes (till the retry upload)
              main.jTextField4.setText(
                  "[APR] retry automated pressure report upload within a few minutes");
            } else {
              // text with next APR update bottom status line TurboWin+ main screen (updated every
              // minute)
              for (int i = (APR_system_hour_local + 1);
                  i < (APR_system_hour_local + 1 + Integer.valueOf(main.APR_reporting_interval));
                  i++) {
                // 24 hours or higher makes no difference for this computation
                if (i % Integer.valueOf(main.APR_reporting_interval) == 0) {
                  cal_APR_system_date_time.add(
                      Calendar.HOUR_OF_DAY, i - APR_system_hour_local); // add 1 - 5 hours
                  cal_APR_system_date_time.getTime();

                  String next_APR_string =
                      "[APR] next automated meteo report upload: "
                          + cal_APR_system_date_time.get(Calendar.DAY_OF_MONTH)
                          + " "
                          + main.convert_month(cal_APR_system_date_time.get(Calendar.MONTH))
                          + " "
                          + cal_APR_system_date_time.get(Calendar.YEAR)
                          + " "
                          + cal_APR_system_date_time.get(Calendar.HOUR_OF_DAY)
                          + ".00 UTC";

                  main.jTextField4.setText(next_APR_string);
                  break;
                }
              } // for (int i = (APR_system_hour_local + 1); etc.
            } // else

            cal_APR_system_date_time = null;
          } // if ( (main.APR == true) && (main.total_string.indexOf("\n") != -1))
        } // if (main.RS232_connection_mode == 0)
      } // for (String ontvangen_string : data)
    } // protected void process(List<String> data)
  }

  private class RS232_Class_Receive_Sensor_Data extends SwingWorker<String, String> {
    // called from RS232_initComponents()

    //  new SwingWorker<String, String>()
    //     {
    boolean retry = false;

    @Override
    protected String doInBackground() throws Exception {
      main.serialPort.openPort();
      if (main.serialPort.isOpen()) {
        // main.serialPort.openPort();               // NB will be closed in Function:
        // File_Exit_menu_actionPerformd() [main.java]
        // main.serialPort.setParams(main.bits_per_second, main.data_bits, main.stop_bits,
        // main.parity);
        // main.serialPort.setFlowControlMode(main.flow_control);
        main.serialPort.setComPortParameters(
            main.bits_per_second, main.data_bits, main.stop_bits, main.parity);
        main.serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

        // Preparing a mask. In a mask, we need to specify the types of events that we want to
        // track.
        // Well, for example, we need to know what came some data, thus in the mask must have the
        // following value: MASK_RXCHAR. If we, for example, still need to know about changes in
        // states
        // of lines CTS and DSR, the mask has to look like this: SerialPort.MASK_RXCHAR +
        // SerialPort.MASK_CTS + SerialPort.MASK_DSR
        // int mask = SerialPort.MASK_RXCHAR;

        // Set the prepared mask
        // main.serialPort.setEventsMask(mask);

      } // if (serialPort.isOpen())
      else {
        // System.out.println("+++ " + ex);
        System.out.println(
            "+++ " + "[BAROMETER] Couldn't open " + main.serialPort.getDescriptivePortName());
      }

      if (main.serialPort.isOpen()) {
        main.serialPort.addDataListener(
            new SerialPortDataListener() {
              @Override
              public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
              }

              @Override
              public void serialEvent(SerialPortEvent event) {
                String ontvangen_SMD_string = "";

                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                  return;
                }

                // byte[] newData = new byte[main.serialPort.bytesAvailable()];
                // int numRead = main.serialPort.readBytes(newData, newData.length);
                int numRead;
                byte[] newData = null;
                try {
                  newData = new byte[main.serialPort.bytesAvailable()];
                  numRead = main.serialPort.readBytes(newData, newData.length);
                } catch (NegativeArraySizeException ex) {
                  numRead = -1;
                  // NB don't modify the GUI anywhere except for on the event-dispatch-thread.!! (so
                  // not here in the function doinBackground())
                }

                if (numRead > 0) {
                  ontvangen_SMD_string = new String(newData, StandardCharsets.UTF_8);

                  // System.out.println("Read " + numRead + " bytes.");

                  // publish: Sends data chunks to the process(java.util.List) method. This method
                  // is to be used from inside the doInBackground method to deliver intermediate
                  // results for processing on the Event Dispatch Thread inside the process method.
                  //          Because the process method is invoked asynchronously on the Event
                  // Dispatch Thread  multiple invocations to the publish method might occur before
                  // the process method is executed. For performance purposes all these invocations
                  // are coalesced into one invocation with concatenated arguments.
                  //
                  // For example:
                  //
                  // publish("1");
                  // publish("2", "3");
                  // publish("4", "5", "6");
                  //
                  // might result in: process("1", "2", "3", "4", "5", "6")
                  //
                  publish(new String[] {ontvangen_SMD_string});
                } // if (numRead > 0)
                else if (numRead == -1) {
                  publish(new String[] {"interruption or excecution error\n"});
                }
              } // public void serialEvent(SerialPortEvent event)
            });
      } // if (serialPort.isOpen())

      return null;
    }

    @Override
    protected void process(List<String> data) {
      // process: Receives data chunks from the publish method asynchronously on the Event Dispatch
      // Thread.
      for (String ontvangen_SMD_string : data) {
        // NB altijd in for loop omdat meerdere ontbvangen_SMD_string's verzameld kunnen zijn
        // voordat het hier procesed wordt(inherent aan SwingWorker)
        // System.out.println("ontvangen_SMD_string = " + ontvangen_SMD_string);

        main.total_string = ontvangen_SMD_string;
        System.out.print(main.total_string);
        // System.out.println(total_string); // NB zo zie je ruwweg hoeveel bytes dat telkens
        // beschikbaar zijn

        // error logging
        if (main.total_string.indexOf("error") != -1) {
          String message = "[BAROMETER] " + "interruption or execution exception";
          main.log_turbowin_system_message(message);
        }
        String info = "";
        if ((main_RS232_RS422.test_record.equals("")) || (main_RS232_RS422.test_record == null)) {
          info = "no data";
        } else {
          info = main_RS232_RS422.test_record;
        }

        if (main.RS232_connection_mode == 4
            || main.RS232_connection_mode == 5
            || main.RS232_connection_mode == 7) // Mintaka Duo or Mintaka Star USB or StarX USB
        {
          main.jLabel4.setText(
              main.APPLICATION_NAME
                  + " "
                  + main.application_mode
                  + ", last received barometer data via USB: "
                  + info);
        } else {
          main.jLabel4.setText(
              main.APPLICATION_NAME
                  + " "
                  + main.application_mode
                  + ", last received barometer data via serial communication: "
                  + info);
        }

        RS232_write_sensor_data_to_file(); // PTB220, PTB330, MintakaDuo, Mintaka Star USB, Mintaka
        // StarX USB

        // for timer checking the data is not obsolete; see Function:
        // RS232_WiFi_init_new_barometer_received_check_timer()[main_RS232_RS422.java]
        last_new_data_received_TimeMillis = System.currentTimeMillis();

        // update the date and time on the main screen (and the global date time var's)
        // NB if during start up the date and time displayed in the pupup message was not comfirmed
        // by the user
        //    it is assumed that the pc is not running on the correct time so then do not update the
        // date time automatically
        if (main.use_system_date_time_for_updating == true) {
          set_datetime_while_collecting_sensor_data();
        }

        // WOW enabled (and not 'only publish if manual obs was send') and a complete sensor data
        // string was received
        if ((main.WOW
                == true) /*&& (!main.WOW_reporting_interval.equals(main.WOW_REPORTING_INTERVAL_MANUAL))*/
            && (main.total_string.indexOf("\n") != -1)) {
          cal_WOW_systeem_datum_tijd =
              new GregorianCalendar(
                  new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
          cal_WOW_systeem_datum_tijd.getTime(); // now effective
          int WOW_system_minute_local = cal_WOW_systeem_datum_tijd.get(Calendar.MINUTE);
          cal_WOW_systeem_datum_tijd = null;

          if ((WOW_system_minute_local - 1) % Integer.valueOf(main.WOW_reporting_interval)
              == 0) // e.g. every 10 minutes (WOW_reporting_interval);   but no interference with
          // APR!
          {
            // logging
            String message = "[WOW] scheduled upload";
            main.log_turbowin_system_message(message);

            // checking
            boolean WOW_settings_ok = RS232_check_WOW_settings();

            if (WOW_settings_ok) {
              // read the barometer data which was saved before by TurboWin+ from a sensor data file
              if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR("WOW", retry);
              } else if (main.RS232_connection_mode == 4) // MintakaDuo
              {
                RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR("WOW", retry);
              } else if (main.RS232_connection_mode == 5
                  || main.RS232_connection_mode == 6) // Mintaka Star (USB or WiFi)
              {
                // RS232_Mintaka_Star_Read_And_Send_Sensor_Data_For_WOW_APR("WOW", retry);
                boolean StarX = false;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "WOW", retry, StarX);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX (USB or WiFi)
              {
                boolean StarX = true;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "WOW", retry, StarX);
              }
            } // if (WOW_settings_ok)
          }
        } // if ((main.WOW == true) &&
        // (!main.WOW_reporting_interval.equals(main.WOW_REPORTING_INTERVAL_MANUAL)) &&
        // (main.total_string.indexOf("\n") != -1))

        /////////////////////////////////////
        // System.out.println("+++ " + "main.total_string = " + main.total_string);
        ////////////////////////////////

        // APR (Automated Pressure Reporting)
        if ((main.APR == true) && (main.total_string.indexOf("\n") != -1)) {
          /////////////////////////////////////
          // System.out.println("+++ " + "main.total_string = " + main.total_string);
          ////////////////////////////////

          cal_APR_system_date_time =
              new GregorianCalendar(
                  new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
          cal_APR_system_date_time.getTime(); // now effective
          int APR_system_hour_local =
              cal_APR_system_date_time.get(
                  Calendar.HOUR_OF_DAY); // HOUR_OF_DAY is used for the 24-hour clock. E.g., at
          // 10:04:15.250 PM the HOUR_OF_DAY is 22.
          int APR_system_minute_local = cal_APR_system_date_time.get(Calendar.MINUTE);

          if ((APR_system_minute_local == 0)
              && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                  == 0)) // e.g. every 1 hour (APR_reporting_interval);
          {
            retry = false;

            // logging
            String message = "[APR] scheduled upload";
            main.log_turbowin_system_message(message);

            // checking
            boolean only_2nd_instrument = false;
            boolean APR_settings_ok = RS232_check_APR_settings(only_2nd_instrument);
            boolean GPS_date_time_ok = true;

            if (main.RS232_connection_mode == 5
                || main.RS232_connection_mode == 6
                || main.RS232_connection_mode == 7
                || main.RS232_connection_mode
                    == 8) // Mintaka Star (USB or WiFi) or StarX (USB or WiFi)
            {
              // NB Mintaka Star saved data do not contain date/time information so it is not
              // possible to check this
              //    so it is made always true
              GPS_date_time_ok = true;
            } else {
              GPS_date_time_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("APR");
            }

            if (GPS_date_time_ok == false) {
              main.Reset_all_meteo_parameters();
            }

            if (APR_settings_ok
                && GPS_date_time_ok) // GPS date-time was compared with system date-time
            {
              // NB in this stage we are sure the date-time is correct! + we are sure position is
              // available!

              // read the barometer data which was saved before by TurboWin+ from a sensor data file
              if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 4) // MintakaDuo
              {
                RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 5
                  || main.RS232_connection_mode == 6) // Mintaka Star (USB or WiFi)
              {
                // RS232_Mintaka_Star_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
                boolean StarX = false;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX (USB or WiFi)
              {
                boolean StarX = true;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              }
            } // if (APR_settings_ok && GPS_date_time_ok)
          } // if ((APR_system_minute_local == 0) etc.
          else if ((APR_server_response_code == main.RESPONSE_NO_INTERNET)
              && (APR_system_minute_local == APR_RETRY_MINUTES)
              && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                  == 0)) // e.g. every 1 hour (APR_reporting_interval);
          {
            retry = true;

            // logging
            String message = "[APR] retry scheduled upload";
            main.log_turbowin_system_message(message);

            // checking
            boolean only_2nd_instrument = false;
            boolean APR_settings_ok = RS232_check_APR_settings(only_2nd_instrument);
            boolean GPS_date_time_ok = true;

            if (main.RS232_connection_mode == 5
                || main.RS232_connection_mode == 6
                || main.RS232_connection_mode == 7
                || main.RS232_connection_mode
                    == 8) // Mintaka Star (USB or WiFi) or StarX (USB or WiFi)
            {
              // NB Mintaka Star saved data do not contain date/time information so it is not
              // possible to check this
              //    so it is made always true
              GPS_date_time_ok = true;
            } else {
              GPS_date_time_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("APR");
            }

            if (APR_settings_ok
                && GPS_date_time_ok) // GPS date-time was compared with system date-time
            {
              // NB in this stage we are sure the date-time is correct! + we are sure position is
              // available!

              // read the barometer data which was saved before by TurboWin+ from a sensor data file
              if (main.RS232_connection_mode == 1
                  || main.RS232_connection_mode == 2) // PTB220 or PTB330
              {
                RS232_vaisala.RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 4) // MintakaDuo
              {
                RS232_mintaka.RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
              } else if (main.RS232_connection_mode == 5
                  || main.RS232_connection_mode == 6) // Mintaka Star (USB or WiFi)
              {
                // RS232_Mintaka_Star_Read_And_Send_Sensor_Data_For_WOW_APR("APR", retry);
                boolean StarX = false;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              } else if (main.RS232_connection_mode == 7
                  || main.RS232_connection_mode == 8) // Mintaka StarX (USB or WiFi)
              {
                boolean StarX = true;
                RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR(
                    "APR", retry, StarX);
              }
            } // if (APR_settings_ok && GPS_date_time_ok)

            // reset (to be sure)
            APR_server_response_code = 0;

          } // else if ((APR_server_response_code = main.RESPONSE_NO_INTERNET) etc.
          else if ((APR_server_response_code == main.RESPONSE_NO_INTERNET)
              && (APR_system_minute_local < APR_RETRY_MINUTES)
              && (APR_system_hour_local % Integer.valueOf(main.APR_reporting_interval)
                  == 0)) // e.g. every 1 hour (APR_reporting_interval);
          {
            // text will be visible only a few minutes (till the retry upload)
            main.jTextField4.setText(
                "[APR] retry automated pressure report upload within a few minutes");
          } else {
            // text with next APR update bottom status line TurboWin+ main screen (updated every
            // minute)
            if (!main.APR_reporting_interval.equals("")) {
              for (int i = (APR_system_hour_local + 1);
                  i < (APR_system_hour_local + 1 + Integer.valueOf(main.APR_reporting_interval));
                  i++) {
                // 24 hours or higher makes no difference for this computation
                if (i % Integer.valueOf(main.APR_reporting_interval) == 0) {
                  cal_APR_system_date_time.add(
                      Calendar.HOUR_OF_DAY, i - APR_system_hour_local); // add 1 - 5 hours
                  cal_APR_system_date_time.getTime();

                  String next_APR_string =
                      "[APR] next automated meteo report upload: "
                          + cal_APR_system_date_time.get(Calendar.DAY_OF_MONTH)
                          + " "
                          + main.convert_month(cal_APR_system_date_time.get(Calendar.MONTH))
                          + " "
                          + cal_APR_system_date_time.get(Calendar.YEAR)
                          + " "
                          + cal_APR_system_date_time.get(Calendar.HOUR_OF_DAY)
                          + ".00 UTC";

                  main.jTextField4.setText(next_APR_string);
                  break;
                }
              } // for (int i = (APR_system_hour_local + 1); etc.
            }
          } // else

          cal_APR_system_date_time = null;
        } // if ( (main.APR == true) && (main.total_string.indexOf("\n") != -1))
      } // for (String ontvangen_string : data)
    } // protected void process(List<String> data)
    //      }.execute(); // new SwingWorker<String, String>()

  }

  public void RS232_initComponents() {
    // called from specific_connection_initComponents() [main.java] which is called from
    // read_muffin() [main.java] or lees_configuratie_regels() [main.java]

    ///////// only used for PTB220/PTB330/Mintaka Duo/Mintaka Star USB barometers (not for EUCAWS
    // and not for Mintaka Star WiFi) ////////

    // http://www.snip2code.com/Snippet/1044/Java--read-from-USB-using-RXTX-library
    // FOR LINUX:
    // #==================================================================#
    // WARNING! - DO NOT SET THE FOLLOWING PROPERTY WITH RXTX LIBRARY, IT
    // CAUSES A PROGRAM LOCK:
    // serialPort.notifyOnOutputEmpty(true);
    // #==================================================================#

    // start timer for checking every 1 minute if there is newbarometer sensor data available
    // (because if last data > 5 minutes old 'gray' data on main screen)
    // last_new_data_received_TimeMillis = 0;
    // RS232_And_WiFi_init_new_sensor_data_received_check_timer();

    // System.loadLibrary("C:\\Users\\hometrainer\\Documents\\NetBeansProjects\\turbowin_jws\\rxtxSerial.dll"); // gaat fout omdat het geen path mag zijn (alleen de library name)

    // NB load dll via applet or java web start
    //    You can definitely accomplish this. I have a working applet in production that does
    // exactly this.
    //    Even if your applet is signed, you still need to use the Access Controller to access the
    // dll, you cannot just call "loadlibrary".
    //    You can add this to the Java policy file however this is not recommended due to 1. You
    // probably do not have access to the users java configuration. 2.
    //    Even if this is for your own company use, managing the policy file is a pain as users will
    // download some JRE and your policy file is either overwritten or ignored.
    //    You best bet is to sign your jar, making sure to wrap your load library code in a
    // privileged block of code like this.
    //
    //    You can Also use System.loadlibrary(mydll.dll) but you have to have the dll folder on the
    // path in windows so the applet can find it.

    // for writing info data to status line (bottom of screen)
    // sdf2 = new SimpleDateFormat("yyyy-MM-dd HH.mm");                            // HH hour in day
    // (0-23) note there is also hh (with am, pm)
    // sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for sensor data files (in the file name itself) (note also used in RS232_view.java)
    main.sdf3 =
        new SimpleDateFormat(
            "yyyyMMddHH"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf3.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for date-time strings/remarks in the sensor data records (note also used in RS232_view.java)
    main.sdf4 =
        new SimpleDateFormat(
            "yyyyMMddHHmm"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf4.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for pop-up air pressure when clicking tray icon (TurboWin+ minimized/iconfied)
    sdf8 = new SimpleDateFormat("dd MMM yyyy HH.mm");
    sdf8.setTimeZone(TimeZone.getTimeZone("UTC")); // <----- DEZE = ESSENTIEEL

    // dir for storage sensor data
    // if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2)
    if ((main.logs_dir == null) || (main.logs_dir.compareTo("") == 0)) {
      String info =
          "logs folder (for sensor data storage) unknown, select: Maintenance -> Log files settings";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
    }

    // checking/looking for the correct com port
    //
    int completed_checks_serial_ports = 0;
    while ((main.defaultPort == null)
        && (completed_checks_serial_ports < MAX_COMPLETED_BAROMETER_PORT_CHECKS)) {
      RS232_Check_Serial_Ports_8(
          completed_checks_serial_ports); // PTB220, PTB330, MintakaDuo, MintakaStar USB
      completed_checks_serial_ports++;
    }

    // send Vaisala/Mintaka serial commands to the barometer
    if (main.defaultPort != null) {
      RS232_Format_Barometer_Output_3(); // PTB220, PTB330, MintakaDuo, MintakaStar USB
    } else {
      String info = "no barometer found (defaultPort = null)";
      System.out.println(info);
    }

    // delete old sensor data files (whether or not a barometer was found)
    main.log_turbowin_system_message("[GENERAL] deleting old sensor data files");
    RS232_Delete_Sensor_Data_Files();

    if (main.defaultPort != null) {
      // info text on (bottom) main screen
      // main.jLabel4.setText(main.APPLICATION_NAME + " " + main.application_mode  + ", receiving
      // barometer sensor data via serial communication.....");
      if (main.RS232_connection_mode == 4
          || main.RS232_connection_mode == 5
          || main.RS232_connection_mode == 7) // Mintaka Duo or Mintaka Star USB or StarX USB
      {
        main.jLabel4.setText(
            main.APPLICATION_NAME
                + " "
                + main.application_mode
                + ", last received barometer data via USB: no data");
      } else {
        main.jLabel4.setText(
            main.APPLICATION_NAME
                + " "
                + main.application_mode
                + ", last received barometer data via serial communication: no data");
      }

      // main.serialPort = new SerialPort(main.defaultPort);  // NB main.defaultPort was determined
      // in Function: RS232_Check_Serial_Ports_8()
      // final SerialPort serialPort  = SerialPort.getCommPort(main.defaultPort
      main.serialPort = SerialPort.getCommPort(main.defaultPort);

      RS232_worker = new RS232_Class_Receive_Sensor_Data();
      RS232_worker.execute();

      String message = "[BAROMETER] start listening";
      main.log_turbowin_system_message(message);

      // set start-up sequence finished flag
      // main.turbowin_start_up_sequence_finished = true;

      // start timer for checking every 1 minute if there is new barometer sensor data available
      // (because if last data > 10 minutes old 'gray' data on main screen)
      // NB invoking after: RS232_worker = new RS232_Class_Receive_Sensor_Data(); and
      // RS232_worker.execute();
      last_new_data_received_TimeMillis = 0;
      RS232_And_WiFi_init_new_sensor_data_received_check_timer();
    } // if (main.defaultPort != null)
  }

  public void RS232_initComponents_II() {
    // called from specific_connection_initComponents() [main.java] which is called from
    // read_muffin() [main.java] or lees_configuratie_regels() [main.java]

    ///////// only used 2nd instrument HMP155 (not for for PTB220/PTB330/Mintaka Duo/Mintaka Star
    // USB barometers and not for EUCAWS and not for Mintaka Star WiFi) ////////

    // for writing info data to status line (bottom of screen)
    // sdf2 = new SimpleDateFormat("yyyy-MM-dd HH.mm");                            // HH hour in day
    // (0-23) note there is also hh (with am, pm)
    // sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for sensor data files (in the file name itself) (note also used in RS232_view.java)
    main.sdf3 =
        new SimpleDateFormat(
            "yyyyMMddHH"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf3.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for date-time strings/remarks in the sensor data records (note also used in RS232_view.java)
    main.sdf4 =
        new SimpleDateFormat(
            "yyyyMMddHHmm"); // HH hour in day (0-23) note there is also hh (then also with am, pm)
    main.sdf4.setTimeZone(TimeZone.getTimeZone("UTC"));

    // for pop-up air pressure when clicking tray icon (TurboWin+ minimized/iconfied)
    // sdf8 = new SimpleDateFormat("dd MMM yyyy HH.mm");
    // sdf8.setTimeZone(TimeZone.getTimeZone("UTC"));                                   // <-----
    // DEZE = ESSENTIEEL

    // dir for storage sensor data
    if ((main.logs_dir == null) || (main.logs_dir.compareTo("") == 0)) {
      String info =
          "logs folder (for sensor data storage) unknown, select: Maintenance -> Log files settings";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
    }

    // checking/looking for the correct com port
    //
    int completed_checks_serial_ports_II = 0;
    while ((main.defaultPort_II == null)
        && (completed_checks_serial_ports_II < MAX_COMPLETED_THERMOMETER_PORT_CHECKS)) {
      RS232_Check_Serial_Ports_8_II(completed_checks_serial_ports_II); // HMP 155
      completed_checks_serial_ports_II++;
    }

    // send Vaisala serial commands to the thermometer
    if (main.defaultPort_II != null) {
      RS232_Format_Thermometer_Output(); // HMP 155
    } else {
      String info = "no thermometer found (defaultPort_II = null)";
      System.out.println(info);
    }

    // delete old sensor data files (whether or not a thermometer was found)
    main.log_turbowin_system_message("[GENERAL] deleting old sensor data files II");
    RS232_Delete_Sensor_Data_Files_II();

    if (main.defaultPort_II != null) {
      // info text on (bottom) main screen
      // main.jLabel4.setText(main.APPLICATION_NAME + " " + main.application_mode  + ", receiving
      // barometer sensor data via serial communication.....");
      if (main.RS232_connection_mode_II == 1) // HMP 155
      {
        // main.jLabel4.setText(main.APPLICATION_NAME + " " + main.application_mode + ", last
        // received thermometer data via USB: no data");
        main.jLabel41.setText(
            main.APPLICATION_NAME + " " + ", last received thermometer data via USB: no data");
      }

      main.serialPort_II = SerialPort.getCommPort(main.defaultPort_II);

      RS232_worker_II = new RS232_Class_Receive_Sensor_Data_II();
      RS232_worker_II.execute();

      String message = "[THERMOMETER] start listening";
      main.log_turbowin_system_message(message);

      // start timer for checking every 1 minute if there is new thermometer sensor data available
      // (because if last data > 5 minutes old 'gray' data on main screen)
      // NB invoking after: RS232_worker_II = new RS232_Class_Receive_Sensor_Data_II(); and
      // RS232_worker_II.execute();
      last_new_data_received_TimeMillis_II = 0;
      RS232_And_WiFi_init_new_sensor_data_received_check_timer_II();
    } // if (main.defaultPort != null)
  }

  private boolean RS422_check_AWSR_settings() {
    // called from: RS422_V3_write_sensor_data_to_file() [main.java]
    //
    // NB see also: AWSR_additional_requirements_checks() [WOW_APR_settings.java]
    //

    String message = "";

    // AWS connected? (EUCAWS only if uploads method is via turbowin)
    if (main.RS232_connection_mode != 9
        && main.RS232_connection_mode != 10
        && main.RS232_connection_mode != 3
        && main.RS232_connection_mode
            != 11) // 9 or 10 = OMC 140 AWS connected; 3 = EUCAWS; 11 = AMOS2X
    {
      message =
          "[AWSR] AWS connection unknown (select: Maintenance -> Serial/USB/LAN device settings)";
      main.log_turbowin_system_message(message);
    }

    // OMC 140 AWS serial connected but not working
    if (main.RS232_connection_mode == 9
        && main.defaultPort == null) // 9 = OMC 140 AWS serial connected;
    {
      message = "[AWSR] OMC 140 AWS connection error (comm port == null)";
      main.log_turbowin_system_message(message);
    }

    // AMOS2X AWS serial connected but not working
    if (main.RS232_connection_mode == 11
        && main.defaultPort == null) // 11 = AMOS2X AWS serial connected;
    {
      message = "[AWSR] AMOS2X AWS connection error (comm port == null)";
      main.log_turbowin_system_message(message);
    }
    if (main.station_ID.trim().equals("") == true || main.station_ID.trim().length() < 2) {
      message = "[AWSR] station ID unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }

    // height barometer
    if (main.barometer_above_sll.trim().equals("") == true
        || main.barometer_above_sll.trim().length() < 1) {
      message =
          "[AWSR] Height of the barometer above SLL unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }
    if (main.keel_sll.trim().equals("") == true || main.keel_sll.trim().length() < 1) {
      message =
          "[AWSR] Distance of bottom of the keel to SLL unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }

    // logs folder
    if (main.logs_dir.trim().equals("") == true || main.logs_dir.trim().length() < 2) {
      message = "[AWSR] Logs folder unknown (select: Maintenance -> Log files settings)";
      main.log_turbowin_system_message(message);
    }

    // obs format
    if ((main.obs_format.equals(main.FORMAT_FM13) == false)
        && (main.obs_format.equals(main.FORMAT_101) == false)
        && (main.eucaws_uploads_method.equals(main.UPLOADS_VIA_TURBOWIN) == false)) {
      // NB AWS mode is always reporting with format101
      message =
          "[AWSR] In AWSR mode the obs format must be FM13 or 101 or 'AWS + uploads via TurboWin+' (select: Maintenance -> Obs format setting)";
      main.log_turbowin_system_message(message);
    }

    // AWSR reporting interval
    // if (main.APR_reporting_interval.equals(""))
    if (main.AWSR_reporting_interval.equals("")) {
      message =
          "[AWSR] AWSR reporting interval unknown (select: Maintenance -> APR/APTR/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // draught
    if (main.WOW_APR_average_draught.equals("")) {
      message = "[AWSR] Draught unknown (select: Maintenance -> APR/APTR/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // IC barometer
    if (main.barometer_instrument_correction.equals("")) {
      message =
          "[AWSR] Barometer instrument correction unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }

    return message.equals("");
  }

  private boolean RS232_check_APR_settings(final boolean only_2nd_instrument) {
    // called from: - class RS232_Class_Receive_Sensor_Data()
    //              - class WiFi_Class_Receive_UDP()
    //              - class RS232_Class_Receive_Sensor_Data_II()
    //
    // NB see also: APR_additional_requirements_checks() [WOW_APR_settings.java]

    String message = "";

    // only 2nd instrument (HMP155) connected
    if (only_2nd_instrument == true) {
      if (main.RS232_connection_mode_II != 1) {
        message =
            "[APR] Thermometer connection unknown (select: Maintenance -> Serial/USB/LAN device settings)";
        main.log_turbowin_system_message(message);
      }

      if ((main.RS232_connection_mode_II != 0) && (main.defaultPort_II == null)) {
        message = "[APR] Thermometer connection error (comm port == null)";
        main.log_turbowin_system_message(message);
      }
    } // if (only_2nd_instrument == true)
    else // at least 1st meteo instrument connected
    {
      // barometer connected?
      if (main.RS232_connection_mode == 0
          || main.RS232_connection_mode == 3
          || main.RS232_connection_mode == 9
          || main.RS232_connection_mode == 10
          || main.RS232_connection_mode
              == 11) // 0 = no instrument connected, 3 or 9/10/11 = AWS connected
      {
        message =
            "[APR] Barometer connection unknown (select: Maintenance -> Serial/USB/LAN device settings)";
        main.log_turbowin_system_message(message);
      }

      // barometer serial or AWS serial connected but not working
      if (main.RS232_connection_mode != 0
          && main.RS232_connection_mode != 3
          && main.RS232_connection_mode != 6
          && main.RS232_connection_mode != 8
          && main.RS232_connection_mode != 9
          && main.RS232_connection_mode != 10
          && main.RS232_connection_mode != 11
          && main.defaultPort
              == null) // 0 = no instrument connected, 3 or 9 or 10 or 11 = AWS connected; 6 =
      // Mintaka Star WiFi
      {
        message = "[APR] Barometer connection error (comm port == null)";
        main.log_turbowin_system_message(message);
      }
    } // else (at least 1 st meteo instrument connected)

    // GPS connected?
    if (main.RS232_GPS_connection_mode == 0) // 0 no GPS connected
    {
      message =
          "[APR] GPS connection unknown (select: Maintenance -> Serial/USB/LAN device settings)";
      main.log_turbowin_system_message(message);
    }

    // GPS connected but not working (does not aplly if also a Mintaka Star or StarX is connected
    // because than integrated GPS)
    if (main.RS232_GPS_connection_mode != 3
        && main.RS232_GPS_connection_mode
            != 4) // in case Mintaka Star or StarX there will be no GPS_defaultPort
    {
      if (main.RS232_GPS_connection_mode != 0
          && main_RS232_RS422.GPS_defaultPort == null) // 0 no GPS connected
      {
        message = "[APR] GPS connection error (comm port == null)";
        main.log_turbowin_system_message(message);
      }
    }

    // call sign
    // if (main.call_sign.trim().equals("") == true || main.call_sign.trim().length() < 2)
    // {
    //   message = "[APR] Call sign unknown (select: Maintenance -> Station data)";
    //   main.log_turbowin_system_message(message);
    // }

    // station ID
    if (main.station_ID.trim().equals("") == true || main.station_ID.trim().length() < 2) {
      message = "[APR] station ID unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }

    // height barometer
    if (main.barometer_above_sll.trim().equals("") == true
        || main.barometer_above_sll.trim().length() < 1) {
      message =
          "[APR] Height of the barometer above SLL unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }
    if (main.keel_sll.trim().equals("") == true || main.keel_sll.trim().length() < 1) {
      message =
          "[APR] Distance of bottom of the keel to SLL unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }

    // logs folder
    if (main.logs_dir.trim().equals("") == true || main.logs_dir.trim().length() < 2) {
      message = "[APR] Logs folder unknown (select: Maintenance -> Log files settings)";
      main.log_turbowin_system_message(message);
    }

    // obs format
    if (main.obs_format.equals(main.FORMAT_101) == false
        && main.obs_format.equals(main.FORMAT_FM13) == false) {
      message =
          "[APR] In APR mode the obs format must be '101' or FM13 (select: Maintenance -> Obs format setting)";
      main.log_turbowin_system_message(message);
    }

    // APR reporting interval
    if (main.APR_reporting_interval.equals("")) {
      message =
          "[APR] APR reporting interval unknown (select: Maintenance -> APR/APTR/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // draught
    if (main.WOW_APR_average_draught.equals("")) {
      message = "[APR] Draught unknown (select: Maintenance -> APR/APTR/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // IC barometer
    if (main.barometer_instrument_correction.equals("")) {
      message =
          "[APR] Barometer instrument correction unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }

    // upload URL
    // if (main.upload_URL.equals(""))
    // {
    //   message = "[APR] upload URL unknown (select: Maintenance -> Server settings)";
    //   main.log_turbowin_system_message(message);
    // }

    // update of date and time automatically
    if (main.use_system_date_time_for_updating == false) {
      message = "[APR] obs and system date/time not comfirmed by the user at startup";
      main.log_turbowin_system_message(message);
    }

    return message.equals("");
  }

  private boolean RS232_check_WOW_settings() {
    String message = "";

    // Barometer connected?
    if (main.RS232_connection_mode == 0
        || main.RS232_connection_mode == 3
        || main.RS232_connection_mode == 9
        || main.RS232_connection_mode == 10
        || main.RS232_connection_mode == 11) {
      message =
          "[WOW] Barometer connection unknown (select: Maintenance -> Serial/USB/LAN device settings)";
      main.log_turbowin_system_message(message);
    }

    // MintakeDuo connected but not working
    if (main.RS232_connection_mode == 4 && main.defaultPort == null) {
      message = "[WOW] MintakaDuo connection error (comm port == null)";
      main.log_turbowin_system_message(message);
    }

    // WOW site id
    else if ((main.WOW_site_id.length() < 2 || main.WOW_site_id.length() > 20)) {
      message = "[WOW] site ID not ok (select: Maintenance -> WOW/AP[&T]R/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // site pin
    if ((main.WOW_site_pin.length() < 2 || main.WOW_site_pin.length() > 20)) {
      message = "[WOW] pin not ok (select: Maintenance -> WOW/AP{&T]R/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // reporting interval
    if ((main.WOW_reporting_interval.equals(
        "") /*|| main.WOW_reporting_interval.equals(main.WOW_REPORTING_INTERVAL_MANUAL)*/)) {
      message =
          "[WOW] reporting interval not ok (select: Maintenance -> WOW/AP[&T]R/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // draught
    if (main.WOW_APR_average_draught.equals("")) {
      message = "[WOW] Draught unknown (select: Maintenance -> WOW/AP[&T]R/AWSR settings)";
      main.log_turbowin_system_message(message);
    }

    // IC barometer
    if (main.barometer_instrument_correction.equals("")) {
      message =
          "[WOW] Barometer instrument correction unknown (select: Maintenance -> Station data)";
      main.log_turbowin_system_message(message);
    }

    // update of date and time automatically
    if (main.use_system_date_time_for_updating == false) {
      message = "[WOW] obs and system date/time not comfirmed by the user at startup";
      main.log_turbowin_system_message(message);
    }

    return message.equals("");
  }

  public static void RS232_APR_AWSR_send(final boolean retry) {
    // called from: - RS422_Read_And_Send_Sensor_Data_For_AWSR() [main_RS232_RS422.java]
    //              - RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR()
    // [RS232_vaisala.java]
    //              - RS232_Vaisala_Read_And_Send_Sensor_Data_For_WOW_APR() [main_RS232_RS422.java]
    //              - RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
    // [main_RS232_RS422.java]
    //              - RS232_Mintaka_Duo_Read_And_Send_Sensor_Data_For_WOW_APR()
    // [main_RS232_RS422.java]
    //

    String log_tag;
    if (main.AWSR == true) {
      log_tag = "[AWSR]";
    } else {
      log_tag = "[APR]";
    }

    AptrAwsrUploadDecision.Action action =
        AptrAwsrUploadDecision.decide(
            main.port_mode_option,
            myposition.SOG_APR_wind,
            main.APTR_AWSR_send_method,
            main.obs_format,
            main.eucaws_uploads_method);

    if (action == AptrAwsrUploadDecision.Action.CANCEL_LOW_SPEED) {
      // NB SOG_APR_wind always over the last 10 minutes. There is also a myposition.SOG_APR but
      // over 10 minutes [format 101] or 3 hours [FM13] depending the obs format
      // main.log_turbowin_system_message(log_tag + " upload cancelled; SOG = " +
      // myposition.SOG_APR_wind + " (port mode option set via menu: Maintenance -> APR/APTR/AWSR
      // settings)");
      main.log_turbowin_system_message(
          log_tag
              + " upload cancelled; SOG [10 min] = "
              + String.format("%.2f", myposition.SOG_APR_wind)
              + " knot (port mode option set via menu: Maintenance -> APR/APTR/AWSR settings)");
    } else if (action == AptrAwsrUploadDecision.Action.CANCEL_UNAVAILABLE_SPEED) {
      main.log_turbowin_system_message(
          log_tag
              + " upload cancelled; SOG [10 min] = not available (port mode option set via menu: Maintenance -> APR/APTR/AWSR settings)");
    } else {
      // send the data
      //
      if (action == AptrAwsrUploadDecision.Action.SEND_FORMAT101_SERVER) {
        RS232_Send_Sensor_Data_to_APR_format101_Server_V2(retry);
      } else if (action == AptrAwsrUploadDecision.Action.SEND_FM13_SERVER) {
        RS232_Send_Sensor_Data_to_APR_FM13_Server(retry);
      } else if (action == AptrAwsrUploadDecision.Action.SEND_EMAIL) {
        RS232_Output_obs_by_email_all_APR();
      } else if (action == AptrAwsrUploadDecision.Action.UNKNOWN_FORMAT) {
        main.log_turbowin_system_message(
            "[APR] obs format unknown (must be format101 or FM13 or 'AWS + uploads via TurboWin+')");
      } else {
        main.log_turbowin_system_message("[APR] APTR-AWSR send method unknown");
      }
    }
  }

  // private void RS232_compute_APR_COG_SOG_wind(double double_lat_start, double double_lon_start,
  // double double_lat_end, double double_lon_end)
  public static void RS232_compute_APR_COG_SOG_wind(
      double double_lat_start,
      double double_lon_start,
      double double_lat_end,
      double double_lon_end) {
    //
    //
    //
    // http://www.movable-type.co.uk/scripts/latlong.html
    //

    RhumbLineCalculator.Result rhumbLine =
        RhumbLineCalculator.calculate(
            double_lat_start, double_lon_start, double_lat_end, double_lon_end);

    // wind always 10 minutes based
    myposition.SOG_APR_wind = (rhumbLine.distanceMeters / 1852) * 6; // now SOG in knots
    myposition.COG_APR_wind = rhumbLine.courseDegrees;

    // SOG_APR_wind wind rounding to three decimals
    BigDecimal bd_SOG_APR_wind =
        new BigDecimal(myposition.SOG_APR_wind).setScale(3, RoundingMode.HALF_UP); // three decimals
    myposition.SOG_APR_wind = bd_SOG_APR_wind.doubleValue();

    // COG_APR_wind rounding to three decimals
    BigDecimal bd_COG_APR_wind =
        new BigDecimal(myposition.COG_APR_wind).setScale(3, RoundingMode.HALF_UP); // three decimals
    myposition.COG_APR_wind = bd_COG_APR_wind.doubleValue();

    // TESTING
    // System.out.println("+++ COG = " + myposition.COG_APR);
    // System.out.println("+++ SOG = " + myposition.SOG_APR);

  }

  public static void RS232_compute_APR_COG_SOG(
      double double_lat_start,
      double double_lon_start,
      double double_lat_end,
      double double_lon_end) {
    // called from: - RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
    //              - RS232_GPS_NMEA_0183_Date_Position_Parsing()
    //
    //
    //
    // http://www.movable-type.co.uk/scripts/latlong.html
    //

    RhumbLineCalculator.Result rhumbLine =
        RhumbLineCalculator.calculate(
            double_lat_start, double_lon_start, double_lat_end, double_lon_end);

    if (main.obs_format.equals(main.FORMAT_101)) {
      // 10 minutes based
      myposition.SOG_APR = (rhumbLine.distanceMeters / 1852) * 6; // now SOG in knots
    } else if (main.obs_format.equals(main.FORMAT_FM13)) {
      // 180 minutes (3 hours, between positions to compute SOG and COG) based
      myposition.SOG_APR = (rhumbLine.distanceMeters / 1852) / 3; // now SOG in knots
    }

    myposition.COG_APR = rhumbLine.courseDegrees;

    // SOG_APR rounding to three decimals
    BigDecimal bd_SOG_APR =
        new BigDecimal(myposition.SOG_APR).setScale(3, RoundingMode.HALF_UP); // three decimals
    myposition.SOG_APR = bd_SOG_APR.doubleValue();

    // COG_APR rounding to three decimals
    BigDecimal bd_COG_APR =
        new BigDecimal(myposition.COG_APR).setScale(3, RoundingMode.HALF_UP); // three decimals
    myposition.COG_APR = bd_COG_APR.doubleValue();

    // TESTING
    // System.out.println("+++ COG = " + myposition.COG_APR);
    // System.out.println("+++ SOG = " + myposition.SOG_APR);

    //
    ///////////// Below for FM13 + APR (not for format #101 + APR) and IMMT + APR + FM13
    // ////////////
    //

    // NB WMO_NO_306.pdf
    //
    // WMO table  4451
    // vs Ship s average speed made good during the three hours preceding the time of observation
    // Code
    // figure
    // 0 = 0 knot 0 km h 1
    // 1 = 1 5 knots 1 10 km h 1
    // 2 = 6 10 knots 11 19 km h 1
    // 3 = 11 15 knots 20 28 km h 1
    // 4 = 16 20 knots 29 37 km h 1
    // 5 = 21 25 knots 38 47 km h 1
    // 6 = 26 30 knots 48 56 km h 1
    // 7 = 31 35 knots 57 65 km h 1
    // 8 = 36 40 knots 66 75 km h 1
    // 9 = Over 40 knots Over 75 km h 1
    // / = Not applicable (report from a coastal land station) or not reported (see Regulation
    // 12.3.1.2 (b))

    String speedCode = ImmtMovementCodeCalculator.speedCode(myposition.SOG_APR);
    if (speedCode != null) {
      myposition.vs_code = speedCode;
    }

    // NB WMO_NO_306.pdf
    //
    // Ds True direction of resultant displacement of the ship during the three hours preceding
    // the time of observation
    // D1 True direction of the point position from the station
    // Code
    // figure
    // 0 Calm (in D, DK), or stationary (in Ds), or at the station (in Da, D1), or stationary or no
    // clouds (in DH,DL, DM)
    // 1 = NE
    // 2 = E
    // 3 = SE
    // 4 = S
    // 5 = SW
    // 6 = W
    // 7 = NW
    // 8 = N
    // 9 All directions (in Da, D1), or confused (in DK), or variable (in D(wind)), or unknown (in
    // Ds), or unknown
    // or clouds invisible (in DH, DL, DM)
    // / Report from a coastal land station or displacement of ship not reported (in Ds only see
    // Regulation 12.3.1.2 (b))

    String directionCode =
        ImmtMovementCodeCalculator.directionCode(myposition.SOG_APR, myposition.COG_APR);
    if (directionCode != null) {
      myposition.Ds_code = directionCode;
    }

    // below for testing
    // System.out.println("+++ double_lat_start = " + double_lat_start);
    // System.out.println("+++ double_lon_start = " + double_lon_start);
    // System.out.println("+++ double_lat_end = " + double_lat_end);
    // System.out.println("+++ double_lon_end = " + double_lon_end);
    // System.out.println("+++ dist [metres] = " + dist);

    // System.out.println("--- SOG APR [knots] = " + myposition.SOG_APR);
    // System.out.println("--- COG APR [deg] = " + myposition.COG_APR);

  }

  private static void RS232_Send_Sensor_Data_to_APR_FM13_Server(final boolean retry) {
    Rs232SendSensorDataToAprFm13Workflow.start(retry);
  }

  public static void RS232_make_pressure_APR_FM13_IMMT_ready() {
    // NB also for making IMMT ready

    // called from:

    // NB below exactly the same as in: RS232_make_APR_IMMT_ready()
    if (mybarometer.pressure_msl_corrected.compareTo("") != 0) {
      try {
        // string to double
        double hulp_double_pressure_msl_corrected =
            Double.parseDouble(mybarometer.pressure_msl_corrected);

        mybarometer.PPPP_code = PressureCodeFormatter.format(hulp_double_pressure_msl_corrected);
      } catch (NumberFormatException ex) {
        mybarometer.PPPP_code = "";
      }
    } // if (mybarometer.pressure_msl_corrected.compareTo("") != 0)
  }

  public static void RS232_make_pressure_ppp_APR_FM13_IMMT_ready() {
    // called from: - RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()

    boolean doorgaan = false;
    double local_double_pressure_amount_tendency = main.INVALID;

    if (mybarograph.pressure_amount_tendency.trim().length() > 0) {
      doorgaan = true;
    } // if (doorgaan)

    if (doorgaan) {
      try {
        local_double_pressure_amount_tendency =
            Double.parseDouble(mybarograph.pressure_amount_tendency.trim());
      } catch (NumberFormatException e) {
        doorgaan = false;
      }
    } // if (doorgaan)

    if (doorgaan) {
      // NB so assuming: mybarograph.pressure_amount_tendency and
      // local_double_pressure_amount_tendency always positive !!!
      //    this is already done in Function:
      // RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
      //    see: mybarograph.pressure_amount_tendency =
      // df.format(Math.abs(hulp_double_APR_pressure_ppp));
      if ((local_double_pressure_amount_tendency < 0.0)
          || (local_double_pressure_amount_tendency > 99.9)) {
        doorgaan = false;
      }
    } // if (doorgaan)

    if (doorgaan) {
      mybarograph.ppp_code =
          PressureTendencyCodeFormatter.format(local_double_pressure_amount_tendency);
    } // if (doorgaan)
  }

  public static void RS232_make_pressure_a_APR_FM13_IMMT_ready() {
    // called from: - RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()

    // NB a_code already determined ! in calling function

    // THESE ARE ALREADY KNOWN IN THE CALLING FUNCTIONS SO NO ACTION REQUIRED HERE
    //

  }

  public static void RS232_make_RH_APR_FM13_IMMT_ready() {
    // called from - RS232_Mintaka_Star_And_StarX_Read_And_Send_Sensor_Data_For_WOW_APR()
    //             -
    // RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR_as_2nd_instrument()
    //             - RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_APR()

    // NB RH is not used in FM13

    //
    // for IMMT:
    //
    // needed: mytemp.double_rv
    //         mytemp.RH
    //
    // THESE ARE ALREADY KNOWN IN THE CALLING FUNCTIONS SO NO ACTION REQUIRED HERE
    //

  }

  public static void RS232_make_air_temp_APR_FM13_IMMT_ready() {
    // NB also for making IMMT ready

    // air temp
    //
    if (mytemp.air_temp.compareTo("") != 0) {
      try {
        // string to double
        double hulp_double_air_temp = Double.parseDouble(mytemp.air_temp);

        // Sn of TTT
        if (hulp_double_air_temp < 0) {
          mytemp.sn_TTT_code = "1";
        } else {
          mytemp.sn_TTT_code = "0";
        }

        // TTT
        int num_TTT_code = (int) Math.floor(Math.abs(hulp_double_air_temp) * 10 + 0.5);
        mytemp.TTT_code = Integer.toString(num_TTT_code); // convert to string

        // NB TTT_code always 3 characters width e.g. 027 (accomplished via construction below)
        int len = 3;
        if (mytemp.TTT_code.length() < len) {
          // pad on left with zeros
          mytemp.TTT_code =
              "0000000000".substring(0, len - mytemp.TTT_code.length()) + mytemp.TTT_code;
        }
      } catch (NumberFormatException ex) {
        mytemp.sn_TTT_code = "";
        mytemp.TTT_code = "";
      }
    } // if (mytemp.air_temp.compareTo("") != 0)
  }

  public static void RS232_make_wet_bulb_temp_APR_FM13_IMMT_ready() {
    // NB also for making IMMT ready

    if (mytemp.wet_bulb_temp.compareTo("") != 0) {
      try {
        // string to double
        double hulp_double_wet_bulb_temp = Double.parseDouble(mytemp.wet_bulb_temp);

        // sn of wet-bulb (TbTbTb)
        if (hulp_double_wet_bulb_temp < 0.0) {
          if (mytemp.wet_bulb_frozen == true) // possible in automatic mode ??
          {
            mytemp.sn_TbTbTb_code = "2"; // measured; iced wet bulb
          } else {
            mytemp.sn_TbTbTb_code = "1"; // measured; negative
          }
        } // if (double_wet_bulb_temp < 0.0)
        else {
          mytemp.sn_TbTbTb_code = "0"; // measured; positive or zero
        } // else

        // TbTbTb
        int num_TbTbTb_code = (int) Math.floor(Math.abs(hulp_double_wet_bulb_temp) * 10 + 0.5);
        mytemp.TbTbTb_code = Integer.toString(num_TbTbTb_code); // convert to string

        // NB TbTbTb_code always 3 characters width e.g. 127 (accomplish via construction below)
        int len = 3;
        if (mytemp.TbTbTb_code.length() < len) {
          // pad on left with zeros
          mytemp.TbTbTb_code =
              "0000000000".substring(0, len - mytemp.TbTbTb_code.length()) + mytemp.TbTbTb_code;
        }
      } // try
      catch (NumberFormatException ex) {
        mytemp.sn_TbTbTb_code = "";
        mytemp.TbTbTb_code = "";
      }
    } // if (mytemp.wet_bulb_temp.compareTo("") != 0)
  }

  public static void RS232_make_dew_point_APR_FM13_IMMT_ready() {
    // NB also for making IMMT ready

    if (mytemp.double_dew_point != main.INVALID) {
      // sn van dew point (TdTdTd)
      if (mytemp.double_dew_point < 0.0) {
        mytemp.sn_TdTdTd_code = "1";
      } else {
        mytemp.sn_TdTdTd_code = "0";
      }

      // TdTdTd
      int num_TdTdTd_code = (int) Math.floor(Math.abs(mytemp.double_dew_point) * 10 + 0.5);
      mytemp.TdTdTd_code = Integer.toString(num_TdTdTd_code); // convert to string

      // NB TdTdTd_code always 3 characters width e.g. 127 (accomplish via construction below)
      int len = 3;
      if (mytemp.TdTdTd_code.length() < len) {
        // pad on left with zeros
        mytemp.TdTdTd_code =
            "0000000000".substring(0, len - mytemp.TdTdTd_code.length()) + mytemp.TdTdTd_code;
      }
    } // if (double_dew_point != main.INVALID)
    else {
      mytemp.sn_TdTdTd_code = "";
      mytemp.TdTdTd_code = "";
    }
  }

  private static void RS232_Output_obs_by_email_all_APR() {
    Rs232OutputObsByEmailAllAprWorkflow.start();
  }

  private static void RS232_Send_Sensor_Data_to_APR_format101_Server_V2(final boolean retry) {
    Rs232SendSensorDataToAprFormat101Workflow.start(retry);
  }

  public static double RS232_WOW_APR_compute_air_pressure_height_correction(
      double double_pressure_reading) {
    // NB this function is used for height air pressure conversion from station level to mean sea
    // level for the WOW site and/or APR
    // NB before this function is called the WOW/APR settings were checked

    double height_correction_pressure = Double.MAX_VALUE;
    double temp;
    double term_1;
    double term_2;
    double double_barometer_instrument_correction = 0;
    double double_barometer_above_sll = 0;
    double double_keel_sll = 0;
    double double_draught = 0;
    double double_hoogte_barometer;
    double double_pressure_reading_corrected;
    boolean checks_ok = true;

    /*
    /////////// checks
    */
    if ((main.barometer_above_sll.equals("")) || (main.barometer_above_sll.trim().length() == 0)) {
      // JOptionPane.showMessageDialog(null, "barometer height above MSL not available (Maintenance
      // -> Station data)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      System.out.println(
          "[APR] barometer height above MSL not available (Maintenance -> Station data)");
      checks_ok = false;
    }
    if ((main.keel_sll.equals("")) || (main.keel_sll.trim().length() == 0)) {
      // JOptionPane.showMessageDialog(null, "distance keel - MSL not available (Maintenance ->
      // Station data)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      System.out.println("[APR] distance keel - MSL not available (Maintenance -> Station data)");
      checks_ok = false;
    }
    if ((main.barometer_instrument_correction.equals(""))
        || (main.barometer_instrument_correction.trim().length() == 0)) {
      System.out.println(
          "[APR] barometer instrument correction not available (Maintenance -> Station data)");
      checks_ok = false;
    }
    if ((main.WOW_APR_average_draught.equals(""))
        || (main.WOW_APR_average_draught.trim().length() == 0)) {
      System.out.println(
          "[APR] average draught not available (Maintenance -> APR/APTR/AWSR settings)");
      checks_ok = false;
    }

    // compute the doubles
    //
    if (checks_ok) {
      // convert to double: distance barometer - keel
      try {
        double_barometer_above_sll = Double.parseDouble(main.barometer_above_sll.trim());
      } catch (NumberFormatException e) {
        // JOptionPane.showMessageDialog(null, "Internal error (converting distance barometer - SLL
        // to double)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        System.out.println("[APR] Internal error (converting distance barometer - SLL to double)");
        checks_ok = false;
      }

      // convert to double: distance keel - sll
      try {
        double_keel_sll = Double.parseDouble(main.keel_sll.trim());
      } catch (NumberFormatException e) {
        // JOptionPane.showMessageDialog(null, "Internal error (converting distance keel - SLL to
        // double)", main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        System.out.println("[APR] Internal error (converting distance keel - SLL to double)");
        checks_ok = false;
      }

      try {
        double_barometer_instrument_correction =
            Double.parseDouble(main.barometer_instrument_correction);
      } catch (NumberFormatException e) {
        System.out.println("[APR] Internal error (converting distance barometer ic to double)");
        checks_ok = false;
      }

      try {
        double_draught = Double.parseDouble(main.WOW_APR_average_draught);
      } catch (NumberFormatException e) {
        System.out.println("[APR] Internal error (converting average draught to double)");
        checks_ok = false;
      }
    } // if checks_ok

    // the 'real' computation
    //
    if (checks_ok) {
      double_pressure_reading_corrected =
          double_pressure_reading + double_barometer_instrument_correction;

      double_hoogte_barometer = double_keel_sll + double_barometer_above_sll - double_draught;

      temp =
          15.0 + CELCIUS_TO_KELVIN_FACTOR; // so calculated with a default air temperature (15.0 C)
      // !!!!
      term_1 = double_pressure_reading_corrected * double_hoogte_barometer;
      term_2 = 29.27 * temp;

      if (term_2 != 0) // prevent exception (in principle will never be 0))
      {
        height_correction_pressure = term_1 / term_2;
      } else {
        height_correction_pressure = 0.0;
      }

      // round to two digits behind the "."
      height_correction_pressure = Math.round(height_correction_pressure * 100.0) / 100.0;

      // at Great lakes? (for extra height correction value)
      double great_lakes_corr = check_at_Great_Lakes();
      if (great_lakes_corr < 50.0) // so at Great Lakes
      {
        height_correction_pressure += great_lakes_corr;
      } else if ((great_lakes_corr > AT_GREAT_LAKES_OUTSIDE_MAIN_AREAS - 1)
          && (great_lakes_corr
              < AT_GREAT_LAKES_OUTSIDE_MAIN_AREAS
                  + 1)) // so at Great Lakes but in transition between two lakes (locks)
      {
        height_correction_pressure = AT_GREAT_LAKES_OUTSIDE_MAIN_AREAS;
        String message =
            "[APR] air pressure will not be reported because outside Great Lakes main areas (in the transition between two Lakes)";
        main.log_turbowin_system_message(message);
      }
    } // if (checks_ok)

    // System.out.println("--- WOW/APR height_correction_pressure : " + height_correction_pressure);

    return height_correction_pressure;
  }

  public static double check_at_Great_Lakes() {
    double north_limit_great_lakes = 49;
    double south_limit_great_lakes = 41;
    double west_limit_great_lakes = -93;
    double east_limit_great_lakes = -73;

    double north_limit_lake_ontario = 44;
    double south_limit_lake_ontario = 43.3;
    double west_limit_lake_ontario = -80;
    double east_limit_lake_ontario = -76.5;

    double north_limit_lake_erie_upper = 42.8;
    double south_limit_lake_erie_upper = 41.5;
    double west_limit_lake_erie_upper = -82;
    double east_limit_lake_erie_upper = -79;

    double north_limit_lake_erie_lower = 42;
    double south_limit_lake_erie_lower = 41.5;
    double west_limit_lake_erie_lower = -83.4;
    double east_limit_lake_erie_lower = -82;

    double north_limit_lake_clair = 42.5;
    double south_limit_lake_clair = 42.35;
    double west_limit_lake_clair = -82.9;
    double east_limit_lake_clair = -82.7;

    double north_limit_lake_huron = 46;
    double south_limit_lake_huron = 43;
    double west_limit_lake_huron = -84.5;
    double east_limit_lake_huron = -80;

    double north_limit_lake_michagan = 45.5;
    double south_limit_lake_michagan = 42;
    double west_limit_lake_michagan = -87.5;
    double east_limit_lake_michagan = -86.0;

    double north_limit_lake_superior = 48;
    double south_limit_lake_superior = 46.5;
    double west_limit_lake_superior = -92;
    double east_limit_lake_superior = -85;

    double extra_great_lakes_pressure_height_correction = Double.MAX_VALUE;
    double num_huidige_obs_breedte = Double.MAX_VALUE;
    double num_huidige_obs_lengte = Double.MAX_VALUE;
    boolean doorgaan = true;

    try {
      num_huidige_obs_breedte =
          (double) myposition.int_latitude_degrees
              + ((double) myposition.int_latitude_minutes / 60);
      num_huidige_obs_lengte =
          (double) myposition.int_longitude_degrees
              + ((double) myposition.int_longitude_minutes / 60);

      if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH) == true) {
        num_huidige_obs_breedte *= -1;
      }

      if (myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST) == true) {
        num_huidige_obs_lengte *= -1;
      }
    } // try
    catch (NumberFormatException ex) {
      String message =
          "[APR] Great Lakes position check failed; NumberFormatException (function: check_at_Great_Lakes)";
      main.log_turbowin_system_message(message);
      doorgaan = false;
    }

    if (doorgaan == true) {
      // at the Great Lakes ?
      if (num_huidige_obs_breedte > south_limit_great_lakes
          && num_huidige_obs_breedte < north_limit_great_lakes
          && num_huidige_obs_lengte > west_limit_great_lakes
          && num_huidige_obs_lengte < east_limit_great_lakes) {
        // Lake Ontario ?
        if (num_huidige_obs_breedte >= south_limit_lake_ontario
            && num_huidige_obs_breedte <= north_limit_lake_ontario
            && num_huidige_obs_lengte >= west_limit_lake_ontario
            && num_huidige_obs_lengte <= east_limit_lake_ontario) {
          extra_great_lakes_pressure_height_correction = 8.99;
        }

        // Lake Erie upper part ?
        else if (num_huidige_obs_breedte >= south_limit_lake_erie_upper
            && num_huidige_obs_breedte <= north_limit_lake_erie_upper
            && num_huidige_obs_lengte >= west_limit_lake_erie_upper
            && num_huidige_obs_lengte <= east_limit_lake_erie_upper) {
          extra_great_lakes_pressure_height_correction = 21.05;
        }

        // Lake Erie lower part ?
        else if (num_huidige_obs_breedte >= south_limit_lake_erie_lower
            && num_huidige_obs_breedte <= north_limit_lake_erie_lower
            && num_huidige_obs_lengte >= west_limit_lake_erie_lower
            && num_huidige_obs_lengte <= east_limit_lake_erie_lower) {
          extra_great_lakes_pressure_height_correction = 21.05;
        }

        // Lake St Clair ?
        else if (num_huidige_obs_breedte >= south_limit_lake_clair
            && num_huidige_obs_breedte <= north_limit_lake_clair
            && num_huidige_obs_lengte >= west_limit_lake_clair
            && num_huidige_obs_lengte <= east_limit_lake_clair) {
          extra_great_lakes_pressure_height_correction = 21.16;
        }

        // Lake Huron ?
        else if (num_huidige_obs_breedte >= south_limit_lake_huron
            && num_huidige_obs_breedte <= north_limit_lake_huron
            && num_huidige_obs_lengte >= west_limit_lake_huron
            && num_huidige_obs_lengte <= east_limit_lake_huron) {
          extra_great_lakes_pressure_height_correction = 21.35;
        }

        // Lake Michagan ?
        else if (num_huidige_obs_breedte >= south_limit_lake_michagan
            && num_huidige_obs_breedte <= north_limit_lake_michagan
            && num_huidige_obs_lengte >= west_limit_lake_michagan
            && num_huidige_obs_lengte <= east_limit_lake_michagan) {
          extra_great_lakes_pressure_height_correction = 21.35;
        }

        // Lake Superior ?
        else if (num_huidige_obs_breedte >= south_limit_lake_superior
            && num_huidige_obs_breedte <= north_limit_lake_superior
            && num_huidige_obs_lengte >= west_limit_lake_superior
            && num_huidige_obs_lengte <= east_limit_lake_superior) {
          extra_great_lakes_pressure_height_correction = 22.24;
        } else {
          // so at the Great lakes but between two Lakes (e.g. in the locks!) or outside a 'main'
          // area
          extra_great_lakes_pressure_height_correction = AT_GREAT_LAKES_OUTSIDE_MAIN_AREAS;
        }

      } // if (num_huidige_obs_breedte > south_limit_great_lakes etc.
      else {
        // not at the Great Lakes
        extra_great_lakes_pressure_height_correction = Double.MAX_VALUE;
      }
    } // if (doorgaan == true)

    return extra_great_lakes_pressure_height_correction;
  }

  private void set_datetime_while_collecting_sensor_data() {
    // called from: class RS232_Class_Receive_Sensor_Data [file main_RS232_RS422.java]
    //              class Ethernet_Class_Receive_UDP [file main_RS232_RS422.java]
    //              class class WiFi_Class_Receive_UDP
    //
    //
    //
    //
    // Never mind the computer is set to UTC or not. SimpleTimeZone with argument UTC convert system
    // date time
    // always to UTC !!
    //

    String system_month_volledig = "";
    int len;

    main.cal_systeem_datum_tijd =
        new GregorianCalendar(
            new SimpleTimeZone(0, "UTC")); // system date time in UTC of this moment
    main.cal_systeem_datum_tijd.getTime(); // now effective
    int system_minute_local = main.cal_systeem_datum_tijd.get(Calendar.MINUTE);

    if (system_minute_local % 5 == 0) {
      if (system_minute_local > 30) {
        main.cal_systeem_datum_tijd.add(Calendar.HOUR_OF_DAY, 1); // add 1 hour
        main.cal_systeem_datum_tijd.getTime();
      }

      int system_year = main.cal_systeem_datum_tijd.get(Calendar.YEAR);
      int system_month =
          main.cal_systeem_datum_tijd.get(
              Calendar.MONTH); // The first month of the year is JANUARY which is 0
      int system_day_of_month =
          main.cal_systeem_datum_tijd.get(
              Calendar.DAY_OF_MONTH); // The first day of the month has value 1
      int system_hour_of_day = main.cal_systeem_datum_tijd.get(Calendar.HOUR_OF_DAY);

      system_month_volledig = DateTimeUtils.convert_month(system_month);

      mydatetime.year = Integer.toString(system_year); // for progress main screen and IMMT
      mydatetime.month = system_month_volledig; // for progress main screen
      mydatetime.day = Integer.toString(system_day_of_month); // for progress main screen
      mydatetime.hour = Integer.toString(system_hour_of_day); // for progress main screen

      /* update date and time on TurboWin+ main screen */
      main.date_time_fields_update();

      // determine code figures for month (not for operational obs only for IMMT storage)
      //
      mydatetime.MM_code =
          Integer.toString(system_month + 1); // +1 because system month started with 0 (January)

      len = 2; // MM_code always 2 characters width e.g. 03
      if (mydatetime.MM_code.length() < len) // pad on left with zeros
      {
        mydatetime.MM_code =
            "0000000000".substring(0, len - mydatetime.MM_code.length()) + mydatetime.MM_code;
      }

      // determine code figures for day of the month (for obs and IMMT)
      //
      mydatetime.YY_code =
          Integer.toString(system_day_of_month); // system_day_of_month e.g. 1,2,11,23,29

      len = 2; // YY_code always 2 characters width e.g. 03
      if (mydatetime.YY_code.length() < len) // pad on left with zeros
      {
        mydatetime.YY_code =
            "0000000000".substring(0, len - mydatetime.YY_code.length()) + mydatetime.YY_code;
      }

      // determine code figures for hour of day (for obs and IMMT)
      //
      mydatetime.GG_code =
          Integer.toString(system_hour_of_day); // system_hour_of_day e.g. 1,2,11,23

      len = 2; // GG_code always 2 characters width e.g. 03
      if (mydatetime.GG_code.length() < len) // pad on left with zeros
      {
        mydatetime.GG_code =
            "0000000000".substring(0, len - mydatetime.GG_code.length()) + mydatetime.GG_code;
      }
    } // if (system_minute_local % 5 == 0)
  }

  public static void RS422_initialise_AWS_Sensor_Data_For_Display() {
    // initialisation
    main.date_from_AWS_present = false;
    main.time_from_AWS_present = false;
    main.latitude_from_AWS_present = false;
    main.longitude_from_AWS_present = false;
    main.COG_from_AWS_present = false;
    main.SOG_from_AWS_present = false;
    main.true_heading_from_AWS_present = false;
    main.pressure_sensor_level_from_AWS_present = false;
    main.pressure_MSL_from_AWS_present = false;
    main.pressure_tendency_from_AWS_present = false;
    main.pressure_characteristic_from_AWS_present = false;
    //    NB in AWS connected mode the date and time parameters by default only from AWS!
    //    NB in AWS connected mode the GPS parameters (lat, lon, SOG, COG, true heading) by default
    // only from AWS!
    //    NB in AWS connected mode the air pressure parameters by default only from AWS! (if not
    // present than still observer cannot insert them manually)
    main.air_temp_from_AWS_present = false;
    main.rh_from_AWS_present = false;
    main.SST_from_AWS_present = false;
    main.relative_wind_speed_from_AWS_present = false;
    main.relative_wind_dir_from_AWS_present = false;
    main.true_wind_speed_from_AWS_present = false;
    main.true_wind_dir_from_AWS_present = false;
    main.true_wind_gust_from_AWS_present = false;
    main.true_wind_gust_dir_from_AWS_present = false;
    main.VOT_from_AWS_present = false;

    mydatetime.year = "";
    mydatetime.month = "";
    mydatetime.day = "";
    mydatetime.hour = "";
    myposition.latitude_hemisphere = "";
    myposition.latitude_degrees = "";
    myposition.latitude_minutes = "";
    myposition.longitude_hemisphere = "";
    myposition.longitude_degrees = "";
    myposition.longitude_minutes = "";
    myposition.course = "";
    myposition.speed = "";
    mywind.ship_ground_course = "";
    mywind.ship_ground_speed = "";
    mywind.ship_heading = "";
    mybarometer.pressure_reading = "";
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";
    main.VOT = Integer.MAX_VALUE;

    if (main.jTextField36.getForeground().getRGB() != main.input_color_from_observer.getRGB()) {
      mytemp.air_temp = "";
    }

    if (main.jTextField37.getForeground().getRGB() != main.input_color_from_observer.getRGB()) {
      mytemp.wet_bulb_temp = "";
    }

    if (main.jTextField38.getForeground().getRGB() != main.input_color_from_observer.getRGB()) {
      mytemp.RH = "";
      mytemp.double_rv = main.INVALID; // double
      mytemp.double_dew_point = main.INVALID; // double
    }

    if (main.jTextField40.getForeground().getRGB() != main.input_color_from_observer.getRGB()) {
      mytemp.sea_water_temp = "";
    }

    // if (main.jTextField16.getForeground().getRGB() != main.input_color_from_observer.getRGB())
    // {
    //   mywind.int_true_wind_speed                  = main.INVALID;      // integer
    //   mywind.wind_speed                           = "";
    // }
    //
    // if (main.jTextField17.getForeground().getRGB() != main.input_color_from_observer.getRGB())
    // {
    //   mywind.int_true_wind_dir                    = main.INVALID;      // integer
    //   mywind.wind_dir                             = "";
    // }
    if (main.jTextField16.getForeground().getRGB()
        != main.input_color_from_observer.getRGB()) // apparent wind
    {
      mywind.int_relative_wind_dir = main.INVALID;
      mywind.int_relative_wind_speed = main.INVALID;
      mywind.wind_speed = "";
      mywind.wind_dir = "";
    }

    if (main.jTextField17.getForeground().getRGB()
        != main.input_color_from_observer.getRGB()) // true wind
    {
      mywind.int_true_wind_speed = main.INVALID; // integer
      mywind.int_true_wind_dir = main.INVALID; // integer
    }
  }

  private void RS422_Read_AWS_Sensor_Data_For_Display() {
    new Rs422ReadAwsSensorDataForDisplayWorkflow(this).start();
  }

  static void RS422_initialise_AWS_Sensor_Data_For_AWSR() {
    // called from: RS422_Read_And_Send_Sensor_Data_For_AWSR() [main_RS232_RS422.java]

    // Date and Time
    mydatetime.year = "";
    mydatetime.month = "";
    mydatetime.day = "";
    mydatetime.hour = "";
    mydatetime.MM_code = ""; // month of year (only for IMMT)
    mydatetime.YY_code = ""; // day of the month
    mydatetime.GG_code = ""; // hour of obs

    // position
    myposition.Qc_code = "";
    myposition.int_latitude_degrees = main.INVALID;
    myposition.int_latitude_minutes = main.INVALID;
    myposition.latitude_degrees = "";
    myposition.latitude_hemisphere = "";
    myposition.lalala_code = "";
    myposition.int_longitude_degrees = main.INVALID;
    myposition.int_longitude_minutes = main.INVALID;
    myposition.longitude_degrees = "";
    myposition.longitude_hemisphere = "";
    myposition.lolololo_code = "";

    // Course and speed
    myposition.Ds_code = "";
    myposition.vs_code = "";
    mywind.ship_heading = "";

    // Air pressure
    mybarometer.pressure_reading_corrected = "";
    mybarometer.pressure_msl_corrected = "";
    mybarometer.PPPP_code = "";
    mybarograph.pressure_amount_tendency = "";
    mybarograph.a_code = "";
    mybarograph.ppp_code = "";

    // Temperatures
    mytemp.air_temp = "";
    mytemp.sn_TTT_code = "";
    mytemp.TTT_code = "";
    mytemp.double_rv = main.INVALID; // double
    mytemp.sea_water_temp = "";
    mytemp.ss_TsTsTs_code = "";
    mytemp.immt_sst_indicator = "";
    mytemp.TsTsTs_code = "";

    // wind
    mywind.RWS_code = "";
    mywind.RWD_code = "";
    mywind.int_true_wind_speed = main.INVALID; // integer
    mywind.ff_code = "";
    mywind.int_true_wind_dir = main.INVALID; // integer
    mywind.dd_code = "";
    mywind.HDG_code = "";
    mywind.COG_code = "";
    mywind.SOG_code = "";

    // wind related (IMMT only)
    mywind.SLL_code = "";
    mywind.sl_code = "";
    mywind.hh_code = "";
  }

  boolean RS422_Check_Date_Time_Last_Saved_Record_For_AWSR(
      final String record_aws_date, final String record_aws_time) {
    // called from: RS422_Read_And_Send_Sensor_Data_For_AWSR() [main_RS232_RS422.java]
    //
    // NB record_aws_date format: yyyyMMdd
    // NB record_aws_time format: HHMmmss

    boolean record_date_time_ok = true;

    if ((record_aws_date.length() != 8) || (record_aws_time.length() != 6)) {
      record_date_time_ok = false;
    }

    if (record_date_time_ok) {
      SimpleDateFormat sdf9 =
          new SimpleDateFormat(
              "yyyyMMddHHmm"); // HH hour in day (0-23) note there is also hh (then also with am,
      // pm); July = 07 !!
      sdf9.setTimeZone(TimeZone.getTimeZone("UTC"));

      // String hulp_GPS_yyyyMMdd = "20" + GPS_date.substring(4) + GPS_date.substring(2,4) +
      // GPS_date.substring(0,2);
      // String hulp_GPS_HHmm = GPS_time.substring(0, 4);
      // String hulp_fix_datum_tijd_minuten = hulp_GPS_yyyyMMdd + hulp_GPS_HHmm;    // eg final
      // result : 202303940927
      String hulp_record_datum_tijd_minuten =
          record_aws_date + record_aws_time.substring(0, 4); // eg final result : 202303940927
      Date hulp_record_date = null;
      try {
        hulp_record_date = sdf9.parse(hulp_record_datum_tijd_minuten);

        long system_sec = System.currentTimeMillis();
        long timeDiff =
            Math.abs(hulp_record_date.getTime() - system_sec) / (60 * 1000); // timeDiff in minutes

        // System.out.println("+++ system_sec: " + system_sec);
        // System.out.println("+++ hulp_record_date: " + hulp_record_date);
        // System.out.println("+++ difference [minuten]: " + timeDiff); //differencs in min

        // String record_pressure_present = null;

        if (timeDiff > TIMEDIFF_AWSR_DATA) // max 10 minutes difference AWS time <--> computer time
        {
          record_date_time_ok = false;
          String message =
              "[AWSR] AWS last saved record date/time obsolete; > "
                  + (int) TIMEDIFF_AWSR_DATA
                  + " minutes old compared with computer date/time (time difference = "
                  + timeDiff
                  + " minutes)";
          main.log_turbowin_system_message(message);
        }
      } // try
      catch (ParseException ex) {
        record_date_time_ok = false;
        String message = "[AWSR] AWS date/time format not ok (" + ex + ")";
        main.log_turbowin_system_message(message);
      }
    } // if (record_date_time_ok)

    return record_date_time_ok;
  }

  boolean RS422_Set_Date_Time_Obs_And_IMMT_For_AWSR(
      String record_aws_date, String record_aws_time) {
    // called from: RS422_Read_And_Send_Sensor_Data_For_AWSR() [main_RS232_RS422.java]
    //
    // NB record_aws_date format: yyyyMMdd
    // NB record_aws_time format: HHmmss

    int len = 0;
    boolean record_date_time_ok = true;

    if ((record_aws_date.length() != 8) || (record_aws_time.length() != 6)) {
      record_date_time_ok = false;
      String message = "[AWSR] AWS date/time last saved record format not ok";
      main.log_turbowin_system_message(message);
    }

    if (record_date_time_ok) {
      SimpleDateFormat sdf9 =
          new SimpleDateFormat(
              "yyyyMMddHHmm"); // HH hour in day (0-23) note there is also hh (then also with am,
      // pm); July = 07 !!
      sdf9.setTimeZone(TimeZone.getTimeZone("UTC"));

      String hulp_record_datum_tijd_minuten =
          record_aws_date + record_aws_time.substring(0, 4); // eg final result : 202303940927
      Date hulp_record_date = null;
      try {
        hulp_record_date = sdf9.parse(hulp_record_datum_tijd_minuten);

        Calendar hulp_cal = new GregorianCalendar(new SimpleTimeZone(0, "UTC")); // UTC
        hulp_cal.setTime(hulp_record_date);
        hulp_cal.getTime();

        int hulp_cal_minute_local = hulp_cal.get(Calendar.MINUTE);

        if (hulp_cal_minute_local > 30) {
          hulp_cal.add(Calendar.HOUR_OF_DAY, 1); // add 1 hour
          hulp_cal.getTime();
        }

        int hulp_year = hulp_cal.get(Calendar.YEAR); // e.g. 2023
        int hulp_month =
            hulp_cal.get(Calendar.MONTH); // The first month of the year is JANUARY which is 0
        int hulp_day_of_month =
            hulp_cal.get(Calendar.DAY_OF_MONTH); // The first day of the month has value 1
        int hulp_hour_of_day = hulp_cal.get(Calendar.HOUR_OF_DAY);

        mydatetime.year = Integer.toString(hulp_year); // e.g. 2023
        mydatetime.month = Integer.toString(hulp_month + 1); // now  mydatetime.month [1 .. 12]
        mydatetime.day = Integer.toString(hulp_day_of_month); // [1 .. 31]
        mydatetime.hour = Integer.toString(hulp_hour_of_day); // [0 .. 23]

        // determine code figures for month (for obs and IMMT)
        //
        mydatetime.MM_code = mydatetime.month; // mydatetime.month [1 .. 12]

        len = 2; // MM_code always 2 characters width e.g. 03
        if (mydatetime.MM_code.length() < len) // pad on left with zeros
        {
          mydatetime.MM_code =
              "0000000000".substring(0, len - mydatetime.MM_code.length()) + mydatetime.MM_code;
        }

        // determine code figures for day of the month (for obs and IMMT)
        //
        mydatetime.YY_code = mydatetime.day; // mydatetime.day e.g. 1,2,11,23,29

        len = 2; // YY_code always 2 characters width e.g. 03
        if (mydatetime.YY_code.length() < len) // pad on left with zeros
        {
          mydatetime.YY_code =
              "0000000000".substring(0, len - mydatetime.YY_code.length()) + mydatetime.YY_code;
        }

        // determine code figures for hour of day (for obs and IMMT)
        //
        mydatetime.GG_code = mydatetime.hour; // mydatetime.hour e.g. 0,1,2,11,23

        len = 2; // GG_code always 2 characters width e.g. 03
        if (mydatetime.GG_code.length() < len) // pad on left with zeros
        {
          mydatetime.GG_code =
              "0000000000".substring(0, len - mydatetime.GG_code.length()) + mydatetime.GG_code;
        }

      } // try
      catch (ParseException ex) {
        record_date_time_ok = false;
        String message = "[AWSR] AWS date/time last saved record format not ok (" + ex + ")";
        main.log_turbowin_system_message(message);
      }
    } //  if (record_date_time_ok)

    return record_date_time_ok;
  }

  private void RS422_Read_And_Send_Sensor_Data_For_AWSR(final boolean retry) {
    new Rs422ReadAndSendSensorDataForAwsrWorkflow(this).start(retry);
  }

  void RS422_update_AWS_dasboard_values() {
    new Rs422UpdateAwsDashboardValuesWorkflow(this).start();
  }

  double RS422_convert_wind_speed_to_10m(double wind_speed_at_anemometer_height) {
    // double num_hoogte_anemometer_ship;
    // char* endptr;
    // string info                       = "\0";
    // char char_hulpje[10];
    // const double Zo                   = 0.0002;
    // double herleidingsfactor;

    /* om de wind van sensor hoogte naar 10 meter nivo te reduceren:                  */
    /*                                                                                */
    /* zie: Guide to Meteorological Instruments and Methods of Observation [WMO no.8] */
    /*                                                                                */
    /* Uc = U.Cf.Ct. ln(10/Zou) . ln(60/Zou) ln(10/Zo)                                */
    /*               ---------    --------------------                                */
    /*               ln(z/Zou)    ln(10/Zou) ln(60/Zo)                                */
    /*                                                                                */
    /* waarbij:                                                                       */
    /* Uc = wind speed op 10 meter                                                    */
    /* U = wind speed op sensor hoogte                                                */
    /* z = sensor hoogte                                                              */
    /* Zou = roughness length of the terrain upstream of the wind station             */
    /* Z0 = rougness length in the application                                        */
    /* Cf = flow distortion factor                                                    */
    /* Ct = topographic correction                                                    */
    /*                                                                                */
    /* ter vereenvoudiging: Cf = 1                                                    */
    /*                      Ct = 1                                                    */
    /*                      Zo = Zou                                                  */
    /* dan:                                                                           */
    /*                                                                                */
    /* Uc =  U. ln(10/Zo)                                                             */
    /*          ---------                                                             */
    /*          ln(z/Zo)                                                              */
    /*                                                                                */
    /*  volgens annex "the effective roughness length"van de zelfde publicatie        */
    /* moet voor Zo voor "open sea, fetch at least 5 km" genomen worden 0.0002        */
    /*                                                                                */
    /*                                                                                */
    /* voor het gemak: herleidingsfactor =  ln(10/Zo)                                 */
    /*                                      ---------                                 */
    /*                                      ln(z/Zo)                                  */
    /*                                                                                */

    // if (hoogte_anemometer_ship.compare("\0") != 0)
    //	 {
    //	 num_hoogte_anemometer_ship = strtod(hoogte_anemometer_ship.c_str(), &endptr);
    //   if (num_hoogte_anemometer_ship >= 1 && num_hoogte_anemometer_ship <= 200)
    //    {
    //       herleidingsfactor = log(10 / Zo) / (log(num_hoogte_anemometer_ship / Zo));
    //       num_true_wind_speed = int((double(num_true_wind_speed) * herleidingsfactor) + 0.5);
    //
    //	    /* ook de obs_wind_speed omzetten */
    //	    sprintf(char_hulpje, "%d", num_true_wind_speed);
    //	    obs_wind_speed = string(char_hulpje);
    //    }
    //    else // hoogte buiten range
    //    {
    //	    info = "Height anemometer outside range 1 - 200 metres, windspeed unreliable. ";
    //	    info += "Please correct anemometer height (Maintenance | Station data | fixed sea
    // station).";
    //	    MessageBox(info.c_str(), "TurboWin warning", MB_OK | MB_ICONWARNING);
    //    } // else (hoogte buiten range
    //
    // } // if (hoogte_anemometer.compare("\0") != 0)
    // else // hoogte anemometer niet ingevuld
    // {
    //	 info = "Height anemometer not available, windspeed unreliable. ";
    //	 info += "Please insert anemometer height (Maintenance | Station data | ship).";
    //	 MessageBox(info.c_str(), "TurboWin warning", MB_OK | MB_ICONWARNING);
    // } // else (hoogte anemometer niet ingevuld

    final double Zo = 0.0002;
    int int_height_anemometer = Integer.MAX_VALUE;
    double wind_speed_10m = main.INVALID;

    if (!main.height_anemometer.trim().equals("")) {
      try {
        int_height_anemometer = Integer.parseInt(main.height_anemometer);
      } catch (NumberFormatException ex) {
        int_height_anemometer = Integer.MAX_VALUE;
      }
    } // if (!main.height_anemometer.trim().equals(""))

    if ((int_height_anemometer > 10 && int_height_anemometer < 200)
        && (wind_speed_at_anemometer_height >= 0 && wind_speed_at_anemometer_height < 500)) {
      double conversion_factor = Math.log(10 / Zo) / (Math.log(int_height_anemometer / Zo));

      wind_speed_10m = wind_speed_at_anemometer_height * conversion_factor;
    }

    return wind_speed_10m;
  }

  public static final String WIND_FM13_DASHBOARD = "wind_FM13_dashboard";
  public static final String WIND_101_DASHBOARD = "wind_101_dashboard";
  /*
  private final String STRING_PRESSURE_INTERRUPTION_EXECUTIONEXEPTION_ERROR       = "11111111";
  private final String STRING_PRESSURE_RETRIEVING_ERROR                           = "11111112"; // number without significance (random)
  private final String STRING_PRESSURE_OUTSIDE_RANGE_ERROR                        = "11111113"; // number without significance (random)
  private final String STRING_PRESSURE_NUMBERFORMATECEPTION_ERROR                 = "11111114"; // number without significance (random)
  private final String STRING_PRESSURE_RECORD_GPS_ERROR                           = "11111115"; // number without significance (random)
  private final String STRING_PRESSURE_HEIGHT_CORRECTION_ERROR                    = "11111116"; // number without significance (random)
  private final String STRING_STARX_PART_OBSOLETE_ERROR                           = "11111117"; // number without significance (random)
  private final String STRING_PRESSURE_AMOUNT_TENDENCY_OUTSIDE_RANGE_ERROR        = "11111118"; // number without significance (random)
  private final String STRING_PRESSURE_CHARACTERISTIC_OUTSIDE_RANGE_ERROR         = "11111119"; // number without significance (random)
  private final String STRING_AIR_TEMP_OUTSIDE_RANGE_ERROR                        = "11111120"; // number without significance (random)
  private final String STRING_WET_BULB_OUTSIDE_RANGE_ERROR                        = "11111121"; // number without significance (random)
  private final String STRING_DEWPOINT_OUTSIDE_RANGE_ERROR                        = "11111122"; // number without significance (random)
  private final String STRING_RH_OUTSIDE_RANGE_ERROR                              = "11111123"; // number without significance (random)


  private final String STRING_PRESSURE_IO_FILE_ERROR                              = "4444444"; // number without significance (random)
  //private final String STRING_AIR_TEMP_IO_FILE_ERROR                              = "4444444"; // number without significance (random)
  private final String STRING_PRESSURE_NO_FILE_ERROR                              = "5555555"; // number without significance (random)
  //private final String STRING_AIR_TEMP_NO_FILE_ERROR                              = "5555555"; // number without significance (random)
  private final String STRING_PRESSURE_RECORD_FORMAT_ERROR                        = "6666666"; // number without significance (random)
  //private final String STRING_AIR_TEMP_RECORD_FORMAT_ERROR                        = "6666666"; // number without significance (random)
  private final String STRING_PRESSURE_CHECKSUM_ERROR                             = "7777777"; // number without significance (random)
  //private final String STRING_AIR_TEMP_CHECKSUM_ERROR                             = "7777777"; // number without significance (random)
  private final String STRING_PRESSURE_TIMEDIFF_ERROR                             = "8888888"; // number without significance (random)
  //private final String STRING_AIR_TEMP_TIMEDIFF_ERROR                             = "8888888"; // number without significance (random)
  private final String STRING_PRESSURE_OBS_AGE_ERROR                              = "3333333"; // number without significance (random)
  //private final String STRING_AIR_TEMP_OBS_AGE_ERROR                              = "3333333"; // number without significance (random)
  private final int INT_PRESSURE_IO_FILE_ERROR                                    = 4444444;   // number without significance (see STRING_PRESSURE_IO_FILE_ERROR)
  private final int INT_AIR_TEMP_IO_FILE_ERROR                                    = 4444444;   // number without significance (see STRING_AIR_TEMP_IO_FILE_ERROR)
  private final int INT_PRESSURE_NO_FILE_ERROR                                    = 5555555;   // number without significance (see STRING_PRESSURE_NO_DFILE_ERROR)
  private final int INT_AIR_TEMP_NO_FILE_ERROR                                    = 5555555;   // number without significance (see STRING_AIR_TEMP_NO_DFILE_ERROR)
  private final int INT_PRESSURE_RECORD_FORMAT_ERROR                              = 6666666;   // number without significance (see STRING_PRESSURE_RECORD_FORMAT_ERROR)
  private final int INT_AIR_TEMP_RECORD_FORMAT_ERROR                              = 6666666;   // number without significance (see STRING_AIR_TEMP_RECORD_FORMAT_ERROR)
  private final int INT_PRESSURE_CHECKSUM_ERROR                                   = 7777777;   // number without significance (see STRING_PRESSURE_CHECKSUM_ERROR)
  private final int INT_AIR_TEMP_CHECKSUM_ERROR                                   = 7777777;   // number without significance (see STRING_AIR_TEMP_CHECKSUM_ERROR)
  private final int INT_PRESSURE_TIMEDIFF_ERROR                                   = 8888888;   // number without significance (see STRING_PRESSURE_TIMEDIFF_ERROR)
  private final int INT_AIR_TEMP_TIMEDIFF_ERROR                                   = 8888888;   // number without significance (see STRING_AIR_TEMP_TIMEDIFF_ERROR)
  private final int INT_PRESSURE_OBS_AGE_ERROR                                    = 3333333;   // number without significance (see STRING_PRESSURE_OBS_AGE_ERROR)
  private final int INT_AIR_TEMP_OBS_AGE_ERROR                                    = 3333333;   // number without significance (see STRING_AIR_TEMP_OBS_AGE_ERROR)
  */
  public static final long TIMEDIFF_SENSOR_DATA =
      10L; // retrieved sensor data (pressure) max ? minutes old, after this the data is considered
  // not valid anymore
  private static final long TIMEDIFF_GPS_DATA =
      10L; // retrieved data (GPS) max ?minutes old, after this the data is considered not valid
  // anymore
  private static final long TIMEDIFF_AWSR_DATA =
      10L; // retrieved sensor data AWSR mode max ?minutes old, after this the data is considered
  // not valid anymore
  public static final double HPA_TO_INHG = 0.02952998751; // WOW
  private final int DELAY_NEW_DATA_CHECK_LOOP =
      60000; // time between checks new aws data in msec (e.g. 1 min = 1 * 60 * 1000 = 60000)
  private final int INITIAL_DELAY_NEW_DATA_CHECK_LOOP =
      60000; // time to wait after start up in msec (e.g. 2 min = 2 * 60 * 1000 = 120000)
  private final long MAX_AGE_AWS_DATA =
      120000; // 300000;    // time in msec (e.g. 5 min = 5 * 60 * 1000 = 300000) // before
  // listening thread cancelling/restarting
  private final int APR_RETRY_MINUTES = 3; // APR
  private final int AWSR_RETRY_MINUTES = 3; // AWSR
  public static final int AT_GREAT_LAKES_OUTSIDE_MAIN_AREAS =
      777; // APR; symbolic value, only to indicate the ship is at the great lakes but mabe in a
  // lock (not possible to determine height corr)
  private final int CANCEL_GPS_THREAD = 40; // minutes, for reconnecting lost connection
  private final int START_GPS_THREAD = 43; // minutes, for reconnecting lost connection
  private final int CANCEL_BAROMETER_THREAD = 30; // minutes, for reconnecting lost connection
  private final int START_BAROMETER_THREAD = 33; // minutes, for reconnecting lost connection
  private final int CANCEL_THERMOMETER_THREAD = 30; // minutes, for reconnecting lost connection
  private final int START_THERMOMETER_THREAD = 33; // minutes, for reconnecting lost connection
  private final int CANCEL_AWS_THREAD = 30; // minutes, for reconnecting lost connection
  private final int START_AWS_THREAD = 33; // minutes, for reconnecting lost connection
  private final long MAX_AGE_GPS_DATA =
      300000; // time in msec (e.g. 10 min = 10 * 60 * 1000 = 600000) before listening thread
  // cancelling/restarting
  public static final int MAX_AGE_STARX_OBS_DATA = 600; // time in sec (e.g. 10 min = 10 * 60 = 600)
  public static final int MAX_GPS_ARRAY =
      200; // theoretically: array indices 0 - 180 (included) -> 181 array places ; representing the
  // minutes since last received GPS data string but sometimes same data in two consecutive
  // array places -> increased to 200

  // private int comm_error_messagebox_shown                                         = 0;
  Color color_dark_green;
  private GregorianCalendar
      cal_RS232_GPS_system_date_time; // for restarting RS232 GPS listener if no data data was
  // received
  private GregorianCalendar
      cal_RS422_system_date_time; // for restarting RS422 (AWS) listener if no data data was
  // received
  private GregorianCalendar
      cal_RS232_system_date_time; // for restarting RS232 (Barometer) listener if no data data was
  // received
  private GregorianCalendar
      cal_RS232_system_date_time_II; // for restarting RS232 (thermomter) listener if no data data
  // was received
  // private GregorianCalendar cal_WiFi_system_date_time;                            // for
  // restarting WiFi listener if no Wifi data was received
  // private GregorianCalendar cal_Ethernet_system_date_time;                        // for
  // restarting WiFi listener if no Ethernet data was received
  private GregorianCalendar cal_WOW_systeem_datum_tijd; // format: yyyyMMddHHmm
  private GregorianCalendar cal_APR_system_date_time;
  private GregorianCalendar cal_APR_system_dt;
  private GregorianCalendar cal_AWSR_system_date_time;
  private GregorianCalendar cal_AWSR_system_dt;
  private GregorianCalendar cal_AWS_system_dt;
  private GregorianCalendar cal_APR_AWS_system_dt; // pop-up dashboard
  private GregorianCalendar cal_delete_datum_tijd;
  private GregorianCalendar cal_sensor_datum_tijd;
  private GregorianCalendar check_presence_parameter_system_datum_tijd;
  public static GregorianCalendar cal_system_date_time;
  private SimpleDateFormat sdf7;
  public static SimpleDateFormat sdf8; // for pressure reading pop-up in system tray
  // private SimpleDateFormat sdf9;                                                            //
  // for GPS check gps time vz sustem time
  private final int MAX_COMPLETED_BAROMETER_PORT_CHECKS = 2;
  private final int MAX_COMPLETED_AWS_PORT_CHECKS = 2;
  private final int MAX_COMPLETED_THERMOMETER_PORT_CHECKS = 2;
  private int system_minute;
  public static long
      last_new_data_received_TimeMillis; // returns the time of last aws or barometer data receipt
  // in milliseconds since midnight, January 1, 1970 UTC.
  private long
      last_new_data_received_TimeMillis_II; // returns the time of last thermometer data receipt in
  // milliseconds since midnight, January 1, 1970 UTC.
  public static long
      last_new_GPS_data_received_TimeMillis; // returns the time of last GPS data receipt in
  // milliseconds since midnight, January 1, 1970 UTC.
  private int check_new_data_timer_delay;
  private int check_new_data_timer_delay_II;
  private Timer check_new_data_timer; // for checking sensor data not obsolete (AWS or barometer)
  private Timer check_new_data_timer_II; // for checking sensor data not obsolete (thermometer)
  private Timer check_new_GPS_data_timer; // for checking sensor data not obsolete (GPS)
  private int check_new_GPS_data_timer_delay;
  private static final GpsTrackHistory GPS_TRACK_HISTORY = new GpsTrackHistory(MAX_GPS_ARRAY);
  public static final String[] GPS_array_device_log = new String[MAX_GPS_ARRAY + 1];
  private boolean in_screen_pop_up_period = false; // screen pop-up (add visual obs)

  // private SwingWorker WiFi_worker;
  private WiFi_Class_Receive_UDP WiFi_worker; // SwingWorker with reference (for WiFi connection)
  private Ethernet_Class_Receive_UDP
      Ethernet_worker; // SwingWorker with reference (for Ethernet connection)
  private RS232_Class_Receive_Sensor_Data
      RS232_worker; // SwingWorker with reference (for RS232 connection
  private RS232_Class_Receive_Sensor_Data_II
      RS232_worker_II; // SwingWorker with reference (for RS232 connection
  // private RS422_Class_Receive_Sensor_Data RS422_worker;                           // SwingWorker
  // with reference (for RS422 connection)
  private RS422_V3_Class_Receive_Sensor_Data
      RS422_worker; // SwingWorker with reference (for RS422 connection)
  private RS232_GPS_Class_Receive_Data
      RS232_GPS_worker; // SwingWorker with reference (for RS232 GPS connection)

  private final StringBuilder message_peumb = new StringBuilder();

  private static final double CELCIUS_TO_KELVIN_FACTOR = 273.15;
  final double M_S_TO_KNOTS = 1.94384;

  public static String test_record = "";
  public static String test_record_II = "";

  // RS232 GPS
  public static String GPS_defaultPort = null; // port info for the system (to open/close etc)
  public static String GPS_defaultPort_descriptive = null; // port info for messages and logging
  private final int MAX_COMPLETED_GPS_PORT_CHECKS = 2;

  private final int GPS_NMEA_0183_parity = 0; // 0 = none parity
  private final int GPS_NMEA_0183_data_bits = 8;
  private final int GPS_NMEA_0183_stop_bits = 1;
  private final int GPS_NMEA_0183_flow_control =
      SerialPort.FLOW_CONTROL_DISABLED; // SerialPort.FLOWCONTROL_NONE;

  public static String GPS_date = "";
  public static String GPS_time = "";
  public static String GPS_latitude = "";
  public static String GPS_latitude_hemisphere = "";
  public static String GPS_longitude = "";
  public static String GPS_longitude_hemisphere = "";
  public static String GPS_latitude_earlier = ""; // for computing COG and SOG (Ds, Vs)
  public static String GPS_longitude_earlier = ""; // for vomputing COG and SOG (Ds, Vs)
  public static String GPS_latitude_earlier_wind = ""; // for computing COG and SOG (wind)
  public static String GPS_longitude_earlier_wind = ""; // for vomputing COG and SOG (wind)

  // APR and AWSR
  public static int APR_server_response_code = 0;
  public static int AWSR_server_response_code = 0;
  // private String format_101_obs                                                   = "";
  static String format_101_obs = "";

  // public static String hulp_apr_main_screen_pressure_reading_corrected            = "";
  // public static String hulp_apr_main_screen_pressure_msl_corrected                = "";

  // Dashboard
  public static double dashboard_double_last_update_record_pressure_ic = Double.MAX_VALUE;
  public static double dashboard_double_last_update_record_ppp = Double.MAX_VALUE;
  public static String dashboard_string_last_update_record_date_time =
      ""; // pc based (NOT output string based)
  public static String dashboard_string_last_update_record_date =
      ""; // AWS output string based (mwasured at)
  public static String dashboard_string_last_update_record_time =
      ""; // AWS output string based (mwasured at)
  public static String dashboard_string_last_update_record_latitude = "";
  public static String dashboard_string_last_update_record_longitude = "";
  public static String dashboard_string_last_update_record_SOG = "";
  public static String dashboard_string_last_update_record_COG = "";
  public static String dashboard_string_last_update_record_heading = "";
  public static double dashboard_double_last_update_record_MSL_pressure_ic = Double.MAX_VALUE;
  public static double dashboard_double_last_update_record_sensor_level_pressure_ic =
      Double.MAX_VALUE;
  public static double dashboard_double_last_update_record_air_temp = Double.MAX_VALUE;
  public static int dashboard_int_last_update_record_true_wind_dir = Integer.MAX_VALUE;
  public static int dashboard_int_last_update_record_true_wind_speed = Integer.MAX_VALUE;
  public static int dashboard_int_last_update_record_true_wind_gust =
      Integer.MAX_VALUE; // true wind gust speed
  public static int dashboard_int_last_update_record_relative_wind_dir = Integer.MAX_VALUE;
  public static int dashboard_int_last_update_record_relative_wind_speed = Integer.MAX_VALUE;
  public static double dashboard_double_last_update_record_sst = Double.MAX_VALUE;
  public static double dashboard_double_last_update_record_humidity = Double.MAX_VALUE;
  public static double dashboard_double_last_update_record_pressure_tendency = Double.MAX_VALUE;
  public static int dashboard_int_last_update_record_true_wind_gust_dir = Integer.MAX_VALUE;
  public static int dashboard_int_last_update_record_VOT = Integer.MAX_VALUE;

  public static int dashboard_int_last_update_record_true_wind_speed_10m =
      Integer.MAX_VALUE; // only used on wind radar dashboard
  public static int dashboard_int_last_update_record_true_wind_gust_10m =
      Integer.MAX_VALUE; // only used on wind radar dashboard
  public static int dashboard_int_last_update_record_relative_wind_speed_10m =
      Integer.MAX_VALUE; // only used on wind radar dashboard
} // public class main_RS232_RS422

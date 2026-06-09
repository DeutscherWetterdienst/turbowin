package turbowin;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
// import javax.xml.bind.DatatypeConverter;                           // needed for Java 6 but
// doesn't exist in Java 12
import java.util.Base64; // Java 8 and higher
// import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public final class myemailsettings extends javax.swing.JFrame {

  /* inner class popupListener (Gmail app password) */
  /*
  class PopupListener extends MouseAdapter
  {
     @Override
     public void mousePressed(MouseEvent e)
     {
        ShowPopup(e);
        //System.out.println("Popup menu will be visible!");
     }

     @Override
     public void mouseReleased(MouseEvent e)
     {
        ShowPopup(e);
     }

     private void ShowPopup(MouseEvent e)
     {
        if (e.isPopupTrigger())
        {
           popup.show(e.getComponent(), e.getX(), e.getY());
        }
     }
  }
  */

  /* inner class popupListener2 (Yahoo app password) */
  /*
  class PopupListener2 extends MouseAdapter
  {
     @Override
     public void mousePressed(MouseEvent e)
     {
        ShowPopup(e);
        //System.out.println("Popup menu will be visible!");
     }

     @Override
     public void mouseReleased(MouseEvent e)
     {
        ShowPopup(e);
     }

     private void ShowPopup(MouseEvent e)
     {
        if (e.isPopupTrigger())
        {
           popup2.show(e.getComponent(), e.getX(), e.getY());
        }
     }
  }
  */

  /* inner class popupListener3 (Custom -app- password) */
  class PopupListener3 extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      ShowPopup(e);
      // System.out.println("Popup menu will be visible!");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      ShowPopup(e);
    }

    private void ShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup3.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  /** Creates new form myemailsettings */
  public myemailsettings() {
    initComponents();
    initEmailComponents();
    setLocation(main.x_pos_main_frame, main.y_pos_main_frame);
  }

  /***********************************************************************************************/
  /*                                                                                             */
  /*                                                                                             */
  /*                                                                                             */
  /***********************************************************************************************/
  private void initEmailComponents() {
    ////////////////////// initialise /////////////////////////

    // [ALL]
    jTextField1.setText(""); // obs by email recipient
    jTextField2.setText(""); // obs by email subject
    jTextField4.setText(""); // obs by email CC

    // [FOTRMAT 101]
    jRadioButton5.setSelected(false); // format 101 obs in body
    jRadioButton6.setSelected(false); // format 101 obs in attachment

    // [SMTP HOST]                                                 // NB sometimes also called LOCAL
    // HOST
    // jTextField5.setText("");                                       // SMTP host email server
    // jTextField10.setText("");                                      // SMTP host ship email
    // address
    // jTextField11.setText("");                                      // SMTP host password
    // jTextField12.setText("");                                      // SMTP host port

    // [GMAIL]
    // jTextField6.setText("");
    // jTextField7.setText("");
    // jRadioButton1.setSelected(false);
    // jRadioButton2.setSelected(false);

    // [YAHOO]
    // jTextField8.setText("");
    // jTextField9.setText("");
    // jRadioButton3.setSelected(false);
    // jRadioButton4.setSelected(false);

    // [CUSTOM]
    jTextField13.setText(""); // Email address
    jTextField14.setText(""); // server
    jTextField15.setText(""); // password
    jTextField16.setText(""); // port
    jRadioButton7.setSelected(false); // TLS
    jRadioButton8.setSelected(false); // SSL
    jCheckBox1.setSelected(true); // STARTTLS
    jRadioButton1.setSelected(false); // default email module (java-jakarta)
    jRadioButton2.setSelected(false); // default email module (python)

    // [LOGS]
    jTextField3.setText(""); // logs email recipient

    if (main.ship_name.equals("")) {
      // ship name still blank (so use generic ship name)
      jTextField5.setText(main.METEO_LOGS + " " + "<ship name>");
    } else {
      // ship name was inserted and is available
      jTextField5.setText(main.METEO_LOGS + " " + main.ship_name);
    }
    jTextField5.setEnabled(
        false); // defualt, alsways disabled (only for showing the user want will be the subject)

    /////////////////////// pop up menu /////////////////////////

    // NB only for the Gmail and Yahoo app password field

    /*
    // pop up menu Gmail app password
    popup = new JPopupMenu();

    JMenuItem menuItem_a = new JMenuItem("copy");
    menuItem_a.addActionListener(new java.awt.event.ActionListener()
    {
       @Override
       public void actionPerformed(ActionEvent e)
       {
          jTextField7.copy();
       }
    });
    popup.add(menuItem_a);

    JMenuItem menuItem_b = new JMenuItem("paste");
    menuItem_b.addActionListener(new java.awt.event.ActionListener()
    {
       @Override
       public void actionPerformed(ActionEvent e)
       {
          jTextField7.paste();
       }
    });
    popup.add(menuItem_b);

    MouseListener popupListener = new myemailsettings.PopupListener();
    jTextField7.addMouseListener(popupListener);

    // tool tip text
    jTextField7.setToolTipText("right click for copy or paste");
    */

    /*
    // pop up menu Yahoo app password
    popup2 = new JPopupMenu();

    JMenuItem menuItem_c = new JMenuItem("copy");
    menuItem_c.addActionListener(new java.awt.event.ActionListener()
    {
       @Override
       public void actionPerformed(ActionEvent e)
       {
          jTextField9.copy();
       }
    });
    popup2.add(menuItem_c);

    JMenuItem menuItem_d = new JMenuItem("paste");
    menuItem_d.addActionListener(new java.awt.event.ActionListener()
    {
       @Override
       public void actionPerformed(ActionEvent e)
       {
          jTextField9.paste();
       }
    });
    popup2.add(menuItem_d);

    MouseListener popupListener2 = new myemailsettings.PopupListener2();
    jTextField9.addMouseListener(popupListener2);

    // tool tip text
    jTextField9.setToolTipText("right click for copy or paste");
    */

    // pop up menu Custom (app) password
    //
    popup3 = new JPopupMenu();

    JMenuItem menuItem_e = new JMenuItem("copy");
    menuItem_e.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            jTextField15.copy();
          }
        });
    popup3.add(menuItem_e);

    JMenuItem menuItem_f = new JMenuItem("paste");
    menuItem_f.addActionListener(
        new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            jTextField15.paste();
          }
        });
    popup3.add(menuItem_f);

    MouseListener popupListener3 = new myemailsettings.PopupListener3();
    jTextField15.addMouseListener(popupListener3);

    // tool tip text
    jTextField15.setToolTipText("right click for copy or paste");

    //////////////// put back earlier inserted values (if applicable) /////////////////

    // [ALL]
    jTextField1.setText(main.obs_email_recipient); // Obs E-mail address recipient
    jTextField4.setText(main.obs_email_cc); // Obs E-mail address cc
    jTextField2.setText(main.obs_email_subject); // Obs E-mail subject

    // [FORMAT 101]
    if (main.obs_101_email.equals(main.FORMAT_101_BODY)) {
      jRadioButton5.setSelected(true);
      jRadioButton6.setSelected(false);
    } else if (main.obs_101_email.equals(main.FORMAT_101_ATTACHEMENT)) {
      jRadioButton5.setSelected(false);
      jRadioButton6.setSelected(true);
    } else // default = 'attachment'
    {
      jRadioButton5.setSelected(false); // format 101 obs in body
      jRadioButton6.setSelected(true); // format 101 obs in attachment
    }

    /*
    // [SMTP HOST]
    jTextField5.setText(main.local_email_server);
    jTextField10.setText(main.your_ship_address);

    if (main.smtp_host_password.equals("") == false)
    {
       jTextField11.setText(password_stars);
    }

    jTextField12.setText(main.smtp_host_port);
    */

    /*
    // [GMAIL]
    jTextField6.setText(main.your_gmail_address);
    if (main.gmail_app_password.equals("") == false)
    {
       jTextField7.setText(password_stars);
    }

    if (main.gmail_security.equals(main.GMAIL_TLS))
    {
       jRadioButton1.setSelected(true);
       jRadioButton2.setSelected(false);
    }
    else if (main.gmail_security.equals(main.GMAIL_SSL))
    {
       jRadioButton1.setSelected(false);
       jRadioButton2.setSelected(true);
    }
    else // default = TLS
    {
       jRadioButton1.setSelected(true);
       jRadioButton2.setSelected(false);
    }
    */

    /*
    // [YAHOO]
    jTextField8.setText(main.your_yahoo_address);
    if (main.yahoo_app_password.equals("") == false)
    {
       jTextField9.setText(password_stars);
    }

    if (main.yahoo_security.equals(main.YAHOO_TLS))
    {
       jRadioButton3.setSelected(true);
       jRadioButton4.setSelected(false);
    }
    else if (main.yahoo_security.equals(main.YAHOO_SSL))
    {
       jRadioButton3.setSelected(false);
       jRadioButton4.setSelected(true);
    }
    else // default = TLS
    {
       jRadioButton3.setSelected(true);
       jRadioButton4.setSelected(false);
    }
    */

    // [CUSTOM]
    jTextField13.setText(main.your_custom_address);
    jTextField14.setText(main.custom_email_server);

    if (main.custom_password.equals("") == false) {
      jTextField15.setText(password_stars);
    }

    jTextField16.setText(main.custom_port);

    if (main.custom_email_module.equals(main.JAKARTA_EMAIL)) {
      jRadioButton1.setSelected(true); // jakarta email
      jRadioButton2.setSelected(false); // python email
    } else if (main.custom_email_module.equals(main.PYTHON_EMAIL)) {
      jRadioButton1.setSelected(false);
      jRadioButton2.setSelected(true);
    } else {
      // so if nothing was selected before -> jakarta email module will be set as default
      jRadioButton1.setSelected(true);
      jRadioButton2.setSelected(false);
    }

    if (main.custom_security.equals(main.CUSTOM_TLS)) {
      jRadioButton7.setSelected(true);
      jRadioButton8.setSelected(false);
      jCheckBox1.setSelected(false);
    } else if (main.custom_security.equals(main.CUSTOM_TLS_STARTTLS)) {
      jRadioButton7.setSelected(true);
      jRadioButton8.setSelected(false);
      jCheckBox1.setSelected(true);
    } else if (main.custom_security.equals(main.CUSTOM_SSL)) {
      jRadioButton7.setSelected(false);
      jRadioButton8.setSelected(true);
      jCheckBox1.setSelected(false);
    } else if (main.custom_security.equals(main.CUSTOM_SSL_STARTTLS)) {
      jRadioButton7.setSelected(false);
      jRadioButton8.setSelected(true);
      jCheckBox1.setSelected(true);
    } else // custom security mode: not inserted = not activated (NB in custom security mode all
    // email items must be present/set!)
    {
      jRadioButton7.setSelected(false); // TLS
      jRadioButton8.setSelected(false); // SSL
      jCheckBox1.setSelected(false); // recipient
    }

    // [LOGS]
    jTextField3.setText(main.logs_email_recipient); // logs (immt etc) adress recipient

    if (main.log_files_email_send_method.equals(main.LOGS_CUSTOM_EMAIL)) {
      jRadioButton3.setSelected(false); // default email client for logs
      jRadioButton4.setSelected(true); // custom email client for logs
    } else if (main.log_files_email_send_method.equals(main.LOGS_DEFAULT_EMAIL)) {
      jRadioButton3.setSelected(true); // default email client for logs
      jRadioButton4.setSelected(false); // custom email client for logs
    } else {
      // so if nothing was selected before -> 'default email client' will be set as default for
      // sending the (immt etc) log files
      jRadioButton3.setSelected(true); // default email client for logs
      jRadioButton4.setSelected(false); // custom email client for logs
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonGroup1 = new javax.swing.ButtonGroup();
    buttonGroup2 = new javax.swing.ButtonGroup();
    buttonGroup3 = new javax.swing.ButtonGroup();
    buttonGroup4 = new javax.swing.ButtonGroup();
    buttonGroup5 = new javax.swing.ButtonGroup();
    buttonGroup6 = new javax.swing.ButtonGroup();
    jLabel3 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jTextField1 = new javax.swing.JTextField();
    jLabel8 = new javax.swing.JLabel();
    jTextField4 = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jTextField2 = new javax.swing.JTextField();
    jPanel4 = new javax.swing.JPanel();
    jLabel9 = new javax.swing.JLabel();
    jRadioButton5 = new javax.swing.JRadioButton();
    jRadioButton6 = new javax.swing.JRadioButton();
    jPanel8 = new javax.swing.JPanel();
    jLabel19 = new javax.swing.JLabel();
    jTextField13 = new javax.swing.JTextField();
    jLabel20 = new javax.swing.JLabel();
    jTextField14 = new javax.swing.JTextField();
    jLabel21 = new javax.swing.JLabel();
    jTextField15 = new javax.swing.JTextField();
    jLabel22 = new javax.swing.JLabel();
    jTextField16 = new javax.swing.JTextField();
    jRadioButton7 = new javax.swing.JRadioButton();
    jRadioButton8 = new javax.swing.JRadioButton();
    jCheckBox1 = new javax.swing.JCheckBox();
    jLabel12 = new javax.swing.JLabel();
    jRadioButton1 = new javax.swing.JRadioButton();
    jRadioButton2 = new javax.swing.JRadioButton();
    jLabel4 = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    jTextField3 = new javax.swing.JTextField();
    jLabel5 = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    jRadioButton3 = new javax.swing.JRadioButton();
    jRadioButton4 = new javax.swing.JRadioButton();
    jLabel10 = new javax.swing.JLabel();
    jTextField5 = new javax.swing.JTextField();
    jSeparator1 = new javax.swing.JSeparator();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    jLabel11 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("E-mail settings");
    setMaximumSize(new java.awt.Dimension(1000, 700));
    setMinimumSize(new java.awt.Dimension(1000, 700));
    setResizable(false);

    jLabel3.setText("for 'Output -> Obs by Email' and AP[&T]R / AWSR");

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel3.setPreferredSize(new java.awt.Dimension(820, 46));

    jLabel1.setText("[ALL] address recipient");

    jLabel8.setText("cc*");

    jLabel2.setText("obs subject");

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel3Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(
                        jLabel1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        175,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(
                        jTextField1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        200,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel8)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(
                        jTextField4,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        150,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(24, 24, 24)
                    .addComponent(
                        jLabel2,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        72,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jTextField2,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        140,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(103, Short.MAX_VALUE)));
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel3Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(
                                jTextField1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(
                                jTextField2,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(
                                jTextField4,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(14, Short.MAX_VALUE)));

    jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel4.setPreferredSize(new java.awt.Dimension(438, 46));

    jLabel9.setText("[FORMAT 101]");

    buttonGroup3.add(jRadioButton5);
    jRadioButton5.setText("obs in body");

    buttonGroup3.add(jRadioButton6);
    jRadioButton6.setText("obs in attachment");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel4Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(
                        jLabel9,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        175,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(
                        jRadioButton5,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        102,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(26, 26, 26)
                    .addComponent(
                        jRadioButton6,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        148,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel4Layout
                    .createSequentialGroup()
                    .addContainerGap(11, Short.MAX_VALUE)
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jRadioButton5)
                            .addComponent(jRadioButton6))
                    .addContainerGap(10, Short.MAX_VALUE)));

    jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel19.setText("[CUSTOM] your email address");

    jLabel20.setText("server");

    jLabel21.setText("password");

    jLabel22.setText("port");

    buttonGroup4.add(jRadioButton7);
    jRadioButton7.setText("TLS");

    buttonGroup4.add(jRadioButton8);
    jRadioButton8.setText("SSL");

    jCheckBox1.setText("STARTTLS");

    jLabel12.setText("email module to be used");

    buttonGroup5.add(jRadioButton1);
    jRadioButton1.setText("primary (java)");

    buttonGroup5.add(jRadioButton2);
    jRadioButton2.setText("secondary (python)");

    javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
    jPanel8.setLayout(jPanel8Layout);
    jPanel8Layout.setHorizontalGroup(
        jPanel8Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel8Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel8Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(
                                jPanel8Layout
                                    .createSequentialGroup()
                                    .addComponent(
                                        jLabel19,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        175,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(
                                        jTextField13,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        200,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(
                                jPanel8Layout
                                    .createSequentialGroup()
                                    .addGroup(
                                        jPanel8Layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(
                                                jLabel12,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                175,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(
                                                jLabel21,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                175,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(
                                        jPanel8Layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(
                                                jTextField15,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                200,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(
                                                jRadioButton1,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                120,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(35, 35, 35)
                    .addGroup(
                        jPanel8Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(
                                jPanel8Layout
                                    .createSequentialGroup()
                                    .addComponent(
                                        jLabel20,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        37,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(
                                        jTextField14,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        200,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(264, Short.MAX_VALUE))
                            .addGroup(
                                jPanel8Layout
                                    .createSequentialGroup()
                                    .addComponent(
                                        jLabel22,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        37,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(
                                        jTextField16,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        50,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addComponent(jRadioButton7)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jRadioButton8)
                                    .addGap(18, 18, 18)
                                    .addComponent(jCheckBox1)
                                    .addContainerGap(210, Short.MAX_VALUE))))
            .addGroup(
                jPanel8Layout
                    .createSequentialGroup()
                    .addGap(323, 323, 323)
                    .addComponent(
                        jRadioButton2,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        159,
                        javax.swing.GroupLayout.PREFERRED_SIZE)));
    jPanel8Layout.setVerticalGroup(
        jPanel8Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel8Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel8Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(
                                jTextField13,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)
                            .addComponent(
                                jTextField14,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(
                        jPanel8Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(
                                jTextField15,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(
                                jTextField16,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton7)
                            .addComponent(jRadioButton8)
                            .addComponent(jCheckBox1))
                    .addGap(18, 18, 18)
                    .addGroup(
                        jPanel8Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton2))
                    .addContainerGap(10, Short.MAX_VALUE)));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel1Layout
                    .createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addGroup(
                        jPanel1Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(
                                jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE)
                            .addComponent(
                                jPanel8,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                jPanel3,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                939,
                                Short.MAX_VALUE))
                    .addContainerGap()));
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel1Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(
                        jPanel3,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(
                        jPanel4,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(
                        jPanel8,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                    .addContainerGap()));

    jLabel4.setText("for 'Maintenance -> Move log files by Email'");

    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jLabel5.setText("logs email recipient");

    jLabel6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
    jLabel6.setForeground(new java.awt.Color(0, 0, 255));
    jLabel6.setText(
        "These log files include important data which is of particular value for climate studies");

    jLabel7.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
    jLabel7.setForeground(new java.awt.Color(0, 0, 255));
    jLabel7.setText(
        "Downloading of the log files should be done at routine intervals (ideally not exceeding 6 months)");

    buttonGroup6.add(jRadioButton3);
    jRadioButton3.setText("via default email client on this pc");

    buttonGroup6.add(jRadioButton4);
    jRadioButton4.setText("via CUSTOM email client (see above)");

    jLabel10.setText("logs subject");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel2Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(
                                jPanel2Layout
                                    .createSequentialGroup()
                                    .addComponent(
                                        jLabel6,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        386,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(572, Short.MAX_VALUE))
                            .addGroup(
                                jPanel2Layout
                                    .createSequentialGroup()
                                    .addComponent(
                                        jLabel7,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        442,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(
                                jPanel2Layout
                                    .createSequentialGroup()
                                    .addGroup(
                                        jPanel2Layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(
                                                jPanel2Layout
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        jLabel10,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        165,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement
                                                            .RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                    .addComponent(
                                                        jTextField5,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        248,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(
                                                jPanel2Layout
                                                    .createSequentialGroup()
                                                    .addComponent(
                                                        jLabel5,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        175,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(27, 27, 27)
                                                    .addComponent(
                                                        jTextField3,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        248,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addGroup(
                                        jPanel2Layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(
                                                jRadioButton3,
                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                249,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(
                                                jRadioButton4,
                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                249,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(104, 104, 104)))));
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel2Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jTextField3,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton3)
                            .addComponent(jLabel5))
                    .addGap(18, 18, 18)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton4)
                            .addGroup(
                                jPanel2Layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(
                                        jTextField5,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel7)
                    .addContainerGap()));

    jButton1.setText("OK");
    jButton1.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            OK_button_actionPerformed(evt);
          }
        });

    jButton2.setText("Cancel");
    jButton2.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            Cancel_button_actionPerformed(evt);
          }
        });

    jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    jLabel11.setText("* optional");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(
                                jPanel2,
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                jPanel1,
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(jSeparator1)
                            .addGroup(
                                layout
                                    .createSequentialGroup()
                                    .addGroup(
                                        layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(
                                                jLabel11,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                203,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(
                                                jLabel4,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                923,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(
                                                jLabel3,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                923,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 45, Short.MAX_VALUE)))
                    .addContainerGap())
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addGap(400, 400, 400)
                    .addComponent(
                        jButton1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        80,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(
                        jButton2,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        80,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    layout.setVerticalGroup(
        layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout
                    .createSequentialGroup()
                    .addGap(37, 37, 37)
                    .addComponent(
                        jLabel3,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        14,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jPanel1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel11)
                    .addGap(63, 63, 63)
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jPanel2,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                    .addGap(18, 18, 18)
                    .addComponent(
                        jSeparator1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        2,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(15, 15, 15)
                    .addGroup(
                        layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                jButton1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                25,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                jButton2,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                25,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(15, 15, 15)));

    jLabel3
        .getAccessibleContext()
        .setAccessibleName(
            "required for: Output > Obs by Email (default / SMTP host / Gmail / Yahoo/ Customl' and optional for AP[&T]R and AWSR");

    pack();
  } // </editor-fold>//GEN-END:initComponents

  /***********************************************************************************************/
  /*                                                                                             */
  /*                                                                                             */
  /*                                                                                             */
  /***********************************************************************************************/
  private void OK_button_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_OK_button_actionPerformed
    // TODO add your handling code here:

    /* read meta data components */

    // [ALL]
    main.obs_email_recipient = jTextField1.getText().trim();
    main.obs_email_subject = jTextField2.getText().trim();
    main.obs_email_cc = jTextField4.getText().trim();

    // [FORMAT 101]
    if (jRadioButton5.isSelected() == true) // email 101 format in body
    {
      main.obs_101_email = main.FORMAT_101_BODY;
    } else if (jRadioButton6.isSelected() == true) // email 101 format in attachement
    {
      main.obs_101_email = main.FORMAT_101_ATTACHEMENT;
    }

    /*
    // [SMTP HOST]
    main.local_email_server          = jTextField5.getText().trim();
    main.your_ship_address           = jTextField10.getText().trim();

    smtp_host_password_plain         = jTextField11.getText().trim();

    // save password (in encrypted mode) only if something was changed
    if ((smtp_host_password_plain.equals(password_stars) == false) && (smtp_host_password_plain.equals("") == false) && (smtp_host_password_plain.length() >= 4))
    {
       main.smtp_host_password       = encrypt(smtp_host_password_plain);
    }
    if (smtp_host_password_plain.equals("") == true)
    {
       main.smtp_host_password       = "";
    }

    main.smtp_host_port              = jTextField12.getText().trim();
    */

    /*
    // {GMAIL]
    main.your_gmail_address          = jTextField6.getText().trim();
    gmail_app_password_plain         = jTextField7.getText().trim();

    // save password (in encrypted mode) only if something was changed
    if ((gmail_app_password_plain.equals(password_stars) == false) && (gmail_app_password_plain.equals("") == false) && (gmail_app_password_plain.length() >= 8))
    {
       main.gmail_app_password       = encrypt(gmail_app_password_plain);
    }
    if (gmail_app_password_plain.equals(""))
    {
       main.gmail_app_password       = "";
    }

    if (jRadioButton1.isSelected() == true)
    {
       main.gmail_security           = main.GMAIL_TLS;
    }
    else if (jRadioButton2.isSelected() == true)
    {
       main.gmail_security           = main.GMAIL_SSL;
    }
    */

    /*
    // [YAHOO]
    main.your_yahoo_address          = jTextField8.getText().trim();
    yahoo_app_password_plain         = jTextField9.getText().trim();

    // save password (in encrypted mode) only if something was changed
    if ((yahoo_app_password_plain.equals(password_stars) == false) && (yahoo_app_password_plain.equals("") == false) && (yahoo_app_password_plain.length() >= 8))
    {
       main.yahoo_app_password       = encrypt(yahoo_app_password_plain);
    }
    if (yahoo_app_password_plain.equals(""))
    {
       main.yahoo_app_password       = "";
    }

    if (jRadioButton3.isSelected() == true)
    {
       main.yahoo_security           = main.YAHOO_TLS;
    }
    else if (jRadioButton4.isSelected() == true)
    {
       main.yahoo_security           = main.YAHOO_SSL;
    }
    */

    // [CUSTOM]
    main.your_custom_address = jTextField13.getText().trim();
    main.custom_email_server = jTextField14.getText().trim();
    custom_password_plain = jTextField15.getText().trim();
    main.custom_port = jTextField16.getText().trim();
    STARTTLS_local = jCheckBox1.isSelected() == true; // true or false

    // save password (in encrypted mode) only if something was changed
    if ((custom_password_plain.equals(password_stars) == false)
        && (custom_password_plain.equals("") == false)
        && (custom_password_plain.length() >= 4)) {
      main.custom_password = encrypt(custom_password_plain);
    }
    if (custom_password_plain.equals("")) {
      main.custom_password = "";
    }

    // security (TLS, SSL, STARTTLS)
    if (jRadioButton7.isSelected() == true) // TLS
    {
      if (STARTTLS_local == true) {
        main.custom_security = main.CUSTOM_TLS_STARTTLS;
      } else {
        main.custom_security = main.CUSTOM_TLS;
      }
    } else if (jRadioButton8.isSelected() == true) // SSL
    {
      if (STARTTLS_local == true) {
        main.custom_security = main.CUSTOM_SSL_STARTTLS;
      } else {
        main.custom_security = main.CUSTOM_SSL;
      }
    }

    // email module (python, jakarta) for sending observations
    if (jRadioButton1.isSelected() == true) // java (jakarta) email module
    {
      main.custom_email_module = main.JAKARTA_EMAIL;
    } else if (jRadioButton2.isSelected() == true) // python email module
    {
      main.custom_email_module = main.PYTHON_EMAIL;
    }

    // [LOGS] (immt etc)
    main.logs_email_recipient = jTextField3.getText().trim();
    if (jRadioButton3.isSelected() == true) // default email client for logs
    {
      main.log_files_email_send_method = main.LOGS_DEFAULT_EMAIL;
    } else if (jRadioButton4.isSelected() == true) // custom email client for logs
    {
      main.log_files_email_send_method = main.LOGS_CUSTOM_EMAIL;
    }

    if (main.offline_mode_via_cmd == true) {
      main.schrijf_configuratie_regels();
    } else // so offline_via_jnlp mode or online (webstart) mode
    {
      // main.set_muffin();
      main.schrijf_configuratie_regels();
    }

    // pop-up info message
    //
    // String info = "Changes will take effect inmediatelly. Not necessary to restart";
    String info = "Changes will take full effect after a " + main.APPLICATION_NAME + " restart";
    JOptionPane.showMessageDialog(
        null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);

    // close this E-mail settings input page
    //
    setVisible(false);
    dispose();
  } // GEN-LAST:event_OK_button_actionPerformed

  /***********************************************************************************************/
  /*                                                                                             */
  /*                                                                                             */
  /*                                                                                             */
  /***********************************************************************************************/
  private void Cancel_button_actionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_Cancel_button_actionPerformed
    // TODO add your handling code here:

    /* close input page */
    setVisible(false);
    dispose();
  } // GEN-LAST:event_Cancel_button_actionPerformed

  /***********************************************************************************************/
  /*                                                                                             */
  /*                                                                                             */
  /*                                                                                             */
  /***********************************************************************************************/
  public static String encrypt(String plainText) {
    String encryptedText = "";
    try {
      Cipher cipher = Cipher.getInstance(cipherTransformation);
      byte[] key = encryptionKey.getBytes(characterEncoding);
      SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithm);
      IvParameterSpec ivparameterspec = new IvParameterSpec(key);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
      byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));

      Base64.Encoder encoder = Base64.getEncoder(); // Java 8 and higher
      encryptedText = encoder.encodeToString(cipherText);

      // encryptedText = DatatypeConverter.printBase64Binary(cipherText);           // Java 6 and 7
      // but not possible Java 12 and higher.
    } catch (NoSuchAlgorithmException
        | NoSuchPaddingException
        | UnsupportedEncodingException
        | InvalidKeyException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException E) {
      // System.err.println("[EMAIL] Encrypt Exception : " + E.getMessage());

      String info = "encrypt exception: " + E.getMessage();
      main.log_turbowin_system_message("[EMAIL] " + info);

      // JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " info",
      // JOptionPane.INFORMATION_MESSAGE);
    }

    return encryptedText;
  }

  /***********************************************************************************************/
  /*                                                                                             */
  /*                                                                                             */
  /*                                                                                             */
  /***********************************************************************************************/
  public static String decrypt(String encryptedText) {
    String decryptedText = "";
    try {
      Cipher cipher = Cipher.getInstance(cipherTransformation);
      byte[] key = encryptionKey.getBytes(characterEncoding);
      SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithm);
      IvParameterSpec ivparameterspec = new IvParameterSpec(key);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);

      Base64.Decoder decoder = Base64.getDecoder(); // Java 8 and higher
      byte[] cipherText = decoder.decode(encryptedText.getBytes("UTF8"));
      // byte[] cipherText = DatatypeConverter.parseBase64Binary(encryptedText);     // Java 6/7 but
      // not availble in Java 12 etc.

      decryptedText = new String(cipher.doFinal(cipherText), "UTF-8");
    } catch (NoSuchAlgorithmException
        | NoSuchPaddingException
        | UnsupportedEncodingException
        | InvalidKeyException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException E) {
      // System.err.println("decrypt Exception : "+E.getMessage());
      String info = "decrypt exception: " + E.getMessage();
      main.log_turbowin_system_message("[EMAIL] " + info);
    }

    return decryptedText;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            new myemailsettings().setVisible(true);
          }
        });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.ButtonGroup buttonGroup2;
  private javax.swing.ButtonGroup buttonGroup3;
  private javax.swing.ButtonGroup buttonGroup4;
  private javax.swing.ButtonGroup buttonGroup5;
  private javax.swing.ButtonGroup buttonGroup6;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JCheckBox jCheckBox1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel19;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel20;
  private javax.swing.JLabel jLabel21;
  private javax.swing.JLabel jLabel22;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel8;
  private javax.swing.JRadioButton jRadioButton1;
  private javax.swing.JRadioButton jRadioButton2;
  private javax.swing.JRadioButton jRadioButton3;
  private javax.swing.JRadioButton jRadioButton4;
  private javax.swing.JRadioButton jRadioButton5;
  private javax.swing.JRadioButton jRadioButton6;
  private javax.swing.JRadioButton jRadioButton7;
  private javax.swing.JRadioButton jRadioButton8;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JTextField jTextField1;
  private javax.swing.JTextField jTextField13;
  private javax.swing.JTextField jTextField14;
  private javax.swing.JTextField jTextField15;
  private javax.swing.JTextField jTextField16;
  private javax.swing.JTextField jTextField2;
  private javax.swing.JTextField jTextField3;
  private javax.swing.JTextField jTextField4;
  private javax.swing.JTextField jTextField5;
  // End of variables declaration//GEN-END:variables

  private static final String encryptionKey = EmailSettingsConstants.encryptionKey;
  private static final String characterEncoding = EmailSettingsConstants.characterEncoding;
  private static final String cipherTransformation = EmailSettingsConstants.cipherTransformation;
  private static final String aesEncryptionAlgorithm =
      EmailSettingsConstants.aesEncryptionAlgorithm;

  // private String gmail_app_password_plain                   = "";
  // private String yahoo_app_password_plain                   = "";
  // private String smtp_host_password_plain                   = "";
  private String custom_password_plain = "";
  private final String password_stars = "******";
  private Boolean STARTTLS_local = false;

  // JPopupMenu popup;
  // JPopupMenu popup2;
  JPopupMenu popup3;
}

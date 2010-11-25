
/*
 * Connection.java
 *
 * Created on Mar 3, 2009, 7:24:58 PM
 */
package com.ben.chat.client.views;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.clientconnection.ClientConnection;
import com.ben.crypt.md5.MD5Password;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author bendaccache
 */
public class ConnectionView extends javax.swing.JDialog implements Serializable {

    BufferedImage image;
    private ClientChatView cv;
    private String username;
    private Hashtable<String, String> user;
    private Hashtable<String, String> host;
    private Hashtable<String, String> port;
    private ImageIcon iconDisplayPic;

    /** Creates new form Connection
     * @param parent windows
     * @param modal modal true if the windows is modal, false if not.
     * @param cv main window.
     */
    public ConnectionView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.cv = ClientChatView.getInstance();
        initComponents();
        this.loadSettings();
        initLanguage();
    }

    private void addTOHashAndSerialize() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            user.put(comboUser.getSelectedItem().toString(), comboUser.getSelectedItem().toString());
            File f = new File("Conf/user.dat");
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();
            fos.close();
            host.put(comboHost.getSelectedItem().toString(), comboHost.getSelectedItem().toString());
            f = new File("Conf/host.dat");
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(host);
            oos.close();
            fos.close();
            port.put(comboPort.getSelectedItem().toString(), comboPort.getSelectedItem().toString());
            f = new File("Conf/port.dat");
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(port);
            oos.close();
            fos.close();
            f = new File("Conf/DP.dat");
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(iconDisplayPic);
            oos.close();
            fos.close();

        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO", ex);
            }
            try {
                oos.close();
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO", ex);
            }
        }
    }

    private void initLanguage() {
        InputStream fisdp;
        ObjectInputStream oisdp;
        try {
            fisdp = new FileInputStream("Conf/langage.dat");
            oisdp = new ObjectInputStream(fisdp);
            String language = (String) oisdp.readObject();
            cv.setLanguage(language);
            oisdp.close();
            fisdp.close();
        } catch (ClassNotFoundException ex) {
            ErrorLogger.getLogger().logError("Class not found", ex);
        } catch (IOException ex) {
            if (System.getProperty("user.language").equalsIgnoreCase("FR")) {
                cv.setLanguage("Francais");
            } else {
                cv.setLanguage("English");

            }
        }
        usernameLabel.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Username"));
        passLabel.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("pass_label"));
        hostLabel.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Host"));
        portLabel.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Port"));
        connectButton.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Connect"));
        exitButton.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Exit"));
        EditPic.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Change_Display_Pic"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        changePicture = new javax.swing.JPopupMenu();
        EditPic = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        comboPort = new javax.swing.JComboBox();
        portLabel = new javax.swing.JLabel();
        panelPic = new javax.swing.JPanel();
        displayPic = new javax.swing.JLabel();
        hostLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        usernameLabel = new javax.swing.JLabel();
        passLabel = new javax.swing.JLabel();
        changePicButton = new javax.swing.JButton();
        comboHost = new javax.swing.JComboBox();
        comboUser = new javax.swing.JComboBox();
        exitButton = new javax.swing.JButton();
        hasAlphaAgainLabel = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();

        changePicture.setName("changePicture"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getResourceMap(ConnectionView.class);
        EditPic.setText(resourceMap.getString("EditPic.text")); // NOI18N
        EditPic.setName("EditPic"); // NOI18N
        EditPic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditPicActionPerformed(evt);
            }
        });
        changePicture.add(EditPic);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
        setModal(true);
        setResizable(false);
        setUndecorated(true);

        jTabbedPane1.setName(""); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        comboPort.setEditable(true);
        comboPort.setName("comboPort"); // NOI18N

        portLabel.setText(resourceMap.getString("portLabel.text")); // NOI18N
        portLabel.setName("portLabel"); // NOI18N

        panelPic.setName("panelPic"); // NOI18N

        displayPic.setText(resourceMap.getString("displayPic.text")); // NOI18N
        displayPic.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        displayPic.setName("displayPic"); // NOI18N

        org.jdesktop.layout.GroupLayout panelPicLayout = new org.jdesktop.layout.GroupLayout(panelPic);
        panelPic.setLayout(panelPicLayout);
        panelPicLayout.setHorizontalGroup(
            panelPicLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelPicLayout.createSequentialGroup()
                .addContainerGap()
                .add(displayPic, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
        );
        panelPicLayout.setVerticalGroup(
            panelPicLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, panelPicLayout.createSequentialGroup()
                .addContainerGap()
                .add(displayPic, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
        );

        hostLabel.setText(resourceMap.getString("hostLabel.text")); // NOI18N
        hostLabel.setName("hostLabel"); // NOI18N

        passwordField.setText(resourceMap.getString("passwordField.text")); // NOI18N
        passwordField.setName("passwordField"); // NOI18N

        usernameLabel.setText(resourceMap.getString("usernameLabel.text")); // NOI18N
        usernameLabel.setName("usernameLabel"); // NOI18N

        passLabel.setText(resourceMap.getString("passLabel.text")); // NOI18N
        passLabel.setName("passLabel"); // NOI18N

        changePicButton.setIcon(resourceMap.getIcon("changePicButton.icon")); // NOI18N
        changePicButton.setText(resourceMap.getString("changePicButton.text")); // NOI18N
        changePicButton.setName("changePicButton"); // NOI18N
        changePicButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                changePicButtonMousePressed(evt);
            }
        });

        comboHost.setEditable(true);
        comboHost.setName("comboHost"); // NOI18N

        comboUser.setEditable(true);
        comboUser.setName("comboUser"); // NOI18N

        exitButton.setIcon(resourceMap.getIcon("exitButton.icon")); // NOI18N
        exitButton.setText(resourceMap.getString("exitButton.text")); // NOI18N
        exitButton.setName("exitButton"); // NOI18N
        exitButton.setOpaque(true);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        hasAlphaAgainLabel.setForeground(resourceMap.getColor("hasAlphaAgainLabel.foreground")); // NOI18N
        hasAlphaAgainLabel.setName("hasAlphaAgainLabel"); // NOI18N

        connectButton.setIcon(resourceMap.getIcon("connectButton.icon")); // NOI18N
        connectButton.setText(resourceMap.getString("connectButton.text")); // NOI18N
        connectButton.setMaximumSize(new java.awt.Dimension(90, 29));
        connectButton.setMinimumSize(new java.awt.Dimension(90, 29));
        connectButton.setName("connectButton"); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(changePicButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(connectButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(hasAlphaAgainLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                        .add(12, 12, 12)
                        .add(exitButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(portLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(hostLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(passLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(usernameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(comboPort, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(comboHost, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(passwordField)
                            .add(comboUser, 0, 188, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelPic, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(usernameLabel)
                            .add(comboUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(passLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(hostLabel)
                            .add(comboHost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 15, Short.MAX_VALUE)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(portLabel)
                            .add(comboPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(panelPic, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(connectButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(4, 4, 4))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(changePicButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(hasAlphaAgainLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(exitButton))))
                .add(58, 58, 58))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 491, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 253, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * methode apellee lorsque on appuie sur le bouton connect et ouvre une connexion avec le serveur
     * @param evt
     */
    private void loadSettings() {
        FileInputStream fisdp = null;
        {
            ObjectInputStream oisdp = null;

            FileInputStream fishost = null;
            FileInputStream fisport = null;
            FileInputStream fisuser = null;
            ObjectInputStream oishost = null;
            ObjectInputStream oisuser = null;
            ObjectInputStream oisport = null;
            try {
                try {
                    File f = new File("Conf");
                    f.mkdir();
                    fisuser = new FileInputStream("Conf/user.dat");
                    oisuser = new ObjectInputStream(fisuser);
                    user = (Hashtable<String, String>) oisuser.readObject();
                    Enumeration e = user.elements();
                    while (e.hasMoreElements()) {
                        comboUser.addItem(e.nextElement());
                    }
                    oisuser.close();
                    fisuser.close();
                } catch (FileNotFoundException fnf) {
                    comboUser.addItem("Username");
                    user = new Hashtable<String, String>();
                }
                try {
                    Enumeration e;
                    fishost = new FileInputStream("Conf/Host.dat");
                    oishost = new ObjectInputStream(fishost);
                    host = (Hashtable<String, String>) oishost.readObject();
                    e = host.elements();
                    while (e.hasMoreElements()) {
                        comboHost.addItem(e.nextElement());
                    }
                    oishost.close();
                    fishost.close();
                } catch (FileNotFoundException fnf) {
                    comboHost.addItem("Host");
                    host = new Hashtable<String, String>();
                }
                try {
                    Enumeration e;
                    fisport = new FileInputStream("Conf/Port.dat");
                    oisport = new ObjectInputStream(fisport);
                    port = (Hashtable<String, String>) oisport.readObject();
                    e = port.elements();
                    while (e.hasMoreElements()) {
                        comboPort.addItem(e.nextElement());
                    }
                    oisport.close();
                    fisport.close();
                } catch (FileNotFoundException fnf) {
                    comboPort.addItem("Port");
                    port = new Hashtable<String, String>();
                }
                try {
                    fisdp = new FileInputStream("Conf/DP.dat");
                    oisdp = new ObjectInputStream(fisdp);
                    iconDisplayPic = (ImageIcon) oisdp.readObject();
                    displayPic.setIcon(iconDisplayPic);
                    oisdp.close();
                    fisdp.close();

                } catch (FileNotFoundException e) {
                    ErrorLogger.getLogger().logError("File not found", e);
                }


            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO", ex);
            } catch (ClassNotFoundException ex) {
                ErrorLogger.getLogger().logError("Class not found", ex);
            }

        }
    }

    private void EditPicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditPicActionPerformed
        BufferedImage mImage = null;
        String source = filechoose();
        File inputFile = new File(source);
        try {
            mImage = ImageIO.read(inputFile);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error reading image", ex);
        }
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(mImage);
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(100, 130, java.awt.Image.SCALE_SMOOTH);
            iconDisplayPic = new ImageIcon(newimg);
            displayPic.setIcon(iconDisplayPic);
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_EditPicActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        String str = comboPort.getSelectedItem().toString();
        int port = 0;
        try {
            port = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(rootPane, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Invalid_port"));
        }
        ClientConnection cl = null;
        if (port < 1024 || port > 65536)//on regarde si le port est valide
        {
            hasAlphaAgainLabel.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("1024_<=_Port_<=_65536"));
        } else if (comboHost.getSelectedItem().toString().equals("") || comboHost.getSelectedItem().toString().equals(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Host"))) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Incorrect_hostname"), "Error", 1);
        } else if (comboUser.getSelectedItem().toString().equals("") || comboUser.getSelectedItem().toString().equals(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Username")) || comboUser.getSelectedItem().toString().contains(" ")) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Incorrect_username"), "Error", 1);
        } else {
            Cursor cur = new Cursor(Cursor.WAIT_CURSOR);//on change le curseur d'attente pour pas que l'utilisateur s'impatiente
            Cursor def = new Cursor(Cursor.DEFAULT_CURSOR);
            this.setCursor(cur);
            cv.setUsername(comboUser.getSelectedItem().toString());
            connectButton.setEnabled(false);
            addTOHashAndSerialize();

            try {
                try {
                    String pass = "";
                    for (int i = 0; i < passwordField.getPassword().length; i++) {
                        pass += passwordField.getPassword()[i];
                    }
                    String encodedPass=MD5Password.getEncodedPassword(pass);
                    cl = new ClientConnection(this,comboHost.getSelectedItem().toString(), port, comboUser.getSelectedItem().toString(), encodedPass);//on cree la connetion
                } catch (ClassNotFoundException ex) {
                    ErrorLogger.getLogger().logError("Class not found", ex);
                }
            } catch (UnknownHostException e) {
                comboHost.setFocusable(false);
                comboUser.setFocusable(false);
                comboPort.setFocusable(false);
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Unknown_server"), "Error", 1);
                cl = null;
                comboHost.setFocusable(true);
                comboUser.setFocusable(true);
                comboPort.setFocusable(true);
                this.setCursor(def);
                connectButton.setEnabled(true);
            } catch (IOException e) {
                comboHost.setFocusable(false);
                comboUser.setFocusable(false);
                comboPort.setFocusable(false);
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Unknown_server"), "Error", 1);
                cl = null;
                comboPort.setFocusable(true);
                comboUser.setFocusable(true);
                comboHost.setFocusable(true);
                this.setCursor(def);
                connectButton.setEnabled(true);
            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(null, "Security problem", "Error", 1);
                cl = null;
                this.setCursor(def);
                connectButton.setEnabled(true);
            }
            if (cl != null) {//si la connection est ouverte on ferme la fenetre
                cv.setSocket(cl, comboHost.getSelectedItem().toString(), comboPort.getSelectedItem().toString());
                cv.setDisplayPic(iconDisplayPic);
                this.dispose();

            }
        }
}//GEN-LAST:event_connectButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        dispose();
        System.exit(0);
}//GEN-LAST:event_exitButtonActionPerformed

    private void changePicButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePicButtonMousePressed
        Point loc;
        changePicture.setInvoker(changePicButton);
        loc = evt.getPoint();
        changePicture.show(changePicButton, loc.x, loc.y);
}//GEN-LAST:event_changePicButtonMousePressed

    private String filechoose() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".jpeg") || f.isDirectory();
            }

            public String getDescription() {
                return java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Image_files");
            }
        });

        int r = chooser.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            String name1 = chooser.getSelectedFile().getAbsolutePath();
            return name1;
        }
        return "";
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem EditPic;
    private javax.swing.JButton changePicButton;
    private javax.swing.JPopupMenu changePicture;
    private javax.swing.JComboBox comboHost;
    private javax.swing.JComboBox comboPort;
    private javax.swing.JComboBox comboUser;
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel displayPic;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel hasAlphaAgainLabel;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel panelPic;
    private javax.swing.JLabel passLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}

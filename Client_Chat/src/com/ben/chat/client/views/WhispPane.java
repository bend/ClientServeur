/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WhispPane.java
 *
 * Created on Mar 6, 2009, 6:04:59 PM
 */
package com.ben.chat.client.views;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.recordsound.Record;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author bendaccache
 */
public class WhispPane extends javax.swing.JPanel {

    private ClientChatView cv;
    private String destUsername;
    private StyledDocument styledDocument;
    private boolean firstMessage = true;
    private int textColor = Color.black.getRGB();
    private Record rec;
    private ByteArrayOutputStream audioFile;
    private SecureMessageView secureView = null;

    /** Creates new form WhispPanel */
    public WhispPane(String username) {
        initComponents();
        this.cv = ClientChatView.getInstance();
        this.destUsername = username;
        this.writeTextOnWhispField("***Private conversation with " + username + "***", true, false, 14, ChatConstants.vert);
        personalDP.setIcon(cv.iconDisplayPic);
        ChangeLanguage();
    }

    /**
     * methode apellee quand le l'utilisateur avec qui on parle se connecte ou se deconnecte
     * elle active ou desactive le champ ou l'on tape le message
     * @param b
     */
    public void setMessageFieldActive(boolean b) {
        whispMessageField.setEditable(b);
        whispButtonSend.setEnabled(b);
    }

    private static String getNameStyle(boolean bold, boolean italic, int size, Color color) {
        StringBuffer sb = new StringBuffer();

        if (bold) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        if (italic) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        sb.append(size);
        sb.append(color.getRGB());

        return sb.toString();
    }

    public void prepareConversationText(int RGB, String msg, String sender) {
        this.writeTextOnWhispField(sender + " " + java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("said") + " :", true, true, 13, new Color(RGB));
        this.writeTextOnWhispField(msg, false, true, 12, new Color(RGB));
    }

    public void setUsername(String newUsername) {
        this.destUsername = newUsername;
    }

    /**
     * met le contenu du champ de conversation dans un string et le retourne
     * @return le string contenant le contenu du champ
     */
    public String getWhispFieldContents() {
        String contents = null;
        try {
            contents = styledDocument.getText(0, styledDocument.getLength());
        } catch (BadLocationException ex) {
            ErrorLogger.getLogger().logError("", ex);
        }
        return contents;
    }

    /**
     * efface le contenu du champ de conversation
     * @throws javax.swing.text.BadLocationException
     */
    public void clearWhispField() throws BadLocationException {
        styledDocument.remove(0, styledDocument.getLength());
    }

    void disposeSecure() {
        if (secureView != null) {
            secureView.dispose();
            secureView = null;
        }
    }

    void setDp(Object obj) {
        userDP.setIcon((ImageIcon) obj);
    }

    public void sendDpToOther() {
        cv.sendInt(ChatConstants.SEND_DP);//on envoi le code dalerte
        cv.sendText(destUsername);//on envoi le nom du destinataire
        cv.sendDp();

    }

    public void startSecureChat() {
        secureView = new SecureMessageView(destUsername);
        secureView.startSecuredChat();
    }

    void setPublicKey(Object obj) {
        secureView.setPublicKey(obj);

    }

    void showCryptedConv(String crypted) {
        secureView.showCryptedConv(crypted);
    }

    private Style getStyle(boolean bold, boolean italic, int size, Color color) {
        String styleName = getNameStyle(bold, italic, size, color);
        Style style = styledDocument.getStyle(styleName);
        if (style != null) {
            return style;
        } else {
            Style styleDefaut = styledDocument.getStyle(StyleContext.DEFAULT_STYLE);
            style = styledDocument.addStyle(styleName, styleDefaut);
            StyleConstants.setBold(style, bold);
            StyleConstants.setItalic(style, italic);
            StyleConstants.setFontSize(style, size);
            StyleConstants.setForeground(style, color);

            return style;
        }
    }

    /**
     * ecrit du texte formate sur le champ de conversation
     * @param texte texte a ecrire
     * @param bold booleen gras
     * @param italic boolean italic
     * @param size boolean taille
     * @param color boolean color
     */
    public void writeTextOnWhispField(String texte, boolean bold, boolean italic, int size, Color color) {
        try {
            styledDocument = whispField.getStyledDocument();
            styledDocument.insertString(styledDocument.getLength(), texte + "\n", getStyle(bold, italic, size, color));
            whispField.setCaretPosition(styledDocument.getLength());
        } catch (Exception e) {
            ErrorLogger.getLogger().logError("", e);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        changeDpPopup = new javax.swing.JPopupMenu();
        changeDpMenuItem = new javax.swing.JMenuItem();
        jScrollPane2 = new javax.swing.JScrollPane();
        whispMessageField = new javax.swing.JTextArea();
        whispButtonSend = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        whispField = new javax.swing.JTextPane();
        userDP = new javax.swing.JLabel();
        personalDP = new javax.swing.JLabel();
        changeDpButton = new javax.swing.JButton();
        colorChooserButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        recButton = new javax.swing.JButton();
        secureButton = new javax.swing.JButton();

        changeDpPopup.setName("changeDpPopup"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getResourceMap(WhispPane.class);
        changeDpMenuItem.setText(resourceMap.getString("changeDpMenuItem.text")); // NOI18N
        changeDpMenuItem.setName("changeDpMenuItem"); // NOI18N
        changeDpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDpMenuItemActionPerformed(evt);
            }
        });
        changeDpPopup.add(changeDpMenuItem);

        setMinimumSize(new java.awt.Dimension(300, 400));
        setName("Form"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        whispMessageField.setColumns(20);
        whispMessageField.setLineWrap(true);
        whispMessageField.setRows(5);
        whispMessageField.setName("whispMessageField"); // NOI18N
        whispMessageField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                whispMessageFieldKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(whispMessageField);

        whispButtonSend.setIcon(resourceMap.getIcon("whispButtonSend.icon")); // NOI18N
        whispButtonSend.setText(resourceMap.getString("whispButtonSend.text")); // NOI18N
        whispButtonSend.setName("whispButtonSend"); // NOI18N
        whispButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                whispButtonSendActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        whispField.setBackground(resourceMap.getColor("whispField.background")); // NOI18N
        whispField.setEditable(false);
        whispField.setDoubleBuffered(true);
        whispField.setName("whispField"); // NOI18N
        jScrollPane1.setViewportView(whispField);

        userDP.setText(resourceMap.getString("userDP.text")); // NOI18N
        userDP.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        userDP.setName("userDP"); // NOI18N

        personalDP.setText(resourceMap.getString("personalDP.text")); // NOI18N
        personalDP.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        personalDP.setName("personalDP"); // NOI18N

        changeDpButton.setIcon(resourceMap.getIcon("changeDpButton.icon")); // NOI18N
        changeDpButton.setText(resourceMap.getString("text")); // NOI18N
        changeDpButton.setBorderPainted(false);
        changeDpButton.setName(""); // NOI18N
        changeDpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                changeDpButtonMousePressed(evt);
            }
        });

        colorChooserButton.setIcon(resourceMap.getIcon("colorChooserButton.icon")); // NOI18N
        colorChooserButton.setBorderPainted(false);
        colorChooserButton.setName("colorChooserButton"); // NOI18N
        colorChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorChooserButtonActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(resourceMap.getColor("jSeparator1.background")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N

        recButton.setIcon(resourceMap.getIcon("recButton.icon")); // NOI18N
        recButton.setText(resourceMap.getString("recButton.text")); // NOI18N
        recButton.setToolTipText(resourceMap.getString("recButton.toolTipText")); // NOI18N
        recButton.setName("recButton"); // NOI18N
        recButton.setPreferredSize(new java.awt.Dimension(10, 10));
        recButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                recButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                recButtonMouseReleased(evt);
            }
        });

        secureButton.setIcon(resourceMap.getIcon("secureButton.icon")); // NOI18N
        secureButton.setToolTipText(resourceMap.getString("secureButton.toolTipText")); // NOI18N
        secureButton.setName("secureButton"); // NOI18N
        secureButton.setPreferredSize(new java.awt.Dimension(10, 10));
        secureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secureButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(userDP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(changeDpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(colorChooserButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(recButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(secureButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(whispButtonSend)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, personalDP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                .add(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(16, 16, 16)
                        .add(userDP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(33, 33, 33)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 35, Short.MAX_VALUE)
                        .add(personalDP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(changeDpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(7, 7, 7)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(colorChooserButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(recButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(secureButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(whispButtonSend, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * envoi le texte au serveur
     * @param evt
     */
    private void whispButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_whispButtonSendActionPerformed
        if (!whispMessageField.getText().equals("")) {
            cv.sendInt(ChatConstants.PRIVATE_CONV);
            cv.sendInt(textColor);
            cv.sendText(destUsername);
            cv.sendText(whispMessageField.getText());
            whispMessageField.setText("");
            if (firstMessage) {
                sendDpToOther();
                firstMessage = false;
            }

        }
}//GEN-LAST:event_whispButtonSendActionPerformed

    private void whispMessageFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_whispMessageFieldKeyTyped
        if (evt.getKeyChar() == 10) {
            whispMessageField.setText(whispMessageField.getText().replace("\n", ""));
            whispButtonSendActionPerformed(null);
        } else if (whispMessageField.getText().length() > ChatConstants.FIELD_TEXT_SIZE) {
            whispMessageField.setText(whispMessageField.getText().substring(0, ChatConstants.FIELD_TEXT_SIZE));
        }
    }//GEN-LAST:event_whispMessageFieldKeyTyped

    private void changeDpButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeDpButtonMousePressed
        Point loc;
        changeDpPopup.setInvoker(changeDpButton);
        loc = evt.getPoint();
        changeDpPopup.show(changeDpButton, loc.x, loc.y);
    }//GEN-LAST:event_changeDpButtonMousePressed

    private void changeDpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDpMenuItemActionPerformed
        BufferedImage mImage = null;
        String source = filechoose();
        File inputFile = new File(source);
        try {
            mImage = ImageIO.read(inputFile);
        } catch (IOException ex) {
            System.out.println("Error while reading image");
        }
        ImageIcon icon = null;
        ImageIcon iconDisplayPic;
        try {
            icon = new ImageIcon(mImage);
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(100, 130, java.awt.Image.SCALE_SMOOTH);
            iconDisplayPic = new ImageIcon(newimg);
            personalDP.setIcon(iconDisplayPic);
            cv.setDisplayPic(iconDisplayPic);
            sendDpToOther();
        //cv.updateDpForAll();
        } catch (NullPointerException e) {
            ErrorLogger.getLogger().logError("", e);
        }
}//GEN-LAST:event_changeDpMenuItemActionPerformed

    private void colorChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorChooserButtonActionPerformed
        Color returnColor = JColorChooser.showDialog(null, java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Choose_the_color"), ChatConstants.noir);
        try {
            if (!returnColor.equals(null) && !returnColor.equals(Color.WHITE)) {
                this.textColor = returnColor.getRGB();
            }
        } catch (NullPointerException e) {
        }
}//GEN-LAST:event_colorChooserButtonActionPerformed

    private void recButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recButtonMousePressed
        rec = new Record();
        rec.captureAudio();
    }//GEN-LAST:event_recButtonMousePressed

    private void recButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recButtonMouseReleased
        rec.stopRecord();
        audioFile = rec.getRecord();
        byte[] b = audioFile.toByteArray();
        cv.sendAudioClip(b, destUsername);
        writeTextOnWhispField("***Sound clip sent***", false, true, 12, ChatConstants.bleu);
    }//GEN-LAST:event_recButtonMouseReleased

    private void secureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secureButtonActionPerformed
        secureView = new SecureMessageView(destUsername);
        secureView.initializeEncryption();
    }//GEN-LAST:event_secureButtonActionPerformed

    private String filechoose() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".jpeg") || f.isDirectory();
            }

            public String getDescription() {
                return "Image files";
            }
        });
        int r = chooser.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            String name1 = chooser.getSelectedFile().getAbsolutePath();
            return name1;
        }
        return "";
    }

    public void ChangeLanguage() {
        whispButtonSend.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Send"));
        changeDpMenuItem.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("Change_Display_Pic"));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changeDpButton;
    private javax.swing.JMenuItem changeDpMenuItem;
    private javax.swing.JPopupMenu changeDpPopup;
    private javax.swing.JButton colorChooserButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel personalDP;
    private javax.swing.JButton recButton;
    private javax.swing.JButton secureButton;
    private javax.swing.JLabel userDP;
    private javax.swing.JButton whispButtonSend;
    private javax.swing.JTextPane whispField;
    private javax.swing.JTextArea whispMessageField;
    // End of variables declaration//GEN-END:variables
}

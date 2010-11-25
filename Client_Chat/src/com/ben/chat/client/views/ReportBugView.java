/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ReportBugView.java
 *
 * Created on Oct 9, 2009, 2:24:02 PM
 */

package com.ben.chat.client.views;

import com.ben.chat.client.constant.ChatConstants;
import javax.swing.JOptionPane;

/**
 *
 * @author bendaccache
 */
public class ReportBugView extends javax.swing.JDialog {
    private ClientChatView cv;

    /** Creates new form ReportBugView */
    public ReportBugView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.cv = ClientChatView.getInstance();
        initLanguage();
        setLocationRelativeTo(parent);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        reportBugLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        typeCombo = new javax.swing.JComboBox();
        reportLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        reportTextField = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        progressLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        setUndecorated(true);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getResourceMap(ReportBugView.class);
        reportBugLabel.setFont(resourceMap.getFont("reportBugLabel.font")); // NOI18N
        reportBugLabel.setText(resourceMap.getString("reportBugLabel.text")); // NOI18N
        reportBugLabel.setName("reportBugLabel"); // NOI18N

        typeLabel.setText(resourceMap.getString("typeLabel.text")); // NOI18N
        typeLabel.setName("typeLabel"); // NOI18N

        typeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bug", "Error", "Advice", "Rating" }));
        typeCombo.setName("typeCombo"); // NOI18N

        reportLabel.setText(resourceMap.getString("reportLabel.text")); // NOI18N
        reportLabel.setName("reportLabel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        reportTextField.setColumns(20);
        reportTextField.setRows(5);
        reportTextField.setName("reportTextField"); // NOI18N
        jScrollPane1.setViewportView(reportTextField);

        sendButton.setText(resourceMap.getString("sendButton.text")); // NOI18N
        sendButton.setName("sendButton"); // NOI18N
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        progressLabel.setText(resourceMap.getString("progressLabel.text")); // NOI18N
        progressLabel.setName("progressLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(145, 145, 145)
                            .addComponent(reportBugLabel))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(typeLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(reportLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reportBugLabel)
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(reportLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendButton)
                    .addComponent(cancelButton)
                    .addComponent(progressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        cv.sendInt(ChatConstants.REPORT);
        cv.sendText(typeCombo.getSelectedItem().toString());
        cv.sendText(reportTextField.getText());
        dispose();
        JOptionPane.showMessageDialog(null, "Thank you");
    }//GEN-LAST:event_sendButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JLabel reportBugLabel;
    private javax.swing.JLabel reportLabel;
    private javax.swing.JTextArea reportTextField;
    private javax.swing.JButton sendButton;
    private javax.swing.JComboBox typeCombo;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables

    private void initLanguage() {
        cancelButton.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("cancel"));
        reportBugLabel.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("report"));
        reportLabel.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("report2"));
        sendButton.setText(java.util.ResourceBundle.getBundle(cv.getLanguage()).getString("send"));





    }

}

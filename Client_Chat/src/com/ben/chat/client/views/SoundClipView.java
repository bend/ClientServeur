/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SoundClipView.java
 *
 * Created on Nov 4, 2009, 12:37:03 PM
 */
package com.ben.chat.client.views;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.recordsound.Play;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;

/**
 *
 * @author bendaccache
 */
public class SoundClipView extends javax.swing.JDialog {

    private byte[] soundClip;
    private Play play;

    /** Creates new form SoundClipView */
    public SoundClipView(java.awt.Frame parent, boolean modal, String nickname, byte[] soundClip) {
        super(parent, modal);
        initComponents();
        this.soundClip = soundClip;
        this.setTitle("Sound file from " + nickname);
        this.setVisible(true);
        this.setLocationRelativeTo(ClientChatApp.getApplication().getMainFrame());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playButton = new javax.swing.JButton();
        discardButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setResizable(false);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getResourceMap(SoundClipView.class);
        playButton.setIcon(resourceMap.getIcon("playButton.icon")); // NOI18N
        playButton.setText(resourceMap.getString("playButton.text")); // NOI18N
        playButton.setName("playButton"); // NOI18N
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        discardButton.setText(resourceMap.getString("discardButton.text")); // NOI18N
        discardButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        discardButton.setName("discardButton"); // NOI18N
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });

        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        stopButton.setIcon(resourceMap.getIcon("stopButton.icon")); // NOI18N
        stopButton.setText(resourceMap.getString("stopButton.text")); // NOI18N
        stopButton.setName("stopButton"); // NOI18N
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(discardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(discardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed
        dispose();
}//GEN-LAST:event_discardButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        play = new Play(soundClip);
        play.playAudio();
}//GEN-LAST:event_playButtonActionPerformed

    private AudioFormat getFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);
    }

    private String filechoose() {
        JFileChooser chooser = new JFileChooser("~");
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".wav") || f.isDirectory();
            }

            public String getDescription() {
                return "wav file";
            }
        });

        int r = chooser.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            String name1 = chooser.getSelectedFile().getAbsolutePath();
            return name1;
        }
        return "";
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        FileOutputStream fout = null;
        try {
            String filename = filechoose();
            if (filename.equals("")) {
                return;
            }
            if (!filename.endsWith(".wav")) {
                int index = filename.lastIndexOf('.');
                String extension = filename.substring(index);
                filename.replaceFirst(extension, ".wav");
            }
            File f = new File(filename);
            ByteArrayInputStream CapturedByteArray = new ByteArrayInputStream(soundClip);
            AudioInputStream capturedAudioStream = new AudioInputStream(CapturedByteArray, getFormat(), soundClip.length);
            AudioSystem.write(capturedAudioStream, AudioFileFormat.Type.WAVE, f);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("", ex);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("", ex);
            }
        }
}//GEN-LAST:event_saveButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        play.setStop(true);
}//GEN-LAST:event_stopButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton discardButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}

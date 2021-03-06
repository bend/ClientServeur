/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * YourShareView.java
 *
 * Created on Oct 18, 2009, 1:59:42 AM
 */

package com.ben.chat.client.views;

import com.ben.chat.client.constant.ChatConstants;

/**
 *
 * @author bendaccache
 */
public class YourShareView extends javax.swing.JDialog {
    private boolean canceled = false;
    private boolean isDisposed = false;
    private ClientChatView cv;
    private String pearNickname;

    /** Creates new form YourShareView */
    public YourShareView(java.awt.Frame parent, boolean modal,String pearNickname) {
        super(parent, modal);
        initComponents();
        buttonGroup1.add(grayBitRadioButton);
        buttonGroup1.add(fullBitRadioButton);
        timeLabel.setText(intervalSlider.getValue()+" ms");
        sliderStatusLabel.setText(slider.getValue() + "%");
        this.cv = ClientChatView.getInstance();
        this.pearNickname = pearNickname;

    }

    public boolean getComboControl() {
        return screenControlBox.isSelected();
    }

    public long getTimeInterval() {
        long time = intervalSlider.getValue();
        return time;
    }

    public void setControl(boolean b) {
        screenControlBox.setSelected(b);
        screenControlBox.setEnabled(b);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        grayBitRadioButton = new javax.swing.JRadioButton();
        fullBitRadioButton = new javax.swing.JRadioButton();
        slider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        sliderStatusLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        intervalSlider = new javax.swing.JSlider();
        timeLabel = new javax.swing.JLabel();
        screenControlBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.ben.chat.client.views.ClientChatApp.class).getContext().getResourceMap(YourShareView.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        grayBitRadioButton.setText(resourceMap.getString("grayBitRadioButton.text")); // NOI18N
        grayBitRadioButton.setName("grayBitRadioButton"); // NOI18N

        fullBitRadioButton.setSelected(true);
        fullBitRadioButton.setText(resourceMap.getString("fullBitRadioButton.text")); // NOI18N
        fullBitRadioButton.setName("fullBitRadioButton"); // NOI18N

        slider.setMinimum(10);
        slider.setName("slider"); // NOI18N
        slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderStateChanged(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        sliderStatusLabel.setText(resourceMap.getString("sliderStatusLabel.text")); // NOI18N
        sliderStatusLabel.setName("sliderStatusLabel"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        intervalSlider.setMaximum(5000);
        intervalSlider.setMinimum(100);
        intervalSlider.setValue(500);
        intervalSlider.setName("intervalSlider"); // NOI18N
        intervalSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                intervalSliderStateChanged(evt);
            }
        });

        timeLabel.setText(resourceMap.getString("timeLabel.text")); // NOI18N
        timeLabel.setName("timeLabel"); // NOI18N

        screenControlBox.setText(resourceMap.getString("screenControlBox.text")); // NOI18N
        screenControlBox.setEnabled(false);
        screenControlBox.setName("screenControlBox"); // NOI18N
        screenControlBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                screenControlBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(100, 100, 100)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(screenControlBox)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(grayBitRadioButton)
                                                .addGap(18, 18, 18)
                                                .addComponent(fullBitRadioButton)))))
                                .addGap(12, 12, 12)
                                .addComponent(sliderStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intervalSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))))
                    .addComponent(jButton1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(sliderStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(intervalSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(grayBitRadioButton)
                    .addComponent(fullBitRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(screenControlBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public  boolean isCanceled(){
        return canceled;
    }

    public boolean isDisposed(){
        return isDisposed;
    }

    @Override
    public void dispose(){
        isDisposed = true;
        super.dispose();
    }

    public int getSliderValue(){
        return slider.getValue();
    }

    public int getImageDepth(){
        if(grayBitRadioButton.isSelected())
            return 8;
        else return 16;

    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        canceled = true;
    }//GEN-LAST:event_jButton1ActionPerformed

    private void sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderStateChanged
        sliderStatusLabel.setText(Integer.toString(slider.getValue())+"%");
}//GEN-LAST:event_sliderStateChanged

    private void intervalSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_intervalSliderStateChanged
        timeLabel.setText(intervalSlider.getValue()+ " ms");
    }//GEN-LAST:event_intervalSliderStateChanged

    private void screenControlBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_screenControlBoxActionPerformed
        if(screenControlBox.isSelected()){
            cv.sendInt(ChatConstants.SCREEN_CONTROL_REQUEST);
            cv.sendText(pearNickname);
        }else{
            cv.sendInt(ChatConstants.SCREEN_CONTROL_END);
            cv.sendText(pearNickname);
        }
    }//GEN-LAST:event_screenControlBoxActionPerformed

    /**
    * @param args the command line arguments
    */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton fullBitRadioButton;
    private javax.swing.JRadioButton grayBitRadioButton;
    private javax.swing.JSlider intervalSlider;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox screenControlBox;
    private javax.swing.JSlider slider;
    private javax.swing.JLabel sliderStatusLabel;
    private javax.swing.JLabel timeLabel;
    // End of variables declaration//GEN-END:variables

}

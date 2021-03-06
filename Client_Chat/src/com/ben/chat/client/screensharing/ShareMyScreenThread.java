/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.client.screensharing;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.YourShareView;
import java.awt.Image;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author bendaccache
 */
public class ShareMyScreenThread extends Thread implements Runnable {
    private ShareScreenConnection ssc;
    private int id;
    private String destusername;
    private String nickname;
    private YourShareView ysv;
    private boolean connected = true;
    private ScreenCapture s;

    public ShareMyScreenThread(ShareScreenConnection ssc, int id, String destusername, String nickname) {
        super("share my screen thread");
        this.ssc = ssc;
        this.id=id;
        this.destusername= destusername;
        this.nickname = nickname;
        ysv = new YourShareView(null, false,destusername);
        ysv.setVisible(true);
    }

    public boolean isDisposed(){
        return ysv.isDisposed();
    }

    public void setScreenControl(boolean b) {
        ysv.setControl(true);
    }

    private Image getImage(){
        s.captureScreen();
        s.resizeImage(getScale());
        int depth = ysv.getImageDepth();
        if(depth==8){
            return s.toGrayScale();
        }
        return s.getImage();
    }

    private float getScale(){
        float si = ysv.getSliderValue();
        float size = si/100;
        return size;
    }


    public boolean isComboControlSelected(){
        return ysv.getComboControl();
    }

    @Override
    public void run(){
        try {
            ssc.sendInt(ChatConstants.RECEIVE_SCREEN_SHARING);
            ssc.sendInt(id);
            ssc.sendText(destusername);
            ssc.sendText(nickname);
            s = new ScreenCapture();
            while (!ysv.isCanceled() && connected) {
                ImageIcon icon = new ImageIcon(getImage());
                try{
                    ssc.sendObject(icon);
                }catch(SocketException e){
                    connected=false;
                    ssc.closeConnection();
                    ysv.dispose();
                }
                try {
                    sleep(ysv.getTimeInterval());
                } catch (InterruptedException ex) {
                    ErrorLogger.getLogger().logError("", ex);
                }
            }
            connected=false;
            ysv.dispose();
            ssc.closeConnection();
        } catch (IOException ex) {
            connected = false;
            ssc.closeConnection();
        }
    }
}

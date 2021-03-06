/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.client.screensharing;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author bendaccache
 */
public class ControlPearScreenThread extends Thread implements Runnable {
    private ScreenControlConnection ssc;
    private ShareScreenReceiveStream shareScreenReceive;
    private String destNickname;

    public ControlPearScreenThread(ScreenControlConnection ssc, ShareScreenReceiveStream shareScreenReceive, String destNickname) {
        super("control pear screen");
        this.ssc = ssc;
        this.shareScreenReceive = shareScreenReceive;
        this.destNickname = destNickname;
        ssc.sendText(destNickname);
    }


    @Override
    public void run(){
        while(!shareScreenReceive.isDisposed() && shareScreenReceive.getPrefPanel().isControl()){
            try {
                try{
                    if(shareScreenReceive.isMouseClick()){
                        MouseEvent evt = shareScreenReceive.getMouseClick();
                        JLabel label  = (JLabel) evt.getSource();
                        ssc.sendInt(ChatConstants.MouseClick);
                        ssc.sendInt(evt.getButton());
                        ssc.sendDouble(evt.getX()/label.getWidth());
                        ssc.sendDouble(evt.getY()/label.getHeight());
                        System.out.println("mouse click");
                    }else if(shareScreenReceive.isMousePressed()){
                        MouseEvent evt = shareScreenReceive.getMousePressed();
                        JLabel label  = (JLabel) evt.getSource();
                        ssc.sendInt(ChatConstants.MousePress);
                        ssc.sendInt(evt.getButton());
                        ssc.sendDouble(evt.getX()/label.getWidth());
                        ssc.sendDouble(evt.getY()/label.getHeight());
                    }else if(shareScreenReceive.isMouseReleased()){
                        MouseEvent evt = shareScreenReceive.getMouseReleased();
                        JLabel label  = (JLabel) evt.getSource();
                        ssc.sendInt(ChatConstants.MouseRelease);
                        ssc.sendInt(evt.getButton());
                        ssc.sendDouble(evt.getX()/label.getWidth());
                        ssc.sendDouble(evt.getY()/label.getHeight());
                    }else{//mouse move
                        double x = shareScreenReceive.getRelativePositionX();
                        double y = shareScreenReceive.getRelativePositionY();
                        ssc.sendInt(ChatConstants.Screen_MOUSE_MOVE);
                        ssc.sendDouble(x);
                        ssc.sendDouble(y);
                        System.out.println("mouse move");

                        Thread.sleep(200);
                    }
                } catch (InterruptedException ex) {
                    ErrorLogger.getLogger().logError("Interrupted", ex);
                }
            }catch (IOException ex) {
                ErrorLogger.getLogger().logError("Io in control pear thread", ex);
            }
        }
    }

}

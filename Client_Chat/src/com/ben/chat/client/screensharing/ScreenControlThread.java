/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.screensharing;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.SocketException;

/**
 *
 * @author bendaccache
 */
public class ScreenControlThread extends Thread implements Runnable {

    private ScreenControlConnection ssc;
    private ShareMyScreenThread sharemyscreen;

    public ScreenControlThread(ScreenControlConnection ssc, ShareMyScreenThread sharemyscreen) {
        super("screen control");
        this.ssc = ssc;
        this.sharemyscreen = sharemyscreen;
        ssc.sendText("null");
    }

    @Override
    public void run() {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException ex) {
            ErrorLogger.getLogger().logError("Robot Error in Screen control thread", ex);
        }
        while (!sharemyscreen.isDisposed() && sharemyscreen.isComboControlSelected()) {
            try {
                int action = ssc.receiveInt();
                if(action==ChatConstants.Screen_MOUSE_MOVE){
                    double x = ssc.receiveDouble();
                    double y = ssc.receiveDouble();
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    double height = toolkit.getScreenSize().getHeight();
                    double width = toolkit.getScreenSize().getWidth();
                    r.mouseMove((int)(x*width),(int)(y*height));
                    System.out.println("action" +action);
                }else if(action == ChatConstants.MouseClick){
                    System.out.println("action" +action);
                    int button = ssc.receiveInt();
                    double x = ssc.receiveDouble();
                    double y = ssc.receiveDouble();
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    double height = toolkit.getScreenSize().getHeight();
                    double width = toolkit.getScreenSize().getWidth();
                    if(button == 1){
                        r.mouseMove((int)(x*width),(int)(y*height));
                        r.mousePress(InputEvent.BUTTON1_MASK);
                        r.mouseRelease(InputEvent.BUTTON1_MASK);
                    }else if(button == 2){
                        r.mouseMove((int)(x*width),(int)(y*height));
                        r.mousePress(InputEvent.BUTTON2_MASK);
                        r.mouseRelease(InputEvent.BUTTON2_MASK);
                    }
                 }else if(action == ChatConstants.MousePress){
                    int button = ssc.receiveInt();
                    double x = ssc.receiveDouble();
                    double y = ssc.receiveDouble();
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    double height = toolkit.getScreenSize().getHeight();
                    double width = toolkit.getScreenSize().getWidth();
                    if(button == 1){
                        r.mouseMove((int)(x*width),(int)(y*height));
                        r.mousePress(InputEvent.BUTTON1_MASK);
                    }else if(button == 2){
                        r.mouseMove((int)(x*width),(int)(y*height));
                        r.mousePress(InputEvent.BUTTON2_MASK);
                    }
                  }else if(action == ChatConstants.MouseRelease){
                      int button = ssc.receiveInt();
                    double x = ssc.receiveDouble();
                    double y = ssc.receiveDouble();
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    double height = toolkit.getScreenSize().getHeight();
                    double width = toolkit.getScreenSize().getWidth();
                    if(button == 1){
                        r.mouseMove((int)(x*width),(int)(y*height));
                    }else if(button == 2){
                        r.mouseMove((int)(x*width),(int)(y*height));
                        r.mouseRelease(InputEvent.BUTTON2_MASK);
                    }
                }
            } catch (SocketException ex) {
                ErrorLogger.getLogger().logError("Socket Ex in ScreenControlThread", ex);
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO in ScreenControlThread", ex);
            }
        }

    }
}

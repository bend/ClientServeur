/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.screensharing;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.*;
import java.awt.event.MouseEvent;
import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author bendaccache
 */
public class ShareScreenReceiveStream extends Thread implements Runnable {

    private ShareScreenConnection ssc;
    private ClientChatView cv;
    private boolean connected = true;
    private ScreenSharingView t;
    private ScreenSharingPrefView prefs;
    private boolean isDisposed;

    public ShareScreenReceiveStream(ShareScreenConnection ssc) {
        super("share screen receive stream");
        this.ssc = ssc;
        this.cv = ClientChatView.getInstance();
        t = new ScreenSharingView(null, false);
        t.setVisible(true);
        prefs = new ScreenSharingPrefView(null, false);
        prefs.setVisible(true);
    }

    public ScreenSharingPrefView getPrefPanel() {
        return prefs;
    }

    public double getRelativePositionX() {
        return t.getRelativePositionX();
    }

    public double getRelativePositionY() {
        return t.getRelativePositionY();
    }

    public boolean isDisposed() {
        return t.isDisposed();
    }

    @Override
    public void run() {
        while (connected) {
            forwardAction();
        }
    }

    public void setScreenControl(boolean b) {
        prefs.setScreenControl(b);
    }

    public MouseEvent getMouseClick() {
        return t.getMouseClick();
    }

    public MouseEvent getMousePressed() {
        return t.getMousePressed();
    }

    public MouseEvent getMouseReleased() {
        return t.getMouseReleased();
    }

    public boolean isMouseClick() {
        return t.isMouseCLick();
    }

    public boolean isMousePressed() {
        return t.isMousePressed();
    }

    public boolean isMouseReleased() {
        return t.isMouseReleased();
    }

    private void forwardAction() {
        try {
            try {
                int action = ssc.receiveInt();
                switch (action) {
                    case ChatConstants.RECEIVE_SCREEN_SHARING:
                        receiveScreenSharing();
                        break;
                }
            } catch (java.net.SocketException e) {
            }
        } catch (EOFException e) {
            connected = false;
        } catch (IOException ex) {
            ssc.closeConnection();
            connected = false;
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    private void receiveScreenSharing() {
        try {
            String fromNickname = ssc.receiveText();
            t.setTitle(fromNickname);
            prefs.setNickname(fromNickname);
            ImageIcon icon;
            while (!t.isDisposed() && !prefs.isCancelled()) {
                icon = (ImageIcon) ssc.receiveObject();
                t.setSize(icon.getIconWidth(), icon.getIconHeight() + 30);
                t.setLabel(icon);
            }
            ssc.closeConnection();
            connected = false;
            if (!t.isDisposed()) {
                t.dispose();
            }
            prefs.dispose();
        } catch (ClassNotFoundException ex) {
            ErrorLogger.getLogger().logError("", ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Connection problem");
            t.dispose();
            ssc.closeConnection();
            connected = false;
            prefs.dispose();

        }
    }
}

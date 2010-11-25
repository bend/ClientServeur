/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.constant.preferences;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author bendaccache
 */
public class Preferences implements Serializable {

    private boolean sound = true;
    private int RGB = Color.black.getRGB();

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean b) {
        this.sound = b;
        savePrefs();
    }

    public void setTextColorPref(int RGB) {
        this.RGB = RGB;
        savePrefs();
    }

    public int getColorPref() {
        return RGB;
    }

    private void savePrefs() {
        ObjectOutputStream oos = null;
        try {
            File f = new File("Conf/Prefs.dat");
            FileOutputStream fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO", ex);
            }
        }
    }
}

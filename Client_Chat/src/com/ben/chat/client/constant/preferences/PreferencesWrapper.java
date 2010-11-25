/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.constant.preferences;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author benoitdaccache
 */
public class PreferencesWrapper {

    private Object oos;
    public static Preferences pref;

    public PreferencesWrapper() {
        pref = loadPrefs();
    }

    public static Preferences getPreferences() {
        return pref;
    }

    private Preferences loadPrefs() {
        FileInputStream fisdp;
        ObjectInputStream oisdp;
        try {
            fisdp = new FileInputStream("Conf/Prefs.dat");
            oisdp = new ObjectInputStream(fisdp);
            return (Preferences) oisdp.readObject();
        } catch (FileNotFoundException e) {
        } catch (ClassNotFoundException ex) {
            ErrorLogger.getLogger().logError("Class not found", ex);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
        return new Preferences();

    }
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.server.traitement;

import com.ben.chat.server.constants.ChatConstants;
import com.ben.chat.server.report.ErrorLogger;
import com.ben.chat.server.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author bendaccache
 */
public class TraitementScreenControl extends Thread implements Runnable {

    private Socket socket;
    private DataOutputStream sortie;
    private DataInputStream entree;
    private boolean connected = true;
    private Serveur serveur;
    private String destNickname;
    private TraitementScreenControl tsc;

    public TraitementScreenControl(Socket socket, Serveur serveur) {
        super();
        this.socket = socket;
        this.serveur = serveur;
    }

    @Override
    public void run() {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();//on cree le flux d'entree et sortie
            sortie = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            entree = new DataInputStream(in);
            destNickname = receiveText();
            while (connected) {//on lit les actions
                int action = receiveInt();
                ForwardAction(action);
            }
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    private void ForwardAction(int action) {
        try {
            switch (action) {
                case ChatConstants.Screen_MOUSE_MOVE:
                    tsc = (TraitementScreenControl) serveur.getHashtableOfScreenControl().get(destNickname);
                    double x = receiveDouble();
                    double y = receiveDouble();
                    tsc.sendInt(action);
                    tsc.sendDouble(x);
                    tsc.sendDouble(y);
                    break;
                default:
                    tsc = (TraitementScreenControl) serveur.getHashtableOfScreenControl().get(destNickname);
                    int button = receiveInt();
                    x = receiveDouble();
                    y = receiveDouble();
                    tsc.sendInt(action);
                    tsc.sendInt(button);
                    tsc.sendDouble(x);
                    tsc.sendDouble(y);
                    break;
            }
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }

    }

    private double receiveDouble() throws IOException {
        return entree.readDouble();
    }

    private int receiveInt() throws IOException {
        int action = entree.readInt();
        return action;

    }

    public String receiveText() {
        String text = null;
        try {
            text = entree.readUTF();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while receiving UTF on stream", ex);
        }
        return text;
    }

    public void sendText(String txt) {
        try {
            sortie.writeUTF(txt);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while writing UTF on stream", ex);
        }
    }

    public void sendInt(int i) {
        try {
            sortie.writeInt(i);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Error while writing integer on stream", ex);
        }
    }

    private void sendDouble(double x) throws IOException {
        sortie.writeDouble(x);
    }
}

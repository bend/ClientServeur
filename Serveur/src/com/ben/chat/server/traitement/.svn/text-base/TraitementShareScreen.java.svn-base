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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bendaccache
 */
public class TraitementShareScreen extends Thread implements Runnable {

    private Socket socket;
    private Serveur serveur;
    private int connectionId;
    private DataOutputStream sortie;
    private boolean connected = true;
    private DataInputStream entree;

    public TraitementShareScreen(Socket socket, Serveur serveur, int shareConnectionid) {
        super("Traitement Sharing");
        this.socket = socket;
        this.serveur = serveur;
        this.connectionId = shareConnectionid;
    }

    @Override
    public void run() {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();//on cree le flux d'entree et sortie
            sortie = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            entree = new DataInputStream(in);
            sortie.writeInt(connectionId);
            while (connected) {//on lit les actions
                int action = receiveInt();
                ForwardAction(action);
            }
        } catch (IOException ex) {//fermeture connexion
            ErrorLogger.getLogger().logError("IO", ex);
        }

    }

    private void ForwardAction(int action) {
        switch (action) {
            case ChatConstants.RECEIVE_SCREEN_SHARING:
                transfertScreenSharing();
                break;
        }
    }

    public void closeConnection() {
        try {
            connected = false;
            serveur.removeShareConnectionFromHashtable(connectionId);
            this.sortie.close();
            this.entree.close();
            this.socket.close();
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("Connection closed", ex);
        }

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

    private void sendObject(Object obj) throws IOException {
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(sortie);
        oos.writeObject(obj);
    }

    private Object receiveObject() throws IOException, ClassNotFoundException {
        Object obj = null;
        ObjectInputStream ois = new ObjectInputStream(entree);
        obj = ois.readObject();
        return obj;
    }

    private void transfertScreenSharing() {
        int id = 0;
        try {
            id = receiveInt();
            String destusername = receiveText();
            String nickname = receiveText();
            TraitementShareScreen tsc = (TraitementShareScreen) serveur.getHashtableOfScreenSharing().get(id);
            tsc.sendInt(ChatConstants.RECEIVE_SCREEN_SHARING);
            tsc.sendText(nickname);
            while (connected) {
                Object o = receiveObject();
                tsc.sendObject(o);
            }
        } catch (ClassNotFoundException ex) {
            ErrorLogger.getLogger().logError("Class not found", ex);
        } catch (IOException ex) {
            closeConnection();
            ((TraitementShareScreen) serveur.getHashtableOfScreenSharing().get(id)).closeConnection();
        }

    }
}

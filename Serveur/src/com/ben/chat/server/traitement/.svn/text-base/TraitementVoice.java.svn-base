/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.server.traitement;

import com.ben.chat.server.constants.ChatConstants;
import com.ben.chat.server.*;
import com.ben.chat.server.report.ErrorLogger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author bendaccache
 */
public class TraitementVoice extends Thread implements Runnable {

    private Serveur serveur;
    private Socket socket;
    private DataOutputStream sortie;
    private int connectionId;
    private DataInputStream entree;
    private boolean connected = true;
    private String username;
    private int id;
    private int destId;

    public TraitementVoice(Socket socket, Serveur serveur, int id) {
        super("voice thread");
        this.socket = socket;
        this.serveur = serveur;
        OutputStream out = null;
        this.id = id;
        try {
            out = socket.getOutputStream();//on cree le flux d'entree et sortie
            sortie = new DataOutputStream(out);
            InputStream in = socket.getInputStream();
            entree = new DataInputStream(in);
            sortie.writeInt(id);
        } catch (IOException ex) {//fernetureconnection
            closeConnection();
            ErrorLogger.getLogger().logError("IO", ex);
        }
    }

    @Override
    public void run() {
        TraitementVoice tv = null;
        try {
            this.destId = entree.readInt();
            tv = (TraitementVoice) serveur.getHashtableOfVoice().get(destId);
            while (connected) {
                int size = entree.readInt();
                byte[] b = new byte[size];
                entree.readFully(b);
                tv.sendInt(b.length);
                tv.sendTabByte(b);
            }
        } catch (EOFException e) {
            tv.closeConnection();
            ErrorLogger.getLogger().logError("EOf", e);
        } catch (SocketException e) {
            ErrorLogger.getLogger().logError("Socket", e);
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
            closeConnection();
            tv.closeConnection();
        }
    }

    public void giveAnswer(boolean b) {
        if (b) {
            try {
                sortie.write(ChatConstants.CALL_ACCEPTANCE);
            } catch (IOException ex) {
                ErrorLogger.getLogger().logError("IO", ex);
                closeConnection();
            }
        }
    }

    public void sendTabByte(byte[] b) throws IOException {
        sortie.write(b);
    }

    public void closeConnection() {
        try {
            entree.close();
            sortie.close();
            connected = false;
        } catch (IOException ex) {
            ErrorLogger.getLogger().logError("IO", ex);
        }

    }

    private void sendInt(int length) throws IOException {
        sortie.writeInt(length);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.client.filetransfert;

import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.*;
import com.ben.chat.client.views.ClientChatView;
import com.ben.chat.client.views.TranfertProgressView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author bendaccache
 */
/**
 * cree un thread de transfert de fichier(envoi) entre 2 clients
 * @author bendaccache
 */
public class TransfertThread extends Thread implements Runnable {
   
    private File f;
    private FileConnection fc;
    private ClientChatView cv;
    private int connectionId;
    private String destUsername;
    private String username;
    private TranfertProgressView winPro;

    public TransfertThread(File f, FileConnection fc,int connectionId,String destUsername, String username) {
        super("Transfert thread");
        this.f = f;
        this.fc = fc;
        this.cv = ClientChatView.getInstance();
        this.connectionId = connectionId;
        this.destUsername = destUsername;
        this.username= username;
    }

    @Override
    public void run(){
        try {
            cv.prepareTextOnSalonField("***Transfert of the file " + f.getName() + " started***");
            fc.sendInt(ChatConstants.FILE_TRANSFERT);
            fc.sendInt(connectionId);
            fc.sendText(destUsername);
            fc.sendText(username);
            fc.sendText(f.getName());
            fc.sendLong(f.length());
            FileInputStream inF = new FileInputStream(f);
            float nbMorceaux = f.length() / ChatConstants.PARSE_SIZE;
            int nbPassage = (int) nbMorceaux;//nombre de morceaux de fichier
            if (f.length() % ChatConstants.PARSE_SIZE > 0) {
                nbPassage++;
            }
            int nbPasse = 0;
            byte[] tabFile;
            long tailleRestante = f.length();
            fc.sendInt(nbPassage);//on envoi le nombre de morceau de fichier a recevoir
            winPro = new TranfertProgressView(null,false,"Transfert of "+f.getName());
            winPro.setVisible(true);
            int nb = 0;
            long time = System.currentTimeMillis();
            while (nbPasse < nbPassage && winPro.notCancelled()) {
                    fc.sendInt(ChatConstants.CONTINUE);
                    if (tailleRestante >= ChatConstants.PARSE_SIZE) {
                        tabFile = new byte[ChatConstants.PARSE_SIZE];
                        inF.read(tabFile);
                        tailleRestante -= ChatConstants.PARSE_SIZE;
                        nbPasse++;
                    } else {
                        tabFile = new byte[(int) tailleRestante];
                        inF.read(tabFile);
                        nbPasse++;
                    }
                    fc.sendInt(tabFile.length);
                    fc.sendTabByte(tabFile);
                    int progress = (int) ((f.length() * 100 - tailleRestante * 100) / f.length() * 100);//on affiche le taux de progression sur la barre
                    winPro.setProgressBarValue((progress/100)+1);
                    nb++;
                    if(nb == ChatConstants.NB_FOR_TIME){
                         long timeSpent = System.currentTimeMillis()-time;
                         long timeLeft = (nbPassage - nbPasse)*timeSpent;
                         winPro.setTimeLeft(timeLeft/ChatConstants.NB_FOR_TIME);
                         winPro.setSpeed(f.length()/(timeLeft/ChatConstants.NB_FOR_TIME));//B/ms
                         time = System.currentTimeMillis();
                         nb=0;
                     }

            }
            if(!winPro.notCancelled()){
                fc.sendInt(ChatConstants.STOP);
                winPro.dispose();
                cv.prepareTextOnSalonField("***Upload of the file " + f.getName() + " cancelled***");
                fc.closeConnection();
                return ;
            }
            winPro.setProgressBarValue(100);
            winPro.setCloseActive();
            cv.prepareTextOnSalonField("***Upload of the file " + f.getName() + " ended***");
            fc.closeConnection();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error while uploading file "+f.getName());
            winPro.dispose();
        }
    }

}

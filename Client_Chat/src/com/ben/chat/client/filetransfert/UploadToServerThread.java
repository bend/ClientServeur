package com.ben.chat.client.filetransfert;

import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Upload un fichier sur le serveur
 * @author bendaccache
 */
public class UploadToServerThread extends Thread implements Runnable {
   
    private FileConnection fileCon;
    private ClientChatView cv;
    private File f;
    private TranfertProgressView winPro = null;
    public UploadToServerThread(File f,FileConnection fileCon){
        super("send Thread");
        this.fileCon=fileCon;
        this.cv=ClientChatView.getInstance();
        this.f=f;
    }

    @Override
    public void run(){
        try {
            cv.prepareTextOnSalonField("***Upload of the file " + f.getName() + " started***");
            fileCon.sendInt(ChatConstants.UPLOAD_FILE_TO_SERVER);
            fileCon.sendText(f.getName());
            FileInputStream inF = new FileInputStream(f);
            float nbMorceaux = f.length() / ChatConstants.PARSE_SIZE;
            int nbPassage = (int) nbMorceaux;//nombre de morceaux de fichier
            if (f.length() % ChatConstants.PARSE_SIZE > 0) {
                nbPassage++;
            }
            int nbPasse = 0;
            byte[] tabFile;
            long tailleRestante = f.length();
            fileCon.sendInt(nbPassage);//on envoi le nombre de morceaux
            winPro = new TranfertProgressView(null,false,"Transfert of "+f.getName());
            winPro.setVisible(true);
            int nb = 0;
            long time = System.currentTimeMillis();
            while (nbPasse < nbPassage && winPro.notCancelled()) {
                if (tailleRestante >= ChatConstants.PARSE_SIZE) {//si le nombre d'octets restant>PARSE_SIZE
                    tabFile = new byte[ChatConstants.PARSE_SIZE];//on envoi PARSE_SIZE octets
                    inF.read(tabFile);
                    tailleRestante -= ChatConstants.PARSE_SIZE;
                    nbPasse++;
                } else {
                    tabFile = new byte[(int) tailleRestante];//sinon on envoi le ce qu'il reste
                    inF.read(tabFile);
                    nbPasse++;
                }
                fileCon.sendInt(tabFile.length);
                fileCon.sendTabByte(tabFile);
                int progress = (int) ((f.length() * 100 - tailleRestante * 100) / f.length() * 100);
                winPro.setProgressBarValue(progress/100);//on affiche la progression du transfert sur la barre
                nb++;
                    if(nb == ChatConstants.NB_FOR_TIME){
                         long timeSpent = System.currentTimeMillis()-time;
                         long timeLeft = (nbPassage - nbPasse)*timeSpent;
                         winPro.setTimeLeft(timeLeft/ChatConstants.NB_FOR_TIME);
                         winPro.setSpeed(f.length()/(timeLeft/ChatConstants.NB_FOR_TIME));
                         time = System.currentTimeMillis();
                         nb=0;


                     }
            }
            if(!winPro.notCancelled()){
                winPro.dispose();
                cv.prepareTextOnSalonField("***Upload of the file " + f.getName() + " cancelled***");
                fileCon.closeConnection();
                return ;
            }
            winPro.setCloseActive();
            cv.prepareTextOnSalonField("***Upload of the file " + f.getName() + " ended***");
            fileCon.closeConnection();
            winPro.setProgressBarValue(100);
            winPro.setCancelEnabled(false);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error while uploading file "+f.getName());
            winPro.dispose();
        }
            
        }
    
}

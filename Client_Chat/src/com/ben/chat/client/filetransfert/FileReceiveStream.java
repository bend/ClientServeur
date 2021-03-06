/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.filetransfert;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * lit sur le flux d'entree pour la reception de fichier
 * @author daccachb
 */
public class FileReceiveStream extends Thread implements Runnable {

    private FileConnection fc;
    private ClientChatView cv;
    private TranfertProgressView winPro;
    private File f;
    private boolean connected = true;

    public FileReceiveStream(FileConnection fc) {
        super("Receive file stream");
        this.fc = fc;
        this.cv = ClientChatView.getInstance();
    }

    @Override
    public void run() {
        String fileName;
        int size;
        while (connected) {//boucle infini qui lit sur le flux d'entree
            try {
                int action = fc.receiveInt();//on lit le type d'action
                switch (action) {
                    case ChatConstants.FILE_DOWNLOAD://recpetion fichier direct du serveur
                        fileName = fc.receiveText();
                        long fileSize = fc.receiveLong();
                        int nbPassage = fc.receiveInt();
                        int nbPasse = 0;
                        f = new File(ChatConstants.DOWNLOAD_FOLDER_LOCATION + fileName);
                        FileOutputStream outF = new FileOutputStream(f);
                        winPro = new TranfertProgressView(null, false, "Transfert of " + f.getName());//on cree la fenetre de progression
                        winPro.setVisible(true);
                        byte[] tabFile = null;
                        long written = 0;
                        int nb = 0;
                        long time = System.currentTimeMillis();
                        while (nbPasse < nbPassage && winPro.notCancelled()) {//on recoi le fichier qui est decoupe en plusieurs morceaux
                            size = fc.receiveInt();
                            tabFile = new byte[size];
                            try {
                                tabFile = fc.receiveTabByte(size);
                                outF.write(tabFile);
                                nbPasse++;
                                written += size;
                                int progress = (int) (written * 100 / fileSize);
                                winPro.setProgressBarValue(progress);
                                nb++;
                                if (nb == ChatConstants.NB_FOR_TIME) {
                                    long timeSpent = System.currentTimeMillis() - time;
                                    long timeLeft = (nbPassage - nbPasse) * timeSpent;
                                    winPro.setTimeLeft(timeLeft / ChatConstants.NB_FOR_TIME);
                                    time = System.currentTimeMillis();
                                    nb = 0;


                                }
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "Error while downloading file from server");
                                winPro.setCloseActive();
                                f.delete();
                                return;
                            }
                        }
                        if (!winPro.notCancelled()) {
                            winPro.dispose();
                            fc.closeConnection();
                            f.delete();
                            cv.prepareTextOnSalonField("***Download of the file " + fileName + " cancelled***");
                            return;
                        }
                        outF.close();
                        winPro.setCancelEnabled(false);
                        winPro.setProgressBarValue(100);
                        winPro.setCloseActive();
                        winPro.setFile(f);
                        cv.prepareTextOnSalonField("***Download of the file " + fileName + " is complete***");
                        fc.closeConnection();
                        connected=false;
                        break;
                    case ChatConstants.FILE_TRANSFERT://transfert entre 2 clients
                        String username = fc.receiveText();
                        size = fc.receiveInt();
                        fileName = fc.receiveText();
                        fileSize = fc.receiveLong();
                        nbPassage = fc.receiveInt();
                        cv.prepareTextOnSalonField("***Receiving file from " + username + "***");
                        nbPasse = 0;
                        f = new File(ChatConstants.DOWNLOAD_FOLDER_LOCATION + fileName);
                        outF = new FileOutputStream(f);
                        winPro = new TranfertProgressView(null, false, "Transfert of " + fileName);//on cree la fenetre de progression
                        winPro.setVisible(true);
                        written = 0;
                        int cont = fc.receiveInt();
                        nb = 0;
                        time = System.currentTimeMillis();
                        while ((nbPasse < nbPassage) && cont == ChatConstants.CONTINUE && winPro.notCancelled()) {//on recoi le fichier qui est en plusieurs morceaux
                            size = fc.receiveInt();
                            tabFile = new byte[size];
                            try {
                                tabFile = fc.receiveTabByte(size);
                                outF.write(tabFile);
                                nbPasse++;
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "Error while transfering file");
                                f.delete();
                                winPro.setCloseActive();
                                break;
                            }
                            written += size;
                            int progress = (int) (written * 100 / fileSize);
                            winPro.setProgressBarValue(progress);//on affiche la progression sur la barre
                            try{
                                cont = fc.receiveInt();
                            }catch(IOException e){break;}
                            nb++;
                            if (nb == ChatConstants.NB_FOR_TIME) {
                                long timeSpent = System.currentTimeMillis() - time;
                                long timeLeft = (nbPassage - nbPasse) * timeSpent;
                                winPro.setTimeLeft(timeLeft / ChatConstants.NB_FOR_TIME);
                                time = System.currentTimeMillis();
                                nb = 0;
                            }
                        }
                        if (cont == ChatConstants.STOP || !winPro.notCancelled()) {
                            JOptionPane.showMessageDialog(winPro, "File transfert cancelled ");
                            winPro.dispose();
                            fc.closeConnection();
                            f.delete();
                            return;
                        }
                        winPro.setCloseActive();
                        outF.close();
                        winPro.setFile(f);
                        fc.closeConnection();
                        connected = false;
                        break;
                }
            } catch (IOException ex) {ErrorLogger.getLogger().logError("IO", ex);
         }
        }
    }
}



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ben.chat.client.clientconnection;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.views.*;

/**
 *
 * @author bendaccache
 */
public class UpdateContactListThread extends Thread implements Runnable {
    ClientChatView c;
    
    public UpdateContactListThread(ClientChatView c){
        super();
        this.c=c;
    }
    @Override
    public void run(){
        while(true){
            if(c.updated==true){
                c.refreshContactList();
                c.updated=false;
            }
            try {
                sleep(200);
            } catch (InterruptedException ex) {ErrorLogger.getLogger().logError("Interrrupted exception ", ex);
}

        }
    }

}

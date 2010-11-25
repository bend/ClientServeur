package com.ben.chat.client.sound;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SoundModule extends Thread{
    private String filename = "";
    private byte[] b;
    private PlaySound playSound;

    public SoundModule(String filename) {
        this.filename = filename;        
    }


    @Override
    public void run(){
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

        // initializes the playSound Object
            playSound = new PlaySound(inputStream);
        // plays the sound
        try {
            playSound.play();
        } catch (PlayWaveException e) {
            ErrorLogger.getLogger().logError("", e);
            return;
        }
    }
}
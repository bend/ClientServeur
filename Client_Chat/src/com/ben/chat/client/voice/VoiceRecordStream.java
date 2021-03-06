/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.voice;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.views.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

/**
 *
 * @author bendaccache
 */
public class VoiceRecordStream extends Thread implements Runnable {

    private CallAcceptView callRequestView;
    private VoiceConnection vc;
    private CallView callView2;
    private boolean connected=true;

    public VoiceRecordStream(CallAcceptView callView, VoiceConnection v) {
        super("voice record stream");
        this.vc = v;
        this.callRequestView = callView;
    }

    public VoiceRecordStream(CallView callView, VoiceConnection v) {
        super("voice record stream");
        this.vc = v;
        this.callView2 = callView;
    }

    @Override
    public void run() {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream out;
            vc.sendInt(vc.getPearId());
            while (connected) {
                out = new ByteArrayOutputStream();
                    long currentTime = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - currentTime) < 1000) {
                        final int count =line.read(buffer, 0, buffer.length);
                        final ByteArrayOutputStream out2 = out;
                        if (count > 0) {
                                    out.write(buffer, 0, count);
                        }
                        Runnable r = new Runnable() {
                            public void run() {
                                try {
                                    vc.sendInt(out2.toByteArray().length);
                                    vc.sendTabByte(out2.toByteArray());
                                } catch (IOException ex) {
                                    closeConnection();
                                }
                            }
                        };
                        r.run();
                    }
            }
        } catch (IOException ex) {
            closeConnection();
        } catch (LineUnavailableException ex) {
            closeConnection();
            ErrorLogger.getLogger().logError("", ex);
        }
    }

    private void closeConnection() {
        vc.closeConnection();
        connected=false;
        try{
            callRequestView.dispose();
        }catch(NullPointerException e){
            callView2.dispose();
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate,sampleSizeInBits, channels, signed, bigEndian);
    }
}

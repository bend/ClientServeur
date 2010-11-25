/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ben.chat.client.voice;

import com.ben.chat.client.ErrorLogger.ErrorLogger;
import com.ben.chat.client.constant.ChatConstants;
import com.ben.chat.client.views.*;
import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author bendaccache
 */
public class VoiceReceiveStream extends Thread implements Runnable {

    public static int sendCount = 0;
    public static int recvCount = 0;
    public double result;
    public double noiseMoving = 0;
    public int noiseIndex = 0;
    protected static AudioFormat captureFormat;
    protected static AudioInputStream audioData;
    protected static DataInputStream dataInputAudio;
    protected static byte[] waveByteData;
    protected static int AudioBytes;
    protected Mixer JavaMixerTarget;
    protected ClientChatView cv;
    protected DataOutputStream output;
    protected DataInputStream input;
    protected boolean connected = true;
    private CallView callView;
    private CallAcceptView callAcceptView;
    private String destUsername;
    private VoiceConnection vc;
    private byte[] out;

    public VoiceReceiveStream(CallView callView, VoiceConnection vc) {
        super("voice thread");
        this.callView = callView;
        this.vc = vc;
        this.cv = ClientChatView.getInstance();
    }

    public VoiceReceiveStream(CallAcceptView callView, VoiceConnection vc) {
        super("voice thread");
        this.callAcceptView = callView;
        this.vc = vc;
        this.cv = ClientChatView.getInstance();
    }

    @Override
    public void run() {

        while (connected) {
            try {
                int size = vc.receiveInt();
                out = vc.receiveTabByte(size);
                playAudio();
            } catch (IOException ex) {
                closeConnection();
            }
        }
    }

    public void closeConnection() {
        connected = false;
        vc.closeConnection();
        try {
            callAcceptView.dispose();
        } catch (NullPointerException e) {
            callView.dispose();
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);
    }

    private void playAudio() {
        try {
            byte audio[] = out;
            InputStream input =
                    new ByteArrayInputStream(audio);
            final AudioFormat format = getFormat();
            final AudioInputStream ais =
                    new AudioInputStream(input, format,
                    audio.length / format.getFrameSize());
            DataLine.Info info = new DataLine.Info(
                    SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable() {

                int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    try {
                        int count;
                        while ((count = ais.read(
                                buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(buffer, 0, count);
                            }
                        }
                        line.drain();
                        line.close();
                    } catch (IOException e) {
                        ErrorLogger.getLogger().logError("", e);
                    }
                }
            };
            Thread playThread = new Thread(runner);
            playThread.start();
        } catch (LineUnavailableException e) {
            ErrorLogger.getLogger().logError("", e);
        }
    }
}

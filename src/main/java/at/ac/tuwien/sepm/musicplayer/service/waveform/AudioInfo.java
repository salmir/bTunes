package at.ac.tuwien.sepm.musicplayer.service.waveform;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;



/**
 * Created by Stefan with a lot of help from
 * Swing Hacks
 * Tips and Tools for Killer GUIs
 * By Joshua Marinacci, Chris Adamson
 */

public class AudioInfo {
    private static final int NUM_BITS_PER_BYTE = 8;

    private AudioInputStream audioInputStream;
    private AudioInputStream codedAudioInputStream;
    private int[][] samplesContainer;

    protected int sampleMax = 0;
    protected int sampleMin = 0;
    protected double biggestSample;

    public AudioInfo(AudioInputStream caiStream) {
        this.codedAudioInputStream = caiStream;

        createDecodedAutioInputStream(codedAudioInputStream);
        createSampleArrayCollection();
    }

    private void createDecodedAutioInputStream(AudioInputStream caiStream) {

        AudioFormat mp3Format = caiStream.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                mp3Format.getSampleRate(),
                16,
                mp3Format.getChannels(),
                mp3Format.getChannels()*2,
                mp3Format.getFrameRate(),
                false);

        audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, caiStream);
    }


    public int getNumberOfChannels(){
        int numBytesPerSample = audioInputStream.getFormat().getSampleSizeInBits() / NUM_BITS_PER_BYTE;
        return audioInputStream.getFormat().getFrameSize() / numBytesPerSample;
    }

    private void createSampleArrayCollection() {
        try {

            ByteArrayOutputStream dynBuffer = new ByteArrayOutputStream();

            byte[] bytes = new byte[4096];
            int result = 0;
            while((result = audioInputStream.read(bytes, 0, bytes.length)) != -1) {
                dynBuffer.write(bytes);
            }

            byte[] byteArray = dynBuffer.toByteArray();
            try {
                result = audioInputStream.read(byteArray);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //convert sample bytes to channel separated 16 bit samples
            samplesContainer = getSampleArray(byteArray);

            //find biggest sample. used for interpolating the yScaleFactor
            if (sampleMax > sampleMin) {
                biggestSample = sampleMax;
            } else {
                biggestSample = Math.abs(((double) sampleMin));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int[][] getSampleArray(byte[] eightBitByteArray) {
        int[][] toReturn = new int[getNumberOfChannels()][eightBitByteArray.length / (2 * getNumberOfChannels())];
        int index = 0;

        //loop through the byte[]
        for (int t = 0; t < eightBitByteArray.length;) {
            //for each iteration, loop through the channels
            for (int a = 0; a < getNumberOfChannels(); a++) {
                //do the byte to sample conversion
                //see AmplitudeEditor for more info
                int low = (int) eightBitByteArray[t];
                t++;
                int high = (int) eightBitByteArray[t];
                t++;
                int sample = (high << 8) + (low & 0x00ff);

                if (sample < sampleMin) {
                    sampleMin = sample;
                } else if (sample > sampleMax) {
                    sampleMax = sample;
                }
                //set the value.
                toReturn[a][index] = sample;
            }
            index++;
        }

        return toReturn;
    }

    public double getXScaleFactor(int panelWidth){
        return (panelWidth / ((double) samplesContainer[0].length));
    }

    public double getYScaleFactor(int panelHeight){
        return (panelHeight / (biggestSample * 2 * 1.2));
    }

    public int[] getAudio(int channel){
        return samplesContainer[channel];
    }

    protected int getIncrement(double xScale) {
        try {
            int increment = (int) (samplesContainer[0].length / (samplesContainer[0].length * xScale));
            return increment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
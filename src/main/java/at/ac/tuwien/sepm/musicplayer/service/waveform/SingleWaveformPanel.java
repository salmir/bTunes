package at.ac.tuwien.sepm.musicplayer.service.waveform;


import javax.swing.*;
import java.awt.*;


/**
 * Created by Stefan with a lot of help from
 * Swing Hacks
 * Tips and Tools for Killer GUIs
 * By Joshua Marinacci, Chris Adamson
 */

public class SingleWaveformPanel extends JPanel {

    protected static final Color BACKGROUND_COLOR = Color.decode("#162935");
    protected static final Color REFERENCE_LINE_COLOR = Color.black;
    protected static final Color WAVEFORM_COLOR = Color.decode("#2f96c7");


    private AudioInfo helper;

    public SingleWaveformPanel(AudioInfo helper) {
        this.helper = helper;

        setBackground(BACKGROUND_COLOR);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int lineHeight = getHeight() / 2;
        g.setColor(REFERENCE_LINE_COLOR);
        g.drawLine(0, lineHeight, (int)getWidth(), lineHeight);

        for(int i = 0; i < helper.getNumberOfChannels(); i++)
            drawWaveform(g, helper.getAudio(i));

    }

    protected void drawWaveform(Graphics g, int[] samples) {
        if (samples == null) {
            return;
        }

        int oldX = 0;
        int oldY = (int) (getHeight() / 2);
        int xIndex = 0;

        int increment = helper.getIncrement(helper.getXScaleFactor(getWidth()));
        g.setColor(WAVEFORM_COLOR);

        int t = 0;

        for (t = 0; t < increment; t += increment) {
            g.drawLine(oldX, oldY, xIndex, oldY);
            xIndex++;
            oldX = xIndex;
        }

        for (; t < samples.length; t += increment) {
            double scaleFactor = helper.getYScaleFactor(getHeight());
            double scaledSample = samples[t] * scaleFactor;
            int y = (int) ((getHeight() / 2) - (scaledSample));
            g.drawLine(oldX, oldY, xIndex, y);

            xIndex++;
            oldX = xIndex;
            oldY = y;
        }
    }
}
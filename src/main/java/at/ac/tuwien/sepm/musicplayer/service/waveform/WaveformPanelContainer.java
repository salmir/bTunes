package at.ac.tuwien.sepm.musicplayer.service.waveform;
import at.ac.tuwien.sepm.musicplayer.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefan with a lot of help from
 * Swing Hacks
 * Tips and Tools for Killer GUIs
 * By Joshua Marinacci, Chris Adamson
 */
@Service
public class WaveformPanelContainer extends JPanel {
    private ArrayList singleChannelWaveformPanels = new ArrayList();
    private AudioInfo audioInfo = null;

    private static final Logger logger = Logger.getLogger(WaveformPanelContainer.class);

    public WaveformPanelContainer() {
        setLayout(new BorderLayout());
    }

    public void setAudioToDisplay(File audioFile) throws ServiceException{

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            setAudioToDisplay(audioStream);
        } catch (UnsupportedAudioFileException e) {
            logger.error("Unsupported Audiofile" + e.getMessage());
            throw new ServiceException(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }


    public void setAudioToDisplay(AudioInputStream audioInputStream){
        singleChannelWaveformPanels = new ArrayList();
        audioInfo = new AudioInfo(audioInputStream);

        SingleWaveformPanel waveformPanel = new SingleWaveformPanel(audioInfo);
        singleChannelWaveformPanels.add(waveformPanel);
        add(createChannelDisplay(waveformPanel));

    }

    private JComponent createChannelDisplay(SingleWaveformPanel waveformPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(waveformPanel, BorderLayout.CENTER);

        return panel;
    }


}

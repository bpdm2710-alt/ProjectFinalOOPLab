package classic;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/** Plays the BGM and the two short WAV effects used by the game. */
public class Sound {
    private final File[] soundFiles = new File[3];
    private Clip musicClip;

    /** Loads the audio files from the local Sound folder. */
    public Sound() {
        String soundDir = "Sound/";
        soundFiles[0] = new File(soundDir + "Tetris 99 - Main Theme - SoundHub.wav");
        soundFiles[1] = new File(soundDir + "delete line.wav");
        soundFiles[2] = new File(soundDir + "gameover.wav");
    }

    /** Starts looping the background music. */
    public void playBgm() {
        playClip(0, true);
    }

    /** Plays the line-clear effect once. */
    public void playLineClear() {
        playClip(1, false);
    }

    /** Plays the game-over effect once. */
    public void playGameOver() {
        playClip(2, false);
    }

    /** Opens and starts one WAV clip. */
    private void playClip(int index, boolean loop) {
        try {
            if (index < 0 || index >= soundFiles.length) {
                return;
            }
            File soundFile = soundFiles[index];
            if (soundFile == null || !soundFile.exists()) {
                return;
            }

            if (index == 0) {
                stopBgm();
            }

            Clip clip = AudioSystem.getClip();
            try (AudioInputStream input = AudioSystem.getAudioInputStream(soundFile)) {
                clip.open(input);
            }

            if (index == 0) {
                musicClip = clip;
            }
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            clip.start();
        } catch (Exception exception) {
            System.err.println("Sound error: " + exception.getMessage());
        }
    }

    /** Pauses the looping background music. */
    public void pause() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }

    /** Resumes the looping background music. */
    public void resume() {
        if (musicClip != null && musicClip.isOpen() && !musicClip.isRunning()) {
            musicClip.start();
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /** Stops and releases the looping background music. */
    public void stop() {
        stopBgm();
    }

    /** Stops the BGM clip if it exists. */
    public void stopBgm() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }
}

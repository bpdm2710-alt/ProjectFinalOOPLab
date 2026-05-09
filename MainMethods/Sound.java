package MainMethods;

import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

public class Sound {
    
    Clip musicClip;
    File soundFiles[] = new File[5];

    public Sound(){
        // Sound index mapping:
        // 0 = Tetris 99 Main Theme (background music)
        // 1 = Delete line effect
        // 2 = Game over
        // 3 = Rotate
        // 4 = Touch floor
        
        String soundDir = "Sound/";
        soundFiles[0] = new File(soundDir + "Tetris 99 - Main Theme - SoundHub.wav");
        soundFiles[1] = new File(soundDir + "delete line.wav");
        soundFiles[2] = new File(soundDir + "gameover.wav");
        soundFiles[3] = new File(soundDir + "rotation.wav");
        soundFiles[4] = new File(soundDir + "touch floor.wav");
    }

    public void playEffect(int i) {
        playClip(i, false);
    }

    public void playAndLoop(int i) {
        playClip(i, true);
    }

    private void playClip(int i, boolean loop) {
        try {
            if (i < 0 || i >= soundFiles.length || soundFiles[i] == null) {
                System.err.println("Sound file index out of range: " + i);
                return;
            }

            // Stop previous music if playing
            if (i == 0 && musicClip != null && musicClip.isRunning()) {
                musicClip.stop();
                musicClip.close();
                musicClip = null;
            }

            File soundFile = soundFiles[i];
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + soundFile.getAbsolutePath());
                return;
            }

            Clip clip = AudioSystem.getClip();
            
            if (i == 0) {
                musicClip = clip; // Save music clip for pause/resume
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            clip.open(audioInputStream);
            audioInputStream.close();

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.addLineListener(new LineListener() {
                    @Override
                    public void update(LineEvent event) {
                        if (event.getType() == Type.STOP) {
                            try {
                                clip.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            clip.start();

        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void pause() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }

    public void resume() {
        if (musicClip != null && musicClip.isOpen() && !musicClip.isRunning()) {
            musicClip.start();
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }
}

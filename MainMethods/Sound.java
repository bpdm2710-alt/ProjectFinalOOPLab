package MainMethods;

import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

public class Sound {
    
    Clip musicClip;
    URL url[] = new URL[4];

    public Sound(){
        url[0] = getClass().getResource("/delete line.wav");
        url[1] = getClass().getResource("/gameover.wav");
        url[2] = getClass().getResource("/rotate.wav");
        url[3] = getClass().getResource("/touchdown.wav");
    }

    public void play(int i, boolean music) {

        try {
            if (i < 0 || i >= url.length || url[i] == null) {
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();

            if (music) {
                musicClip = clip;
            }

            clip.open(audioInputStream);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == Type.STOP) {
                        clip.close();
                    }
                }
            });
            audioInputStream.close();
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAndLoop(int i) {
        try {
            if (i < 0 || i >= url.length || url[i] == null) {
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();
            musicClip = clip;
            clip.open(audioInputStream);
            audioInputStream.close();
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        if (musicClip != null && musicClip.isOpen()) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
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

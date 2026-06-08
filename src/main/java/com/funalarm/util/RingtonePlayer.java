package com.funalarm.util;

import javafx.scene.media.AudioClip;

public final class RingtonePlayer {
    private static AudioClip clip;
    private static Thread beepThread;

    private RingtonePlayer() {
    }

    public static void start() {
        start(RingtoneCatalog.defaultRingtone());
    }

    public static void start(String ringtoneFileName) {
        stop();
        String file = (ringtoneFileName == null || ringtoneFileName.isBlank())
                ? RingtoneCatalog.defaultRingtone()
                : ringtoneFileName.trim();
        try {
            var url = RingtonePlayer.class.getResource("/sounds/" + file);
            if (url != null) {
                clip = new AudioClip(url.toExternalForm());
                clip.setCycleCount(AudioClip.INDEFINITE);
                clip.play();
                return;
            }
        } catch (Exception ignored) {
        }
        beepThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "ringtone-beep");
        beepThread.setDaemon(true);
        beepThread.start();
    }

    public static void stop() {
        if (clip != null) {
            clip.stop();
            clip = null;
        }
        if (beepThread != null) {
            beepThread.interrupt();
            beepThread = null;
        }
    }

    public static boolean isPlaying() {
        return clip != null || beepThread != null;
    }
}

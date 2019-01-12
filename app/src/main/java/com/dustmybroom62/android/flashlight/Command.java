package com.dustmybroom62.android.flashlight;

import android.support.design.widget.Snackbar;

import static com.dustmybroom62.android.flashlight.MainActivity.showMessage;

public class Command {
    private static StrobeRunner strobeRunner;
    private static Thread srThread;

    public static void flashlightOn() {
        strobeRunner = StrobeRunner.getInstance();
        try {
            strobeRunner.pattern = new Delay[]{new Delay(1, 0, 250)};
            strobeRunner.patternAlt = new Delay[]{};
            strobeRunner.patternRepeat = true;
            strobeRunner.playSound = false;
            if (!strobeRunner.isRunning) {
                srThread = new Thread(strobeRunner);
                srThread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

    public static void flashlightOff() {
        strobeRunner = StrobeRunner.getInstance();
        try {
            strobeRunner.requestStop = true;
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

    public static void setMorseOn(String msg, double morseCodeBaseMilliseconds, Boolean repeat, Boolean soundOn) {
        strobeRunner = StrobeRunner.getInstance();
        try {
            strobeRunner.patternRepeat = repeat;
            MorseCode.setBaseUnitMilliseconds(morseCodeBaseMilliseconds);
            strobeRunner.pattern = MorseCode.BuildMorseCodeDelayArray(msg);
            strobeRunner.playSound = soundOn;
            if (!strobeRunner.isRunning) {
                srThread = new Thread(strobeRunner);
                srThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

}

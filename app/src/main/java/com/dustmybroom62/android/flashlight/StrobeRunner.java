package com.dustmybroom62.android.flashlight;

/*
 * Original work Copyright (c) 2011 Simon Walker
 * Modified work Copyright 2015 Mathieu Souchaud
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class StrobeRunner implements Runnable {
    public static StrobeRunner getInstance()
    {
        return instance == null ? instance = new StrobeRunner() : instance;
    }

    private static StrobeRunner instance;


    public volatile boolean requestStop = false;
    public volatile boolean isRunning = false;
    public volatile boolean playSound = false;
    public volatile Delay[] pattern = {new Delay(0, 0)};
    public volatile boolean patternRepeat = false;
    public volatile Delay[] patternAlt = {};
//    public volatile StrobeLightConfig controller;
    public volatile String errorMessage = "";

    private void DoPatternElement(Camera cam, Camera.Parameters pOn, Camera.Parameters pOff, ToneGenerator toneGenerator, boolean useSound
            , double delayOn, double delayOff) throws InterruptedException {
        if (delayOn > 0) {
            toneGenerator.stopTone();
            if (useSound) {
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_9);
            }
            cam.setParameters(pOn);
            Thread.sleep(Math.round(delayOn));
            toneGenerator.stopTone();
        }

        if (delayOff > 0) {
            cam.setParameters(pOff);
            Thread.sleep(Math.round(delayOff));
        }
    }

    @Override
    public void run() {
        if(isRunning)
            return;

        requestStop = false;
        isRunning = true;

        Camera cam = Camera.open();
        cam.startPreview();
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 100);

        Camera.Parameters pOn = cam.getParameters(), pOff = cam.getParameters();

        pOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        pOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        while(!requestStop) {
            try {
                for (int i = 0; !requestStop && null != pattern && i < pattern.length; i++) {
                    DoPatternElement(cam, pOn, pOff, toneGenerator, playSound, pattern[i].on(), pattern[i].off());
                }
                if (requestStop) {
                    break;
                }
                for (int i = 0; !requestStop && null != pattern && i < patternAlt.length; i++) {
                    DoPatternElement(cam, pOn, pOff, toneGenerator, playSound, patternAlt[i].on(), patternAlt[i].off());
                }
                if (!patternRepeat) {
                    break;
                }
            }
            catch(InterruptedException ex) {
                requestStop = true;
            }
            catch(RuntimeException ex) {
                requestStop = true;
                errorMessage = "Error setting camera flash status. Your device may be unsupported.";
            }
        }
        cam.setParameters(pOff);
        cam.stopPreview();
        cam.release();

        isRunning = false;
        requestStop = false;

//        controller.mHandler.post(controller.mShowToastRunnable);
    }

}

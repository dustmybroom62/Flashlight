package com.dustmybroom62.android.flashlight;


import android.content.Context;
import android.hardware.Camera;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class StrobeLightView extends View
{
    private Camera cam;
    private StrobeRunner strobeRunner;
    private Settings settings;
    private Thread thread;
    private TextView textViewOn;
    private TextView textViewOff;
    private TextView textViewFreq;
    private SeekBar seekbarOn;
    private SeekBar seekbarOff;
//    private SeekBar seekbarFreq;
    private Switch swStrobe;
    private Button moreFreqButton;
    private Button lessFreqButton;
    private Button buttonOffLess;
    private Button buttonOffMore;
    private Button buttonOnLess;
    private Button buttonOnMore;
    private double frequency;
    private boolean strobeIsOn = false;
    private final int maxSeekDelay = 200;
    private final int maxSeekFreq = 109;
    private Delay speed = new Delay(750,550, 1);
    private View rootView;

    public boolean hasCameraRights = false;
    private Thread srThread;

    public StrobeLightView(Context context) {
        super(context);
    }

    private void strobeOn() {
        settings.setStrobeOn(speed.onFactor);
        settings.setStrobeOff(speed.offFactor);
        settings.serialize();
        strobeIsOn = true;
        strobeRunner.pattern = new Delay[] {speed};
        strobeRunner.patternAlt = new Delay[]{};
        strobeRunner.patternRepeat = true;
        if (!strobeRunner.isRunning) {
            srThread = new Thread(strobeRunner);
            srThread.start();
        }
    }
    private void strobeOff() {
        strobeRunner.requestStop = true;
        strobeIsOn = false;
    }

    private void showMessage(String msg) {
        String s = msg.trim();
    }


    /** Called when the activity is first created. */
    public void Init(View viewToManage) {
        rootView = viewToManage;
        swStrobe = (Switch) rootView.findViewById(R.id.swStrobe);
//        moreFreqButton = (Button) findViewById(R.id.buttonFreqMore);
//        lessFreqButton = (Button) findViewById(R.id.buttonFreqLess);

//        buttonOffLess = (Button) findViewById(R.id.buttonOffLess);
//        buttonOffMore = (Button) findViewById(R.id.buttonOffMore);
//        buttonOnLess = (Button) findViewById(R.id.buttonOnLess);
//        buttonOnMore = (Button) findViewById(R.id.buttonOnMore);
//
        textViewOn = (TextView) rootView.findViewById(R.id.tvStrobeDurationOn);
        textViewOff = (TextView) rootView.findViewById(R.id.tvStrobeDurationOff);
//        textViewFreq = (TextView) findViewById(R.id.textViewFreq);

        seekbarOn = (SeekBar) rootView.findViewById(R.id.seekBarOn);
        seekbarOff = (SeekBar) rootView.findViewById(R.id.seekBarOff);
//        seekbarFreq = (SeekBar) findViewById(R.id.SeekBarFreq);

        strobeRunner = StrobeRunner.getInstance();
        settings = Settings.getInstance();
        speed.onFactor = settings.getStrobeOn();
        speed.offFactor = settings.getStrobeOff();
        setSeekbarOnProgress((int) speed.onFactor);
        setSeekbarOffProgress((int) speed.offFactor);

        swStrobe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!hasCameraRights) {
                    buttonView.setChecked(false);
                    showMessage("Not Allowed: Flashlight requires Camera privelege.");
                    return;
                }
                if (isChecked) {
                    strobeOn();
                } else {
                    strobeOff();
                }
            }
        });

//        OnClickListener freqButtonListener = new OnClickListener() {
//            public void onClick(View v) {
//                int progress = seekbarFreq.getProgress();
//                if (v == moreFreqButton)
//                    progress += 1;
//                else
//                    progress -= 1;
//                seekbarFreq.setProgress(progress);
//                setSeekbarFreqProgress(progress);
//            }
//        };
//        moreFreqButton.setOnClickListener(freqButtonListener);
//        lessFreqButton.setOnClickListener(freqButtonListener);


//        OnClickListener offButtonListener = new OnClickListener() {
//            public void onClick(View v) {
//                int progress = seekbarOff.getProgress();
//                if (v == buttonOffMore)
//                    progress += 1;
//                else
//                    progress -= 1;
//                seekbarOff.setProgress(progress);
//                setSeekbarOffProgress(progress);
//            }
//        };
//        buttonOffLess.setOnClickListener(offButtonListener);
//        buttonOffMore.setOnClickListener(offButtonListener);
//
//
//        OnClickListener onButtonListener = new OnClickListener() {
//            public void onClick(View v) {
//                int progress = seekbarOn.getProgress();
//                if (v == buttonOnMore)
//                    progress += 1;
//                else
//                    progress -= 1;
//                seekbarOn.setProgress(progress);
//                setSeekbarOnProgress(progress);
//            }
//        };
//        buttonOnLess.setOnClickListener(onButtonListener);
//        buttonOnMore.setOnClickListener(onButtonListener);


        ////////////////////
        // delay on
        seekbarOn.setMax(maxSeekDelay);
        seekbarOn.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setSeekbarOnProgress(progress);
                    settings.setStrobeOn(progress);
                    if (strobeIsOn) {
                        settings.serialize();
                    }
                }
            }
        });
        setTextSpeedOn(speed.onFactor);
        seekbarOn.setProgress(delayToSeek(speed.onFactor));


        ////////////////////
        // delay off
        seekbarOff.setMax(maxSeekDelay);
        seekbarOff.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setSeekbarOffProgress(progress);
                    settings.setStrobeOff(progress);
                    if (strobeIsOn) {
                        settings.serialize();
                    }
                }
            }
        });
        setTextSpeedOff(speed.offFactor);
        seekbarOff.setProgress(delayToSeek(speed.offFactor));
    }

        ////////////////////
        // frequency
//        seekbarFreq.setMax(maxSeekFreq);
//        seekbarFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    setSeekbarFreqProgress(progress);
//                }
//            }
//        });
//        // init
//        frequency = freqFromDelays((float) strobeRunner.delayOff, (float) strobeRunner.delayOn);
//        setTextFreq(frequency);
//        seekbarFreq.setProgress(freqToSeek(frequency));
//    }
//
//    private void setSeekbarFreqProgress(int progress) {
//        frequency = seekToFreq(progress);
//
//        // avoid divide by 0
//        if (frequency <= 0)
//            frequency = 1;
//        if (strobeRunner.delayOn <= 0)
//            strobeRunner.delayOn = 1;
//
//        setTextFreq(frequency);
//
//        final double prevRatio = strobeRunner.delayOff / strobeRunner.delayOn;
//        final double newOffShare = (prevRatio / (prevRatio + 1));
//        final double newOnShare = 1 - newOffShare;
//        final double newTotalDelay = 1000 / frequency; // ms
//        strobeRunner.delayOff = newTotalDelay * newOffShare;
//        strobeRunner.delayOn = newTotalDelay * newOnShare;
//
//        setTextSpeedOff(strobeRunner.delayOff);
//        setTextSpeedOn(strobeRunner.delayOn);
//
//        seekbarOff.setProgress(delayToSeek(strobeRunner.delayOff));
//        seekbarOn.setProgress(delayToSeek(strobeRunner.delayOn));
//    }

    private void setSeekbarOffProgress(int progress) {
        speed.offFactor = seekToDelay(progress);
        setTextSpeedOff(speed.offFactor);

        frequency = freqFromDelays(speed.offFactor, speed.onFactor);
        setTextFreq(frequency);
//        seekbarFreq.setProgress(freqToSeek(frequency));
    }

    private void setSeekbarOnProgress(int progress) {
        speed.onFactor = seekToDelay(progress);
        setTextSpeedOn(speed.onFactor);

        frequency = freqFromDelays(speed.offFactor, speed.onFactor);
        setTextFreq(frequency);
//        seekbarFreq.setProgress(freqToSeek(frequency));
    }

    private void setTextSpeedOff(double speed) {
        if (null == textViewOff) return;
        textViewOff.setText(getResources().getString(R.string.textSpeedOff) + String.format(": %.0f ms", speed));
        //textViewOff.setText(String.format("%.0f", speed));
    }

    private void setTextSpeedOn(double speed) {
        if (null == textViewOn) return;
        textViewOn.setText(getResources().getString(R.string.textSpeedOn) + String.format(": %.0f ms", speed));
        //textViewOn.setText(String.format("%.0f", speed));
    }

    private void setTextFreq(double freq) {
        if (null == textViewFreq) return;
        textViewFreq.setText(getResources().getString(R.string.frequency) + String.format(": %.3f Hz", freq));
    }


    private double seekToFreq(int seek) {
        double freq;

        // check
        if (seek < 0)
            seek = 0;

        // input 0 to 9
        // output 0.1 to 1
        if (seek <= 9) {
            freq = 0.1 + ((double) seek / 10);
        }
        // input 10 to 109
        // output 1 to 100
        else {
            freq = 1 + ((double) seek - 10);
        }

        return freq;
    }

    private int freqToSeek(double freq) {
        int seek;

        // input 0 to 1
        // output 0 to 9
        if (freq <= 0.94)
            seek = (int) Math.round(freq) * 10;
            // input 1 to 100
            // output 10 to 109
        else
            seek = (int) Math.round(freq) + 9;

        // check
        if (seek < 0)
            seek = 0;
        if (seek > maxSeekFreq)
            seek = maxSeekFreq;

        return seek;
    }

    private double seekToDelay(int seek) {
        double delay;

        // check
        if (seek < 0)
            seek = 0;

        // input 0 to 999
        // output 0 to 999
        if (seek <= 1000) {
            delay = seek;
        }
        // input 1000 to 1090
        // output 1000 to 10000
        else {
            delay = 1000 + ((seek - 1000) * 100);
        }

        return delay;
    }

    private int delayToSeek(double delay) {
        int seek;

        // input 0 to 999
        // output 0 to 999
        if (delay <= 1000) {
            seek = (int) Math.round(delay);
        }
        // input 1000 to 10000
        // output 1000 to 1090
        else {
            seek = 1000 + (((int) Math.round(delay) - 1000) / 100);
        }

        // check
        if (seek < 0)
            seek = 0;
        if (seek > maxSeekDelay)
            seek = maxSeekDelay;

        return seek;
    }

    private double freqFromDelays(double delayOff, double delayOn) {
        double freq;
        if ((delayOff + delayOn) <= 0)
            freq = 0;
        else
            freq = 1000 / (delayOff + delayOn);
        return freq;
    }
}

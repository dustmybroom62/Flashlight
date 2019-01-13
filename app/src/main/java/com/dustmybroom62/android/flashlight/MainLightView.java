package com.dustmybroom62.android.flashlight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import static com.dustmybroom62.android.flashlight.Command.flashlightOff;
import static com.dustmybroom62.android.flashlight.Command.flashlightOn;
import static com.dustmybroom62.android.flashlight.Command.setMorseOn;
import static com.dustmybroom62.android.flashlight.MainActivity.showMessage;

public class MainLightView extends Fragment {

    private TextView tv1;
    private Switch swPower;
    public boolean hasCameraRights;
    private boolean flashOn;
    private Switch swSos;
    private boolean sosOn;
    private Settings settings;
    private static boolean appStartup = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getViewLight(inflater, container);
    }

    public View getViewLight(LayoutInflater inflater, ViewGroup container) {
        settings = Settings.getInstance();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tv1 = (TextView) rootView.findViewById(R.id.textView1);
        swPower = (Switch) rootView.findViewById(R.id.switch1);

        swPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean on) {
                if (!hasCameraRights) {
                    buttonView.setChecked(false);
                    tv1.setText("Disabled");
                    showMessage("Not Allowed: Flashlight requires Camera privelege.");
                    return;
                }
                if (on) {
                    flashOn = true;
                    swSos.setChecked(false);
                    tv1.setText("On");
                    flashlightOn();
                } else {
                    flashOn = false;
                    if (!sosOn) {
                        tv1.setText("Off");
                        flashlightOff();
                    }
                }
            }
        });

        swSos = (Switch) rootView.findViewById(R.id.switchSos);
        swSos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!hasCameraRights) {
                    buttonView.setChecked(false);
                    showMessage("Not Allowed: Flashlight requires Camera privelege.");
                    return;
                }
                if (isChecked) {
                    sosOn = true;
                    swPower.setChecked(false);
                    tv1.setText("SOS Mode");
                    boolean soundOn = settings.getSoundOn();
                    setMorseOn("sos ", settings.getMorseDuration(), true, soundOn);
                } else {
                    sosOn = false;
                    if (!flashOn) {
                        tv1.setText("Off");
                        flashlightOff();
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        if (appStartup) {
            appStartup = false;
            swPower.setChecked(true);
        }
        super.onStart();
    }
}

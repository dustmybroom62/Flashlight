package com.dustmybroom62.android.flashlight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class MorseView extends Fragment {
    public boolean hasCameraRights = false;
    boolean morseOn;
    private Settings settings;
    private EditText etMorseMessage;
    private EditText etMorseDuration;
    private CheckBox chkMorseRepeat;
    private CheckBox chkSoundOn;
    private Switch swMorse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getViewMorse(inflater, container);
    }

    public View getViewMorse(LayoutInflater inflater, ViewGroup container) {
        settings = Settings.getInstance();
        View rootView = inflater.inflate(R.layout.fragment_morse, container, false);
        etMorseMessage = (EditText) rootView.findViewById(R.id.etMorseMessage);
        etMorseMessage.setText(settings.getMorseMessage());
        etMorseDuration = (EditText) rootView.findViewById(R.id.etMorseDuration);
        int morseDuration = (int) settings.getMorseDuration();
        etMorseDuration.setText(String.valueOf(morseDuration));
        etMorseDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
        chkMorseRepeat = (CheckBox) rootView.findViewById(R.id.chkMorseRepeat);
        chkMorseRepeat.setChecked(settings.getMorseRepeat());
        chkSoundOn = (CheckBox) rootView.findViewById(R.id.chkSoundOn);
        chkSoundOn.setChecked(settings.getSoundOn());

        swMorse = (Switch) rootView.findViewById(R.id.switchMorse);
        swMorse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!hasCameraRights) {
                    buttonView.setChecked(false);
                    MainActivity.showMessage("Not Allowed: Flashlight requires Camera privelege.");
                    return;
                }
                if (isChecked) {
                    morseOn = true;
                    String msg = etMorseMessage.getText().toString();
                    settings.setMorseMessage(msg);
                    boolean repeat = chkMorseRepeat.isChecked();
                    boolean soundOn = chkSoundOn.isChecked();
                    String szDuration = etMorseDuration.getText().toString();
                    double duration = Double.valueOf(szDuration);
                    settings.setMorseDuration(duration);
                    settings.setMorseRepeat(repeat);
                    settings.setSoundOn(soundOn);
                    Command.setMorseOn(msg, duration, repeat, soundOn);
                } else {
                    morseOn = false;
                    Command.flashlightOff();
                }
            }
        });
        return rootView;
    }

}

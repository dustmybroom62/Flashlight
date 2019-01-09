package com.dustmybroom62.android.flashlight;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    private static final String KEY_SHARED_SETTINGS = "FLASHLIGHT_SETTINGS";
    private static Settings instance;
    public static Settings getInstance() {
        return instance == null ? instance = new Settings(): instance;
    }
    private static Context _context;
    public static Settings getInstance(Context context) {
        instance = getInstance();
        _context = context;
        deserialize();
        return instance;
    }

    private static final String KEY_MORSE_MESSAGE = "MORSE_MESSAGE";
    private static String _morseMessage;
    public String getMorseMessage() {
        return _morseMessage;
    }

    public void setMorseMessage(String morseMessage) {
        _morseMessage = morseMessage;
    }

    private static final String KEY_MORSE_DURATION = "MORSE_DURATION";
    private static double _morseDuration;

    public double getMorseDuration() {
        return _morseDuration;
    }

    public void setMorseDuration(double morseDuration) {
        _morseDuration = morseDuration;
    }

    private static final String KEY_STROBE_ON = "STROBE_ON";
    private static double _strobeOn;

    public double getStrobeOn() {
        return _strobeOn;
    }

    public void setStrobeOn(double strobeOn) {
        _strobeOn = strobeOn;
    }

    private static final String KEY_STROBE_OFF = "STROBE_OFF";
    private static double _strobeOff;

    public double getStrobeOff() {
        return _strobeOff;
    }

    public void setStrobeOff(double strobeOff) {
        _strobeOff = strobeOff;
    }

    private static final String KEY_MORSE_REPEAT = "MORSE_REPEAT";
    private static boolean _morseRepeat;

    public boolean getMorseRepeat() {
        return _morseRepeat;
    }

    public void setMorseRepeat(boolean morseRepeat) {
        _morseRepeat = morseRepeat;
    }

    private static final String KEY_SOUND_ON = "SOUND_ON";
    private static boolean _soundOn;

    public boolean getSoundOn() {return _soundOn;}

    public static void setSoundOn(boolean soundOn) {
        _soundOn = soundOn;
    }

    private static void deserialize() {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(KEY_SHARED_SETTINGS, Context.MODE_PRIVATE);
        _morseMessage = sharedPreferences.getString(KEY_MORSE_MESSAGE, null);
        _morseDuration = sharedPreferences.getFloat(KEY_MORSE_DURATION, 200f);
        _strobeOn = sharedPreferences.getFloat(KEY_STROBE_ON, 40f);
        _strobeOff = sharedPreferences.getFloat(KEY_STROBE_OFF, 40f);
        _morseRepeat = sharedPreferences.getBoolean(KEY_MORSE_REPEAT, false);
        _soundOn = sharedPreferences.getBoolean(KEY_SOUND_ON, false);
    }

    public void serialize() {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(KEY_SHARED_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString(KEY_MORSE_MESSAGE, _morseMessage);
        spEditor.putFloat(KEY_MORSE_DURATION, (float) _morseDuration);
        spEditor.putFloat(KEY_STROBE_ON, (float) _strobeOn);
        spEditor.putFloat(KEY_STROBE_OFF, (float) _strobeOff);
        spEditor.putBoolean(KEY_MORSE_REPEAT, _morseRepeat);
        spEditor.putBoolean(KEY_SOUND_ON, _soundOn);
        spEditor.apply();
    }
}

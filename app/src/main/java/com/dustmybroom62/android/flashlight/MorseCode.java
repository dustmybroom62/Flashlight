package com.dustmybroom62.android.flashlight;

import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MorseCode {
    public static AssetManager appAssets = null;
    private static double mcUnit = 200;
    private static Delay mcDot = new Delay(1, 1);
    private static Delay mcDash = new Delay(3, 1);
    private static Delay mcCharGap = new Delay(0, 3);
    private static Delay mcWordGap = new Delay(0,7);

    public static void setBaseUnitMilliseconds(double milliseconds) {
        mcUnit = milliseconds;
        mcDot.baseMilliseconds = mcUnit;
        mcDash.baseMilliseconds = mcUnit;
        mcCharGap.baseMilliseconds = mcUnit;
        mcWordGap.baseMilliseconds = mcUnit;
    }


    public static Delay Dot() {
        return mcDot;
    }

    public static Delay Dash() {
        return mcDash;
    }

    public static Delay CharGap() {
        return mcCharGap;
    }

    public static Delay WordGap() {
        return mcWordGap;
    }

    private static JSONObject jsonObjectMorseCodes = null;

    private static JSONObject getJsonObjectMorseCodes() {
        try {
            if (null == jsonObjectMorseCodes) {
                jsonObjectMorseCodes = new JSONObject(loadJSONFromAsset());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectMorseCodes;
    }

    private static String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = appAssets.open("morse_code.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private static void AddDelaysFromPattern(ArrayList<Delay> delayArrayList, String pattern, Delay mcDot, Delay mcDash) {
        if (null == pattern) {
            return;
        }
        for (int p = 0; p < pattern.length(); p++) {
            char mc = pattern.charAt(p);
            switch (mc) {
                case '.':
                    delayArrayList.add(mcDot);
                    break;
                case '-':
                    delayArrayList.add(mcDash);
                    break;
            }
        }
    }

    private static void BuildDelaysFromMessage(ArrayList<Delay> delayArrayList, String buildFrom) {
        JSONObject jsonObjectMorseCodes = getJsonObjectMorseCodes();
        if (null == jsonObjectMorseCodes) {
            return;
        }
        boolean addCharGap = false;
        for (int i = 0; i < buildFrom.length(); i++ ) {
            char c = buildFrom.charAt(i);
            if (' ' == c) {
                addCharGap = false;
                delayArrayList.add(mcWordGap);
                continue;
            }
            String pattern = jsonObjectMorseCodes.optString(String.valueOf(c), null);
            if (addCharGap) {
                delayArrayList.add(mcCharGap);
            }
            AddDelaysFromPattern(delayArrayList, pattern, mcDot, mcDash);
            addCharGap = true;
        }
    }

    public static Delay[] BuildMorseCodeDelayArray(String message) {
        if (null == message) {
            return new Delay[] {};
        }
        ArrayList<Delay> delayArrayList = new ArrayList<>();
        String buildFrom = message.toUpperCase();
        BuildDelaysFromMessage(delayArrayList, buildFrom);
        Delay[] result = new Delay[delayArrayList.size()];
        return delayArrayList.toArray(result);
    }

}

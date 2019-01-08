package com.dustmybroom62.android.flashlight;

public class Delay {
    public double baseMilliseconds = 1;
    public double onFactor = 0;
    public double offFactor = 0;

    private void init(double onFactor, double offFactor) {
        this.onFactor = onFactor;
        this.offFactor = offFactor;
    }

    public Delay (double onFactor, double offFactor, double baseMilliseconds) {
        this.baseMilliseconds = baseMilliseconds;
        init(onFactor, offFactor);
    }

    public Delay (double onFactor, double offFactor) {
        init(onFactor, offFactor);
    }

    public double on() {
        return baseMilliseconds * onFactor;
    }

    public double off() {
        return baseMilliseconds * offFactor;
    }
}

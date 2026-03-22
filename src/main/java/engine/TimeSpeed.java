package engine;

public enum TimeSpeed {
    PAUSED(0),
    NORMAL(4),
    FAST(2),
    SUPERFAST(1);

    private int multiplier;

    private TimeSpeed(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return this.multiplier;
    }
}

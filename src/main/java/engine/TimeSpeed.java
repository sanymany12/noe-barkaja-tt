package engine;

public enum TimeSpeed {
    PAUSED(0),
    NORMAL(16),
    FAST(4),
    SUPERFAST(1);

    private int multiplier;

    private TimeSpeed(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return this.multiplier;
    }
}

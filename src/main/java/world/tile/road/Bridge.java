package world.tile.road;

public class Bridge extends Road {
    private BridgeType type;

    private int speedlimit;

    public Bridge(int x, int y, BridgeType type) {
        super(x, y);

        this.isBridge = true;

        this.type = type;
    }

    public BridgeType getType() {
        return this.type;
    }
}

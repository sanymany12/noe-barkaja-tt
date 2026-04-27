package world.tile.road;

public class Bridge extends Road {
    private BridgeType type;

    private int speedlimit;

    private RoadDirection direction;
    private boolean isEnd;

    public Bridge(int x, int y, BridgeType type, RoadDirection dir, boolean isEnd) {
        super(x, y);

        this.isBridge = true;

        this.type = type;
        this.direction = dir;
        this.isEnd = isEnd;
    }

    public BridgeType getType() {
        return this.type;
    }
}

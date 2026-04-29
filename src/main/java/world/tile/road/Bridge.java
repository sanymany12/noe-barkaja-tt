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

    @Override
    public String getSpriteName() {
        String spriteName = "bridge-";
        switch (this.type) {
            case BridgeType.WOOD:
                spriteName = spriteName.concat("wood-");
                break;
            case BridgeType.STONE:
                spriteName = spriteName.concat("stone-");
                break;
            case BridgeType.GLASS:
                spriteName = spriteName.concat("glass-");
                break;
        }
        if (this.isEnd) {
            spriteName = spriteName.concat("end-");
        }
        switch (this.direction) {
            case RoadDirection.NORTH:
                spriteName = spriteName.concat("n");
                break;
            case RoadDirection.SOUTH:
                spriteName = spriteName.concat("s");
                break;
            case RoadDirection.WEST:
                spriteName = spriteName.concat("w");
                break;
            case RoadDirection.EAST:
                spriteName = spriteName.concat("e");
        }
        return spriteName;
    }
}

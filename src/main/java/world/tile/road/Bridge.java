package world.tile.road;

import world.tile.Tile;

public class Bridge extends Road {
    private BridgeType type;

    private int speedlimit;

    private Tile startTile;
    private Tile endTile;

    private RoadDirection direction;
    private boolean isEnd;
    private boolean isPreBuilt;

    public Bridge(int x, int y, BridgeType type, RoadDirection dir, Tile startTile, Tile endTile, boolean isEnd, boolean isPreBuilt) {
        super(x, y, isPreBuilt);

        this.isBridge = true;

        this.startTile = startTile;
        this.endTile = endTile;

        this.type = type;
        this.direction = dir;
        this.isEnd = isEnd;
        this.isPreBuilt = isPreBuilt;
    }

    public RoadDirection getDirection() {
        return this.direction;
    }

    public boolean getIsEnd() {
        return this.isEnd;
    }

    public Tile getStartTile() {
        return this.startTile;
    }

    public Tile getEndTile() {
        return this.endTile;
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

package world.tile.road;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private List<RoadDirection> connections;
    private final int COST_TO_REMOVE = 3;
    private final int COST_TO_BUILD = 5;

    public Road() {
        this.connections = new ArrayList<RoadDirection>();
    }

    public int getCostToBuild() {
        return this.COST_TO_BUILD;
    }

    public void setConnection(RoadDirection dir) {
        if (!this.connections.contains(dir)) {
            this.connections.add(dir);
        }
    }

    public boolean canGo(RoadDirection dir) {
        return this.connections.contains(dir);
    }

    public String getSpriteName() {
        String spriteName = "road-";
        spriteName = spriteName.concat(String.valueOf(connections.toArray().length));
        spriteName = spriteName.concat("-");
        if (canGo(RoadDirection.NORTH)) {
            spriteName = spriteName.concat("n");
        }
        if (canGo(RoadDirection.WEST)) {
            spriteName = spriteName.concat("w");
        }
        if (canGo(RoadDirection.SOUTH)) {
            spriteName = spriteName.concat("s");
        }
        if (canGo(RoadDirection.EAST)) {
            spriteName = spriteName.concat("e");
        }
//        spriteName.concat(".png");
        return spriteName;
    }
}

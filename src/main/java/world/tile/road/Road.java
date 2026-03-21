package world.tile.road;

import world.World;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private List<RoadDirection> connections;
    private final int COST_TO_REMOVE = 3;

    public Road() {
        this.connections = new ArrayList<RoadDirection>();
    }

    public void setConnection(RoadDirection dir) {
        this.connections.add(dir);
    }

    public boolean canGo(RoadDirection dir) {
        return this.connections.contains(dir);
    }

    public String getSpriteName() {
        return "TODO";
    }
}

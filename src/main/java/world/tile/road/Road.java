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
        if (!this.connections.contains(dir)) {
            this.connections.add(dir);
        }
    }

    public boolean canGo(RoadDirection dir) {
        return this.connections.contains(dir);
    }

    public String getSpriteName() {
        String spriteName = "road-";
        spriteName.concat(String.valueOf(connections.toArray().length));
        spriteName.concat("-");
        if (canGo(RoadDirection.NORTH)) {
            spriteName.concat("n");
        }
        if (canGo(RoadDirection.WEST)) {
            spriteName.concat("w");
        }
        if (canGo(RoadDirection.SOUTH)) {
            spriteName.concat("s");
        }
        if (canGo(RoadDirection.EAST)) {
            spriteName.concat("e");
        }
        spriteName.concat(".png");
        return spriteName;
//        switch (connections.toArray().length) {
//            case 4:
//                return "road-4.png";
//            case 3:
//                if (!this.canGo(RoadDirection.NORTH)) {
//                    return "road-3-wse.png";
//                }
//                else if (!this.canGo(RoadDirection.WEST)) {
//                    return "road-3-nse.png";
//                }
//                else if (!this.canGo(RoadDirection.SOUTH)) {
//                    return "road-3-nwe.png";
//                }
//                else return "road-3-nws.png";
//            case 2:
//                if (this.canGo(RoadDirection.NORTH)) {
//
//                }
//            case 1:
//                if (this.canGo(RoadDirection.NORTH)) {
//                    return "road-1-n.png";
//                }
//                else if (this.canGo(RoadDirection.EAST)) {
//                    return "road-1-e.png";
//                }
//                else if (this.canGo(RoadDirection.WEST)) {
//                    return "road-1-w.png";
//                }
//                else return "road-1-s.png";
//                break;
//            default:
//                return "road-0.png";
//        }
    }
}

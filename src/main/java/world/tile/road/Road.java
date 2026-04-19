package world.tile.road;

import world.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private List<RoadDirection> connections;

    private final int COST_TO_REMOVE = 3;
    private final int COST_TO_BUILD = 5;

    private Vehicle rightLaneV;
    private Vehicle leftLaneV;
    private Vehicle rightLaneH;
    private Vehicle leftLaneH;

    private int locationX;
    private int locationY;

    public Road(int x, int y) {
        this.connections = new ArrayList<RoadDirection>();

        this.rightLaneV = null;
        this.leftLaneV = null;
        this.rightLaneH = null;
        this.leftLaneH = null;

        this.locationX = x;
        this.locationY = y;
    }

    public int getCostToBuild() {
        return this.COST_TO_BUILD;
    }

    public Vehicle getRightLaneV() {
        return this.rightLaneV;
    }

    public Vehicle getLeftLaneV() {
        return this.leftLaneV;
    }

    public Vehicle getRightLaneH() {
        return this.rightLaneH;
    }

    public Vehicle getLeftLaneH() {
        return this.leftLaneH;
    }

    public boolean isOccupied (RoadDirection dir) {
        switch (dir) {
            case RoadDirection.NORTH:
                if (!(this.rightLaneV == null && this.rightLaneH == null && this.leftLaneH == null)) {
                    return true;
                } else {
                    return false;
                }
            case RoadDirection.SOUTH:
                if (!(this.leftLaneV == null && this.leftLaneH == null && this.rightLaneH == null)) {
                    return true;
                } else {
                    return false;
                }
            case RoadDirection.EAST:
                if (!(this.leftLaneH == null && this.rightLaneV == null && this.leftLaneV == null)) {
                    return true;
                } else {
                    return false;
                }
            case RoadDirection.WEST:
                if (!(this.rightLaneH == null && this.leftLaneV == null && this.rightLaneV == null)) {
                    return true;
                } else {
                    return false;
                }
            default:
                return true;
        }
    }

    // Jármű érkezése sávkezeléssel
    public void vehicleEnters(Vehicle vehicle, RoadDirection vehicleDirection) {
        switch (vehicleDirection) {
            case RoadDirection.NORTH:
                this.rightLaneV = vehicle;
                break;
            case RoadDirection.SOUTH:
                this.leftLaneV = vehicle;
                break;
            case RoadDirection.EAST:
                this.leftLaneH = vehicle;
                break;
            case RoadDirection.WEST:
                this.rightLaneH = vehicle;
                break;
        }
    }

    // Jármű távozása sávkezeléssel
    public void vehicleLeaves(Vehicle vehicle, RoadDirection vehicleDirection) {
        switch (vehicleDirection) {
            case RoadDirection.NORTH:
                this.rightLaneV = null;
                break;
            case RoadDirection.SOUTH:
                this.leftLaneV = null;
                break;
            case RoadDirection.EAST:
                this.leftLaneH = null;
                break;
            case RoadDirection.WEST:
                this.rightLaneH = null;
                break;
        }
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

package world.tile.road;

public enum RoadDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public RoadDirection getOpposite() {
        return switch (this) {
            case RoadDirection.NORTH -> RoadDirection.SOUTH;
            case RoadDirection.EAST -> RoadDirection.WEST;
            case RoadDirection.SOUTH -> RoadDirection.NORTH;
            case RoadDirection.WEST -> RoadDirection.EAST;
        };
    }
}

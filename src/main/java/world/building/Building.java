package world.building;

import world.tile.Tile;

public abstract class Building<In, Out> {
    protected int row;
    protected int col;
    protected BuildingType type;

    public Building(Tile tile) {
        this.col = tile.getCoordinate().x;
        this.row = tile.getCoordinate().y;
        this.type = null;
    }

    public BuildingType getBuildingType() {
        return this.type;
    }

    public void update() {

    }
}

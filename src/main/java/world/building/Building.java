package world.building;

import world.World;
import world.tile.Tile;

public abstract class Building<In, Out> {
    protected World world;

    protected int row;
    protected int col;

    protected BuildingType type;

    public Building(World world, Tile tile) {
        this.world = world;
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

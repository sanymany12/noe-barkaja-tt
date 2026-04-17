package world.building;

import world.World;
import world.tile.Tile;

public abstract class Building<In, Out> {
    transient protected World world;

    protected int width;
    protected int height;

    protected BuildingType type;

    public Building(World world) {
        this.world = world;

        this.width = 0;
        this.height = 0;
        
        this.type = null;
    }

    public BuildingType getBuildingType() {
        return this.type;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public abstract void newDay() throws Exception;

    public abstract String getSpriteName();
}

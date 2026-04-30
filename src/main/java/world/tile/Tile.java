package world.tile;

import engine.AssetManager;
import world.building.Building;
import world.building.BuildingType;
import world.tile.road.Road;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Tile {
    private Point coordinate;
    private TerrainType terrainType;
    private int treeCount;
    private Road road;
    private Building<?,?> building;
    private boolean isAnchor; //Az épület bal alsó cellája (horgony)
    private final int randomValue = ThreadLocalRandom.current().nextInt(4);

    public Tile(Point coordinate, TerrainType terrainType, int treeCount, Road road, Building<?, ?> building, boolean isAnchor) {
        this.coordinate = coordinate;
        this.terrainType = terrainType;
        this.treeCount = treeCount;
        this.road = road;
        this.building = building;
        this.isAnchor = isAnchor;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public void setBuilding(Building<?, ?> building) {
        this.building = building;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public void setTreeCount(int treeCount) {
        this.treeCount = treeCount;
    }

    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    public int getTreeCount() {
        return treeCount;
    }

    public Road getRoad() {
        return road;
    }

    public Building<?, ?> getBuilding() {
        return building;
    }

    public boolean isAnchor(){
        return isAnchor;
    }

    public void setAnchor(boolean value){
        isAnchor = value;
    }

    public boolean isEmpty() {
        return this.building == null && this.road == null && this.treeCount == 0;
    }

    public void removeStation() {
        if (this.building.getBuildingType() == BuildingType.STATION) {
            this.building = null;
            this.terrainType = TerrainType.LAND;
        }
    }

    public void removeRoad() {
        this.road = null;
        this.terrainType = TerrainType.ROAD;
    }

    public String getSpriteName() {
        switch (terrainType) {
            case LAND, ROAD, BUILDING, STOP:
                if (this.coordinate.x % 2 == 0) {
                    if (this.coordinate.y % 2 == 0) {
                        return "land-1";
                    } else {
                        return "land-2";
                    }
                } else {
                    if (this.coordinate.y % 2 == 0) {
                        return "land-2";
                    } else {
                        return "land-1";
                    }
                }
            case WATER:
                return "water";
            case CLIFF:
                return "cliff";
            case VOID:
                return "cliff";
            default:
                return "none";
        }
    }
}

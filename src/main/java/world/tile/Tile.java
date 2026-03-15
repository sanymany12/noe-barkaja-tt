package world.tile;

import world.building.Building;
import world.tile.road.Road;

public class Tile {
    private Point coordinate;
    private TerrainType terrainType;
    private int treeCount;
    private Road road;
    private Building<?,?> building;
    private boolean isAnchor;

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

    //TODO
    public int calculateCost(){
        return 0;
    }

    //TODO
    public boolean isEmpty(){
        return false;
    }

}

package world.tile.road;

public enum BridgeType {
    WOOD(70, 3, 1),
    STONE(80, 5, 5),
    GLASS(150, 10, 20);

    private int cost;
    private int maxlength;
    private int speedlimit;

    BridgeType(int cost, int maxlength, int speedlimit) {
        this.cost = cost;
        this.maxlength = maxlength;
        this.speedlimit = speedlimit;
    }

    public int getCost() {
        return this.cost;
    }

    public int getMaxLength() {
        return this.maxlength;
    }

    public int getSpeedLimit() {
        return this.speedlimit;
    }
}

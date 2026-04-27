package world.vehicle;

public enum VehicleType {
    BUS(4),
    FOODTRUCK(7),
    ANIMALTRUCK(5);

    private int baseSpeed;

    VehicleType(int speed) {
        this.baseSpeed = speed;
    }

    public int getBaseSpeed() {
        return this.baseSpeed;
    }
}

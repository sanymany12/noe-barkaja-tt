package world.building;

import world.World;
import world.vehicle.Bus;
import world.vehicle.Vehicle;
import world.tile.Tile;
import world.vehicle.VehicleType;

import java.util.Random;

public class BusStop extends Building<Integer,Integer> {
    private Random random;

    private Vehicle vehicle;

    private int numOfPeople;
    private boolean isStart;
    private boolean isStop;

    public BusStop(World world, Tile tile) {
        super(world, tile);

        this.random = new Random();

        this.vehicle = null;

        this.numOfPeople = 0;
        this.isStart = false;
        this.isStop = false;
    }

    // Jármű érkezésekor ellenőrzi, hogy busz érkezett-e
    //      -> ha igen és a mező a jelenlegi kiindulópont az utasoknak, felveszi őket
    //      -> ha igen és a mező a jelenlegi célpont az utasoknak, leteszi őket
    public void vehicleArrives(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (this.vehicle.getVehicleType() == VehicleType.BUS) {
            if (this.isStop && !vehicle.isEmpty()) {
                vehicle.unloadTo(this);
            } else if (this.isStart && vehicle.isEmpty()) {
                vehicle.loadFrom(this);
            }
        }
    }

    // Utasok érkeznek
    public boolean setAsStart() {
        if (this.isStop) {
            return false;
        }
        else {
            this.numOfPeople = random.nextInt(1,20);
            this.isStart = true;
            return true;
        }
    }

    // Új célpont beállítása ebbe a buszmegállóba
    public boolean setAsStop() {
        if (this.isStart) {
            return false;
        }
        else {
            this.isStop = true;
            return true;
        }
    }

    // Megálló visszaállítása alaphelyzetbe (ha letelt az idő a teljesítésre és már új útvonal van)
    public void resetStop() {
        this.numOfPeople = 0;
        this.isStart = false;
        this.isStop = false;
    }

    // Segédfüggvény az utasok buszra való feltöltéséhez
    public int loadTo(Bus bus) {
        return this.numOfPeople;
    }
}

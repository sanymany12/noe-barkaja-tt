package world.building;

import world.World;
import world.tile.road.RoadDirection;
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

    private RoadDirection direction;

    private final int BUS_TICKET_PRICE = 50;

    public BusStop(World world, RoadDirection dir) {
        super(world);

        this.width = 1;
        this.height = 1;

        this.random = new Random();

        this.vehicle = null;

        this.numOfPeople = 0;
        this.isStart = false;
        this.isStop = false;

        this.direction = dir;
    }

    public boolean isStart() {
        return this.isStart;
    }

    public boolean isStop() {
        return this.isStop;
    }

    public int getNumOfPeople() {
        return this.numOfPeople;
    }

    // Segédfüggvény az utasok buszra való feltöltéséhez
    public void loadOntoBus() {
        this.numOfPeople = 0;
    }

    public void peopleArrived(int people) {
        this.world.receiveMoney(people * this.BUS_TICKET_PRICE);
    }

    // Jármű érkezésekor ellenőrzi, hogy busz érkezett-e
    //      -> ha igen és a mező a jelenlegi kiindulópont az utasoknak, felveszi őket
    //      -> ha igen és a mező a jelenlegi célpont az utasoknak, leteszi őket
    public void vehicleArrives(Vehicle vehicle) throws Exception {
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
    public void setAsStart() throws Exception {
        if (this.isStop) {
            throw new Exception("This stop is already assigned as a destination!");
        }
        else {
            this.numOfPeople = random.nextInt(1,20);
            this.isStart = true;
        }
    }

    // Új célpont beállítása ebbe a buszmegállóba
    public void setAsStop() throws Exception {
        if (this.isStart) {
            throw new Exception("This stop is already assigned as a starting point!");
        }
        else {
            this.isStop = true;
        }
    }

    // Megálló visszaállítása alaphelyzetbe (ha letelt az idő a teljesítésre és már új útvonal van)
    public void resetStop() {
        this.numOfPeople = 0;
        this.isStart = false;
        this.isStop = false;
    }

    @Override
    public String getSpriteName() {
        String spriteName = "bus-stop-";
        switch(this.direction) {
            case RoadDirection.NORTH:
                spriteName = spriteName.concat("n-");
                break;
            case RoadDirection.EAST:
                spriteName = spriteName.concat("e-");
                break;
            case RoadDirection.SOUTH:
                spriteName = spriteName.concat("s-");
                break;
            case RoadDirection.WEST:
                spriteName = spriteName.concat("w-");
        }
        if (this.isStart) {
            spriteName = spriteName.concat("start");
        }
        if (this.isStop) {
            spriteName = spriteName.concat("stop");
        }
        return spriteName;
    }

    @Override
    public void newDay() {
        // Itt abszolút nincs semmi dolga, mivel a buszokat globálisan kezeljük, de az épületek nagyrészénél igen, szóval...
    }
}

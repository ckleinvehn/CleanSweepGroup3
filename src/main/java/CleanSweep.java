import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import static java.lang.Thread.sleep;

public class CleanSweep {

    private double battery;
    private double currCapacity;
    private double totalCapacity = 50.0;
    public FloorCell currentLocation;
    public FloorCell previousLocation;
    public Location startingLocation;
    public SensorSimulator sensors;
    public Location location; //starting location
    private CleanSweep cleanSweep = null;
    public State currentState;
    public Stack<Location> traversalStack = new Stack<>();


    public CleanSweep(Double battery, double currCapacity, SensorSimulator sensors, FloorCell currentLocation, FloorCell previousLocation) {
        this.sensors = sensors;
        this.currentLocation = currentLocation;
        this.previousLocation = previousLocation;
        this.battery = battery;
        this.currCapacity = currCapacity;

    }

    public CleanSweep getInstance() {
        if (cleanSweep == null) {
            synchronized (CleanSweep.class) {
                if (cleanSweep == null) {
                    cleanSweep = new CleanSweep(battery, currCapacity, sensors, currentLocation, previousLocation);
                }
            }
        }
        return cleanSweep;
    }


    public double useBattery() {
        double batteryDec = 0;

        if (currentLocation.surfaceType == SurfaceType.BARE_FLOOR)
            batteryDec = 1;
        if (currentLocation.surfaceType == SurfaceType.LOW_PILE_CARPET)
            batteryDec = 2;
        else if (currentLocation.surfaceType == SurfaceType.HIGH_PILE_CARPET)
            batteryDec = 3;

        if (previousLocation == currentLocation) {
            battery = battery - batteryDec;
        } else {
            if ((currentLocation.surfaceType == SurfaceType.BARE_FLOOR) && (previousLocation.surfaceType == SurfaceType.LOW_PILE_CARPET))
                battery = battery - ((batteryDec + 2) / 2);
            if ((currentLocation.surfaceType == SurfaceType.BARE_FLOOR) && (previousLocation.surfaceType == SurfaceType.HIGH_PILE_CARPET))
                battery = battery - ((batteryDec + 3) / 2);
            if ((currentLocation.surfaceType == SurfaceType.LOW_PILE_CARPET) && (previousLocation.surfaceType == SurfaceType.BARE_FLOOR))
                battery = battery - ((batteryDec + 1) / 2);
            if ((currentLocation.surfaceType == SurfaceType.LOW_PILE_CARPET) && (previousLocation.surfaceType == SurfaceType.HIGH_PILE_CARPET))
                battery = battery - ((batteryDec + 3) / 2);
            if ((currentLocation.surfaceType == SurfaceType.HIGH_PILE_CARPET) && (previousLocation.surfaceType == SurfaceType.BARE_FLOOR))
                battery = battery - ((batteryDec + 1) / 2);
            if ((currentLocation.surfaceType == SurfaceType.HIGH_PILE_CARPET) && (previousLocation.surfaceType == SurfaceType.LOW_PILE_CARPET))
                battery = battery - ((batteryDec + 2) / 2);


        }
        if (needToCharge()) {
            System.out.println(this.currentState);
            return battery;
        } else System.out.println("Battery Percent: " + (battery / 250) * 100 + "%");
        return battery;

    }

    public boolean needToCharge() {
        if (battery < .20 * 250) {
            this.currentState = State.LOW_BATTERY;
            System.out.println("Need to charge! Low Battery!");

            return true;
        } else return false;
    }

    public void suckUpDirt() throws InterruptedException {
        while (currentLocation.dirtAmount > 0) {
            System.out.println("Cleaning... " + currentLocation.dirtAmount + " units of dirt left");
            currentLocation.dirtAmount--;
            currCapacity++;
            //sleep(1000);


        }


        System.out.println("FloorCell is Clean!");
        System.out.println("Capacity : " + (currCapacity / totalCapacity) * 100 + "% full");

    }

    public void move(Direction direction) {
        FloorCell tempPrev = currentLocation;
        if (direction == Direction.SOUTH) {
            moveSouth();
        }

        if (direction == Direction.EAST) {
            moveEast();
        }

        if (direction == Direction.NORTH) {
            moveNorth();
        }

        if (direction == Direction.WEST) {
            moveWest();
        }
        tempPrev = previousLocation;
    }

    public void moveNorth() {
        System.out.println("Move North");
        int x = sensors.currentLocation.x - 1;
        int y = sensors.currentLocation.y;

        sensors.currentLocation = new Location(x, y);
        updateCurrentCell();
    }

    public void moveSouth() {
        System.out.println("Move South");
        int x = sensors.currentLocation.x + 1;
        int y = sensors.currentLocation.y;

        sensors.currentLocation = new Location(x, y);
        updateCurrentCell();
    }

    public void moveEast() {
        System.out.println("Move East");
        int x = sensors.currentLocation.x;
        int y = sensors.currentLocation.y + 1;

        sensors.currentLocation = new Location(x, y);
        updateCurrentCell();
    }

    public void moveWest() {
        System.out.println("Move West");
        int x = sensors.currentLocation.x;
        int y = sensors.currentLocation.y - 1;

        sensors.currentLocation = new Location(x, y);
        updateCurrentCell();
    }

    public void updateCurrentCell() {
        int x = sensors.currentLocation.x;
        int y = sensors.currentLocation.y;


        currentLocation = sensors.floorPlan.floorLayout.get(x).get(y);
    }

    public void goToCharger() {
       while(!traversalStack.isEmpty()){
           Location togo = traversalStack.pop();
           int x = togo.x;
           int y = togo.y;
           sensors.currentLocation = new Location(x, y);
           updateCurrentCell();
           System.out.println("moving to location" + "("+x+","+y+")");
       }


    }

    public void zigZag() throws InterruptedException {
        Direction direction = Direction.SOUTH;
        //sleep(1000);
        suckUpDirt();
        //sleep(1000);
        useBattery();
        //sleep(1000);
        startingLocation = sensors.currentLocation;
        traversalStack.push(sensors.currentLocation);
        sensors.floorPlan.print();

        while (!(sensors.isEastWall() && sensors.isSouthWall())) {
            if (!sensors.isWall(direction)) {
                move(direction);
            } else {
                moveEast();

                if (direction == Direction.SOUTH) {
                    direction = Direction.NORTH;
                } else {
                    direction = Direction.SOUTH;
                }
            }
            traversalStack.push(sensors.currentLocation);
            //sleep(1000);
            suckUpDirt();
            //sleep(1000);
            useBattery();
            //sleep(1000);
            sensors.floorPlan.print();

            System.out.format("Current Location \n x: %d, y: %d\n", sensors.currentLocation.x, sensors.currentLocation.y);

        }
    }

    public void turnOn() {
        try {
            zigZag();
            System.out.println("TraveralStack:");
            for (Location f : traversalStack) {
                System.out.print("("+f.x+","+f.y+") ->");
            }
            System.out.println("\n");
            goToCharger();

            for (Location f : traversalStack) {
                System.out.println(f.x+","+f.y);
            }

            System.out.println("Back to Charger, location:" + sensors.currentLocation.x +","+ sensors.currentLocation.y);
        } catch (InterruptedException e) {
            System.out.println("CleanSweep cannot be turned on. Please contact customer support.");
            e.printStackTrace();

        }
    }
}

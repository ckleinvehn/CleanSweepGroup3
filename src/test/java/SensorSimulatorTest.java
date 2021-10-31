import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SensorSimulatorTest {

    @Test
    void visitedTest() {
        FloorPlan oneRoom = FloorPlan.oneRoomFloorPlan(3, 3);
        Location startingLocation = new Location(0, 0);
        SensorSimulator sensor = new SensorSimulator(oneRoom, startingLocation);
        CleanSweep cs = new CleanSweep(250.0, 0, sensor, oneRoom.floorLayout.get(0).get(0), oneRoom.floorLayout.get(0).get(0));
        cs.sensors.visitedCells(oneRoom);
        cs.sensors.printVisitedLocations();

    }

    @Test
    void traversableTest() {
        FloorPlan oneRoom = FloorPlan.oneRoomFloorPlan(3, 3);
        Location startingLocation = new Location(0, 0);
        SensorSimulator sensor = new SensorSimulator(oneRoom, startingLocation);
        CleanSweep cs = new CleanSweep(250.0, 0, sensor, oneRoom.floorLayout.get(0).get(0), oneRoom.floorLayout.get(0).get(0));
        cs.sensors.getTraversableDirections();
    }

    @Test
    void wallTest() {
        FloorPlan oneRoom = FloorPlan.oneRoomFloorPlan(3, 3);
        Location startingLocation = new Location(0, 0);
        SensorSimulator sensor = new SensorSimulator(oneRoom, startingLocation);
        CleanSweep cs = new CleanSweep(250.0, 0, sensor, oneRoom.floorLayout.get(0).get(0), oneRoom.floorLayout.get(0).get(0));
        cs.sensors.isWall(Direction.NORTH);
        Assertions.assertTrue(cs.currentLocation.wallsPresent.contains(Direction.NORTH));
    }

    @Test
    void obstacleTest() {
        FloorPlan oneRoom = FloorPlan.oneRoomFloorPlan(3, 3);
        Location startingLocation = new Location(0, 0);
        SensorSimulator sensor = new SensorSimulator(oneRoom, startingLocation);
        CleanSweep cs = new CleanSweep(250.0, 0, sensor, oneRoom.floorLayout.get(0).get(0), oneRoom.floorLayout.get(0).get(0));
        cs.sensors.isObstacle(Direction.NORTH);
        Assertions.assertTrue(cs.currentLocation.wallsPresent.contains(Direction.NORTH));
        //assumes we always put the charging/startingLoc at 0,0
    }
}

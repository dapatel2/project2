import java.io.FileReader;
import java.util.*;
import java.util.Properties;

class Simulator {
    /*
     *  Properties  *
     */
    private String structure;
    private int floors;
    private double passengers;
    private int elevators;
    private int elevatorCapacity;
    private int duration;

    /*
     ************
     *  Fields  *
     ************
     */
    private List<List<Passenger>> mListFloorPassengers;
    private List<Elevator> mListElevators;
    private List<Long> mListTimes;

    public Simulator(){
        structure = "linked";
        floors = 32;
        passengers = 0.03;
        elevators = 1;
        elevatorCapacity = 10;
        duration = 500;
    }

    public Simulator(String configFile){
        Properties config = new Properties();
        // Use java.util.Properties to load the file
        try {
            FileReader reader = new FileReader(configFile);
            config.load(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try{
            structure = config.getProperty("structures");
        }catch (Exception er){

        }
        try{
            floors = Integer.parseInt(config.getProperty("floors"));
        }catch (Exception er){

        }
        try{
            passengers = Double.parseDouble(config.getProperty("passengers"));
        }catch (Exception er){

        }
        try{
            elevators = Integer.parseInt(config.getProperty("elevators"));
        }catch (Exception er){

        }
        try{
            elevatorCapacity = Integer.parseInt(config.getProperty("elevatorCapacity"));
        }catch (Exception er){

        }
        try{
            duration = Integer.parseInt(config.getProperty("duration"));
        }catch (Exception er){

        }
        // Set up the simulation configuration
        // Handle missing property file by using defaults
        if(!structure.equals("linked") && !structure.equals("array")){
            structure = "linked";
        }
        if(floors < 2)
            floors = 32;
        if(passengers < 0 || passengers > 1.0)
            passengers = 0.03;
        if(elevators < 1)
            elevators = 1;
        if(elevatorCapacity < 1)
            elevatorCapacity = 10;
        if(duration < 1)
            duration = 500;

    }

    private int findNearestPassesnger(int currentFloor, int direction){
        int minDis = floors;
        int destFloor = -1;
        for(int i = 0 ; i < floors; i++){
            if(mListFloorPassengers.get(i).size() == 0 || currentFloor == i)
                continue;
            int currentDirection = currentFloor - i > 0 ? 1 : -1;

            if(direction == 0 || currentDirection == direction){
                int curDis = Math.abs(currentFloor - i);
                if(curDis < minDis){
                    minDis = curDis;
                    destFloor = i;
                }
            }
        }
        return destFloor;
    }
    public void runSimulation() {
        // Initialize building and elevators
        // For each tick:
        // - building.runTick();
        if(structure.equals("linked")){
            mListElevators = new LinkedList<>();
            for(int i = 0 ; i < elevators; i++){
                mListElevators.add(new Elevator(floors, structure, elevatorCapacity));
            }
            mListFloorPassengers = new LinkedList<>();
            for(int i = 0 ; i < floors; i++){
                mListFloorPassengers.add(new LinkedList<Passenger>());
            }
            mListTimes = new LinkedList<>();
        }
        else{
            mListElevators = new ArrayList<>();
            for(int i = 0 ; i < elevators; i++){
                mListElevators.add(new Elevator(floors, structure, elevatorCapacity));
            }
            mListFloorPassengers = new ArrayList<>();
            for(int i = 0 ; i < floors; i++){
                mListFloorPassengers.add(new ArrayList<Passenger>());
            }
            mListTimes = new ArrayList<>();
        }
        Random rand = new Random(System.currentTimeMillis());
        int passengerCount = 0;
        int indexPassenger = 1;
        for(int i = 0 ; i < duration; i++){

            // generate passenger
            for(int j = 0 ; j < floors; j++){
                double passengerProb = rand.nextDouble();
                if(passengerProb < passengers){
                    int destFloor = rand.nextInt(floors);
                    while(destFloor == j)
                        destFloor = rand.nextInt(floors);
                    mListFloorPassengers.get(j).add(new Passenger(indexPassenger, j, destFloor));
                    indexPassenger++;
                }
                passengerCount += mListFloorPassengers.get(j).size();
            }
            // move elevator
            if(passengerCount > 0){
                for(int j = 0 ; j < elevators; j++){
                    //unload passengers
                    Elevator currentEle = mListElevators.get(j);
                    int tickMove = 5;
                    while(tickMove > 0){
                        if(!currentEle.isEmpty()){
                            List<Passenger> arrivedPassengers = currentEle.unloadPassenger();
                            for(Passenger p: arrivedPassengers){
                                mListTimes.add(p.arriveFloor());
                            }
                        }
                        //load passengers
                        List<Passenger> floorPass = mListFloorPassengers.get(currentEle.getCurrentFloor());
                        if(!currentEle.isFull() && floorPass.size() > 0){
                            List<Passenger> loadPassengers = currentEle.loadPassenger(floorPass);
                            for(Passenger p: loadPassengers){
                                floorPass.remove(p);
                            }
                        }
                        if(currentEle.isEmpty()){
                            //find nearest waiting passenger
                            int destFloor = findNearestPassesnger(currentEle.getCurrentFloor(), currentEle.getDirection());
                            if(destFloor > -1){
                                int nMove = currentEle.moveTo(destFloor, tickMove);
                                tickMove -= nMove;
                            }
                            else{
                                tickMove = 0;
                            }
                        }
                        else{
                            currentEle.moveStep();
                            tickMove--;
                        }

                    }
                }
            }
        }
    }

    public void reportResults() {
        // Calculate and print out the required metrics
        // Average, longest, shortest times
        long avgTime = 0;
        long shortestTime = Long.MAX_VALUE;
        long longestTime = 0;
        for(Long time : mListTimes){
            avgTime += time;
            if(shortestTime > time)
                shortestTime = time;
            if(time > longestTime)
                longestTime = time;
        }
        if(mListTimes.size()> 0)
            avgTime /= mListTimes.size();
        System.out.println();
        System.out.println("Average travel time of passengers: " + avgTime + " ms.");
        System.out.println("Longest travel time of passenger: " + longestTime + " ms.");
        System.out.println("Shortest travel time of passenger: " + shortestTime + " ms.");
    }

    public static void main(String[] args) {
        Simulator simulator;
        if(args.length > 0)
            simulator = new Simulator(args[0]);
        else
            simulator = new Simulator();
        simulator.runSimulation();
        simulator.reportResults();
    }
}

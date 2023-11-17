class Passenger {
    private int startingFloor;
    private int destinationFloor;
    private long arrivalTime;

    private int index;
    
    public Passenger(int index, int start, int destination) {
        this.index = index;
        this.startingFloor = start;
        this.destinationFloor = destination;
        arrivalTime = System.currentTimeMillis();
    }

    public long arriveFloor(){
        long duration = System.currentTimeMillis() - arrivalTime;
        System.out.println(index + " passenger arrive from " + (startingFloor+1) + " to " + (destinationFloor + 1) + " for " + duration + "ms.");
        return duration;
    }
    
    // Getter methods if needed

    public int getStartingFloor() {
        return startingFloor;
    }

    public void setStartingFloor(int startingFloor) {
        this.startingFloor = startingFloor;
    }

    public int getDirection(){
        if(startingFloor < destinationFloor)
            return 1;
        else
            return -1;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

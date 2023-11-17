import java.util.*;

class Elevator {
    private int currentFloor;

    private int numFloors;
    private int capacity;
    private int direction; // 1 for up, -1 for down, 0 for idle
    private List<Passenger> mListPassengers;
    private String structure;

    private final int moveDuration = 1;
    
    public Elevator(int numFloors, String structure, int capacity) {
        this.numFloors = numFloors;
        this.structure = structure;
        this.capacity = capacity;
        this.currentFloor = 1; // Assuming ground floor is 1
        this.direction = 0;
        if(this.structure.equals("linked"))
            this.mListPassengers = new LinkedList<>();
        else
            this.mListPassengers = new ArrayList<>();
    }

    public int moveTo(int destFloor, int remainMove){
        // move the elevator up or down based on 'direction'
        if(currentFloor > destFloor)
            this.direction = -1;
        else if(currentFloor < destFloor)
            this.direction = 1;
        else
            this.direction = 0;
        int nstep = Math.abs(this.currentFloor - destFloor);
        if(nstep > remainMove)
            nstep = remainMove;
        currentFloor += nstep * direction;
        if(currentFloor == 0)
            direction = 1;
        if(currentFloor == numFloors-1)
            direction = -1;
        try {
            Thread.sleep(nstep*moveDuration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return nstep;
    }

    public void moveStep() {
        // move the elevator up or down based on 'direction'
        // if top or bottom is reached, change direction
        try {
            Thread.sleep(moveDuration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        currentFloor += direction;
        if(currentFloor == 0)
            direction = 1;
        if(currentFloor == numFloors-1)
            direction = -1;

    }

    public List<Passenger> loadPassenger(List<Passenger> waitPassengers) {
        // add passenger to the elevator if capacity allows
        List<Passenger> loadPassengers;
        if(this.structure.equals("linked"))
            loadPassengers = new LinkedList<>();
        else
            loadPassengers = new ArrayList<>();
        for(Passenger p: waitPassengers){
            if(isFull())
                break;
            if(p.getDirection() == direction || direction == 0){
                direction = p.getDirection();
                loadPassengers.add(p);
                mListPassengers.add(p);
            }
        }
        return loadPassengers;
    }

    public List<Passenger> unloadPassenger() {
        // remove passenger from the elevator

        List<Passenger> arrivalPassengers;
        if(this.structure.equals("linked"))
            arrivalPassengers = new LinkedList<>();
        else
            arrivalPassengers = new ArrayList<>();

        for(Passenger p: mListPassengers){
            if(p.getDestinationFloor() == this.currentFloor){
                arrivalPassengers.add(p);
            }
        }
        for(Passenger p : arrivalPassengers)
            mListPassengers.remove(p);
        if(isEmpty())
            this.direction = 0;
        return arrivalPassengers;
    }

    public boolean isFull() {
        // return true if elevator capacity is reached
        return mListPassengers.size() >= capacity;
    }
    public boolean isEmpty() {
        // return true if elevator capacity is reached
        return mListPassengers.size() == 0;
    }

    public int getCurrentFloor(){
        return this.currentFloor;
    }

    public int getDirection(){
        return this.direction;
    }
}

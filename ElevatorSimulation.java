// NAME: Colin Pham         Date: 11/18/23
import java.util.*;
import java.io.*;

class ElevatorSimulation{
	
	static int tick;    // For duration
	
	
	//DEFAULT PARAMETERS
	static String structures = "linked";
	static int floors = 32;
	static double passengers = 0.03;
	static int elevators = 1;
	static int elevatorCapacity = 10;
	static int duration = 500;
	
	//To track when the simulation will end
	static int currTick = 0;
	
	public static void main(String[] args) throws Exception {

		// Reading the file
		try {
			readFile(args[0]);
		} catch (ArrayIndexOutOfBoundsException AIOOBE) {
			System.out.println("DEFAULT PARAMETERS ENABLED");
		}
		
		if (structures.equals("array")) {
			ArrayList<Floor> totFloorsA = new ArrayList<Floor>(floors);
			ArrayList<Elevator> totElevatorsA = new ArrayList<Elevator>();
			runSimulation(totFloorsA, totElevatorsA, structures);
		} else {
			List<Floor> totFloorsL = new LinkedList<Floor>();
			List<Elevator> totElevatorsL = new LinkedList<Elevator>();
			runSimulation(totFloorsL, totElevatorsL, structures);
		}
		
	}
	
	
	// TWO OVERLOADED METHODS
	// 1 For LinkedLists
	// Another for ArrayLists
	public static void runSimulation(ArrayList<Floor> totFloors, ArrayList<Elevator> totElevators, String structure) {
		long start = System.currentTimeMillis();
		
		// Adds all floor into list based on settings
		for (int i = 1; i < floors + 1; i++) {
			totFloors.add(new Floor(i, structure));
		}
		
		
		// Adds all elevators into list based on settings
		for (int i = 1; i < elevators + 1; i++) {
			totElevators.add(new Elevator(i, structure));
		}
		
		while (currTick < duration) {
			currTick++;
			
			// Handles generating passengers on every floor
			for (int i = 0; i < floors; i++) {
				totFloors.get(i).generatePassenger(passengers, floors, structure);
			}
			
			// Handles Elevator Travel, Passenger Management
			for (int i = 0; i < elevators; i++) {
				totFloors.get(i).load(totElevators, totFloors, elevatorCapacity, structure);
			}
		}
		long end = System.currentTimeMillis();
		
	}
	
	
	// ArrayList implementation does the exact same but with ArrayLists being passed through
	
	public static void runSimulation(List<Floor> totFloors, List<Elevator> totElevators, String structure) {
		long start = System.currentTimeMillis();
		for (int i = 1; i < floors + 1; i++) {
			totFloors.add(new Floor(i, structure));
		}
		
		for (int i = 1; i < elevators + 1; i++) {
			totElevators.add(new Elevator(i, structure));
		}
		
		while (currTick < duration) {
			currTick++;
			
			for (int i = 0; i < floors; i++) {
				totFloors.get(i).generatePassenger(passengers, floors, structure);
			}
			
			for (int i = 0; i < elevators; i++) {
				totFloors.get(i).load(totElevators, totFloors, elevatorCapacity, structure);
			}
		}
		long end = System.currentTimeMillis();
		
		Passenger p = new Passenger(currTick, currTick);
		long totTimeP = p.calcTotTime();
		int totNumP = p.calcTotPassed();
		System.out.println("Total length of time of passenger travel: " + ((int)totTimeP/totNumP) + " milliseconds.");
		System.out.println("Longest time: " + p.max() + " milliseconds.");
		System.out.println("Shortest time: " + p.min() + " milliseconds.");
	}
	
	private static void readFile(String fileName) throws Exception{
		// Try and catch in case the filename is WRONG
		try {
			
			FileReader reader = new FileReader(fileName);
			Properties p = new Properties();
			p.load(reader);
			
			// Checks: Is this property set in the file? Is the property numeric? Is the property a legal input?
			if (p.getProperty("floors") != null){
				if ((isNumericI(p.getProperty("floors")) && (Double.parseDouble(p.getProperty("floors")) >= 2))) {
					floors = Integer.parseInt(p.getProperty("floors"));
				} else {
					System.err.println("Floors not valid. Default (32) will be used");
				}
			}
			
			if (p.getProperty("passengers") != null){
				if ((isNumericD(p.getProperty("passengers")) && (Double.parseDouble(p.getProperty("passengers")) > 0) && (Double.parseDouble(p.getProperty("passengers")) < 1.0))) {
					passengers = Double.parseDouble(p.getProperty("passengers"));
				} else {
					System.err.println("Passengers not valid. Default (0.03) will be used");
				}
			}
			
			if (p.getProperty("elevators") != null){
				if ((isNumericI(p.getProperty("elevators")) && (Double.parseDouble(p.getProperty("elevators")) >= 1))) {
					elevators = Integer.parseInt(p.getProperty("elevators"));
				} else {
					System.err.println("Elevators not valid. Default (1) will be used");
				}
			} 
			
			if (p.getProperty("elevatorCapacity") != null){	
				if ((isNumericI(p.getProperty("elevatorCapacity")) && (Double.parseDouble(p.getProperty("elevatorCapacity")) >= 1))) {
					elevatorCapacity = Integer.parseInt(p.getProperty("elevatorCapacity"));
				} else {
					System.err.println("Capacity not valid. Default (10) will be used");
				}
			} 
			
			if (p.getProperty("duration") != null){
				if ((isNumericI(p.getProperty("duration")) && (Double.parseDouble(p.getProperty("duration")) >= 1))) {
					duration = Integer.parseInt(p.getProperty("duration"));
				} else {
					System.err.println("Duration not valid. Default (500) will be used");
				}
			} 
			
			if (p.getProperty("structures") != null) {
				if ((p.getProperty("structures").equals("linked")) || (p.getProperty("structures").equals("array"))) {
					structures = p.getProperty("structures");
				} else {
					System.err.println("Structure not valid. Default (linked) will be used");
				}
			} 
			
			
// Not needed by the instructions but it's just to make sure the settings are right
			System.out.println("structures: " + structures);
			System.out.println("floors: " + floors);
			System.out.println("passengers: " + passengers + " or " + (passengers * 100) + "%");
			System.out.println("elevators: " + elevators);
			System.out.println("elevatorCapacity: " + elevatorCapacity);
			System.out.println("duration: " + duration);
			
		} catch (FileNotFoundException FNFE){
			System.err.println("NO SUCH FILE. DEFAULT PARAMETERS WILL BE USED");
			return;
		}
	}
	
// To make sure the inputs are numbers
	private static boolean isNumericD(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private static boolean isNumericI(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}

class Floor{
	
	
	//RNG to determine if a passenger appears and the desired floor
	static Random passengerGen = new Random();
	
	static Random desiredFloor = new Random();
	
	
	// L = LinkedList
	// A = ArrayList
	Queue<Passenger> upCrowdL;
	Queue<Passenger> downCrowdL;
	List<Passenger> upCrowdA;
	List<Passenger> downCrowdA;
	
	protected int floorNumber;
	
	// Creating a floor means to have a group that wants to go up and a group that
	// wants to go down. Floor number is associated with it.
	public Floor(int floorNumber, String structure){
		this.floorNumber = floorNumber;
		if (structure.equals("linked")) {
			upCrowdL = new LinkedList<Passenger>();
			downCrowdL = new LinkedList<Passenger>();
		} else {
			upCrowdA = new ArrayList<Passenger>();
			downCrowdA = new ArrayList<Passenger>();
		}
		
	}
	
	public int getNum() {
		return(this.floorNumber);
	}
	
	
	// Handles generating passengers, their desired floors. and whether or not they want to go up or down
	void generatePassenger(double prob, int maxFloors, String structure) {
		
		if (passengerGen.nextDouble(100)+ 1 <= prob*100) {
			int destination = desiredFloor.nextInt(1, maxFloors + 1);
			
			if (floorNumber == maxFloors) {
				destination = desiredFloor.nextInt(1, maxFloors);
			} else if (floorNumber == 1) {
				destination = desiredFloor.nextInt(2, maxFloors + 1);
			} else if (floorNumber == destination) {
				int toAdd = desiredFloor.nextInt(3) - 1;
				while (toAdd == 0) {
					toAdd = desiredFloor.nextInt(3) - 1;
				}
				destination += toAdd;
			}
			
		// EDGE CASES
		// (Going to floor 0 and 33 (default max 32) does not make sense)
			if ((destination == 0) && (floorNumber != 1)){
				destination = 1;
			} else if ((destination == maxFloors + 1) && (floorNumber != maxFloors)){
				destination = maxFloors;
			} else if ((destination == 0) && (floorNumber == 1)){
				destination = 2;
			} else if ((destination == maxFloors + 1) && (floorNumber == maxFloors)){
				destination = maxFloors - 2;
			}
			
			
			if (structure.equals("array")) {
				if (destination < this.floorNumber) {
					downCrowdA.add(new Passenger(this.floorNumber, destination));
				} else if (destination > this.floorNumber) {
					upCrowdA.add(new Passenger(this.floorNumber, destination));
				}
			} else {
				if (destination < this.floorNumber) {
					downCrowdL.add(new Passenger(this.floorNumber, destination));
				} else if (destination > this.floorNumber) {
					upCrowdL.add(new Passenger(this.floorNumber, destination));
				}
			}
			
		}
		
	}
	
	
// Elevator's travel method handles everything an elevator should do per tick
	void load(List<Elevator> allEl, List<Floor> allFl, int capacity, String structure){
		if (structure.equals("linked")) {
			for (int i = 0; i < allEl.size(); i++) {
				allEl.get(i).travelL(allFl, capacity);
			}
		} else {
			for (int i = 0; i < allEl.size(); i++) {
				allEl.get(i).travelA(allFl, capacity);
			}
		}

		
	}
	
	
}

class Passenger{
	
	protected int startFloor;
	protected int endFloor;
	protected long startTime;
	protected long endTime;
	protected boolean desireUp;
	protected static long total = 0;
	protected static int completed = 0;
	protected static int min = -1;
	protected static int max = -1;
	
// Every passenger has their own start and end times, start and end floors, and direction (desireUp)
	public Passenger(int start, int end) {
		this.startTime = System.currentTimeMillis();
		this.startFloor = start;
		this.endFloor = end;
		if (this.endFloor > this.startFloor) {
			desireUp = true;
		} else {
			desireUp = false;
		}
	}
	
	public int getStart() {
		return(startFloor);
	}
	
	public int getEnd() {
		return(endFloor);
	}
	
	// Gets the end time once the passenger leaves the elevator
	// The total amount of time for all passengers is added
	// The min and max are always kept updated
	public void outTime() {
		this.endTime = System.currentTimeMillis();
		total += (endTime - startTime);
		
		if (max == -1) {
			max = (int) (endTime - startTime);
		} else if ((endTime - startTime) > max) {
			max = (int) (endTime - startTime);
		}
		
		if (min == -1) {
			min = (int) (endTime - startTime);
		} else if ((endTime - startTime) < min) {
			min = (int) (endTime - startTime);
		}
		
		// To calculate average
		completed += 1;
	}
	
	public long calcTotTime() {
		return(total);
	}
	
	public int calcTotPassed() {
		return(completed);
	}
	
	public int min() {
		return(min);
	}
	
	public int max() {
		return(max);
	}

}

class Elevator{
	
	int elevatorNumber;
	PriorityQueue<Integer> stopUp = new PriorityQueue<>();
	//minHeap
	PriorityQueue<Integer> stopDown = new PriorityQueue<>(); 
	//maxHeap done through storing endFloor's NEGATIVE number (5 = -5)
	int currFloor = 1;
	boolean up = true;
	List<Passenger> passengersInA;
	List<Passenger> passengersInL;
	
	// Making an elevator means that it has it's number, current floor, passengers, and up value
	public Elevator(int num, String structure) {
		this.elevatorNumber = num;
		if (structure.equals("array")) {
			passengersInA = new ArrayList<>();
		} else {
			passengersInL = new LinkedList<>();
		}
	}
	
	// (Notes on the ArrayList implementation will be the same as the LinkedList implementation)
	// Order, for every movement: travel() loadUnload()(E) requestStop()   loadUnload()  requestStop()
	// -----------------------------------------------------------------------------------
	//     Linked List implementation
	private void loadUnloadL(List<Floor> allFl, int capacity) {
		
		// If the elevator is going up and if there's a passenger who arrived at the floor, remove them and get end time
		// Same for else
		if (up == true) {
			if ((stopUp.peek() != null) && (stopUp.peek() == currFloor)) {
				Iterator<Passenger> itr = passengersInL.iterator();
				while (itr.hasNext()) {
					Passenger p = itr.next();
					if (p.getEnd() == currFloor) {
						p.outTime();
						itr.remove();
						if ((stopUp.peek() != null) && (stopUp.peek() == currFloor)) {
							stopUp.remove();
						}
					}
				}
			}
			


		} else {
			if ((stopDown.peek() != null) && (stopDown.peek() + (stopDown.peek() * -2)  == currFloor)) {
				Iterator<Passenger> itr = passengersInL.iterator();
				while (itr.hasNext()) {
					Passenger p = itr.next();
					if (p.getEnd() == currFloor) {
						p.outTime();
						itr.remove();
						if ((stopDown.peek() != null) && (stopDown.peek() + (stopDown.peek() * -2) == currFloor)) {
							stopDown.remove();
						}
					}
				}
			}
			

		}
		
		// Anyone who is on the floor waiting to go in may get in
		requestStopL(allFl, capacity);
	}
	
	private void loadUnloadL(List<Floor> allFl) {
		
		// Before my code didn't allow for people to get out if their floors are 1 or max amount
		// This overloaded method is just here to handle that
		
		Iterator<Passenger> itr = passengersInL.iterator();
		if (currFloor == allFl.size()) {
			while (itr.hasNext()) {
				Passenger p = itr.next();
				if (p.getEnd() == allFl.size()) {
					p.outTime();
					itr.remove();
					if ((stopUp.peek() != null) && (stopUp.peek() == currFloor)) {
						stopUp.remove();
					}
				}
			}
		} else if (currFloor == 1) {
			while (itr.hasNext()) {
				Passenger p = itr.next();
				if (p.getEnd() == 1) {
					p.outTime();
					itr.remove();
					if ((stopDown.peek() != null) && (stopDown.peek() + (stopDown.peek() * -2) == currFloor)) {
						stopDown.remove();
					}
				}
			}
		}
	}
	
	void travelL(List<Floor> allFl, int capacity) {
		int moveLimit = 5;
		while((moveLimit > 0) && (currFloor <= allFl.size())) {
			// Any passengers going the same direction as the elevator may get in and request stop
			requestStopL(allFl, capacity);
			// Any passengers in elevators who reached the floor may get off
			loadUnloadL(allFl, capacity);
			if (up == true) {
				currFloor++;
				// Prevent edge cases
				if (currFloor >= allFl.size() + 1) {
					currFloor = allFl.size();
					up = false;
				}
			} else {
				currFloor--;
				if (currFloor <= 0) {
					currFloor = 1;
					up = true;
				}
			}
			moveLimit--;
		}
	}
	
	private void requestStopL(List<Floor> allFl, int capacity) {
		
		// Edge cases handled in this mini loadUnload method
		loadUnloadL(allFl);
		//Prevents the MIN and MAX floor from being reached and passengers are still on
		
		
		// IF THE ELEVATOR IS IN THE MIDDLE WITH NO PASSENGERS, the direction will depend on what floor
		// is the closest with passengers
		if ((passengersInL.size() == 0) && ((currFloor != 1) && (currFloor != allFl.size()))) {
			int upCount = 0;
			int downCount = 0;
			for (int i = 1; i + currFloor < allFl.size(); i++) {
				if ((currFloor - 1 + i < allFl.size()) && (allFl.get(currFloor - 1 + i).upCrowdL.isEmpty())) {
					upCount++;
				} else {
					break;
				}
			}
			
			for (int i = 1; currFloor - i < allFl.size(); i++) {
				if ((currFloor - 1 - i > 0) && (allFl.get(currFloor - 1 - i).downCrowdL.isEmpty())) {
					downCount++;
				} else {
					break;
				}
			}
			
			if (upCount > downCount) {
				up = false;
			} else {
				up = true;
			}
	// handles if the elevator is in the abyss (floors 0 or one above the max)
		} else if (currFloor <= 1) {
			currFloor = 1;
			up = true;
		} else if (currFloor >= allFl.size()){
			currFloor = allFl.size();
			up = false;
		}
		
		// Adds passengers and removes them from the floor's waiting room
		if (up == true) {
			
			Iterator<Passenger> itr = allFl.get(currFloor - 1).upCrowdL.iterator();
			while (itr.hasNext()){
				if (passengersInL.size() >= 10) {
					break;
				}
				Passenger p = itr.next();
				if (p.desireUp) {
					passengersInL.add(p);
					stopUp.add(p.endFloor);
					itr.remove();
				}
			}
			
			
		} else {

			Iterator<Passenger> itr = allFl.get(currFloor - 1).downCrowdL.iterator();
			while (itr.hasNext()){
				if (passengersInL.size() >= 10) {
					break;
				}
				Passenger p = itr.next();
				if (! p.desireUp) {
					passengersInL.add(p);
					stopDown.add(p.endFloor - (p.endFloor * 2));
					itr.remove();
				}
				
			}
			
		}
	}
	
	
	// -------------------------------------------------------------------------------------
	// ArrayList Implementation
	
	private void loadUnloadA(List<Floor> allFl, int capacity) {
		

		if (up == true) {
			if ((stopUp.peek() != null) && (stopUp.peek() == currFloor)) {
				Iterator<Passenger> itr = passengersInA.iterator();
				while (itr.hasNext()) {
					Passenger p = itr.next();
					if (p.getEnd() == currFloor) {
						p.outTime();
						itr.remove();
						if ((stopUp.peek() != null) && (stopUp.peek() == currFloor)) {
							stopUp.remove();
						}
					}
				}
			}
			


		} else {
			if ((stopDown.peek() != null) && (stopDown.peek() + (stopDown.peek() * -2)  == currFloor)) {
				Iterator<Passenger> itr = passengersInA.iterator();
				while (itr.hasNext()) {
					Passenger p = itr.next();
					if (p.getEnd() == currFloor) {
						p.outTime();
						itr.remove();
						if ((stopDown.peek() != null) && (stopDown.peek() + (stopDown.peek() * -2) == currFloor)) {
							stopDown.remove();
						}
					}
				}
			}
			

		}
		requestStopA(allFl, capacity);
	}
	
	private void loadUnloadA(List<Floor> allFl) {
		Iterator<Passenger> itr = passengersInA.iterator();
		if (currFloor == allFl.size()) {
			while (itr.hasNext()) {
				Passenger p = itr.next();
				if (p.getEnd() == allFl.size()) {
					p.outTime();
					itr.remove();
					if ((stopUp.peek() != null) && (stopUp.peek() == currFloor)) {
						stopUp.remove();
					}
				}
			}
		} else if (currFloor == 1) {
			while (itr.hasNext()) {
				Passenger p = itr.next();
				if (p.getEnd() == 1) {
					p.outTime();
					itr.remove();
					if ((stopDown.peek() != null) && (stopDown.peek() + (stopDown.peek() * -2) == currFloor)) {
						stopDown.remove();
					}
				}
			}
		}
	}
	
	void travelA(List<Floor> allFl, int capacity) {
		int moveLimit = 5;
		while((moveLimit > 0) && (currFloor <= allFl.size())) {
			requestStopA(allFl, capacity);
			loadUnloadA(allFl, capacity);
			if (up == true) {
				currFloor++;
				if (currFloor >= allFl.size() + 1) {
					currFloor = allFl.size();
					up = false;
				}
			} else {
				currFloor--;
				if (currFloor <= 0) {
					currFloor = 1;
					up = true;
				}
			}
			moveLimit--;
		}
	}
	
	private void requestStopA(List<Floor> allFl, int capacity) {
		
		loadUnloadA(allFl);
		
		if ((passengersInA.size() == 0) && ((currFloor != 1) && (currFloor != allFl.size()))) {
			int upCount = 0;
			int downCount = 0;
			for (int i = 1; i + currFloor < allFl.size(); i++) {
				if ((currFloor - 1 + i < allFl.size()) && (allFl.get(currFloor - 1 + i).upCrowdA.isEmpty())) {
					upCount++;
				} else {
					break;
				}
			}
			
			for (int i = 1; currFloor - i < allFl.size(); i++) {
				if ((currFloor - 1 - i > 0) && (allFl.get(currFloor - 1 - i).downCrowdA.isEmpty())) {
					downCount++;
				} else {
					break;
				}
			}
			
			if (upCount > downCount) {
				up = false;
			} else {
				up = true;
			}
		} else if (currFloor <= 1) {
			currFloor = 1;
			up = true;
		} else if (currFloor >= allFl.size()){
			currFloor = allFl.size();
			up = false;
		}
		
		if (up == true) {
			
			Iterator<Passenger> itr = allFl.get(currFloor - 1).upCrowdA.iterator();
			while (itr.hasNext()){
				if (passengersInA.size() >= 10) {
					break;
				}
				Passenger p = itr.next();
				if (p.desireUp) {
					passengersInA.add(p);
					stopUp.add(p.endFloor);
					itr.remove();
				}
			}
			
			
		} else {

			Iterator<Passenger> itr = allFl.get(currFloor - 1).downCrowdA.iterator();
			while (itr.hasNext()){
				if (passengersInA.size() >= 10) {
					break;
				}
				Passenger p = itr.next();
				if (! p.desireUp) {
					passengersInA.add(p);
					stopDown.add(p.endFloor - (p.endFloor * 2));
					itr.remove();
				}
				
			}
			
		}
	}
}
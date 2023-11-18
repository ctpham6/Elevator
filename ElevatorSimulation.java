// NAME: Colin Pham         Date: 11/16/23
import java.util.*;
import java.io.*;
// [Elevator Simulation]
// - elevator : List<Elevator>
// - floor : List<Floor>
// - tick : int
// -----------------------------
// + runSimulation()

// [Floor]
// - Queue<Passenger>
//    - has Up,Down
// - floor number
// -------------------------------
// + generatePassenter()
// + load()

// [Passenger]
// - startFloor : int
// - endFloor : int
// - startTime : int
// - endTime : int
// ------------------------------
// - passenger()

// [Elevator]
// - stopUp : Heap<int>
// - stopDown : Heap<int>
// - passenger : 
// - floor: int
// - up : bool          is the elevator going up or down?
// ------------------------------
// + loadUnload()
// + travel()

class ElevatorSimulation{
	
	static int tick;
	
	static String structures = "linked";
	static int floors = 32;
	static double passengers = 0.03;
	static int elevators = 1;
	static int elevatorCapacity = 10;
	static int duration = 500;
	
	static List<Floor> totFloors = new ArrayList<Floor>(floors);
	static List<Elevator> totElevators = new ArrayList<Elevator>();
	
	
	static int currTick = 0;
	
	public static void main(String[] args) throws Exception {

		try {
			readFile(args[0]);
		} catch (ArrayIndexOutOfBoundsException AIOOBE) {
			System.out.println("DEFAULT PARAMETERS ENABLED");
		}
		
		long start = System.currentTimeMillis();
		runSimulation();
		long end = System.currentTimeMillis();
		
		System.out.println("Finished in: " + (end - start) + " milliseconds");
	}
	
	public static void runSimulation() {

		for (int i = 1; i < floors + 1; i++) {
			totFloors.add(new Floor(i));
		}
		
		for (int i = 1; i < elevators + 1; i++) {
			totElevators.add(new Elevator(i));
		}
		
		while (currTick < duration) {
			currTick++;
			System.out.println("Tick is at: " + currTick);
			
			for (int i = 0; i < floors; i++) {
				totFloors.get(i).generatePassenger(passengers, floors);
			}
			
			for (int i = 0; i < elevators; i++) {
				totFloors.get(i).load(totElevators, totFloors, elevatorCapacity);
			}
		}
		
	}
	
	private static void readFile(String fileName) throws Exception{
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
	
	Queue<Passenger> upCrowd;
	Queue<Passenger> downCrowd;
	static Random passengerGen = new Random();
	static Random desiredFloor = new Random();
	protected int floorNumber;
	
	public Floor(int floorNumber){
		this.floorNumber = floorNumber;
		upCrowd = new LinkedList<Passenger>();
		downCrowd = new LinkedList<Passenger>();
	}
	
	public int getNum() {
		return(this.floorNumber);
	}
	
	void generatePassenger(double prob, int maxFloors) {
		
		if (passengerGen.nextDouble(100)+ 1 <= prob*100) {
			int destination = desiredFloor.nextInt(maxFloors + 1);
			
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
			
			
			if (destination < this.floorNumber) {
				downCrowd.add(new Passenger(this.floorNumber, destination));
			} else if (destination > this.floorNumber) {
				upCrowd.add(new Passenger(this.floorNumber, destination));
			} else {
				System.err.println("OWO DADDY");
			}
		}
		
	}
	
	void load(List<Elevator> allEl, List<Floor> allFl, int capacity){
		for (int i = 0; i < allEl.size(); i++) {
			allEl.get(i).travel(allFl, capacity);
		}

		
	}
	
	
}

class Passenger{
	
	protected int startFloor;
	protected int endFloor;
	protected long startTime;
	protected long endTime;
	protected boolean desireUp;
	
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
	
	public void outTime() {
		this.endTime = System.currentTimeMillis();
		System.out.println("[I waited for: " + (endTime - startTime) + " milliseconds to get to my floor!]");
	}
}

class Elevator{
	
	int elevatorNumber;
	PriorityQueue<Integer> stopUp = new PriorityQueue<>(); //minHeap
	PriorityQueue<Integer> stopDown = new PriorityQueue<>(); //maxHeap
	List<Passenger> passengersIn = new ArrayList<>();
	int currFloor = 1;
	boolean up = true;
	
	public Elevator(int num) {
		this.elevatorNumber = num;
	}
	
	
	private void loadUnload(List<Floor> allFl, int capacity) {
		if (up == true) {
			if ((stopUp.peek() != null) && (stopUp.peek() == currFloor)) {
				for (Passenger p : passengersIn) {
					if (p.endFloor == currFloor) {
						p.outTime();
						passengersIn.remove(p);
					}
				}
				while (stopUp.contains(allFl.get(currFloor - 1).upCrowd.element().endFloor)) {
					stopUp.remove();
				}
			}

		} else {
			if ((stopDown.peek() != null) && (stopDown.peek() + (stopDown.peek() * 2)  == currFloor)) {
				for (Passenger p : passengersIn) {
					if (p.endFloor == currFloor) {
						p.outTime();
						passengersIn.remove(p);
					}
				}
				while (stopDown.contains((allFl.get(currFloor - 1).upCrowd.element().endFloor) - (((allFl.get(currFloor - 1).upCrowd.element().endFloor) * 2)))) {
					stopDown.remove();
				}
			}
		}
		requestStop(allFl, capacity);
	}
	
	void travel(List<Floor> allFl, int capacity) {
		int moveLimit = 5;
		while((moveLimit > 0) && (currFloor < allFl.size() + 1)) {
			requestStop(allFl, capacity);
			loadUnload(allFl, capacity);
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
	
	private void requestStop(List<Floor> allFl, int capacity) {
		
		if ((passengersIn.size() == 0) && ((currFloor != 1) && (currFloor != allFl.size()))) {
			int upCount = 0;
			int downCount = 0;
			for (int i = 1; i + currFloor < allFl.size(); i++) {
				if ((currFloor - 1 + i < allFl.size()) && (allFl.get(currFloor - 1 + i).upCrowd.isEmpty())) {
					upCount++;
				} else {
					break;
				}
			}
			
			for (int i = 1; currFloor - i < allFl.size(); i++) {
				if ((currFloor - 1 - i > 0) && (allFl.get(currFloor - 1 - i).downCrowd.isEmpty())) {
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
		} else if (currFloor == 1) {
			up = true;
		} else if (currFloor == allFl.size()){
			up = false;
		}
		
		if (up == true) {
			
			for (Passenger p : allFl.get(currFloor-1).upCrowd) {
				if (passengersIn.size() >= 10) {
					break;
				}
				passengersIn.add(p);
				stopUp.add(p.endFloor);
				allFl.get(currFloor-1).upCrowd.remove(p);
			}
			
			
		} else {

			for (Passenger p : allFl.get(currFloor-1).downCrowd) {
				
				System.out.println(p.endFloor);
				
				if (passengersIn.size() >= 10) {
					break;
				}
				
				passengersIn.add(p);
				stopDown.add(p.endFloor - (p.endFloor * 2));
				allFl.get(currFloor-1).upCrowd.remove(p);
				
			}
			
		}
	}
}
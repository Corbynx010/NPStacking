import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class NPStack {
	static String filepath;
	static int generations;
	static int currentGeneration = 0;
	static int populationSize;
	static ArrayList<Box> values = new ArrayList<Box>();
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Please use \"NPStack filepath generationCount populationPerGeneration\"");
		}
		else {
			filepath = args[0];
			generations = Integer.parseInt(args[1]);
			populationSize = Integer.parseInt(args[2]);
		}
		File inputFile = new File(filepath);
		Scanner scanner;
		try {
			scanner = new Scanner(inputFile);
		while(scanner.hasNext()) {
			try {
				int a,b,c;
				String stringDimentions = scanner.nextLine();
				String[] dimentions = stringDimentions.split(" ");
				a = Integer.parseInt(dimentions[0]);
				b = Integer.parseInt(dimentions[1]);
				c = Integer.parseInt(dimentions[2]);
				values.add(new Box(a,b,c,null));
			}
			catch(Exception e) { }
		}
		scanner.close();
		} catch (FileNotFoundException e1) {}		
		Tower[] population = initialPopulation();
		for(int i = 0; i < generations; i++) {
			Tower[] parents = getBestParents(population);	//0 is best
			currentGeneration++;
			population = newGeneration(parents);
		}
		Tower[] parents = getBestParents(population);	//0 is best
		parents[0].writeTower();
	}
	
	public static Tower[] getBestParents(Tower[] population) {	// bubble sort the best two parents, parents[0] is best
		Tower[] parents = new Tower[2];
		for(Tower t : population) {
			if(parents[0] == null) {
				parents[0] = t;
			}
			else if(parents[1] == null || parents[1].height < t.height) {
				if(parents[0].height < t.height) {
					parents[1] = parents[0];
					parents[0] = t;
				}
				else {
					parents[1] = t;
				}
			}
		}
		return parents;
	}
	
	public static Tower[] newGeneration(Tower[] parents) {
		Tower[] nextGeneration = new Tower[populationSize];
		nextGeneration[0] = parents[0]; 	//carry best over from previous generation (Elitism);
		for(int i = 1; i < populationSize; i++) {
			nextGeneration[i] = breedParents(parents);
		}
		return nextGeneration;
	}
	
	public static Tower breedParents(Tower[] parents) {
		Random r = new Random();
		ArrayList<Box> parent0 = new ArrayList<Box>(parents[0].boxes);
		ArrayList<Box> parent1 = new ArrayList<Box>(parents[1].boxes);
		Tower child = new Tower();
		while(parent0.size() > 0 || parent1.size() > 0) {
			int randParent = r.nextInt(2);
			double mutationRate = (1.05-(currentGeneration/generations))*4;
			int randMutate = r.nextInt((int)Math.round(100/mutationRate)); 
			if(randMutate == 1) {	//mutate with decreasing chance per generation 4.2%-0.2% chance
				int mutation = r.nextInt(values.size());
				if(!child.boxInTower(values.get(mutation))) {
					child.addBox(values.get(mutation));
				}
			}
			else {
				if(parent0.size() == 0) { randParent = 1; }
				else if(parent1.size() == 0) { randParent = 0; }
				if(randParent == 0) {
					int randBox = r.nextInt(parent0.size());
					if(!child.boxInTower(parent0.get(randBox))) {
						child.addBox(parent0.get(randBox));
					}
					parent0.remove(randBox);
				}
				else if(randParent == 1) {
					int randBox = r.nextInt(parent1.size());
					if(!child.boxInTower(parent1.get(randBox))) {
						child.addBox(parent1.get(randBox));
					}
					parent1.remove(randBox);
				}
			}
		}
		return child;		
	}
	
	public static Tower[] initialPopulation() {
		Tower[] population = new Tower[populationSize];
		for(int i = 0; i < populationSize; i++) {
			Random r = new Random();
			ArrayList<Box> currentValues = new ArrayList<Box>(values);
			Tower t = new Tower();
			while(currentValues.size() > 0) {
				int result = r.nextInt(currentValues.size());
				Box b = currentValues.get(result);
				currentValues.remove(result);
				t.addBox(b);
			}
			population[i] = t;
		}
		return population;
	}
	
	public static class Box{
		int x,y,z;	
		String uniqueID;
		public Box(int a, int b, int c, String id) {
			x=a;
			y=b;
			z=c;
			if(id != null) {
				uniqueID = id;
			}
			else {
				uniqueID = UUID.randomUUID().toString();
			}
		}
		
		public static Box compare(Box newBox, Box belowBox, Box aboveBox) throws BoxNotFitException {
			if(belowBox == null) {	//if the box is on the ground it fits
				Box onGround = canFitBelow(newBox, aboveBox); 
				return onGround;
				}	
			else if(aboveBox == null) {		//if box can fit on top try that
				Box onTop = canFitAbove(newBox, belowBox);
				return onTop;
				}

			Box inBetween = canFitBetween(newBox, belowBox, aboveBox);
			return inBetween;
		}
		
		public static Box canFitBetween(Box newBox, Box belowBox, Box aboveBox) throws BoxNotFitException {
			int height = 0;
			Box finalBox = newBox;
			
			if(newBox.x < belowBox.x && newBox.x > aboveBox.x) {
				if(newBox.y < belowBox.y && newBox.y > aboveBox.y) {
					int newHeight = newBox.z;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.x, newBox.y, newBox.z, newBox.uniqueID);
					}
				}
				if(newBox.z < belowBox.y && newBox.z > aboveBox.y) {
					int newHeight = newBox.y;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.x, newBox.z, newBox.y, newBox.uniqueID);
					}
				}
			}
			if(newBox.y < belowBox.x && newBox.y > aboveBox.x) {
				if(newBox.x < belowBox.y && newBox.x > aboveBox.y) {
					int newHeight = newBox.z;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.y, newBox.x, newBox.z, newBox.uniqueID);
					}
				}
				if(newBox.z < belowBox.y && newBox.z > aboveBox.y) {
					int newHeight = newBox.x;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.y, newBox.z, newBox.x, newBox.uniqueID);
					}
				}
			}
			if(newBox.z < belowBox.x && newBox.z > aboveBox.x) {
				if(newBox.x < belowBox.y && newBox.x > aboveBox.y) {
					int newHeight = newBox.y;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.z, newBox.x, newBox.y, newBox.uniqueID);
					}
				}
				if(newBox.y < belowBox.y && newBox.y > aboveBox.y) {
					int newHeight = newBox.x;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.z, newBox.y, newBox.x, newBox.uniqueID);
					}
				}
			}
			if(height > 0) {
				return finalBox;
			}
			throw new BoxNotFitException();
		}
		
		public static Box canFitBelow(Box newBox, Box aboveBox) throws BoxNotFitException {	//a is box that can be rotated, b is box it is trying to go underneath
			int height = 0;
			Box finalBox = newBox;
			if(newBox.x > aboveBox.x) {
				if(newBox.y > aboveBox.y) {
					int newHeight = newBox.z;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.x, newBox.y, newBox.z, newBox.uniqueID);
					}
				}
				if(newBox.z > aboveBox.y) {
					int newHeight = newBox.y;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.x, newBox.z, newBox.y, newBox.uniqueID);
					}
				}
			}
			if(newBox.y > aboveBox.x) {
				if(newBox.x > aboveBox.y) {
					int newHeight = newBox.z;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.y, newBox.x, newBox.z, newBox.uniqueID);
					}
				}
				if(newBox.z > aboveBox.y) {
					int newHeight = newBox.x;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.y, newBox.z, newBox.x, newBox.uniqueID);
					}
				}
			}
			if(newBox.z > aboveBox.x) {
				if(newBox.x > aboveBox.y) {
					int newHeight = newBox.y;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.z, newBox.x, newBox.y, newBox.uniqueID);
					}
				}
				if(newBox.y > aboveBox.y) {
					int newHeight = newBox.x;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.z, newBox.y, newBox.x, newBox.uniqueID);
					}
				}
			}
			if(height > 0) {
				return finalBox;
			}
			throw new BoxNotFitException();
		}
		
		public static Box canFitAbove(Box newBox, Box belowBox) throws BoxNotFitException {	//a is box that can be rotated, b is box it is trying to go above
			int height = 0;
			Box finalBox = newBox;
			if(newBox.x < belowBox.x) {
				if(newBox.y < belowBox.y) {
					int newHeight = newBox.z;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.x, newBox.y, newBox.z, newBox.uniqueID);
					}
				}
				if(newBox.z < belowBox.y) {
					int newHeight = newBox.y;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.x, newBox.z, newBox.y, newBox.uniqueID);
					}
				}
			}
			if(newBox.y < belowBox.x) {
				if(newBox.x < belowBox.y) {
					int newHeight = newBox.z;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.y, newBox.x, newBox.z, newBox.uniqueID);
					}
				}
				if(newBox.z < belowBox.y) {
					int newHeight = newBox.x;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.y, newBox.z, newBox.x, newBox.uniqueID);
					}
				}
			}
			if(newBox.z < belowBox.x) {
				if(newBox.x < belowBox.y) {
					int newHeight = newBox.y;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.z, newBox.x, newBox.y, newBox.uniqueID);
					}
				}
				if(newBox.y < belowBox.y) {
					int newHeight = newBox.x;
					if(newHeight > height) {
						height = newHeight;
						finalBox = new Box(newBox.z, newBox.y, newBox.x, newBox.uniqueID);
					}
				}
			}
			if(height > 0) {
				return finalBox;
			}
			throw new BoxNotFitException();
		}
		
		public String boxString() {
			return x + "\t" + y + "\t" + z;
		}
	}
	
	public static class BoxNotFitException extends Throwable{
		private static final long serialVersionUID = -5585051265913148449L;
	}
	
	public static class Tower{
		ArrayList<Box> boxes = new ArrayList<Box>();	//0 lowest box
		int height = 0;
		
		public void addBox(Box b) {
			if(boxes.size() == 0) {
			boxes.add(0, b);
			height += b.z;
			return;
			}
			for(int i = 0; i <= boxes.size(); i++) {
				try{				
					Box belowBox = null;
					Box aboveBox = null;
					if(i > 0) { belowBox = boxes.get(i-1);}
					if(i != boxes.size()) { aboveBox = boxes.get(i);}					
					Box box = Box.compare(b, belowBox, aboveBox);
					boxes.add(i, box);
					height += box.z;
					return;
				}
				catch(BoxNotFitException bnfe) {}
			}
		}
		
		public boolean boxInTower(Box b) {
			for(Box a : boxes) {
				if(a.uniqueID.equals(b.uniqueID)) {
					return true;
				}
			}
			return false;
		}
		
		public void writeTower() {
			System.out.println("Height: " + height + "\n");
			System.out.println("x\ty\tz\n");
			for(int i = boxes.size(); i > 0; i--) {
				System.out.println(boxes.get(i-1).boxString());
			}
		}
	}
}
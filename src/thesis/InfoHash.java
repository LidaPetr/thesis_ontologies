package thesis;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class InfoHash {

	public HashMap<String, HashMap<String, String>> Individuals;
	List<String> attr;

	// HashC : HashMap for classes
	public HashMap<String, HashMap<String, String>> HashC = new HashMap<>();

	// HashOP : HashMap for the object properties
	public HashMap<String, HashMap<String, HashMap<String, String>>> HashOP = new HashMap<>();


	// Function that creates a mapping between individuals and classes
	public void insertC(String c, String i) {
		if (HashC.containsKey(i)) {

		}
		else {
			HashC.put(i, new HashMap<String, String>());
		}
		HashC.get(i).put(c, "true");
	}


	public void deleteC(String i) {
		Set<String> set = HashC.keySet();
		for (String ind : set) {

			if (HashC.get(ind).containsKey(i)){
				HashC.get(ind).remove(i);
			}
		}		
	}



	public void deleteOP(String i) {
		Set<String> set = HashOP.keySet();
		for (String ind : set) {

			if (HashOP.get(ind).containsKey(i)){
				HashOP.get(ind).remove(i);
			}
		}
	}


	// Function that creates a mapping between object properties and the associated individuals
	public void insertOP(String op, String i1, String i2) {
		if (HashOP.containsKey(i1)) {
			if (HashOP.get(i1).containsKey(op)) {

			}
			else {
				HashOP.get(i1).put(op, new HashMap<String, String>());
			}
		}
		else {
			HashOP.put(i1, new HashMap<String, HashMap<String, String>>());
			HashOP.get(i1).put(op, new HashMap<String, String>());
		}
		HashOP.get(i1).get(op).put(i2, "true");
		if(!i2.equals("isAnonymous!")){
			op = op.substring(0, op.lastIndexOf('(')) + "(Y, X)";
			if (HashOP.containsKey(i2)) {
				if (HashOP.get(i2).containsKey(op)) {

				}
				else {
					HashOP.get(i2).put(op, new HashMap<String, String>());
				}
			}
			else {
				HashOP.put(i2, new HashMap<String, HashMap<String, String>>());
				HashOP.get(i2).put(op, new HashMap<String, String>());
			}
			HashOP.get(i2).get(op).put(i1, "true");
		}
	}


	// Function that creates a list with all individuals 
	public ArrayList<String> getIndividuals() {
		// For each HashMap create a Set
		Set<String> set1 = HashC.keySet();
		Set<String> set2 = HashOP.keySet();

		ArrayList<String> list = new ArrayList<String>();

		// Insert the sets in the list
		list.addAll(set1);

		// Without duplicates
		for (String e : set2) {
			if (!(list.contains(e))) {
				list.add(e);
			}
		}

		// Return a sorted list
		Collections.sort(list);		
		return list;
	}

	// Function that creates a list with all classes
	public ArrayList<String> getClasses() {
		// For C HashMap create a Set
		Set<String> set = HashC.keySet();

		ArrayList<String> list = new ArrayList<String>();

		// Insert the values/classes for each key/individual in the list
		for (String ind : set) {
			for (String cl : HashC.get(ind).keySet()) {
				// Without duplicates
				if (!(list.contains(cl))) {
					list.add(cl);
				}
			}
		}

		// Return a sorted list
		Collections.sort(list);

		return list;
	}



	// Function that returns the amount of individuals that belong to the specified class
	public int getClassIndividuals(String cl) {
		// Initialize the counter
		int count = 0;

		// For each individual of the C HashMap
		for (String ind : HashC.keySet()) {
			// If individual belongs to the class increase the counter
			if (belongs2class(cl, ind)) {
				count++;
			}
		}
		return count;
	}

	// Function that creates a list with all object properties
	public ArrayList<String> getObjectProperties() {
		// For OP HashMap create a Set
		Set<String> set = HashOP.keySet();

		ArrayList<String> list = new ArrayList<String>();

		// Insert the values/object properties for each key/individual in the list
		for (String ind : set) {
			for (String op : HashOP.get(ind).keySet()) {
				// Without duplicates
				if (!(list.contains(op))) {
					list.add(op);
				}
			}
		}

		// Return a sorted list
		Collections.sort(list);		
		return list;
	}

	// Function that returns the amount of individuals that take part in a relation of object property
	public int getObjectPropertyIndividuals(String op) {
		// Initialize the counter
		int count = 0;

		// For each individual of the OP HashMap
		for (String ind : HashOP.keySet()) {
			// If individual takes part in the relation increase the counter
			if (belongs2objectProperty(op, ind)) {
				count++;
			}
		}
		return count;
	}


	// Function that indicates whether an individual belongs to a class
	public boolean belongs2class(String cl, String i) {
		return HashC.containsKey(i) && HashC.get(i).containsKey(cl);
	}


	// Function that indicates whether an individual takes part in a relation of object property
	public boolean belongs2objectProperty(String op, String i) {
		return HashOP.containsKey(i) && HashOP.get(i).containsKey(op);
	}

	// In this case we return the other individual that takes part in the relation
	public String belongs2objectProperty2(String op, String i) {
		if (HashOP.containsKey(i) && HashOP.get(i).containsKey(op)) {
			return HashOP.get(i).get(op).keySet().toString();
		}
		return null;
	}

	public static void main(String[] args) {

	}
}
package thesis;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class FileEdit {


	public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap)
	{
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>()
		{
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2)
			{
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}


	
	public static Map<String, Double> sortByComparatorD(Map<String, Double> unsortMap)
	{
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Double>>()
		{
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2)
			{
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Collections.reverse(list);

		// Maintaining insertion order with the help of LinkedList
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}


	
	public static Map<String, Double> sortRevByComparatorD(Map<String, Double> unsortMap)
	{

		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Double>>()
		{
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2)
			{
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}


	
	public static void completoFile(LinkedList<String> checked){

		try {

			PrintWriter writerall = new PrintWriter(Main.queriesPath);

			checked.forEach(k -> {

				if(k.contains(" AND ")) k = k.replaceAll(" AND ", ", ");
				if(k.contains("(X)")){
					k = k.replaceAll("\\(X\\)", "(?X)");

				}
				int j=0;
				while(k.contains("(X, Y)")){
					k = k.replaceFirst("\\(X, Y\\)", "(?X, ?Y" +j+")");
					j++;
				}
				while(k.contains("(Y, X)")){
					k = k.replaceFirst("\\(Y, X\\)", "(?Y"+j+", ?X)");
					j++;
				}


				String[] attributes = k.split(" => ");

				writerall.println("Q(?X) <- " + attributes[0] +", -" + attributes[1] +".");
			});
			writerall.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void completoFile2(LinkedList<String> checked, String queriesPath) {


		try {

			PrintWriter writerall = new PrintWriter(queriesPath);

			checked.forEach(k -> {

				if(k.contains(" AND ")) k = k.replaceAll(" AND ", ", ");
				if(k.contains("(X)")){
					k = k.replaceAll("\\(X\\)", "(?X)");

				}
				int j=0;
				while(k.contains("(X, Y)")){
					k = k.replaceFirst("\\(X, Y\\)", "(?X, ?Y" +j+")");
					j++;
				}
				while(k.contains("(Y, X)")){
					k = k.replaceFirst("\\(Y, X\\)", "(?Y"+j+", ?X)");
					j++;
				}


				String[] attributes = k.split(" => ");

				writerall.println("Q(?X) <- " + attributes[0] +", " + attributes[1] +".");
				if (attributes[1].substring(0,1).equals("-")) attributes[1].replaceFirst("-","");
				else attributes[1] = "-" + attributes[1] ;
				writerall.println("Q(?X) <- " + attributes[0] +", " + attributes[1] +".");
				writerall.println("Q(?X) <- " + attributes[0] + ".");
				
			});
			writerall.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}

	public static String transformQuery (String line) {
		String body = "";
		String head = "";
		line = line.split("<- ")[1];
		String[] atts = line.split("\\), ");

		for(int i = 0; i<atts.length; i++){
			if(i < atts.length-1) atts[i] = atts [i] + ")";
			if(atts[i].contains("(?X,")) atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(X, Y)";
			else if(atts[i].contains("(?Y")) atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(Y, X)";
			else atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(X)";

			if(atts[i].contains("-")) {
				head = atts[i].replace("-", "");
			}
			else {
				if(!body.isEmpty()) body = body + " AND ";
				body = body + atts[i];
			}
		}
		String rule = body + " => " + head;

		return rule;
	}
	
	public static Map<String, Integer> constraints(Map<String, Integer> constr, String rule, String path){


		//Map<String, Integer> constr = new HashMap<>();
		if(!constr.containsKey(rule)) constr.put(rule, 0);
		
		if(Files.exists(Paths.get(path))){
			
			try(BufferedReader br = new BufferedReader(new FileReader(path))) {

				while(br.readLine() != null){

					if(constr.containsKey(rule)){

						constr.put(rule,constr.get(rule)+1);
					}
				}


				br.close();


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return constr;
	}


	public static Map<String, LinkedList<String>> rewritingsExisting(String rewritingsFile, Map<String, LinkedList<String>> Rewritings){

		try(BufferedReader br = new BufferedReader(new FileReader(rewritingsFile))) {
			Double prob;
			String temp = "";
			String line;
			while((line = br.readLine())!= null){

				if (line.contains("=>")){
					if(!Rewritings.containsKey(line)) Rewritings.put(line, new LinkedList<String>());
					temp = line;
					Main.Prob.put(line,0.0);
				}
				else {
					if(Rewritings.containsKey(temp)){
						prob = 1.0;
						Rewritings.get(temp).add(line);
						
						String[] att = line.split("\\), ");
						
						for(int k = 0; k < att.length; k++) {
							if(k < att.length-1) {
								att[k] = att[k] + ")";
							}
							
							if(Main.Prob.containsKey(att[k])) prob = prob * Main.Prob.get(att[k]);
							else prob = 0.0;
						}
						Main.Prob.put(temp, Main.Prob.get(temp) + prob);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Rewritings;
	}




	public static Map<String, LinkedList<String>> rewritings1(String rewritingsFile, Map<String, LinkedList<String>> Rewritings, String rule){

		if(!Rewritings.containsKey(rule)) Rewritings.put(rule, new LinkedList<String>());
		Double proball = 0.0;
		if(Files.exists(Paths.get(rewritingsFile))){
			try(BufferedReader br = new BufferedReader(new FileReader(rewritingsFile))) {
				String line;
				while((line = br.readLine())!= null){

					Double prob = 1.0;
					if(Rewritings.containsKey(rule)){
						line = line.substring(line.indexOf('-')+2, line.length()-1);
						String[] atts = line.split("\\), ");
						String rw = "";
						for(int i = 0; i<atts.length; i++){
							if(i < atts.length-1) atts[i] = atts [i] + ")";
							if(atts[i].contains("(?X)")) atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(X)";
							else if(atts[i].contains("(?X,")) atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(X, Y)";
							else atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(Y, X)";
							if(!rw.isEmpty()) rw = rw + ", ";
							rw = rw + atts[i];
							
							if(Main.Prob.containsKey(atts[i])) prob = prob * Main.Prob.get(atts[i]);
							else prob = 0.0;
						}
						Rewritings.get(rule).add(rw);
						proball = proball + prob;
					}
				}
				Main.Prob.put(rule, proball);
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return Rewritings;
	}




	public static Map<String, LinkedList<String>> rewritings2(Map<String, Integer> constr, String rewritingsFile, Map<String, LinkedList<String>> Rewritings, String rule){
		
		int rewr = 0;
		
		
		Double proball = 0.0;
		
		//if(!Rewritings.containsKey(rule)) Rewritings.put(rule, new LinkedList<String>());
		if(constr.get(rule) == 0) Rewritings.put(rule, new LinkedList<String>());
		if(Files.exists(Paths.get(rewritingsFile))){
			try(BufferedReader br = new BufferedReader(new FileReader(rewritingsFile))) {
				String line;


				while((line = br.readLine())!= null){
					Double prob = 1.0;
					rewr ++;
					if(Rewritings.containsKey(rule)){
						line = line.substring(line.indexOf('-')+2, line.length()-1);
						String[] atts = line.split("\\), ");
						String rw = "";
						for(int i = 0; i<atts.length; i++){
							if(i < atts.length-1) atts[i] = atts [i] + ")";
							if(atts[i].contains("(?X)")) atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(X)";
							else if(atts[i].contains("(?X,")) atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(X, Y)";
							else atts[i] = atts[i].substring(0, atts[i].indexOf('(')) + "(Y, X)";
							if(!rw.isEmpty()) rw = rw + ", ";
							rw = rw + atts[i];
							
							if(Main.Prob.containsKey(atts[i])) prob = prob * Main.Prob.get(atts[i]);
							else prob = 0.0;
						}
						Rewritings.get(rule).add(rw);
						proball = proball + prob;
					}
				}
				Main.Prob.put(rule, proball);
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		Main.newrules.put(rule, Main.newrules.get(rule) +  ";"+ rewr);
		
		return Rewritings;
	}


}
package thesis;


import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTable;
import org.semanticweb.owlapi.model.*;

public class Main {

	public static InfoHash InfoHash1;

	public static String ontologyPath = "";
	public static String tbox = "";
	public static String queriesPath = "";
	public static String constraintsPath = "";
	public static String output = "output_didnt_work.arff";
	public static String dir = "";
	public static String rewritingsFile = "", rulesFile = "";

	public static String assoc = "Apriori";

	public static Map<String, LinkedList<String>> rew = new HashMap<>();
	public static Map<String,Double> Confidence = new HashMap<>();
	public static Map<String,Double> Prob = new HashMap<>();

	public static Map<String, String> newrules = new HashMap<>();
	
	public static Map<String, List<String>> already = new HashMap<>();

	public static File time;


	public static void exit(boolean flag) {
		String message = "";
		if(flag) message = "Program canceled by user\n";
		message = message + "Exiting...";
		JOptionPane.showMessageDialog(new JFrame(), message);
		/*
		try {
			if(!queriesPath.equals("")){
				Files.deleteIfExists(Paths.get(queriesPath));
			}
			if(!constraintsPath.equals("")){
				Files.deleteIfExists(Paths.get(constraintsPath));
			}
			FileUtils.deleteDirectory(new File(dir+"/completo1"));
			FileUtils.deleteDirectory(new File(dir+"/completo2"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		System.exit(0);		
	}


	public static int stringToInt(String string) {

		try {
			int num = Integer.parseInt(string);
			if(num > 1) {
				return num;
			}
			else {
				JOptionPane.showMessageDialog(null,"Not an integer greater than 1! Please try again.");
				return -1;	    		
			}
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null,"Not an integer greater than 1! Please try again.");
			return -1;
		}
	}


	public static void load() throws OWLOntologyCreationException {
		//Load Ontology from File
		JOptionPane opt = new JOptionPane("Loading from File...");
		String userDir = System.getProperty("user.home");
		JFileChooser fileChooser = new JFileChooser(userDir +"/Desktop/OWL API");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("OWL File", "owl");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Choose an ontology from file.");
		int result = fileChooser.showOpenDialog(opt);
		File selectedFile = new File("/home/lida/thesis/OWL API/FD.owl");

		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		}
		else exit(false);
		ontologyPath = selectedFile.getAbsolutePath();
		output = selectedFile.getName().toString().replace(".owl", "");


	}

	
	
	public static void loadTbox() throws OWLOntologyCreationException {
		//Load Ontology from File
		//JOptionPane opt = new JOptionPane("Loading from File...");
		//String userDir = System.getProperty("user.home");
		//JFileChooser fileChooser = new JFileChooser(userDir +"/Desktop/OWL API");
		
		System.out.print("nai");
		
		//FileNameExtensionFilter filter = new FileNameExtensionFilter("OWL File", "owl");
		//fileChooser.setFileFilter(filter);
		//fileChooser.setDialogTitle("Choose an ontology from file.");
		//int result = fileChooser.showOpenDialog(opt);
		File selectedFile = new File("/Users/lidaptr/Desktop/thesis_paper/thesis/abox/tbox/lubm.owl");

		//if (result == JFileChooser.APPROVE_OPTION) {
		//	selectedFile = fileChooser.getSelectedFile();
		//	System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		//}
		//else exit(false);
		tbox = selectedFile.getAbsolutePath();
		
		System.out.println(tbox);

	}
	

	public static void main(String args[]) {
		// Load the ontology that will be used as a training data set

		int i = 0;
		boolean flag = true;

		Object[] selectionValues = { "Only classes", "Only object properties", "Both" };
		Object[][] data = null;

		LinkedList<String> RulesToFix = new LinkedList<>();
		LinkedList<String> RulesToCheck = new LinkedList<>();
		Map<String, Integer> constraints = new HashMap<>();

		FileWriter fw = null;
		BufferedWriter bw = null;

		dir = System.getProperty("user.dir");


		try {
			load();
		} catch (OWLOntologyCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		int reply = JOptionPane.showConfirmDialog(null, "Do you want to load a TBox?", "TBox", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			System.out.print("nai");
			try {
				loadTbox();
			} catch (OWLOntologyCreationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		
		
		
		
		

		new File(dir+"/"+output).mkdir();
		dir = dir+"/"+output;
		queriesPath = Main.dir + "/queries_" + Main.output + ".txt";

		constraintsPath = dir+"/constraints_" + Main.output +".txt";
		time = new File(dir+"/time.txt");
		try {
			time.createNewFile();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		Map<String, LinkedList<String>> finalRules = new HashMap<String, LinkedList<String>>();



		OntologyLoad o = new OntologyLoad();

		reply = JOptionPane.showConfirmDialog(null, "Do you want to load existing rules?", "Existing rules", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			flag = false;
			o = Rules.existing();
		}
		else if (reply == JOptionPane.NO_OPTION){			
			o = Rules.newrules(args);
		}
		else exit(true);




		if(flag){
			data = Rules.loadnewrules();
		}
		else{
			data = Rules.loadexisting();
		}



		String[] columns = new String[] {"Rule", "Confidence", "Counter-Examples", "Size of Rewritings"};

		JXTable table = new JXTable(data, columns);
		table.packAll();

		Map<String, LinkedList<String>> goodRules = new HashMap<String, LinkedList<String>>();
		final Object[][] cdata = data.clone();
		final Map<String, LinkedList<String>> crew = new HashMap<>(rew);
		TableDialog editRules = new TableDialog("Choose rules to edit: ", table);
		editRules.setOnOk(e -> {
			int[] items = editRules.getSelectedItem();

			for (Object item : items) {

				String initialRule = cdata[(int) item][0].toString();
				if((int) cdata[(int) item][2] == 0) {
					goodRules.put(initialRule, crew.get(initialRule));
					
					String body = initialRule.split(" => ")[0];
					String head = initialRule.split(" => ")[1];
					
					if(!already.containsKey(head)) already.put(head, new LinkedList<String>());
					already.get(head).add(body);
					
				}
				else {
					RulesToFix.add(initialRule);
					String head = initialRule.split(" => ")[1];
					if(!already.containsKey(head)) already.put(head, new LinkedList<String>());
				}
			}
		});
		editRules.show();

		finalRules = goodRules;

		new File(dir+"/completo2").mkdir();
		LinkedList<String> checked = new LinkedList<>();
		if(!RulesToFix.isEmpty()){
			String attributeShape = "";

			int n = JOptionPane.showOptionDialog(null, "Choose the shape of the attributes to add:", "Attribute shape...", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, selectionValues, selectionValues[2]);
			if(n == -1) exit(true);
			else if (n == 0) attributeShape = "c";
			else if(n == 1) attributeShape = "o";

			try {
				fw = new FileWriter(Main.time.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				Instant start = Instant.now();
				if(attributeShape.equals("c")) RulesToCheck = RuleEnrich.AddClass(RulesToFix);
				else if(attributeShape.equals("o")) RulesToCheck = RuleEnrich.AddObjectProperty(RulesToFix);
				else RulesToCheck = RuleEnrich.AddAttribute(RulesToFix);


				checked = QueryAnswer.checknew(RulesToCheck);

				Instant end = Instant.now();
				bw.write("New Rules: " + Duration.between(start, end).toString() + "\n");

				bw.close();
				fw.close();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(!checked.isEmpty()) {
			try {
				fw = new FileWriter(Main.time.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				FileEdit.completoFile(checked);
				Instant start = Instant.now();
				
				for (int j = 0; j < checked.size(); j++) {

					Rewriter.callCompleto(args, Main.ontologyPath, Main.queriesPath, Main.constraintsPath, 
							Main.output, dir+"/completo2/rewritings_"+j+".txt", dir+"/completo2/answers_"+j+".txt", j);	

				}
				
				Instant end = Instant.now();
				bw.write("Completo2: " + Duration.between(start, end).toString() + "\n");

				bw.close();
				fw.close();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (int j = 0; j < checked.size(); j++) {
				String rule = checked.get(j);

				constraints = FileEdit.constraints(constraints, rule, dir+"/completo2/answers_"+j+".txt");
				finalRules = FileEdit.rewritings2(constraints, dir+"/completo2/rewritings_"+j+".txt", finalRules, rule);
			}

		}




		if(finalRules.isEmpty()) {
			System.out.println("No rules found");
			JOptionPane.showMessageDialog(null, "No rules found");
			Main.exit(false);
		}


		
//		if(finalRules.containsKey("member(X, Y) AND memberOf(Y, X) => subOrganizationOf(X, Y)")){
//			System.out.println("Diagrafw to 3");
//			finalRules.remove("member(X, Y) AND memberOf(Y, X) => subOrganizationOf(X, Y)");
//		}
//		
//		if(finalRules.containsKey("member(X, Y) AND memberOf(Y, X) => Department(X)")){
//			System.out.println("Diagrafw to 3");
//			finalRules.remove("member(X, Y) AND memberOf(Y, X) => Department(X)");
//		}
		

		Map<String, Double> metric = new HashMap<>();

		for(Map.Entry<String, LinkedList<String>> entry : finalRules.entrySet()) {
			
			String rule = entry.getKey();
			Double met = Confidence.get(rule);
			Double prob = Prob.get(rule);
			Double size = 1.0 + prob;
			met = Math.round((met / size)*100)/100.0d;
			metric.put(rule, met);				
		}

		metric = FileEdit.sortRevByComparatorD(metric);
		
		
		try {
			PrintStream out = new PrintStream(Main.dir+"/"+ Main.output+".csv");
			out.println("\"rule\";\"newrule\";\"newtype\";\"confidence\";\"cexamples\";\"rwcexamples\"");
			for(Map.Entry<String, String> entry : newrules.entrySet()) {
				String[] col = entry.getValue().split(";");
				String rule = entry.getKey();
				out.println("\""+col[0]+"\";"+"\""+rule+"\";"+"\""+col[1]+"\";"+Confidence.get(rule)+";"+constraints.get(rule)+";"+col[2]);

			}
			out.close();
			
			out = new PrintStream(Main.dir+"/"+ Main.output+"_metric.csv");
			out.println("\"rule\";\"metric\";\"confidence\";\"rwcexamples\"");
			for(Map.Entry<String, Double> entry : metric.entrySet()){
				String rule = entry.getKey();
				out.println("\"" + rule +"\";" + entry.getValue()+";" +Confidence.get(rule)+";"+ finalRules.get(rule).size());
			}
			out.close();
			
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}


		columns = new String[] {"Rule", "Confidence", "Size of Rewritings", "Metric"};

		String[][] consistentRules = new String[metric.size()][4];
		i = 0;
		for (Map.Entry<String, Double> entry : metric.entrySet())
		{
			String rule = entry.getKey();
			consistentRules[i][0] = rule;
			consistentRules[i][1] = Confidence.get(rule).toString();
			consistentRules[i][2] = String.valueOf(finalRules.get(rule).size());
			consistentRules[i][3] = entry.getValue().toString();

			i++;

		}


		LinkedList<String> rules = new LinkedList<>();
		JXTable table1 = new JXTable(consistentRules, columns);
		table1.packAll();

		TableDialog addRules = new TableDialog("Choose rules to add to the ontology: ", table1);
		addRules.setOnOk(e -> {
			int[] items = addRules.getSelectedItem();


			for (Object item : items) {

				rules.add(consistentRules[(int) item][0]);

			}
		});
		addRules.show();


		try {
			CreateOWL.enrichedOntol(o.ontology, rules, finalRules);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		try {
			FileUtils.deleteDirectory(new File(dir+"/completo1"));
			FileUtils.deleteDirectory(new File(dir+"/completo2"));
			Files.deleteIfExists(Paths.get(queriesPath));
			Files.deleteIfExists(Paths.get(constraintsPath));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
*/

		System.exit(0);


	}

}
package thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import weka.core.Instances;

public class Rules {

	public static OntologyLoad existing(){

		JOptionPane opt = new JOptionPane("Loading rules...");
		String userDir = System.getProperty("user.home");
		JFileChooser fileChooser = new JFileChooser(userDir +"/Desktop/OWL API");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV file", "csv");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Choose file with existing rules.");
		int result = fileChooser.showOpenDialog(opt);
		File selectedFile = new File("/home/lida/thesis/OWL API/FD.owl");
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			Main.rulesFile = selectedFile.getAbsolutePath();
		}
		else Main.exit(false);

		opt = new JOptionPane("Loading rewritings...");
		filter = new FileNameExtensionFilter("TXT file", "txt");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Choose file with existing rewritings.");	
		result = fileChooser.showOpenDialog(opt);
		selectedFile = new File("/home/lida/thesis/OWL API/FD.owl");
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			Main.rewritingsFile = selectedFile.getAbsolutePath();
		}
		else Main.exit(false);

		OntologyLoad o = new OntologyLoad();

		//load without using reasoner
		//o.load(Main.ontologyPath, Main.dir +"/"+Main.output+".arff", false);

		//load after using reasoner
		o.load1(Main.ontologyPath, Main.dir +"/"+Main.output+".arff", false);
		return o;
	}


	public static OntologyLoad newrules(String[] args){
		int train = JOptionPane.showConfirmDialog(null, "Would you like to generate a database?", "Database", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (train == JOptionPane.YES_OPTION) {				
			Rewriter.generateDatabase(args, Main.ontologyPath, Main.output);
		}
		else if(train == JOptionPane.NO_OPTION) {

		}
		else Main.exit(true);

		Object[] select = {"Apriori", "Tertius"};

		int n = JOptionPane.showOptionDialog(null, "Choose an associator:", "Initializing associator", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, select, select[0]);
		if(n == -1) Main.exit(true);
		else if (n == 0) Main.assoc = "Apriori";
		else if(n == 1) Main.assoc = "Tertius";



		OntologyLoad o = new OntologyLoad();

		//load without using reasoner
		//o.load(Main.ontologyPath, Main.dir +"/"+Main.output+".arff", true);


		//load after using reasoner
		o.load1(Main.ontologyPath, Main.dir +"/"+Main.output+".arff", true);

		return o;
	}

	public static Object[][] loadnewrules(){
		FileWriter fw = null;
		BufferedWriter bw = null;

		String[] args = null;
		int number = -1, i;
		BufferedReader reader;
		Instances file;
		Map<String, Integer> constraints = new HashMap<>();
		Object[][] data = null;
		try {
			reader = new BufferedReader(new FileReader(Main.dir +"/"+Main.output+".arff"));
			file = new Instances(reader);
			//DeleteUseless del = new DeleteUseless();
			//file = del.delete(file);

			while (number == -1) {
				String size = JOptionPane.showInputDialog(null, "Set maximum number of literals in a rule...", "Enter the maximum number:", JOptionPane.QUESTION_MESSAGE);
				if(size == null) Main.exit(true);
				number = Main.stringToInt(size);
			}


			fw = new FileWriter(Main.time.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			Instant start = Instant.now();

			//use Apriori Algorithm
			if(Main.assoc.equals("Apriori")) {
				Associate.AssociatorApriori(file, number);
			}
			//use Tertius Algorithm
			else if(Main.assoc.equals("Tertius")) {
				Associate.Tertius(file, number);
			}
			Instant end = Instant.now();
			bw.write("Association Rules: " + Duration.between(start, end).toString() + "\n");

			bw.close();
			fw.close();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		Main.Confidence = FileEdit.sortRevByComparatorD(Main.Confidence);

		int j = 0;

		/*
		try {
			PrintStream out = new PrintStream(Main.dir+"/arxiko_"+ Main.output+".csv");
			out.println("\"rule\";\"confidence\";\"cexamples\";\"rwcexamples\"");
			for (Map.Entry<String, Double> entry : Main.Confidence.entrySet())
			{
				out.println("\"" + entry.getKey() + "\"" + ";" + entry.getValue() + ";" + " " + ";" + " ");
			}
			out.close();
		}
		catch(IOException ioe)
		{
			System.err.println("IOException: " + ioe.getMessage());
		}

		 */

		LinkedList<String> check = new LinkedList<String>(Main.Confidence.keySet());

		FileEdit.completoFile(check);

		Main.rulesFile = Main.dir +"/rules"+ number +"_"+ Main.output+".csv";
		//Main.rewritingsFile = Main.dir +"/rewritings"+ number+"_"+ Main.output+".txt";

		new File(Main.dir+"/completo1").mkdir();

		//Iterator<Map.Entry<String, Double>> iter = Main.Confidence.entrySet().iterator();


		try {
			fw = new FileWriter(Main.time.getAbsoluteFile(), true);

			bw = new BufferedWriter(fw);
			Instant start = Instant.now();
			
			for(j=0; j<Main.Confidence.size(); j++) {


				Rewriter.callCompleto(args, Main.ontologyPath, Main.queriesPath, Main.constraintsPath, Main.output, Main.dir+"/completo1/rewritings_" + j + ".txt", 
						Main.dir+"/completo1/answers_"+ j + ".txt", j);
			}
			
			Instant end = Instant.now();
			bw.write("Completo1: " + Duration.between(start, end).toString() + "\n");

			bw.close();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator<Map.Entry<String, Double>> iter = Main.Confidence.entrySet().iterator();
		j=0;
		
		while(iter.hasNext()){
			
			Map.Entry<String, Double> entry = iter.next();
			String rule = entry.getKey();
			constraints = FileEdit.constraints(constraints, rule,Main.dir+"/completo1/answers_"+ j + ".txt");
			Main.rew = FileEdit.rewritings1( Main.dir+"/completo1/rewritings_" + j + ".txt", Main.rew, rule);
			j++;
		}



		if(Main.rew.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No rules to edit");
			Main.exit(false);
		}

		try {
			PrintStream out = new PrintStream(Main.rulesFile);
			PrintStream rout = new PrintStream(Main.dir+"/rewr"+number +"_"+ Main.output+".txt");

			out.println("\"rule\";\"confidence\";\"cexamples\";\"rwcexamples\"");
			data = new Object[Main.rew.size()][4];
			i = 0;
			for (Map.Entry<String, Double> entry : Main.Confidence.entrySet())
			{
				String rule = entry.getKey();
				data[i][0] = rule;
				data[i][1] = entry.getValue();
				data[i][2] = constraints.get(rule);
				data[i][3] = Main.rew.get(rule).size();


				out.println("\"" + data[i][0] + "\"" + ";" + data[i][1] + ";" + data[i][2] + ";" + data[i][3]);
				i++;
				rout.println(rule);
				LinkedList<String> rewritings = Main.rew.get(rule);
				while(!rewritings.isEmpty()){
					String rw = rewritings.removeFirst();
					rout.println(rw);
				}

			}
			out.close();
			rout.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return data;
	}


	public static Object[][] loadexisting(){
		Object[][] data;
		Main.rew = FileEdit.rewritingsExisting(Main.rewritingsFile, Main.rew);
		data = new Object[Main.rew.size()][4];
		try(BufferedReader br = new BufferedReader(new FileReader(Main.rulesFile))) {
			String line = br.readLine();
			int i = 0;
			while((line = br.readLine())!= null){
				String[] col = line.split(";");
				//String rule = col[0];
				//System.out.println(rule);
				String rule = col[0].substring(col[0].indexOf("\"") + 1,col[0].lastIndexOf("\""));
				Main.Confidence.put(rule, Double.parseDouble(col[1]));
				data[i][0] = rule;
				data[i][1] = Double.parseDouble(col[1]);
				data[i][2] = Integer.parseInt(col[2]);
				data[i][3] = Integer.parseInt(col[3]);
				i++;
			}
			br.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return data;
	}




}

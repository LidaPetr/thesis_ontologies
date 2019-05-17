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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import weka.core.Instances;

public class Test2 {


	public static File load() throws OWLOntologyCreationException {
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



		return selectedFile;


	}
	
	
	public static Object[][] loadnewrules(String dir, String output, String assoc, String ontologyPath, String queriesPath, String constraintsPath){
		

		String[] args = null;
		int number = -1, i;
		BufferedReader reader;
		Instances file;
		Map<String, Integer> constraints = new HashMap<>();
		Object[][] data = null;
		try {
			reader = new BufferedReader(new FileReader(dir +"/"+output+".arff"));
			file = new Instances(reader);
			//DeleteUseless del = new DeleteUseless();
			//file = del.delete(file);

			while (number == -1) {
				String size = JOptionPane.showInputDialog(null, "Set maximum number of literals in a rule...", "Enter the maximum number:", JOptionPane.QUESTION_MESSAGE);
				if(size == null) Main.exit(true);
				number = Main.stringToInt(size);
			}



			//use Apriori Algorithm
			if(assoc.equals("Apriori")) {
				Associate.AssociatorApriori(file, number, queriesPath);
			}
			//use Tertius Algorithm
			else if(assoc.equals("Tertius")) {
				Associate.Tertius(file, number, queriesPath);
			}


		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		//Main.Confidence = FileEdit.sortRevByComparatorD(Main.Confidence);

		//int j = 0;


		//LinkedList<String> check = new LinkedList<String>(Main.Confidence.keySet());

		//FileEdit.completoFile2(check,queriesPath);

		String rulesFile = dir +"/rules"+ number +"_"+ output+".csv";
/*
		new File(dir+"/completo1").mkdir();
			
			for(j=0; j<Main.Confidence.size(); j++) {

				Rewriter.callCompleto(args, ontologyPath, queriesPath, constraintsPath, output, dir+"/completo1/rewritings_" + j + ".txt", 
						dir+"/completo1/answers_"+ j + ".txt", j);
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
			PrintStream out = new PrintStream(rulesFile);
			PrintStream rout = new PrintStream(dir+"/rewr"+number +"_"+ output+".txt");

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
		
		*/
		return data;
	}
	

	public static ArrayList<Object> load1(String opath, String arffpath, boolean flag, String assoc){


		ArrayList<Object> exprs = new ArrayList<>();
		ArrayList<String> atts = new ArrayList<>();

		
		Main.InfoHash1 = new InfoHash();

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		try {

			OWLOntology ontology = (opath.contains("http://"))? manager.loadOntologyFromOntologyDocument(IRI.create(opath))
					:manager.loadOntologyFromOntologyDocument(new FileDocumentSource(new File(opath)));

			OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);	


			ontology.classesInSignature().forEach(c -> {
				String cl = c.getIRI().getShortForm() + "(X)";

				if(!c.isTopEntity()){
					NodeSet<OWLNamedIndividual> ind = reasoner.getInstances(c);

					ind.nodes().forEach(in -> {

						String indiv = in.getRepresentativeElement().getIRI().getShortForm();
						Main.InfoHash1.insertC(cl, indiv);
						if(!atts.contains(cl)) atts.add(cl);					
					});
				}
			});

			ontology.objectPropertiesInSignature().forEach(p -> {


				if(!p.isOWLTopObjectProperty()){
					ontology.individualsInSignature().forEach(ind -> {
						String i1 = ind.getIRI().getShortForm();
						NodeSet<OWLNamedIndividual> individualValues = reasoner.getObjectPropertyValues(ind, p);
						Set<OWLNamedIndividual> values = individualValues.getFlattened();
						values.forEach(ind2 -> {
							String i2 = ind2.getIRI().getShortForm();
							String prop = p.getIRI().getRemainder().get() + "(X, Y)";
							Main.InfoHash1.insertOP(prop, i1, i2);

							if(!atts.contains(prop)) {
								atts.add(prop);
								atts.add(p.getIRI().getRemainder().get() + "(Y, X)");
							}
						});

					});


				}
			});


			if(flag){

				Owl2Arff o = new Owl2Arff();
				if(assoc.equals("Apriori")) {
					o.transform(opath,arffpath,atts,"?");
				}
				else o.transform(opath,arffpath,atts,"false");

			}



		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return exprs;

	}

	public static void main(String args[]) {
		// Load the ontology that will be used as a training data set

		int i = 0;
		boolean flag = true;



		String dir = System.getProperty("user.dir");

		File sFile;
		String ontologyPath = "";
		String opath = "";


		try {
			sFile = load();
			ontologyPath = sFile.getAbsolutePath();
			opath = sFile.getName().toString().replace(".owl", "");
		} catch (OWLOntologyCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		new File(dir+"/"+opath).mkdir();
		dir = dir+"/"+opath;
		
		String queriesPath = dir + "/queries_" + opath + ".txt";
		String constraintsPath = dir+"/constraints_" + opath +".txt";

		//Map<String, LinkedList<String>> finalRules = new HashMap<String, LinkedList<String>>();


		Rewriter.generateDatabase(args, ontologyPath, opath);
		
		Object[] select = {"Apriori", "Tertius"};
		String assoc ="";
		
		int n = JOptionPane.showOptionDialog(null, "Choose an associator:", "Initializing associator", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, select, select[0]);
		if(n == -1) Main.exit(true);
		else if (n == 0) assoc = "Apriori";
		else if(n == 1) assoc = "Tertius";
		
		OntologyLoad o = new OntologyLoad();
		o.load2(ontologyPath, dir +"/"+opath+".arff", true, assoc);
		
		Object[][] data = loadnewrules(dir, opath, assoc, ontologyPath, queriesPath, constraintsPath);


	}
}
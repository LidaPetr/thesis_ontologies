package thesis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


public class OntologyLoad {

	public OWLOntology ontology;
	private ArrayList<String> atts;
	private ArrayList<Object> exprs;

	public ArrayList<Object> load(String opath, String arffpath, boolean flag){
		Main.InfoHash1 = new InfoHash();
		exprs = new ArrayList<>();
		atts = new ArrayList<>();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			ontology = (opath.contains("http://"))? manager.loadOntologyFromOntologyDocument(IRI.create(opath))
					:manager.loadOntologyFromOntologyDocument(new FileDocumentSource(new File(opath)));

			ontology.individualsInSignature().forEach((ind)->{


				String ind1 = ind.getIRI().getShortForm();
				Stream<OWLClassAssertionAxiom> s = ontology.classAssertionAxioms(ind);
				s.forEach((axiom)->{
					if(axiom.getClassExpression().isOWLClass()) {
						OWLClass o = axiom.getClassExpression().asOWLClass();
						if(!o.isTopEntity()){
							if(!exprs.contains(o)) exprs.add(o);

							String cl = axiom.getClassExpression().asOWLClass().getIRI().getShortForm() + "(X)";
							Main.InfoHash1.insertC(cl, ind1);
						}
					}
				});

				ontology.objectPropertyAssertionAxioms(ind).forEach((axiom)->{

					OWLObjectPropertyExpression p = axiom.getProperty();
					if(!p.isTopEntity()){
						if(!exprs.contains(p.asObjectPropertyExpression().asOWLObjectProperty())) {
							exprs.add(p);
							exprs.add(p.getInverseProperty());
						}
						String prop = axiom.getProperty().asOWLObjectProperty().getIRI().getShortForm() + "(X, Y)";

						String obj;
						if(axiom.getObject().isAnonymous()) {
							obj = "isAnonymous!";
						}
						else obj = axiom.getObject().asOWLNamedIndividual().getIRI().getShortForm();

						Main.InfoHash1.insertOP(prop, ind1, obj);
					}
				});  
			});


			//			String ruleShape = "";
			if(flag){
				/*
				Object[] selectionValues1 = { "Only classes", "Only object properties", "Both" };
				int n = JOptionPane.showOptionDialog(null, "Choose the shape of the rules:", "Rule shape...", 
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, selectionValues1, selectionValues1[2]);
				if(n == -1) Main.exit(true);
				else if (n == 0) ruleShape = "c";
				else if(n == 1) ruleShape = "o";
				if(ruleShape.equals("c")) classesLoad();
				else if(ruleShape.equals("o")) propertiesLoad();
				else allLoad();
				 */
				allLoad();

				Owl2Arff o = new Owl2Arff();

				if(Main.assoc.equals("Apriori")) {
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

	private void allLoad(){
		exprs.stream().forEach(owlClassExpression -> {
			if (owlClassExpression instanceof OWLClass) {
				IRI iri = ((OWLClass) owlClassExpression).getIRI();
				String shortForm = iri.getShortForm();
				if (!atts.contains(shortForm+"(X)")) addBooleanAttr("(X)",shortForm);
			}
			if (owlClassExpression instanceof OWLObjectProperty) {
				IRI iri = ((OWLObjectProperty) owlClassExpression).getIRI();
				String shortForm = iri.getShortForm();
				if (!atts.contains(shortForm+"(X, Y)")) addBooleanAttr("(X, Y)", shortForm);
			}
			if (owlClassExpression instanceof OWLObjectInverseOf) {
				IRI iri = ((OWLObjectInverseOf) owlClassExpression).getNamedProperty().getIRI();
				String shortForm = iri.getShortForm();
				if (!atts.contains(shortForm+"(Y, X)")) addBooleanAttr("(Y, X)", shortForm);
			}
		});
	}

	private void addBooleanAttr(String string, String iri) {
		atts.add(iri +string);
	}


	public ArrayList<Object> load1(String opath, String arffpath, boolean flag){

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(Main.time.getAbsoluteFile(), false);			
			bw = new BufferedWriter(fw);
			
			PrintStream out = new PrintStream(Main.dir+"/probability.csv");
			out.println("\"concept\";\"probability\"");
			
			Main.InfoHash1 = new InfoHash();
			exprs = new ArrayList<>();
			atts = new ArrayList<>();
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

			try {
				Instant start = Instant.now();
				ontology = (opath.contains("http://"))? manager.loadOntologyFromOntologyDocument(IRI.create(opath))
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
				
				Instant end = Instant.now();
				bw.write("Ontology Load time: " + Duration.between(start, end).toString() + "\n");

				int all = Main.InfoHash1.getIndividuals().size();
				ArrayList<String> classes = Main.InfoHash1.getClasses();
				ArrayList<String> properties = Main.InfoHash1.getObjectProperties();
				
				for(String cl: classes){
					int ind = Main.InfoHash1.getClassIndividuals(cl);
					Double p = (ind * 1.00 )/ all;
					p = Math.round(p*1000)/1000.0d;
					Main.Prob.put(cl, p);
					out.println("\""+cl+"\";"+p);
					//System.out.println(cl + ":" + p);
				}
				for(String op: properties){
					int ind = Main.InfoHash1.getObjectPropertyIndividuals(op);
					Double p = (ind * 1.00 )/ all;
					p = Math.round(p*1000)/1000.0d;
					Main.Prob.put(op, p);
					out.println("\""+op+"\";"+p);
					//System.out.println(op + ":" + p);
				}
				
				out.close();
				
				if(flag){

					//allLoad();
					start = Instant.now();
					Owl2Arff o = new Owl2Arff();
					if(Main.assoc.equals("Apriori")) {
						o.transform(opath,arffpath,atts,"?");
					}
					else o.transform(opath,arffpath,atts,"false");
					end = Instant.now();
					bw.write("ARFF Creation: " + Duration.between(start, end).toString() + "\n");
					System.out.println("ARFF Creation time: " + Duration.between(start, end).toString());
				}

				bw.close();
				fw.close();

			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return exprs;

	}
	
	
	public ArrayList<Object> load2(String opath, String arffpath, boolean flag, String assoc){


		exprs = new ArrayList<>();
		atts = new ArrayList<>();

		
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
					o.transform(opath,arffpath,atts,"false");
				}
				else o.transform(opath,arffpath,atts,"false");

			}



		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return exprs;

	}



}
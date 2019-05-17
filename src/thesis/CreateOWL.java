package thesis;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;


@SuppressWarnings("deprecation")
public class CreateOWL {

	public static void  enrichedOntol(OWLOntology initial,LinkedList<String> initials, Map<String, LinkedList<String>> rewr) throws Exception {
		
		String message = "";
		

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = manager.getOWLDataFactory();

		IRI base = initial.getOntologyID().getOntologyIRI().get();
		PrefixManager pm = new DefaultPrefixManager(null, null, base.toString());



		OWLClassExpression clExp2 = null;


		while(!initials.isEmpty()) {
		message = message + "\nAdding new axiom to the ontology...\n";
		String inrule = initials.removeFirst();

		String[] rule = inrule.split(" => ");
		String head = rule[1];
		String[] body = rule[0].split(" AND ");
		if(head.contains("(X)")) {
			String cl = head.substring(0,head.lastIndexOf('('));
			clExp2 = dataFactory.getOWLClass(":#" + cl, pm);
			head = cl;
		}
		else if(head.contains("(X, Y)")) {
			String prop = head.substring(0,head.lastIndexOf('('));
			OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
			clExp2 = dataFactory.getOWLObjectSomeValuesFrom(objProp, dataFactory.getOWLThing());
			head = "?"+prop;
		}
		else if(head.contains("(Y, X)")) {
			String prop = head.substring(0,head.lastIndexOf('('));
			OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
			OWLObjectInverseOf invObjProp = dataFactory.getOWLObjectInverseOf(objProp);
			clExp2 = dataFactory.getOWLObjectSomeValuesFrom(invObjProp, dataFactory.getOWLThing());  
			head = "??"+prop;
		}


		if(body.length > 1){
			Set<OWLClassExpression> concepts = new HashSet<OWLClassExpression>();
			OWLObjectIntersectionOf intersection = null;
			message = message + "SUBCLASS: Intersection[";
			for(int k = 0; k < body.length; k++) {
				
				if(body[k].contains("(X)")) {
					String cl = body[k].substring(0,body[k].lastIndexOf('('));
					concepts.add(dataFactory.getOWLClass(":#" + cl, pm));
					if(k < body.length -1) message = message + cl + ", ";
					else message = message + cl + "] ";
					
				}
				else if(body[k].contains("(X, Y)")) {
					String prop = body[k].substring(0,body[k].lastIndexOf('('));
					OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
					concepts.add(dataFactory.getOWLObjectSomeValuesFrom(objProp, dataFactory.getOWLThing()));
					if(k < body.length -1) message = message + "?"+prop + ", ";
					else message = message + "?"+prop + "] ";
				}
				else if(body[k].contains("(Y, X)")) {
					String prop = body[k].substring(0,body[k].lastIndexOf('('));
					OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
					OWLObjectInverseOf invObjProp = dataFactory.getOWLObjectInverseOf(objProp);
					concepts.add(dataFactory.getOWLObjectSomeValuesFrom(invObjProp, dataFactory.getOWLThing())); 
					if(k < body.length -1) message = message + "?"+prop + "-inv, ";
					else message = message + "??"+prop + "] ";
				}
			}
			intersection = dataFactory.getOWLObjectIntersectionOf(concepts);

			OWLSubClassOfAxiom subAxiom = dataFactory.getOWLSubClassOfAxiom(intersection, clExp2);
			manager.addAxiom(initial, subAxiom);
			message = message + " of " + head + " \n";
		}

		else {
			OWLClassExpression clExp1 = null;
			if(body[0].contains("(X)")) {
				String cl = body[0].substring(0,body[0].lastIndexOf('('));
				clExp1 = dataFactory.getOWLClass(":#" + cl, pm);
				message = message + "SUBCLASS: " + cl + " of " + head + " \n";
			}
			else if(body[0].contains("(X, Y)")) {
				String prop = body[0].substring(0,body[0].lastIndexOf('('));
				OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
				clExp1 = dataFactory.getOWLObjectSomeValuesFrom(objProp, dataFactory.getOWLThing());
				message = message + "SUBCLASS: ?" + prop + " of " + head + " \n";
			}
			else if(body[0].contains("(Y, X)")) {
				String prop = body[0].substring(0,body[0].lastIndexOf('('));
				OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
				OWLObjectInverseOf invObjProp = dataFactory.getOWLObjectInverseOf(objProp);
				clExp1 = dataFactory.getOWLObjectSomeValuesFrom(invObjProp, dataFactory.getOWLThing()); 
				message = message + "SUBCLASS: ??" + prop + " of " + head + " \n";
			}

			OWLSubClassOfAxiom subAxiom = dataFactory.getOWLSubClassOfAxiom(clExp1, clExp2);
			manager.addAxiom(initial, subAxiom);
			
		}

		
		LinkedList<String> rewritings = rewr.get(inrule);
		if(!rewritings.isEmpty()) {
			message = message + "Adding the rewritings... \n";
		}
		while(!rewritings.isEmpty()){
			String rw = rewritings.removeFirst();
			String[] att = rw.split("\\), ");
			if(att.length > 1){
				Set<OWLClassExpression> concepts = new HashSet<OWLClassExpression>();

				message = message + "DISJOINT CLASSES:";
				for(int k = 0; k < att.length; k++) {
					if(k < att.length-1) {
						att[k] = att[k] + ")";
					}
					if(att[k].contains("(X)")) {
						String cl = att[k].substring(0,att[k].lastIndexOf('('));
						concepts.add(dataFactory.getOWLClass(":#" + cl, pm));
						if(k < att.length-1) {
							message = message + " " + cl + ",";
						}
						else message = message +" " + cl + " \n";;
					}
					else if(att[k].contains("(X, ")) {
						String prop = att[k].substring(0,att[k].lastIndexOf('('));
						OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
						concepts.add(dataFactory.getOWLObjectSomeValuesFrom(objProp, dataFactory.getOWLThing()));
						if(k < att.length-1) {
							message = message + " ?" + prop + ",";
						}
						else message = message +" ?" + prop + " \n";;
					}
					else if(att[k].contains(", X)")) {
						String prop = att[k].substring(0,att[k].lastIndexOf('('));
						OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
						OWLObjectInverseOf invObjProp = dataFactory.getOWLObjectInverseOf(objProp);
						concepts.add(dataFactory.getOWLObjectSomeValuesFrom(invObjProp, dataFactory.getOWLThing()));  
						if(k < att.length-1) {
							message = message + " ?" + prop + "-inv,";
						}
						else message = message +" ?" + prop + "-inv \n";;
					}
				}
				OWLDisjointClassesAxiom disjointClassesAxiom = dataFactory.getOWLDisjointClassesAxiom(concepts);
				manager.addAxiom(initial, disjointClassesAxiom);
			}
			else{
				att[0] = att[0] + ")";
				OWLClassExpression clExp1 = null;
				if(att[0].contains("(X)")) {
					String cl = att[0].substring(0,att[0].lastIndexOf('('));
					clExp1 = dataFactory.getOWLClass(":#" + cl, pm);
					message = message + "SUBCLASS: " + cl + " of owl:Nothing \n";
				}
				else if(att[0].contains("(X, ")) {
					String prop = att[0].substring(0,att[0].lastIndexOf('('));
					OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
					clExp1 = dataFactory.getOWLObjectSomeValuesFrom(objProp, dataFactory.getOWLThing());
					message = message + "SUBCLASS: ?" + prop + " of owl:Nothing \n";
				}
				else if(att[0].contains(", X)")) {
					String prop = att[0].substring(0,att[0].lastIndexOf('('));
					OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(":#" + prop, pm);
					OWLObjectInverseOf invObjProp = dataFactory.getOWLObjectInverseOf(objProp);
					clExp1 = dataFactory.getOWLObjectSomeValuesFrom(invObjProp, dataFactory.getOWLThing()); 
					message = message + "SUBCLASS: ?" + prop + "-inv of owl:Nothing \n";
				}

				OWLSubClassOfAxiom subAxiom = dataFactory.getOWLSubClassOfAxiom(clExp1, dataFactory.getOWLNothing());
				manager.addAxiom(initial, subAxiom);
			}
		}
		}
			
		
		JTextArea  textArea = new JTextArea(message);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		JOptionPane.showMessageDialog(null, scrollPane);
		
		 
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("OWL File", "owl");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Specify a file to save");   
		 
		int userSelection = fileChooser.showSaveDialog(null);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			
		 // Save the ontology in a different format
		    OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
		    
			// Save the new ontology - THE END.
			manager.saveOntology(initial, owlxmlFormat, IRI.create(fileToSave.toURI()));
		}
		else Main.exit(true);

	}
}
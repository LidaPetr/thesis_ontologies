package thesis;

import logic.program.OntologyRewriter;

public class Rewriter {


	public static void generateDatabase(String[] args, String ontology, String output) {
		
		
		
		
		args = new String[5];
		args[0] = "-o";
		args[1] = ontology;
		args[2] = "-eabox";
		args[3] = "-aboxname";
		args[4] = output;

		OntologyRewriter.main(args);
	}


	public static void callCompleto(String[] args, String ontology, String queries, String constraints, String output, String rewritings, String answers, int i) {
		
		if (!Main.tbox.isEmpty()) ontology = Main.tbox;
		
		args = new String[18];
		args[0] = "-o";
		args[1] = ontology;
		args[2] = "-q";
		args[3] = queries;
		args[4] = "-qi";
		args[5] = String.valueOf(i);
		args[6] = "-c";
		args[7] = constraints;
		args[8] = "-aboxname";
		args[9] = output;
		args[10] = "-qrewritings";
		args[11] = rewritings;
		args[12] = "-apath";
		args[13] = answers;
		args[14] = "-a";
		args[15] = "completor";
		args[16] = "-erewriter";
		args[17] = "graal";


		OntologyRewriter.main(args);
	}

}

package thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import logic.program.OntologyRewriter;

public class Test1 {
	
	
	
	public static void main3(String args[]) {
		String fileName = System.getProperty("user.dir") + "/initial_lubm001.csv";
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
			String line;
			
			line = br.readLine();
			
			PrintWriter w = new PrintWriter(System.getProperty("user.dir") + "new.csv");
			w.println(line);
			
			while ((line = br.readLine()) != null) {
				line = line.substring(0, line.length() - 1);
				w.println(line);
			}
			w.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	

	public static void main(String args[]) {
		
		String name = "travel";
		String path = "/travel/";
		
		System.out.println(System.getProperty("user.dir"));
		args = new String[5];
		args[0] = "-o";
		args[1] = System.getProperty("user.dir") +"/travel/travel.owl";
		args[2] = "-eabox";
		args[3] = "-aboxname";
		args[4] = name;
		OntologyRewriter.main(args);
		
		
		int i = 0;
		args = new String[18];
		args[0] = "-o";
		args[1] = System.getProperty("user.dir") +"/travel/travel.owl";
		args[2] = "-q";
		args[3] = System.getProperty("user.dir") + path +"queries_travel.txt";
		args[4] = "-qi";
		
		args[6] = "-c";
		args[7] = System.getProperty("user.dir") + path + "constraint.txt";
		args[8] = "-aboxname";
		args[9] = name;
		args[10] = "-qrewritings";
		
		args[12] = "-apath";
		
		args[14] = "-a";
		args[15] = "completor";
		args[16] = "-erewriter";
		args[17] = "graal";
		
		System.out.println(System.getProperty("user.dir"));
		
		while(i<2295) {
			args[11] = System.getProperty("user.dir") + path + "completo2/rewritings_" + i + ".txt";
			args[13] = System.getProperty("user.dir") + path + "completo2/counter_answers_" + i + ".txt";
			args[5] = String.valueOf(i);

			
			
				//System.out.println("Computing query "+i);
				OntologyRewriter.main(args);
				i++;
			args[11] = System.getProperty("user.dir") + path + "completo2/rewritings_" + i + ".txt";
			args[13] = System.getProperty("user.dir") + path + "completo2/answers_" + (i-1) + ".txt";
			args[5] = String.valueOf(i);
			OntologyRewriter.main(args);
			i++;
			args[11] = System.getProperty("user.dir") + path + "completo2/rewritings_" + i + ".txt";
			args[13] = System.getProperty("user.dir") + path + "completo2/body_" + (i-2) + ".txt";
			args[5] = String.valueOf(i);
			OntologyRewriter.main(args);
			i++;
		}
		
	}
}
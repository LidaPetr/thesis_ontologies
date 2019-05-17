package thesis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import weka.associations.ItemSet;
import weka.core.Instances;

public class AssociationResults{

	public static void tertiusConf(String results) throws Exception {

		String firstline = "\nTertius\n=======\n\n";
		String split1 = "\\*/ ";
		String rulesplit = " ==> ";
		String sidesplit = " = ";
		String lastline = "\n\nNumber";
		results = results.split(firstline)[1];
		results = results.split(lastline)[0];
		String[] lines = results.split("\\r?\\n", -1);

		for (String line : lines){

			String result = line.split(split1)[1];
			if(!result.contains("TRUE") && !result.contains("FALSE")) {
				//if(!result.contains(" false")){

				String side[] = result.split(rulesplit);
				String left = "";
				String right = side[1].split(sidesplit)[0];
				if(side[1].contains(" true")) right = "-" + right;
				String atts[] = side[0].split(" and ");
				Arrays.sort(atts);
				for(int i =0; i<atts.length; i++){
					if(!left.equals("")) left = left + " AND ";
					if(atts[i].contains(" false")) {
						atts[i] = "-"+atts[i];
					}
					left = left + atts[i].split(sidesplit)[0];
				}
				String rule = left + " => " + right;
				QueryAnswer ans = new QueryAnswer();
				double conf = ans.answernew(left,right);
				//if(conf > 0) {
				Main.Confidence.put(rule, conf);
				//}
				//}
			}
		} 

	}


	public static void tertiusConf2(String results, String queriesPath) throws Exception {

		String firstline = "\nTertius\n=======\n\n";
		String split1 = "\\*/ ";
		String rulesplit = " ==> ";
		String sidesplit = " = ";
		String lastline = "\n\nNumber";
		results = results.split(firstline)[1];
		results = results.split(lastline)[0];
		String[] lines = results.split("\\r?\\n", -1);
		try {

			PrintWriter writerall = new PrintWriter(queriesPath);

			for (String line : lines){

				String result = line.split(split1)[1];
				if(result.contains(" and ")) {

					//if(!result.contains(" false")){

					if(result.contains("(X)")){
						result = result.replaceAll("\\(X\\)", "(?X)");

					}
					int j=0;
					while(result.contains("(X, Y)")){
						result = result.replaceFirst("\\(X, Y\\)", "(?X, ?Y" +j+")");
						j++;
					}
					while(result.contains("(Y, X)")){
						result = result.replaceFirst("\\(Y, X\\)", "(?Y"+j+", ?X)");
						j++;
					}

					String side[] = result.split(rulesplit);
					String left = "";
					String right = side[1].split(sidesplit)[0] + ".";
					String right1 = "";
					if(side[1].contains(" true")) {
						right1 = right;
						right = "-" + right;
					}
					else right1 = "-" + right;

					String atts[] = side[0].split(" and ");
					Arrays.sort(atts);
					for(int i =0; i<atts.length; i++){
						if(atts[i].contains(" false")) {
							atts[i] = "-"+atts[i];
						}
						//if(!left.equals("")) left = left + ", ";

						left = left + atts[i].split(sidesplit)[0] + ", ";
					}
					String k = left + right;
					String k1 = left + right1;






					String[] attributes = k.split(" => ");

					writerall.println("Q(?X) <- " + k);
					writerall.println("Q(?X) <- " + k1);
					writerall.println("Q(?X) <- " + left.substring(0, left.length()-2) + ".");



				}

			}
			writerall.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		//}
	}






	public static void aprioriConf(@SuppressWarnings("rawtypes") ArrayList[] rules, Instances data) throws Exception {

		// For each rule
		
		for (int i = 0; i < rules[0].size(); i++) {
			
			// Find the premises
			// Count how many attributes take part in the left side of the rule
			// Handle only those rules with one attribute on the left side
			int count = 0;
			for (int k = 0; k < ((ItemSet) rules[1].get(i)).items().length; k++) {
				if (((ItemSet) rules[1].get(i)).items()[k] != -1) {
					count++;
				}
			}

			String body = "";
			String head = "";

			String val = "";
			ArrayList<String> atts = new ArrayList<>();
			// Find the premises

			// For each possible value of the nominal attribute

			if(count == 1) {
				for (int k = 0; k < ((ItemSet) rules[0].get(i)).items().length; k++) {

					// foo : the value of the nominal attribute in the rule
					int foo = ((ItemSet) rules[0].get(i)).items()[k];
					// if that attribute takes part in the association rule
					if (foo != -1) {
						// Save the value of the attribute
						if(!val.equals("")) val = val + " ";
						val = val + data.attribute(k).value(foo);
						atts.add(data.attribute(k).name());
					}
				}
				Collections.sort(atts);

				while(!atts.isEmpty()){
					if(!body.equals("")) body = body + " AND ";
					body = body + atts.remove(0);
				}
				// Find the consequences
				for (int k = 0; k < ((ItemSet) rules[1].get(i)).items().length; k++) {
					// foo : the value of the nominal attribute in the rule
					int foo = ((ItemSet) rules[1].get(i)).items()[k];
					if (foo != -1) {
						// Save the value of the attribute
						val = val + " " + data.attribute(k).value(foo);
						head = data.attribute(k).name();
						if (!val.contains("false")) {
							String rule = body + " => " + head;
							QueryAnswer ans = new QueryAnswer();
							double conf = ans.answernew(body,head);
							if(conf > 0) {
								Main.Confidence.put(rule, conf);
							}
						}
					}						
				}	
			}
		}
	}


	public static void aprioriConf2(@SuppressWarnings("rawtypes") ArrayList[] rules, Instances data, String queriesPath) throws Exception {

		// For each rule
		
		try {
			String f = (queriesPath.replace("queries", "rules")).replace(".txt", ".csv");
			PrintWriter writerall = new PrintWriter(queriesPath);
			PrintWriter writerall1 = new PrintWriter(f);
			
			writerall1.println("Rule;Confidence");
			

			for (int i = 0; i < rules[0].size(); i++) {


				String body = "";
				String b1 = "";
				String head = "";
				String h1 = "";
				String att = "";
				String val = "";
				String  rule = "";
				String r1 = "";
				ArrayList<String> atts = new ArrayList<>();
				// Find the premises

				// For each possible value of the nominal attribute

				int count = 0;
				for (int k = 0; k < ((ItemSet) rules[1].get(i)).items().length; k++) {
					if (((ItemSet) rules[1].get(i)).items()[k] != -1) {
						count++;
					}
				}
				int count2 = 0;
				for (int k = 0; k < ((ItemSet) rules[0].get(i)).items().length; k++) {
					if (((ItemSet) rules[0].get(i)).items()[k] != -1) {
						count2++;
					}
				}

				if(count == 1 && count2 > 1) {

					for (int k = 0; k < ((ItemSet) rules[0].get(i)).items().length; k++) {

						// foo : the value of the nominal attribute in the rule
						int foo = ((ItemSet) rules[0].get(i)).items()[k];
						// if that attribute takes part in the association rule
						if (foo != -1) {
							// Save the value of the attribute
							//if(!val.equals("")) val = val + " ";
							val = data.attribute(k).value(foo);
							if(val.contains("false")) att = "-" + data.attribute(k).name();
							else att = data.attribute(k).name();
							atts.add(att);
						}
					}
					//Collections.sort(atts);

					while(!atts.isEmpty()){
						String a = atts.remove(0);
						if(!b1.equals("")) b1 = b1 + " AND ";
						body = body + a + ", ";
						b1 = b1 + a;
					}
					// Find the consequences
					for (int k = 0; k < ((ItemSet) rules[1].get(i)).items().length; k++) {
						// foo : the value of the nominal attribute in the rule
						int foo = ((ItemSet) rules[1].get(i)).items()[k];
						if (foo != -1) {
							// Save the value of the attribute
							val = data.attribute(k).value(foo);
							if(val.contains("false")) {
								head = data.attribute(k).name();
								h1 = "-"+head;
							}
							else {
								h1 = data.attribute(k).name();
								head = "-" + data.attribute(k).name();
							}
							//if (!val.contains("false")) {
							rule = body + head + ".";
							r1= b1 + " ==> " + h1 + ";" + rules[2].get(i);

						}
					}
					writerall.println("Q(?X) <- " + rule);
					writerall1.println(r1);
				}						
			}
			writerall.close();
			writerall1.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


package thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;


public class Owl2Arff {


	private static final String BOOLEAN = "{true, false}";

	public void transform(String opath, String arffpath, ArrayList<String> atts, String flag) {
		
				
		try {
			
			PrintStream out = new PrintStream(arffpath);
			out.println("@relation "+getName(opath));
			//out.println("@attribute Individuals string");
			for (String string : atts) {
				out.println("@attribute \""+string+"\" "+BOOLEAN);
			}
			out.println("@DATA");
			
			

			ArrayList<String> individuals = Main.InfoHash1.getIndividuals();

			individuals.stream().forEach((ind)-> {

				StringBuilder b = new StringBuilder();
				//b.append("\"" + ind + "\"");

				for(String attribute : atts){
					if (b.length()>0)
						b.append(", ");
					if (attribute.contains("(X)")){
						if (Main.InfoHash1.belongs2class(attribute,ind)){
							b.append("true");
						}else {
							//b.append("?");
							b.append(flag);
						}	
					}
					else {
						if (Main.InfoHash1.belongs2objectProperty(attribute,ind)){
							b.append("true");
						}else {
							//b.append("?");
							b.append(flag);
						}	
					}
				}
				out.println(b.toString());
			});

			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getName(String opath) {
		return opath.substring(opath.lastIndexOf(File.separator)+1,opath.lastIndexOf('.'));
	}



}
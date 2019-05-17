package thesis;


import java.util.LinkedList;
import java.util.List;



public class QueryAnswer {

	public Double answernew(String body, String head){
		List<String> indiv = Main.InfoHash1.getIndividuals();
		Integer ruleInst = 0;
		Integer bodyInst = 0;
		String[] left = body.split(" AND ");

		int j=0;
		while(j<indiv.size()){

			boolean exist = true;
			int i = 0;
			while (i<left.length){
				String attr = left[i];
				if(!Main.InfoHash1.belongs2class(attr, indiv.get(j)) && !Main.InfoHash1.belongs2objectProperty(attr, indiv.get(j))) {
					exist = false;
					break;
				}
				i++;
			}
			if(exist){
				bodyInst++;
				if(Main.InfoHash1.belongs2class(head, indiv.get(j)) || Main.InfoHash1.belongs2objectProperty(head, indiv.get(j))){
					ruleInst++;
				}
			}
			j++;
		}
		if (bodyInst == 0) return 0.00;

		else {
			Double conf = (ruleInst * 1.00 )/ bodyInst;
			conf = Math.round(conf*100)/100.0d;
			return conf;
		}
	}

	public static LinkedList<String> checknew(LinkedList<String> rules){

		@SuppressWarnings("unchecked")
		LinkedList<String> checked =  (LinkedList<String>) rules.clone();
		while(!rules.isEmpty()){
			String rule = rules.removeFirst();
			String body = rule.split(" => ")[0];
			String head = rule.split(" => ")[1];
			QueryAnswer a = new QueryAnswer();
			double conf = a.answernew(body,head);
			if(conf != 0.00){
				Main.Confidence.put(rule, conf);
			}
			else {
				checked.remove(rule);
				Main.newrules.remove(rule);
			}
		}
		return checked;
	}
}


package thesis;

import java.util.Collections;
import java.util.LinkedList;



public class RuleEnrich {	

	public static LinkedList<String> AddAttribute(LinkedList<String> initialRules){

		LinkedList<String> rulesToCheck = new LinkedList<>();
		while(!initialRules.isEmpty()){	

			String in_rule = initialRules.removeFirst();
			String left = in_rule.split(" => ")[0];
			String right = in_rule.split(" => ")[1];
			String[] body = left.split(" AND ");

			for(String cl : Main.InfoHash1.getClasses()){

				if (!left.contains(cl) && !right.contains(cl)){
					
					if(!Main.already.get(right).contains(cl)) {
						LinkedList<String> list = new LinkedList<>();
						int i = 0;
						while(i<body.length){
							list.add(body[i]);
							i++;
						}
						list.add(cl);
						Collections.sort(list);
						left = "";
						while(!list.isEmpty()){
							if(!left.equals("")) left = left + " AND ";
							left = left + list.removeFirst();
						}

						String rule = left + " => " + right;
						if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
						if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";concept");
					}
				}
			}

			for (String op : Main.InfoHash1.getObjectProperties()) {

				if (!left.contains(op) && !right.contains(op)){
					if(!Main.already.get(right).contains(op)) {
						LinkedList<String> list = new LinkedList<>();
						int i = 0;
						while(i<body.length){
							list.add(body[i]);
							i++;
						}
						list.add(op);
						Collections.sort(list);
						left = "";
						while(!list.isEmpty()){
							if(!left.equals("")) left = left + " AND ";
							left = left +list.removeFirst();
						}
						String rule = left + " => " + right;
						if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
						if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";property");
					}
				}
			}
		}

		return rulesToCheck;
	}

	public static LinkedList<String> AddAttribute(String in_rule){

		LinkedList<String> rulesToCheck = new LinkedList<>();
		String left = in_rule.split(" => ")[0];
		String right = in_rule.split(" => ")[1];
		String[] body = left.split(" AND ");

		for(String cl : Main.InfoHash1.getClasses()){

			if (!left.contains(cl) && !right.contains(cl)){

				LinkedList<String> list = new LinkedList<>();
				int i = 0;
				while(i<body.length){
					list.add(body[i]);
					i++;
				}
				list.add(cl);
				Collections.sort(list);
				left = "";
				while(!list.isEmpty()){
					if(!left.equals("")) left = left + " AND ";
					left = left + list.removeFirst();
				}

				String rule = left + " => " + right;
				if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
				if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";concept");
			}
		}

		for (String op : Main.InfoHash1.getObjectProperties()) {

			if (!left.contains(op) && !right.contains(op)){

				LinkedList<String> list = new LinkedList<>();
				int i = 0;
				while(i<body.length){
					list.add(body[i]);
					i++;
				}
				list.add(op);
				Collections.sort(list);
				left = "";
				while(!list.isEmpty()){
					if(!left.equals("")) left = left + " AND ";
					left = left +list.removeFirst();
				}
				String rule = left + " => " + right;
				if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
				if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";property");
			}
		}
		return rulesToCheck;
	}


	public static LinkedList<String> AddClass(LinkedList<String> initialRules){
		LinkedList<String> rulesToCheck = new LinkedList<>();
		while(!initialRules.isEmpty()){	

			String in_rule = initialRules.removeFirst();
			String left = in_rule.split(" => ")[0];
			String right = in_rule.split(" => ")[1];
			String[] body = left.split(" AND ");

			for(String cl : Main.InfoHash1.getClasses()){

				if (!left.contains(cl) && !right.contains(cl)){

					LinkedList<String> list = new LinkedList<>();
					int i = 0;
					while(i<body.length){
						list.add(body[i]);
						i++;
					}
					list.add(cl);
					Collections.sort(list);
					left = "";
					while(!list.isEmpty()){
						if(!left.equals("")) left = left + " AND ";
						left = left + list.removeFirst();
					}
					String rule = left + " => " + right;
					if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
					if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";concept");
				}
			}
		}
		return rulesToCheck;
	}


	public static LinkedList<String> AddClass(String in_rule){

		LinkedList<String> rulesToCheck = new LinkedList<>();
		String left = in_rule.split(" => ")[0];
		String right = in_rule.split(" => ")[1];
		String[] body = left.split(" AND ");

		for(String cl : Main.InfoHash1.getClasses()){

			if (!left.contains(cl) && !right.contains(cl)){

				LinkedList<String> list = new LinkedList<>();
				int i = 0;
				while(i<body.length){
					list.add(body[i]);
					i++;
				}
				list.add(cl);
				Collections.sort(list);
				left = "";
				while(!list.isEmpty()){
					if(!left.equals("")) left = left + " AND ";
					left = left + list.removeFirst();
				}
				String rule = left + " => " + right;
				if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
				if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";concept");
			}
		}
		return rulesToCheck;
	}

	public static LinkedList<String> AddObjectProperty(LinkedList<String> initialRules){
		LinkedList<String> rulesToCheck = new LinkedList<>();
		while(!initialRules.isEmpty()){	

			String in_rule = initialRules.removeFirst();
			String left = in_rule.split(" => ")[0];
			String right = in_rule.split(" => ")[1];
			String[] body = left.split(" AND ");

			for (String op : Main.InfoHash1.getObjectProperties()) {

				if (!left.contains(op) && !right.contains(op)){

					LinkedList<String> list = new LinkedList<>();
					int i = 0;
					while(i<body.length){
						list.add(body[i]);
						i++;
					}
					list.add(op);
					Collections.sort(list);
					left = "";
					while(!list.isEmpty()){
						if(!left.equals("")) left = left + " AND ";
						left = left +list.removeFirst();
					}
					String rule = left + " => " + right;
					if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
					if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";property");
				}
			}
		}

		return rulesToCheck;
	}


	public static LinkedList<String> AddObjectProperty(String in_rule){

		LinkedList<String> rulesToCheck = new LinkedList<>();
		String left = in_rule.split(" => ")[0];
		String right = in_rule.split(" => ")[1];
		String[] body = left.split(" AND ");

		for (String op : Main.InfoHash1.getObjectProperties()) {

			if (!left.contains(op) && !right.contains(op)){

				LinkedList<String> list = new LinkedList<>();
				int i = 0;
				while(i<body.length){
					list.add(body[i]);
					i++;
				}
				list.add(op);
				Collections.sort(list);
				left = "";
				while(!list.isEmpty()){
					if(!left.equals("")) left = left + " AND ";
					left = left +list.removeFirst();
				}
				String rule = left + " => " + right;
				if(!rulesToCheck.contains(rule)) rulesToCheck.add(rule);
				if(!Main.newrules.containsKey(rule)) Main.newrules.put(rule, in_rule + ";property");

			}				
		}		
		return rulesToCheck;
	}

}





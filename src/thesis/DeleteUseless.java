package thesis;

import weka.core.Attribute;
import weka.core.Instances;

public class DeleteUseless {
	public Instances delete(Instances data){
		Instances inst = new Instances(data);

		boolean flag;

		// For each attribute see if all its values are the same
		// and if they are the same delete the attribute

		int del = 0;
		for (int i = 0; i < inst.numAttributes(); i++) {
			flag = true;
			if (inst.attribute(i).type() != Attribute.STRING && inst.attribute(i).type() != Attribute.NUMERIC) {

				for (int j = 1; ((j < inst.attributeToDoubleArray(i).length) && flag); j++) {

					// If a different value is found, flag->false & go to the next attribute
					if (inst.attributeToDoubleArray(i)[0] != inst.attributeToDoubleArray(i)[j]) {
						flag = false;
						break;
					}
				}
			}

			if (flag) {

				System.out.println("Deleting attribute " + inst.attribute(i).name());

				if ((inst.attribute(i).name()).contains("(X)")){
					Main.InfoHash1.deleteC(inst.attribute(i).name());
				}
				else {
					Main.InfoHash1.deleteOP(inst.attribute(i).name());
				}
				data.deleteAttributeAt(i-del);
				del++;
			}
		}
		return data;
	}
}
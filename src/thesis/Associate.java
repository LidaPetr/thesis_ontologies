package thesis;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import weka.associations.*;
import weka.core.Instances;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.WekaEnumeration;

@SuppressWarnings("serial")
public class Associate extends Apriori{


	@SuppressWarnings("unchecked")
	public void buildAssociations(Instances instances, int att_num) throws Exception {

		double[] confidences, supports;
		int[] indices;
		ArrayList<Object>[] sortedRuleSet;
		double necSupport = 0;

		instances = new Instances(instances);

		if (m_removeMissingCols) {
			instances = removeMissingColumns(instances);
		}
		if (m_car && m_metricType != CONFIDENCE) {
			throw new Exception("For CAR-Mining metric type has to be confidence!");
		}

		// only set class index if CAR is requested
		if (m_car) {
			if (m_classIndex == -1) {
				instances.setClassIndex(instances.numAttributes() - 1);
			} else if (m_classIndex <= instances.numAttributes() && m_classIndex > 0) {
				instances.setClassIndex(m_classIndex - 1);
			} else {
				throw new Exception("Invalid class index.");
			}
		}

		// can associator handle the data?
		getCapabilities().testWithFail(instances);

		m_cycles = 0;

		// make sure that the lower bound is equal to at least one instance
		double lowerBoundMinSupportToUse = (m_lowerBoundMinSupport
				* instances.numInstances() < 1.0) ? 1.0 / instances.numInstances()
						: m_lowerBoundMinSupport;

				if (m_car) {
					// m_instances does not contain the class attribute
					m_instances = LabeledItemSet.divide(instances, false);

					// m_onlyClass contains only the class attribute
					m_onlyClass = LabeledItemSet.divide(instances, true);
				} else {
					m_instances = instances;
				}

				if (m_car && m_numRules == Integer.MAX_VALUE) {
					// Set desired minimum support
					m_minSupport = lowerBoundMinSupportToUse;
				} else {
					// Decrease minimum support until desired number of rules found.
					// m_minSupport = m_upperBoundMinSupport - m_delta;
					m_minSupport = 1.0 - m_delta;
					m_minSupport = (m_minSupport < lowerBoundMinSupportToUse) ? lowerBoundMinSupportToUse
							: m_minSupport;
				}

				do {
					// Reserve space for variables
					m_Ls = new ArrayList<ArrayList<Object>>();
					m_hashtables = new ArrayList<Hashtable<ItemSet, Integer>>();
					m_allTheRules = new ArrayList[6];
					m_allTheRules[0] = new ArrayList<Object>();
					m_allTheRules[1] = new ArrayList<Object>();
					m_allTheRules[2] = new ArrayList<Object>();
					// if (m_metricType != CONFIDENCE || m_significanceLevel != -1) {
					m_allTheRules[3] = new ArrayList<Object>();
					m_allTheRules[4] = new ArrayList<Object>();
					m_allTheRules[5] = new ArrayList<Object>();
					// }
					sortedRuleSet = new ArrayList[6];
					sortedRuleSet[0] = new ArrayList<Object>();
					sortedRuleSet[1] = new ArrayList<Object>();
					sortedRuleSet[2] = new ArrayList<Object>();
					// if (m_metricType != CONFIDENCE || m_significanceLevel != -1) {
					sortedRuleSet[3] = new ArrayList<Object>();
					sortedRuleSet[4] = new ArrayList<Object>();
					sortedRuleSet[5] = new ArrayList<Object>();
					// }
					if (!m_car) {
						// Find large itemsets and rules
						findLargeItemSets(att_num);
						if (m_significanceLevel != -1 || m_metricType != CONFIDENCE) {
							findRulesBruteForce();
						} else {
							findRulesQuickly();
						}
					} else {
						findLargeCarItemSets(att_num);
						findCarRulesQuickly();
					}

					// prune rules for upper bound min support
					if (m_upperBoundMinSupport < 1.0) {
						pruneRulesForUpperBoundSupport();
					}

					// Sort rules according to their support
					/*
					 * supports = new double[m_allTheRules[2].size()]; for (int i = 0; i <
					 * m_allTheRules[2].size(); i++) supports[i] =
					 * (double)((AprioriItemSet)m_allTheRules[1].elementAt(i)).support();
					 * indices = Utils.stableSort(supports); for (int i = 0; i <
					 * m_allTheRules[2].size(); i++) {
					 * sortedRuleSet[0].add(m_allTheRules[0].get(indices[i]));
					 * sortedRuleSet[1].add(m_allTheRules[1].get(indices[i]));
					 * sortedRuleSet[2].add(m_allTheRules[2].get(indices[i])); if
					 * (m_metricType != CONFIDENCE || m_significanceLevel != -1) {
					 * sortedRuleSet[3].add(m_allTheRules[3].get(indices[i]));
					 * sortedRuleSet[4].add(m_allTheRules[4].get(indices[i]));
					 * sortedRuleSet[5].add(m_allTheRules[5].get(indices[i])); } }
					 */
					int j = m_allTheRules[2].size() - 1;
					supports = new double[m_allTheRules[2].size()];
					for (int i = 0; i < (j + 1); i++) {
						supports[j - i] = ((double) ((ItemSet) m_allTheRules[1].get(j - i))
								.support()) * (-1);
					}
					indices = Utils.stableSort(supports);
					for (int i = 0; i < (j + 1); i++) {
						sortedRuleSet[0].add(m_allTheRules[0].get(indices[j - i]));
						sortedRuleSet[1].add(m_allTheRules[1].get(indices[j - i]));
						sortedRuleSet[2].add(m_allTheRules[2].get(indices[j - i]));
						if (!m_car) {
							// if (m_metricType != CONFIDENCE || m_significanceLevel != -1) {
							sortedRuleSet[3].add(m_allTheRules[3].get(indices[j - i]));
							sortedRuleSet[4].add(m_allTheRules[4].get(indices[j - i]));
							sortedRuleSet[5].add(m_allTheRules[5].get(indices[j - i]));
						}
						// }
					}

					// Sort rules according to their confidence
					m_allTheRules[0].clear();
					m_allTheRules[1].clear();
					m_allTheRules[2].clear();
					// if (m_metricType != CONFIDENCE || m_significanceLevel != -1) {
					m_allTheRules[3].clear();
					m_allTheRules[4].clear();
					m_allTheRules[5].clear();
					// }
					confidences = new double[sortedRuleSet[2].size()];
					int sortType = 2 + m_metricType;

					for (int i = 0; i < sortedRuleSet[2].size(); i++) {
						confidences[i] = ((Double) sortedRuleSet[sortType].get(i))
								.doubleValue();
					}
					indices = Utils.stableSort(confidences);
					for (int i = sortedRuleSet[0].size() - 1; (i >= (sortedRuleSet[0].size() - m_numRules))
							&& (i >= 0); i--) {
						m_allTheRules[0].add(sortedRuleSet[0].get(indices[i]));
						m_allTheRules[1].add(sortedRuleSet[1].get(indices[i]));
						m_allTheRules[2].add(sortedRuleSet[2].get(indices[i]));
						// if (m_metricType != CONFIDENCE || m_significanceLevel != -1) {
						if (!m_car) {
							m_allTheRules[3].add(sortedRuleSet[3].get(indices[i]));
							m_allTheRules[4].add(sortedRuleSet[4].get(indices[i]));
							m_allTheRules[5].add(sortedRuleSet[5].get(indices[i]));
						}
						// }
					}

					if (m_verbose) {
						if (m_Ls.size() > 1) {
							System.out.println(toString());
						}
					}

					if (m_minSupport == lowerBoundMinSupportToUse
							|| m_minSupport - m_delta > lowerBoundMinSupportToUse) {
						m_minSupport -= m_delta;
					} else {
						m_minSupport = lowerBoundMinSupportToUse;
					}

					necSupport = Math.rint(m_minSupport * m_instances.numInstances());

					m_cycles++;
				} while ((m_allTheRules[0].size() < m_numRules)
						&& (Utils.grOrEq(m_minSupport, lowerBoundMinSupportToUse))
						/* (necSupport >= lowerBoundNumInstancesSupport) */
						/* (Utils.grOrEq(m_minSupport, m_lowerBoundMinSupport)) */&& (necSupport >= 1));
				m_minSupport += m_delta;
	}


	private void pruneRulesForUpperBoundSupport() {
		int necMaxSupport = (int) (m_upperBoundMinSupport
				* m_instances.numInstances() + 0.5);

		@SuppressWarnings("unchecked")
		ArrayList<Object>[] prunedRules = new ArrayList[6];
		for (int i = 0; i < 6; i++) {
			prunedRules[i] = new ArrayList<Object>();
		}

		for (int i = 0; i < m_allTheRules[0].size(); i++) {
			if (((ItemSet) m_allTheRules[1].get(i)).support() <= necMaxSupport) {
				prunedRules[0].add(m_allTheRules[0].get(i));
				prunedRules[1].add(m_allTheRules[1].get(i));
				prunedRules[2].add(m_allTheRules[2].get(i));

				if (!m_car) {
					prunedRules[3].add(m_allTheRules[3].get(i));
					prunedRules[4].add(m_allTheRules[4].get(i));
					prunedRules[5].add(m_allTheRules[5].get(i));
				}
			}
		}
		m_allTheRules[0] = prunedRules[0];
		m_allTheRules[1] = prunedRules[1];
		m_allTheRules[2] = prunedRules[2];
		m_allTheRules[3] = prunedRules[3];
		m_allTheRules[4] = prunedRules[4];
		m_allTheRules[5] = prunedRules[5];

	}

	private void findCarRulesQuickly() throws Exception {

		ArrayList<Object>[] rules;

		// Build rules
		for (int j = 0; j < m_Ls.size(); j++) {
			ArrayList<Object> currentLabeledItemSets = m_Ls.get(j);
			Enumeration<Object> enumLabeledItemSets = new WekaEnumeration<Object>(
					currentLabeledItemSets);
			while (enumLabeledItemSets.hasMoreElements()) {
				LabeledItemSet currentLabeledItemSet = (LabeledItemSet) enumLabeledItemSets
						.nextElement();
				rules = currentLabeledItemSet.generateRules(m_minMetric, false);
				for (int k = 0; k < rules[0].size(); k++) {
					m_allTheRules[0].add(rules[0].get(k));
					m_allTheRules[1].add(rules[1].get(k));
					m_allTheRules[2].add(rules[2].get(k));
				}
			}
		}
	}


	private void findLargeCarItemSets(int att_num) throws Exception {

		ArrayList<Object> kMinusOneSets, kSets;
		Hashtable<ItemSet, Integer> hashtable;
		int necSupport, i = 0;

		// Find large itemsets

		// minimum support
		double nextMinSupport = m_minSupport * m_instances.numInstances();
		double nextMaxSupport = m_upperBoundMinSupport * m_instances.numInstances();
		if (Math.rint(nextMinSupport) == nextMinSupport) {
			necSupport = (int) nextMinSupport;
		} else {
			necSupport = Math.round((float) (nextMinSupport + 0.5));
		}
		if (Math.rint(nextMaxSupport) == nextMaxSupport) {
		} else {
			Math.round((float) (nextMaxSupport + 0.5));
		}

		// find item sets of length one
		//kSets = LabeledItemSet.singletons(m_instances, m_onlyClass);
		//LabeledItemSet.upDateCounters(kSets, m_instances, m_onlyClass);

		kSets = LabeledItemSet.singletons(m_instances, m_instances);
		LabeledItemSet.upDateCounters(kSets, m_instances, m_instances);


		// check if a item set of lentgh one is frequent, if not delete it
		kSets = LabeledItemSet.deleteItemSets(kSets, necSupport,
				m_instances.numInstances());
		if (kSets.size() == 0) {
			return;
		}
		do {
			m_Ls.add(kSets);
			kMinusOneSets = kSets;
			kSets = LabeledItemSet.mergeAllItemSets(kMinusOneSets, i,
					m_instances.numInstances());
			hashtable = LabeledItemSet.getHashtable(kMinusOneSets,
					kMinusOneSets.size());
			kSets = LabeledItemSet.pruneItemSets(kSets, hashtable);
			//LabeledItemSet.upDateCounters(kSets, m_instances, m_onlyClass);
			LabeledItemSet.upDateCounters(kSets, m_instances, m_instances);
			kSets = LabeledItemSet.deleteItemSets(kSets, necSupport,
					m_instances.numInstances());
			i++;
		} while (kSets.size() > 0 && i < att_num );
	}

	private void findLargeItemSets(int att_num) throws Exception {

		ArrayList<Object> kMinusOneSets, kSets;
		Hashtable<ItemSet, Integer> hashtable;
		int necSupport, i = 0;

		// Find large itemsets

		// minimum support
		necSupport = (int) (m_minSupport * m_instances.numInstances() + 0.5);

		kSets = AprioriItemSet.singletons(m_instances, m_treatZeroAsMissing);
		if (m_treatZeroAsMissing) {
			AprioriItemSet.upDateCountersTreatZeroAsMissing(kSets, m_instances);
		} else {
			AprioriItemSet.upDateCounters(kSets, m_instances);
		}
		kSets = AprioriItemSet.deleteItemSets(kSets, necSupport,
				m_instances.numInstances());
		if (kSets.size() == 0) {
			return;
		}
		do {
			m_Ls.add(kSets);
			kMinusOneSets = kSets;
			kSets = AprioriItemSet.mergeAllItemSets(kMinusOneSets, i,
					m_instances.numInstances());
			hashtable = AprioriItemSet.getHashtable(kMinusOneSets,
					kMinusOneSets.size());
			m_hashtables.add(hashtable);
			kSets = AprioriItemSet.pruneItemSets(kSets, hashtable);
			if (m_treatZeroAsMissing) {
				AprioriItemSet.upDateCountersTreatZeroAsMissing(kSets, m_instances);
			} else {
				AprioriItemSet.upDateCounters(kSets, m_instances);
			}
			kSets = AprioriItemSet.deleteItemSets(kSets, necSupport,
					m_instances.numInstances());
			i++;
		} while (kSets.size() > 0 && i < att_num);
	}

	private void findRulesBruteForce() throws Exception {

		ArrayList<Object>[] rules;

		// Build rules
		for (int j = 1; j < m_Ls.size(); j++) {
			ArrayList<Object> currentItemSets = m_Ls.get(j);
			Enumeration<Object> enumItemSets = new WekaEnumeration<Object>(
					currentItemSets);
			while (enumItemSets.hasMoreElements()) {
				AprioriItemSet currentItemSet = (AprioriItemSet) enumItemSets
						.nextElement();
				// AprioriItemSet currentItemSet = new
				// AprioriItemSet((ItemSet)enumItemSets.nextElement());
				rules = currentItemSet.generateRulesBruteForce(m_minMetric,
						m_metricType, m_hashtables, j + 1, m_instances.numInstances(),
						m_significanceLevel);
				for (int k = 0; k < rules[0].size(); k++) {
					m_allTheRules[0].add(rules[0].get(k));
					m_allTheRules[1].add(rules[1].get(k));
					m_allTheRules[2].add(rules[2].get(k));

					m_allTheRules[3].add(rules[3].get(k));
					m_allTheRules[4].add(rules[4].get(k));
					m_allTheRules[5].add(rules[5].get(k));
				}
			}
		}
	}

	private void findRulesQuickly() throws Exception {

		ArrayList<Object>[] rules;
		// Build rules
		for (int j = 1; j < m_Ls.size(); j++) {
			ArrayList<Object> currentItemSets = m_Ls.get(j);
			Enumeration<Object> enumItemSets = new WekaEnumeration<Object>(
					currentItemSets);
			while (enumItemSets.hasMoreElements()) {
				AprioriItemSet currentItemSet = (AprioriItemSet) enumItemSets
						.nextElement();
				rules = currentItemSet.generateRules(m_minMetric, m_hashtables, j + 1);
				for (int k = 0; k < rules[0].size(); k++) {
					m_allTheRules[0].add(rules[0].get(k));
					m_allTheRules[1].add(rules[1].get(k));
					m_allTheRules[2].add(rules[2].get(k));

					if (rules.length > 3) {
						m_allTheRules[3].add(rules[3].get(k));
						m_allTheRules[4].add(rules[4].get(k));
						m_allTheRules[5].add(rules[5].get(k));
					}
				}
			}
		}
	}


	public static void AssociatorApriori(Instances data, int att_num, String queriesPath) throws Exception {
		/*
		 * 
		 * Class for running an arbitrary associator on data that has been passed through an arbitrary filter. 
		 * Like the associator, the structure of the filter is based exclusively on the training data and test instances 
		 * will be processed by the filter without changing their structure.
		 * 
		 * */

		Associate apriori = new Associate();
		apriori.setMinMetric(0.0);
		apriori.setNumRules(30000);
		apriori.setLowerBoundMinSupport(0.0);
		apriori.buildAssociations(data, att_num);

		String bestrules = apriori.toString();
		System.out.println(bestrules);

		if (!bestrules.contains("No large itemsets and rules found!")) {
			@SuppressWarnings("rawtypes")
			ArrayList[] ass = apriori.getAllTheRules();
			//AssociationResults.aprioriConf(ass, data);
			AssociationResults.aprioriConf2(ass, data, queriesPath);

		}
		else {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane.showMessageDialog(null, "No large itemsets and rules found!");
			Main.exit(false);
		}


	}

	public static final Tag [] tags = {
			new Tag(0, "Confidence"),
			new Tag(1, "Lift"),
			new Tag(2, "Leverage"),
			new Tag(3, "Conviction")
	};




	public static void Tertius(Instances data, int num, String queriesPath) throws Exception {
		/*
		 * 
		 * Class for running an arbitrary associator on data that has been passed through an arbitrary filter. 
		 * Like the associator, the structure of the filter is based exclusively on the training data and test instances 
		 * will be processed by the filter without changing their structure.
		 * 
		 * */
		Tertius ter = new Tertius();
		ter.setConfirmationValues(1000);
		ter.setHornClauses(true);
		ter.setNumberLiterals(num);

		ter.buildAssociations(data);

		String bestrules = ter.toString();
		System.out.println(bestrules);

		if (bestrules != null) {
			AssociationResults.tertiusConf(bestrules);
			AssociationResults.tertiusConf2(bestrules, queriesPath);
		}
		else {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane.showMessageDialog(null, "No large itemsets and rules found!");
		}

	} 


}
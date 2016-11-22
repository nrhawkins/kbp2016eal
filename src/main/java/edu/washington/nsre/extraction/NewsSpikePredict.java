package edu.washington.nsre.extraction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;

import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import edu.washington.nsre.stanfordtools.StanfordRegression;
import edu.washington.nsre.util.D;
import edu.washington.nsre.util.DR;
import edu.washington.nsre.util.DW;
import edu.washington.nsre.util.Util;

import edu.washington.nsre.figer.FigerParsedSentence;
import edu.washington.multir2.RelationPrediction;
import edu.washington.multir2.Argument;

public class NewsSpikePredict {

        /* ---------------------------------------------
            Add this method for Deft Integration
           ------------------------------------------------*/
	public static List<RelationPrediction> predict(String input_model, List<Tuple> input_test) {

//                System.out.println("NSP - tuples size: " + input_test.size());

                //output
                List<RelationPrediction> predictions = new ArrayList<RelationPrediction>();

		Map<String, Counter<String>> ftrEventMap = new HashMap<String, Counter<String>>();

                //Buffered Writer and Reader
		//DW dw = new DW(output_extraction);
		DR dr = new DR(input_model);

                // --------------------------------------
                // Read nsre model from file
                // --------------------------------------

		String[] l;
		HashMap<String, Set<String>> phrasestr2candidate = new HashMap<String, Set<String>>();
		while ((l = dr.read()) != null) {
			if (l[0].equals("ENDCANDIDATE"))
				break;
			phrasestr2candidate.put(l[1], gson.fromJson(l[2], Set.class));
		}

		while ((l = dr.read()) != null) {
			String eventstr = l[0];
			String ftr = l[1];
			double val = Double.parseDouble(l[2]);
			if (!ftrEventMap.containsKey(ftr)) {
				ftrEventMap.put(ftr, new ClassicCounter<String>());
			}
			ftrEventMap.get(ftr).incrementCount(eventstr, val);
		}
		dr.close();
               
                // ----------------------------------------
                // Use test data = List<Tuple> input_test
                // ----------------------------------------

                int sentenceStartToken = 0;
                String previousSentence = "";		
                int previousSentenceNumTokens = 0;
                int sentenceCount = 0;

                for (Tuple t : input_test) {	

			String s = t.getSentence();
 
                        if(!s.equals(previousSentence)){
                            sentenceCount = sentenceCount + 1;
                            if(sentenceCount > 1) sentenceStartToken = sentenceStartToken + previousSentenceNumTokens;
                        }

			Set<String> candidates = new HashSet<String>();
			for (String phrase : t.getPatternList()) {
				if (phrasestr2candidate.containsKey(phrase)) {
					candidates.addAll(phrasestr2candidate.get(phrase));
				}
			}
			Counter<String> dist1 = t.getArg1FineGrainedNer();
			Counter<String> dist2 = t.getArg2FineGrainedNer();
			HashMap<String, Double> fts = new HashMap<String, Double>();
			for (String a1t : dist1.keySet()) {
				for (String a2t : dist2.keySet()) {
					for (String pattern : t.getPatternList()) {
						String f = a1t + "|" + pattern + "|" + a2t;
						double fw = dist1.getCount(a1t) * dist2.getCount(a2t);
						fts.put(f, fw);
					}
					String[] wordsInShortestPath = t.wordsInShortestPath();
					if (wordsInShortestPath != null && wordsInShortestPath.length >= 2) {
						String f = a1t + "|" + t.getShortestPathFromTuple() + "|" + a2t;
						fts.put(f, 1.0);
					}
				}
			}
			Counter<String> scorer_withneg = scoresOf(ftrEventMap, fts);
			Counter<String> scorer = new ClassicCounter<String>();
			for (String eventstr : candidates) {
				double v = scorer_withneg.getCount(eventstr);
				if (v > 0)
					scorer.incrementCount(eventstr, v);
			}
			Counters.normalize(scorer);

                        // ------------------------------------------------------------
                        // Create RelationPrediction - for predictions made; i.e. > 0
                        // ------------------------------------------------------------
			if (scorer.size() > 0) {
                        	
                                String arg1Name = t.getArg1();
                                String arg2Name = t.getArg2();                                 
                                //String[] argString = l[0].split("@");
                                //if(argString.length > 1){ 
                                // 	arg1Name = argString[0]; 
				//	arg2Name = argString[1]; 
				//}                               
                                Argument arg1 = new Argument(arg1Name, t.a1[0]+sentenceStartToken, t.a1[1]+sentenceStartToken);   
                                Argument arg2 = new Argument(arg2Name, t.a2[0]+sentenceStartToken, t.a2[1]+sentenceStartToken);

                                System.out.println("NSP: relation:confidence string: " + Util.counter2jsonstr(scorer));
                               
                                //First, remove outer brackets from relation string, {}, then split by comma
                                // {"leave@/person@/organization":0.9999999999999999}
                                // {"die at@/person@/number":0.4954774036942262,"die@/person@/number":0.5045225963057739}
                                //Util.counter2jsonstr(scorer).split(",");
                                String relationString = Util.counter2jsonstr(scorer); 
                                String[] rpStrings = relationString.substring(1,relationString.length()-1).split(",");
                                int rpStringsLastIndex = rpStrings.length-1;
                                String[] rpString = "".split(":");
                                if(rpStringsLastIndex >= 0){
                                	rpString = rpStrings[rpStringsLastIndex].split(":");
                                } 
                                //String[] rpString = Util.counter2jsonstr(scorer).split(":");
                                //String rpString = t.getRel();
				String relation = "";
                                Double confidence = 0.0;
                                if(rpString.length > 1) { 
                                  relation = rpString[0].substring(1,rpString[0].length()-1); 
                                  confidence = Double.parseDouble(rpString[1].substring(0,rpString[1].length()));
                                } 

                          	RelationPrediction rp = new RelationPrediction(arg1,arg2,relation,0,confidence);
				predictions.add(rp);

                     	    	//dw.write(Util.counter2jsonstr(scorer), l[0], l[1]);
                
                        } 

                        previousSentence = s;
                        previousSentenceNumTokens = t.tkn.length;

		} //for tuple

		return predictions;
	}

	public static void predict(String input_model, String input_test, String output_extraction) {
		Map<String, Counter<String>> ftrEventMap = new HashMap<String, Counter<String>>();
		DW dw = new DW(output_extraction);
		DR dr = new DR(input_model);
		String[] l;
		HashMap<String, Set<String>> phrasestr2candidate = new HashMap<String, Set<String>>();
		while ((l = dr.read()) != null) {
			if (l[0].equals("ENDCANDIDATE"))
				break;
			phrasestr2candidate.put(l[1], gson.fromJson(l[2], Set.class));
		}

		while ((l = dr.read()) != null) {
			String eventstr = l[0];
			String ftr = l[1];
			double val = Double.parseDouble(l[2]);
			if (!ftrEventMap.containsKey(ftr)) {
				ftrEventMap.put(ftr, new ClassicCounter<String>());
			}
			ftrEventMap.get(ftr).incrementCount(eventstr, val);
		}
		dr.close();
		dr = new DR(input_test);
		while ((l = dr.read()) != null) {
			Tuple t = gson.fromJson(l[2], Tuple.class);
			String s = t.getSentence();
			// if (s.contains("Carmelo Anthony sits as")) {
			// D.p(s);
			// }
			Set<String> candidates = new HashSet<String>();
			for (String phrase : t.getPatternList()) {
				if (phrasestr2candidate.containsKey(phrase)) {
					candidates.addAll(phrasestr2candidate.get(phrase));
				}
			}
			Counter<String> dist1 = t.getArg1FineGrainedNer();
			Counter<String> dist2 = t.getArg2FineGrainedNer();
			HashMap<String, Double> fts = new HashMap<String, Double>();
			for (String a1t : dist1.keySet()) {
				for (String a2t : dist2.keySet()) {
					for (String pattern : t.getPatternList()) {
						String f = a1t + "|" + pattern + "|" + a2t;
						double fw = dist1.getCount(a1t) * dist2.getCount(a2t);
						fts.put(f, fw);
					}
					String[] wordsInShortestPath = t.wordsInShortestPath();
					if (wordsInShortestPath != null && wordsInShortestPath.length >= 2) {
						String f = a1t + "|" + t.getShortestPathFromTuple() + "|" + a2t;
						fts.put(f, 1.0);
					}
				}
			}
			Counter<String> scorer_withneg = scoresOf(ftrEventMap, fts);
			Counter<String> scorer = new ClassicCounter<String>();
			for (String eventstr : candidates) {
				double v = scorer_withneg.getCount(eventstr);
				if (v > 0)
					scorer.incrementCount(eventstr, v);
			}
			// for (String eventstr : scorer_withneg.keySet()) {
			// double v = scorer_withneg.getCount(eventstr);
			// if (v > 1) {
			// scorer.incrementCount(eventstr, v);
			// }
			// }
			Counters.normalize(scorer);
			if (scorer.size() > 0) {
				dw.write(Util.counter2jsonstr(scorer), l[0], l[1]);
			} else {
				dw.write(Util.counter2jsonstr(scorer));
			}
		}
		dr.close();
		dw.close();
	}

	public static Counter<String> scoresOf(Map<String, Counter<String>> ftrEventMap, HashMap<String, Double> fts) {
		Counter<String> ret = new ClassicCounter<String>();
		for (String f : fts.keySet()) {
			double v1 = fts.get(f);
			if (ftrEventMap.containsKey(f)) {
				Counter<String> weight = ftrEventMap.get(f);
				for (String eventstr : weight.keySet()) {
					double v2 = weight.getCount(eventstr);
					double s = v1 * v2;
					ret.incrementCount(eventstr, s);
				}
			}
		}
		return ret;
	}

	public static void predict(String input_generated, String input_model, String input_test,
			String output_extraction) {
		StanfordRegression sr = new StanfordRegression();
		sr.loadModel(input_model);
		DR dr = new DR(input_generated);
		String[] l;
		// DW dw = new DW(output);
		HashMultimap<String, String> possibleEvents = HashMultimap.create();
		while ((l = dr.read()) != null) {
			EventType eventtype = new EventType(l[0]);
			Tuple t = gson.fromJson(l[3], Tuple.class);
			String tupleName = l[2];
			possibleEvents.put(eventtype.arg1type + "|" + l[1] + "|" + eventtype.arg2type, eventtype.str);
			HashMap<String, Double> fts = new HashMap<String, Double>();
			for (String pattern : t.getPatternList()) {
				String f = eventtype.arg1type + "|" + pattern + "|" + eventtype.arg2type;
				fts.put(f, 1.0);
			}
		}
		dr.close();

		dr = new DR(input_test);
		DW dw = new DW(output_extraction);
		while ((l = dr.read()) != null) {
			Counter<String> scorer = new ClassicCounter<String>();
			Tuple t = gson.fromJson(l[2], Tuple.class);
			String tupleName = l[2];
			Counter<String> dist1 = t.getArg1FineGrainedNer();
			Counter<String> dist2 = t.getArg2FineGrainedNer();
			Set<String> candidates = new HashSet<String>();
			HashMap<String, Double> fts = new HashMap<String, Double>();
			for (String a1t : dist1.keySet()) {
				for (String a2t : dist2.keySet()) {
					for (String pattern : t.getPatternList()) {
						String f = a1t + "|" + pattern + "|" + a2t;
						if (possibleEvents.containsKey(f)) {
							candidates.addAll(possibleEvents.get(f));
						}
						double fw = dist1.getCount(a1t) * dist2.getCount(a2t);
						fts.put(f, fw);
					}

				}
			}
			if (fts.size() > 0) {
				Map<String, Double> allscores = sr.scoreOf(fts);
				for (String r : candidates) {
					scorer.incrementCount(r, allscores.get(r));
				}
				Counters.normalize(scorer);
			}
			dw.write(Util.counter2jsonstr(scorer));
		}
		dw.close();
	}

	public static void readDump(String input_model) {
		DR dr = new DR(input_model);
		String[] l0 = dr.read();
		String[] s = l0[0].split(" ");
		dr.close();
	}

	static Gson gson = new Gson();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String input_generated = args[0];
		try {
			// Config.parseConfig();
			String inputModelFile = args[0];
			String inputTestFile = args[1];
			String outputPredictFile = args[2];
			predict(inputModelFile, inputTestFile, outputPredictFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String input_model = args[0];
		// String input_test = args[1];
		// String output_extraction = args[2];
		// predict(input_model, input_test, output_extraction);
		// readDump(input_model);
		// predict(input_generated, input_model, input_test, output_extraction);

	}

}

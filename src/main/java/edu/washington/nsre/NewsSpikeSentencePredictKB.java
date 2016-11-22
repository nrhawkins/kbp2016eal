package edu.washington.nsre;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adept.common.Document;
import adept.common.Token;
import adept.common.TokenStream;

import edu.washington.multir2.RelationPrediction;
import edu.washington.nsre.extraction.Tuple;
import edu.washington.nsre.extraction.NewsSpikeRelationPrediction;
import edu.washington.nsre.figer.FigerParsedSentence;
import edu.washington.nsre.figer.Parsed2Tuple;
import edu.washington.nsre.figer.ParseStanfordFigerReverb;
import edu.washington.nsre.extraction.NewsSpikePredictKB;

public class NewsSpikeSentencePredictKB {
	
	String model_nsre = "model";          
//	String model_nsre = "/nsre/model";          

	public NewsSpikeSentencePredictKB() {
	}

	public NewsSpikeSentencePredictKB(String nsre_model) {
		model_nsre = nsre_model;   
	}

	public List<NewsSpikeRelationPrediction> predict(String text) throws IOException, InterruptedException {

                List<Tuple> tuples = new ArrayList<Tuple>();                
                List<FigerParsedSentence> ps = ParseStanfordFigerReverb.process(text);
                //System.out.println("NSSPkb ps size: " + ps.size());
                for(FigerParsedSentence fps : ps){
                  tuples.addAll(Parsed2Tuple.getReverbTuples(fps));
                }
                //System.out.println("NSSPkb tuples size: " + tuples.size());
		List<NewsSpikeRelationPrediction> predictions = NewsSpikePredictKB.predict(model_nsre, tuples);
					
		return predictions;

	}


}


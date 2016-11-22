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

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

import edu.washington.multir2.RelationPrediction;
import edu.washington.nsre.extraction.Tuple;
import edu.washington.nsre.figer.FigerParsedSentence;
import edu.washington.nsre.figer.Parsed2Tuple;
import edu.washington.nsre.figer.ParseStanfordFigerReverb;
import edu.washington.nsre.extraction.NewsSpikePredict;
import edu.washington.nsre.extraction.NewsSpikePredictKB;
import edu.washington.nsre.extraction.NewsSpikeRelationPrediction;


public class NewsSpikeSentencePredict {
	
	String model_nsre = "model";          
//	String model_nsre = "/nsre/model";          

	public NewsSpikeSentencePredict() {
	}

	public NewsSpikeSentencePredict(String nsre_model) {
		model_nsre = nsre_model;   
	}

	public List<RelationPrediction> predict(String text) throws IOException, InterruptedException {

                List<Tuple> tuples = new ArrayList<Tuple>();                
                List<FigerParsedSentence> ps = ParseStanfordFigerReverb.process(text);
                for(FigerParsedSentence fps : ps){
                  tuples.addAll(Parsed2Tuple.getReverbTuples(fps));
                }
		List<RelationPrediction> predictions = NewsSpikePredict.predict(model_nsre, tuples);
					
		return predictions;

	}


	//public List<NewsSpikeRelationPrediction> predictKBP(String sentenceText) throws IOException, InterruptedException {
	public List<NewsSpikeRelationPrediction> predictKBP(Annotation document, ParseStanfordFigerReverb sys) throws IOException, 
InterruptedException {

                List<Tuple> tuples = new ArrayList<Tuple>();                
                List<FigerParsedSentence> ps = ParseStanfordFigerReverb.processKBP(document,sys);
//                List<FigerParsedSentence> ps = ParseStanfordFigerReverb.processKBP(sentenceText);
                for(FigerParsedSentence fps : ps){
                  tuples.addAll(Parsed2Tuple.getReverbTuples(fps));
                }

                //System.out.println("Tuples #: " + tuples.size());

                //for(Tuple tuple: tuples){

                   //System.out.println("Tuple: " + tuple.arg1str + " " + tuple.relstr + " " + tuple.arg2str);
                   //if(tuple.getArg1() != null){
                   //   System.out.println("Tuple: " + tuple.getArg1() + ", " + tuple.getRel() + ", " + tuple.getArg2() + 
                   //       ", " + tuple.getArg1Ner() + ", " + tuple.getArg2Ner() + ", " + tuple.date + ", " + 
                   //       tuple.articleId);
                   //}
                   //System.out.println("Tuple: " + tuple.getRelForReverb());
                   // + " " + tuple.getRelOllie()); 

                //}

		List<NewsSpikeRelationPrediction> predictions = NewsSpikePredictKB.predict(model_nsre, tuples);
					
		return predictions;

	}


}

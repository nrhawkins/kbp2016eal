package edu.washington.utilities;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;


public class CoreNLPTextLoader{


    public static Annotation readCoreNLPTextFile(String filename) {

        Annotation annotation = new Annotation();

        try{

            String testTree = "(ROOT (S (NP (NNP Barack) (NNP Obama)) (VP (VBD was) (VP (VBN born) (PP (IN in) (NP (NNP Honolulu))))) (. .)))";
            //String testTree = "(ROOT (S (NP (NNP Cipriani)) (VP (VBD left) (NP (NNP Ensisheim))) (. .)))";
            Tree tree = Tree.valueOf(testTree);

            SemanticGraph sg = SemanticGraphFactory.makeFromTree(tree);
            System.out.println("CNLP_TL: sent, dependencies: " + sg.toList());


            String testSentence = "Barack Obama was born in Honolulu.";
            List<CoreLabel> tokens = new ArrayList<CoreLabel>();            

            //CoreLabel token0 = new CoreLabel();
            //token0.setFromString("Text=killer CharacterOffsetBegin=165 CharacterOffsetEnd=171 PartOfSpeech=NN Lemma=killer NamedEntityTag=O");
            //System.out.println("token0: " + token0.tag() + "," + token0.ner() + "," + token0.lemma() + "," + token0.value() + "," + token0.beginPosition() + "," + token0.endPosition());

            CoreLabel token1 = new CoreLabel();

            int beginPosition = 0;
            int endPosition = 6;
            token1.setBeginPosition(beginPosition);
            token1.setEndPosition(endPosition);
            token1.setOriginalText("Barack");
            token1.setValue("Barack");
            token1.setWord("Barack");
            token1.setTag("NNP");
            //token.set(PartOfSpeechAnnotation.class,"NNP");
            token1.set(LemmaAnnotation.class,"Barack");            
            //token1.setLemma("Barack");
            token1.setNER("PERSON");

            //System.out.println("lemma test: " + token1.lemma() + "," + token1.get(LemmaAnnotation.class));
            //System.exit(1);

            tokens.add(token1);

            CoreLabel token2 = new CoreLabel();

            beginPosition = 7;
            endPosition = 12;
            token2.setBeginPosition(beginPosition);
            token2.setEndPosition(endPosition);
            token2.setOriginalText("Obama");
            token2.setValue("Obama");
            token2.setWord("Obama");
            token2.setTag("NNP");
            token2.setLemma("Obama");
            //token.set(PartOfSpeechAnnotation.class,"NNP");
            token2.setNER("PERSON");

            tokens.add(token2);

            CoreLabel token3 = new CoreLabel();
          
            beginPosition = 13;
            endPosition = 16;
            token3.setBeginPosition(beginPosition);
            token3.setEndPosition(endPosition);
            token3.setOriginalText("was");
            token3.setValue("was");
            token3.setWord("was");
            token3.setTag("VBD");
            token3.setLemma("be");
            token3.setNER("O");
            //token.set(PartOfSpeechAnnotation.class,"VBD");

            tokens.add(token3);

            CoreLabel token4 = new CoreLabel();

            beginPosition = 17;
            endPosition = 21;
            token4.setBeginPosition(beginPosition);
            token4.setEndPosition(endPosition);
            token4.setOriginalText("born");
            token4.setValue("born");
            token4.setWord("born");
            token4.setTag("VBN");
            token4.setLemma("bear");
            token4.setNER("O");
            //token.set(PartOfSpeechAnnotation.class,"VBN");

            tokens.add(token4);

            CoreLabel token5 = new CoreLabel();

            beginPosition =22;
            endPosition = 24;
            token5.setBeginPosition(beginPosition);
            token5.setEndPosition(endPosition);
            token5.setOriginalText("in");
            token5.setValue("in");
            token5.setWord("in");
            token5.setTag("IN");
            token5.setLemma("in");
            token5.setNER("O");
            //token.set(PartOfSpeechAnnotation.class,"IN");

            tokens.add(token5);

            CoreLabel token6 = new CoreLabel();

            beginPosition = 25;
            endPosition = 33;
            token6.setBeginPosition(beginPosition);
            token6.setEndPosition(endPosition);
            token6.setOriginalText("Honolulu");
            token6.setValue("Honolulu");
            token6.setWord("Honolulu");
            token6.setTag("NNP");
            token6.setLemma("Honolulu");
            token6.setNER("LOCATION");
            //token.set(PartOfSpeechAnnotation.class,"NNP");

            tokens.add(token6);

            CoreLabel token7 = new CoreLabel();

            beginPosition = 33;
            endPosition = 34;
            token7.setBeginPosition(beginPosition);
            token7.setEndPosition(endPosition);
            token7.setOriginalText(".");
            token7.setValue(".");
            token7.setWord(".");
            token7.setTag(".");
            token7.setLemma(".");
            token7.setNER("O");
            //token.set(PartOfSpeechAnnotation.class,".");

            tokens.add(token7);


            System.out.println("first token: " + token1.value() + ", " + token1.beginPosition() + ", " + token1.endPosition() + ", " + token1.lemma() + ", " + token1.ner());
            System.out.println("last token: " + token7.value() + ", " + token7.beginPosition() + ", " + token7.endPosition() + ", " + token7.lemma() + ", " + token7.ner());
            System.out.println("tokens: " + tokens.size());


            //List<CoreLabel> tokens = new ArrayList<CoreLabel>();
            //List<CoreLabel> tokensOut = new ArrayList<CoreLabel>();

            //tokens.add(token);

            List<CoreMap> sentences = new ArrayList<CoreMap>();
            CoreMap sentence = new ArrayCoreMap();
            sentence.set(TokensAnnotation.class,tokens);            
            sentence.set(TreeAnnotation.class, tree);
            sentence.set(TextAnnotation.class, testSentence);            
            sentence.set(CollapsedDependenciesAnnotation.class, sg);

            sentences.add(sentence);

            String sentenceText = sentence.get(TextAnnotation.class);
        
            //tokensOut = sentence.get(TokensAnnotation.class);

            System.out.println("sentences: " + sentences.size());
            System.out.println("sentence Text: " + sentenceText);
            //System.out.println("sentence tokens: " + tokensOut.size());

            annotation.set(SentencesAnnotation.class, sentences);



        } catch (Exception e){
            e.printStackTrace();
            
        }

        return annotation;


    }


}

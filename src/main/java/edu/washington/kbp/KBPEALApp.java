package edu.washington.kbp;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.lang.StringBuffer;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import com.bbn.bue.common.IntIDSequence;
import com.bbn.bue.common.scoring.Scored;
import com.bbn.bue.common.symbols.Symbol;
import com.bbn.kbp.events2014.Response;
import com.bbn.kbp.events2014.ResponseSet;
import com.bbn.kbp.events2014.KBPString;
import com.bbn.kbp.events2014.CharOffsetSpan;
import com.bbn.kbp.events2014.KBPRealis;
import com.bbn.kbp.events2014.DocumentSystemOutput2015;
import com.bbn.kbp.events2014.ArgumentOutput;
import com.bbn.kbp.events2014.ResponseLinking;

import com.bbn.kbp.events2014.io.SystemOutputStore2016;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableBiMap;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.DocDateAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import edu.washington.common.KBPDocument;
import edu.washington.cs.figer.analysis.Preprocessing;

import edu.washington.nsre.LabelTriple;
import edu.washington.nsre.NewsSpikeSentencePredict;
import edu.washington.nsre.extraction.NewsSpikeRelationPrediction;
import edu.washington.nsre.figer.ParseStanfordFigerReverb;

import edu.washington.io.Reader;

import edu.washington.utilities.StanfordAnnotatorHelperMethods;
import edu.washington.utilities.CoreNLPTextLoader;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;

//import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraph;


public class KBPEALApp {


    //public static void main(String[] args){
    //    System.out.println("KBPEALApp: " );
    //}

    public static void doProcessNewsSpike(List<KBPDocument> documents, String outputDirectory){

        //Annotation annotation = CoreNLPTextLoader.readCoreNLPTextFile("test");
        //List<CoreMap> sentencesX = annotation.get(SentencesAnnotation.class);
        //System.out.println("KBPEALApp num sentences: " + sentencesX.size());
        //String sentence1 = sentencesX.get(0).get(TextAnnotation.class);
        //System.out.println("KBPEALApp sent text:" + sentence1);

        //System.exit(1);

        System.out.println("KBPEALApp: num docs " + documents.size());
        ParseStanfordFigerReverb sys = ParseStanfordFigerReverb.instance();
        Preprocessing.initPipeline();

        NewsSpikeSentencePredict nssp = new NewsSpikeSentencePredict();
        //StanfordCoreNLP annotator = new StanfordAnnotatorHelperMethods().getBasicPipeline();
        KBPTaxonomy kbpTaxonomy = new KBPTaxonomy();
        File writeDir = new File(outputDirectory);
 
        int docCount = 0;

        for(KBPDocument document: documents){

            docCount += 1;
            String docId = document.getDocId();

            System.out.println("-------------------------------------------"); 
            System.out.println("doc #: " + docCount + " docId: " + docId); 
            System.out.println("-------------------------------------------"); 

            try {

                List<CoreLabel> docTokens = new ArrayList<CoreLabel>();
                //String docString = document.getRawText();
                String docString = document.getCleanXMLString();
                String testString = "Barack Obama was born in Honolulu.";
                //String testString = "Cipriani left Ensisheim.";
                //String testString = "Cipriani left Ensisheim in a police vehicle bound for an open prison near Strasbourg where the police officer said he was due to do community service including working at a food bank as part of his parole.";
                //Annotation annotation = new Annotation(testString);
                //Preprocessing.pipeline.annotate(annotation);
                Annotation annotation = new Annotation(docString);

                // --------------------------
                // Set docDate
                // --------------------------
                String docDate = document.getDocDate();
                System.out.println("DocDate: " + docDate);
                if(!docDate.equals("noDate")){
                    annotation.set(DocDateAnnotation.class, docDate);
                }
                // --------------------------
                // Annotate
                // --------------------------
                Preprocessing.pipeline.annotate(annotation);
                ////old: annotator.annotate(annotation);
                //List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);            

                List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);            
                //SemanticGraph dependencies1 = sentences.get(0).get(CollapsedDependenciesAnnotation.class);
                 
                //System.out.println("KBPEALApp num sentences: " + sentences.size());
                //System.out.println("KBPEALApp first sent, dependencies: " + dependencies1.toList());                

                //System.exit(1);

                // -------------------------------------
                // Collect the document tokens
                // -------------------------------------
                for(CoreMap sentence : sentences){
                    //String sentenceText = sentence.get(TextAnnotation.class);
                    //int sentenceBeginOffset = tokens.get(0).beginPosition();
                    //System.out.println("KBPEALApp: sentence begin offset: " + sentenceBeginOffset);
                    //System.out.println("KBPEALApp sent text: " + sentenceText);                                        

                    //String treeString = sentence.get(TreeAnnotation.class).toString();
                    //System.out.println("tree: " + treeString);

                    List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);

                    //for(CoreLabel token : tokens){
                    //  System.out.println("token: " + token.tag() + "," + token.word() + "," + token.value() + "," + 
                    //      token.ner() + "," + token.lemma() + 
                    //      "," + token.beginPosition() + "," + token.endPosition()); 
                    //}

                    docTokens.addAll(tokens);
                }

                //System.out.println("KBPEALApp num doc tokens: "  + docTokens.size());
 
                // -------------------------------
                // Call Newsspike
                // -------------------------------               
                List<NewsSpikeRelationPrediction> relations = nssp.predictKBP(annotation,sys);
//                List<NewsSpikeRelationPrediction> relations = nssp.predictKBP(docString);
                System.out.println("RELATIONS: " + relations.size() + " " + (relations == null));

                // --------------------------------- 
                // Examine Relations Output
                // ---------------------------------
                //Note: NewsSpikeRelationPrediction stores sentence token offsets, not sentence char offsets
                for(NewsSpikeRelationPrediction relation: relations){
                    int arg1Start = docTokens.get(relation.getArg1().getStartOffset()).beginPosition();
                    int arg1End = docTokens.get(relation.getArg1().getEndOffset()-1).endPosition()-1;
                    int arg2Start = docTokens.get(relation.getArg2().getStartOffset()).beginPosition();
                    int arg2End = docTokens.get(relation.getArg2().getEndOffset()-1).endPosition()-1;

                    //String arg1CAS = testString.substring(arg1Start, arg1End);
                    //String arg2CAS = testString.substring(arg2Start, arg2End);

                    CoreLabel arg2Token = docTokens.get(relation.getArg2().getStartOffset());
                    String nnet = arg2Token.get(NormalizedNamedEntityTagAnnotation.class);
                    System.out.println("arg2 NormalizedNamedEntityTag: " + nnet);

                    String arg1CAS = docString.substring(arg1Start, arg1End+1);
                    String arg2CAS = docString.substring(arg2Start, arg2End+1);
                    System.out.println("arg1: " + arg1CAS + " " + arg1Start + ":" + arg1End);
                    System.out.println("arg2: " + arg2CAS + " " + arg2Start + ":" + arg2End);

                    System.out.println("relation: " + relation.getRelation());
                    System.out.println("arg1Type: " + relation.getArg1Type());
                    System.out.println("arg2Type: " + relation.getArg2Type());
                    System.out.println("confidence: " + relation.getConfidence());

                }

                // ------------------------------------
                // Create and Write Document Output
                // ------------------------------------

                if(relations != null){

                    DocumentSystemOutput2015 docSystemOutput = createDocumentOutput(relations, docId, docTokens, 
                        docString, kbpTaxonomy, docDate);
                    SystemOutputStore2016 systemOutputStore2016 = SystemOutputStore2016.openOrCreate(writeDir);
                    systemOutputStore2016.write(docSystemOutput);
                    systemOutputStore2016.close();
                }else{

                    String argsFileName = outputDirectory + "arguments/" + docId;
                    String linkingFileName = outputDirectory + "linking/" + docId;
                    BufferedWriter bwArgs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(argsFileName), "UTF-8"));
                    BufferedWriter bwDocEvents = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(linkingFileName), "UTF-8"));
                    bwArgs.close();
                    bwDocEvents.close();                    
                }                


            } catch (Exception e) {
                e.printStackTrace();
                String argsFileName = outputDirectory + "arguments/" + docId;
                String linkingFileName = outputDirectory + "linking/" + docId;
                try{
                    BufferedWriter bwArgs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(argsFileName), "UTF-8"));
                    BufferedWriter bwDocEvents = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(linkingFileName), "UTF-8"));
                    bwArgs.close();
                    bwDocEvents.close();                    
                }catch(Exception e2){
                    e2.printStackTrace();
                }

            }


        }

    }

    public static DocumentSystemOutput2015 createDocumentOutput(List<NewsSpikeRelationPrediction> relations, String docId, 
        List<CoreLabel> docTokens, String docString, KBPTaxonomy kbpTaxonomy, String docDate){

        Symbol docid = Symbol.from(docId);


        Set<Scored<Response>> scoredResponses = new HashSet<Scored<Response>>();
        Set<Response> responses = new HashSet<Response>();
        ImmutableBiMap.Builder<String, ResponseSet> responseSetIdsBuilder = ImmutableBiMap.builder();
        int responseSetId = 0;
        ImmutableSet.Builder<ResponseSet> responseSetsBuilder = ImmutableSet.builder();

        
        for(NewsSpikeRelationPrediction relation: relations){

            System.out.println("relation: " + relation.getRelation());
            double relationConfidence = relation.getConfidence();

            List<LabelTriple> eventTypeRoles = kbpTaxonomy.newsSpikeToTAC2016EventTripleLabel(relation.getRelation());

            if(eventTypeRoles!=null){

                for(LabelTriple labelTriple : eventTypeRoles){     

                    Symbol arg1Role = Symbol.from(labelTriple.getArg1Role());
                    String arg1Name = relation.getArg1().getArgName();
                    System.out.println("B4 NEWLINE: " + arg1Name);
                   
                    arg1Name = replaceIllegalCharacters(arg1Name);

                    int arg1StartOffset = docTokens.get(relation.getArg1().getStartOffset()).beginPosition();
                    if(arg1Name.startsWith("the") || arg1Name.startsWith("The")){ 
                        arg1StartOffset = arg1StartOffset + 4;                   
                        if(arg1Name.length() >= 5){
                            arg1Name = arg1Name.substring(4);}
                    }

                    System.out.println("B4 HREF: " + arg1Name.contains("href") + " " + arg1Name.contains("<"));
                    if(arg1Name.startsWith("<")){
                        int closeBracketIndex = arg1Name.lastIndexOf(">");
                        System.out.println("CLOSE BRACKET INDEX: " + closeBracketIndex);
                        int newStartIndex = closeBracketIndex + 1;
                        if(arg1Name.length() > newStartIndex){
                            arg1StartOffset = arg1StartOffset + newStartIndex; 
                            arg1Name = arg1Name.substring(newStartIndex);
                        }
                    }
                    int arg1EndOffset = docTokens.get(relation.getArg1().getEndOffset()-1).endPosition()-1;

                    if(docString.length() >= (arg1EndOffset+1)){ 
                        if(docString.substring(arg1StartOffset,arg1EndOffset+1).contains("\n")){
                            int newLineIndex = docString.substring(arg1StartOffset,arg1EndOffset+1).lastIndexOf("\n") + 1;
                            System.out.println("NEWLINE: yes " + newLineIndex);
                            arg1StartOffset = arg1StartOffset + newLineIndex; 
                            arg1Name = docString.substring(arg1StartOffset,arg1EndOffset+1);                        
                        }
                    }

                    CharOffsetSpan arg1Basefiller = new CharOffsetSpan(arg1StartOffset,arg1EndOffset,arg1Name);
                    KBPString arg1CAS = KBPString.from(arg1Name, arg1Basefiller);

                    Symbol arg2Role = Symbol.from(labelTriple.getArg2Role());

                    String arg2Name = relation.getArg2().getArgName();

                    arg2Name = replaceIllegalCharacters(arg2Name);

                    int arg2StartOffset = docTokens.get(relation.getArg2().getStartOffset()).beginPosition();
                    if(arg2Name.startsWith("the") || arg2Name.startsWith("The")){ 
                        arg2StartOffset = arg2StartOffset + 4;                   
                        if(arg2Name.length() >= 5){
                            arg2Name = arg2Name.substring(4);}
                    }
                    if(arg2Name.startsWith("<")){
                        int closeBracketIndex = arg2Name.lastIndexOf(">");
                        System.out.println("CLOSE BRACKET INDEX: " + closeBracketIndex);
                        int newStartIndex = closeBracketIndex + 1;
                        if(arg2Name.length() > newStartIndex){
                            arg2StartOffset = arg2StartOffset + newStartIndex; 
                            arg2Name = arg2Name.substring(newStartIndex);
                        }
                    }
                    int arg2EndOffset = docTokens.get(relation.getArg2().getEndOffset()-1).endPosition()-1;

                   if(docString.length() >= (arg2EndOffset+1)){ 
                       if(docString.substring(arg2StartOffset,arg2EndOffset+1).contains("\n")){
                            int newLineIndex = docString.substring(arg2StartOffset,arg2EndOffset+1).lastIndexOf("\n") + 1;
                            System.out.println("NEWLINE: yes " + newLineIndex);
                            arg2StartOffset = arg2StartOffset + newLineIndex; 
                            arg2Name = docString.substring(arg2StartOffset,arg2EndOffset+1);                        
                       }
                    }
 
                    CharOffsetSpan arg2Basefiller = new CharOffsetSpan(arg2StartOffset,arg2EndOffset,arg2Name);
                    KBPString arg2CAS = KBPString.from(arg2Name, arg2Basefiller);

                    Boolean timeNormalized = false;

                    if(arg2Role.asString().equals("Time")) {
                        CoreLabel token = docTokens.get(relation.getArg2().getStartOffset());  
                        String normalizedTime = token.get(NormalizedNamedEntityTagAnnotation.class);
                        System.out.println("KBPEALApp Time NNET: " + normalizedTime);
                        // sutime values: 
                        // 20: THIS P1W OFFSET P-1W (last week, w/no docDate)                         
                        // 8: THIS P1D (today w/no docDate)
                        // 20: THIS P1M OFFSET P-1M
                        // 10: 2013-12-05
                        // 10: XXXX-WXX-1       

                        if(normalizedTime != null && normalizedTime.length() > 0){

                            if(normalizedTime.length() == 10){
                                String[] normalizedTimeSplit = normalizedTime.split("-");
                                if(normalizedTimeSplit.length==3 && normalizedTimeSplit[1].length()==2 && normalizedTimeSplit[2].length()==2){
                                    arg2CAS = KBPString.from(normalizedTime, arg2Basefiller);
                                    timeNormalized = true;
                                }
                            }
                            else if(normalizedTime.length() == 7) {
                                arg2CAS = KBPString.from(normalizedTime + "-XX", arg2Basefiller);                                
                                timeNormalized = true;
                            }
                            else if(normalizedTime.length() == 4) {
                                arg2CAS = KBPString.from(normalizedTime + "-XX-XX", arg2Basefiller);                                
                                timeNormalized = true;
                            }
                            else if(normalizedTime.length() == 8) {
                                //case: last week, next week
                                if(docDate != null && !docDate.equals("noDate")){
                                    arg2CAS = KBPString.from(computeDateString(docDate, arg2Name), arg2Basefiller);                                
                                    timeNormalized = true;
                                }
                            }
                            else if(normalizedTime.length() == 13){
                                arg2CAS = KBPString.from(normalizedTime.substring(0,10), arg2Basefiller);
                                timeNormalized = true;
                            } 

                        }
                    } 
                    System.out.println("KBPEALApp Time arg2CAS: " + arg2CAS.string());
    

                    int minStartOffset = Math.min(arg1StartOffset,arg2StartOffset);
                    int maxEndOffset = Math.max(arg1EndOffset,arg2EndOffset);
                    String spanText = docString.substring(minStartOffset,maxEndOffset+1);

                    CharOffsetSpan pj = new CharOffsetSpan(minStartOffset,maxEndOffset,spanText);
                    Symbol type = Symbol.from(labelTriple.getRelation());
                    KBPRealis realis = KBPRealis.parse("ACTUAL");

                    Response arg1Response = Response.builder()
                        .docID(docid)
                        .type(type)
                        .role(arg1Role)
                        .canonicalArgument(arg1CAS)
                        .baseFiller(arg1Basefiller)
                        .addPredicateJustifications(pj)
                        .realis(realis)
                        .build();

                    Response arg2Response = Response.builder()
                        .docID(docid)
                        .type(type)
                        .role(arg2Role)
                        .canonicalArgument(arg2CAS)
                        .baseFiller(arg2Basefiller)
                        .addPredicateJustifications(pj)
                        .realis(realis)
                        .build();
            

                    ResponseSet.Builder responseSetBuilder = ResponseSet.builder();
  
                    responses.add(arg1Response);
                    scoredResponses.add(Scored.from(arg1Response,relationConfidence));
                    responseSetBuilder.addResponses(arg1Response);


                    if(arg2Role.asString().equals("Time")) {                    
                        if(timeNormalized){
                            responses.add(arg2Response);      
                            scoredResponses.add(Scored.from(arg2Response,relationConfidence));
                            responseSetBuilder.addResponses(arg2Response);
                            System.out.println("KBPEALApp: adding time"); 
                        }
                        else{
                            System.out.println("KBPEALApp: not adding time"); 
                        }
                    }
                    else if(arg2Role.asString().equals("None")) {                    
                        System.out.println("KBPEALApp: not adding None");
                    }                    
                    else{
                        responses.add(arg2Response);
                        scoredResponses.add(Scored.from(arg2Response,relationConfidence));
                        responseSetBuilder.addResponses(arg2Response);
                    }


                    ResponseSet responseSet = responseSetBuilder.build();

                    responseSetsBuilder.add(responseSet);

                    responseSetId = responseSetId + 1;
                    responseSetIdsBuilder.put(String.valueOf(responseSetId), responseSet);

                } //for each tripleLabel

            } //if event types

        } //for each relation

        //ArgumentOutput argumentOutput = ArgumentOutput.createWithConstantScore(docid, responses, 1.0);
        ArgumentOutput argumentOutput = ArgumentOutput.from(docid, scoredResponses);


        ImmutableSet<ResponseSet> responseSets = responseSetsBuilder.build();
        ImmutableBiMap<String, ResponseSet> responseSetIds = responseSetIdsBuilder.build();
        Optional<ImmutableBiMap<String, ResponseSet>> responseSetIdsOptional = Optional.of(responseSetIds);


        ResponseLinking responseLinking = ResponseLinking.builder()
                  .docID(docid)
                  .responseSets(responseSets)
                  .responseSetIds(responseSetIdsOptional)
                  .build();


        DocumentSystemOutput2015 docSystemOutput = DocumentSystemOutput2015.from(argumentOutput, responseLinking);
        

        return docSystemOutput;

    }

    public static String computeDateString(String docDate, String arg2Name) {
    
        String dateString = "XXXX-XX-XX";

        Calendar calendar = new GregorianCalendar(Integer.parseInt(docDate.substring(0,4)),
            Integer.parseInt(docDate.substring(5,7)),Integer.parseInt(docDate.substring(8,10)));

        System.out.println("CALENDAR: " + calendar.get(Calendar.YEAR) + "=" +
            calendar.get(Calendar.MONTH) + "=" + calendar.get(Calendar.DAY_OF_MONTH));

        if(arg2Name.toLowerCase().contains("last week")){

            calendar.add(Calendar.DAY_OF_MONTH, -7);
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH));
            if(month.length() == 1) month = "0" + month;
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            if(day.length() == 2) { day = day.substring(0,1) + "X";
            } else if (day.length() == 1) { day = "0X" ; }            
            dateString = year + "-" + month + "-" + day; 

        } else if(arg2Name.toLowerCase().contains("next week")){
 
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH));
            if(month.length() == 1) month = "0" + month;
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            if(day.length() == 2) { day = day.substring(0,1) + "X";
            } else if (day.length() == 1) { day = "0X" ; }            
            dateString = year + "-" + month + "-" + day; 
           
        }      

        return dateString;

    }
 
    public static String replaceIllegalCharacters(String string) {
        return string.replace("\r\n", " ").replace("\n", " ").replace("\t", " ");
    }


    public static void testBBNClasses(List<KBPDocument> documents, String outputDirectory){

        Symbol type = Symbol.from("Life.Die");
        Symbol role = Symbol.from("Victim");
        Symbol docid = Symbol.from("doc1");
        CharOffsetSpan basefiller = new CharOffsetSpan(0,11, "nothing");
        CharOffsetSpan pj = new CharOffsetSpan(0,31, "nothing");
        KBPString cas = KBPString.from("Barack Obama", basefiller);
        KBPRealis realis = KBPRealis.parse("actual");
        Response response = Response.builder()
            .docID(docid)
            .type(type)
            .role(role)
            .canonicalArgument(cas)
            .baseFiller(basefiller)
            .addPredicateJustifications(pj)
            .realis(realis)
            .build();

        System.out.println("Response: " + response.docID());
        System.out.println("Response: " + response.type());
        System.out.println("Response: " + response.role());
        System.out.println("Response: " + response.canonicalArgument());
        System.out.println("Response: " + response.baseFiller());
        System.out.println("Response: " + response.realis());
        System.out.println("Response: " + response.uniqueIdentifier());
    
        basefiller = new CharOffsetSpan(0,13, "nothing");
        cas = KBPString.from("Michelle Obama", basefiller);
        Response response2 = Response.builder()
            .docID(docid)
            .type(type)
            .role(role)
            .canonicalArgument(cas)
            .baseFiller(basefiller)
            .addPredicateJustifications(pj)
            .realis(realis)
            .build();

        System.out.println("Response: " + response2.docID());
        System.out.println("Response: " + response2.type());
        System.out.println("Response: " + response2.role());
        System.out.println("Response: " + response2.canonicalArgument());
        System.out.println("Response: " + response2.baseFiller());
        System.out.println("Response: " + response2.realis());
        System.out.println("Response: " + response2.uniqueIdentifier());

        Set<Response> responses = new HashSet<Response>();
        responses.add(response);
        responses.add(response2);

        //this code works, but isn't what ArgumentOutput expects
        ResponseSet.Builder responseSetBuilder = ResponseSet.builder();
        responseSetBuilder.addResponses(response);
        responseSetBuilder.addResponses(response2);
        ResponseSet responseSet = responseSetBuilder.build();

        ImmutableSet.Builder<ResponseSet> responseSetsBuilder = ImmutableSet.builder();
        responseSetsBuilder.add(responseSet);
        ImmutableSet<ResponseSet> responseSets = responseSetsBuilder.build();

        Map<Response, Double> confidences = new HashMap<Response, Double>();
        Map<Response, String> metadata = new HashMap<Response, String>();

        ArgumentOutput argumentOutput = ArgumentOutput.createWithConstantScore(docid, responses, 1.0);
        //ArgumentOutput.Builder argumentOutputBuilder = ArgumentOutput.Builder(docid, responses, confidences, metadata);
        //ArgumentOutput argumentOutput = argumentOutputBuilder.build();
        //ArgumentOutput argumentOutput = new ArgumentOutput.Builder(docid, ResponseSet.from(responses), confidences, metadata).build();

        ImmutableBiMap.Builder<String, ResponseSet> responseSetIdsBuilder = ImmutableBiMap.builder();
        responseSetIdsBuilder.put("1", responseSet);
        ImmutableBiMap<String, ResponseSet> responseSetIds = responseSetIdsBuilder.build();
        Optional<ImmutableBiMap<String, ResponseSet>> responseSetIdsOptional = Optional.of(responseSetIds);


        ResponseLinking responseLinking = ResponseLinking.builder()
                  .docID(docid)
                  .responseSets(responseSets)
                  .responseSetIds(responseSetIdsOptional)
                  .build();

        System.out.println("arguments docid: " + argumentOutput.docId());
        System.out.println("linking docid: " + responseLinking.docID());


        try{

        File writeDir = new File("/projects/WebWare9/nhawkins/ealOutput/test/");

        DocumentSystemOutput2015 docSystemOutput = DocumentSystemOutput2015.from(argumentOutput, responseLinking);
        SystemOutputStore2016 systemOutputStore2016 = SystemOutputStore2016.openOrCreate(writeDir);
        //SystemOutputStore2016 systemOutputStore2016 = SystemOutputStore2016.getInstance();
        systemOutputStore2016.write(docSystemOutput);

        }catch(IOException e){

        }

    }

}

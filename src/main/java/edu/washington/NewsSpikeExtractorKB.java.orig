package edu.washington;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.lang.Math;

import adept.io.Reader;
import org.apache.commons.io.input.BOMInputStream;

import adept.common.Argument;
import adept.common.CharOffset;
import adept.common.Chunk;
import adept.common.Document;
import adept.common.DocumentEvent;
import adept.common.DocumentEventArgument;
import adept.common.Entity;
import adept.common.EntityMention;
import adept.common.EventMention;
import adept.common.EventMentionArgument;
import adept.common.EventText;
import adept.common.HltContentContainer;
import adept.common.Passage;
import adept.common.Relation;
import adept.common.Token;
import adept.common.TokenOffset;
import adept.common.TokenStream;
import adept.common.TokenizerType;
import adept.common.Type;
import adept.module.AbstractModule;
import adept.module.AdeptModuleException;
import adept.module.IDocumentProcessor;
import adept.module.RelationExtractor;

import adept.metadata.SourceAlgorithm;

import edu.washington.multir2.RelationPrediction;
import edu.washington.nsre.extraction.NewsSpikeRelationPrediction;
import edu.washington.nsre.NewsSpikeSentencePredictKB;
import edu.washington.nsre.LabelTriple;


public class NewsSpikeExtractorKB extends AbstractModule implements IDocumentProcessor{

	private edu.washington.nsre.NewsSpikeSentencePredictKB nssp;       
	private HashMap<String, List<LabelTriple>> tripleMapping;

	public NewsSpikeExtractorKB(String nsre_dir) {
		nssp = new edu.washington.nsre.NewsSpikeSentencePredictKB(nsre_dir);
	}

	public NewsSpikeExtractorKB() {
		nssp = new edu.washington.nsre.NewsSpikeSentencePredictKB();
	}

        @Override
        public HltContentContainer process (Document document, HltContentContainer hltContentContainerIn)
          throws AdeptModuleException {

          HltContentContainer hltContentContainerOut = process(document);

          return hltContentContainerOut;
        }
	
	@Override
	public void process (HltContentContainer hltContentContainerIn) 
          throws AdeptModuleException {
/*
		List<Relation> relations = new ArrayList<Relation>();

//		try {
			TokenStream tokenStream = document
					.getDefaultTokenStream();
			List<int[]> tokenStartEnds = Util
					.getTokenStartEndAbsoluteOffset(tokenStream);
			// for (int[] se : tokenStartEnds) {
			// System.out.println("se\t" + se[0] + "\t" + se[1]);
			// }
			String text = tokenStream.getTextValue();
			List<RelationPrediction> predicts = new ArrayList<RelationPrediction>();
			try {
				predicts = multir.predict(document,tokenStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			boolean haveRelations = false;
			for (RelationPrediction rp : predicts) {
				Relation r = convertMultirRelationPrediction(rp,tokenStream);
				if (r != null) {
					relations.add(r);
					haveRelations = true;
				}				
			}
			if ( ! haveRelations ){
				// Add the document into the HLTCC anyway, so that XSLT can display it as HTML.
				TokenStream ts = document.getTokenStreamList().get(0);
				TokenOffset to = new TokenOffset(0, ts.size()-1);
				Passage p = new Passage( 0, to, ts );
				List<Passage> pList = new ArrayList<Passage>();
				pList.add(p);
				hltContentContainerIn.setPassages( pList );				
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		hltContentContainerIn.setRelations(relations);
		return hltContentContainerIn;
*/
	}

	@Override
	public HltContentContainer process (Document document) 
          throws AdeptModuleException {

//                System.out.println("NSE:process...");

          	tripleMapping = createLabelMapping();

		List<DocumentEvent> relations = new ArrayList<DocumentEvent>();
                HltContentContainer hltContentContainerIn = new HltContentContainer();

//		try {
			TokenStream tokenStream = document
					.getDefaultTokenStream();

                        //System.out.println("NSEkb: ts: " + tokenStream.size());
                        int tokenCount = 0;
                        //for(Token token: tokenStream){
                        //    tokenCount++;
                        //    System.out.println(token.getValue() + " " + tokenCount);
                        //}

// This one isn't used?
			List<int[]> tokenStartEnds = Util
					.getTokenStartEndAbsoluteOffset(tokenStream);
			String text = tokenStream.getTextValue();
                        
//                        System.out.println("NSE: TEXT: " + text);

			List<NewsSpikeRelationPrediction> predicts = new ArrayList<NewsSpikeRelationPrediction>();

                        // ---------------------------------------  
                        // predict relation using newsspike
                        // ---------------------------------------
			try {
				predicts = nssp.predict(text);

//                                System.out.println("NSE Size of predicts: " + predicts.size());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			boolean haveRelations = false;
			for (NewsSpikeRelationPrediction rp : predicts) {

//                                System.out.println("rp arg1: " + rp.getArg1().getArgName());
//                                System.out.println("rp arg2: " + rp.getArg2().getArgName());
//                                System.out.println("rp rel: "  + rp.getRelation());
//                                System.out.println("rp conf: " + rp.getConfidence());

				List<DocumentEvent> docEvents = convertNewsSpikeRelationPrediction(rp,tokenStream);
				for (DocumentEvent r : docEvents){
				if (r != null) {
					relations.add(r);
					haveRelations = true;
				}				
			}
			}
			if ( ! haveRelations ){
				// Add the document into the HLTCC anyway, so that XSLT can display it as HTML.
				TokenStream ts = document.getTokenStreamList().get(0);
				TokenOffset to = new TokenOffset(0, ts.size()-1);
				Passage p = new Passage( 0, to, ts );
				List<Passage> pList = new ArrayList<Passage>();
				pList.add(p);
				hltContentContainerIn.setPassages( pList );				
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		hltContentContainerIn.setDocumentEvents(relations);
		return hltContentContainerIn;

	}
	
	private List<DocumentEvent> convertNewsSpikeRelationPrediction(NewsSpikeRelationPrediction rp, TokenStream tokenStream) {
		
		edu.washington.multir2.Argument newsspikeArg1 = rp.getArg1();
		edu.washington.multir2.Argument newsspikeArg2 = rp.getArg2();

		List<DocumentEvent> docEvents = new ArrayList<DocumentEvent>();
		System.out.println("Relation: " + rp.getRelation());

		List<LabelTriple> newLabels = tripleMapping.get(rp.getRelation());
//		System.out.println("Argument 1 " + newsspikeArg1.getArgName() + " " + newsspikeArg1.getStartOffset() + ":" + newsspikeArg1.getEndOffset());
//		System.out.println("Relation " + rp.getRelation());
//		System.out.println("Argument 2 " + newsspikeArg2.getArgName() + " " + newsspikeArg2.getStartOffset() + ":" + newsspikeArg2.getEndOffset());
		try{
			
			float confidence = (float)rp.getConfidence();
			//Relation rel = new Relation(rp.getRelationID(), new Type(rp.getRelation()));
			//rel.setConfidence(confidence);
                        System.out.println("Confidence: " + confidence);
			
			//Argument arg1 = new Argument(new Type(rp.getArg1().getArgName()), 1);
			//Argument arg2 = new Argument(new Type(rp.getArg2().getArgName()), 1);

			//Type arg1Role = new Type(rp.getArg1().getArgName());
			//Type arg2Role = new Type(rp.getArg2().getArgName());
			

			for (LabelTriple newLabel : newLabels){

				HashMap<String, String> entityMap = new HashMap<String, String>();
				entityMap.put("PERSON", "per");
				entityMap.put("GPE", "gpe");
				entityMap.put("ORGANIZATION", "org");
				entityMap.put("LOCATION", "loc");
				entityMap.put("FACILITY", "fac");
                                // adding NUMBER to avoid throwing exception here
				entityMap.put("NUMBER", "num");
                                entityMap.put("DATE", "date");
                                entityMap.put("MONEY", "mon");
                                entityMap.put("WEEKDAY", "wkdy");
                                entityMap.put("TIME", "time");
                                        
				//HashMap<String, String> eventMap = new HashMap<String, String>();
				//eventMap.put("sue@/organization@/organization", "sue");

                                System.out.println("NSEkb event type: " + newLabel.getRelation());
                                System.out.println("NSEkb arg1 type: " + rp.getArg1Type());
                                System.out.println("NSEkb arg2 type: " + rp.getArg2Type());


				// Edited by Colin
				//Type arg1Role = new Type(rp.getArg1Type());
				//Type arg2Role = new Type(rp.getArg2Type());
				Type arg1Role = new Type(entityMap.get(rp.getArg1Type()));
				Type arg2Role = new Type(entityMap.get(rp.getArg2Type()));
				//Type arg1Role = new Type(newLabel.getArg1Role());
				//Type arg2Role = new Type(newLabel.getArg2Role());
				System.out.println("################################################");
				System.out.println("type: " + arg1Role.getType());
				System.out.println("type: " + arg2Role.getType());

				Entity arg1 = new Entity(1, arg1Role);
				Entity arg2 = new Entity(2, arg2Role);
				
				//Type eventType = new Type(rp.getRelation());
				//Type eventType = new Type(eventMap.get(rp.getRelation()));
				Type eventType = new Type(newLabel.getRelation());

				TokenOffset arg1TokenOffset = getTokenOffset(rp.getArg1(),tokenStream);
				TokenOffset arg2TokenOffset = getTokenOffset(rp.getArg2(),tokenStream);
                                Integer spanStartOffset = new Integer(Math.min(arg1TokenOffset.getBegin(), arg2TokenOffset.getBegin()));
                                Integer spanEndOffset = new Integer(Math.max(arg1TokenOffset.getEnd(), arg2TokenOffset.getEnd()));                                
                                TokenOffset spanTokenOffset = new TokenOffset(spanStartOffset,spanEndOffset);

				EntityMention arg1mention = new EntityMention(0, arg1TokenOffset, tokenStream);
				arg1mention.addEntityConfidencePair(arg1.getEntityId(), confidence);
				arg1mention.setMentionType(arg1Role);
				arg1mention.setSourceAlgorithm(new SourceAlgorithm("NewsSpike", "Washington"));

				EntityMention arg2mention = new EntityMention(1, arg2TokenOffset, tokenStream);
				arg2mention.addEntityConfidencePair(arg2.getEntityId(), confidence);
				arg2mention.setMentionType(arg2Role);
				arg2mention.setSourceAlgorithm(new SourceAlgorithm("NewsSpike", "Washington"));

				arg1.setCanonicalMentions(arg1mention);
				arg1.setEntityConfidence(confidence);
				arg1.setCanonicalMentionConfidence(confidence);

				arg2.setCanonicalMentions(arg2mention);
				arg2.setEntityConfidence(confidence);
				arg2.setCanonicalMentionConfidence(confidence);

				Chunk arg1Chunk = new Chunk(arg1TokenOffset, tokenStream);
				Chunk arg2Chunk = new Chunk(arg2TokenOffset, tokenStream);
                                Chunk spanChunk = new Chunk(spanTokenOffset, tokenStream);
				
				EventMention.Builder eventMentionBuilder = EventMention.builder(eventType);
				eventMentionBuilder.setScore(confidence);
				eventMentionBuilder.setProvenance(EventText.builder(eventType, spanChunk)
					.setScore(confidence).build());
				// not sure if this is correct

				EventMentionArgument mentionArg1 = EventMentionArgument
					.builder(eventType, arg1Role, arg1Chunk)
					.setScore(confidence).build();
				eventMentionBuilder.addArgument(mentionArg1);

				EventMentionArgument mentionArg2 = EventMentionArgument
					.builder(eventType, arg2Role, arg2Chunk)
					.setScore(confidence).build();
				eventMentionBuilder.addArgument(mentionArg2);
				
				EventMention eventMention = eventMentionBuilder.build();

				DocumentEvent.Builder documentEventBuilder = DocumentEvent.builder(eventType);
				documentEventBuilder.addProvenanceFromEventMention(eventMention);
				documentEventBuilder.setScore(confidence);
				DocumentEventArgument.Builder arg1Builder = 
DocumentEventArgument.builder(eventType,
					new Type(newLabel.getArg1Role()), 
DocumentEventArgument.Filler.fromEntity(arg1)).setScore(confidence);
				
arg1Builder.addProvenance(DocumentEventArgument.Provenance.fromArgumentOfEventMention(
					eventMention, mentionArg1));
				documentEventBuilder.addArgument(arg1Builder.build());

				DocumentEventArgument.Builder arg2Builder = 
DocumentEventArgument.builder(eventType,
					new Type(newLabel.getArg2Role()), 
DocumentEventArgument.Filler.fromEntity(arg2)).setScore(
					confidence);
				
arg2Builder.addProvenance(DocumentEventArgument.Provenance.fromArgumentOfEventMention(
					eventMention, mentionArg2));
				documentEventBuilder.addArgument(arg2Builder.build());

				DocumentEvent documentEvent = documentEventBuilder.build();

				docEvents.add(documentEvent);
			}
		

			//arg1.addArgumentConfidencePair(arg1Chunk,confidence);
			//arg2.addArgumentConfidencePair(arg2Chunk, confidence);
			
			//rel.addArgument(arg1);
			//rel.addArgument(arg2);
			
			return docEvents;
		}
		catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}


	private HashMap<String, List<LabelTriple>> createLabelMapping(){
		HashMap<String, List<LabelTriple>> mapping = new HashMap<String, List<LabelTriple>>();

		String mappingFile = "edu/washington/NewsSpikeToExtendedRERE.csv";
		String csvFile = "";
		try{
        csvFile = Reader.getAbsolutePathFromClasspathOrFileSystem(mappingFile);
        System.out.println("file" + csvFile);
    } catch (FileNotFoundException e){
    	e.printStackTrace();
    }
        BufferedReader br = null;
        String line = "";
        try{
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {

			// use comma as separator
			String[] line_array = line.split(",");

			if (mapping.containsKey(line_array[0])){
				mapping.get(line_array[0]).add(new LabelTriple(line_array[1], line_array[2], 
line_array[3]));
			} else{
				List<LabelTriple> labels = new ArrayList<LabelTriple>();
				labels.add(new LabelTriple(line_array[1], line_array[2], line_array[3]));
				mapping.put(line_array[0], labels);
			}
			

		}

		} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		//List<LabelTriple> triples = new ArrayList<LabelTriple>();
		//triples.add(new LabelTriple("sue", "sue.plaintiff", "sue.defendant"));
		//mapping.put("sue@/organization@/organization", triples);
		
		return mapping;

	}

        private TokenOffset getTokenOffset(edu.washington.multir2.Argument theArgument, TokenStream 
tokenStream){
                int tokenStart = -1;
                int tokenEnd = -1;

//                System.out.println("NSE GTO text length: " + tokenStream.getTextValue().length());
                        
                Integer theArgumentStartOffset = theArgument.getStartOffset();
                Integer theArgumentEndOffset = theArgument.getEndOffset() - 1;

//                System.out.println("NSE GTO arg start: " + theArgumentStartOffset);
//                System.out.println("NSE GTO arg end: " + theArgumentEndOffset);
  
// for NewsSpike, theArgumentStartOffset and theArgumentEndOffset are already token offsets
                                      
//                for(int i =0; i < tokenStream.size(); i++){
//                        Token t = tokenStream.get(i);
//                        CharOffset co = t.getCharOffset();
//                        if(co.getBegin() == theArgumentStartOffset){
//                                tokenStart = i;
//                        }
//                        if(co.getEnd() == theArgumentEndOffset){
//                                tokenEnd = i;
//                                break;
//                        }
//                }
                        
//                return new TokenOffset(tokenStart,tokenEnd);
                  return new TokenOffset(theArgumentStartOffset,theArgumentEndOffset);

        }


	@Override
	public long processAsync(Document document, HltContentContainer hltContentContainer)
			throws AdeptModuleException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long processAsync(HltContentContainer hltContentContainer)
			throws AdeptModuleException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long processAsync(Document document)
			throws AdeptModuleException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean tryGetResult(long requestId,
			HltContentContainer hltContentContainer)
			throws AdeptModuleException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public edu.washington.nsre.NewsSpikeSentencePredictKB getNewsSpikeSentencePredictor(){
		return nssp;
	}


}


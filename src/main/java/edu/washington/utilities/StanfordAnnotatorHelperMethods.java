package edu.washington.utilities;


import java.util.Properties;


import edu.stanford.nlp.pipeline.StanfordCoreNLP;



public class StanfordAnnotatorHelperMethods {


    private final StanfordCoreNLP basicPipeline;
    private final StanfordCoreNLP cleanXMLPipeline;


    public StanfordAnnotatorHelperMethods(){
                Properties basicProps = new Properties();
                Properties cleanXMLProps = new Properties();
                basicProps.put("annotators", "tokenize, ssplit"); 
                cleanXMLProps.put("annotators", "tokenize, cleanxml, ssplit"); 
                cleanXMLProps.put("clean.xmltags", ".*");
                this.basicPipeline = new StanfordCoreNLP(basicProps);
                this.cleanXMLPipeline = new StanfordCoreNLP(cleanXMLProps);

                //suTimeProps.put("annotators", "tokenize, ssplit, pos, lemma, cleanxml, ner");
                //suTimeProps.put("sutime.binders", "0");
                //suTimeProps.put("clean.datetags","datetime|date|dateline");
                //this.suTimePipeline = new StanfordCoreNLP(suTimeProps);
    }


    public StanfordCoreNLP getBasicPipeline(){return basicPipeline;}

    public StanfordCoreNLP getCleanXMLPipeline(){return cleanXMLPipeline;}


}



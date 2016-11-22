package edu.washington.utilities;

//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.ling.CoreLabel;
//import edu.stanford.nlp.util.CoreMap;

//import edu.stanford.nlp.simple.Document;

import org.w3c.dom.Document;
import edu.stanford.nlp.util.XMLUtils;


public class CoreNLPXMLLoader{





    public static void readCoreNLPXMLFile(String filename) {

        try{
            Document document = XMLUtils.readDocumentFromFile(filename);

        //Annotation annotation = document.asAnnotation();

        //return annotation;

            System.out.println("Document: " + document.getXmlVersion());
        } catch (Exception e){
            e.printStackTrace();
            
        }



    }


}

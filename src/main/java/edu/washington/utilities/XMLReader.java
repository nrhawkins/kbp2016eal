package edu.washington.utilities;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class XMLReader {

    
    public static void readXMLFileIntoDocument(String filename){

       try{

        File inputFile = new File(filename);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputFile);

        NodeList sentences = doc.getElementsByTagName("sentence");

        System.out.println("Document: " + doc.getXmlVersion());
        System.out.println("Sentences #: " + sentences.getLength());

       }catch(Exception e){
           e.printStackTrace();    
       }


    }        

}

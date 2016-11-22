package edu.washington.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.io.FileNotFoundException;
import java.io.InputStream;

import java.net.URL;

import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.commons.io.input.BOMInputStream;



public class Reader {

    //public static String getDocID(String filename) {
    //}

    public static String fileToNoXMLTagsString(String filename) {
        String spaces50 = "                                                  ";
        String spaces250 = spaces50+spaces50+spaces50+spaces50+spaces50;
        String spaces500 = spaces250+spaces250;
        System.out.println("spaces 50 length: " + spaces50.length());
        System.out.println("spaces 500 length: " + spaces500.length());
        String lines = "";
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
            while ((line = in.readLine()) != null) {
               //replace these lines with spaces
               if(line.startsWith("<doc") | line.startsWith("</doc") | line.startsWith("<headline>") | 
                  line.startsWith("<post") | line.startsWith("</post") | line.startsWith("<quote") | line.startsWith("</quote") | 
                  line.startsWith("<img") | line.startsWith("<a href") | line.startsWith("[/url") | line.startsWith("<DOC") | 
                  line.startsWith("</DOC") | line.startsWith("<HEADLINE") | 
                  line.startsWith("<DATELINE") | line.startsWith("</DATELINE>") | line.startsWith("<KEYWORD>") | 
                  line.startsWith("</KEYWORD>") |  
                  line.startsWith("<P>") | line.startsWith("</P>") | line.startsWith("<TEXT>") | line.startsWith("</TEXT>")) {

                   int lengthLine = line.length();
                   if(lengthLine <= 500){
                      String newLine = spaces500.substring(0,lengthLine);  
                      lines = lines + newLine + "\n";                   
                   }
                   else{
                      lines = lines + line + "\n";                       
                   }
               }
               else if(line.startsWith("</headline>") | line.startsWith("</HEADLINE>")){
                      lines = lines + ".          " + "\n";
               }
               else{
                   lines = lines + line + "\n";                       
               }
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }


    public static String fileToString(String filename) {
        String lines = "";
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
            while ((line = in.readLine()) != null) {
                lines = lines + line + "\n";
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }



    public static List<String> getInputFileNames(String inputFileNamesFileName){

        List<String> inputFileNames = new ArrayList<String>();
        String l;
        try { 
            BufferedReader br = new BufferedReader(new FileReader(inputFileNamesFileName));
            while((l = br.readLine()) != null ){
                inputFileNames.add(l);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
            
        return inputFileNames;        

    }


    public static org.w3c.dom.Document readXML(String path) {
        try {
            InputStream is = findStreamInClasspathOrFileSystem(path);
            BOMInputStream bis = new BOMInputStream(is, false);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(bis);
            // TODO - might have backslashes.
            doc.setDocumentURI(path);
            return doc;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }


    public static BOMInputStream findStreamInClasspathOrFileSystem(String name) throws FileNotFoundException {
        InputStream is = Reader.class.getClassLoader().getResourceAsStream(name);
        BOMInputStream bis = new BOMInputStream(is, false);
        if (is == null) {
            is = Reader.class.getClassLoader().getResourceAsStream(name.replaceAll("\\\\", "/"));
            bis = new BOMInputStream(is, false);
            if (is == null) {
                File f = new File(name);
                if (!f.exists()) {
                    System.out.println("Warning - creating FileInputStream: " + f.getAbsolutePath());
                }
                is = new FileInputStream(name);
                bis = new BOMInputStream(is, false);
                //System.out.println("Reading InputStream: " + f.getAbsolutePath());
            } else {
                System.out.println("InputStream found in Class Loader after adjusting separators.");
            }
        } else {
            //System.out.println("InputStream found in Class Loader.");
        }
        return bis;
    }


    public static String cleanXMLString(String docString){

         String spaces50 = "                                                  ";
         String spaces500 = spaces50+spaces50+spaces50+spaces50+spaces50+spaces50+spaces50+spaces50+spaces50+spaces50;        
         System.out.println("length spaces50: " + spaces50.length());
         System.out.println("length spaces500: " + spaces500.length());

         int endDocTagIndex = docString.indexOf(">", 0) + 1;
         String docTagSpaces = spaces500.substring(0, endDocTagIndex);
         System.out.println("endDocTagIndex: " + endDocTagIndex);
         System.out.println("length docTagSpaces: " + docTagSpaces.length());
         String cleanString = docString.replace(docString.substring(0,endDocTagIndex), docTagSpaces);  

         cleanString = cleanString.replace("<HEADLINE>", "          ")  
                                  .replace("</HEADLINE>", "           ")  
                                  .replace("<DATELINE>", "          ") 
                                  .replace("</DATELINE>", "           ")  
                                  .replace("<TEXT>", "      ")  
                                  .replace("</TEXT>", "       ")  
                                  .replace("<P>", "   ")  
                                  .replace("</P>", "    ")  
                                  .replace("</DOC>", "      ")  
                                  .replace("<POST>", "      ")  
                                  .replace("</POST>", "       ")  
                                  .replace("<QUOTE>", "       ")  
                                  .replace("</QUOTE>", "        ");  
                  
         cleanString = cleanString.replace("<headline>", "          ")  
                                  .replace("</headline>", "           ")  
                                  .replace("<dateline>", "          ") 
                                  .replace("</dateline>", "           ")  
                                  .replace("<text>", "      ")  
                                  .replace("</text>", "       ")  
                                  .replace("<p>", "   ")  
                                  .replace("</p>", "    ")  
                                  .replace("</doc>", "      ")  
                                  .replace("<post>", "      ")  
                                  .replace("</post>", "       ")  
                                  .replace("<quote>", "       ")  
                                  .replace("</quote>", "        ")  
                                  .replace("<post", "     ")
                                  .replace("<quote", "      ")
                                  .replace("<a href=", "        ")
                                  .replace("</a>", "    ")
                                  .replace(">", " ");


         System.out.println("docString length: " + docString.length());
         System.out.println("cleanString length: " + cleanString.length());


         return cleanString;

    }


    public static String getAbsolutePathFromClasspathOrFileSystem(String name) throws FileNotFoundException {
        if (name.startsWith("/")) return name;
        //
        // System.out.println("To find the absolute path of: " + name);
        URL url = null;
        try {
            url = Reader.class.getClassLoader().getResource(name);
            if (url != null) return url.toURI().getPath();
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



}


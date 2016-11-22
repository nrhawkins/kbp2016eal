package edu.washington.utilities;


public class KBPDocumentMaker {


   public static String getDocIdFromFileName(String fileName){

       String docId;
       if(fileName.contains("mpdf") || fileName.contains("NW") || fileName.contains("DF")){
           docId =  fileName.split("\\.")[0];
       } else {
           docId = fileName.split("\\.")[0] + "." + fileName.split("\\.")[1];
       }

       return docId;
   }


   public static String getDocIdFromFileNameDotXml(String fileName){

       String docId;
       if(fileName.contains(".mpdf.xml")){
           docId = fileName.split("\\.mpdf\\.xml")[0];
       }
       else{
           docId =  fileName.split("\\.xml")[0];
       }

       return docId;
   }


   public static String getDocDateFromFileName(String fileName){

       String docDate = "noDate";
       String[] docDateParts =  fileName.split("\\_");

       if(fileName.startsWith("ENG")){
           if(docDateParts.length >= 4){
               if(docDateParts[3].length() == 8){
                   docDate = docDateParts[3].substring(0,4) + "-" + docDateParts[3].substring(4,6) + "-" + 
                       docDateParts[3].substring(6,8);
               }
           } 
       }else if(fileName.startsWith("NYT") || fileName.startsWith("XIN")) {
           if(docDateParts.length == 3){
              String[] docDateParts2 = docDateParts[2].split("\\.");
              if(docDateParts2.length >= 1){
                  docDate = docDateParts2[0].substring(0,4) + "-" + docDateParts2[0].substring(4,6) + "-" + docDateParts2[0].substring(6,8);
              }

           } 
       }else if(fileName.startsWith("AFP")) {
           if(docDateParts.length == 3){
              String[] docDateParts2 = docDateParts[2].split("\\.");
              if(docDateParts2.length >= 1){
                  docDate = docDateParts2[0].substring(0,4) + "-" + docDateParts2[0].substring(4,6) + "-" + docDateParts2[0].substring(6,8);
              }

           } 
       }

       return docDate;
   }

}

package edu.washington.kbp;


import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


import edu.washington.io.Reader;
import edu.washington.nsre.LabelTriple;



public class KBPTaxonomy {

     
    private final HashMap<String, List<LabelTriple>> newsSpikeToKBPEAL2016;


    public KBPTaxonomy(){

        newsSpikeToKBPEAL2016 = createNewsSpikeToKBP2016EALMapping();

        System.out.println("constructor: " + newsSpikeToKBPEAL2016.size());

    }


    // --------------------------------------------------------------------------------------------
    // translates a newsspike relation such as: leave@/person@/location
    // to a triple: EventType.Subtype, Arg1Role, Arg2Role
    // the triple is from Table 1: Event Taxonomy in the task description for TAC KBP EAL 2016
    // --------------------------------------------------------------------------------------------

    public List<LabelTriple> newsSpikeToTAC2016EventTripleLabel(String eventType){

        System.out.println("newsSpikeToTAC2016ETL: " + newsSpikeToKBPEAL2016.size());

        List<LabelTriple> labelTriples = null;

        if(newsSpikeToKBPEAL2016.containsKey(eventType)){

            labelTriples = newsSpikeToKBPEAL2016.get(eventType);    

        }       

        return labelTriples;        
    }


    // -----------------------------------------------------------------------------------
    // reads a csv file of the newsspike relation to KBPEAL2016 triple translation
    // -----------------------------------------------------------------------------------

    private HashMap<String, List<LabelTriple>> createNewsSpikeToKBP2016EALMapping(){
                
        HashMap<String, List<LabelTriple>> mapping = new HashMap<String, List<LabelTriple>>();

        String mappingFile = "edu/washington/NewsSpikeToKBPEAL2016.csv";
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

                    mapping.get(line_array[0]).add(new LabelTriple(line_array[1], line_array[2], line_array[3]));

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


}

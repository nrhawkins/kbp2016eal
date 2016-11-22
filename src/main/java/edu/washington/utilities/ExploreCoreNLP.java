package edu.washington.utilities;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;

import edu.washington.cs.figer.analysis.Preprocessing;
import edu.washington.nsre.figer.ParseStanfordFigerReverb;


public class ExploreCoreNLP{


    public static void main(String[] args){

        System.out.println("Explore CoreNLP");

        String text ="Bell, based in Los Angeles, makes and distributes electronic, computer and building products.";
        Annotation annotation = new Annotation(text);

        ParseStanfordFigerReverb sys = ParseStanfordFigerReverb.instance();
        Preprocessing.initPipeline();
        Preprocessing.pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
        CoreMap sentence = sentences.get(0);

        Tree t = sentence.get(TreeAnnotation.class);

        System.out.println("##########################################");

        System.out.println("Tree size: " + t.size());
        System.out.println("Tree value: " + t.value());
        System.out.println("Tree label value: " + t.label().value());
        System.out.println("Tree: " + t.toString());

        System.out.println("##########################################");
         
        List<Tree> children = t.getChildrenAsList();
        System.out.println("Children: " + children.size());

        for(Tree child : children){
            System.out.println("Child: " + child.size());
            System.out.println("Child: " + child.value());
            System.out.println("Child: " + child.label().value());
        }
        
        t.pennPrint();        

        String newTreeString = t.toString();

        Tree newTree = Tree.valueOf(newTreeString);

        System.out.println("##########################################");

        System.out.println("Tree size: " + newTree.size());
        System.out.println("Tree value: " + newTree.value());
        System.out.println("Tree label value: " + newTree.label().value());
        System.out.println("Tree: " + newTree.toString());

        System.out.println("##########################################");

        List<Tree> newChildren = newTree.getChildrenAsList();
        System.out.println("Children: " + newChildren.size());

        for(Tree child : children){
            System.out.println("Child: " + child.size());
            System.out.println("Child: " + child.value());
            System.out.println("Child: " + child.label().value());
        }

        newTree.pennPrint();




    }

}

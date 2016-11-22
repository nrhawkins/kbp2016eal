package edu.washington.nsre.extraction;

import java.util.List;

import adept.common.Token;

import edu.washington.multir2.Argument;

public class NewsSpikeRelationPrediction {
	
	private Argument arg1;
	private Argument arg2;
	private String arg1Type;
	private String arg2Type;
	private String relation;
	private Integer relationID;
	private double confidence;
	
	public NewsSpikeRelationPrediction(
			Argument arg1,
			Argument arg2, 
			String relation,
			Integer relationID,
			double confidence){
		this.arg1=arg1;
		this.arg2=arg2;
		this.relation=relation;
		this.relationID=relationID;
		this.confidence=confidence;
	}
	
		public NewsSpikeRelationPrediction(
			Argument arg1,
			Argument arg2, String arg1Type, String arg2Type,
			String relation,
			Integer relationID,
			double confidence){
		this.arg1=arg1;
		this.arg2=arg2;
		this.arg1Type = arg1Type;
		this.arg2Type = arg2Type;
		this.relation=relation;
		this.relationID=relationID;
		this.confidence=confidence;
	}

	public Argument getArg1(){
		return arg1;
	}
	public Argument getArg2(){
		return arg2;
	}
	public String getArg1Type(){
		return arg1Type;
	}
	public String getArg2Type(){
		return arg2Type;
	}
	public String getRelation(){
		return relation;
	}
	public Integer getRelationID(){
		return relationID;
	}
	public double getConfidence(){
		return confidence;
	}

}


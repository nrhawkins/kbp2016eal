package edu.washington.multir2;

import java.util.List;

import adept.common.Token;

public class RelationPrediction {
	
	private Argument arg1;
	private Argument arg2;
	private String relation;
	private Integer relationID;
	private double confidence;
	
	public RelationPrediction(
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
	
	public Argument getArg1(){
		return arg1;
	}
	public Argument getArg2(){
		return arg2;
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

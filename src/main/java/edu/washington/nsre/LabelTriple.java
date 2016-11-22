package edu.washington.nsre;

public class LabelTriple{
	
	private String relation;
	private String arg1Role;
	private String arg2Role;

	private final String default_text = "NONE";

	public LabelTriple(String relation, String arg1Role, String arg2Role){
		this.relation = relation;
		this.arg1Role = arg1Role;
		this.arg2Role = arg2Role;
	}

	public LabelTriple(){
		this.relation = default_text;
		this.arg1Role = default_text;
		this.arg2Role = default_text;
	}	

	public String getRelation(){
		return relation;
	}

	public String getArg1Role(){
		return arg1Role;
	}

	public String getArg2Role(){
		return arg2Role;
	}	

	public void setRelation(String text){
		relation = text;
	}

	public void setArg1Role(String text){
		arg1Role = text;
	}

	public void setArg2Role(String text){
		arg2Role = text;
	}	

}

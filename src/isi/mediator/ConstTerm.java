// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

package isi.mediator;

public class ConstTerm extends Term{
	
	private String val;
	
	public ConstTerm(){}
	public ConstTerm(String val){
		this.val = val;
	}
	public ConstTerm(String var, String val){
		this.var=var;
		this.val = val;
	}

	public ConstTerm clone(){
		ConstTerm t = new ConstTerm();
		t.val=val;
		t.var=var;
		return t;
	}

	public String getFreeVar(){
		return null;
	}

	public String getTermValue(){
		return val;
	}
	
	public String getVal(){
		return val;
	}
	
	public String getSqlVal(boolean isNumber){
		if(isNumber)
			return getNumberVal();
		else
			return getStringVal();
	}
	
	private String getNumberVal(){
		return val.substring(1, val.length()-1);
	}
	public String getStringVal(){
		String v = val.substring(1, val.length()-1);
		if(v.equals("NULL"))
			return v;
		else
			return "'" + v + "'";
	}

	public void setVal(String v){
		//System.out.println("Set Value " + v);
		val=v;
	}	
		
	public boolean equals(Term t){
		//System.out.println("Equal term : " + this + " and " + t);
		if(!(t instanceof ConstTerm))
			return false;
		if(val.equals(((ConstTerm)t).val))
				return true;
		else return false;
	}
	
	public boolean needsBinding(boolean b){
		return false;
	}

//	public Term unify(Binding binding){
//		return this;
//	}
	
	public String toString(){
		if(var!=null)
			return var + ":" + val;
		else
			return val;
	}

}


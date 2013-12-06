package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

public abstract class Term{
	
	protected String var;

	@Override
	public abstract Term clone();
	@Override
	public abstract String toString();
	public abstract boolean equals(Term t);
	
	//for VarTerm=var & ConstTerm=val
	public String getTermValue(){
		return var;
	}
	
	public String getVar(){
		return var;
	}
	public String getFreeVar(){
		return var;
	}
	public void setVar(String v){
		var=v;
	}

	public void changeVarName(int index){
		if(var!=null)
			var = var + index;
	}

	public String getVal(){return null;}
	
	
}


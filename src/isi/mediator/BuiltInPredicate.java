package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__



//a Predicate can be a normal relation and built-in predicates like equality, lessThan, like, etc. 
//for a normal relation, the "name" is the name of the relation
//for built-in predicates like equality, lessThan, the "name" will be PredicateType 
public class BuiltInPredicate extends Predicate{
	
	public static String EQUALS = "=";
	public static String NOT_EQUAL = "!=";
	public static String LESS_THAN_EQ = "<=";
	public static String LESS_THAN = "<";
	public static String GREATER_THAN_EQ = ">=";
	public static String GREATER_THAN = ">";
	public static String LIKE = "LIKE";
	public static String IS = "IS";
	// (x isnot "NULL") => (x IS NOT NULL)
	public static String ISNOT = "IS NOT";
	public static String OTHER = "other";
	
	private boolean isAssignment=false;
	
	public BuiltInPredicate(String name){
		this.name=name;
	}
	
	@Override
	public BuiltInPredicate clone(){
		BuiltInPredicate p = new BuiltInPredicate(name);
		for(int i=0; i<terms.size(); i++){
			Term t = terms.get(i);
			p.addTerm(t.clone());
		}
		p.isAssignment=isAssignment;
		return p;
	}
	

	public void isAssignment(boolean b){
		isAssignment=b;
	}
	public boolean isAssignment(){
		return isAssignment;
	}
	
	
	@Override
	public String toString(){
		String s = "";
		s += "(" + terms.get(0).toString() + " " + name + " " + terms.get(1).toString() + ")";
		if(isAssignment)
			s += " (assign) ";
		else
			s += " (select) ";
		return s;
	}

}
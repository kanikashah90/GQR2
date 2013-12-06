package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

import java.util.ArrayList;

//a Predicate can a normal relation and built-in predicates like equality, lessThan, like, etc. 
//for a normal relation, the "name" is the name of the relation
//for built-in predicates like equality, lessThan, the "name" will be PredicateType 
public abstract class Predicate{
	
	protected String name;
	protected ArrayList<Term> terms = new ArrayList<Term>();
		
//	private boolean isDomainPredicate = false;
	
	@Override
	public abstract Predicate clone();
	@Override
	public abstract String toString();


	public void addTerm(Term t){
		terms.add(t);
	}
	
	public ArrayList<Term> getTerms(){
		return terms;
	}
	
	//return the first term in "terms" that is equal to t
	protected int findTerm(Term t, int index){
		//System.out.println("Find term " + t + " in " + terms + " starting at " + index);
		for(int i=index; i<terms.size(); i++){
			Term t1 = terms.get(i);
			if(t1.equals(t))
				return i;
		}
		return -1;
	}

	protected boolean containsTerm(Term t1){
		for(int i=0; i<terms.size(); i++){
			Term t2 = terms.get(i);
			if(t1.equals(t2))
				return true;
		}
		return false;
	}
	
	public String getName(){
		return name;
	}

	//get all variables NOT attached to a constant present in this predicate
	public ArrayList<String> getFreeVars(){
		ArrayList<String> vars = new ArrayList<String>();
		for(int i=0; i<terms.size(); i++){
			Term t = terms.get(i);
			String var = t.getFreeVar();
			if(var!=null)
				vars.add(var);
		}
		return vars;
	}

	public ArrayList<String> getVars(){
		ArrayList<String> vars = new ArrayList<String>();
		for(int i=0; i<terms.size(); i++){
			Term t = terms.get(i);
			String var = t.getVar();
			if(var!=null)
				vars.add(var);
		}
		return vars;
	}

	//for the constants just return Vari
	public ArrayList<String> getVarsAndConst(){
		ArrayList<String> vars = new ArrayList<String>();
		for(int i=0; i<terms.size(); i++){
			Term t = terms.get(i);
			String var = t.getVar();
			if(var!=null){
				vars.add(var);
			}
			else{
				vars.add("Const." + name + i);
			}
		}
		return vars;
	}
	
	public boolean equals(Predicate p){
		if(!p.getName().equals(name))
			return false;
		//compare the terms
		for(int i=0; i<terms.size(); i++){
			Term t1 = terms.get(i);
			Term t2 = p.getTerms().get(i);
			//System.out.println("Compare " + t1 + " and " + t2);
			if(!t1.equals(t2))
				return false;
		}
		return true;
	}
	

//	//find all  rules that have as a head this predicate
//	ArrayList<Rule> findAllRules(DomainModel dm){
//		ArrayList<Rule> allRules = new ArrayList<Rule>();
//		boolean foundRule = false;
//		for(int j=0; j<dm.getRules().size(); j++){
//			Rule r = dm.getRules().get(j);
//			if(name.equals(r.getHead().getName())){
//				System.out.println("Same rule ...");
//				//it's the same rule
//				foundRule=true;
//				allRules.add(r);
//			}
//		}
//		if(!foundRule){
//			//see if it's a source predicate
//			SourceSchema schema = dm.getSourceSchema(name);
//			if(schema==null)
//				throw new RuntimeException("Rule:" + name + " not found in domain model");
//		}
//		return allRules;
//	}

}
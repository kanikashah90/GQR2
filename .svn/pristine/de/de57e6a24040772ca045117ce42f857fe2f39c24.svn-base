package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__


import java.util.ArrayList;
import java.util.HashSet;


public class Rule{
	
	//for unique var names
	
	protected Predicate head;
	protected ArrayList<Predicate> conjunctiveFormula = new ArrayList<Predicate>();
	
	public Rule(){}
	
	public void addPredicate(Predicate p){
		conjunctiveFormula.add(p);
	}

	public void addHead(Predicate p){
		head=p;
	}

	public Predicate getHead(){
		return head;
	}
	
	@Override
	public Rule clone(){
		Rule r = new Rule();
		r.head=head.clone();
		for(int i=0; i<conjunctiveFormula.size(); i++){
			Predicate p = conjunctiveFormula.get(i);
			r.conjunctiveFormula.add(p.clone());
		}
		return r;
	}

	protected ArrayList<RelationPredicate> getRelations(){
		ArrayList<RelationPredicate> rels = new ArrayList<RelationPredicate>();
		for(int i=0; i<conjunctiveFormula.size(); i++){
			Predicate p = conjunctiveFormula.get(i);
			if(p instanceof RelationPredicate)
				rels.add((RelationPredicate)p);
		}
		return rels;
	}
	
	protected HashSet<String> getDuplicatePredicates(){
		HashSet<String> predNames = new HashSet<String>();
		for(int i=0; i<conjunctiveFormula.size(); i++){
			Predicate p = conjunctiveFormula.get(i);
			if(containsPredicate(p, i+1))
				predNames.add(p.getName());
		}
		return predNames;
	}
	
	//return true if the conjunctiveForm contains pred with name p.name
	private boolean containsPredicate(Predicate pred, int index){
		//System.out.println("Find term " + t + " in " + terms + " starting at " + index);
		for(int i=index; i<conjunctiveFormula.size(); i++){
			Predicate p = conjunctiveFormula.get(i);
			if(pred.getName().equals(p.getName()))
				return true;
		}
		return false;
	}

	
//	//make sure that all variables in the head are present in the body
//	//make sure that all variables in a built-in predicate (x=3) appear in a body relation or the head
//	public boolean isValid(){
//		ArrayList<String> headVars = head.getVars();
//		ArrayList<String> bodyVars = new ArrayList<String>();
//		ArrayList<String> bodyRelationVars = new ArrayList<String>();
//		for(int i=0; i<conjunctiveFormula.size(); i++){
//			Predicate p = conjunctiveFormula.get(i);
//			bodyVars.addAll(p.getVars());
//			if(p instanceof RelationPredicate)
//				bodyRelationVars.addAll(p.getVars());
//		}
//		//make sure that all variables in the head are present in the body
//		for( int j=0; j<headVars.size(); j++){
//			String headVar = headVars.get(j);
//			if(!bodyVars.contains(headVar)){
//				throw(new RuntimeException("The head variable " + headVar + " does not appear in the body of rule " + this));
//			}
//		}
//
//		//make sure that all variables in a built-in predicate (x=3) appear in a relation in the body or head
//		ArrayList<BuiltInPredicate> nonRels = getNonRelations();
//		for(int i=0; i<nonRels.size(); i++){
//			BuiltInPredicate p = nonRels.get(i);
//			//System.out.println("Validate predicate : " + p);
//			ArrayList<String> pVars = p.getVars();
//			for(int k=0; k<pVars.size(); k++){
//				String pVar = pVars.get(k);
//				//System.out.println("Check var : " + pVar + " in " + bodyRelationVars + " and " + headVars);
//				if(!bodyRelationVars.contains(pVar) && !headVars.contains(pVar))
//					throw(new RuntimeException("The variable " + pVar + " does not appear in a body relation or head of rule " + this));
//				else{
//					if(!bodyRelationVars.contains(pVar)){
//						//it is in the head, but not the body => in the graph it will be represented as an AssignNode
//						(p).isAssignment(true);
//					}
//				}
//			}
//		}
//		return true;
//	}
	
	@Override
	public String toString(){
		String s = "";
		s+= head + ":-";
		for(int i=0; i<conjunctiveFormula.size(); i++){
			if(i>0) s += " ^ \n\t";
			s += conjunctiveFormula.get(i).toString();
		}
		return s;
	}

}
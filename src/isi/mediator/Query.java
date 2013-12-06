package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

import java.util.ArrayList;

public class Query extends Rule{
	
	//query expressed in source terms
	private ArrayList<SourceQuery> sourceQuery = new ArrayList<SourceQuery>();
	
	public Query(){
		super();
	}
	
	public Query(Rule r){
		head=r.head;
		conjunctiveFormula=r.conjunctiveFormula;
	}

//	@Override
//	public Query clone(){
//		Query newQ = new Query();
//		newQ.head=head.clone();
//		for(int i=0; i<conjunctiveFormula.size(); i++){
//			Predicate p = conjunctiveFormula.get(i);
//			newQ.conjunctiveFormula.add(p.clone());
//		}
//		return newQ;
//	}

	protected void removePredicate(int i){
		if(i<conjunctiveFormula.size())
			conjunctiveFormula.remove(i);
	}

	/////////////////////////////////////
	
	@Override
	public String toString(){
		String s = super.toString();
		if(!sourceQuery.isEmpty()){
			s += "\nSourceQuery:";
			for(int i=0; i<sourceQuery.size(); i++){
				s += sourceQuery.get(i).toString() + "\n";
			}
		}
		return s;
	}
}
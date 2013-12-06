package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class SourceQuery extends Query{
	
	//THE QUERY expressed in source terms
	
	//I have one binding list per sourceQuery
	//each sourceQuery is a node in a Union; I don't want the bindings/constants 
	//from one union node to interfere with the ones from another
//	private Binding binding = new Binding();
	
	public SourceQuery(){
		super();
	}
	
//	@Override
//	public SourceQuery clone(){
//		SourceQuery newQ = new SourceQuery();
//		newQ.head=head.clone();
//		for(int i=0; i<conjunctiveFormula.size(); i++){
//			Predicate p = conjunctiveFormula.get(i);
//			newQ.conjunctiveFormula.add(p.clone());
//		}
////		newQ.binding = binding.clone();
//		return newQ;
//	}

//	//get all the variables in these relations
//	HashSet<String> getAllVars(ArrayList<Predicate> rels){
//		HashSet<String> vars = new HashSet<String>();
//		for(int i=0; i<rels.size(); i++){
//			Predicate rel = rels.get(i);
//			vars.addAll(rel.getVars());
//		}
//		return vars;
//	}
	
	//returns true if this query is contained in q
	//this=Q1 and q=Q2 , we want to show Q1 contained in Q2
	public boolean isContained(SourceQuery q) {
		
		//System.out.println("Is Contained: " + this + " IN " + q);
		
		//create a canonical DB that is the frozen body of Q1
		Database db = createFrozenBody();
		
//		System.out.println("Frozen Body of q1:\n " + db);
		
		//compute Q2(db) => execute all the joins from Q2 over the database db
		ArrayList<RelationPredicate> rels = q.getRelations();
		Relation joinRelation = null;
		for(int i=0; i<rels.size(); i++){
			RelationPredicate p = rels.get(i);
			Relation rel = db.getRelation(p);
			if(rel==null){
				//elation not found so we can't evaluate q2(D)
				return false;
			}
			joinRelation = rel.join(joinRelation);
			if(joinRelation.isEmpty()){
				//all joins will be empty => it is not contained
				return false;
			}
//			System.out.println("Result relation:\n " + joinRelation);
		}

		//System.out.println("Result relation for Q2(D):\n " + joinRelation);
		
		if(joinRelation==null) return false;

		//check if joinRelation contains the frozen head of Q1
		//the head of q1 and q2 should have only variables (no constants)
		ArrayList<String> q1HeadVars = head.getVars(); 
		ArrayList<String> q2HeadVars = q.head.getVars();
		//in joinRelation, the attributes q2HeadVars should have the values q1HeadVars
		if(joinRelation.containsTuple(q2HeadVars, q1HeadVars)){
			//contains frozen head of q1
			return true;
		}
		
		return false;
	}
	
	private Database createFrozenBody(){
				
		Database db = new Database();
		
		ArrayList<RelationPredicate> allRels = getRelations();
		for(int i=0; i<allRels.size(); i++){
			Predicate rel = allRels.get(i);
			db.addRelation(rel);
		}
		return db;
	}

	///////////////////////////////
	
	@Override
	public String toString(){
		String s = super.toString() + "\n";
		//s+= binding;
		return s;
	}
	

}
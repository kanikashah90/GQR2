package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class RelationPredicate extends Predicate{

	//the source schema that corresponds to this predicate
	//	protected SourceSchema source;

	public RelationPredicate(){}
	public RelationPredicate(String name){
		this.name=name;
	}

	@Override
	public RelationPredicate clone(){
		RelationPredicate p = new RelationPredicate(name);
		for(int i=0; i<terms.size(); i++){
			Term t = terms.get(i);
			p.addTerm(t.clone());
		}
		//		p.source=source;
		return p;
	}

	public ArrayList<String> findCommonAttrs(RelationPredicate p){
		//System.out.println("Common in: " + this + " and " + p);
		ArrayList<String> attrs = new ArrayList<String>();
		for(int i=0; i<terms.size(); i++){
			Term t = terms.get(i);
			String var = t.getVar();
			if(var!=null && p.containsTerm(t))
				attrs.add(var);
		}
		return attrs;
	}
    //take a predicate of the form (x,y,x,"10") and expand in
    //(x0,y1,x2,const3) ^ (const3="10") ^ (x0=x2)
    public ArrayList<Predicate> expandInConjunctiveFormula(){
            ArrayList<Predicate> formula = new ArrayList<Predicate>();
            
            // (x, (0,2)) (y,1)
            //holds positions of the vars
            Hashtable<String,ArrayList<String>> pos = new Hashtable<String,ArrayList<String>>();
            //(const3, "10")
            //holds vars that have constants assigned
            Hashtable<String,String> constants = new Hashtable<String,String>();
            
            RelationPredicate p = this.clone();
            //rename each term to a unique name
            for(int i=0; i<p.terms.size(); i++){
                    Term t = p.terms.get(i);
                    t.changeVarName(i);

            }
            formula.add(p);

            ArrayList<String> attrsOld = getVarsAndConst();
            ArrayList<String> attrsNew = p.getVarsAndConst();

            
            for(int i=0; i<attrsOld.size(); i++){
                    Term t = terms.get(i);
                    if(t instanceof ConstTerm){
                            //get the constant value
                            constants.put(attrsNew.get(i),t.getVal());
                    }
                    
                    ArrayList<String> posVals = pos.get(attrsOld.get(i));
                    if(posVals==null){
                            posVals=new ArrayList<String>();
                            pos.put(attrsOld.get(i),posVals);
                    }
                    posVals.add(String.valueOf(i));

            }
            
            //System.out.println("Position of attrs=" + pos + " Constants=  " + constants);
    
            //deal with var equality x0=x2
            Iterator it = pos.keySet().iterator();
            while(it.hasNext()){
                    String key = (String)it.next();
                    ArrayList<String> vals = pos.get(key);
                    if(vals.size()>1){
                            //add x0=x2 & x2=x9, etc
                            for(int i=1; i<vals.size(); i++){
                                    BuiltInPredicate p1 = new BuiltInPredicate(MediatorConstants.EQUALS);
                                    //p1.addTerm(new VarTerm(key+vals.get(i)));
                                    //p1.addTerm(new VarTerm(key+vals.get(i+1)));
                                    VarTerm v1 = new VarTerm(key);
                                    v1.changeVarName(Integer.valueOf(vals.get(i-1)).intValue());
                                    p1.addTerm(v1);
                                    VarTerm v2 = new VarTerm(key);
                                    v2.changeVarName(Integer.valueOf(vals.get(i)).intValue());
                                    p1.addTerm(v2);
                                    formula.add(p1);
                                    //i=i+1;
                            }
                    }
            }
            //deal with constants
            it = constants.keySet().iterator();
            while(it.hasNext()){
                    String key = (String)it.next();
                    String val = constants.get(key);
                    //it's a constant
                    //add const3="10"
                    BuiltInPredicate p1 = new BuiltInPredicate(MediatorConstants.EQUALS);
                    p1.addTerm(new VarTerm(key));
                    p1.addTerm(new ConstTerm(val));
                    formula.add(p1);
            }
            return formula;
	}	
	@Override
	public String toString(){
		String s = "";
		s += name + "(";
		for(int i=0; i<terms.size(); i++){
			if(i>0) s += ",";
			s += terms.get(i).toString();
		}
		s+= ")";
		return s;
	}

}
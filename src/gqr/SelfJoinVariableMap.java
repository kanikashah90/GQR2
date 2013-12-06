package gqr;

import gqr.SelfJoinVariableRecord.VariableType;

import java.util.HashMap;



class SelfJoinVariableMap {

	private HashMap<String,SelfJoinVariableRecord> map = new HashMap<String, SelfJoinVariableRecord>();
	
	/**
	 * 
	 * @param namesOfqueryVarAndSource
	 * @param newRec
	 * @return true if everything OK, i.e., we either added an equate between sources distinguished vars in order to cover the selfjoin , or the selfjoin is covered by default by source existential vars. Returns false if this source predicate cannot be used to cover the selfjoined query pred
	 */
	boolean add(String namesOfqueryVarAndSource, SelfJoinVariableRecord newRec, SourcePredicateJoin spj)
	{ 
		if(!map.containsKey(namesOfqueryVarAndSource))
		{
			map.put(namesOfqueryVarAndSource,newRec);
			return true; //true means everything OK
		}
		else//if map contained key	
		{
			SelfJoinVariableRecord existingRec = map.get(namesOfqueryVarAndSource);
			
			//the first source variable (the one stored already in the map) covering the selfjoin query one is distinguished
			if(existingRec.getSourceVarType().equals(VariableType.Distinguished))
			{
				if(!newRec.getSourceVarType().equals(VariableType.Distinguished))// the second one should also be distinguished so we can equate them
					return false;
				else//both covering variables of the underlying selfjoined query var are distinguished
				{
					//TODO what to do with constants?
//					System.out.println(existingRec.getSourceVarName()+" "+newRec.getSourceVarName());
					spj.addEquate(existingRec.getSourceVarName(), newRec.getSourceVarName(), null);
					return true;
				}
			}
			else //source variable covering the selfjoin query one is existential 
			{
				if(newRec.getSourceVarType().equals(VariableType.Distinguished)) // the second is distinguished
					return false;
				else//both covering variables of the underlying selfjoined query one are existential
				{
					if(!existingRec.getSourceVarName().equals(newRec.getSourceVarName()))
						return false;
					return true;
				}
			}
		}
	}
	
	boolean addSourceVariablesInOrdertoEquateQueryOnes(String namesOfqueryVarAndSource, SelfJoinVariableRecord newRec, AtomicRewriting at)
	{ 
		if(!map.containsKey(namesOfqueryVarAndSource))
		{
			map.put(namesOfqueryVarAndSource,newRec);
			return true; //true means everything OK
		}
		else//if map contained key	
		{
			SelfJoinVariableRecord existingRec = map.get(namesOfqueryVarAndSource);
			
			//the first source variable (the one stored already in the map) covering the selfjoin query one is distinguished
			if(existingRec.getSourceVarType().equals(VariableType.Distinguished))
			{
				if(!newRec.getSourceVarType().equals(VariableType.Distinguished))// the second one should also be distinguished so we can equate them
					return false;
				else//both covering variables of the underlying selfjoined query var are distinguished
				{
					//TODO what to do with constants?
//					System.out.println(existingRec.getSourceVarName()+" "+newRec.getSourceVarName());
					at.addEquateInQueryHeadForThisRewriting(existingRec.getSourceVarName(), newRec.getSourceVarName(), null);
//					System.out.println("Adding an equate between "+existingRec.getSourceVarName()+" and "+ newRec.getSourceVarName()+" in rewriting: "+at);
					return true;
				}
			}
			else //source variable covering the selfjoin query one is existential 
			{
				throw new RuntimeException("This method shoiuld be called only for distinguished vars");
			}
		}
	}
	
	boolean addTemp(String namesOfqueryVarAndSource, SelfJoinVariableRecord newRec, AtomicRewriting at)
	{ 
		if(!map.containsKey(namesOfqueryVarAndSource))
		{
			map.put(namesOfqueryVarAndSource,newRec);
			return true; //true means everything OK
		}
		else//if map contained key	
		{
			SelfJoinVariableRecord existingRec = map.get(namesOfqueryVarAndSource);
			
			//the first source variable (the one stored already in the map) covering the selfjoin query one is distinguished
			if(existingRec.getSourceVarType().equals(VariableType.Distinguished))
			{
				if(!newRec.getSourceVarType().equals(VariableType.Distinguished))// the second one should also be distinguished so we can equate them
					return false;
				else//both covering variables of the underlying selfjoined query var are distinguished
				{
					//TODO what to do with constants?
//					System.out.println(existingRec.getSourceVarName()+" "+newRec.getSourceVarName());
					//TODO Now that I also use the following function here rename it 
					at.addEquateInQueryHeadForThisRewriting(existingRec.getSourceVarName(), newRec.getSourceVarName(), null);
					return true;
				}
			}
			else //source variable covering the selfjoin query one is existential 
			{
				if(newRec.getSourceVarType().equals(VariableType.Distinguished)) // the second is distinguished
					return false;
				else//both covering variables of the underlying selfjoined query one are existential
				{
					if(!existingRec.getSourceVarName().equals(newRec.getSourceVarName()))
						return false;
					return true;
				}
			}
		}
	}
}


class SelfJoinVariableRecord{
	
	enum VariableType {
		Distinguished, Existential
	}
	//queryvartype will probably be always existential so its useless
//	private VariableType queryVarType; 
	private VariableType sourceVarType; 
	private String sourceVarName; 
	
	SelfJoinVariableRecord(VariableType sourceVarType, String sourceVarName) {
//		this.queryVarType = queryVarType;
		this.sourceVarType = sourceVarType;
		this.sourceVarName = sourceVarName;
	}
	
//	VariableType getQueryVarType() {
//		return queryVarType;
//	}

	VariableType getSourceVarType() {
		return sourceVarType;
	}

	String getSourceVarName() {
		return sourceVarName;
	}

	
}
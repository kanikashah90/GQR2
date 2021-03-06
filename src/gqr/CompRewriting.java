package gqr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * This class Represents a conjunction of atomic rewritings, i.e., a partial or final conjunctive rewriting
 * 
 * @author george konstantinidis
 *
 */
public class CompRewriting {

	private List<AtomicRewriting> atomicRewritings;
	private List<Set<AtomicRewriting>> merges = new ArrayList<Set<AtomicRewriting>>();
	private Map<String,Set<String>> equates = new HashMap<String, Set<String>>() ;
	//	private String head;
	private String queryName;
	private List<String> headVariables;

	void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	void setHeadVariables(List<String> headVariables) {
		this.headVariables = headVariables;
	}

	String getHead() {
		return queryName + "(" + DatalogQuery.printCollection(headVariables) + ")";
	}

	List<Set<AtomicRewriting>> getMerges() {
		return merges;
	}

	Map<String,Set<String>> getEquates() {
		return equates;
	}

	void setMerges(List<Set<AtomicRewriting>> merges) {
		this.merges = merges;
	}

	void setEquates(Map<String,Set<String>> equates) {
		this.equates = equates;
	}

	List<AtomicRewriting> getAtomicRewritings() {
		return atomicRewritings;
	}

	void setAtomicRewritings(List<AtomicRewriting> atomicRewritings) {
		this.atomicRewritings = atomicRewritings;
	}

	public CompRewriting(String name, List<String> headvars)
	{
		this.queryName = name;
		this.headVariables = headvars;
		atomicRewritings = new ArrayList<AtomicRewriting>();
	}

	void add(AtomicRewriting ar) {
		atomicRewritings.add(ar);
	}

	CompRewriting merge(CompRewriting crB) {
		//TODO since we are returning a new object here, we shouldn't call it merge or take it out of here
		List<AtomicRewriting> newRewr = new ArrayList<AtomicRewriting>();
		newRewr.addAll(atomicRewritings);
		newRewr.addAll(crB.getAtomicRewritings());
		assert(this.getHead().equals(crB.getHead()));
		CompRewriting cmpr = new CompRewriting(queryName,headVariables);
		cmpr.setAtomicRewritings(newRewr);
		//		System.out.println("combine CPJs: >>>>>"+this+ " \n          and >>>>>"+crB);
		cmpr.setEquates(combineEquates(this.equates,crB.getEquates()));
		cmpr.setMerges(combineMerges(this.merges,crB.getMerges()));
		return cmpr;
	}

	private List<Set<AtomicRewriting>> combineMerges(List<Set<AtomicRewriting>> merges2,
			List<Set<AtomicRewriting>> merges3) {
		List<Set<AtomicRewriting>> l = new ArrayList<Set<AtomicRewriting>>();
		//		System.out.println("Ccccccccombining merges: \n\t"+merges2);
		//		System.out.println("Cccccccc with: \t"+merges3);
		l.addAll(merges2);
		l.addAll(merges3);
		//		System.out.println("Cccccccc merges in the end: "+l);
		return l;
	}

	private Map<String, Set<String>> combineEquates(
			Map<String, Set<String>> eq, Map<String, Set<String>> eq2) {

		Map<String, Set<String>> m = new HashMap<String, Set<String>>();
		//		System.out.println("eq1: "+eq);
		//		System.out.println("eq2: "+eq2);

		//		System.out.println("Putting eq in m");
		for(Entry<String, Set<String>> es: eq.entrySet())
		{
			m.put(es.getKey(), es.getValue());
		}

		//		System.out.println("Iterating over eq2");
		for(Entry<String, Set<String>> es: eq2.entrySet())
		{
			//			System.out.println("Looking for "+es.getKey()+" in the keys of m");
			Set<String> s = m.get(es.getKey());
			if(s == null)
			{
				m.put(es.getKey(), es.getValue());
				//				System.out.println("Not found, so adding it with its values in m (value: "+es.getValue()+")"); 
			}
			else
			{
				Set<String> s1 = new HashSet<String>();
				s1.addAll(s);
				s1.addAll(es.getValue());
				m.put(es.getKey(), s1);
				//				System.out.println("Found, adding the new value for the smae key in m (value: "+m.get(es.getKey())+")"); 
			}
		}
		return m;
	}

	@Override
	public String toString() {
		String ret = new String();

		for(AtomicRewriting at: atomicRewritings)
		{
			assert(at.getSourceHeads().size() == 1);
			ret += at.getSourceHeads().iterator().next().toString()+", ";
		}

		ret = ret.substring(0, ret.lastIndexOf(", "));

		return this.getHead()+ " :- "+ ret;//+" ----  equates: "+equates.toString();// + " merges: "+merges.toString()+"

		//		return atomicRewritings.toString()+"merges: "+merges.toString()+"equates: "+equates.toString();
	}


	AtomicRewriting removeAtomic(AtomicRewriting at) {
		if(atomicRewritings.remove(at))
			return at;
		else
			return null;
	}

	Integer getAtomic(AtomicRewriting next) {
		return atomicRewritings.indexOf(next);
	}

	void addMerge(AtomicRewriting at1, AtomicRewriting at2) {

		Set<AtomicRewriting> first_set = null;
		Set<AtomicRewriting> second_set = null;

		boolean in =false;
		for(Set<AtomicRewriting> setj: merges)
		{	
			if((first_set==null) && setj.contains(at1))
			{
				setj.add(at2);
				in = true;
				first_set = setj;
			}
			else if((second_set==null) && setj.contains(at2))
			{
				setj.add(at1);
				in = true;
				second_set = setj;
			}
			if(first_set!=null && second_set!=null)
				break;
		}	

		if(first_set!=null && second_set!=null)
		{
			merges.remove(second_set);
			merges.get(merges.indexOf(first_set)).addAll(second_set);
		}
		if(!in)
		{
			Set<AtomicRewriting> hs = new HashSet<AtomicRewriting>();
			hs.add(at1);
			hs.add(at2);
			merges.add(hs);
		}
	}

	String getExpansion()
	{
//				System.out.println("Rewriting : "+ this.toString());

		//		System.out.println("Atomic Rewritings (view heads) : ");

		String exp = new String();
		int freshvarcount = 1;
		//		System.out.println("Getting the expansion of:"+this);
		//		System.out.println("Going in atomic");
		////		
		for(AtomicRewriting at: atomicRewritings)
		{

			//			System.out.println("At1: "+at);
			assert(at.getSourceHeads().size() == 1);
			SourceHead sh = at.getSourceHeads().iterator().next();

			DatalogQuery rule =null;
			try {
				rule = sh.getQuery().clone();//TODO this clone here will clone every query in the rewritings and this is unnecessary. 
				//The problem was that sometimes through the renaming of variables below and because of selfjoins, we might equate a query's variables when unfolding it.
				//Then subsequent rewritings will have this constrained (selfjoined) form to query in their sh for them to unfold which will produce errors.
				//If we can pinpoint this case there's no need for general and always clonign anymore and we could optimize here. 
				//This is if we want getexpansion in our main algorithms, because up to now it's only used for sanity checking and we don't care about its performance.  
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			} 
			//			System.out.println(" 		Rule: "+rule);

			for(Variable v:rule.getExistentialVariables())
			{
				rule.substituteVarWithFresh(v.name,true,freshvarcount);
			}
			freshvarcount++;

			for(int i=0; i<sh.getSourceHeadVars().size(); i++)
			{
				String shv = sh.getSourceHeadVars().get(i);
				//				System.out.println("alreadyfreshed.contains(shv) ? "+rule.alreadyfreshed.contains(shv));
				if(!rule.alreadyfreshed.contains(shv))
				{
					rule.substituteVarWithFresh(shv,false,0);//substitute with a variable not appearing in the body
					rule.alreadyfreshed.add(shv);
				}
				//				System.out.println("		Rule after substituting shv:"+shv+" with fresh : "+rule);
				String varInRule = rule.getHeadVariables().get(i).toString();

				rule.substituteVarWithNew(varInRule,shv);

				//				System.out.println("		Rule after substituting varInruleAtPosi:"+varInRule+" with var in sh:"+shv +" "+rule);

			}

			//			System.out.println(" 		Rule after unification: "+rule);

			exp+="," + rule.printBody(rule.getPredicates());

		}
		exp = exp.replaceFirst(",","");
		return this.getHead() + " :- "+exp; 
	}

	void addEquate(String sourcevar1, String sourcevar2, String queryVar1) {

		Set<String> strs = equates.get(queryVar1); //TODO in the case of selfjoins the query vars might be different but forced to be equated by us; hence I should merge their sets
		String repElemQuerVar = queryVar1;

		Set<String> foundVar1 = equates.get(sourcevar1);
		String repElem1 = sourcevar1;
		Set<String> foundVar2 = equates.get(sourcevar2);
		String repElem2 = sourcevar2;
//int testcount=0;
		for(String key: equates.keySet()) // get all equivalence sets
		{   Set<String> sr =  equates.get(key);

			if(foundVar1 == null && sr.contains(sourcevar1)) //found a set containing var1
			{
				foundVar1 = sr;
				repElem1 = key;
			}

			if(foundVar2 ==null && sr.contains(sourcevar2))
			{
				foundVar2 = sr;
				repElem2 = key;
			}

			if(queryVar1!=null && strs==null && sr.contains(queryVar1))
			{
				strs = sr;
				repElemQuerVar = key;
			}

			if(foundVar1!=null && foundVar2!=null && (strs!=null || queryVar1==null))
				break;
		}
//		System.out.println("sourcevar1 = "+sourcevar1);
//		System.out.println("sourcevar2 = "+sourcevar2);
//		System.out.println("queryvar = "+queryVar1);
//		System.out.println("Equates: " +equates);
//		assert(testcount<=1);

		Set<String> hs = new HashSet<String>();

		if(strs!=null)//either queryVar was the representative of a set or it existed in a set
		{
			if(!repElemQuerVar.equals(queryVar1)) // if queryvar was not the representative
			{
				mergeSecondSetInFirst(hs, strs);
				hs.add(repElemQuerVar);
				equates.remove(repElemQuerVar);
			}
			else
				hs = strs;
		}
		else if(queryVar1 !=null)//queryvar was not found but we have one to make a key 
			hs.add(queryVar1);
		else //queryVar1 == null
			;

		if(foundVar1!=null) //either sourcevar1 was the representative of the set foundVar1 or it existed in foundVar1 
		{
			mergeSecondSetInFirst(hs, foundVar1);
			hs.add(repElem1);
			equates.remove(repElem1);
		}
		else //no set containing sourcevar1 existed
			hs.add(sourcevar1);

		if(foundVar2!=null) //either sourcevar2 was the representative of the set foundVar2 or it existed in foundVar2 
		{
			mergeSecondSetInFirst(hs, foundVar2);
			hs.add(repElem2);
			equates.remove(repElem2);
		}
		else //no set containing sourcevar2 existed
			hs.add(sourcevar2);

		if(queryVar1 != null)
			equates.put(queryVar1,hs);//make queryvar the representative of the set
		else if(repElem1!=null && repElem2!=null) //else if we have both representatives of old sets as candidates for representatives of the new set
		{
			equates.put(repElem1,hs);//TODO we need to decide who to keep now I'm keeping the first arbitrarily but we should keep one that is a query variable if possible
		}
		else if(repElem1!=null) //if one set existed and had a representative keep it
		{
			equates.put(repElem1,hs);
		}
		else if(repElem2!=null) //if one set existed and had a representative keep it
		{
			equates.put(repElem2,hs);
		}
		else
			equates.put(sourcevar1,hs);//make the first variable arbitrarily representative

	}

	private void mergeSecondSetInFirst(Set<String> strs, Set<String> strs2) {
		strs.addAll(strs2);
	}

	
	protected CompRewriting cloneDummy() throws CloneNotSupportedException {
		List<AtomicRewriting> l = new ArrayList<AtomicRewriting>();
		List<String> hvars = new ArrayList<String>(headVariables);

		for(AtomicRewriting at: this.getAtomicRewritings())
			l.add(at.cloneDummy());
		CompRewriting cr = new CompRewriting(queryName,hvars);
		cr.setAtomicRewritings(l);
		cr.setEquates(this.getEquates());
		cr.setMerges(this.getMerges());
		return cr;
	}

	String getQueryName() {
		return queryName;
	}

	List<String> getHeadVariables() {
		return headVariables;
	}

}

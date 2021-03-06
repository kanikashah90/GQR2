package gqr;


import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author george konstantinidis
 *
 * This class represents the part of a partial rewriting covering one atomic query subgoal with some CPJ
 */
class AtomicRewriting {

	private List<SourceHead> sourceHeads;//normally this should be just an element
	//TODO change the above list to a single sourcehead if everything works out
	private List<JoinInView> joivs = new ArrayList<JoinInView>();
	//for all variable nodes in a CPJ, this is a list of the sourceboxes associated with this rewriting 

//	private boolean clonedForVariableRenamingDueToEquations = false;
	private List<Pair<String,Pair<String,String>>> equates = new ArrayList<Pair<String,Pair<String,String>>>();	
	
	/**
	 * TODO returns the sourceboxes associated with this rewriting
	 * @return
	 */
	List<JoinInView> getJoivs() {
		return joivs;
	}

    /**
     *  
     * @return
     */
	List<SourceHead> getSourceHeads() {
		return sourceHeads;
	}

	/**
	 * 
	 * @param sourceHeads
	 */
	void setSourceHeads(List<SourceHead> sourceHeads) {
		this.sourceHeads = sourceHeads;
	}


	AtomicRewriting() {
		sourceHeads = new ArrayList<SourceHead>();	
	}
	
	
	void addSourceHead(SourceHead i)
	{
		sourceHeads.add(i);
	}


	@Override
	public String toString() {
		return sourceHeads.toString();
	}
	
	@Override
	protected AtomicRewriting clone() throws CloneNotSupportedException {
		
		AtomicRewriting ret = new AtomicRewriting();
		List<SourceHead> sHeads = new ArrayList<SourceHead>();
		for(SourceHead sh: this.getSourceHeads())
			sHeads.add(sh.clone());
		ret.setSourceHeads(sHeads);
		List<Pair<String,Pair<String,String>>> new_equates = new ArrayList<Pair<String,Pair<String,String>>>();
		for(Pair<String,Pair<String,String>> p: this.getEquates())
			new_equates.add(p);
		ret.setEquates(new_equates);
		return ret;
	}


	private void setEquates(List<Pair<String, Pair<String, String>>> equates2) {
		this.equates = equates2;
	}

//	AtomicRewriting append(AtomicRewriting rB) {
//		this.sourceHeads.addAll(rB.getSourceHeads());
//		return this;
//	}


	boolean contains(List<AtomicRewriting> atomicRewritings) {
		return this.sourceHeads.containsAll(atomicRewritings);
	}


	void addRefToJoiv(JoinInView joiv) {
		joivs.add(joiv);
	}
	
	AtomicRewriting cloneDummy() {
		AtomicRewriting ret = new AtomicRewriting();
		List<SourceHead> sHeads = new ArrayList<SourceHead>();
		for(SourceHead sh: this.getSourceHeads())
			sHeads.add(sh.cloneDummy());
		ret.setSourceHeads(sHeads);
		List<Pair<String,Pair<String,String>>> new_equates = new ArrayList<Pair<String,Pair<String,String>>>();
		for(Pair<String,Pair<String,String>> p: this.getEquates())
			new_equates.add(p);
		ret.setEquates(new_equates);
		return ret;
	}
	
	
	

//	AtomicRewriting cloneDummyAndSetSourceHeadVars( List<String> newsourceheadvars) 
//	{
//		AtomicRewriting ret = new AtomicRewriting();
//		List<SourceHead> sHeads = new ArrayList<SourceHead>();
//		assert(this.getSourceHeads().size() == 1);
//		for(SourceHead sh: this.getSourceHeads())
//			sHeads.add(sh.cloneAndSetSourceHeadVars(newsourceheadvars));
//		ret.setSourceHeads(sHeads);
//		ret.setEquates(equates);
//		return ret;
//	}

	void addEquateInQueryHeadForThisRewriting(String string, String string2, String queryVar) {
		Pair<String,String> p = new Pair<String,String>(string,string2); 
		equates.add(new Pair<String,Pair<String,String>>(queryVar,p));
	}
	List<Pair<String, Pair<String, String>>> getEquates() {
		return equates;
	}

}

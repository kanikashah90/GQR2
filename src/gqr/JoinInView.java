package gqr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author george
 *
 * This class represents a "sourcebox"
 * TODO query should use a different kind of object (wityh less methods/fields e.g. source info, and rewritings
 */
class JoinInView {

	private String sourceName;
	private List<JoinDescription> joinDescriptions;
	private List<AtomicRewriting> atomicRewritings;

	private int headPosition = -1;
	private String sourceVarName;

	void setSourceVarName(String sourceVarName) {
		this.sourceVarName = sourceVarName;
	}

	List<AtomicRewriting> getRewritings() {
		return atomicRewritings;
	}

	void setRewritings(List<AtomicRewriting> atomicRewritings) {
		this.atomicRewritings = atomicRewritings;
	}

	JoinInView(String name) {
		this.sourceName = name;
		atomicRewritings = new ArrayList<AtomicRewriting>();
		joinDescriptions = new ArrayList<JoinDescription>();
	}

	String getSourceName() {
		return sourceName;
	}

	void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	List<JoinDescription> getJoinDescriptions() {
		return joinDescriptions;
	}

	void setJoinDescriptions(List<JoinDescription> joinDescriptions) {
		this.joinDescriptions = joinDescriptions;
	}

	void addJoinDescription(JoinDescription joinDescription) {
		joinDescriptions.add(joinDescription);	
	}
	
	void addRewriting(AtomicRewriting rw) {
		atomicRewritings.add(rw);
	}
	
	@Override
	protected JoinInView clone() throws CloneNotSupportedException {
		JoinInView jv = new JoinInView(this.sourceName);
		jv.setSourceVarName(this.getSourceVarName());
		try{
		jv.headPosition = this.getHeadPosition();
		}catch (UnitializedHeadPositionException e) {
			jv.headPosition = -1;
		}
		List<JoinDescription> joinDs = new ArrayList<JoinDescription>();
		for(JoinDescription jd: this.getJoinDescriptions())
		{
			joinDs.add(jd.clone());
		}
		jv.setJoinDescriptions(joinDs);
		jv.setRewritings(null);// we don't clone this as in the way we are using clone we only do it for source pjs
		//in which case we are going to manually set up the cloned node's rewriting's to be those (new cloned) 
		//atomicRewritings that belong in the new cloned source pj. That is because we always need to maintain a link. 
		
		return jv;
	}
	
	@Override
	public String toString() {
		return "("+sourceName+")->"+joinDescriptions.toString()+" rw="+atomicRewritings.toString();
	}

	boolean containsDescriptionsIgnoreRepID(
			List<JoinDescription> joinDesc) {
		
		for(JoinDescription query_jd: joinDesc)
		{
			boolean found = false;
			for(JoinDescription this_jds: joinDescriptions)
			{
				if(this_jds.equalsIgnoreRepeatedID(query_jd))
				found = true;
			}
			if(!found)
				return false;
		}
		return true;
	}

	void addSourceHeadPosition(int positionInHead) {
//		if(positionInHead == -1)
//			throw new RuntimeException("Cannot initialize head variable position with -1 ");
			
		headPosition = positionInHead;
	}
	
	


	Integer getHeadPosition()
	{
		if(headPosition == -1)
			throw new UnitializedHeadPositionException();
		return headPosition;
	}

	String getSourceVarName() {
		
		if(sourceVarName==null)
			throw new RuntimeException("Name of the source variable for this sourcebox is unitialized. Maybe this is a query \"sourcebox\" ");
		
		return sourceVarName;
	}
}

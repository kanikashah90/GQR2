package gqr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PredicateJoin {

	private Predicate pred;
	private Map<Integer,GQRNode> gqrNodes;
	private List<AtomicRewriting> atomicRewritings;
	private int serialNo;


	PredicateJoin(final Predicate p) {
		this.pred = p;
		gqrNodes = new HashMap<Integer,GQRNode>();
		atomicRewritings = new ArrayList<AtomicRewriting>();
	}

	Predicate getPredicate() {
		return pred;
	}

	void setPredicate(Predicate pred) {
		this.pred = pred;
	}

	void addNode(GQRNode nv, int edgeNo) {
		gqrNodes.put(edgeNo,nv);
	}

	Map<Integer, GQRNode> getGqrNodes() {
		return gqrNodes;
	}

	void setGqrNodes(Map<Integer, GQRNode> gqrNodes) {
		this.gqrNodes = gqrNodes;
	}

	void addRewriting(AtomicRewriting atomicRewriting) {
		atomicRewritings.add(atomicRewriting); 
	}

	List<AtomicRewriting> getRewritings() {
		return atomicRewritings;
	}

	void setRewritings(List<AtomicRewriting> atomicRewritings) {
		this.atomicRewritings = atomicRewritings;
	}

	void setSerialNumber(int i) {
		serialNo = i;
	}
	
	String variablePatternStringSequence()
	{
		String hash = "";
		for(int i=1; i<=getGqrNodes().size(); i++)
		{
			hash+=String.valueOf(i);
			hash+=((GQRNode)getGqrNodes().get(i)).isExistential()?"E":"D";
		}
		return hash;
	}

	int getSerialNumber() {
		return serialNo;
	}
	
	@Override
	public String toString() {
		return pred+variablePatternStringSequence();
	}
//	boolean isEmpty()
//	{
//		return false;
//	}

}

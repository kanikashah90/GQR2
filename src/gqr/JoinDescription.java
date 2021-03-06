package gqr;

class JoinDescription {

	private Predicate predicate;
	private int edgeNo;

	JoinDescription(Predicate otherpred, int i) {
		predicate = otherpred;
		edgeNo = i;
	}

	Predicate getPredicate() {
		return predicate;
	}

	void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	int getEdgeNo() {
		return edgeNo;
	}

	void setEdgeNo(int edgeNo) {
		this.edgeNo = edgeNo;
	}

	@Override
	public String toString() {
		return "-|"+predicate+"_"+predicate.getRepeatedId()+" on:("+edgeNo+")|-";
	}
	
	@Override
	protected JoinDescription clone() throws CloneNotSupportedException {
		return new JoinDescription(this.predicate, this.getEdgeNo());//don't think we have to clone the predicate..
		//we'll leave it like this for now
		//TODO
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof JoinDescription))
			return false;
		//TODO should probably change. 
		//I use it for contains, when looking if two variable's infoboxes describe this join
		return ((JoinDescription)obj).toString().equals(this.toString());
//		JoinDescritpion other = ((JoinDescritpion)obj);
//		return (other.getPredicate() == this.predicate)&&(other.getEdgeNo()==this.getEdgeNo());
	}

	boolean equalsIgnoreRepeatedID(JoinDescription queryJd) {
		if(!(queryJd instanceof JoinDescription))
			return false;
		//TODO to change if toString() cahnges and decide to print only predicates nameand not parameters!
		return new String("-|"+this.getPredicate()+" on:("+this.edgeNo+")|-").equals("-|"+queryJd.getPredicate()+" on:("+queryJd.edgeNo+")|-");
	}
	
	int hashCodeIgnoreRepeatedID() {
		String s = new String("-|"+this.getPredicate()+" on:("+this.edgeNo+")|-");
		return s.hashCode();
	}

	//This is the old equals
	boolean equalsWithSamePred(JoinDescription obj) {
		if(!(obj instanceof JoinDescription))
			return false;
		JoinDescription other = ((JoinDescription)obj);
		return (other.getPredicate() == this.predicate)&&(other.getEdgeNo()==this.getEdgeNo());
	}
}

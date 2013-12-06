package gqr;

class GQRNode {

	private Variable variable;
	private Infobox infobox;
	private boolean isExistential;
//	private String queryDistinguishedVar = null;

	GQRNode(Variable v, Infobox box) {
		variable = v;
		infobox = box;
		this.setExistential(v.isExistential());
	}
	

	boolean isExistential() {
		return isExistential;
	}

	void setExistential(boolean isExistential) {
		this.isExistential = isExistential;
	}

	Variable getVariable() {
		return variable;
	}

	void setVariable(Variable variable) {
		this.variable = variable;
	}

	Infobox getInfobox() {
		return infobox;
	}

	void setInfobox(Infobox infobox) {
		this.infobox = infobox;
	}
	
	static GQRNode dummyExistentialNode()
	{
		Variable v = new Variable(null);
		v.setIsExistential();
		return new GQRNode(v, null);
	}
	
	static GQRNode dummyDistinguishedNode()
	{
		Variable v = new Variable(null);
		return new GQRNode(v, null);
	}
	
	@Override
	protected GQRNode clone() throws CloneNotSupportedException {
		return new GQRNode(this.getVariable().clone(),this.getInfobox().clone());
	}
	
	@Override
	public String toString() {
		return (isExistential ? "E " :"D ")+ infobox.toString();
	}


//	void addQueryDistinguishedVar(String name) {
//		queryDistinguishedVar = name;
//	}


//	String getQueryDistinguishedVar() {
////		if(queryDistinguishedVar == null)
////			throw new RuntimeException("uninitialized query variable reference");
//		return queryDistinguishedVar;
//	}
}

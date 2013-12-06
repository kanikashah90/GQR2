package gqr;


/**
 * This class extends PredicateArgument and represents a variable of a Datalog
 * query.
 */

public class Variable extends PredicateArgument {
	
	private boolean isExistential = false;
	private int positionInHead;

	/**
	 * Variable constructor. Calls the PredicateArgument constructor.
	 * 
	 * @param name (value) of variable
	 *            
	 */
	public Variable(String name) {
		super(name);
	}
	
	boolean isExistential() {
		return isExistential;
	}

	@Override
	public boolean equals(Object elem) {
		return ((Variable)(elem)).name.equals(this.name);
	}

	public void setIsExistential() {
		isExistential = true;
		positionInHead = -1;//don't care
	}
	int getPositionInHead() {
		return positionInHead;
	}

	public void setPositionInHead(int positionInHead) {
		this.positionInHead = positionInHead;
	}
	
	@Override
	protected Variable clone() throws CloneNotSupportedException {
		Variable var = new Variable(this.name);
		if(this.isExistential())
			var.setIsExistential();
		var.setPositionInHead(this.getPositionInHead());
		return var;
	}

}

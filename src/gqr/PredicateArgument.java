package gqr;

/**
 * Class PredicateArgument represents an element of a Datalog predicate. An
 * element can either be a variable or a constant which in turn can be a
 * numerical constant of string constant.
 * 
 * @author Kevin Irmscher
 */
public class PredicateArgument {
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	/** name (value) of predicate element */
	String name;

	/**
	 * PredicateArgument constructor
	 * 
	 * @param name
	 *            (value) of predicate element
	 */
	PredicateArgument(String name) {
		this.name = name;
	}

	/**
	 * Overwrites Object method. Returns name of predicate element.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Overwrites Object method. Returns true if names are equal.
	 */
	public boolean equals(Object elem) {
		//System.out.println("maybe here?");
		return this.name.equals(((PredicateArgument) elem).name);
	}

}

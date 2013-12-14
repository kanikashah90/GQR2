package gqr;

import java.util.ArrayList;
import java.util.List;

/**
 * Class predicate represents an body element of a datalog query that is NOT a
 * interpreted, i.e does not have a comparison of the elements.
 * 
 * A Predicate object consists of list of elements that can be both, variables
 * and constants. Moreover, it has two additional lists for variables and
 * constants.
 * 
 * @author Kevin Irmscher, george konstantinidis
 */
public class Predicate {

//	@Override
//	public int hashCode() {		
//		return name.hashCode();
//	}
	
	/** predicate name */
	public String name;

	/** both, predicate variables and constants */
	List<PredicateArgument> elements;

	/** predicate constants */
	private List<Constant> constants;

	/** predicate variables */
	private List<Variable> variables;

	private boolean isComparison;
	
	private int repeatedId = -1;

	/**
	 * Predicate constructor
	 * 
	 * @param name
	 *            predicate name
	 */
	public Predicate(String name) {
		this.name = name;
		this.elements = new ArrayList<PredicateArgument>();
		this.constants = new ArrayList<Constant>();
		this.variables = new ArrayList<Variable>();
		this.isComparison = false;
	}
	
	public boolean isComparison(){
		return this.isComparison;
	}
	
	public void makeComparisonPredicate()
	{
		this.isComparison = true;
	}
	
//	@Override
//		protected Predicate clone() throws CloneNotSupportedException {
//			Predicate cloned = new Predicate(this.name);
//			
//			cloned.elements = this.elements;
//			cloned.constants = this.constants;
//			cloned.variables = this.variables;
//			cloned.repeatedId = this.repeatedId;
//			return cloned;
//		}

	/**
	 * The method takes a list of PredicateArgument objects and will add them to
	 * the list of elements and to their relevant list. Elements of type
	 * Variable will be added to list 'variables', type Constant elements will be
	 * added to list 'constants'.
	 * 
	 * @param elems
	 *            to be added to the predicate
	 */
	public void addAllElements(List<PredicateArgument> elems) {
		for (PredicateArgument elem : elems) {
			elements.add(elem);
			if (elem instanceof Variable) {
				Variable var = (Variable) elem;
				variables.add(var);
			} else if (elem instanceof Constant) {
				Constant cons = (Constant) elem;
				constants.add(cons);

				// this code should never be reached
			} else {
				System.out.println("ERROR! Element of wrong type added to predicate");
			}
		}
	}

	/**
	 * The method will add a predicate element according to its type to the
	 * predicate by calling the relevant method.
	 * 
	 * @param elem to be added to the predicate
	 */
	void addElement(PredicateArgument elem) {
		if (elem instanceof Variable) {
			addVariable((Variable) elem);
		} else {
			addConstant((Constant) elem);
		}
	}

	/**
	 * Adds a variable to the predicate.
	 * 
	 * @param var to be added to the predicate
	 */
	void addVariable(Variable var) {
		elements.add(var);
		variables.add(var);
	}

	/**
	 * Removes a variable from the list of variables at the positon provided as
	 * argument. Removes the first occurence of the variable from the element
	 * list.
	 * 
	 * @param position of variable in member list variables
	 */
	void removeVariableAt(int position) {

		elements.remove(variables.get(position));
		variables.remove(position);
	}

	/**
	 * Returns the list of all elements of the predicate
	 * 
	 * @return list of variables and constants
	 */
	List<PredicateArgument> getArguments() {
		return elements;
	}

	/**
	 * Returns the list of variables of the predicate
	 * 
	 * @return list of variables
	 */
	List<Variable> getVariables() {
		return variables;
	}

	/**
	 * The method will add a constant to the predicate.
	 * 
	 * @param cons constant to be added to the predicate
	 */
	void addConstant(Constant cons) {
		elements.add(cons);
		constants.add(cons);
	}

	/**
	 * Adds a constant to the member list elements at position provided as
	 * argument. Adds a constant to the end of member list constants.
	 * 
	 * @param position of the constant in list elements
	 * @param cons constant to be added
	 */
	void addConstantAt(int position, Constant cons) {
		constants.add(cons);
		elements.add(position, cons);
	}

	/**
	 * Returns the ith element of the list of elements.
	 * 
	 * @param i
	 *            ith position in the predicate
	 * @return ith element of the predicate
	 */
	PredicateArgument getArgument(int i) {
		return elements.get(i);

	}

	/**
	 * Returns true if given element is contained in the predicate.
	 * 
	 * @param elem
	 *            PredicateArgument to be tested whether it is contained
	 * @return true, if element is contained in the predicate, false otherwise
	 */
	boolean contains(PredicateArgument elem) {
		if (elem instanceof Constant) {
			return constants.contains((Constant) elem);
		} else {
			return variables.contains((Variable) elem);
		}
	}

//	/**
//	 * Called by MiniCon.createMapping. The method will test if it is possible
//	 * to map the predicate 'this' with predicate provided as argument. A mapping is
//	 * possible if:
//	 * 
//	 * 1. The name of both predicates is equal.
//	 * 
//	 * 2. The number of elements (variables+constants) are the same.
//	 * 
//	 * 3. If two constant have to be mapped they must be the same
//	 * 
//	 * @param pred
//	 *            predicate to be compared with
//	 * @return true when mapping is possible, false otherwise
//	 */
//	public boolean canBeMapped(Predicate pred) {
//
//		boolean canBeMapped = true;
//
//		if ((this.name.equals(pred.name))
//				&& (this.numberOfElements() == pred.numberOfElements())) {
//
//			List<PredicateArgument> elemList = pred.getArguments();
//			for (int i = 0; i < elemList.size(); i++) {
//				PredicateArgument elem = elemList.get(i);
//				PredicateArgument thisElem = elements.get(i);
//
//				// mapping Constant -> Constants then constants must be the same
//				if ((elem instanceof Constant)
//						&& (thisElem instanceof Constant)
//						&& !elem.equals(thisElem)) {
//					canBeMapped = false;
//				}
//			}
//		} else {
//			canBeMapped = false;
//		}
//		return canBeMapped;
//	}

	/**
	 * Returns the total number of elements (variables + constants) of the
	 * predicate
	 * 
	 * @return number of predicate elements
	 */
	int numberOfElements() {
		return elements.size();
	}

	/**
	 * DO NOT overwrite this method MCD.findPredicates uses contains in order to
	 * find same objects
	 */
	public boolean equals(Object p) {
		return super.equals(p);
	}

	/**
	 * Overwrites Object method. Returns a String representation of the
	 * predicate.
	 */
	public String toString() {
		String val = name ;//+ "(" + printElements() + ")";
		return val;
	}

	/**
	 * Returns a String representation of the predicate's elements
	 * 
	 * @return String of predicate elements
	 */
	private String printElements() {
		String val = "";
		for (PredicateArgument elem : elements) {
			val = val + "," + elem.toString();
		}
		val = val.replaceFirst(",", "");
		return val;
	}
	
	void setRepeatedId(int i) {
		repeatedId  = i;
	}

	int getRepeatedId() {
		if(repeatedId == -1)
			throw new RuntimeException("Unitialized Repeated Id");
		return repeatedId;
	}
}
package gqr;

import java.util.Map.Entry;

/**
 * This class represents a query join (one variable) "covered" by some views combination.
 * 
 * @author george
 *
 */
public class Join {

	public enum joinTypeInQuery {E,D}; // E for existential , D for distinguished
	public Pair<SourcePredicateJoin, Integer> node1; 
	public Pair<SourcePredicateJoin, Integer> node2;
	
	public joinTypeInQuery jt;
//	private Predicate predicate1;
//	private Predicate predicate2;	
	
	public Join(Pair<SourcePredicateJoin, Integer> pair1 , Pair<SourcePredicateJoin, Integer> pair2) {
		node1= pair1; 
		node2= pair2;
		//jt = pair1.getA().getGqrNodes().get(pair1.getB()).isExistential()?joinTypeInQuery.E:joinTypeInQuery.D;
//		if(pair1.getA().getGqrNodes().get(pair1.getB()).isExistential())
//		{
//			//WAIT!!! joins are constructed even if infeasible
//			//they'll be dropped later!
//			assert(pair2.getA().getGqrNodes().get(pair2.getB()).isExistential());
//		}
	}

	public JoinsForSPJs getDistinguishedJoinedSPJs(CompositePredicateJoin a,
			CompositePredicateJoin b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return jt+" Node1: "+node1.toString()+", Node2: "+node2.toString();
	}
//	public Predicate getPredicate1() {
//		return predicate1;
//	}
//
//	public void setPredicate1(Predicate predicate1) {
//		this.predicate1 = predicate1;
//	}
//
//	public Predicate getPredicate2() {
//		return predicate2;
//	}
//
//	public void setPredicate2(Predicate predicate2) {
//		this.predicate2 = predicate2;
//	}

}

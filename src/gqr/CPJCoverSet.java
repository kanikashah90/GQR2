package gqr;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author george konstantinidis
 *
 */
class CPJCoverSet {

	
	List<CompositePredicateJoin> cpjs;
//	private boolean completeCover = false;
//	CPJCoverSet(List<CompositePredicateJoin> pjs) {
//		this.cpjs=pjs;
//	}
	private int serialNo;

	int getSerialNo() {
		return serialNo;
	}

	CPJCoverSet() {
		cpjs = new ArrayList<CompositePredicateJoin>() ;
	}

	boolean isEmpty() {
		return cpjs.isEmpty();
	}

//	boolean isCompleteCover() {
//		return completeCover;
//	}

//	void setCompleteCover(boolean completeCover) {
//		this.completeCover = completeCover;
//	}

	List<CompositePredicateJoin> getCPJs() {
		return cpjs;
	}

	void addAll(List<CompositePredicateJoin> l) {
		cpjs.addAll(l);
	}
	
	void add(CompositePredicateJoin c) {
		cpjs.add(c);
	}

	void setSerialNo(int serialNumber) {
		serialNo = serialNumber;
	}

}

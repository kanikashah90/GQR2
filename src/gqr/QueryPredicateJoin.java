package gqr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

class QueryPredicateJoin extends PredicateJoin{

	
	QueryPredicateJoin(Predicate p) {
		super(p);
	}

	void addNode(GQRNode nv, int j,int p) {
		super.addNode(nv,j);
	}

}

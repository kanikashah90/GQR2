package gqr;

class Pair<T,Y> {
	
	T A;
	Y B;
	
	Pair(T a, Y b) {
		A = a;
		B = b;
	}

	T getA() {
		return A;
	}

	Y getB() {
		return B;
	}
	
	@Override
	public String toString() {
		return "("+A.toString()+"),("+B.toString()+")";
	}

}

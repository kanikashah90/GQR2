package gqr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/**
 * 
 * @author george konstantinidis
 *
 */
class CompositePredicateJoin {

	private List<SourcePredicateJoin> pjs;
	private List<CompRewriting> compRewritings;
	CompositePredicateJoin()
	{
		pjs = new ArrayList<SourcePredicateJoin>();
		compRewritings = new ArrayList<CompRewriting>();
	}

	List<CompRewriting> getRewritings() {
		return compRewritings;
	}

	void addRewritings(CompRewriting compRewritings) {
		this.compRewritings.add(compRewritings);
	}

	void setRewritings(List<CompRewriting> compRewritings) {
		this.compRewritings = compRewritings;
	}

	void add(SourcePredicateJoin pj)
	{
		pjs.add(pj);
	}

	List<SourcePredicateJoin> getPjs() {
		return pjs;
	}

	void setPjs(List<SourcePredicateJoin> pjs) {
		this.pjs = pjs;
	}

	void add(CompositePredicateJoin a) {
		pjs.addAll(a.getPjs());
	}

	boolean emptyJoinInView(JoinInView jv) {

		Iterator<CompRewriting> it = this.getRewritings().iterator();
		while(it.hasNext()) //remove all the compRewritings that contain
			//the atomicRewritings in joininview
		{
			boolean remove = false;
			for(AtomicRewriting thisRewr: it.next().getAtomicRewritings())
			{
				assert(jv.getRewritings().size() == 1);
				if(jv.getRewritings().contains(thisRewr))
				{
					remove =true;
					break;
				}
			}

			if(remove)
			{
				it.remove();
			}

		}
		if(this.getRewritings().isEmpty())
			return true;


		//TODO I think I do have to do this
		for(SourcePredicateJoin spj: this.pjs)
		{	
			if(spj.emptyJoinInView(jv))
				return true;
		}
		return false;
	}

	CompositePredicateJoin cloneShallow() throws CloneNotSupportedException {

		CompositePredicateJoin a = new CompositePredicateJoin(); 
		List<SourcePredicateJoin> lspj = new ArrayList<SourcePredicateJoin>();

		for(SourcePredicateJoin spj: this.getPjs())
		{
			lspj.add(spj.cloneShallow());
		}
		a.setPjs(lspj);

		List<CompRewriting> l = new ArrayList<CompRewriting>();
		for(CompRewriting ar:this.getRewritings())
		{
			l.add(ar);
		}
		a.setRewritings(l);
		return a;
	}

}

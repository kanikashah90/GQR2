package gqr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

class SourcePredicateJoin extends PredicateJoin {
	private QueryPredicateJoin queryCPJ;
	private List<Pair<String,Pair<String,String>>> equates = new ArrayList<Pair<String,Pair<String,String>>>();	

	@Override
	public int hashCode() {
		String hash = getPredicate().name;
		for(int i=1; i<=getGqrNodes().size(); i++)
		{
			hash+=String.valueOf(i);
			hash+=((GQRNode)getGqrNodes().get(i)).isExistential()?"E":"D";
		}
		return hash.hashCode();
	}
	@Override
	public String toString() {
		String hash = getPredicate().name;
		for(int i=1; i<=getGqrNodes().size(); i++)
		{
			hash+=String.valueOf(i);
			hash+=((GQRNode)getGqrNodes().get(i)).isExistential()?"E":"D";
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return (this.hashCode() == obj.hashCode());
	}

	SourcePredicateJoin(Predicate p) {
		super(p);
	}

	SourcePredicateJoin(final PredicateJoin pj) {
		super(pj.getPredicate());
		setGqrNodes(pj.getGqrNodes());
		setRewritings(pj.getRewritings());
	}

	/**
	 * Adds to every node's infobox, of this PJ, the joinInView description of the newSpj
	 * Also appends the rewritings of this PJ with the newSpj's rewritings
	 * @param newSpj a sourcePredicateJoin
	 * @return this PJ merged with newSpj
	 */
	SourcePredicateJoin mergeWithSameSpj(SourcePredicateJoin newSpj)
	{
		//		SourcePredicateJoin retSpj = new SourcePredicateJoin(newSpj);
		Map<Integer, GQRNode> nod = newSpj.getGqrNodes();
		for(int i=1; i<=nod.size(); i++)
		{
			nod.get(i).getInfobox().getJoinInViews().addAll(this.getGqrNodes().get(i).getInfobox().getJoinInViews());
		}

		newSpj.getRewritings().addAll(this.getRewritings());
		return newSpj;
	}

	/**
	 * Constructs a list of SourcePredicateJoin objects which are all the different potential
	 * PJs a source PJ could map to
	 * @return a list of potential mapped PJs
	 */
	List<SourcePredicateJoin> potentialMappings() {

		List<SourcePredicateJoin> ret = new ArrayList<SourcePredicateJoin>();
		ArrayList<Integer> distinguishedPositions= new ArrayList<Integer>();

		Map<Integer, GQRNode> gnods = getGqrNodes();
		for(int i=1; i<= gnods.size(); i++)
		{ 
			GQRNode nod = gnods.get(i);
			if(!nod.isExistential())
				distinguishedPositions.add(i);
		}

		PowerSet pow = new PowerSet(distinguishedPositions);

		ret.add(new SourcePredicateJoin(this));
		//System.out.println("Adding "+this);

		while(pow.hasNext())
		{
			SourcePredicateJoin spj = new SourcePredicateJoin(this.getPredicate());
			Map<Integer,GQRNode> nodes = new HashMap<Integer, GQRNode>(); 
			Vector v = ((Vector)pow.next());

			for(int i=1; i<=getGqrNodes().size(); i++)
			{
				if(v.contains(i))//make this existential
					nodes.put(i,GQRNode.dummyExistentialNode());
				else
					nodes.put(i,getGqrNodes().get(i));
			}

			spj.setGqrNodes(nodes);
			ret.add(spj);
		}

		return ret;
	}

	@Override
	protected SourcePredicateJoin clone() throws CloneNotSupportedException {
		Map<AtomicRewriting,AtomicRewriting> r2r = new HashMap<AtomicRewriting, AtomicRewriting>();

		SourcePredicateJoin spj = new SourcePredicateJoin(this.getPredicate());	//I still haven't implemented Predicate#clone()
		//TODO maybe we don't have to clone the predicates: IF we do watch out JoinDescription#equals()

		assert(spj.getPredicate().getRepeatedId() !=-1);
		List<AtomicRewriting> rew = new ArrayList<AtomicRewriting>();
		for(AtomicRewriting r: this.getRewritings())
		{
			AtomicRewriting newR = r.clone();
			rew.add(newR);
			r2r.put(r,newR);//keep what we are substituting with what
		}
		spj.setRewritings(rew);

		Map<Integer, GQRNode> map = this.getGqrNodes();
		Map<Integer, GQRNode> ret = new HashMap<Integer, GQRNode>(); 

		for(int i=1; i<=map.size(); i++)
		{
			GQRNode thisNode = map.get(new Integer(i));
			GQRNode clonedNode = thisNode.clone();
			
			//connect cloned rewritings with cloned joinInViews
			int index = 0;
			for(JoinInView jvOriginal : thisNode.getInfobox().getJoinInViews())
			{
				JoinInView jvCloned = clonedNode.getInfobox().getJoinInViews().get(index++);
				assert(jvOriginal.getSourceName().equals(jvCloned.getSourceName()));
				List <AtomicRewriting> clonedRew = new ArrayList<AtomicRewriting>();

				for(AtomicRewriting rOrg: jvOriginal.getRewritings())
				{
					clonedRew.add(r2r.get(rOrg));
				}

				jvCloned.setRewritings(clonedRew);
			}

			ret.put(i,clonedNode);
		}
		spj.setGqrNodes(ret);
		spj.setQueryPJ(this.queryCPJ);

		return spj;
	}

	//I create a more shallow clone than cloneDummy. I don't clone the atomicRewr ,just the infoboxes
	//TODO delete the part where I connect cloned rewritings with cloned joinInViews -- the rewritings are not cloned anymore and it's useless
	protected SourcePredicateJoin cloneShallow() throws CloneNotSupportedException {
//		Map<AtomicRewriting,AtomicRewriting> r2r = new HashMap<AtomicRewriting, AtomicRewriting>();

		SourcePredicateJoin spj = new SourcePredicateJoin(this.getPredicate());		

		assert(spj.getPredicate().getRepeatedId() !=-1);
		List<AtomicRewriting> rew = new ArrayList<AtomicRewriting>();
		for(AtomicRewriting r: this.getRewritings())
		{
//			AtomicRewriting newR = r;
			rew.add(r);
//			r2r.put(r,newR);//keep what we are substituting with what
		}
		spj.setRewritings(rew);

		Map<Integer, GQRNode> map = this.getGqrNodes();
		Map<Integer, GQRNode> ret = new HashMap<Integer, GQRNode>(); 

		for(int i=1; i<=map.size(); i++)
		{
			GQRNode thisNode = map.get(new Integer(i));
			GQRNode clonedNode = thisNode.clone();//haven't yet implemented Predicate#clone()
			//TODO maybe we don't have to clone the predicates
			
			//connect cloned rewritings with cloned joinInViews
			int index = 0;
			for(JoinInView jvOriginal : thisNode.getInfobox().getJoinInViews())
			{
				JoinInView jvCloned = clonedNode.getInfobox().getJoinInViews().get(index++);
				assert(jvOriginal.getSourceName().equals(jvCloned.getSourceName()));
				List <AtomicRewriting> clonedRew = new ArrayList<AtomicRewriting>();

				for(AtomicRewriting rOrg: jvOriginal.getRewritings())
				{
					clonedRew.add(rOrg);
				}

				jvCloned.setRewritings(clonedRew);
			}

			ret.put(i,clonedNode);
		}
		spj.setGqrNodes(ret);
		spj.setQueryPJ(this.queryCPJ);

		return spj;
	}
	
	
	void setQueryPJ(QueryPredicateJoin pjq) {
		queryCPJ = pjq;
	}

	QueryPredicateJoin getQueryCPJ() {
		return queryCPJ;
	}

	/**
	 * 
	 * @param jv
	 * @return
	 */
	boolean emptyJoinInView(JoinInView jv) {
		for(GQRNode gnode: this.getGqrNodes().values())
		{
			List<JoinInView> list = gnode.getInfobox().getJoinInViews();
			this.getRewritings().removeAll(jv.getRewritings());
			Iterator<JoinInView> joiv_it = list.iterator();
			while(joiv_it.hasNext())
			{
				if(joiv_it.next().getSourceName().equals(jv.getSourceName()))
				{	
					joiv_it.remove();
					if(list.isEmpty())
						return true;
					else
						break;//if jv found no point in searching in this infobox anymore
				}
			}
		}
		return false;
	}
	
	void renameRewritingVarsAppending(int i) {
		for(AtomicRewriting re: getRewritings())
		{
			assert(re.getSourceHeads().size() == 1);
			
			List<String> newVars = new ArrayList<String>();
			boolean gotIn = false;
			for(String var: re.getSourceHeads().iterator().next().getSourceHeadVars())
			{
				newVars.add(var.concat("UR"+i));//UNDERSCORE REPEATED id
				if(!gotIn)
					gotIn=true;
			}
			//TODO it will not get in if we face "witnesses", i.e., we use sources but not interested or don't have distnguished vars in their heads
			assert(gotIn);
			re.getSourceHeads().iterator().next().setSourceHeadVars(newVars);
		}
	}

	void addEquate(String string, String string2, String queryVar) {
		Pair<String,String> p = new Pair<String,String>(string,string2); 
		equates.add(new Pair<String,Pair<String,String>>(queryVar,p));
	}
	List<Pair<String, Pair<String, String>>> getEquates() {
		return equates;
	}

}

package gqr;

import gqr.Join.joinTypeInQuery;
import gqr.SelfJoinVariableRecord.VariableType;
//import gr.forth.ics.aggregator.Aggregator;
//import gr.forth.ics.aggregator.DataTypes;
//import gr.forth.ics.aggregator.Database;
//import gr.forth.ics.aggregator.DbFactories;
//import gr.forth.ics.aggregator.Record;
//import gr.forth.ics.aggregator.Schema;
import isi.mediator.SourceQuery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
/**
 * This class implements the main procedure of the GQR algorithm, as described in 
 * [George Konstantinidis, Jose Luis Ambite, Scalable Query Rewriting: A Graph-Based Approach, 
 * in Proceedings of the ACM SIGMOD International Conference on Managment of Data, SIGMOD'11, 
 * June 12-16, 2011, Athens, Greece.]
 * 
 * @author george konstantinidis konstant@usc.edu
 */
public class GQR {

	//the user query to be reformulated
	private DatalogQuery query;

	//map that temporarily holds pairs of partial rewritings that are to be merged while combining their CPJs
	private List<Pair<AtomicRewriting, AtomicRewriting>> merges = new ArrayList<Pair<AtomicRewriting, AtomicRewriting>>();
	//exmerges temporarily holds at each combination step all the to-be-merged partial rewritings due to them being existential
	//we need this so we don't cross-product partial rewritings among different sets of exmerges when concluding the combination step.
	private HashSet<HashSet<AtomicRewriting>> exmerges = new HashSet<HashSet<AtomicRewriting>>();////TODO Wrap up AtomicRewritings in exmerges below, so I can use my own equals for efficient hashing (look at method addExistentialMerge)

	//map that temporarily holds pairs of variables that are to be equated while combining their CPJs
	private List<Pair<String,Pair<Pair<Integer,JoinInView>, Pair<Integer,JoinInView>>>> equates = new ArrayList<Pair<String,Pair<Pair<Integer,JoinInView>, Pair<Integer,JoinInView>>>>();

	//map to index all PJs that are keys of the next map, any pj A which maps to a key pj B of the next map
	//will be a kept in this map, and its value will be a table containing B
	private Map <SourcePredicateJoin,List<SourcePredicateJoin>> indexSourcePJs;

	//map to ``globally'' hold all PJs, if repeated predicates exist in the same source, these are contained in the list for the spj (source pj)
	private Map<SourcePredicateJoin, List<SourcePredicateJoin>> sourcePJs;

	//holds all different query pj patterns asked by retrieveSourcePjs() 
	private Map<SourcePredicateJoin, List<SourcePredicateJoin>> pastReturned = new HashMap<SourcePredicateJoin,List<SourcePredicateJoin>>();

	//Number of the conjunctive rewritings in the answer
	private Integer reNo;
	
	//savedcomParisonJoins
	List<Pair<GQRNode,GQRNode>> comParisonJoins = new ArrayList<Pair<GQRNode,GQRNode>>();
	//savedMap;
	Map<String,Integer> circleSet = new HashMap<String,Integer>();

//	//enum used in recording measurements
//	private enum Variables {
//		Query, ViewNo, Time, RewNo, ExcludedTime
//	}

//	public static void main(String[] args) throws FileNotFoundException {
//
//
//		int run = Integer.parseInt(args[0]);
//
////
////				for(int i=0; i<=9; i++)
////				{
//
//		int viewNo = 5000;
////		while(viewNo<=140)
//		{
////			Database db;
////			try {
////				db = DbFactories.localDerby().getOrCreate("gqrDBchainTo10000_run_"+run);
////			} catch (SQLException e1) {
////				throw new RuntimeException(e1);
////			}
////			Schema schema = new Schema().add(Variables.Query, DataTypes.MED_STRING).
////			add(Variables.ViewNo, DataTypes.INTEGER).
////			add(Variables.Time, DataTypes.LONG).
////			add(Variables.RewNo, DataTypes.INTEGER).
////			add(Variables.ExcludedTime, DataTypes.LONG);
////
////			Aggregator aggregator;
////			try {
////				aggregator = db.getOrCreate(schema, "aggregator");
////				//db.forceCreate(schema, "aggregator");
////				//aggregator = db.get("aggregator");
////			} catch (SQLException e1) {
////				throw new RuntimeException(e1);
////			}
//
//
//
//			System.out.println("********view "+viewNo+" *******");
//
//			long st = System.currentTimeMillis();
//
//			//local random examples
//						GQR g = new GQR(new File(System.getProperty("user.dir")+"/run_"+run+"/query_"+run+".txt"),new File(System.getProperty("user.dir")+"/run_"+run+"/views_for_q_"+run+".txt"), viewNo);
//            //local chain tests
////			GQR g = new GQR(new File(System.getProperty("user.dir")+"/run_"+run+"/views_for_q_"+run+"/queryHD_"+run+".txt"),new File(System.getProperty("user.dir")+"/run_"+run+"/views_for_q_"+run+"/view_139.txt"), viewNo);
//
//			//HPCC random tests		
////						GQR g = new GQR(new File(System.getProperty("user.dir")+"/run_"+run+"/query_"+run+".txt"),new File(System.getProperty("user.dir")+"/run_"+run+"/views_for_q_"+run+".txt"), viewNo);
//			//HPCC chain tests		
////					GQR g = new GQR(new File(System.getProperty("user.dir")+"/run_"+run+"/views_for_q_"+run+"/queryHD_"+run+".txt"),
////									new File(System.getProperty("user.dir")+"/run_"+run+"/views_for_q_"+run+"/view_139.txt"), 
////									viewNo);
//
//			//local macbook
////			GQR g = new GQR(new File(System.getProperty("user.dir")+"/examples/linux_examples/paper_query.txt"),new File(System.getProperty("user.dir")+"/examples/linux_examples/paper_views.txt"), viewNo);
//			
//			
//
//			int recordedViewNo = viewNo;
////
////			if(viewNo < 100)
////			{
////				viewNo+=5;
////			}
////			else if(viewNo <= 120)
////			{
////				viewNo+=20;
////			}
////			else if(viewNo < 1000)
////			{
////				viewNo+=100;
////			}
////			else if(viewNo < 10000)
////			{
////				viewNo+=1000;
////			}
//			
////			FileWriter outFile;
////	        PrintWriter out = null;
////			
////	        try{
////	        	outFile = new FileWriter(System.getProperty("user.dir")+"/result_"+run+".txt",true);
////	        	out = new PrintWriter(outFile,true);
////	        } catch (IOException e){
////	        e.printStackTrace();
////	        }
//
////						List<CompRewriting> ans = new ArrayList<CompRewriting>();
//			try {
////				ans = g.reformulate(g.getQuery());
//				g.reformulate(g.getQuery());
//			} catch (NonAnswerableQueryException e) {
////				//					throw new RuntimeException(e);
//				long end = System.currentTimeMillis();
//				System.out.println("NA Case: "+recordedViewNo+ " Time:" + ((long)((end-st)-g.dontCountTime)));
//
////				out.append(g.getQuery().getName()+"\t"+recordedViewNo+"\t"+(((long)(end-st))-g.dontCountTime)+"\t"+0+"\t"+g.dontCountTime+"\n");
////		        out.close();
//				
////				aggregator.record(
////						new Record().add(Variables.Query, g.getQuery().getName()).
////						add(Variables.ViewNo, recordedViewNo).
////						add(Variables.Time, (long)(((long)(end-st))-g.dontCountTime)).
////						add(Variables.RewNo, 0).
////						add(Variables.ExcludedTime,g.dontCountTime)
////				);
////
////				try {
////					db.shutDown();
////				} catch (SQLException e1) {
////					throw new RuntimeException(e1);
////				}
////				continue;
//			}
//			
//			
//			
//			long end = System.currentTimeMillis();
//			
////			for(CompRewriting cr: ans)
////			{
////				System.out.println(cr);
////			}
//			
////
////			aggregator.record(
////					new Record().add(Variables.Query, g.getQuery().getName()).
////					add(Variables.ViewNo, recorderViewNo).
////					add(Variables.Time, (long)(((long)(end-st))-g.dontCountTime)).
////					add(Variables.RewNo, g.reNo).
////					add(Variables.ExcludedTime,g.dontCountTime)
////			);
////
////			try {
////				db.shutDown();
////			} catch (SQLException e1) {
////				throw new RuntimeException(e1);
////			}
//
////			out.append(g.getQuery().getName()+"\t"+recordedViewNo+"\t"+(((long)(end-st))-g.dontCountTime)+"\t"+g.reNo+"\t"+g.dontCountTime+"\n");
////			out.close();
//			
//			System.out.println("Case: "+recordedViewNo+" Time:" + ((long)((end-st)-g.dontCountTime))+" rewNo:"+g.reNo);
//		}
//
//	}



	public DatalogQuery getQuery() {
		return query;
	}
	
	/**
	 * Initializes a GQR object. Takes a set of pre-processed  views (source definitions), through the IndexViews object, and the query
	 * @param gueryFile the file containing the query
	 * @param viewsFile the file containing the sources
	 * @param numberofsources how many views to use from the view file
	 */
	public GQR(File gueryFile, IndexViews views)
	{
		indexSourcePJs = views.getIndexSourcePJs();
		sourcePJs = views.getSourcePJs();
		query = readQuery(gueryFile);
	}

	private DatalogQuery readQuery(File file) {
		GQRQueryParser qparser = new GQRQueryParser(file,0);
		DatalogQuery query = qparser.parseNextQuery();
		query.computeQueryPJs();
		query.countRepeatedPredicates();
		return query;
	}



	/**
	 * Input: A query Q on the mediation schema
	 * Output: A list of re-writings for the query
	 * @return a list of conjunctive rewritings (using only source/view relations), whose union is a maximally-contained rewriting of the query
	 * @throws NonAnswerableQueryException if some part of the query cannot covered (or answered) by any source/view
	 */
	public final List<CompRewriting> reformulate(DatalogQuery query) throws NonAnswerableQueryException {

		Set<CPJCoverSet> currentCPJSets = new HashSet<CPJCoverSet>(); //(sets of) partial coverings of the query
		Set<CompositePredicateJoin> resultCPJs = new HashSet<CompositePredicateJoin>(); //complete coverings

		Set<PredicateJoin> guery_pjs = query.getQPJs();
		for(PredicateJoin pjq: guery_pjs)
		{
			CPJCoverSet source_pjs = retrieveSourcePJSet((QueryPredicateJoin)pjq); //(a wrapper of) a set of CPJs for this predicate
			currentCPJSets.add(source_pjs);
		}

		while(!currentCPJSets.isEmpty()) //iterates over partial sets of query coverings
		{
			Pair<CPJCoverSet,CPJCoverSet>  p = null;
			try{
				p = select(currentCPJSets); //must always exist a pair until we reach a complete rewriting 
			}catch(NotEnoughCPJsException e){

				
				assert(currentCPJSets.size() == 1);

				CPJCoverSet C = currentCPJSets.iterator().next();
				if(C.getSerialNo() == 1 && query.sumOfPredicatesSerials() ==1)
				{
					assert(query.numberOfPredicates() ==1);
					assert(AssertAllHaveSerialOne(C.getCPJs()));//Assertions only for debugging
					resultCPJs.addAll(C.getCPJs());
					//I change collect rewritings and I am additionally passing the original query (casted as ISI query) 
					//in order to check that the rewritings collected are contained in the query

					return collectRewritings(resultCPJs,Util.castQueryAsISISourceQuery(query));
				}


				throw new NonAnswerableQueryException();

			}

			CPJCoverSet A = p.getA();
			CPJCoverSet B = p.getB();

			//combine A,B remove them and put C in their place

			CPJCoverSet C = combineSets(A,B); 

			//again add the no conflict PLT
			if(comParisonJoins.size()>0){
			System.out.println("have PLT but is ok");

			for(int i =0;i<comParisonJoins.size();i++){
				GQRNode	sourceVarNode1 = comParisonJoins.get(i).getA();
				GQRNode	sourceVarNode2 = comParisonJoins.get(i).getB();
				for(JoinInView jv1: sourceVarNode1.getInfobox().getJoinInViews())
				{
					for(JoinInView jv2: sourceVarNode2.getInfobox().getJoinInViews())
					{

						addEquate(jv1, jv1.getHeadPosition(), jv2, jv2.getHeadPosition(),null/*sourceVarNode1.getQueryDistinguishedVar()*/);

				
					}
				}
			}
			
			
		}

			if(C.isEmpty())
				throw new NonAnswerableQueryException();

			currentCPJSets.remove(A);
			currentCPJSets.remove(B);

			if(C.getSerialNo() == query.sumOfPredicatesSerials())//we reached a complete cover
				resultCPJs.addAll(C.getCPJs());
			else
				currentCPJSets.add(C);  
		}

		//		reNo = countRewritings(resultCPJs);

		
		return collectRewritings(resultCPJs,Util.castQueryAsISISourceQuery(query));
	}


	private Integer countRewritings(Set<CompositePredicateJoin> resultCPJs) {
		int i =0;
		for(CompositePredicateJoin rc: resultCPJs)
			i += rc.getRewritings().size();
		return i;
	}

	private boolean AssertAllHaveSerialOne(List<CompositePredicateJoin> AltCpjs) {

		for(CompositePredicateJoin cpj: AltCpjs)
		{
			assert(cpj.getPjs().size() == 1);
			assert(((SourcePredicateJoin)cpj.getPjs().iterator().next()).getQueryCPJ().getSerialNumber() == 1);
		}
		return true;
	}

	private List<CompRewriting> collectRewritings(Set<CompositePredicateJoin> resultCPJs, SourceQuery inputQuery) {
		List<CompRewriting> res = new ArrayList<CompRewriting>();	
		reNo =0;

		for(CompositePredicateJoin rc: resultCPJs)
		{	
			for(CompRewriting cr: rc.getRewritings())
			{
				reNo++;
				CompRewriting nc = renameRewrAndReturnIt(cr);
				boolean test = assertContainedInQuery(nc, inputQuery);
				
				assert(assertContainedInQuery(nc,inputQuery));
				res.add(nc);
			}
		}

		return res;
	}



	private boolean assertContainedInQuery(CompRewriting nc,
			SourceQuery inputQuery) {
		String exp = nc.getExpansion();
		System.out.println(nc.getExpansion());
		SourceQuery expansion = Util.castQueryAsISISourceQuery(DatalogQuery.parseQueryIntoDatalog(exp));
		if(expansion.isContained(inputQuery))
			return true;
		else{ 
			throw new RuntimeException("rewriting "+nc+" not contained in the query \n"+" Rewriting expansion:"+expansion+"\n Query: "+inputQuery+" \n");
		}
	}


	private CompRewriting renameRewrAndReturnIt(CompRewriting newcr) {

		newcr = enforceMergesAndEquates(newcr);
		return newcr;
	}

	private CompRewriting enforceMergesAndEquates(CompRewriting cr) {

		for(Set<AtomicRewriting> s: cr.getMerges() )
		{
			mergeSetViewsInRw(s,cr);
		}

		CompRewriting newr = null;

		try {
			newr = cr.cloneDummy();   

		} catch (CloneNotSupportedException e1) {
			throw new RuntimeException(e1);
		}


		for(Entry<String,Set<String>> e: newr.getEquates().entrySet())
		{
			Set<String> vars = e.getValue();
			String var2 = e.getKey();

			substituteVarsbyOtherVar(newr, vars, var2);
		}

		return newr;
	}

	private void mergeSetViewsInRw(Set<AtomicRewriting> s, CompRewriting cr) {

		AtomicRewriting at1 = s.iterator().next();
		assert(at1.getSourceHeads().size() == 1);
		SourceHead s2 = at1.getSourceHeads().iterator().next();

		SourceHead newsh = new SourceHead(s2.getSourceName());
		try {
			newsh.setQuery(s2.getQuery().clone());
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		boolean uninitialized = true;
		for(AtomicRewriting jv: s)
		{
			AtomicRewriting at = (cr.getAtomicRewritings().contains(jv)?jv:null);

			if(at == null) //if this is null it must be because we already enforced the merges for this compRewriting so we went to the next statement and 
				//removed the atomic rewriting involved in the merge
			{
				return;
			}

			at = cr.removeAtomic(jv);

			assert(at.getSourceHeads().size() == 1);
			
			SourceHead s1 = at.getSourceHeads().iterator().next();

			for(int i =0; i<s1.getSourceHeadVars().size(); i++)
			{
				if(uninitialized)//need to put something there
				{
					newsh.addSourceHeadVar(s1.getSourceHeadVars().get(i));
				}
				else
				{
					//TODO "DC" and all these strings could be (in a rare case) inserted by a user in a variables name so code will break. Use an object with fields rather than a string with delimeters.
					String varS1 = s1.getSourceHeadVars().get(i);
					String already_in = newsh.getSourceHeadVars().get(i);
					if(!already_in.startsWith("DC") && !varS1.startsWith("DC"))
					{
						cr.addEquate(varS1, already_in, null);
					}
					else if(!varS1.startsWith("DC"))
					{
						assert(already_in.startsWith("DC"));
						newsh.setSourceHeadVar(i, varS1);
					}
					else if(already_in.startsWith("DC"))
					{
						newsh.setSourceHeadVar(i,"DC");
					}
				}

			}
			uninitialized = false;
		}


		AtomicRewriting at3 = new AtomicRewriting();
		at3.addSourceHead(newsh);
		cr.add(at3);	
	}

	private void substituteVarsbyOtherVar(CompRewriting cr, Set<String> vars,
			String var2) {

		for(AtomicRewriting at:cr.getAtomicRewritings())
		{
			assert(at.getSourceHeads().size() == 1);
		
			SourceHead s1 = at.getSourceHeads().iterator().next();
			ListIterator<String> l_it = s1.getSourceHeadVars().listIterator();
			while(l_it.hasNext())
			{
				if(vars.contains(l_it.next()))
				{	
					l_it.set(var2);
				}
			}
		}

		ListIterator<String> l_it = cr.getHeadVariables().listIterator();

		while(l_it.hasNext())
		{
			if(vars.contains(l_it.next()))
			{	
				l_it.set(var2);
			}
		}


	}

	/**
	 * 
	 * @param setA
	 * @param setB
	 * @return a set of CPJs combinations of setA, setB
	 */
	private CPJCoverSet combineSets(CPJCoverSet setA, CPJCoverSet setB) {

		CPJCoverSet r = new CPJCoverSet();
		r.setSerialNo(setA.getSerialNo()+setB.getSerialNo());


		for(CompositePredicateJoin sa:setA.getCPJs())
			for(CompositePredicateJoin sb:setB.getCPJs())
			{	
				CompositePredicateJoin c = combineCPJs(sa,sb);
				if(c != null)//null means uncombinable
					r.add(c);
			}
		

		return r;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return a CPJ, combination of a,b or null if a,b uncombinable
	 */
	private CompositePredicateJoin combineCPJs(CompositePredicateJoin a, CompositePredicateJoin b) {

		try {
			a = a.cloneShallow();
			b = b.cloneShallow();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		CompositePredicateJoin c = new CompositePredicateJoin();
		exmerges = new HashSet<HashSet<AtomicRewriting>>();
		
		//An list to store the PLT info node

		for(Join j: lookupJoins(a,b))
		{
			//			System.out.println("Join :"+j);
			//TODO change names in the Join object, node1, getA, getB etc.
			//also change name to variables here, e.g., p1, jv1 etc.
			Pair<SourcePredicateJoin,Integer> spjOfAandEdge = j.node1;
			GQRNode sourceVarNode1 = spjOfAandEdge.getA().getGqrNodes().get(spjOfAandEdge.getB());
			//join also returns which pairs spjs (of a and b) are joined only distinguished
			//			spj_joins = j.getDistinguishedJoinedSPJs(a,b);

			//if all pairs of joins can be minimized p.canMinimize will be true 

			Pair<SourcePredicateJoin,Integer> spjOfBandEdge = j.node2;
			GQRNode sourceVarNode2 = spjOfBandEdge.getA().getGqrNodes().get(spjOfBandEdge.getB());
			int edgeNo2 = spjOfBandEdge.getB();
			

			if((sourceVarNode1.isExistential()!=sourceVarNode2.isExistential()) || (sourceVarNode1.isExistential() &&(j.jt == joinTypeInQuery.D)))
				return null;
			else if (sourceVarNode1.isExistential())
			{
				//		for all sources S in va's infobox do
				//		if S contains a join description for v2 then

				Map<Integer,Boolean> sources_of_jv2_found_in_jv1 = new HashMap<Integer,Boolean>();
//				for(int i=0; i<sourceVarNode2.getInfobox().getJoinInViews().size(); i++)
//					sources_of_jv2_found_in_jv1.put(i,false);
				
				int index_of_jv2_sources = 0;

				List<JoinInView> newListJv1 = new ArrayList<JoinInView>(sourceVarNode1.getInfobox().getJoinInViews());
				for(JoinInView jv1: newListJv1)
				{
					boolean same_view_found_in_jv2 = false;//after this for is over if this is false I'm dropping jv1

					index_of_jv2_sources = 0;

					List<JoinInView> newListJv2 = new ArrayList<JoinInView>(sourceVarNode2.getInfobox().getJoinInViews());
					for(JoinInView jv2: newListJv2)
					{
						index_of_jv2_sources++; //TODO check this is correct 
						
						if(jv1.getSourceName().equals(jv2.getSourceName()))//we're talking about the smae view
						{
							try {
								sources_of_jv2_found_in_jv1.put(index_of_jv2_sources-1,true); //I'm keeping a list with all jvs in infobox2
							} catch (IndexOutOfBoundsException e) {
								throw new RuntimeException(e);
							}
							//if jv2 found exists also in infobox1 I set its pointer true; 

							same_view_found_in_jv2 = true; //I don't have to drop jv1 for sure since I found it in infobox2
							// I still might drop it if next flag doesn't go true;
							boolean once_found_in_jv2_preserves_join = false;

							for(JoinDescription jdOfSourceNode1: jv1.getJoinDescriptions())
							{	
								if(jdOfSourceNode1.equals(new JoinDescription(j.node2.getA().getPredicate(), edgeNo2)))
								{

									assert(jv1.getRewritings().size() == 1);
									assert(jv2.getRewritings().size() == 1);
									addMerge(jv1.getRewritings().iterator().next(),jv2.getRewritings().iterator().next());

									//since the rewriting for jv1 is going to be merged with some other rewritings
									// (this means there are some views/jvs that "preserve" (existentially) the joins of jv1)
									// then the rewriting for jv1 should NOT be cross-producted with all the rest rewritings apart from those merged
									// "existential merges" records the merges done for exactly this reason.
									addExistentialMerge(jv2.getRewritings().iterator().next(),jv1.getRewritings().iterator().next());
									//TODO addMerge and addExistentialMerge can be optimized and merged to one function
									once_found_in_jv2_preserves_join = true;
									break;
								}
							}

							//if not found in jv2 DROP
							//		if va infobox is empy  then
							//		return null;
							if(!once_found_in_jv2_preserves_join)
							{
								if(a.emptyJoinInView(jv1)|| b.emptyJoinInView(jv2))//TODO check this!! This dropping the view from the pj across all coversets it belongs not just this one! is this what we want?
									return null; //null means at least one sourcepredicatejoin in a or b is left with no rewritings
								//therefore they are uncombinable
							}

						}


						assert(jv1.getRewritings().size() == 1);
						assert(containsExactlyOncePerCompRew(a.getRewritings(),jv1.getRewritings().iterator().next()));
						assert(jv2.getRewritings().size() == 1);
						assert(containsExactlyOncePerCompRew(b.getRewritings(),jv2.getRewritings().iterator().next()));
						
					}

					if(!same_view_found_in_jv2)
						if(a.emptyJoinInView(jv1))
							return null;
				}

				for(int i =0; i<sources_of_jv2_found_in_jv1.size(); i++)//for all sources of jv2
					if(sources_of_jv2_found_in_jv1.get(i)==null || sources_of_jv2_found_in_jv1.get(i) !=true)//if the source is not common with jv1
						if(b.emptyJoinInView(sourceVarNode2.getInfobox().getJoinInViews().get(i)))
							return null;
			}
			else
			{
				//TODO change this for constants; they have jv.getHeadPosition == -1
				assert(!sourceVarNode2.isExistential());
				//		repeat
				//		for all pairs, get a pair sources (s1; s2) from infobox of va
				//		and vb respectively
				//		if s1 = s2 and we can "simplify" (-->WITNESS PROBLEM)
				//		addMerge(s1; va; vb)
				//		else
				//		addEquate(s1; va; s2; vb)
				//		until until all possible pairs of sources are chosen
				
				
				
				//@param:sourceVarNode1 join node in one set(Say p1(x,y))
				//@param:sourceVarNode2 join node in another set(Say p3(x,y))
				//Compare nodes to see if there is any circle
				
				//check circle 
				boolean isPLT =false;

				
				JoinInView jvA =sourceVarNode1.getInfobox().getJoinInViews().get(0);
				JoinInView jvB =sourceVarNode2.getInfobox().getJoinInViews().get(0);
				
				
				//TODO: Find out if one of they are from the same view && this view has a comparison predicate. 
				//2 situations 
				//1. Predicate (join PLT) Vs Predicate(join PLT)
				//2. PLT VS Predicate(join PLT)
				
				// PLT VS Predicate(join PLT) 

				if(spjOfAandEdge.getA().getQueryCPJ().getPredicate().isComparison()){
					//A is PLT
					int edgeA = spjOfAandEdge.getB();
					int edgeB =edgeA;
					//check the join PLT

					for(JoinDescription jd2 :jvB.getJoinDescriptions()){
						System.out.println("Join JoinPredicate name: "+ jd2.toString());
						if(jd2.getPredicate().isComparison()){
							edgeB = jd2.getEdgeNo();
							break;
						}
					}
					
					if(edgeA != edgeB){
						
						System.out.println("Add Comprision PLT");
						Pair<String,Integer> pairA = new Pair<String,Integer>(jvA.getSourceName(),edgeA);
						Pair<String,Integer> pairB = new Pair<String,Integer>(jvB.getSourceName(),edgeB);
						//checking circle
						System.out.println("Pair A: "+pairA.toString()+" Pair B: "+pairB);
						
						System.out.println("Set: "+circleSet.toString());
	
						System.out.println("\n set: "+circleSet.toString());

						if(circleSet.containsKey((jvA.getSourceName()))||circleSet.containsKey(jvB.getSourceName())){
							System.out.println("Find PLT Circle Conflict");
							circleSet.clear();
							comParisonJoins.clear();
							return null;
						}
						
						comParisonJoins.add(new Pair(sourceVarNode1,sourceVarNode1));

						circleSet.put(jvA.getSourceName(),edgeA);
						circleSet.put(jvB.getSourceName(),edgeB);
						System.out.println("PLT A"+pairA.toString()+" PLT B"+pairB.toString()+"\n set: "+circleSet.toString());
//						System.out.println(circleList.toString());
						isPLT= true;
					}
				}else if(spjOfBandEdge.getA().getQueryCPJ().getPredicate().isComparison()){
					int edgeB = spjOfBandEdge.getB();
					int edgeA =	edgeB;
					//check the join PLT
					for(JoinDescription jd1 :jvA.getJoinDescriptions()){
						System.out.println("Join JoinPredicate name: "+ jd1.toString());
						if(jd1.getPredicate().isComparison()){
							edgeA = jd1.getEdgeNo();
							break;
						}
					}
					
					if(edgeA != edgeB){
						System.out.println("Add Comprision PLT");

						Pair<String,Integer> pairA = new Pair<String,Integer>(jvA.getSourceName(),edgeA);
						Pair<String,Integer> pairB = new Pair<String,Integer>(jvB.getSourceName(),edgeB);
						//checking circle
						
						System.out.println("Pair A: "+pairA.toString()+" Pair B: "+pairB);
						
						System.out.println("Set: "+circleSet.toString());

						System.out.println("\n set: "+circleSet.toString());

						if(circleSet.containsKey((jvA.getSourceName()))||circleSet.containsKey(jvB.getSourceName())){
							System.out.println("Find PLT Circle Conflict");
							circleSet.clear();
							comParisonJoins.clear();
							return null;
						}
						
						
						comParisonJoins.add(new Pair(sourceVarNode1,sourceVarNode1));
					
						
						circleSet.put(jvA.getSourceName(),edgeA);
						circleSet.put(jvB.getSourceName(),edgeB);
						System.out.println("PLT A"+pairA.toString()+" PLT B"+pairB.toString()+"\n set: "+circleSet.toString());

						//						System.out.println(currentCircle.toString());
						isPLT= true;
					}
				}else {
					int edgeB = -1;
					int edgeA =	-1;
					//predicate A join PLT
					
					for(JoinDescription jd1 :jvA.getJoinDescriptions()){
						System.out.println("Join JoinPredicate name: "+ jd1.toString());
						if(jd1.getPredicate().isComparison()){
							edgeA = jd1.getEdgeNo();
							
							break;
						}
					}
					
					edgeB = edgeA;
					//predicate B join PLT

					for(JoinDescription jd2 :jvB.getJoinDescriptions()){
						System.out.println("Join JoinPredicate name: "+ jd2.toString());
						if(jd2.getPredicate().isComparison()){
							edgeB = jd2.getEdgeNo();
							break;
						}
					}
					
					if(edgeA>0&&edgeB>0&&edgeA != edgeB){
						System.out.println("Add Comprision PLT");

						Pair<String,Integer> pairA = new Pair<String,Integer>(jvA.getSourceName(),edgeA);
						Pair<String,Integer> pairB = new Pair<String,Integer>(jvB.getSourceName(),edgeB);
						
						//checking circle
						
						System.out.println("\n set: "+circleSet.toString());

						if(circleSet.containsKey((jvA.getSourceName()))||circleSet.containsKey(jvB.getSourceName())){
							System.out.println("Find PLT Circle Conflict");
							circleSet.clear();
							comParisonJoins.clear();
							return null;
						}
						
						comParisonJoins.add(new Pair(sourceVarNode1,sourceVarNode1));
					
						
						circleSet.put(jvA.getSourceName(),edgeA);
						circleSet.put(jvB.getSourceName(),edgeB);
						System.out.println("PLT A"+pairA.toString()+" PLT B"+pairB.toString()+"\n set: "+circleSet.toString());

						//						System.out.println(currentCircle.toString());
						isPLT= true;
					}
					
					
				}
				
				//check circle 
				
				
				
				
				
		
				
				if(!isPLT){
					
					System.out.println("no PLT");
					for(JoinInView jv1: sourceVarNode1.getInfobox().getJoinInViews())
					{
						for(JoinInView jv2: sourceVarNode2.getInfobox().getJoinInViews())
						{
							assert(jv1.getRewritings().size() == 1);
							assert(containsExactlyOncePerCompRew(a.getRewritings(),jv1.getRewritings().iterator().next()));
							assert(jv2.getRewritings().size() == 1);
							assert(containsExactlyOncePerCompRew(b.getRewritings(),jv2.getRewritings().iterator().next()));
							addEquate(jv1, jv1.getHeadPosition(), jv2, jv2.getHeadPosition(),null/*sourceVarNode1.getQueryDistinguishedVar()*/);

						
						}
					}
				}
			}	
		}
		
		// if no circle add equate back the PLT

		
		
		//TODO if p satisfies Def 4.2
		//		if(p.canMinimize())
		//		//then potential merges can be added
		//		for(Pair<AtomicRewriting,AtomicRewriting> pair_merges: p.potentialMerges())
		//			addMerge(pair_merges.getA(),pair_merges.getA());

		c.add(a);
		c.add(b);

		List<CompRewriting> compRew = crossProductRewritings(a,b);

		c.setRewritings(appendMergesAndEquates(compRew));		

		return c;
	}


	private boolean containsExactlyOncePerCompRew(List<CompRewriting> rewritings,
			AtomicRewriting atr) {
		for(CompRewriting cr : rewritings)
		{
			int i = cr.getAtomicRewritings().indexOf(atr);
			if(i == -1)
				return true;
			int j = cr.getAtomicRewritings().lastIndexOf(atr);
			if(j != i)
				return false;
		}
		return true;
	}

	private List<CompRewriting> appendMergesAndEquates(List<CompRewriting> compRew) {

		for(Pair<String,Pair<Pair<Integer,JoinInView>,Pair<Integer,JoinInView>>> pwithq: equates)
		{
			String queryVar = pwithq.getA();
			Pair<Pair<Integer,JoinInView>,Pair<Integer,JoinInView>> p = pwithq.getB();
			Pair<Integer,JoinInView> p1 = p.getA();
			Pair<Integer,JoinInView> p2 = p.getB();

			for(CompRewriting cr: compRew)
			{
				addEquateViewsInRw(p1.getB(),p1.getA(),p2.getB(),p2.getA(),cr, queryVar);
			}
		}

		equates = new ArrayList<Pair<String, Pair<Pair<Integer, JoinInView>, Pair<Integer, JoinInView>>>>();

		//Imagine the set of cr.getMerges() contains {at1,at2} and this.merges contains two
		//pairs {at1,at3} and {at3,at4}.. Then all merges should go into the same set in cr
		//however if {at3,at4} goes in the loop first this will end up in an independent set of cr.getMerges
		//then 
		//hence we need to "merge" the merges in this.Merge before adding them to the cr

		//TODO I keep pairs in this.merges If I had sets it would be better
		//mergeMergesNotConnectedThroughRewriting(merges);
		for(Pair<AtomicRewriting,AtomicRewriting> p: merges )
		{

			AtomicRewriting at1 = p.getA();
			AtomicRewriting at2 = p.getB();

			for(CompRewriting cr:  compRew)
			{
				addMergeViewsInRw(at1,at2,cr);
			}
		}
		merges = new ArrayList<Pair<AtomicRewriting,AtomicRewriting>>();



		return compRew;
	}

	private void addEquateViewsInRw(JoinInView jv1, Integer arg1, JoinInView jv2,
			Integer arg2, CompRewriting compRew, String queryVar) {

		assert(jv1.getRewritings().size() ==1);
		assert(jv2.getRewritings().size() ==1);

		AtomicRewriting at1 = (compRew.getAtomicRewritings().contains(jv1.getRewritings().iterator().next())?jv1.getRewritings().iterator().next():null);
		AtomicRewriting at2 = (compRew.getAtomicRewritings().contains(jv2.getRewritings().iterator().next())?jv2.getRewritings().iterator().next():null);

		if((at1 == null) || at2 == null)
		{
			return;
		}

		assert(at1.getSourceHeads().size() == 1);
		assert(at2.getSourceHeads().size() == 1);

		SourceHead s1 = at1.getSourceHeads().iterator().next();
		SourceHead s2 = at2.getSourceHeads().iterator().next();

		compRew.addEquate(s1.getSourceHeadVars().get(arg1),s2.getSourceHeadVars().get(arg2),queryVar);

	}

	private void addMergeViewsInRw(AtomicRewriting jv1, AtomicRewriting jv2,
			CompRewriting compRew) {

		AtomicRewriting at1 = (compRew.getAtomicRewritings().contains(jv1)?jv1:null);
		AtomicRewriting at2 = (compRew.getAtomicRewritings().contains(jv2)?jv2:null);

		if((at1 == null) || at2 == null)
			return;

		assert(at1.getSourceHeads().size() == 1);
		assert(at2.getSourceHeads().size() == 1);

		SourceHead s1 = at1.getSourceHeads().iterator().next();
		SourceHead s2 = at2.getSourceHeads().iterator().next();

		assert(s1.getSourceName().equals(s2.getSourceName()));
		compRew.addMerge(jv1,jv2);	
	}

	private List<CompRewriting> crossProductRewritings(CompositePredicateJoin a,
			CompositePredicateJoin b) {
		List <CompRewriting> newR = new ArrayList<CompRewriting>();
		for(CompRewriting crA: a.getRewritings())
		{
			for(CompRewriting crB: b.getRewritings())
			{

				boolean existsMergeForCra = false;

				for(HashSet<AtomicRewriting> mergeSet: exmerges)
				{
					boolean toBeMergedWithCrb = false;

					for(AtomicRewriting at1 : mergeSet)
					{	
						if(crA.getAtomicRewritings().contains(at1)) //there is an existential merge for this rewriting
						{
							existsMergeForCra = true;
							for(AtomicRewriting atMergedWithat1: mergeSet)
							{
								if(crB.getAtomicRewritings().contains(atMergedWithat1))// if crB does not contain any of the jvMergedWithjv1 there's no need to crossproduct it
								{
									toBeMergedWithCrb = true; 
									//if it does, we do crossproduct it
									newR.add(crA.merge(crB));// crB contains an atomic term (i.e., atomic rewriting) which is supposed to be merged with a term from crA   
									break;
								}
							}
							break;
						}
					}

					if(toBeMergedWithCrb)
						break;
				}

				if(!existsMergeForCra)
					newR.add(crA.merge(crB));

			}
		}
		return newR;
	}


	private void addEquate(JoinInView jv1, int arg1, JoinInView jv2,
			int arg2, String queryVar) {

		Pair<Integer,JoinInView> a = new Pair<Integer, JoinInView>(arg1, jv1);
		Pair<Integer,JoinInView> b = new Pair<Integer, JoinInView>(arg2, jv2);
		Pair<Pair<Integer,JoinInView>, Pair<Integer,JoinInView>> p = new Pair<Pair<Integer,JoinInView>, Pair<Integer,JoinInView>>(a, b);
		Pair<String,Pair<Pair<Integer,JoinInView>, Pair<Integer,JoinInView>>> pwithq = new Pair<String,Pair<Pair<Integer,JoinInView>, Pair<Integer,JoinInView>>>(queryVar, p);
		equates.add(pwithq);
	}


	private void addMerge(AtomicRewriting at1, AtomicRewriting at2) {
		Pair<AtomicRewriting, AtomicRewriting> p = new Pair<AtomicRewriting,AtomicRewriting>(at1,at2);
		merges.add(p);
	}

	private void addExistentialMerge(AtomicRewriting at1, AtomicRewriting at2) {

		boolean found = false;
		for(HashSet<AtomicRewriting> setMerges: exmerges)
		{
			if(setMerges.contains(at1) || setMerges.contains(at2))
			{	
				found = true;
				setMerges.add(at1);
				setMerges.add(at2);
			}

		}

		if(!found)
		{
			HashSet<AtomicRewriting> s = new HashSet<AtomicRewriting>();
			s.add(at1);
			s.add(at2);
			exmerges.add(s);
		}
	}

	/**
	 * Constructs the joins among pairs of pjs. Each member of each pair belongs to exactly to one of 
	 * the CPJs a or b.
	 * Each Join object returned in the list, contains two source pjs (which cover query pjs) and 
	 * whose join covers a corresponding joined subgoal in the query.
	 * The covered query pjs can be joined on an existential or a distinguished query variables; allowing the 
	 * corresponding variable types in the source pjs.
	 * @param a
	 * @param b
	 * @return
	 */
	private List<Join> lookupJoins(CompositePredicateJoin a, CompositePredicateJoin b) {

		List<Join> joins = new ArrayList<Join>();

		for(SourcePredicateJoin spj1: a.getPjs())
		{	
			QueryPredicateJoin q1 = spj1.getQueryCPJ();

			for(SourcePredicateJoin spj2: b.getPjs())
			{	
				QueryPredicateJoin q2 = spj2.getQueryCPJ();

				for(Entry<Integer,GQRNode> node1OfQ: q1.getGqrNodes().entrySet())
				{
					for(Entry<Integer,GQRNode> node2OfQ: q2.getGqrNodes().entrySet())
					{					
						//this is a dummy for - only one joininview will be retrieved 
						//describing the query joins of this variable
						for(JoinInView joinsofnode1ofQ: node1OfQ.getValue().getInfobox().getJoinInViews())
						{
							assert(node1OfQ.getValue().getInfobox().getJoinInViews().size() == 1);

							//this is a dummy for - only one joininview will be retrieved 
							//describing the query joins of this variable

							for(JoinInView joinsofnode2ofQ: node2OfQ.getValue().getInfobox().getJoinInViews())
							{
								assert(node2OfQ.getValue().getInfobox().getJoinInViews().size() == 1);
								assert(joinsofnode1ofQ.getSourceName().equals(joinsofnode2ofQ.getSourceName()));

								//that is the name of the query

								//all the joindescriptions that the query variable (covered by a source variable
								//attached in a source pj in cpj b) participates in.
								for(JoinDescription jd2 :joinsofnode2ofQ.getJoinDescriptions())
								{
									//all the joindescriptions that the query variable (covered by a source variable
									//attached in a source pj in cpj a) participates in. 
									for(JoinDescription jd1: joinsofnode1ofQ.getJoinDescriptions())
									{	

										if(jd1.equalsIgnoreRepeatedID(new JoinDescription(q2.getPredicate(),node2OfQ.getKey()))
												&&
												jd2.equalsIgnoreRepeatedID(new JoinDescription(q1.getPredicate(),node1OfQ.getKey())))//second part of if might be redundant; TODO check this
										{
											Join j = new Join(new Pair<SourcePredicateJoin,Integer>(spj1,node1OfQ.getKey()),new Pair<SourcePredicateJoin,Integer>(spj2,node2OfQ.getKey()));
											j.jt = node1OfQ.getValue().isExistential()?joinTypeInQuery.E:joinTypeInQuery.D;
											
//											assert(j.jt == (node2OfQ.getValue().isExistential()?joinTypeInQuery.E:joinTypeInQuery.D));
											joins.add(j);	

										}
									}
								}								
							}
						}
					}
				}
			}
		}

		return joins;
	}


	private Pair<CPJCoverSet,CPJCoverSet> select (Set<CPJCoverSet> currentCPJSets) throws NotEnoughCPJsException{

		Iterator<CPJCoverSet> it = currentCPJSets.iterator();

		CPJCoverSet cpjCSet1;
		CPJCoverSet cpjCSet2;

		try{
			cpjCSet1 = it.next();
			cpjCSet2 = it.next();

		}catch (NoSuchElementException e) {
			throw new NotEnoughCPJsException();
		}

		return new Pair<CPJCoverSet,CPJCoverSet>(cpjCSet1,cpjCSet2);
	}

	private CPJCoverSet retrieveSourcePJSet(QueryPredicateJoin pjq) throws NonAnswerableQueryException {

		//indexSourcePJs:
		//map to index all PJs that are keys of the next map, any query pj A which maps to a different source PJ B , also key of the next map
		//will be a kept of this map, and its value will be a list containing B

		//sourcePJs:
		//map to ``globally'' hold all PJs, if repeated predicates exist in the same source, the different patterns created for capturing these repeated predicates are contained in the list for the spj 

		SourcePredicateJoin askfor = new SourcePredicateJoin(pjq); 
//		System.out.println("RETRIEVING_PJS_FOR "+pjq.variablePatternStringSequence());
		CPJCoverSet cset = new CPJCoverSet(); //this is the set of the alternative spjs that will cover the query pj 'askfor'
		List<SourcePredicateJoin> indexes = indexSourcePJs.get(askfor); //take all distinct pjs that askfor maps onto
		if(indexes != null)
		{
			for(SourcePredicateJoin key: indexes) //every alternative pj (here 'key') could appear more than once in the subsequent list (repeated predicates)
			{
//				System.out.println("PJ_RETRIEVED "+key);
				List<SourcePredicateJoin> l = sourcePJs.get(key); //so take the list of all repeated pjs for key

				if(l != null) //if there is one
				{
					l = cloneIfReturnedInPast(key,l); //TODO need to check this! if breaking later do we still clone here? we shouldn't! (optimization)

					assert(assertRepeatedIds(l));

					Iterator<SourcePredicateJoin> l_it= l.iterator(); //take all repeated predicates for this specific key
					while(l_it.hasNext())// TODO When we are to fail, we shouldn't iterate over all repeated patterns (since they are the same) --think: can we do that?
					{
						SourcePredicateJoin s_pj = l_it.next();//

						SelfJoinVariableMap selfjoinmap = new SelfJoinVariableMap();
						SelfJoinVariableMap selfjoinmap4sources = new SelfJoinVariableMap();//this is for the rare case where the query does not have a self join but the sources have. If the query (and hence the source) variables are distinguished we can equate the query vars so as to rewrite with the selfjoined source

						boolean breakNodeIteration = false;
						for(Entry<Integer,GQRNode> queryNodeEntry: pjq.getGqrNodes().entrySet()) //for the query pj, take all its nodes
						{

							GQRNode sourceNode = s_pj.getGqrNodes().get(queryNodeEntry.getKey()); //for one such repeated predicate iterate over the same nodes as in the query pj

							GQRNode qNode = queryNodeEntry.getValue();

							if(sourceNode.isExistential())
							{
								if(qNode.isExistential()) //If both the sourcenode and the query node are existential assert that the source one contains at least the query one's joins, in some of its sourceboxes (joininviews)
								{
									//TODO I disabled the optimization below. In order for this to work we need to "deep" clone the sourcePJs.get(key) above 
									//and I don't know if it worths it -- UPDATE I enable it again as too many exceptions have risen.
									//Past this point my original implementation assumed this true

									//I'm cloning the infobox so I don't get a ConcurrentModificationException
									List<JoinInView> cloneListViews  = new ArrayList<JoinInView>(sourceNode.getInfobox().getJoinInViews()); 

									for(JoinInView jv:cloneListViews) //for all the source node's sourceboxes in its infobox
									{
										//if the sourcebox does not contain the corresponding query join decriptions
										if(!jv.containsDescriptionsIgnoreRepID(qNode.getInfobox().getJoinInViews().iterator().next().getJoinDescriptions()))//should have exactly one joinInView since it's a query 
										{	
											if(s_pj.emptyJoinInView(jv))//drop sourcebox (i.e, joininview) from source_pj's infoboxes, and if we dropped all return true
											{
												l_it.remove();//if infoboxes of s_pj became empty remove s_pj from l
												breakNodeIteration = true;//no need to go to go other nodes of the same PJ
												break;//we're out of sourceboxes
											}
										}
										else //sourcebox contains query's join descriptions
										{
											SelfJoinVariableRecord rec = new SelfJoinVariableRecord(VariableType.Existential,jv.getSourceVarName());


											if(!selfjoinmap.addTemp(""+qNode.getVariable().name+jv.getSourceName(),rec,jv.getRewritings().iterator().next())) //if we cannot cover the underlying query selfjoin
												if(s_pj.emptyJoinInView(jv))//drop sourcebox (i.e, joininview) from source_pj's infoboxes, and if we dropped all return true
												{
													l_it.remove();//if infoboxes of s_pj became empty remove s_pj from l
													breakNodeIteration = true;//no need to go to go other nodes of the same PJ
													break;//we're out of sourceboxes
												}
										}
									}

									if(breakNodeIteration)
										break;
								}
								else
									throw new RuntimeException("a distinguished query variable was covered by an existential one"); //should never come here
							}
							else if(!qNode.isExistential()) //if both source and the query node are distinguished, add an equate between the source node and the query node's name, on the source PJs
							{
//								sourceNode.addQueryDistinguishedVar(qNode.getVariable().name);	
								
								for(JoinInView jv: sourceNode.getInfobox().getJoinInViews())
								{
									assert(jv.getRewritings().size() == 1);
									assert(jv.getRewritings().iterator().next().getSourceHeads().size() == 1);
									

									//TODO change this for constants
//									s_pj.addEquate(qNode.getVariable().name, jv.getRewritings().iterator().next().getSourceHeads().iterator().next().getSourceHeadVars().get(jv.getHeadPosition()), qNode.getVariable().name);
									jv.getRewritings().iterator().next().addEquateInQueryHeadForThisRewriting(qNode.getVariable().name, jv.getRewritings().iterator().next().getSourceHeads().iterator().next().getSourceHeadVars().get(jv.getHeadPosition()), qNode.getVariable().name);

									SelfJoinVariableRecord rec = new SelfJoinVariableRecord(VariableType.Distinguished,qNode.getVariable().name);
//																		System.out.println(""+jv.getSourceVarName()+jv.getSourceName()+","+qNode.getVariable().name);
									selfjoinmap4sources.addSourceVariablesInOrdertoEquateQueryOnes(""+jv.getSourceVarName()+jv.getSourceName(),rec,jv.getRewritings().iterator().next());
								}
							}//if the query node is existential but the source one distinguished, don't care about it, reformulation phase will equate the distinguished source nodes to cover for existential query joins
							//there's a small exception however: predicates of the query that have selfjoins
							else //query node is existential but the source one distinguished
							{
								//I'm cloning the infobox to avoid concurrent access
								List<JoinInView> cloneListViews  = new ArrayList<JoinInView>(sourceNode.getInfobox().getJoinInViews()); 

//								List<JoinInView> listOfThisSnodesRewritings = sourceNode.getInfobox().getJoinInViews();
								for(JoinInView jv:  cloneListViews)
								{
									SelfJoinVariableRecord rec = new SelfJoinVariableRecord(VariableType.Distinguished,jv.getRewritings().iterator().next().getSourceHeads().iterator().next().getSourceHeadVars().get(jv.getHeadPosition()));
									if(!selfjoinmap.addTemp(""+qNode.getVariable().name+jv.getSourceName(),rec,jv.getRewritings().iterator().next())) //if we cannot cover the underlying query selfjoin. Note: we might have to explicitly add an equate to cover the self join; SelfJoinVariableMap#add here will do this and return true if possible
										if(s_pj.emptyJoinInView(jv))//drop sourcebox (i.e, joininview) from source_pj's infoboxes, and if we dropped all return true
										{
											l_it.remove();//if infoboxes of s_pj became empty remove s_pj from l
											breakNodeIteration = true;//no need to go to go other nodes of the same PJ
											break;//we're out of sourceboxes
										}
								}
								if(breakNodeIteration)
									break;
							}
						}
					}

					addPJQtoCPJCoverSets(l,pjq); //link the query PJ to these (alternatives) sourcePJs
					List<String> headvars = new ArrayList<String>();
					for(Variable v:this.query.getHeadVariables())
						headvars.add(v.name);
					addAllSourcePJsAsCPJs(cset,l,this.query.getName(),headvars);
				}
			}
		}

		if(cset.isEmpty())
			throw new NonAnswerableQueryException();

		cset.setSerialNo(pjq.getSerialNumber());

		if(this.getQuery().numberOfPredicates()==1)
		{
			assert(this.getQuery().sumOfPredicatesSerials() == 1);
			assert(pjq.getSerialNumber()==1);
		}

		return cset;
	}

	static boolean assertRepeatedIds(List<SourcePredicateJoin> l) {
		for(SourcePredicateJoin spj: l)
			assert(spj.getPredicate().getRepeatedId() != -1);

		return true;
	}

	private void addPJQtoCPJCoverSets(List<SourcePredicateJoin> l,
			QueryPredicateJoin pjq) {

		for(SourcePredicateJoin spj: l)
			spj.setQueryPJ(pjq);
	}


	private void addAllSourcePJsAsCPJs(CPJCoverSet cset,
			List<SourcePredicateJoin> l, String name, List<String> headvars) {
		for(SourcePredicateJoin spj: l)
		{
			CompositePredicateJoin cpj = new CompositePredicateJoin();
			assert(spj.getPredicate().getRepeatedId() != -1);

			cpj.add(spj);
			for(AtomicRewriting ar: spj.getRewritings())
			{
				CompRewriting cr = new CompRewriting(name,new ArrayList<String>(headvars));
				cr.add(ar);
				//TODO next for is recent addition -- check if the performance dropped
				for(Pair<String, Pair<String,String>> p : ar.getEquates())
				{
//					System.out.println("--->Taking equate from rewriting \n\t"+ar+" with hashcode "+ar.hashCode());
//					System.out.println("           "+p.getB().getA()+"-"+p.getB().getB()+"->"+p.getA());
//					System.out.println("   Adding this in \n\t"+cr+" with hashcode "+cr.hashCode());
					
					cr.addEquate(p.getB().getA(), p.getB().getB(), p.getA());
										
										
				}
				for(Pair<String, Pair<String,String>> p : spj.getEquates())
				{
					System.out.println("Nothing------------------------------------------------------------------");	
					cr.addEquate(p.getB().getA(), p.getB().getB(), p.getA());
				}
				cpj.addRewritings(cr);
			}
			cset.add(cpj);
		}
	}


	private List<SourcePredicateJoin> cloneIfReturnedInPast(SourcePredicateJoin key, List<SourcePredicateJoin> l) {

		List<SourcePredicateJoin> retlist = new ArrayList<SourcePredicateJoin>();
		// If key has been asked for in the past clone all list and return it
		if(pastReturned.containsKey(key))
		{
			List<SourcePredicateJoin> retl = pastReturned.get(key);
			for(SourcePredicateJoin sp: retl)
			{
				try{
					retlist.add(sp.clone());}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			pastReturned.put(key, retlist);
			return retl;
		}
		else
		{
			//dummyclone list and keep it for future cloning //TODO an optimization is to to do the cloning on DEMAND (here we're beeing proactive)
			for(SourcePredicateJoin sp: l)
			{
				try{
					retlist.add(sp.clone());}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			pastReturned.put(key,retlist);
			return l;	
		}
	}


}


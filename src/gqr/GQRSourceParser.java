package gqr;

import edu.isi.mediator.domain.DomainModel;
import gr.forth.ics.graph.Edge;
import gr.forth.ics.graph.Graph;
import gr.forth.ics.graph.Node;
import gr.forth.ics.graph.PrimaryGraph;
import gr.forth.ics.graph.io.Format;
import gr.forth.ics.graph.io.GraphIO;
import gr.forth.ics.graph.layout.BasicLocator;
import gr.forth.ics.graph.layout.GPoint;
import gr.forth.ics.graph.layout.Locator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author george konstantinidis (george@konstantinidis.org)
 * Object for parsing and preprocessing the sources. Extends the more general GQRQueryParser.
 */
class GQRSourceParser extends GQRQueryParser {

	/**
	 * Calls its super class to parse the view file up to a certain numberofviews 
	 * @param file full name of an existing, readable file.
	 * @param numberofviews number of line of the file up to which it will parse.
	 */
	GQRSourceParser(File file,int numberofviews) {
		super(file, numberofviews);
	}
	
	GQRSourceParser(DomainModel domainmodel,int numberofviews) {
		super(domainmodel, numberofviews);
	}
	
	//map to index all source PJs. The keys of this map are all potential query PJs. The values are all the existing source PJs that can cover the key.
	//The sources PJs in the values are keys of the next map. Any pj A which maps to a key pj B of the next map
	//will be a kept in this map, and its value will be a table containing B
	Map <SourcePredicateJoin,List<SourcePredicateJoin>> indexSourcePJs = new HashMap<SourcePredicateJoin,List<SourcePredicateJoin>>();

	//map to ``globally'' hold all PJs, if repeated predicates exist in the same source, these are contained in the list for the spj 
	Map <SourcePredicateJoin,List<SourcePredicateJoin>> sourcePJs = new HashMap<SourcePredicateJoin,List<SourcePredicateJoin>>();
	
	/**
	 * Iterates over the views file, line by line, parsing the queries that represent 
	 * the views and creating predicate joins for them.
	 * It constructs sourcePJs: a map whose each entry has key a PJ and value a table of copies of the key as many times as the PJ is repeated in a view.  
	 */
	void parseSources() {
		DatalogQuery source = parseNextQuery();
		
		while(source != null)
		{//System.out.println("View: "+ source);
			//locally holds for every source S how many occurrences of the same pj this source contains
			//at the same time the integer value of this map is the index of the place in the list "repeeated_predicates" below in which
			//we'll store a copy of S
			createPJ(source);
			source = parseNextQuery();
			System.out.println("view: "+source);
		}
		
		DatalogQuery specialSource = new DatalogQuery();
		specialSource.setName("SpecialSourceLT");
		Variable headVariableLeft = new Variable("x0");
		headVariableLeft.setPositionInHead(0);
		specialSource.addHeadVariable(headVariableLeft);
		Variable headVariableRight = new Variable("x1");
		headVariableRight.setPositionInHead(1);
		specialSource.addHeadVariable(headVariableRight);
		
		List<PredicateArgument> tempPredElems = new ArrayList<PredicateArgument>(2);
		tempPredElems.add(headVariableLeft);
		tempPredElems.add(headVariableRight);
		Predicate specialPredicate = new Predicate("PLT");
		specialPredicate.addAllElements(tempPredElems);
		tempPredElems.clear();
		//specialPredicate.setComparisonPredicate(true);
		
		specialSource.addPredicate(specialPredicate);
		createPJ(specialSource);
		
		
	}
	
	/* Creates a PJ for the Predicates in the view */
	void createPJ (DatalogQuery source)
	{
		System.out.println("View: "+ source);
		//locally holds for every source S how many occurrences of the same pj this source contains
		//at the same time the integer value of this map is the index of the place in the list "repeeated_predicates" below in which
		//we'll store a copy of S
		Map <SourcePredicateJoin,Integer> occurences_of_the_same_pj = new HashMap<SourcePredicateJoin, Integer>();
		
		for(PredicateJoin pj:source.constructAndReturnSourcePJs()) //constructs all the pjs with their infoboxes only updated for this source
		{
			SourcePredicateJoin spj = new SourcePredicateJoin(pj);
			
			//get the list of the already existed and constructed
			//PJs that can hold information also for this PJ
			List<SourcePredicateJoin> repeated_predicates = sourcePJs.get(spj);
			
			if(repeated_predicates == null) //first time we see this PJ among all views
			{	
				occurences_of_the_same_pj.put(spj, 1); //it appears only once in this source
				spj.getPredicate().setRepeatedId(0);
				spj.renameRewritingVarsAppending(0);
				repeated_predicates = new ArrayList<SourcePredicateJoin>();
				repeated_predicates.add(spj);//new PJ in town
				sourcePJs.put(spj, repeated_predicates); //now a subsequent source can use this pj
			}
			else
			{
				Integer times = occurences_of_the_same_pj.get(spj); //I've seen this pj again in THIS source this many times
				if(times == null)
					times = 0;
				try{
					SourcePredicateJoin spj_to_capture_thisspj = repeated_predicates.remove((int)times);//
					//since I've seen this PJ this many times in this source, all previous same PJs of this source
					//have been represented "globally". therefore I'm choosing another PJ (same as this, and apparently 
					//constructed for another source some time in the past) which will be in index `times'
					spj.getPredicate().setRepeatedId(spj_to_capture_thisspj.getPredicate().getRepeatedId());
					spj.renameRewritingVarsAppending(spj_to_capture_thisspj.getPredicate().getRepeatedId());
					repeated_predicates.add(times, spj_to_capture_thisspj.mergeWithSameSpj(spj));
					
					//I merge the information for those two PJs, put back in `times', and increase ``times'' 
					occurences_of_the_same_pj.put(spj,times+1);
				}catch (IndexOutOfBoundsException e) { //not enough source PJs already constructed to represent this
					//this can happen if I have seen this PJ more times in this source than there are PJs (same as this)
					//stored globally
					spj.getPredicate().setRepeatedId(times);
					spj.renameRewritingVarsAppending(times);
					repeated_predicates.add(spj);//new PJ in town		
					//and increase ``times'' 
					occurences_of_the_same_pj.put(spj,times+1);//this shouldn't be needed
				}
			}
		}
		
	}
	
	
	
//	/**
//	 * Iterates over the views file, line by line, parsing the queries that represent 
//	 * the views and creating predicate joins for them.
//	 * It constructs sourcePJs: a map whose each entry has key a PJ and value a table of copies of the key as many times as the PJ is repeated in a view.  
//	 */
//	void parseBIRNSources() {
//		DatalogQuery source = parseNextBIRNQuery();
//		
//		while(source != null)
//		{//System.out.println("View: "+ source);
//			//locally holds for every source S how many occurrences of the same pj this source contains
//			//at the same time the integer value of this map is the index of the place in the list "repeeated_predicates" below in which
//			//we'll store a copy of S
//			Map <SourcePredicateJoin,Integer> occurences_of_the_same_pj = new HashMap<SourcePredicateJoin, Integer>();
//			
//			for(PredicateJoin pj:source.constructAndReturnSourcePJs()) //constructs all the pjs with their infoboxes only updated for this source
//			{
//				SourcePredicateJoin spj = new SourcePredicateJoin(pj);
//				
//				//get the list of the already existed and constructed
//				//PJs that can hold information also for this PJ
//				List<SourcePredicateJoin> repeated_predicates = sourcePJs.get(spj);
//				
//				if(repeated_predicates == null) //first time we see this PJ among all views
//				{	
//					occurences_of_the_same_pj.put(spj, 1); //it appears only once in this source
//					spj.getPredicate().setRepeatedId(0);
//					spj.renameRewritingVarsAppending(0);
//					repeated_predicates = new ArrayList<SourcePredicateJoin>();
//					repeated_predicates.add(spj);//new PJ in town
//					sourcePJs.put(spj, repeated_predicates); //now a subsequent source can use this pj
//				}
//				else
//				{
//					Integer times = occurences_of_the_same_pj.get(spj); //I've seen this pj again in THIS source this many times
//					if(times == null)
//						times = 0;
//					try{
//						SourcePredicateJoin spj_to_capture_thisspj = repeated_predicates.remove((int)times);//
//						//since I've seen this PJ this many times in this source, all previous same PJs of this source
//						//have been represented "globally". therefore I'm choosing another PJ (same as this, and apparently 
//						//constructed for another source some time in the past) which will be in index `times'
//						spj.getPredicate().setRepeatedId(spj_to_capture_thisspj.getPredicate().getRepeatedId());
//						spj.renameRewritingVarsAppending(spj_to_capture_thisspj.getPredicate().getRepeatedId());
//						repeated_predicates.add(times, spj_to_capture_thisspj.mergeWithSameSpj(spj));
//						
//						//I merge the information for those two PJs, put back in `times', and increase ``times'' 
//						occurences_of_the_same_pj.put(spj,times+1);
//					}catch (IndexOutOfBoundsException e) { //not enough source PJs already constructed to represent this
//						//this can happen if I have seen this PJ more times in this source than there are PJs (same as this)
//						//stored globally
//						spj.getPredicate().setRepeatedId(times);
//						spj.renameRewritingVarsAppending(times);
//						repeated_predicates.add(spj);//new PJ in town		
//						//and increase ``times'' 
//						occurences_of_the_same_pj.put(spj,times+1);//this shouldn't be needed
//					}
//				}
//			}
//			source = parseNextQuery();
////			System.out.println("view: "+source);
//		}
//	}
	
	Map<SourcePredicateJoin, List<SourcePredicateJoin>> getSourcePJs() {
		return sourcePJs;
	}

	/**
	 * construct an index having as key any potential query pj, and as value the source pjs this key could map onto 
	 */
	void indexSourcePJs()
	{
		if(sourcePJs.isEmpty())
			throw new RuntimeException("SourcePJs not initiliazed. Call parseSources() first");
		
		for(SourcePredicateJoin spj: sourcePJs.keySet())
		{
			for(SourcePredicateJoin key: spj.potentialMappings())
				addToIndex(key,spj);//appends the value table for key with spj
		}
		
	}
	

	
	private void addToIndex(SourcePredicateJoin key, SourcePredicateJoin spj) {
		List<SourcePredicateJoin> l = indexSourcePJs.get(key);
		if(l == null)
			l=new ArrayList<SourcePredicateJoin>();
		l.add(spj);
		indexSourcePJs.put(key, l);
	}

	Map<SourcePredicateJoin, List<SourcePredicateJoin>> getIndexSourcePJs() {
		return indexSourcePJs;
	}

	void setIndexSourcePJs(
			Map<SourcePredicateJoin, List<SourcePredicateJoin>> indexSourcePJs) {
		this.indexSourcePJs = indexSourcePJs;
	}
	
	void draw()
	{
		for(SourcePredicateJoin temp: this.sourcePJs.keySet())
		{
			for(SourcePredicateJoin spj: this.sourcePJs.get(temp))
			{
				Graph g = new PrimaryGraph();
				Locator locator = new BasicLocator();
				Node pnode = g.newNode(spj.getPredicate());
				System.out.println("Creating graph for "+spj.getPredicate());
				System.out.println("  Rewritings :"+spj.getRewritings());
				int x=0,y=0;
				locator.setLocation(pnode, new GPoint(x, y));
				
				for(int j=1; j<=spj.getGqrNodes().keySet().size(); j++)
				{
					Node vnode = g.newNode(spj.getGqrNodes().get(j));
					x+=100;
					y+=100;
			        locator.setLocation(vnode, new GPoint(x-100, y+100));
					Edge e = g.newEdge(pnode, vnode);
			        e.setValue(j);    
				}
				
				try {		        
					GraphIO.write(Format.GML, g, locator, new File(System.getProperty("user.dir")+"/NewPJ_"+spj.getPredicate()+(new Random()).nextInt()+".gml"));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			
			}
		}
	}
}

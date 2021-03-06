package gqr;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import datalog.DatalogParser;
import datalog.DatalogScanner;



/**
 * Class DatalogQuery is used to represent the query as well as one or more
 * views that are provided as user input.
 * 
 * A DatalogQuery object consists of the head and the body. The head contains
 * head variables and the body consists of predicates 
 * 
 * @author Kevin, George Konstantinidis (george@konstantinidis.org)
 */

public class DatalogQuery {

	/** name of the Datalog query, i.e. name of head predicate */
	private String name;

	/** head variables */
	private List<Variable> headVariables;
	
	/** head variables */
	private List<Variable> existentialVariables;

	/** predicates of the body */
	private List<Predicate> predicates;
	
	private boolean hasComparisonPredicate = false;

	private Set<PredicateJoin> queryPJs = null;

	private int sumSerNo = 0;
	
	HashSet<String> alreadyfreshed = new HashSet<String>(); //needed when getting the expansion of a rewriting

//	/** interpreted predicates of the body */
//	private List<InterpretedPredicate> interpretedPredicates;

//	/**
//	 * DatalogQuery constructor
//	 */
	public DatalogQuery() {
		headVariables = new ArrayList<Variable>();
		existentialVariables = new ArrayList<Variable>();
		predicates = new ArrayList<Predicate>();
//		interpretedPredicates = new ArrayList<InterpretedPredicate>();
	}

//	/**
//	 * DatalogQuery constructor
//	 * 
//	 * @param name of the Datalog query
//	 *            
//	 */
//	DatalogQuery(String name) {
//		this.name = name;
//		headVariables = new HashSet<Variable>();
//		existentialVariables = new HashSet<Variable>();
//		predicates = new ArrayList<Predicate>();
////		interpretedPredicates = new ArrayList<InterpretedPredicate>();
//	}

	/**
	 * Set query name
	 * 
	 * @param name of query
	 *           
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns Datalog query name.
	 * 
	 * @return name of Datalog query
	 */
	String getName() {
		return this.name;
	}
	
	public void setHasComparisonPred()
	{
		this.hasComparisonPredicate = true;
	}
	
	String getHeadString()
	{
		return name + "(" + printCollection(headVariables) + ")";
	}

	/**
	 * Adds a head variable to Datalog query.
	 * 
	 * @param var head variable
	 *            
	 */
	public void addHeadVariable(Variable var) {
		headVariables.add(var);
	}

	/**
	 * Returns a head variables of Datalog query
	 * 
	 * @return list of head variables
	 */
	public List<Variable> getHeadVariables() {
		return headVariables;
	}

	public void addExistentialVariable(Variable var) {
		existentialVariables.add(var);
	}
	
	/**
	 * The method will return list of variables that are existential in the
	 * Datalog query, i.e. that are not in the head.
	 * 
	 * @return set of existential variables
	 */
	List<Variable> getExistentialVariables() {
		return existentialVariables;
	}
	
	/**
	 * Finds a predicate with the given argument (name)
	 * 
	 * @param name of the predicate that is to be found
	 *            
	 * @return Datalog query predicate, null if no matching predicate will be
	 *         found
	 */
	Predicate getPredicate(String name) {

		for (Predicate pred : predicates) {
			if (pred.name.equals(name)) {
				return pred;
			}
		}
		return null;
	}

	/**
	 * Returns predicates of Datalog query
	 * 
	 * @return list of predicates
	 */
	List<Predicate> getPredicates() {
		return predicates;
	}

//	/**
//	 * Returns interpreted predicates of Datalog query
//	 * 
//	 * @return list of interpreted predicates
//	 */
//	List<InterpretedPredicate> getInterpretedPredicates() {
//		return interpretedPredicates;
//	}

	/**
	 * Sets the reference of predicates to the list of predicates (preds) given
	 * as argument.
	 * 
	 * @param preds list of predicates
	 *            
	 */
	void setPredicates(List<Predicate> preds) {
		this.predicates = preds;
	}

	/**
	 * Returns true if Datalog query contains the head variable that is provided
	 * as argument. If argument elem is not of type Variable, false will be
	 * returned.
	 * 
	 * @param var head variable that is tested to be contained
	 *            
	 * @return true, if variable is in the head, false otherwise
	 */
	boolean containsHeadVariable(PredicateArgument elem) {
		if (elem instanceof Variable) {
			return headVariables.contains(elem);
		} else
			return false;
	}

	/**
	 * Adds a predicate to the Datalog query.
	 * 
	 * @param predicate Predicate to be added
	 *           
	 */
	public void addPredicate(Predicate predicate) {
		predicates.add(predicate);
	}

//	/**
//	 * Adds an interpreted predicate to the Datalog query.
//	 * 
//	 * @param predicate
//	 *            interpreted predicate to be added
//	 */
//	void addInterpretedPredicate(InterpretedPredicate predicate) {
//		interpretedPredicates.add(predicate);
//	}

//	/**
//	 * The method will return list of variables that are existential in the
//	 * Datalog query, i.e. that are not in the head.
//	 * 
//	 * @return list of existential variables
//	 */
//	List<Variable> getExistentialVariables() {
//		List<Variable> vars = new ArrayList<Variable>();
//		List<Variable> existentVars = new ArrayList<Variable>();
//
//		// collect all variables
//		for (Predicate pred : predicates) {
//			vars.addAll(pred.getVariables());
//		}
//		
//		for (Variable var : vars) {
//			// variable is not contained in the head and not already
//			// in the list of existential variables	
//			if (!this.getHeadVariables().contains(var) && (!existentVars.contains(var))) {
//				existentVars.add(var);
//
//			}
//		}
//		
//		return existentVars;
//	}

	/**
	 * The method will return the the number of predicates of the Datalog query
	 * (without interpreted predicates)
	 * 
	 * @return number of predicates
	 */
	int numberOfPredicates() {
		return predicates.size();
	}
	
	int sumOfPredicatesSerials() {
		if(sumSerNo == 0)
		{
			for(int i =1; i<=predicates.size(); i++)
			{
				sumSerNo +=i;
			}
		}		
		return sumSerNo ;
	}

	/**
	 * Overwrites object method. Returns a String representation of the Datalog
	 * query.
	 */
	public String toString() {
		String preds = printBody(predicates);
		String interpretedPreds = "";
//		if (printCollection(interpretedPredicates).length() > 0) {
//			interpretedPreds = "," + printCollection(interpretedPredicates);
//		}
		String val = name + "(" + printCollection(headVariables) + ") :- "
				+ preds + interpretedPreds;
		return val;
	}
	
	/**
	 * String representation of the query body.
	 * 
	 * @param collection of predicates
	 *            
	 * @return String of the form "predicate1(variable1,variable2,...),...)"
	 */
	String printBody(List<Predicate> collect) {
		String val = "";
		for (Predicate pred : collect) {
			val = val + "," + pred.toString();
			val +="("+printCollection(pred.getVariables())+")";
			
		}
		val = val.replaceFirst(",", "");
		return val;
	}

	/**
	 * String representation of either a list of head variables or a list of
	 * predicates.
	 * 
	 * @param coll
	 *            list of head variables of predicates
	 * @return String of the form "(list1Elem1,...)"
	 */
	static String printCollection(Collection collect) {
		String val = "";
		for (Object obj : collect) {
			val = val + "," + obj.toString();
		}
		val = val.replaceFirst(",", "");
		return val;
	}
	/**
	 * Computes all query PJs
	 */
	void computeQueryPJs(){
		
		Set<PredicateJoin> res = new HashSet<PredicateJoin>();
		int k = 1;
		for(Predicate p:this.getPredicates())//for all predicates	
		{
			PredicateJoin qpj = new QueryPredicateJoin(p);//construct a cpj (in fact a pj)
			qpj.setSerialNumber(k++);
	        int j=1;
			for(PredicateArgument el:p.getArguments())//tale all the variables of the query PJ
			{
				assert(el instanceof Variable); //at this version all of predicate's "elements" (i.e., arguments) are variables
				Infobox queryVarBox = new Infobox();
				JoinInView joiv = new JoinInView(this.name);
				
				Variable v = (Variable)el;
				
				for(Predicate otherpred:this.getPredicates()) //get the predicates once again
				{
					if(!p.equals(otherpred)) //for every other predicate
					{
						//if the variable (is joined with) belongs also to otherpred
						int i = 0;
						for(PredicateArgument ped: otherpred.getArguments())//find the place v exists in otherpred
						{
							i++;
							assert(ped instanceof Variable);
							if(el.equals(ped)) //add this join description to the variable's infobox
								joiv.addJoinDescription(new JoinDescription(otherpred,i));
						}
					}
				}// here we have a  complete infobox for v
				
				queryVarBox.addJoinInView(joiv);
				gqr.GQRNode nv = new gqr.GQRNode(v,queryVarBox);
				qpj.addNode(nv,j++);
				
			}
			res.add(qpj);
		}	
		queryPJs = res;
	}
	/**
	 * 
	 * @return all the PJs for this source query
	 */
	Set <PredicateJoin> constructAndReturnSourcePJs(){
		
		//I am using a hashset to return the pjs so I cannot construct  SourcePredicateJoins here because different occurrences might hash on the same one
		//and I want all of them. However what I can TODO is not use a hashset, just a list or sth  
		Set<PredicateJoin> res = new HashSet<PredicateJoin>();
		for(Predicate p:this.getPredicates())//for all predicates	
		{
			PredicateJoin spj = new PredicateJoin(p);//construct a cpj (in fact a pj)
	        int j=1;
	        
	        AtomicRewriting rw = new AtomicRewriting();
	        List<String> head_variables = new ArrayList<String>();
	        
			for(PredicateArgument el:p.getArguments())//take all the variables of the query PJ
			{
				
				
				Infobox queryNodeBox = new Infobox();
				JoinInView joiv = new JoinInView(this.name);
				joiv.setSourceVarName(el.name);
				
				Variable v = (Variable)el;
				
				if(v.getPositionInHead() != -1){
					head_variables.add(""+v.getPositionInHead());
				}
					
				
				for(Predicate otherpred:this.getPredicates()) //get the predicates once again
				{
					if(!p.equals(otherpred)) //for every other predicate
					{
						//if the variable (is joined with) belongs also to otherpred
						int i = 0;
						for(PredicateArgument ped: otherpred.getArguments())//find the place v exists in otherpred
						{
							i++;
							if(el.equals(ped)) //add this join description to the variable's infobox
								joiv.addJoinDescription(new JoinDescription(otherpred,i));
						}
					}
				}// here we have a  complete infobox for v
				
				joiv.addRewriting(rw);
//				System.out.println("----> In getSourcePJs(): joiv for var :"+v);
//				System.out.println("----------> In getSourcePJs(): joiv position in head"+v.getPositionInHead());				
				joiv.addSourceHeadPosition(v.getPositionInHead());
				rw.addRefToJoiv(joiv);
				queryNodeBox.addJoinInView(joiv);
					
				GQRNode nv = new GQRNode(v,queryNodeBox);
				spj.addNode(nv,j++);

			}
			
			head_variables = insertDontCaresAndPredName(head_variables,p.name+spj.variablePatternStringSequence());			

//			System.out.println("final rewriting "+head_variables);
			SourceHead sh = new SourceHead(this.name);
			sh.setSourceHeadVars(head_variables);
			try {
				sh.setQuery(this.clone());
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
			rw.addSourceHead(sh);
			spj.addRewriting(rw);
			
			boolean shouldalwaysaddit = res.add(spj);
			assert(shouldalwaysaddit);
		}	
		return res;
	}

	private List<String> insertDontCaresAndPredName(List<String> head_variables, String predicate_name) {
		
		List<String> newHeadVar = new ArrayList<String>();
		if(head_variables.isEmpty())
		{
			for(int i =0; i<this.headVariables.size(); i++)
			{
				//TODO string delimeters is not a good idea -- use objects instead of Strings and fields instead of delimeters
				newHeadVar.add(i, "DC"+i+"AT"+this.name+"DOT"+predicate_name+"CiD0DiC");
			}
		}
		else{

			//TODO I might be able to give a quicker solution without sorting
					Collections.sort(head_variables, new Comparator<String>() {
			
						public int compare(String o1, String o2) {
							if(Integer.parseInt(o1) < Integer.parseInt(o2))
								return -1;
							else if(Integer.parseInt(o1) > Integer.parseInt(o2))
								return 1;
							else
								return 0;
						}
					});
			
			assertSorted(head_variables);
			
			int inner_index = 0;
			
//			while((inner_index<head_variables.size()) && head_variables.get(inner_index).equals("-1"))
//			{
//				inner_index++;
//			}

			for(int i =0; i<this.headVariables.size(); i++)
			{

//				System.out.println(head_variables);
//				System.out.println(inner_index);
//				System.out.println(i);

//				if(inner_index == head_variables.size())
//				{
//					newHeadVar.add(i, "DC"+i+"AT"+this.name+"CiD0DiC");
//
//				}else
//				{
//					while((inner_index<head_variables.size()) && head_variables.get(inner_index).equals("-1"))
//					{
//						inner_index++;
//					}
//
//					if(inner_index == head_variables.size())
//						continue;

					//				System.out.println("I'm checking whether "+i+" is in partialHead.\n\t Actually I'm only checking index "+inner_index);
//					if((inner_index == head_variables.size()) || Integer.parseInt(head_variables.get(inner_index)) != i  )
//					{
//						//					System.out.println("\t N--> It is NOT so I put a don't care");
//						newHeadVar.add(i, "DC"+i+"AT"+this.name+"DOT"+predicate_name+"CiD0DiC");
//					}
//					else{
//						assert(head_variables.get(inner_index).equals(""+i));
						//					System.out.println("\t Y--> It IS so I put "+inner_index +" \n\t ..and I increase index by 1");

						newHeadVar.add("Z"+i+"AT"+this.name+"DOT"+predicate_name+"CiD0DiC");
						inner_index++;
//					}
//				}
			}
		}
		return newHeadVar;
	}

	private void assertSorted(List<String> partialHeadVarList) {
		
		for(int i=0; i<partialHeadVarList.size()-1; i++)
		{
//			System.out.println(partialHeadVarList);
//			if(Integer.parseInt(partialHeadVarList.get(i)) == -1)
//				continue;
//			int j=i+1;
//			while(j<partialHeadVarList.size() && Integer.parseInt(partialHeadVarList.get(j))==-1)
//				j++;
//			if(j<partialHeadVarList.size())//if((Integer.parseInt(partialHeadVarList.get(i)) != -1) && (Integer.parseInt(partialHeadVarList.get(i+1))!=-1))
	
			//TODO this assertion initially was strictly '<'  but it broke so I put '<='. I NEED TO VERIFY THIS IS CORRECT. 
			assert(Integer.parseInt(partialHeadVarList.get(i)) <= Integer.parseInt(partialHeadVarList.get(i+1)));
		}
	}

	void countRepeatedPredicates() {
		//holds for every source how many occurences of the same pj this query contains
		//We're using SourcePredicateJoin objects only to take advantage of their hash/equals method
		Map <SourcePredicateJoin,Integer> occurences_of_the_same_pj = new HashMap<SourcePredicateJoin, Integer>();

		for(PredicateJoin pj:this.getQPJs()) //get all the pjs with their infoboxes only updated for this source
		{
			SourcePredicateJoin spj = new SourcePredicateJoin(pj);

			Integer repeatedTimes = occurences_of_the_same_pj.get(spj);//get the list of the already existed and constructed
			//PJs that can hold information also for this PJ
			if(repeatedTimes == null) //first time we see this PJ in the query
			{
				occurences_of_the_same_pj.put(spj, 1);
				spj.getPredicate().setRepeatedId(0);
			}
			else
			{
				spj.getPredicate().setRepeatedId(repeatedTimes);
				occurences_of_the_same_pj.put(spj,++repeatedTimes);//new PJ in town		
			}
		}

	}

	
	Set <PredicateJoin> getQPJs() {
		if(queryPJs == null)
			throw new RuntimeException("query pjs have not been yet computed");
		return queryPJs;
	}
	
	public static DatalogQuery parseQueryIntoDatalog(String s)
	{
		DatalogScanner scanner = new DatalogScanner(new StringReader(s));
		DatalogParser parser = new DatalogParser(scanner);
		
		try{
//			System.out.println(s);
			return parser.query();
			
		} catch (RecognitionException re) {
			throw new RuntimeException(re);
		} catch (TokenStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected DatalogQuery clone() throws CloneNotSupportedException {
		return DatalogQuery.parseQueryIntoDatalog(this.toString());
	}

	void substituteVarWithFresh(String shv, boolean ex,int exfreshcount) {
	
		if(!ex)
		{
//			System.out.println("Head variables in the view definition"+headVariables);
			for(Variable var:headVariables)
			{
				if(var.toString().equals(shv))
				{
					
					var.name = PoolOfNames.getName(shv);
				}
			}
		}
		
		for(Predicate pred: this.predicates)
		{
			for(Variable v : pred.getVariables())
			{
				if(v.toString().equals(shv))
				{
					v.name = ex?"F"+exfreshcount+""+PoolOfNames.getName(shv):PoolOfNames.getName(shv);
				}
			}
		}
	}

	void substituteVarWithNew(String varInRule, String shv) {
		for(Variable var:headVariables)
		{
			if(var.toString().equals(varInRule))
			{
				var.name = shv;
			}
		}
		
		for(Predicate pred: this.predicates)
		{
			for(Variable v : pred.getVariables())
			{
				if(v.toString().equals(varInRule))
					v.name = shv;
			}
		}
	}
}

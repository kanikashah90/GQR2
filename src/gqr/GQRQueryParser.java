package gqr;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import datalog.DatalogParser;
import datalog.DatalogScanner;
import edu.isi.mediator.domain.DomainModel;
import edu.isi.mediator.gav.query.Query;
import edu.isi.mediator.rule.LAVRule;

/**
 * 
 * @author george konstantinidis (george@konstantinidis.org)
 * Parser for safe conjunctive datalog-like rules(user queries or views).
 */
class GQRQueryParser {

	// PRIVATE //
	private File fFile;
	private int readupto = 0;
	private int limit = 0;
	private Scanner sc = null;
	private ArrayList<Query> domainQueries;

	/**
	 * @param file full name of an existing, readable file.
	 * @param limit number of line of the file up to which it will parse.
	 */
	GQRQueryParser(File file,int limit){
		this.limit = limit;
		fFile = file;
		try {
			sc = new Scanner(fFile);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	}

	public GQRQueryParser(DomainModel domainmodel, int numberofviews) {
		ArrayList<LAVRule> rulesList = domainmodel.getLAVRules();
		this.limit = numberofviews;
	}

	public GQRQueryParser(ArrayList<Query> domainQuery) {
		this.domainQueries = domainQuery;
	}

	/**
	 * Parses one line of the file given to the parser and creates one query. Each time called gets next line.
	 * In EOF is reached (in last call) it returns null
	 * @return a DatalogQuery
	 */
	DatalogQuery parseNextQuery()
	{
		DatalogQuery query;
		if(sc.hasNextLine() && readupto<=limit)
		{
			readupto++;
			//			System.out.println(readupto+"here");

			String next= sc.nextLine();
			//			System.out.println(next);
			DatalogScanner scanner = new DatalogScanner(new StringReader(next));
			DatalogParser parser = new DatalogParser(scanner);
			try{
				query = parser.query();

			} catch (RecognitionException re) {
				throw new RuntimeException(re);
			} catch (TokenStreamException e) {
				throw new RuntimeException(e);
			}
		}else
			return null;
		return query;
	}


//	/**
//	 * Parses one line of the BIRN file given to the parser and creates one query. Each time called gets next line.
//	 * In EOF is reached (in last call) it returns null
//	 * @return a DatalogQuery
//	 */
//	DatalogQuery parseNextBIRNQuery()
//	{
//		DatalogQuery query = new DatalogQuery();
////		if(sc.hasNextLine() && readupto<=limit)
////		{
////			readupto++;
////			//			System.out.println(readupto+"here");
////
////			String next= sc.nextLine();
////			//			System.out.println(next);
////			DatalogScanner scanner = new DatalogScanner(new StringReader(next));
////			DatalogParser parser = new DatalogParser(scanner);
////			try{
////				query = parser.query();
////
////			} catch (RecognitionException re) {
////				throw new RuntimeException(re);
////			} catch (TokenStreamException e) {
////				throw new RuntimeException(e);
////			}
////		}else
////			return null;
////		return query;
//
//
//		if(readupto <=limit && readupto < domainQueries.size())
//		{
//			//set head variables
//			ArrayList<String> vars_ant = new ArrayList<String>();	
//			vars_ant.addAll(domainQueries.get(readupto).getAllAntecedentVars());
//			for(int j=0; j<vars_ant.size();j++){
//				Variable x = new Variable(vars_ant.get(j));	
//				query.addHeadVariable(x);
//			}
//
//			//set existential variables
//			ArrayList<String> vars_con = new ArrayList<String>();	
//			vars_con.addAll(domainQueries.get(readupto).getAllConsequentVars());
//			for(int k=0; k<vars_con.size();k++){
//				Variable y = new Variable(vars_con.get(k));	
//				query.addExistentialVariable(y);
//			}
//
//
//			ArrayList<edu.isi.mediator.rule.Predicate> pred_ant = new ArrayList<edu.isi.mediator.rule.Predicate>();
//			pred_ant.addAll(domainQueries.get(readupto).getAntecedent());
//
//			query.setName(pred_ant.get(0).getName());
//
//
//			ArrayList<edu.isi.mediator.rule.Predicate> pred_cons = new ArrayList<edu.isi.mediator.rule.Predicate>();
//			pred_cons.addAll(domainQueries.get(readupto).getConsequent());
//
//
//
//			for(int l=0; l<pred_cons.size(); l++){
//
//				gqr.Predicate p = new gqr.Predicate(pred_cons.get(l).getName());
//
//				ArrayList<String> cons_string= new ArrayList<String>();
//				cons_string.addAll(pred_cons.get(l).getVars());
//
//				for(int m = 0; m < cons_string.size() ; m++)
//				{
//					Variable z = new Variable(cons_string.get(m));	
//					p.elements.add(z);
//				}
//
//				List<PredicateArgument> elements_copy =  new ArrayList<PredicateArgument>();
//				elements_copy.clear();
//
//				for (int re = 0 ; re < p.elements.size() ; re ++)
//				{
//					elements_copy.add(p.elements.get(re));
//
//				}
//
//				p.elements.clear();
//
//				p.addAllElements(elements_copy);
//
//
//				query.predicates.add(p);
//			}
//
//
//			/* 
//	    HashSet hs_c = new HashSet();
//		hs_c.addAll(query1.headVariables);
//		query1.headVariables.clear();
//		query1.headVariables.addAll(hs_c);
//			 */
//
//
//			query1.existentialVariables.removeAll(query1.headVariables);
//
//			//this is to remove the duplicates
//			/*
//		HashSet hs = new HashSet();
//		hs.addAll(query1.existentialVariables);
//		query1.existentialVariables.clear();
//		query1.existentialVariables.addAll(hs);
//			 */
//
//			for(int jb = 0; jb < query1.existentialVariables.size() ; jb++ ) {
//
//				query1.existentialVariables.get(jb).isExistential = true;
//				query1.existentialVariables.get(jb).positionInHead = -1;
//
//			}
//
//
//
//
//
//			for(int jb = 0; jb < query1.predicates.size() ; jb++){
//
//
//
//				for(int jc = 0; jc < query1.predicates.get(jb).variables.size() ; jc++){
//
//					for(int jd = 0; jd < query1.existentialVariables.size() ; jd++){
//
//
//
//						if(query1.existentialVariables.get(jd).name ==  query1.predicates.get(jb).variables.get(jc).name)
//						{
//							query1.predicates.get(jb).variables.get(jc).isExistential = true;
//							query1.predicates.get(jb).variables.get(jc).positionInHead = -1;
//
//						}
//
//					}
//
//				}
//
//			}
//
//
//
//			String[] arr = new String[query1.headVariables.size()];
//
//			for(int id = 0 ; id < query1.headVariables.size(); id++){
//
//				arr[id] = query1.headVariables.get(id).name; 
//
//			}
//
//
//
//			for(int jb = 0; jb < query1.predicates.size() ; jb++){
//
//
//
//				for(int jc = 0; jc < query1.predicates.get(jb).variables.size() ; jc++){
//
//					for(int jd = 0; jd < arr.length ; jd++){
//
//						//System.out.println("array value :"+ arr[jd]);
//						// System.out.println("variable string value : " + query1.predicates.get(jb).variables.get(jc).name);
//						if(query1.predicates.get(jb).variables.get(jc).name.equals(arr[jd])){
//							//   System.out.println("Andar aaya");
//
//							query1.predicates.get(jb).variables.get(jc).positionInHead = jd;
//
//						}
//
//
//					}
//
//				}
//
//			}
//
//			readupto++;
//
//		}
//
//
//		return query1;
//	}
//	else{
//		return null;
//	}
//}


//	/**   */
//	  final void processLineByLine() throws FileNotFoundException {
//	    Scanner sc = new Scanner(fFile);
//	    try {
//	      //first use a Scanner to get each line
//	    	SourceQuery sq1 = null;
//	    	boolean firstTimeInTheLoop = true;
//	      while ( sc.hasNextLine() ){
//				DatalogScanner scanner = new DatalogScanner(new StringReader(sc.nextLine()));
//				DatalogParser parser = new DatalogParser(scanner);
//				try{
//				query = parser.query();
//				
//
//				
//				if(!firstTimeInTheLoop)
//				{
//					System.out.println("First query contained in the second?-->"+sq1.isContained(Util.castQueryAsISISourceQuery(query)));
//				}
//				
//				if(firstTimeInTheLoop)
//				{
//					sq1 = Util.castQueryAsISISourceQuery(query);
//					firstTimeInTheLoop = false;
//				}
//				
//				query.getQueryPJs();
//				System.out.println(query);
//				} catch (RecognitionException re) {
//					throw new RuntimeException(re);
//				} catch (TokenStreamException e) {
//					throw new RuntimeException(e);
//				}
//	      }
//	    }
//	    finally {
//	      //ensure the underlying stream is always closed
//	      sc.close();
//	    }
//	  }


//	/**
//	 * Returns last query read by the parseQuery method.
//	 */
//	DatalogQuery getQuery() {
//		return query;
//	}

//	private void check(DatalogQuery query) {
//		
//		for(Variable v:query.getExistentialVariables())
//		{
//			if(!v.isExistential())
//				System.out.println("ERRROR---> I didn't find this free:\n\t Query: "+query+"\n\t Variable: "+v);
//		}
//		for(Variable v:query.getHeadVariables())
//		{
//			if(v.isExistential())
//				System.out.println("ERRROR---> I mistakenly found this as free:\n\t Query: "+query+"\n\t Variable: "+v);
//		}	
//	}

//	private void breakdown(DatalogQuery query) {
//		
//	}






}

import gqr.CompRewriting;
import gqr.DatalogQuery;
import gqr.FolderIterator;
import gqr.GQR;
import gqr.NonAnswerableQueryException;
import gqr.Util;

import isi.mediator.SourceQuery;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import datalog.DatalogParser;
import datalog.DatalogScanner;

import junit.framework.TestCase;


public class CheckSolutionEquivalence extends TestCase {

	int count = 0;
	
//	@Override
//	protected void tearDown() throws Exception {
//		count++;
//		super.tearDown();
//	}
	
	public void test1()
	{
	
//		File data = new File(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/star_queries/run_"+countQ+"/views_for_q_"+countQ);
								
//					File data = new File(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/chain_10000_views/run_"+countQ);
					File data = new File(System.getProperty("user.dir")+"/run_"+0+"/views_for_q_0");
//		File data = new File(System.getProperty("user.dir")+"/examples/");

		
		
//		if(countQ != run)
//			throw new RuntimeException("count different than q");
		
		for(int countQ = 0; countQ<=100; countQ++)
		{
//			File data = FolderIterator.searchFolders(new File(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/new_half_dist/"+"run_"+countQ));
//			int countQ = Integer.parseInt(data.getName().substring(12));
			
			System.out.println("\n ----Query "+countQ+"\n");
//		int viewNo=71;
		for(int viewNo=47; viewNo<140; viewNo++)
		{
			System.out.println("********view "+viewNo+" *******");
			

			GQR g = new GQR(new File(data.getAbsolutePath()+"/"+"view_"+10+".txt"),new File(data.getAbsolutePath()+"/"+"view_"+1+".txt"),viewNo);
//			GQR g = new GQR(new File(data.getAbsolutePath()+"/"+"queryHD_"+countQ+".txt"),new File(data.getAbsolutePath()+"/"+"view_"+viewNo+".txt"),viewNo);

			List<CompRewriting> res = new ArrayList<CompRewriting>();
			try {
				res = g.reformulate(g.getQuery());
				
			} catch (NonAnswerableQueryException e) {
			}

		
//			Process p;
//			Count count = new Count();
//			try {
//				p = Runtime.getRuntime().exec(System.getProperty("user.dir")+"/MCDSAT/mcdsat/mcdsat RW "+data.getAbsolutePath()+"/"+"view_"+viewNo+".txt "+data.getAbsolutePath()+"/"+"queryHD_"+countQ+".txt");
//				StreamGobbler errorGobbler = new 
//				StreamGobbler(p.getErrorStream(), "ERROR", count);            
//				StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", count);
//				errorGobbler.start();
//				outputGobbler.start();
//				p.waitFor();
//				} catch (IOException e) {
//				throw new RuntimeException(e);
//				}
//				catch (InterruptedException e) {
//					throw new RuntimeException(e);
//			}
				
				List<SourceQuery> gqr_queries = new ArrayList<SourceQuery>();

				for(CompRewriting cr: res)
				{
					gqr_queries.add(Util.castQueryAsISISourceQuery(DatalogQuery.parseQueryIntoDatalog(cr.toString())));
				}
				System.out.println("GQR -->" + gqr_queries);
//				System.out.println("MCDSAT --> "+count.mcdsat_queries.size());
////				
//			List<SourceQuery> msat_queries = new ArrayList<SourceQuery>();
//			Set<SourceQuery> covers = new HashSet<SourceQuery>();
//			for(String s: count.mcdsat_queries)
//			{
//				SourceQuery mcq = Util.castQueryAsISISourceQuery(parseQueryIntoDatalog(s));
////				System.out.println("here mcq "+mcq);
//				boolean contained= false;
//				for(SourceQuery gqrq: gqr_queries)
//				{
//					System.out.println("checking wether "+ mcq+ " is contained in "+gqrq);
//					if(mcq.isContained(gqrq))
//					{
//						System.out.println("           Yes\n");
//						contained =true;
//						covers.add(gqrq);
//						break;
//					}
//					else
//						System.out.println("           No\n");
//					
//				}
//				
////				assertTrue(contained);
//				
//				msat_queries.add(mcq);
//			}
			
//			assertTrue(covers.size() == gqr_queries.size());
			
//			System.out.println("---GQR---");
//			System.out.println(gqr_queries);
//			System.out.println("------------------------\n");
//			System.out.println("---MCDSAT---");
//			System.out.println(msat_queries);
//			System.out.println("------------------------\n");
		}
		}
	}
	
	
	
}

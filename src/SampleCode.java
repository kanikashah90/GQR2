import gqr.CompRewriting;
import gqr.GQR;
import gqr.IndexViews;
import gqr.NonAnswerableQueryException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


class SampleCode
{

	int count = 0;

	static long  avgOnlineTime = 0;
	static long avgPreTime = 0;
	static long avgTotalTime = 0;

	public static void main(String [] args)
	{

		//we have sent a previous email w.r.t the format of the input files. This should be as generated by the generator we have sent you.

		File data = new File(System.getProperty("user.dir"));//+"/run_"+0);

		//the constructor of the IndexViews object preprocess the views and constructs our internal data structures and indices. This is the so-called pre-processing phase in our paper:
		//[George Konstantinidis, Jose Luis Ambite, Scalable Query Rewriting: A Graph-Based Approach, in Proceedings of the ACM SIGMOD International Conference on Managment of Data, SIGMOD'11, June 12-16, 2011, Athens, Greece.]	

		//number of views/sources (lines) to parse from the input file
		int numberOfSources = 20;

		//parsing will get all views if you give a number larger than the number of lines in the file.
		//NOTE: Don't leave empty lines after your last view description in the input file (one new line is ok but not more); parser will throw an exception

		//for(int h=0; h<=10; h++)
		{
		
			//if(h==6)
			/*{
				avgOnlineTime = 0;
				avgPreTime = 0;
				avgTotalTime = 0;
			}*/
			
		long st = System.currentTimeMillis(); 
		IndexViews viewsIndex = new IndexViews(new File(data.getAbsolutePath()+"/requiem_expansions/"+"testSource3.txt"), 50);
		long end = System.currentTimeMillis(); 
		avgPreTime += (long)(end-st);
		
		System.out.println("Pretime: "+ (long)(end-st));
				
		st = System.currentTimeMillis(); 
		//GQR takes the pre-processed views and the path to query (again do not leave empty lines after the query in the file)
		GQR g = new GQR(new File(data.getAbsolutePath()+"/requiem_expansions/"+"testQuery3.txt"),viewsIndex);

		List<CompRewriting> res = new ArrayList<CompRewriting>();
		try {
			
			res = g.reformulate(g.getQuery());

		} catch (NonAnswerableQueryException e) {
			System.out.println(" No rewritings ");
		}

		
		ListIterator<CompRewriting> resIterator = res.listIterator();
		while(resIterator.hasNext())
			System.out.println(resIterator.next().toString());
		
//		for(CompRewriting r:res)
//		System.out.println(r);
		
		end = System.currentTimeMillis(); 
		
		avgOnlineTime += (long)(end-st);

		System.out.println("Runtime: "+(long)(end-st));
		System.out.println("Rewritings: "+(res.size()));
		}
		
		
		avgOnlineTime = (long)(avgOnlineTime/5.0);
		avgPreTime = (long)(avgPreTime/5.0);
		avgTotalTime = ((long)(avgPreTime+avgOnlineTime));
		

		System.out.println("AVG PREPROC TIME: "+avgPreTime);
		System.out.println("AVG ONLINE TIME: "+avgOnlineTime);
		System.out.println("AVG TOTAL TIME: "+avgTotalTime);


	}


}

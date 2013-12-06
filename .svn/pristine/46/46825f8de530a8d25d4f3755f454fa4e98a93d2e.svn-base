package gqr;

import java.io.File;

public class FolderIterator {

	public static void main(String[] args)
	{
//		String com = "";
//		for(int i =0; i<=98; i++)
//		{
//			com += "mv query_"+i+".txt views_for_q_"+i+"/.;";
//		}
//		System.out.println(com+"mv query_99.txt views_for_q_99/.");
	
//		String com = "";
//		for(int i =0; i<=98; i++)
//		{
//			com += "rm run_"+i+"/gqr.jar; ";//cp gqr.jar "+"run_"+i+"/.;
//		}
//		System.out.println(com+"rm run_"+99+"/gqr.jar");//; cp gqr.jar "+" run_"+99+"/.
		
		String com = "";
		for(int i =0; i<=98; i++)
		{
			com += "cp half_dist/run_"+i+"/views_for_q_"+i+"/queryHD_"+i+".txt hd_till_80/run_"+i+"/views_for_q_"+i+"/.;";
		}
		System.out.println(com+"cp half_dist/run_"+99+"/views_for_q_"+99+"/queryHD_"+99+".txt hd_till_80/run_"+99+"/views_for_q_"+99+"/.");//; cp gqr.jar "+" run_"+99+"/.
		
//		
//		com = "";
//		for(int i =0; i<=98; i++)
//		{
//			com += "rm run_"+i+"/*.err;rm run_"+i+"/*.log; rm run_"+i+"/*.out;";
//		}
//		System.out.println(com+"rm run_"+99+"/*.err;rm run_"+99+"/*.log; rm run_"+99+"/*.out");
//		
//		 System.out.println(com+"rm -r run_"+99+"/mydb");
		
//		String path = System.getProperty("user.dir");//+"/examples/";
//		searchFolders(new File(path));
//		System.out.println("Done"); 
	}

	/**

	 *

	 * @param fo - File object received recursively.

	 */

	public static File searchFolders(File fo)
	{
		if(fo.isDirectory())
		{
			if(fo.getName().startsWith("views_for_q"))
			{
				return fo;
			}
			
			String internalNames[] = fo.list();

			for(int i=0; i<internalNames.length; i++)
			{
				File f= searchFolders(new File(fo.getAbsolutePath() + "/" + internalNames[i]));
				if(f!=null)
					return f;

			}
		}
		
		return null;
		
	}
}
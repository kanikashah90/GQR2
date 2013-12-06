package gqr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Utils {

	/**
	 * @param args
	 * @throws IOException 
	 */
	static void main(String[] args) throws IOException {


		String var = "2@v44.m14004{0}_0";
		var = var.replaceAll("_", "CCC");
		var = var.replaceAll("@", "AT");
		var = var.replaceAll("\\(", "lpar");
		var = var.replaceAll("\\)", "rpar");
		var = var.replaceAll("\\{", "lb");
		var = var.replaceAll("\\}", "rb");
		var = var.replaceAll("\\.", "DOT");
		System.out.println(var);	
		assert(false);
//		for(int i=0; i<=200; i++)
//		{
//			
////			FileWriter fw = new FileWriter(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/submitscripts/"+"gqr_"+i+".sub");
////			BufferedWriter out  = new BufferedWriter(fw);
////			
////			out.write("#PBS -l walltime=24:00:00\n");
////			out.write("#PBS -l mem=1gb\n");
////			out.write("#PBS -M konstant\n");
////			out.write("#PBS -o gqr_"+i+".out\n");
////			out.write("#PBS -e gqr_"+i+".err\n");
////			out.write("cd \"$PBS_O_WORKDIR\"\n");
////			
////			out.write("java -jar gqr.jar "+i+"\n");
////			out.close();
////			
//			int j = 8797485+i;
//			System.out.print("qdel "+j+";");
////			System.out.print("cp mcdsat.jar run_"+i+"/.;");
//			
//		}
//		
	}
	
	static class FolderIterator {

		static void main(String[] args)
		{
//			String com = "";
//			for(int i =0; i<=98; i++)
//			{
//				com += "mv query_"+i+".txt views_for_q_"+i+"/.;";
//			}
//			System.out.println(com+"mv query_99.txt views_for_q_99/.");
		
//			String com = "";
//			for(int i =0; i<=98; i++)
//			{
//				com += "rm run_"+i+"/gqr.jar; ";//cp gqr.jar "+"run_"+i+"/.;
//			}
//			System.out.println(com+"rm run_"+99+"/gqr.jar");//; cp gqr.jar "+" run_"+99+"/.
			
			String com = "";
			for(int i =0; i<=98; i++)
			{
				com += "cp half_dist/run_"+i+"/views_for_q_"+i+"/queryHD_"+i+".txt hd_till_80/run_"+i+"/views_for_q_"+i+"/.;";
			}
			System.out.println(com+"cp half_dist/run_"+99+"/views_for_q_"+99+"/queryHD_"+99+".txt hd_till_80/run_"+99+"/views_for_q_"+99+"/.");//; cp gqr.jar "+" run_"+99+"/.
			
//			
//			com = "";
//			for(int i =0; i<=98; i++)
//			{
//				com += "rm run_"+i+"/*.err;rm run_"+i+"/*.log; rm run_"+i+"/*.out;";
//			}
//			System.out.println(com+"rm run_"+99+"/*.err;rm run_"+99+"/*.log; rm run_"+99+"/*.out");
//			
//			 System.out.println(com+"rm -r run_"+99+"/mydb");
			
//			String path = System.getProperty("user.dir");//+"/examples/";
//			searchFolders(new File(path));
//			System.out.println("Done"); 
		}

		/**

		 *

		 * @param fo - File object received recursively.

		 */

		static File searchFolders(File fo)
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
}

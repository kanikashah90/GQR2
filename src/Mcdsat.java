


import gr.forth.ics.aggregator.Aggregator;
import gr.forth.ics.aggregator.DataTypes;
import gr.forth.ics.aggregator.Database;
import gr.forth.ics.aggregator.DbFactories;
import gr.forth.ics.aggregator.Filters;
import gr.forth.ics.aggregator.Record;
import gr.forth.ics.aggregator.Records;
import gr.forth.ics.aggregator.Schema;
import gr.forth.ics.aggregator.diagram.Diagram;
import gr.forth.ics.aggregator.diagram.DiagramFactory;
import gr.forth.ics.aggregator.diagram.gnuplot.GnuPlotContext;
import gr.forth.ics.aggregator.diagram.gnuplot.GnuPlotWriter;
import gr.forth.ics.aggregator.diagram.jfreechart.Chart;
import gr.forth.ics.aggregator.diagram.jfreechart.ChartFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.StringTokenizer;


class Mcdsat {

	/**
	 * @param args
	 */

	private enum Variables {
		Query, ViewNo, Time, RewNo
	}

	static void main(String[] args) {
		
//		int run = Integer.parseInt(args[0]);
//		
//		Database db;
//		try {
//			db = DbFactories.localDerby().getOrCreate("mcdsat_DBSTAR_run_"+run);
//		} catch (SQLException e1) {
//			throw new RuntimeException(e1);
//		}
//		//
//		//		//      Database db = DbFactories.remoteDerby("localhost", "ounos", "ounos").
//		//		//      getOrCreate("mydb");
//		Schema schema = new Schema().add(Variables.Query, DataTypes.MED_STRING).
//		add(Variables.ViewNo, DataTypes.INTEGER).
//		add(Variables.Time, DataTypes.LONG).add(Variables.RewNo, DataTypes.INTEGER);
//
//		Aggregator aggregator;
//		try {
//			aggregator = db.forceCreate(schema, "aggregator");
////									aggregator = db.get("aggregator");
//
//		} catch (SQLException e1) {
//			throw new RuntimeException(e1);
//		}
//
//
//		File data = FolderIterator.searchFolders(new File(System.getProperty("user.dir")));
////		System.out.println("data:  "+data);
////		System.out.println(System.getProperty("user.dir")+" data:"+data);
//		int countQ = Integer.parseInt(data.getName().substring(12));
//		if(countQ != run)
//			throw new RuntimeException("count different than q");

//		aggregator.filtered(Filters.and(Filters.eq(Variables.Query, "queryHD_5"),Filters.eq(Variables.ViewNo, 113))).deleteRecords();
		
		
//		for(int countQ = 0; countQ<=19; countQ++)
		int countQ=9;
		{
//						if((countQ == 42) || (countQ==67))
//							continue;
			//		int countQ = 4;

			//			File data = new File(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/data/half_dist/run_"+countQ+"/views_for_q_"+countQ);
//			File data = new File(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/data/hd_till_80/run_"+countQ+"/views_for_q_"+countQ);
//			File data = new File(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/star_queries/run_"+countQ+"/views_for_q_"+countQ);
//			File data = new File(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/chain_10000_views/run_"+countQ);
			File data = new File(System.getProperty("user.dir"));
//			File data = new File(System.getProperty("user.dir")+"/examples/");
//			
			System.out.println("\n ----Query "+countQ+"\n");
//
//			for(int viewNo=63; viewNo<140; viewNo++)
//			int viewNo =41;
			{
//				System.out.println("********view "+viewNo+" *******");
				System.out.flush();

				System.out.println("absolute path:  "+data.getAbsolutePath());
				Process p;
				int v = -200;
				Count count = new Count();
				long time;
				try {
					p = Runtime.getRuntime().exec(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/Python-2.7.1/python "+System.getProperty("user.home")+"/users_link/gkonstant/Desktop/ssdsat/driver.py -t RW -v "+data.getAbsolutePath()+"/"+"view_ex -q "+data.getAbsolutePath()+"/"+"query_ex");
					
//					p = Runtime.getRuntime().exec(System.getProperty("user.dir")+"/MCDSAT/mcdsat/mcdsat RW "+data.getAbsolutePath()+"/"+"view_ex "+data.getAbsolutePath()+"/"+"query_ex");
//					p = Runtime.getRuntime().exec(System.getProperty("user.dir")+"/MCDSAT/mcdsat/mcdsat RW "+data.getAbsolutePath()+"/"+"250_views_for_q_"+countQ+".txt "+data.getAbsolutePath()+"/"+"query_"+countQ+".txt");
					// any error message?
					StreamGobbler errorGobbler = new 
					StreamGobbler(p.getErrorStream(), "ERROR", count);            

					// any output?
					StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", count);

					// kick them off
					errorGobbler.start();
					outputGobbler.start();


					v = p.waitFor();
					time=((long)((outputGobbler.timeC+outputGobbler.timeM+outputGobbler.timeT)*1000));
					System.out.println("Process "+countQ+ ((v==0)?" finished OK":" crashed, v="+v) +"time ="+time+" ~~~~~~~~~~~~~~~~~~ rewritings = "+count.count);
				} catch (IOException e) {
					throw new RuntimeException(e);

				}
				catch (InterruptedException e) {
					throw new RuntimeException(e);
				}


				
//				aggregator.record(
//						new Record().add(Variables.Query, "queryHD_"+countQ).
//						add(Variables.ViewNo, viewNo).
//						add(Variables.Time, time).add(Variables.RewNo, (count.count > 3)?(count.count-4):0));

			}
		}
		
////		Records records = aggregator.filtered(Filters.eq(Variables.Query, "queryHD_4")).averageOf(Variables.Time).per(Variables.ViewNo);
////
////		System.out.println(records);
//		Records records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo);
//
//		Diagram diagram = DiagramFactory.newDiagram(records).
//		withTitle("Mcdsat").		
//		withRangeLabel("RewNo").
//		withVariableLabel(0, "ViewNo");
//////
//		GnuPlotContext context = new GnuPlotContext();
//		context.setLogscaleY();
//		GnuPlotWriter writer = new GnuPlotWriter(new File("diagramsFolder"));
//		try {
//			writer.writeDiagram(diagram, "HPCmcdsatHD80query_"+run,context);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////
////		//		final Chart chart = new ChartFactory(diagram).newLineChart();
////		//
////		//		try {
////		//			chart.write(800, 600, "jpeg", new File(System.getProperty("user.dir")+"\\MiniconDiagramT93.jpg"));
////		//		} catch (IOException e) {
////		//			e.printStackTrace();
////		//		}
////
//		try {
//			db.shutDown();
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
		
	}
}

class StreamGobbler extends Thread
{
	InputStream is;
	String type;
	Count counting;
	double timeT;
	double timeC;
	double timeM;

	StreamGobbler(InputStream is, String type, Count count)
	{
		this.is = is;
		this.type = type;
		counting = count;
	}

	public void run()
	{
		if(type.equals("OUTPUT"))
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line=null;
				while ( (line = br.readLine()) != null)
				{
					System.out.println(line);
					
					if(line.startsWith("[Done]"))
					{
						 
					     StringTokenizer st = new StringTokenizer(line);
					     st.nextToken();
					     String time1= st.nextToken();
					     String time2= st.nextToken();
					     String time3= st.nextToken();
					     
					     timeT = Double.parseDouble(time1);
					     timeC = Double.parseDouble(time2);
					     timeM = Double.parseDouble(time3);
					}
					counting.count++;
				}
			} catch (IOException ioe)
			{
				ioe.printStackTrace();  
			}
		}else
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line=null;
				while ( (line = br.readLine()) != null)
				{
					System.out.println(type + ">" + line);
				}
			} catch (IOException ioe)
			{
				ioe.printStackTrace();  
			}
		}
	}
}

class Count
{
	int count;
	Count()
	{
		count = 0;
	}
}

package gqr;

import gr.forth.ics.aggregator.*;
import gr.forth.ics.aggregator.diagram.Diagram;
import gr.forth.ics.aggregator.diagram.DiagramFactory;
import gr.forth.ics.aggregator.diagram.gnuplot.GnuPlotContext;
import gr.forth.ics.aggregator.diagram.gnuplot.GnuPlotWriter;
import gr.forth.ics.aggregator.diagram.gnuplot.Pattern;
import gr.forth.ics.aggregator.diagram.gnuplot.PlotStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

class Plotter {

	private enum Variables {
		Query, ViewNo, Time, Framework, RewNo, ExcludedTime
	}
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	static void main(String[] args) throws FileNotFoundException {

		//		plotTenThousandViewQueries();
		//plotStarQueries();
		//		plotChainQueries();
		//		plotRandomQueries();
		//		gatherMeasurementsInSingleFile();
		plotFromSimpleTextFile();
	}

	private static void gatherMeasurementsInSingleFile() throws FileNotFoundException {

		FileWriter outFile;
		PrintWriter out = null;

		try{
			outFile = new FileWriter(System.getProperty("user.dir")+"/result.txt");
			out = new PrintWriter(outFile,true);
		} catch (IOException e){
			e.printStackTrace();
		}

		outer:	for(int run=0; run<=99; run++)
		{
			String dir = System.getProperty("user.dir")+"/result_"+run+".txt";

			Scanner sc = new Scanner(new File(dir));

			String line = null;
			while(sc.hasNext())
			{
				line = sc.nextLine();
				Scanner sc1 = new Scanner(line).useDelimiter("\\t");

				String query = null;
				Integer viewNo = null;
				Long onlinetime = null;
				Integer rewNo = null;
				Long preproctime = null;

				try{
					query = sc1.next();
					viewNo = Integer.parseInt(sc1.next());
					onlinetime = Long.parseLong(sc1.next());
					rewNo = Integer.parseInt(sc1.next());
					preproctime = Long.parseLong(sc1.next());
				}catch (NoSuchElementException e) {
					System.out.println("Skipped test "+run);
					continue outer;
				}
			}
			System.out.println("Appended "+run);
			out.append(line+"\n");
		}
		out.close();
	}

	private static void plotFromSimpleTextFile() throws FileNotFoundException {

		String dir = "/lfs1/GQRexperiments/experiments_before_releasing_to_caracas/chain_100qX140v_20PredSpace_8PredBody_4var_10Dtill80v_3Dtill140v_5repMax/results/";

		Scanner sc = new Scanner(new File(dir+"result.txt"));

		Database db;
		try {
			db = DbFactories.localDerby().getOrCreate(dir+"gqrDBchainTo140");

		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}

		Schema schema = new Schema().
		add(Variables.ViewNo, DataTypes.INTEGER).
		add(Variables.Time, DataTypes.LONG).
		add(Variables.Framework, DataTypes.MED_STRING).
		add(Variables.Query, DataTypes.MED_STRING).add(Variables.RewNo, DataTypes.INTEGER);

		Aggregator aggregator;
		try {
			aggregator = db.getOrCreate(schema,"aggregator");	
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}		
		
		int i = 0;
		while(sc.hasNext())
		{
			Scanner sc1 = new Scanner(sc.nextLine()).useDelimiter("\\t");

			String query = null;
			Integer viewNo = null;
			Long onlinetime = null;
			Integer rewNo = null;
			Long preproctime = null;

			try{
				query = sc1.next();
				viewNo = Integer.parseInt(sc1.next());
				onlinetime = Long.parseLong(sc1.next());
				rewNo = Integer.parseInt(sc1.next());
				preproctime = Long.parseLong(sc1.next());
			}catch (NoSuchElementException e) {
				System.out.println("Skipped "+i++);
				continue;
			}

			System.out.println("Added "+i++);

			Record rec = new Record().add(Variables.Query,query).add(Variables.ViewNo, viewNo).add(Variables.Time, onlinetime).add(Variables.RewNo, rewNo).add(Variables.Framework, "gqr");
			System.out.println(rec);
			//add online time in the framework gqr
			aggregator.record(rec);

			//add preproc time in the framework p
			aggregator.record(new Record().add(Variables.Query,query).add(Variables.ViewNo, viewNo).add(Variables.Time, preproctime).add(Variables.RewNo, rewNo).add(Variables.Framework, "p"));

			//add total time in the framework gqr
			aggregator.record(new Record().add(Variables.Query,query).add(Variables.ViewNo, viewNo).add(Variables.Time, ((long)(onlinetime+preproctime))).add(Variables.RewNo, rewNo).add(Variables.Framework, "gqr+p"));
		}

		Records records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo, Variables.Framework);
		//filtered(Filters.notEq(Variables.Query, "q1")).filtered(Filters.notEq(Variables.Query, "q37")).filtered(Filters.notEq(Variables.Query, "q75")).filtered(Filters.notEq(Variables.Query, "q77")).filtered(Filters.notEq(Variables.Query, "q83")).filtered(Filters.notEq(Variables.Query, "q87")).filtered(Filters.notEq(Variables.Query, "q89"))
		//.filtered(Filters.notEq(Variables.Framework, "gqr-index"))
		//.filtered(Filters.eq(Variables.Framework, "gqr+p"))

		Diagram diagram = DiagramFactory.newDiagram(records).withTitle("chain, 100x140, 8 preds body, 4 var/pred, <=5 repeated preds").withRangeLabel("time"/*"number of conj. rewritings"*/).withVariableLabel(0, "number of views");

		GnuPlotContext context = new GnuPlotContext();
		context.setLogscaleY();
		GnuPlotWriter writer = new GnuPlotWriter(new File(dir+"diagramsFolder"));
		try {
			writer.writeDiagram(diagram, "chain140viewsAllTimes",context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			db.shutDown();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}


	}

	private static void plotTenThousandViewQueries() {
		String dir = "/users_link/gkonstant/Desktop/experiments/chain_100qX140v_20PredSpace_8PredBody_4var_10Dtill80v_3Dtill140v_5repMax/new_results_from_data_currently_on_cluster_13_July_11_evenmoreoptimized_assertionsoff/results/gqr/";
		Database db_gqr_tenth ;
		try {
			db_gqr_tenth = DbFactories.localDerby().getOrCreate(System.getProperty("user.home")+dir+"combDB100x140");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}

		Schema schema = new Schema().
		add(Variables.ViewNo, DataTypes.INTEGER).
		add(Variables.Time, DataTypes.LONG).
		add(Variables.Framework, DataTypes.MED_STRING).
		add(Variables.Query, DataTypes.MED_STRING).add(Variables.RewNo, DataTypes.INTEGER);

		Aggregator aggregator1;
		try {
			aggregator1 = db_gqr_tenth.get("aggregator");//forceCreate(schema, "aggregator");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}

		//		for(int i =0; i<=99; i++)
		//		{
		//			if(i==42 || i==67 ) //(i==1 || i==37 || i==42 || i==67 || i==75 || i==77 || i==83 || i==87 || i==89)
		//				continue;
		//			
		//			System.out.println("DB "+i);
		//
		//		
		//			Database db_gqr;
		//			try {
		//				db_gqr = DbFactories.localDerby().getOrCreate(System.getProperty("user.home")+dir+"gqrDBchainTo10000_run_"+i);
		//
		//			} catch (SQLException e1) {
		//				throw new RuntimeException(e1);
		//			}
		//			Aggregator aggregator;
		//			try {
		//				aggregator = db_gqr.get("aggregator");	
		//			} catch (Exception e1) {
		//				System.out.println(" DB "+i+"--------> skipped");
		//				continue;
		//			}
		//			
		////			(Variables.Query, g.getQuery().getName()).
		////			(Variables.ViewNo, temp).
		////			(Variables.Time, (long)(((long)(end-st))-g.dontCountTime)).
		////			(Variables.RewNo, g.reNo).
		////			(Variables.ExcludedTime,g.dontCountTime)
		//
		//			Records	records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo);
		//			Records	records2 = aggregator.averageOf(Variables.ExcludedTime).per(Variables.ViewNo);
		//			Records	records3 = aggregator.averageOf(Variables.RewNo).per(Variables.ViewNo);
		//			
		//			
		//
		//			Diagram diagram = DiagramFactory.newDiagram(records).withTitle("Query"+i+", 8 preds body, 4 var/pred, <=5 repeated preds").withRangeLabel("time"/*"number of conj. rewritings"*/).withVariableLabel(0, "number of views");
		//
		//			GnuPlotContext context = new GnuPlotContext();
		//			context.setLogscaleY();
		//			GnuPlotWriter writer = new GnuPlotWriter(new File(System.getProperty("user.home")+dir+"diagramsFolder"));
		//			try {
		//				writer.writeDiagram(diagram, "chain140viewsTIME_Query"+i,context);
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//					
		//			
		//
		//			List<Record> lr = records.list();
		//			List<Record> lr2 = records2.list();
		//			List<Record> lr3 = records3.list();
		//			
		//			if(lr.size()!=lr2.size())
		//				throw new RuntimeException("hah");
		//			
		//			
		//				for(int g=0; g<lr.size(); g++)
		//				{
		//					Record r = lr.get(g);
		//					Record r2 = lr2.get(g);
		//					Record r3 = lr3.get(g);
		//					
		//					aggregator1.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, r.getValue()).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "gqr")
		//						.add(Variables.Query,"q"+i ));
		//				
		//					aggregator1.record(
		//							new Record().
		//							add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//							add(Variables.Time, Long.parseLong(r.getValue().toString())+Long.parseLong(r2.getValue().toString())).
		//							add(Variables.RewNo, r3.getValue()).
		//							add(Variables.Framework, "gqr+p")
		//							.add(Variables.Query,"q"+i ));
		//					
		//					aggregator1.record(
		//							new Record().
		//							add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//							add(Variables.Time, r2.getValue()).
		//							add(Variables.RewNo, r3.getValue()).
		//							add(Variables.Framework, "p")
		//							.add(Variables.Query,"q"+i ));
		//					
		//				}
		//
		//
		//			try {
		//				db_gqr.shutDown();
		//			} catch (SQLException e) {
		//				throw new RuntimeException(e);
		//			}
		//		}

		Records records = aggregator1.filtered(Filters.notEq(Variables.Query, "q1")).filtered(Filters.notEq(Variables.Query, "q37")).filtered(Filters.notEq(Variables.Query, "q75")).filtered(Filters.notEq(Variables.Query, "q77")).filtered(Filters.notEq(Variables.Query, "q83")).filtered(Filters.notEq(Variables.Query, "q87")).filtered(Filters.notEq(Variables.Query, "q89")).averageOf(Variables.Time).per(Variables.ViewNo, Variables.Framework);
		//.filtered(Filters.notEq(Variables.Framework, "gqr-index"))
		//.filtered(Filters.eq(Variables.Framework, "gqr+p"))
		Diagram diagram = DiagramFactory.newDiagram(records).withTitle("chain, 100x140, 8 preds body, 4 var/pred, <=5 repeated preds").withRangeLabel("time"/*"number of conj. rewritings"*/).withVariableLabel(0, "number of views");

		GnuPlotContext context = new GnuPlotContext();
		context.setLogscaleY();
		GnuPlotWriter writer = new GnuPlotWriter(new File(System.getProperty("user.home")+dir+"diagramsFolder"));
		try {
			writer.writeDiagram(diagram, "chain140viewsAllTimesSameViews",context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			db_gqr_tenth.shutDown();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	static void plotStarQueries()
	{
		//		Database db;
		//		try {
		//			db = DbFactories.localDerby().getOrCreate("comDBtenth");
		//		} catch (SQLException e1) {
		//			throw new RuntimeException(e1);
		//		}
		////		
		//		Schema schema = new Schema().
		//		add(Variables.ViewNo, DataTypes.INTEGER).
		//		add(Variables.Time, DataTypes.LONG).
		//		add(Variables.Framework, DataTypes.MED_STRING).
		//		add(Variables.Query, DataTypes.MED_STRING).add(Variables.RewNo, DataTypes.INTEGER);
		////		
		//		Aggregator aggregator;
		//		try {
		//			aggregator = db.forceCreate(schema, "aggregator");
		////			aggregator = db.get("aggregator");
		//		} catch (SQLException e1) {
		//			throw new RuntimeException(e1);
		//		}
		////		aggregator.filtered(Filters.eq(Variables.Query, "q85")).deleteRecords();
		//////////		set yrange[2200:3000]
		//		for(int i=0; i<=99; i++)
		//		{
		//			if(i==24)
		//				continue;
		////			
		//			System.out.println("query "+i);
		//			
		//			Database db_gqr;
		//			try {
		//				db_gqr = DbFactories.localDerby().getOrCreate(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/star_queries/finished_25_JUN/star/gqrDB_run_"+i);
		//			} catch (SQLException e1) {
		//				throw new RuntimeException(e1);
		//			}
		//
		//
		//			Aggregator aggregator_gqr;
		//			try {
		//			aggregator_gqr = db_gqr.get("aggregator");
		//			} catch (SQLException e1) {
		//				throw new RuntimeException(e1);
		//			}
		////		
		////			
		//			Database db_mcdsat;
		//			try {
		//				db_mcdsat = DbFactories.localDerby().getOrCreate(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/star_queries/finished_new/mcdsat_DBSTAR_run_"+i);
		//			} catch (SQLException e1) {
		//				throw new RuntimeException(e1);
		//			}	
		//
		//			Aggregator aggregator_mcdsat;
		//			try {
		//				aggregator_mcdsat = db_mcdsat.get("aggregator");
		//			} catch (SQLException e1) {
		//				throw new RuntimeException(e1);
		//			}
		//			
		////			System.out.println("Database "+i);
		////			
		////			Records rec =  aggregator_gqr.count().per(Variables.Query);
		////			
		////			for(Record r: rec)
		////				System.out.println(r);
		//
		//
		//			Records	records = aggregator_gqr.averageOf(Variables.Time).per(Variables.ViewNo);
		//			Records	records2 = aggregator_gqr.averageOf(Variables.ExcludedTime).per(Variables.ViewNo);
		//			Records	records3 = aggregator_gqr.averageOf(Variables.RewNo).per(Variables.ViewNo);
		//			
		//	
		//			List<Record> lr = records.list();
		//			List<Record> lr2 = records2.list();
		//			List<Record> lr3 = records3.list();
		//			
		//			if(lr.size()!=lr2.size())
		//				throw new RuntimeException("hah");
		//			
		//			
		//				for(int g=0; g<lr.size(); g++)
		//				{
		//					Record r = lr.get(g);
		//					Record r2 = lr2.get(g);
		//					Record r3 = lr3.get(g);
		//					
		//					aggregator.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, r.getValue()).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "gqr")
		//						.add(Variables.Query,"q"+i ));
		//				
		//					aggregator.record(
		//							new Record().
		//							add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//							add(Variables.Time, Long.parseLong(r.getValue().toString())+Long.parseLong(r2.getValue().toString())).
		//							add(Variables.RewNo, r3.getValue()).
		//							add(Variables.Framework, "gqr-p")
		//							.add(Variables.Query,"q"+i ));
		//					
		//					aggregator.record(
		//							new Record().
		//							add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//							add(Variables.Time, r2.getValue()).
		//							add(Variables.RewNo, r3.getValue()).
		//							add(Variables.Framework, "gqr-index")
		//							.add(Variables.Query,"q"+i ));
		//					
		//				}
		//				
		//				
		//				records = aggregator_mcdsat.averageOf(Variables.Time).per(Variables.ViewNo);
		//				records3 = aggregator_mcdsat.averageOf(Variables.RewNo).per(Variables.ViewNo);
		//				
		//				lr = records.list();
		//				lr3 = records3.list();
		//				for(int g=0; g<lr.size(); g++)
		//				{
		//					Record r = lr.get(g);
		//					Record r3 = lr3.get(g);
		//					
		//					aggregator.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Time, Long.parseLong(r.getValue().toString())).add(Variables.Framework, "mcdsat")
		//						.add(Variables.Query,"q"+i ));
		//				}
		//
		//				try {
		//					db_gqr.shutDown();
		//					db_mcdsat.shutDown();
		//				} catch (SQLException e) {
		//					throw new RuntimeException(e);
		//				}
		//
		//		}

		//		Records records = aggregator.filtered(
		//				Filters.or(
		//						Filters.and(
		//								Filters.ge(Variables.RewNo,5),Filters.eq(Variables.Framework,"mcdsat")
		//								),
		//						Filters.and(
		//								Filters.ge(Variables.RewNo,10),Filters.eq(Variables.Framework,"gqr")
		//								), 
		//						Filters.and(
		//								Filters.ge(Variables.RewNo,10),Filters.eq(Variables.Framework,"gqr-p")
		//								)		
		//				 			)
		//				 ).filtered(Filters.notEq(Variables.Framework,"gqr-index")).averageOf(Variables.Time).per(Variables.ViewNo,Variables.Framework);



		//		Records records = aggregator.filtered(Filters.notEq(Variables.Framework,"gqr-index")).averageOf(Variables.Time).per(Variables.ViewNo,Variables.Framework);
		//		.filtered(Filters.eq(Variables.ViewNo,31)).filtered(Filters.le(Variables.RewNo,30))
		//		Records records = aggregator.ordered(Orders.asc(Variables.RewNo)).filtered(Filters.notEq(Variables.Framework,"gqr-index")).filtered(Filters.notEq(Variables.Framework,"gqr-p")).filtered(Filters.gt(Variables.RewNo,0)).count().per(Variables.Query,Variables.ViewNo,Variables.RewNo,Variables.Framework);

		//		System.out.println(records);
		//		
		//		filtered(Filters.gt(Variables.RewNo,0))
		//.filtered(Filters.gt(Variables.RewNo,0))
		//.filtered(Filters.eq(Variables.RewNo,0))
		//.filtered(Filters.le(Variables.RewNo,4))			
		//		//Filters.and(Filters.notEq(Variables.Framework,"gqr-index"),Filters.notEq(Variables.Framework,"gqr-p"))
		//
		//		Diagram diagram = DiagramFactory.newDiagram(records).withTitle("99 star queries").withRangeLabel("Query Time").withVariableLabel(0, "Number of Views");
		////	      
		////		System.out.println(records);
		////		
		//		GnuPlotContext context = new GnuPlotContext();
		//		context.setLogscaleY();
		//		context.set("key","left");		
		//		GnuPlotWriter writer = new GnuPlotWriter(new File("diagramsFolder"));
		//		try {
		//			writer.writeDiagram(diagram, "HPC_star_all_time",context);
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		try {
		//			db.shutDown();
		//		} catch (SQLException e) {
		//			throw new RuntimeException(e);
		//		}

	}

	static void plotChainQueries()
	{
		Database db;
		try {
			db = DbFactories.localDerby().getOrCreate("comDBAllchain");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}

		Schema schema = new Schema().
		add(Variables.ViewNo, DataTypes.INTEGER).
		add(Variables.Time, DataTypes.LONG).
		add(Variables.Framework, DataTypes.MED_STRING).
		add(Variables.Query, DataTypes.MED_STRING).add(Variables.RewNo, DataTypes.INTEGER);

		Aggregator aggregator1;
		try {
			aggregator1 = db.get("aggregator");//forceCreate(schema, "aggregator");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}

		String dir = "/opt/GQRexperiments/chain100x140_GQR_BUGFREE_JAN_30_2012/";

		//		for(int i =0; i<=99; i++)
		//		{
		////			if(i==42 || i==67 ) //(i==1 || i==37 || i==42 || i==67 || i==75 || i==77 || i==83 || i==87 || i==89)
		////				continue;
		////			
		//			System.out.println("DB "+i);
		////
		////		
		//			Database db_gqr;
		//			try {
		//				db_gqr = DbFactories.localDerby().getOrCreate(dir+"gqrDBchainTo10000_run_"+i);
		//
		//			} catch (SQLException e1) {
		//				throw new RuntimeException(e1);
		//			}
		//			Aggregator aggregator;
		//			try {
		//				aggregator = db_gqr.get("aggregator");	
		//			} catch (Exception e1) {
		//				System.out.println(" DB "+i+"--------> skipped");
		//				continue;
		//			}
		////			
		//////			(Variables.Query, g.getQuery().getName()).
		//////			(Variables.ViewNo, temp).
		//////			(Variables.Time, (long)(((long)(end-st))-g.dontCountTime)).
		//////			(Variables.RewNo, g.reNo).
		//////			(Variables.ExcludedTime,g.dontCountTime)
		////
		//			Records	records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo);
		//			Records	records2 = aggregator.averageOf(Variables.ExcludedTime).per(Variables.ViewNo);
		//			Records	records3 = aggregator.averageOf(Variables.RewNo).per(Variables.ViewNo);
		////			
		////			
		////
		////			Diagram diagram = DiagramFactory.newDiagram(records).withTitle("Query"+i+", 8 preds body, 4 var/pred, <=5 repeated preds").withRangeLabel("time"/*"number of conj. rewritings"*/).withVariableLabel(0, "number of views");
		////
		////			GnuPlotContext context = new GnuPlotContext();
		////			context.setLogscaleY();
		////			GnuPlotWriter writer = new GnuPlotWriter(new File(System.getProperty("user.home")+dir+"diagramsFolder"));
		////			try {
		////				writer.writeDiagram(diagram, "chain140viewsTIME_Query"+i,context);
		////			} catch (IOException e) {
		////				// TODO Auto-generated catch block
		////				e.printStackTrace();
		////			}
		////					
		////			
		////
		//			List<Record> lr = records.list();
		//			List<Record> lr2 = records2.list();
		//			List<Record> lr3 = records3.list();
		//			
		//			if(lr.size()!=lr2.size())
		//				throw new RuntimeException("hah");
		//
		//
		//			for(int g=0; g<lr.size(); g++)
		//			{
		//				Record r = lr.get(g);
		//				Record r2 = lr2.get(g);
		//				Record r3 = lr3.get(g);
		//
		//				aggregator1.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, r.getValue()).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "gqr")
		//						.add(Variables.Query,"q"+i ));
		//
		//				aggregator1.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, Long.parseLong(r.getValue().toString())+Long.parseLong(r2.getValue().toString())).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "gqr+p")
		//						.add(Variables.Query,"q"+i ));
		//
		//				aggregator1.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, r2.getValue()).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "p")
		//						.add(Variables.Query,"q"+i ));
		//
		//			}
		//
		//
		//			try {
		//				db_gqr.shutDown();
		//			} catch (SQLException e) {
		//				throw new RuntimeException(e);
		//			}
		//		}

		Records records = aggregator1.filtered(Filters.notEq(Variables.Query, "q42")).filtered(Filters.notEq(Variables.Query, "q67")).averageOf(Variables.Time).per(Variables.ViewNo, Variables.Framework);
		//filtered(Filters.notEq(Variables.Query, "q1")).filtered(Filters.notEq(Variables.Query, "q37")).filtered(Filters.notEq(Variables.Query, "q75")).filtered(Filters.notEq(Variables.Query, "q77")).filtered(Filters.notEq(Variables.Query, "q83")).filtered(Filters.notEq(Variables.Query, "q87")).filtered(Filters.notEq(Variables.Query, "q89"))
		//.filtered(Filters.notEq(Variables.Framework, "gqr-index"))
		//.filtered(Filters.eq(Variables.Framework, "gqr+p"))

		Diagram diagram = DiagramFactory.newDiagram(records).withTitle("chain, 100x140, 8 preds body, 4 var/pred, <=5 repeated preds").withRangeLabel("time"/*"number of conj. rewritings"*/).withVariableLabel(0, "number of views");

		GnuPlotContext context = new GnuPlotContext();
		context.setLogscaleY();
		GnuPlotWriter writer = new GnuPlotWriter(new File(dir+"diagramsFolder"));
		try {
			writer.writeDiagram(diagram, "chain140viewsAllTimes",context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			db.shutDown();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	static void plotRandomQueries()
	{
		String dir = "/opt/GQRexperiments/random_30qX150v_GQR_JAN_30_2012/";
		Database db;
		try {
			db = DbFactories.localDerby().getOrCreate(dir+"comDBAllrandom_no_query_as_first_view");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}

		Schema schema = new Schema().
		add(Variables.ViewNo, DataTypes.INTEGER).
		add(Variables.Time, DataTypes.LONG).
		add(Variables.Framework, DataTypes.MED_STRING).
		add(Variables.Query, DataTypes.MED_STRING).add(Variables.RewNo, DataTypes.INTEGER);

		Aggregator aggregator1;
		try {
			aggregator1 = db.getOrCreate(schema,"aggregator");//forceCreate(schema, "aggregator");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}


		//		for(int i =0; i<=29; i++)
		//		{
		////			if(i==42 || i==67 ) //(i==1 || i==37 || i==42 || i==67 || i==75 || i==77 || i==83 || i==87 || i==89)
		////				continue;
		////			
		//			System.out.println("DB "+i);
		////
		////		
		//			Database db_gqr;
		//			try {
		//				db_gqr = DbFactories.localDerby().getOrCreate(dir+"gqrDBchainTo10000_run_"+i);
		//
		//			} catch (SQLException e1) {
		//				throw new RuntimeException(e1);
		//			}
		//			Aggregator aggregator;
		//			try {
		//				aggregator = db_gqr.get("aggregator");	
		//			} catch (Exception e1) {
		//				System.out.println(" DB "+i+"--------> skipped");
		//				continue;
		//			}
		////			
		//////			(Variables.Query, g.getQuery().getName()).
		//////			(Variables.ViewNo, temp).
		//////			(Variables.Time, (long)(((long)(end-st))-g.dontCountTime)).
		//////			(Variables.RewNo, g.reNo).
		//////			(Variables.ExcludedTime,g.dontCountTime)
		////
		//			Records	records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo);
		//			Records	records2 = aggregator.averageOf(Variables.ExcludedTime).per(Variables.ViewNo);
		//			Records	records3 = aggregator.averageOf(Variables.RewNo).per(Variables.ViewNo);
		////			
		////			
		////
		////			Diagram diagram = DiagramFactory.newDiagram(records).withTitle("Query"+i+", 8 preds body, 4 var/pred, <=5 repeated preds").withRangeLabel("time"/*"number of conj. rewritings"*/).withVariableLabel(0, "number of views");
		////
		////			GnuPlotContext context = new GnuPlotContext();
		////			context.setLogscaleY();
		////			GnuPlotWriter writer = new GnuPlotWriter(new File(System.getProperty("user.home")+dir+"diagramsFolder"));
		////			try {
		////				writer.writeDiagram(diagram, "chain140viewsTIME_Query"+i,context);
		////			} catch (IOException e) {
		////				// TODO Auto-generated catch block
		////				e.printStackTrace();
		////			}
		////					
		////			
		////
		//			List<Record> lr = records.list();
		//			List<Record> lr2 = records2.list();
		//			List<Record> lr3 = records3.list();
		//			
		//			if(lr.size()!=lr2.size())
		//				throw new RuntimeException("hah");
		//
		//
		//			for(int g=0; g<lr.size(); g++)
		//			{
		//				Record r = lr.get(g);
		//				Record r2 = lr2.get(g);
		//				Record r3 = lr3.get(g);
		//
		//				aggregator1.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, r.getValue()).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "gqr")
		//						.add(Variables.Query,"q"+i ));
		//
		//				aggregator1.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, Long.parseLong(r.getValue().toString())+Long.parseLong(r2.getValue().toString())).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "gqr+p")
		//						.add(Variables.Query,"q"+i ));
		//
		//				aggregator1.record(
		//						new Record().
		//						add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//						add(Variables.Time, r2.getValue()).
		//						add(Variables.RewNo, r3.getValue()).
		//						add(Variables.Framework, "p")
		//						.add(Variables.Query,"q"+i ));
		//
		//			}
		//
		//
		//			try {
		//				db_gqr.shutDown();
		//			} catch (SQLException e) {
		//				throw new RuntimeException(e);
		//			}
		//		}

		Records records = aggregator1.filtered(Filters.eq(Variables.Framework, "gqr")).averageOf(Variables.Time).per(Variables.ViewNo, Variables.Query);
		//filtered(Filters.notEq(Variables.Query, "q1")).filtered(Filters.notEq(Variables.Query, "q37")).filtered(Filters.notEq(Variables.Query, "q75")).filtered(Filters.notEq(Variables.Query, "q77")).filtered(Filters.notEq(Variables.Query, "q83")).filtered(Filters.notEq(Variables.Query, "q87")).filtered(Filters.notEq(Variables.Query, "q89"))
		//.filtered(Filters.notEq(Variables.Framework, "gqr-index"))
		//.filtered(Filters.eq(Variables.Framework, "gqr+p"))

		Diagram diagram = DiagramFactory.newDiagram(records).withTitle("random, 30qx10000v").withRangeLabel("time"/*"number of conj. rewritings"*/).withVariableLabel(0, "number of views");

		GnuPlotContext context = new GnuPlotContext();
		context.setLogscaleY();
		GnuPlotWriter writer = new GnuPlotWriter(new File(dir+"diagramsFolder"));
		try {
			writer.writeDiagram(diagram, "random10000viewsAllTime",context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			db.shutDown();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void plotForCase(int i, Aggregator aggregator3) {
		Database db_Gqr;

		/*    GET THIS OUT IN MAIN
		 * 		//		Database db;
		//		try {
		//			db = DbFactories.localDerby().getOrCreate("combinedDB");
		//		} catch (SQLException e1) {
		//			throw new RuntimeException(e1);
		//		}

		//		String com = "";
		//		for(int i=53; i<=64; i++)
		//			com+=" gnuplot runDiagram_"+i+".plt;";
		//		com+= "gnuplot runDiagram_65.plt";
		//		System.out.println(com);


		Database db;
		try {
			db = DbFactories.localDerby().getOrCreate("comDB");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}	
//		Schema schema = new Schema().
//		add(Variables.ViewNo, DataTypes.INTEGER).
//		add(Variables.Time, DataTypes.LONG).
	//	add(Variables.RewNo, DataTypes.INTEGER);


		Aggregator aggregator;
		try {
			//aggregator = db.forceCreate(schema, "aggregator");
			aggregator = db.get("aggregator");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}

//		Plotter pl = new Plotter();
//		for(int i=0; i<=65; i++)
//			pl.plotForCase(i,aggregator);


		Records records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo);
		//				

		Diagram diagram = DiagramFactory.newDiagram(records);
		//	        
		GnuPlotContext context = new GnuPlotContext();
		context.setLogscaleY();
		GnuPlotWriter writer = new GnuPlotWriter(new File("diagramsFolder"));
		try {
			writer.writeDiagram(diagram, "combined2",context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			db.shutDown();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		 */
		try {
			db_Gqr = DbFactories.localDerby().get(System.getProperty("user.home")+"/users_link/gkonstant/Desktop/new_half_dist/run_"+i+"/mydb");
		} catch (Exception e1) {
			System.out.println("CASE "+i+" *****"+ e1.getMessage());
			return;

		}
		System.out.println("yeeeeeeeeeeeeeeeee");

		Aggregator aggregator;
		try {
			aggregator = db_Gqr.get("aggregator");
		} catch (SQLException e1) {
			throw new RuntimeException(e1);
		}


		//		
		//		
		Records records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo,Variables.RewNo);
		//					
		for(Record r: records)
		{
			aggregator3.record(
					new Record().
					add(Variables.ViewNo, r.get(Variables.ViewNo)).
					add(Variables.Time, r.getValue()).add(Variables.RewNo, r.get(Variables.RewNo)));
		}
		//		
		//		records = aggregator2.averageOf(Variables.Time).per(Variables.ViewNo);
		//		
		//		for(Record r: records)
		//		{
		//			aggregator3.record(
		//				new Record().
		//				add(Variables.ViewNo, r.get(Variables.ViewNo)).
		//				add(Variables.Time, r.getValue()).add(Variables.Framework, "minicon"));
		//		}
		//		
		//	      records = aggregator3.averageOf(Variables.Time).per(Variables.ViewNo,Variables.Framework);

		//		Records records = aggregator.averageOf(Variables.Time).per(Variables.ViewNo,Variables.RewNo);


		//			 Records records = aggregator.filtered(Filters.ge(Variables.ViewNo, 50)).filtered(Filters.le(Variables.ViewNo, 93)).averageOf(Variables.Time).per(Variables.ViewNo);

		//ordered(Orders.asc(Variables.ViewNo))
		//.report(aggr, variables)
		//	     ;
		//	      report(records);
		//
		//	        System.out.println(records);
		//		Diagram diagram = DiagramFactory.newDiagram(records);
		//		//	        
		//		GnuPlotContext context = new GnuPlotContext();
		//		context.setLogscaleY();
		//		GnuPlotWriter writer = new GnuPlotWriter(new File("diagramsFolder"));
		//		try {
		//			writer.writeDiagram(diagram, "runDiagram_"+i,context);
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		//	        withTitle("GQR").
		//	        withRangeLabel("Query Time").
		//	        withVariableLabel(0, "ViewNo");

		//Diagram diagram = DiagramFactory.newDiagram(records).withVariableLabel(0,
		//	        //"XXX").withVariableLabel(1, "YYY");
		//	        final Chart chart = new ChartFactory(diagram).newLineChart();
		//	        final ChartPanel chartPanel = chart.newPanel();
		//	
		//	        try {
		//	        	chart.write(800, 600, "jpeg", new File(System.getProperty("user.dir")+"\\letSee.jpg"));
		//	        } catch (IOException e) {
		//	        	e.printStackTrace();
		//	        }

		//		try {
		//			db_Gqr.shutDown();
		//		} catch (SQLException e) {
		//			throw new RuntimeException(e);
		//		}
		//			try {
		//				db_minocon.shutDown();
		//			} catch (SQLException e) {
		//				throw new RuntimeException(e);
		//			}
		//			


	}

}

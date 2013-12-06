package gqr;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.isi.mediator.domain.DomainModel;
import edu.isi.mediator.domain.parser.DomainParser;
import edu.isi.mediator.gav.main.MediatorException;
import edu.isi.mediator.gav.util.MediatorUtil;

public class IndexViews {
	
	//map to index all PJs that are keys of the next map, any pj A which maps to a key pj B of the next map
	//will be a kept in this map, and its value will be a table containing B
	private Map <SourcePredicateJoin,List<SourcePredicateJoin>> indexSourcePJs;

	//map to ``globally'' hold all PJs, if repeated predicates exist in the same source, these are contained in the list for the spj (source pj)
	private Map<SourcePredicateJoin, List<SourcePredicateJoin>> sourcePJs;

	/**
	 * Parses and preprocesses the views for the input file, one at a line up to 'numberofsources'. It constructs the internal data structures (PJs) and the indices upon 
	 * which GQR will retrieve the relevant structures when used with a query. 
	 * @param file a File object for a text file containing one view query per line formated as V(X0,X1,X2) :- P1(X0,X1), P2(X1,X2)
	 * @param numberofsources specifies up to how many lines (i.e. views) from the file will be read. The parser will throw an exception if called to parse an empty line (EOF must be after the last view in the file)
	 */
	public IndexViews(File viewsFile,int numberofsources)
	{
		readViews(viewsFile,numberofsources-1);
	}
	
//	public void IndexViewsBirnFormat(String pathToFile,int numberofsources)
//	{
//		//maria's code
//		DomainParser sp = new DomainParser();
//		String domainStr;
//		DomainModel domainModel;
//		try {
//			domainStr = MediatorUtil.getFileAsString(pathToFile);
//			domainModel = sp.parseDomain(domainStr);
//		} catch (MediatorException e) {
//			throw new RuntimeException(e);
//		}
//		
//		readBirnViews(domainModel,numberofsources);
//	
//	}

//	private void readBirnViews(DomainModel domainModel, int numberofsources) {
//
//
//		GQRSourceParser sparser = new GQRSourceParser(domainModel,numberofsources);
//
//		sparser.parseDomainModelSources(numberofsources);
//		
//
//
//		//Getting the sourcepjs
//		//System.out.println(" getting the sourcepjs");
//		//sourcePJs.clear();
//		sourcePJs = sparser.getSourcePJs();
//
//		long st = System.currentTimeMillis();
//		assert(assertIDs(sourcePJs));
//
//		dontCountTime += ((long)(System.currentTimeMillis() - st));
//		//System.out.println(" getting the indexsourcepjs");
//		sparser.indexSourcePJs();
//		//indexSourcePJs.clear();
//		indexSourcePJs = sparser.getIndexSourcePJs();
//
//
//
//	}  
	
	/**
	 * Parses the views. Constructs sourcePjs map, whose entries maintain multiple instances of each PJ,
	 * for all the repeated times occurring in the sources.
	 * Also creates the indexSourcePJs having for all potential query PJs, all th distinct source PJs
	 * these query PJs could map onto (i.e., the values of the map are pjs which are keys in
	 * sourcePJs).
	 * @param file a File object for a text file containing one view query per line
	 * @param numberofsources specifies up to how many lines (i.e. views) from the file will be read 
	 */
	private void readViews(File file,int numberofsources) {
		GQRSourceParser sparser = new GQRSourceParser(file,numberofsources);
		sparser.parseSources();
		sourcePJs = sparser.getSourcePJs();
		assert(assertIDs(sourcePJs));
		sparser.indexSourcePJs();
		indexSourcePJs = sparser.getIndexSourcePJs();
	}
	
	private static boolean assertIDs(
			Map<SourcePredicateJoin, List<SourcePredicateJoin>> sourcePJs2) {
		for(List<SourcePredicateJoin> lsp: sourcePJs2.values())
			assert(GQR.assertRepeatedIds(lsp));

		return true;
	}
	
	protected Map<SourcePredicateJoin, List<SourcePredicateJoin>> getIndexSourcePJs() {
		return indexSourcePJs;
	}
	
	protected Map<SourcePredicateJoin, List<SourcePredicateJoin>> getSourcePJs() {
		return sourcePJs;
	}
	
}

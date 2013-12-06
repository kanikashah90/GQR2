package gqr;

import isi.mediator.SourceQuery;
import isi.mediator.Term;
import isi.mediator.VarTerm;

public class Util {
	
	public static SourceQuery castQueryAsISISourceQuery(DatalogQuery dq)
	{
		SourceQuery sq = new SourceQuery();

		for(Predicate gqr_p:dq.getPredicates())
		{
			isi.mediator.RelationPredicate isi_p = new isi.mediator.RelationPredicate(gqr_p.name);
			for(PredicateArgument p_arg:gqr_p.getArguments())
			{
				Term t = new VarTerm(p_arg.name);
				isi_p.addTerm(t);
			}
			sq.addPredicate(isi_p);
		}
		isi.mediator.RelationPredicate isi_head_p = new isi.mediator.RelationPredicate(dq.getName());
		
		for(PredicateArgument p_arg:dq.getHeadVariables())
		{
			Term t = new VarTerm(p_arg.name);
			isi_head_p.addTerm(t);
		}
		sq.addHead(isi_head_p);
		
		return sq;
	}

}

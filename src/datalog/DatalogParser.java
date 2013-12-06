// $ANTLR : "datalog.g" -> "DatalogParser.java"$

package datalog;

import gqr.DatalogQuery;
import gqr.NumericalConstant;
import gqr.Predicate;
import gqr.PredicateArgument;
import gqr.StringConstant;
import gqr.Variable;

import java.util.ArrayList;
import java.util.List;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;


public class DatalogParser extends antlr.LLkParser       implements DatalogParserTokenTypes
{

	private DatalogQuery query = new DatalogQuery();
	private List<PredicateArgument> tempPredElems = new ArrayList<PredicateArgument>();

	protected DatalogParser(TokenBuffer tokenBuf, int k) {
		super(tokenBuf,k);
		tokenNames = _tokenNames;
	}

	public DatalogParser(TokenBuffer tokenBuf) {
		this(tokenBuf,2);
	}

	protected DatalogParser(TokenStream lexer, int k) {
		super(lexer,k);
		tokenNames = _tokenNames;
	}

	public DatalogParser(TokenStream lexer) {
		this(lexer,2);
	}

	public DatalogParser(ParserSharedInputState state) {
		super(state,2);
		tokenNames = _tokenNames;
	}

	public final DatalogQuery  query() throws RecognitionException, TokenStreamException {
		DatalogQuery quer = null;


		head();
		match(COLONDASH);
		body();
		{
			switch ( LA(1)) {
			case PERIOD:
			{
				match(PERIOD);
				break;
			}
			case EOF:
			case NEWLINE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		{
			switch ( LA(1)) {
			case NEWLINE:
			{
				match(NEWLINE);
				break;
			}
			case EOF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}

		quer = query;

		return quer;
	}

	public final void head() throws RecognitionException, TokenStreamException {

		Token  n = null;

		n = LT(1);
		match(VARIABLE);
		match(LPAREN);
		head_variables();
		match(RPAREN);

		query.setName(n.getText().trim());

	}

	public final void body() throws RecognitionException, TokenStreamException {


		predicate();
		{
			_loop1498:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						predicate();
					}
					else {
						break _loop1498;
					}

				} while (true);
		}
	}

	public final void head_variables() throws RecognitionException, TokenStreamException {


		head_var();
		{
			_loop1494:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						head_var();
					}
					else {
						break _loop1494;
					}

				} while (true);
		}
	}

	public final void head_var() throws RecognitionException, TokenStreamException {

		Token  n = null;

		n = LT(1);
		match(VARIABLE);

		String name = n.getText().trim();
		query.addHeadVariable(new Variable(name));

	}

	public final void predicate() throws RecognitionException, TokenStreamException {


		{
			if ((LA(1)==VARIABLE) && (LA(2)==LPAREN)) {
				regular_pred();
			}
			else if ((LA(1)==VARIABLE||LA(1)==STRING_CONST||LA(1)==NUMERICAL_CONST) && (LA(2)==COMPARISON)) {
				interpreted_pred();
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}

		}
	}

	public final void regular_pred() throws RecognitionException, TokenStreamException {

		Token  n = null;

		n = LT(1);
		match(VARIABLE);
		match(LPAREN);
		vars_or_cons();
		match(RPAREN);

		String name = n.getText().trim();
		Predicate pred = new Predicate(name);
		pred.addAllElements(tempPredElems);
		tempPredElems.clear();
		query.addPredicate(pred);

	}

	public final void interpreted_pred() throws RecognitionException, TokenStreamException {


		{
			switch ( LA(1)) {
			case STRING_CONST:
			case NUMERICAL_CONST:
			{
				const_compar_var();
				break;
			}
			case VARIABLE:
			{
				var_compar_const();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
	}

	public final void vars_or_cons() throws RecognitionException, TokenStreamException {


		{
			switch ( LA(1)) {
			case VARIABLE:
			{
				variable();
				break;
			}
			case STRING_CONST:
			case NUMERICAL_CONST:
			{
				constant();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		{
			_loop1510:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						{
							switch ( LA(1)) {
							case VARIABLE:
							{
								variable();
								break;
							}
							case STRING_CONST:
							case NUMERICAL_CONST:
							{
								constant();
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							}
						}
					}
					else {
						break _loop1510;
					}

				} while (true);
		}
	}

	public final void const_compar_var() throws RecognitionException, TokenStreamException {

		//		Token  c = null;
		//		
		//		constant();
		//		c = LT(1);
		//		match(COMPARISON);
		//		variable();
		//		
		//		PredicateArgument left = tempPredElems.get(0);
		//		PredicateArgument right = tempPredElems.get(1);
		//		String comparator = c.getText();
		//		InterpretedPredicate pred = new InterpretedPredicate(left,right,comparator);
		//		tempPredElems.clear();
		//		query.addInterpretedPredicate(pred);

	}

	public final void var_compar_const() throws RecognitionException, TokenStreamException {

			Token  c = null;
			
			variable();
			c = LT(1);
			match(COMPARISON);
			//constant();
			switch ( LA(1)) {
				/*case NUMERICAL_CONST:
				{
					constant();
					break;
				}*/
				case VARIABLE:
				{
					variable();
					break;
				}
			}
			PredicateArgument left = tempPredElems.get(0);
			PredicateArgument right = tempPredElems.get(1);
			String comparator = c.getText();

			Predicate pred = new Predicate("P"+ "LT");
			if(comparator.equals("<"))
			{
				pred.addAllElements(tempPredElems);
			}
			else if(comparator.equals(">"))
			{
				PredicateArgument temp = left;
				tempPredElems.set(0, right);
				tempPredElems.set(1, temp);
				pred.addAllElements(tempPredElems);
				
			}
			tempPredElems.clear();
			pred.makeComparisonPredicate();
			query.addPredicate(pred);
			query.setHasComparisonPred();
			//InterpretedPredicate pred = new InterpretedPredicate(left,right,comparator);
			//tempPredElems.clear();
			//query.addInterpretedPredicate(pred);

	}

	public final void constant() throws RecognitionException, TokenStreamException {


		{
			switch ( LA(1)) {
			case STRING_CONST:
			{
				string_constant();
				break;
			}
			case NUMERICAL_CONST:
			{
				numerical_constant();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
	}

	public final void variable() throws RecognitionException, TokenStreamException {

		Token  n = null;

		n = LT(1);
		match(VARIABLE);

		String name = n.getText().trim();
		Variable newvar = new Variable(name);
		int ind = query.getHeadVariables().indexOf(newvar);
		if(ind == -1)
		{
			newvar.setIsExistential();
			query.addExistentialVariable(newvar);
		}else
		{
			newvar.setPositionInHead(ind);
		}
		tempPredElems.add(newvar);

	}

	public final void string_constant() throws RecognitionException, TokenStreamException {

		System.out.println("In datalogParser.java#l360 I found a string constant");
				Token  n = null;
				
				n = LT(1);
				match(STRING_CONST);
				
				String name = n.getText().trim();
				tempPredElems.add(new StringConstant(name));

	}

	public final void numerical_constant() throws RecognitionException, TokenStreamException {

		System.out.println("In datalogParser.java#l373 I found a numerical constant");
				Token  n = null;
				
				n = LT(1);
				match(NUMERICAL_CONST);
				
				String name = n.getText().trim();
				tempPredElems.add(new NumericalConstant(name)); 

	}


	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"COLONDASH",
		"PERIOD",
		"NEWLINE",
		"VARIABLE",
		"LPAREN",
		"RPAREN",
		"COMMA",
		"COMPARISON",
		"STRING_CONST",
		"NUMERICAL_CONST",
		"LETTER",
		"QUOTE",
		"LOWERCASE",
		"UPPERCASE",
		"DIGIT",
		"WS"
	};


}

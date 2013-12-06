header 
{ 
	  package parser;
	  import datalog.*;
  import java.util.ArrayList;
  import java.util.List;
}

class DatalogParser extends Parser;
options {defaultErrorHandler=false;k=2;}

{
	  private DatalogQuery query = new DatalogQuery();
	  private List<PredicateElement> tempPredElems = new ArrayList<PredicateElement>();
}

query returns [DatalogQuery quer = null] : head COLONDASH body (PERIOD)? (NEWLINE)? 
{
			  quer = query;
};

head : 	n:VARIABLE LPAREN head_variables RPAREN
{ 
  query.setName(n.getText().trim());
};

head_variables : head_var (COMMA head_var)*	;
	
head_var : n:VARIABLE
{
  String name = n.getText().trim();
  query.addHeadVariable(new Variable(name));
};

body : predicate (COMMA predicate)*;

predicate : (regular_pred | interpreted_pred);

regular_pred : n:VARIABLE LPAREN vars_or_cons RPAREN
{
  String name = n.getText().trim();
  Predicate pred = new Predicate(name);
  pred.addAllElements(tempPredElems);
  tempPredElems.clear();
  query.addPredicate(pred);
};

interpreted_pred : (const_compar_var | var_compar_const);

const_compar_var : constant c:COMPARISON variable
{
  PredicateElement left = tempPredElems.get(0);
  PredicateElement right = tempPredElems.get(1);
  String comparator = c.getText();
  InterpretedPredicate pred = new InterpretedPredicate(left,right,comparator);
  tempPredElems.clear();
  query.addInterpretedPredicate(pred);
};

var_compar_const : variable c:COMPARISON constant
{
  PredicateElement left = tempPredElems.get(0);
  PredicateElement right = tempPredElems.get(1);
  String comparator = c.getText();
  InterpretedPredicate pred = new InterpretedPredicate(left,right,comparator);
  tempPredElems.clear();
  query.addInterpretedPredicate(pred);
};


vars_or_cons : (variable | constant) (COMMA (variable | constant))*;

variable : n:VARIABLE
{
  String name = n.getText().trim();
  tempPredElems.add(new Variable(name));
};

constant : (string_constant | numerical_constant);

string_constant : n:STRING_CONST
{
  String name = n.getText().trim();
  tempPredElems.add(new StringConstant(name));
};

numerical_constant : n:NUMERICAL_CONST
{
  String name = n.getText().trim();
  tempPredElems.add(new NumericalConstant(name)); 
};

class DatalogScanner extends Lexer;
options {defaultErrorHandler=false;k=2;}

COLONDASH: ":-";
LPAREN: '(';
RPAREN: ')';
COMMA : ',';
PERIOD: '.';

NUMERICAL_CONST : (DIGIT)+;

VARIABLE:   ( LETTER | DIGIT)+ ;
LETTER: (LOWERCASE | UPPERCASE);

QUOTE : '"' | "'";    
STRING_CONST : QUOTE VARIABLE QUOTE;

COMPARISON : ('<' | '>' | "<=" | ">=") ;
    
LOWERCASE : 'a'..'z';
UPPERCASE : 'A'..'Z';
DIGIT : '0'..'9';

NEWLINE
    :   '\r' '\n'   // DOS
    |   '\n'        // UNIX
    ;
    
WS : (' ' | '\t' | '\n' | '\r')
{ 
	  _ttype = Token.SKIP; 
	};
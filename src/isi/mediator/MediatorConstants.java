// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

package isi.mediator;

/**
 * @author mariam
 *
 */
public class MediatorConstants {

	//Source & Domain Schema Parser
	public static final String SOURCE_SCHEMA = "SOURCE_SCHEMA";
	public static final String DOMAIN_SCHEMA = "DOMAIN_SCHEMA";
	public static final String GAV_RULES = "GAV_RULES";
	public static final String LAV_RULES = "LAV_RULES";
	public static final String UAC_RULES = "UAC_RULES";
	public static final String QUERIES = "QUERIES";
	public static final String FUNCTIONS = "FUNCTIONS";
	public static final String NAMESPACES = "NAMESPACES";
	public static final String RELATION_PRED = "RELATION_PRED";
	public static final String BUILTIN_PRED = "BUILTIN_PRED";
	public static final String FUNCTION_PRED = "FUNCTION_PRED";
	public static final String FUNCTION = "function";
	public static final String BOUND = "B";
	public static final String FREE = "F";
	
	//Source Schema
	public static final String OGSADQP = "ogsadqp";
	//replaced with space; not used at this time; if needed uncomment in SourceSchema and SourceAttribute
	public static final String ILLEGAR_CHARS = "___";


	//Supported BuiltInPredicates
	public static final String EQUALS = "=";
	public static final String NOT_EQUAL1 = "!=";
	public static final String NOT_EQUAL2 = "<>";
	public static final String LESS_THAN_EQ = "<=";
	public static final String LESS_THAN = "<";
	public static final String GREATER_THAN_EQ = ">=";
	public static final String GREATER_THAN = ">";
	public static final String LIKE = "LIKE";
	public static final String IN = "IN";
	public static final String NOT_IN = "NOT_IN";
	public static final String SET = "SET";
	public static final String IS_NULL = "IS";
	// (x isnot "NULL") => (x IS NOT NULL)
	public static final String ISNOT_NULL = "IS NOT";
	
	public static final String NULL_VALUE="NULL"; 
	
	//before, I was using hashCode(), but if I do this I can't reproduce the output queries for Junit tests
	/**
	 * used in SQL query for renaming of tables
	 */
	private static int table_id=0;

	/**
	 * used in SQL query for renaming of queries
	 */
	private static int query_id=0;
	/**
	 * used in SQL query for renaming of columns
	 */
	private static int column_id=0;

	public static int getTableId(){
		table_id++;
		return table_id;
	}
	public static void resetTableId(){
		table_id=0;
	}
	public static int getQueryId(){
		query_id++;
		return query_id;
	}
	public static void resetQueryId(){
		query_id=0;
	}
	public static int getColumnId(){
		column_id++;
		return column_id;
	}
	public static void resetColumnId(){
		column_id=0;
	}
}

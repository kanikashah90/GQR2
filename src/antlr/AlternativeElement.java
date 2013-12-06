package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: AlternativeElement.java,v 1.1 2005/03/20 09:16:52 irmscher Exp $
 */

abstract class AlternativeElement extends GrammarElement {
    AlternativeElement next;
    protected int autoGenType = AUTO_GEN_NONE;

    protected String enclosingRuleName;

    public AlternativeElement(Grammar g) {
        super(g);
    }

    public AlternativeElement(Grammar g, Token start) {
        super(g, start);
    }

    public AlternativeElement(Grammar g, Token start, int autoGenType_) {
        super(g, start);
        autoGenType = autoGenType_;
    }

    public int getAutoGenType() {
        return autoGenType;
    }

    public void setAutoGenType(int a) {
        autoGenType = a;
    }

    public String getLabel() {
        return null;
    }

    public void setLabel(String label) {
    }
}

package antlr.collections.impl;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: VectorEnumeration.java,v 1.1 2005/03/20 09:16:46 irmscher Exp $
 */

import java.util.Enumeration;
import java.util.NoSuchElementException;

// based on java.lang.Vector; returns any null indices between non-null ones.

class VectorEnumeration implements Enumeration {
    Vector vector;
    int i;


    VectorEnumeration(Vector v) {
        vector = v;
        i = 0;
    }

    public boolean hasMoreElements() {
        synchronized (vector) {
            return i <= vector.lastElement;
        }
    }

    public Object nextElement() {
        synchronized (vector) {
            if (i <= vector.lastElement) {
                return vector.data[i++];
            }
            throw new NoSuchElementException("VectorEnumerator");
        }
    }
}

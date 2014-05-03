/*
 * $RCSfile: IONode.java,v $
 * $Date: 2006/05/16 09:56:34 $ - $Revision: 1.4 $
 */

package giraffe.ui;

/**
 * A IONode has a value represented on a given number of bits
 */
public interface IONode
{
    /**
     * Bits count accessor
     * @return the number of bits used to represent the value
     */
    public int getBitsCount ( );
}

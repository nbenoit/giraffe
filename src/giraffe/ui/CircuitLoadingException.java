/*
 * $RCSfile: CircuitLoadingException.java,v $
 * $Date: 2006/05/03 16:51:15 $ - $Revision: 1.2 $
 */

package giraffe.ui;

/**
 * Exception for circuit loading errors
 */
public class CircuitLoadingException extends Exception
{
    private static final long serialVersionUID = 0L;

    /**
     * Default constructor
     * @param msg the error message
     */
    public CircuitLoadingException ( String msg )
    {
        super ( msg );
    }
}

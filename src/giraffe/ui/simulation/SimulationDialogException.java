/*
 * $RCSfile: SimulationDialogException.java,v $
 * $Date: 2006/05/10 17:01:18 $ - $Revision: 1.1 $
 */

package giraffe.ui.simulation;

/**
 * Exception for simulation loading errors
 */
public class SimulationDialogException extends Exception
{
    private static final long serialVersionUID = 0L;

    /**
     * Default constructor
     * @param msg the error message
     */
    public SimulationDialogException ( String msg )
    {
        super ( msg );
    }
}

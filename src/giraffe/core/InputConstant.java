/*
 * $RCSfile: InputConstant.java,v $
 * $Date: 2006/05/21 15:34:24 $ - $Revision: 1.2 $
 */

package giraffe.core;

/**
 * Class InputConstant : defines the object input (inject constant data in wires)
 */
public class InputConstant extends Input
{
    private int constant;

    /**
     * Default constructor
     * @param bitsCount the number of bits in the input
     * @param value the constant value
     */
    public InputConstant ( int bitsCount, int value, Circuit c )
    {  
        super ( bitsCount, c );
        this.constant = value;
        this.addEvent ( 0, this.constant );
    }

    /**
     * Reset the input
     */
    public void reset ( )
    {
        this.setValue ( this.constant );
    }

    /**
     * Clear the input events related to this input
     */
    public void clearEvents ( )
    {
        this.events.clear ( );
        this.addEvent ( 0, this.constant );
    }
}	

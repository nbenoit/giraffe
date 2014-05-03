/*
 * $RCSfile: Output.java,v $
 * $Date: 2006/05/21 15:58:58 $ - $Revision: 1.8 $
 */

package giraffe.core;

import java.util.Vector;
import java.util.Iterator;

/**
 * Class Output : defines the object output (receive data from wires)
 */
public class Output extends Actor
{
    private int value;
    private int bitsCount;
    private Wire[] input;
    private Vector<OutputListener> listeners;

    /**
     * Default constructor
     */
    public Output ( int bitsCount, Circuit c )
    {  
        super ( );
        this.setDelay ( 0 );
        this.value = 0;
        this.bitsCount = bitsCount;
        this.input = new Wire [ bitsCount ];

        for ( int i=0; i<this.bitsCount; ++i )
            {
                this.input[i] = new Wire ( c );
                c.addWire ( this.input[i] );
                this.input[i].addConnection ( this );
            }

        this.listeners = new Vector<OutputListener> ( );
    }

    /**
     * Get the value of the output
     * @return the value of the output
     */
    public int getValue ( )
    {
        return this.value;
    }

    /**
     * Reset the output
     */
    public void reset ( )
    {
        this.value = 0;

        for ( int i=0; i<this.bitsCount; ++i )
            this.input[i].setLogiclevel ( false );
    }

    /**
     * Get the wire with specified weight
     * @param bitN the number of the wanted bit
     */
    public Wire getWire ( int bitN )
    {
        return this.input[bitN];
    }

    /**
     * Read the value from the wires
     */
    public void activate ( )
    {
        int v = 0;

        for ( int i=0; i<this.bitsCount; ++i )
            {
                if ( this.input[i].getOutput() != null )
                    v += ( (this.input[i].getOutput()?1:0) << i );
            }

        this.value = v;

        for ( Iterator<OutputListener>iter=this.listeners.iterator(); iter.hasNext(); )
            iter.next().outputValueUpdated ( new OutputEvent ( Actor.gestionnaire.getTime(), this.value, this ) );
    }

    /**
     * Add a listener to this output
     * @param l the listener
     */
    public void addListener ( OutputListener l )
    {
        this.listeners.add ( l );
    }

    /**
     * Remove a listener from this output
     * @param l the listener
     */
    public void removeListener ( OutputListener l )
    {
        this.listeners.remove ( l );
    }
}	

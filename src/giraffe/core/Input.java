/*
 * $RCSfile: Input.java,v $
 * $Date: 2006/05/21 15:58:58 $ - $Revision: 1.7 $
 */

package giraffe.core;

import java.util.Vector;
import java.util.Iterator;

/**
 * Class Input : defines the object input (inject data in wires)
 */
public class Input extends Actor
{
    private int value;
    private int bitsCount;
    private Wire[] output;
    protected Vector<InputEvent> events;

    /**
     * Default constructor
     * @param bitsCount the number of bits in the input
     */
    public Input ( int bitsCount, Circuit c )
    {  
        super ( );
        this.bitsCount = bitsCount;
        this.output = new Wire [ bitsCount ];

        for ( int i=0; i<this.bitsCount; ++i )
            {
                this.output[i] = new Wire ( c );
                c.addWire ( this.output[i] );
                this.output[i].setInput ( this );
            }

        this.setValue ( 0 );
        this.events = new Vector<InputEvent> ( );
    }

    /**
     * Set the value of the input
     * @param value : the new new value
     */
    public void setValue ( int value )
    {
        this.value = value;
        this.activate ( );
    }

    /**
     * Reset the input
     */
    public void reset ( )
    {
        this.setValue ( 0 );
    }

    /**
     * Get the value of the input
     * @return the value of the input
     */
    public int getValue ( )
    {
        return value;
    }

    /**
     * Herited method from Actor : not used for wires
     */
    public void activate ( )
    {
        for ( int i=0; i<this.bitsCount; ++i )
            this.output[i].setSignal ( ( (this.value & mask(i)) != 0) ? true : false );
    }

    /**
     * Get the boolean value of the value, actually useless because Input is not a true actor.
     * @return the logic level of the input value.
     */
    public Boolean getOutput ( )
    {
        return (this.value != 0) ? true : false;
    }

    /**
     * Get the output on the specified wire
     * @param bitN the number of the wanted bit
     * @return the logic level of the specified wire
     */
    public Boolean getOutput ( int bitN )
    {
        return ((this.value & mask(bitN)) != 0) ? true : false;
    }

    /**
     * Create a binary mask from the index of the bit to set to 1
     * @param b the bit to set to 1
     */
    private int mask ( int b )
    {
        return ( 1 << b );
    }

    /**
     * Get the wire with specified weight
     * @param bitN the number of the wanted bit
     */
    public Wire getWire ( int bitN )
    {
        return this.output[bitN];
    }

    /**
     * Add an actor to the specified output wire
     * @param output : the actor to add
     * @param bitN the bit number of the input to link the output to
     */
    public void addConnection ( Actor output, int bitN )
    {
        this.output[bitN].addConnection ( output );
    }

    /**
     * Remove an actor from the specified output wire
     * @param output : the actor to remove
     * @param bitN the bit number of the input to unlink the output from
     */
    public void removeConnection ( Actor output, int bitN )
    {
        this.output[bitN].removeConnection ( output );
    }

    /**
     * Add a new input event
     * @param time the time the event occurs at
     * @param value the new value
     */
    public void addEvent ( int time, int value )
    {
        this.events.add ( new InputEvent(time,value,this) );
    }

    /**
     * Clear the input events related to this input
     */
    public void clearEvents ( )
    {
        this.events.clear ( );
    }

    /**
     * Schedule the input events
     */
    public void scheduleEvents ( )
    {
        for ( Iterator<InputEvent>iter=this.events.iterator(); iter.hasNext(); )
            Actor.gestionnaire.schedule ( iter.next() );
    }
}	

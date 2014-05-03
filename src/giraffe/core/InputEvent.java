/*
 * $RCSfile: InputEvent.java,v $
 * $Date: 2006/05/18 10:04:10 $ - $Revision: 1.2 $
 */

package giraffe.core;

/**
 * Class for input changes
 * A value change occur when the value of the input changes
 */
public class InputEvent implements Comparable<InputEvent>
{
    private int time;
    private int value;
    private Input owner;

    /**
     * Default constructor
     * @param time the time offset of the change in the chronogram
     * @param value the new value of the node
     * @param owner the input owning this event
     */
    public InputEvent ( int time, int value, Input owner )
    {
        this.time = time;
        this.value = value;
        this.owner = owner;
    }

    /**
     * Accessor for time
     * @return the time the event occurs at
     */
    public int getTime ( )
    {
        return this.time;
    }

    /**
     * Accessor for value
     * @return the new value
     */
    public int getValue ( )
    {
        return this.value;
    }

    /**
     * Accessor for owner
     * @return the owner of the event
     */
    public Input getOwner ( )
    {
        return this.owner;
    }

    /**
     * Compares the current InputEvent with the specified object for order
     * @return a negative int, zero, or a positive int depending on the priority of the objects
     */
    public int compareTo ( InputEvent ie )
    {
        if ( this.getTime() > ie.getTime() )
            return 1;
        else if ( this.getTime() == ie.getTime() )
            return 0;
        else
            return -1;            
    }

    /**
     * Activate the event
     */
    public void activate ( )
    {
        this.owner.setValue ( this.value );
    }
}

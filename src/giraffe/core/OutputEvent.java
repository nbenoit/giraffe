/*
 * $RCSfile: OutputEvent.java,v $
 * $Date: 2006/06/03 13:30:38 $ - $Revision: 1.2 $
 */

package giraffe.core;

/**
 * Class for output changes
 * This event occurs when the value of the output changes
 */
public class OutputEvent
{
    private int time;
    private int value;
    private Output owner;

    /**
     * Default constructor
     * @param time the time offset of the change in the chronogram
     * @param value the new value of the node
     * @param owner the input owning this event
     */
    public OutputEvent ( int time, int value, Output owner )
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
    public Output getOwner ( )
    {
        return this.owner;
    }
}

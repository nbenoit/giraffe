/*
 * $RCSfile: KeyPriorityQueue.java,v $
 * $Date: 2006/05/18 09:45:52 $ - $Revision: 1.3 $
 */

package giraffe.core;

import java.util.PriorityQueue;

/**
 * Class KeyPriorityQueue : defines a queue containing events
 * The queue is ordered by priority
 */
public class KeyPriorityQueue<T extends Comparable> extends PriorityQueue<T>
{
    private static final long serialVersionUID = 0L;
	
    /**
     * Default constructor
     */
    public KeyPriorityQueue ( )
    {
        super ( );
    }
	
    /**
     * Adds an event to the queue
     * @param eventToAdd : the event to add to the queue
     */
    public void addEvent ( T eventToAdd )
    {
        this.add ( eventToAdd );
    }
	
    /**
     * Removes an event from the queue
     * @param eventToRemove : the event to remove from the queue
     */
    public void removeEvent ( T eventToRemove)
    {
        this.remove ( eventToRemove );
    }
}

/*
 * $RCSfile: ActorPriorityPair.java,v $
 * $Date: 2006/05/21 19:32:15 $ - $Revision: 1.3 $
 */

package giraffe.core;

/**
 * Class ActorPriorityPair : defines a pair containing an Actor and an integer
 */
public class ActorPriorityPair implements Comparable < ActorPriorityPair >
{
    private Actor event;
    private int priority;
    private long id;

    /**
     * Default constructor
     * @param event : the actor of the pair
     * @param priority : the priority of the pair
     * @param ID : the higher the ID is, the higher the event will prevail on other events with the same priority
     */
    public ActorPriorityPair ( Actor event, int priority, long id )
    {
        this.event = event;
        this.priority = priority;
        this.id = id;
    }
	
    /**
     * Get the actor of the current pair
     * @return the actor of the pair
     */
    public Actor getEvent ( )
    {
        return this.event;
    }
	
    /**
     * Get the priority of the current pair
     * @return the priority of the pair
     */
    public int getPriority ( )
    {
        return this.priority;
    }

    /**
     * Get the ID of the current pair
     * @return the ID of the pair
     */
    public long getID ( )
    {
        return this.id;
    }
	
    /**
     * Compares the current ActorPriorityPair with the specified object for order
     * @return a negative int, zero, or a positive int depending on the priority of the objects
     */
    public int compareTo ( ActorPriorityPair pair )
    {
        if ( this.getPriority() > pair.getPriority() )
            return 1;
        else if ( this.getPriority() < pair.getPriority() )
            return -1;
        else
            {
                if ( this.getID() > pair.getID() )
                    return -1;
                else
                    return 1;
            }
    }
}

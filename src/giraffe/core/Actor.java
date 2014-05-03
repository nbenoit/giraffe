/*
 * $RCSfile: Actor.java,v $
 * $Date: 2006/05/21 15:34:24 $ - $Revision: 1.4 $
 */

package giraffe.core;

/**
 * Class Actor : defines the generic object Actor
 */
public abstract class Actor 
{	
    /* Scheduler */
    public static Scheduler gestionnaire = null;

    private int delay;
    private Boolean outputlevel=null;

    /**
     * Default constructor 
     */
    public Actor ( )
    {
        if ( gestionnaire == null )
            gestionnaire = new Scheduler ( );
    }

    /**
     * Call the method schedule () to build an object to add in the queue of the scheduler
     */
    public void trigger ( )
    {
        gestionnaire.schedule ( this, delay );
    }
	
    /**
     * Get the delay associated to the actor
     * @return an integer representing the delay associated to the actor
     */
    public int getDelay ( )
    {
        return this.delay;
    }
	
    /**
     * Set the delay associated to the actor
     * @param value : the delay to set
     */
    public void setDelay ( int value )
    {
        this.delay = value;
    }
	
    /**
     * Get the output of the actor
     * @return the logic level of the actor output
     */
    public Boolean getOutput ( )
    {
        return this.outputlevel;
    }

    /**
     * Activates the Actor
     */
    public abstract void activate ( );
}

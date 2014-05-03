/*
 * $RCSfile: Scheduler.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.10 $
 */

package giraffe.core;

/**
 * Class Scheduler : defines the simulator tool of logic circuits 
 */
public class Scheduler
{
    private KeyPriorityQueue<ActorPriorityPair> queue;
    private KeyPriorityQueue<InputEvent> queue_inputs;
    private int time = 0;
    private long id;
    private int max_time = 201;

    private SimulationListener listener;
	
    /**
     * Default constructor
     */
    public Scheduler ( )
    {
        this.queue = new KeyPriorityQueue<ActorPriorityPair> ( );
        this.queue_inputs = new KeyPriorityQueue<InputEvent> ( );
        this.listener = null;
        this.id = 0;
    }
	
    /**
     * Schedules an event and adds it to the queue
     * @param client : the Actor to schedule
     * @param delay : the delay time associated to the Actor ( i.e : the propagation of a gate for example )
     */
    public void schedule ( Actor client, int delay )
    {
        ActorPriorityPair pair = new ActorPriorityPair ( client, time+delay, id );
        ++id;
        this.queue.addEvent ( pair );
    }

    /**
     * Schedules an input event
     * @param event : the event to schedule
     */
    public void schedule ( InputEvent event )
    {
        this.queue_inputs.add ( event );
    }

    /**
     * Set the simulation max time
     * @param maw_time the new max time
     */
    public void setMaxTime ( int max_time )
    {
        this.max_time = max_time;
    }
	
    /**
     * Executes the scheduler by an activation of each element it contains
     */
    public void run ( )
    {
        ActorPriorityPair current;
        final int timeMax = max_time;
        this.time = 0;

        while ( this.time < timeMax )
            {
                this.applyInputEvents ( );
                current = this.queue.poll ( );

                if ( current == null )
                    break;

                this.time = current.getPriority ( );
                current.getEvent().activate ( );

                if ( listener != null )
                    listener.timeUpdated ( this.time );
            }
		
        this.queue.clear ( );
        this.time = 0;
    }

    /**
     * Apply the input events
     */
    public void applyInputEvents ( )
    {
        int next_actor_time;
        ActorPriorityPair next;
        InputEvent ie;

        while ( queue_inputs.size() != 0 )
            {
                next = this.queue.peek ( );

                if ( next == null )
                    next_actor_time = Integer.MAX_VALUE;
                else
                    next_actor_time = next.getPriority ( );

                ie = this.queue_inputs.peek ( );

                if ( ie.getTime() <= next_actor_time )
                    {
                        this.queue_inputs.poll ( );
                        this.time = ie.getTime ( );
                        ie.activate ( );
                    }
                else
                    break;
            }
    }

    /**
     * Clear the scheduler
     */
    public void clear ( )
    {
        this.queue.clear ( );
        this.queue_inputs.clear ( );
        this.id = 0;
    }

    /**
     * Get the current time
     * @return the current time
     */
    public int getTime ( )
    {
        return this.time;
    }

    /**
     * Set the listener of this scheduler
     * @param l the listener
     */
    public void setListener ( SimulationListener l )
    {
        this.listener = l;
    }
}

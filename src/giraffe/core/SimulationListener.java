/*
 * $RCSfile: SimulationListener.java,v $
 * $Date: 2006/05/20 10:42:59 $ - $Revision: 1.1 $
 */

package giraffe.core;

/**
 * Class SimulationListener : must be implemented by object that want to be told about the simulation when it's running
 */
public interface SimulationListener
{
    /**
     * timeUpdated : called when the time has changed
     * @param time the new time
     */
    public void timeUpdated ( int time );
}
   

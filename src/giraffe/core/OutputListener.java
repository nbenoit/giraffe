/*
 * $RCSfile: OutputListener.java,v $
 * $Date: 2006/06/03 13:30:38 $ - $Revision: 1.2 $
 */

package giraffe.core;

/**
 * Interface OutputListener : must be implemented by object that want to be told when an output changes
 */
public interface OutputListener
{
    /**
     * outputValueUpdated : called when the value of the output has been updated
     * @param event the output event
     */
    public void outputValueUpdated ( OutputEvent event );
}

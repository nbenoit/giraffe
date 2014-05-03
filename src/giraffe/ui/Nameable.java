/*
 * $RCSfile: Nameable.java,v $
 * $Date: 2006/05/03 16:51:15 $ - $Revision: 1.3 $
 */

package giraffe.ui;

/**
 * A nameable node has a specific name
 */
public interface Nameable extends Dialogable
{
    /**
     * Node name accessor
     * @return a string containing the node name
     */
    public String getNodeName ( );

    /**
     * Node name modificator
     * @param name the new node name
     */
    public void setNodeName ( String name );
}

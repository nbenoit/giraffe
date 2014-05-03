/*
 * $RCSfile: CompositeNode.java,v $
 * $Date: 2006/05/03 16:51:15 $ - $Revision: 1.4 $
 */

package giraffe.ui;

import java.awt.Graphics;
import java.io.File;

/**
 * A CompositeNode is a node that embed a circuit
 */
public interface CompositeNode
{
    /**
     * Load the circuit of this node
     * @param file the file that contains the XML description of the circuit
     * @param g the graphics that will paint the node
     * @throws CircuitLoadingException if the internal circuit can not be loaded
     */
    public void load ( File file, Graphics g ) throws CircuitLoadingException;
    
    /**
     * Reload the circuit of this node
     * @param g the graphics that will paint the node
     * @pre hasBeenInitialized()
     * @pre g != null
     * @throws CircuitLoadingException if the internal circuit can not be loaded
     */
    public void reload ( Graphics g ) throws CircuitLoadingException;

    /**
     * Initialization accessor
     * @return true if the node has been initialized, false either
     */
    public boolean hasBeenInitialized ( );

    /**
     * File name accessor
     * @return the file name that contains the XML description of the circuit
     */
    public String getCircuitFileName ( );

    /**
     * Path name accessor
     * @return the path to the XML description of the circuit
     */
    public String getCircuitPathName ( );

    /**
     * Fulle name accessor
     * @return the full path to the XML description of the circuit
     */
    public String getCircuitFullPathName ( );
}

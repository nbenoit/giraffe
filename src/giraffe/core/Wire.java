/*
 * $RCSfile: Wire.java,v $
 * $Date: 2006/05/21 15:58:58 $ - $Revision: 1.6 $
 */

package giraffe.core;

import java.util.Vector;

/**
 * Class Wire : defines the object wire 
 */
public class Wire extends Actor
{
    private Actor input;
    private Vector < Actor > output;
    private Boolean wireValue;
	
    /**
     * Default constructor
     */
    public Wire ( Circuit c )
    {  
        super ( );
        output = new Vector <Actor> ( ) ;
        c.addWire ( this );
    }
	
    /**
     * Get the value of the wire
     * @return the logic level of the wire
     */
    public Boolean getOutput ( )
    {
        return wireValue;
    }
	
    /**
     * Set the logic value of the wire and applies the change to the output
     * @param logicLevel : the logic level to set 
     */
    public void setSignal ( Boolean logicLevel )
    {
        if ( logicLevel == null )
            this.setLogiclevel ( null );
        else if ( wireValue != logicLevel )
            {
                this.setLogiclevel ( logicLevel );
                this.activate ( );
            }
    }
	
    /**
     * Get the actor(s) connected to the wire
     * @return a Vector containing the references of the connected actors
     */
    public Vector < Actor > getConnections ( )
    {
        return this.output;
    }
	
    /**
     * Set the input of the wire
     * @param inputActor : the actor to connect as the input of the wire
     */
    public void setInput ( Actor inputActor )
    {
        this.input = inputActor;
        this.wireValue = inputActor.getOutput();
    }
	
    /**
     * Get the input of the wire
     * @return the actor connected as the input of the wire
     */
    public Actor getInput ( )
    {
        return this.input;
    }
	
    /**
     * Set the logic value of the wire
     * @param value : the logic level to set
     */
    public void setLogiclevel ( Boolean value )
    {
        this.wireValue = value;
    }
	
    /**
     * Herited method from Actor : not used for wires
     */
    public void activate ( )
    {
        if ( this.input instanceof Wire )
            this.setLogiclevel ( this.input.getOutput() );

        for( int i=0 ; i<this.output.size(); ++i )
            this.output.elementAt(i).trigger();
    }
	
    /**
     * Add an actor in the output Vector of the wire
     * @param output : the actor to add in the Vector
     */
    public void addConnection ( Actor output )
    {
        this.output.addElement ( output );
    }
	
    /**
     * Remove an actor from the output Vector of the wire
     * @param output : the actor to remove from the Vector
     */
    public void removeConnection ( Actor output )
    {
        this.output.remove ( output );
    }
}	

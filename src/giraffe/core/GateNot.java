/*
 * $RCSfile: GateNot.java,v $
 * $Date: 2006/05/21 15:34:24 $ - $Revision: 1.4 $
 */

package giraffe.core;

/**
 * Class Not : defines the NOT Gate
 */
public class GateNot extends Actor
{
    private Wire input;
    private Wire output;
	
    /**
     * Constructor parameterized with input and output wires
     * @param in : the wire associated to the input of the NOT Gate
     * @param out : the wire associated to the output of th NOT Gate
     */
    public GateNot ( Wire in, Wire out )
    {
        super ( );
        this.setDelay ( 1 );
        this.input = in;
        this.output = out;
		
        this.input.addConnection ( this );
        this.output.setInput( this );
		
    }

    /**
     * Set the input of the NOT Gate
     * @param value : the logic level to set
     */
    public void setInput ( boolean value )
    {
        this.input.setSignal ( value );
    }

    /**
     * Get the input of the NOT Gate
     * @return the logic level of the input
     */
    public Boolean getInput ( )
    {
        return this.input.getOutput ( );
    }

    /**
     * Get the output of the NOT Gate
     * @return the logic level of the output
     */
    public Boolean getOutput ( )
    {
        return this.output.getOutput ( );
    }

    /**
     * Activates the NOT Gate
     */
    public void activate ( )
    {
        if ( this.input.getOutput() != null )
            this.output.setSignal( !this.input.getOutput() );
        else
            this.output.setSignal( true );
    }
}

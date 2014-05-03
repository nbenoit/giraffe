/*
 * $RCSfile: GateNor.java,v $
 * $Date: 2006/05/21 15:34:24 $ - $Revision: 1.5 $
 */

package giraffe.core;

/**
 * Class GateNor : defines the NOR Gate
 */
public class GateNor extends GateTwoInputs
{
    /**
     * Constructor parameterized with input and output wires linked to the gate
     * @param i1 : the wire associated to the first input (input A in GateTwoInputs) of the gate
     * @param i2 : the wire associated to the second input (input B in GateTwoInputs) of the gate
     * @param o : the wire associated to the output (output in GateTwoInputs) of the gate
     */
    public GateNor ( Wire i1, Wire i2, Wire o )
    {
        super ( i1, i2, o );
        this.setDelay ( 1 );
    }
	
    /**
     * Activates the NOR Gate 
     */
    public void activate ( )
    {
        if ( ( this.inputA.getOutput() != null ) && ( this.inputB.getOutput() != null ) )
            this.output.setSignal( ! ( this.inputA.getOutput() || this.inputB.getOutput() ) );
        else if ( ( this.inputA.getOutput() == null ) && ( this.inputB.getOutput() != null ) )
            this.output.setSignal( ! this.inputB.getOutput() );
        else if ( ( this.inputA.getOutput() != null ) && ( this.inputB.getOutput() == null ) )
            this.output.setSignal( ! this.inputA.getOutput() );
        else
            this.output.setSignal( true );
    }
}

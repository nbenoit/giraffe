/*
 * $RCSfile: GateXor.java,v $
 * $Date: 2006/05/21 15:34:24 $ - $Revision: 1.4 $
 */

package giraffe.core;

/**
 * Class GateXor : defines the XOR Gate
 */
public class GateXor extends GateTwoInputs
{
    /**
     * Constructor parameterized with input and output wires linked to the gate
     * @param i1 : the wire associated to the first input (input A in GateTwoInputs) of the gate
     * @param i2 : the wire associated to the second input (input B in GateTwoInputs) of the gate
     * @param o : the wire associated to the output (output in GateTwoInputs) of the gate
     */
    public GateXor ( Wire i1, Wire i2, Wire o )
    {
        super ( i1, i2, o );
        this.setDelay ( 1 );
    }
	
    /**
     * Activates the XOR Gate 
     */
    public void activate ( )
    {
        if ( ( this.inputA.getOutput() != null ) && ( this.inputB.getOutput() != null ) )
            this.output.setSignal( this.inputA.getOutput() ^ this.inputB.getOutput() );
        else if ( ( this.inputA.getOutput() == null ) && ( this.inputB.getOutput() != null ) )
            this.output.setSignal( this.inputB.getOutput() );
        else if ( ( this.inputA.getOutput() != null ) && ( this.inputB.getOutput() == null ) )
            this.output.setSignal( this.inputA.getOutput() );
        else
            this.output.setSignal( false );
    }
}

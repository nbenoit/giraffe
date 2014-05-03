/*
 * $RCSfile: GateTwoInputs.java,v $
 * $Date: 2006/05/21 15:34:24 $ - $Revision: 1.4 $
 */

package giraffe.core;

/**
 * Class GateTwoInputs : defines the two-input gates
 */
public abstract class GateTwoInputs extends Actor
{
    protected Wire inputA;
    protected Wire inputB;
    protected Wire output;

    /**
     * Constructor parameterized with input and output wires linked to the gate
     * @param input1 : the wire associated to the A-input of the gate
     * @param input2 : the wire associated to the B-inputof the gate
     * @param output : the wire associated to the output of the gate
     */
    public GateTwoInputs ( Wire input1, Wire input2, Wire output )
    {
        super ( );
        this.inputA = input1;
        this.inputA.addConnection ( this );
        
        this.inputB = input2;
       	this.inputB.addConnection ( this );
        
        this.output = output;
       	this.output.setInput ( this );
    }

    /**
     * Get the output of the gate
     * @return the logic level of the gate output
     */
    public Boolean getOutput( )
    {
        return this.output.getOutput ( );
    }

    /**
     * Get the A-input of the gate
     * @return the logic level of the A-input
     */
    public Boolean getInputA ( )
    {
        return this.inputA.getOutput ( );
    }

    /**
     * Get the B-input of the gate
     * @return the logic level of the B-input
     */
    public Boolean getInputB ( )
    {
        return this.inputB.getOutput ( );
    }

    /**
     * Set the A-input of the gate
     * @param value : the logic level to set on the A-input
     */
    public void setInputA ( Boolean value )
    {
        this.inputA.setSignal ( value );
    }

    /**
     * Set the B-input of the gate
     * @param value : the logic level to set on the B-input
     */
    public void setInputB ( Boolean value )
    {
        this.inputB.setSignal ( value );
    }

    /**
     * Set the A and B inputs of the gate with the same logic level
     * @param value : the logic level to set on the inputs
     */
    public void setInputs ( Boolean value )
    {
        this.inputA.setSignal ( value );
        this.inputB.setSignal ( value );
    }

    /**
     * Activates the two-input gate
     */
    public abstract void activate ( );	
}

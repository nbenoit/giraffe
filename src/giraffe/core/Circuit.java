/*
 * $RCSfile: Circuit.java,v $
 * $Date: 2006/05/21 19:32:15 $ - $Revision: 1.5 $
 */

package giraffe.core;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Class Circuit : defines the object circuit
 */
public class Circuit
{
    private HashSet<Input> inputs;
    private HashSet<Output> outputs;
    private HashSet<Wire> wires;

    /**
     * Default constructor
     */
    public Circuit ( )
    {
        this.inputs = new HashSet<Input> ( );
        this.outputs = new HashSet<Output> ( );
        this.wires = new HashSet<Wire> ( );
    }

    /**
     * Get an iterator over the inputs
     * @return an iterator over the inputs
     */
    public Iterator<Input> getInputs ( )
    {
        return this.inputs.iterator ( );
    }

    /**
     * Get an iterator over the inputs
     * @return an iterator over the inputs
     */
    public Iterator<Output> getOutputs ( )
    {
        return this.outputs.iterator ( );
    }

    /**
     * Get an iterator over the wires
     * @return an iterator over the wires
     */
    public Iterator<Wire> getWires ( )
    {
        return this.wires.iterator ( );
    }

    /**
     * Add an input to the circuit
     * @param in the input to add
     */
    public void addInput ( Input in )
    {
        this.inputs.add ( in );
    }

    /**
     * Add an output to the circuit
     * @param out the output to add
     */
    public void addOutput ( Output out )
    {
        this.outputs.add ( out );
    }

    /**
     * Add a wire to the circuit
     * @param w the wire to add
     */
    public void addWire ( Wire w )
    {
        this.wires.add ( w );
    }

    /**
     * Initializes the simulation
     * @param max_time the max length of the simulation
     * @param l the listener of the simulation, can be null
     */
    public void initSimulation ( int max_time, SimulationListener l )
    {
        /* run a first round with default values on inputs */
        Actor.gestionnaire.clear ( );
        Actor.gestionnaire.setListener ( null );
        Actor.gestionnaire.setMaxTime ( max_time );

        for ( Iterator<Wire> iter=this.wires.iterator(); iter.hasNext(); )
            iter.next().setLogiclevel ( null );

        for ( Iterator<Input> iter=this.inputs.iterator(); iter.hasNext(); )
            {
                Input in = iter.next ( );
                in.reset ( );
            }

        for ( Iterator<Output> iter=this.outputs.iterator(); iter.hasNext(); )
            iter.next().reset ( );

        Actor.gestionnaire.run ( );

        /* prepare for simulation (events scheduling) */
        Actor.gestionnaire.clear ( );

        for ( Iterator<Input> iter=this.inputs.iterator(); iter.hasNext(); )
            iter.next().scheduleEvents ( );

        Actor.gestionnaire.setListener ( l );
    }

    /**
     * Run the simulation
     */
    public void runSimulation ( )
    {
        Actor.gestionnaire.run ( );
    }
}

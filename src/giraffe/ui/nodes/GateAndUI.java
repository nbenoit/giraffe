/*
 * $RCSfile: GateAndUI.java,v $
 * $Date: 2006/05/17 13:49:32 $ - $Revision: 1.5 $
 */

package giraffe.ui.nodes;

import giraffe.core.Wire;
import giraffe.core.GateAnd;
import giraffe.core.GateTwoInputs;

public class GateAndUI extends Gate2InputsUI
{
    public static final String NAME = "And";

    public static final String IMG_NORMAL_NAME = "gate_and.png";
    public static final String IMG_SELECTED_NAME = "gate_and_selected.png";

    public GateAndUI ( )
    {
        super ( GateAndUI.IMG_NORMAL_NAME, GateAndUI.IMG_SELECTED_NAME );
    }

    public final String getName ( )
    {
        return NAME;
    }

    /**
     * Create a new simulation gate for the current gate
     * @param inA the input 1
     * @param inB the input 2
     * @param out the output
     * @return a valid simulation gate
     */
    public GateTwoInputs newSimulationGate ( Wire inA, Wire inB, Wire out )
    {
        return new GateAnd ( inA, inB, out );
    }
}

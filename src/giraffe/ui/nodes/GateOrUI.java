/*
 * $RCSfile: GateOrUI.java,v $
 * $Date: 2006/05/17 13:49:32 $ - $Revision: 1.5 $
 */

package giraffe.ui.nodes;

import giraffe.core.Wire;
import giraffe.core.GateOr;
import giraffe.core.GateTwoInputs;

public class GateOrUI extends Gate2InputsUI
{
    public static final String NAME = "Or";

    public static final String IMG_NORMAL_NAME = "gate_or.png";
    public static final String IMG_SELECTED_NAME = "gate_or_selected.png";

    public GateOrUI ( )
    {
        super ( GateOrUI.IMG_NORMAL_NAME, GateOrUI.IMG_SELECTED_NAME );
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
        return new GateOr ( inA, inB, out );
    }
}

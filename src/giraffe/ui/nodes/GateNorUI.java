/*
 * $RCSfile: GateNorUI.java,v $
 * $Date: 2006/05/17 13:49:32 $ - $Revision: 1.5 $
 */

package giraffe.ui.nodes;

import giraffe.core.Wire;
import giraffe.core.GateNor;
import giraffe.core.GateTwoInputs;

public class GateNorUI extends Gate2InputsUI
{
    public static final String NAME = "Nor";

    public static final String IMG_NORMAL_NAME = "gate_nor.png";
    public static final String IMG_SELECTED_NAME = "gate_nor_selected.png";

    public GateNorUI ( )
    {
        super ( GateNorUI.IMG_NORMAL_NAME, GateNorUI.IMG_SELECTED_NAME );
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
        return new GateNor ( inA, inB, out );
    }
}

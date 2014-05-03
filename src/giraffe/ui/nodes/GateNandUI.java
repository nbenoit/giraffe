/*
 * $RCSfile: GateNandUI.java,v $
 * $Date: 2006/05/17 13:49:32 $ - $Revision: 1.5 $
 */

package giraffe.ui.nodes;

import giraffe.core.Wire;
import giraffe.core.GateNand;
import giraffe.core.GateTwoInputs;

public class GateNandUI extends Gate2InputsUI
{
    public static final String NAME = "Nand";

    public static final String IMG_NORMAL_NAME = "gate_nand.png";
    public static final String IMG_SELECTED_NAME = "gate_nand_selected.png";

    public GateNandUI ( )
    {
        super ( GateNandUI.IMG_NORMAL_NAME, GateNandUI.IMG_SELECTED_NAME );
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
        return new GateNand ( inA, inB, out );
    }
}

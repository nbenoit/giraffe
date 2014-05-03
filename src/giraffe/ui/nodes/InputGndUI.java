/*
 * $RCSfile: InputGndUI.java,v $
 * $Date: 2006/05/21 15:34:24 $ - $Revision: 1.9 $
 */

package giraffe.ui.nodes;

import giraffe.ui.Node;
import giraffe.ui.Anchor;
import giraffe.ui.CircuitUI;

import giraffe.core.Actor;
import giraffe.core.InputConstant;

public class InputGndUI extends InputUI
{
    public static final String NAME = "Ground";

    public static final String IMG_NORMAL_NAME = "input_gnd.png";
    public static final String IMG_SELECTED_NAME = "input_gnd_selected.png";

    public InputGndUI ( )
    {
        super ( InputGndUI.IMG_NORMAL_NAME, InputGndUI.IMG_SELECTED_NAME );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (img.getHeight(null)/2)-(Anchor.HEIGHT/2)+2, this, 0 ) );
    }

    public final String getName ( )
    {
        return NAME;
    }

    public int getBitsCount ( )
    {
        return 1;
    }

    public int getCategoryID ( )
    {
        return Node.VALUE;
    }

    /**
     * Make the node equivalent for simulation
     * @param c the CircuitUI owning the component
     */
    public Actor makeSimulationNode ( CircuitUI c )
    {
        InputConstant in = new InputConstant ( this.getBitsCount(), 0, c.getCircuit() );

        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            {
                Anchor a = iterAnchors.next ( );
                a.linkForSimulation ( in.getWire(a.getID()), c );
            }

        return in;
    }
}

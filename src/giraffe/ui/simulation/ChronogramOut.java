/*
 * $RCSfile: ChronogramOut.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.7 $
 */

package giraffe.ui.simulation;

import java.awt.*;

import giraffe.ui.Node;

import giraffe.core.OutputEvent;
import giraffe.core.OutputListener;


/**
 * Chronogram of an outputable node
 */
public class ChronogramOut extends Chronogram implements OutputListener
{
    private static final long serialVersionUID = 0L;

    private static final Color color_low = new Color ( 246, 64, 98 );
    private static final Color color_high = new Color ( 138, 233, 81 );

    /**
     * Constructor for outputable's chronogram
     * @param owner the simulation dialog that contains the chronogram
     * @param input the output node we will deal with
     */
    public ChronogramOut ( SimulationDialog owner, Node output )
    {
        super ( owner, output );
        this.type = Chronogram.OUT;
        this.init ( );
        this.changes.add ( new ChronogramChange ( 0, 0 ) );
    }

    /**
     * Get color for low values
     * @return the color for low values
     */
    public Color getColorLow ( )
    {
        return color_low;
    }

    /**
     * Get color for high values
     * @return the color for high values
     */
    public Color getColorHigh ( )
    {
        return color_high;
    }

    /**
     * outputValueUpdated : called when the value of the output has been updated
     * @param event the output event
     */
    public void outputValueUpdated ( OutputEvent event )
    {
        this.addChange ( event.getTime(), event.getValue() );
    }
}

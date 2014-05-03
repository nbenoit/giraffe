/*
 * $RCSfile: ChronogramIn.java,v $
 * $Date: 2006/05/22 13:29:00 $ - $Revision: 1.7 $
 */

package giraffe.ui.simulation;

import java.awt.*;
import java.util.Iterator;

import giraffe.ui.Node;

import giraffe.core.Input;

/**
 * Chronogram of an inputable node
 */
public class ChronogramIn extends Chronogram
{
    private static final long serialVersionUID = 0L;

    protected static final Color color_low = new Color ( 55, 174, 242 );
    protected static final Color color_high = new Color ( 237, 197, 108 );

    /**
     * Constructor for inputable's chronogram
     * @param owner the simulation dialog that contains the chronogram
     * @param input the input node we will deal with
     */
    public ChronogramIn ( SimulationDialog owner, Node input )
    {
        super ( owner, input );
        this.type = Chronogram.IN;
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
     * Copy the changes record into an input for simulation
     */
    public void copyChangesRecordTo ( Input in )
    {
        in.clearEvents ( );

        for ( Iterator<ChronogramChange> iter=this.changes.iterator(); iter.hasNext(); )
            {
                ChronogramChange cc = iter.next ( );
                in.addEvent ( cc.time, cc.value );
            }
    }
}

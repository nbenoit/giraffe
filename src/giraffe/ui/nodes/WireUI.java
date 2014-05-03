/*
 * $RCSfile: WireUI.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.3 $
 */

package giraffe.ui.nodes;

import java.awt.*;

import giraffe.ui.*;

import giraffe.core.*;

public class WireUI extends Node
{
    public static final String CATEGORY = "Other";
    public static final String NAME = "Wire";

    private Color color;

    public static final Color colorNormal = Color.black;
    public static final Color colorSelected = new Color ( 239, 229, 0 );

    public static final int WIRE_LENGTH = 8;

    public WireUI ( )
    {
        super ( );

        this.color = colorNormal;

        this.bounds.setSize ( WIRE_LENGTH+3+(2*Anchor.WIDTH), Anchor.HEIGHT+2 );
        this.anchors.add ( new Anchor ( Anchor.IN, 2, 1+(bounds.height/2)-(Anchor.HEIGHT/2), this, 0 ) );
        this.anchors.add ( new Anchor ( Anchor.OUT, 1+bounds.width-Anchor.WIDTH, 1+(bounds.height/2)-(Anchor.HEIGHT/2), this, 1 ) );
    }

    public void paint ( Graphics g )
    {
        g.setColor ( color );
        g.drawLine ( bounds.x+1+Anchor.WIDTH, 1+bounds.y+(bounds.height/2), 1+bounds.x+bounds.width-Anchor.WIDTH-1, 1+bounds.y+(bounds.height/2) );

        super.paint ( g );
    }

    public void select ( )
    {
        super.select ( );
        this.color = colorSelected;
    }

    public void unselect ( )
    {
        super.unselect ( );
        this.color = colorNormal;
    }

    public final String getCategory ( )
    {
        return CATEGORY;
    }

    public final int getCategoryID ( )
    {
        return Node.GATE;
    }

    public final String getName ( )
    {
        return NAME;
    }

    /**
     * Make the node equivalent for simulation
     * @param c the CircuitUI owning the component
     */
    public Actor makeSimulationNode ( CircuitUI c )
    {
        /* todo: core nodes should allow multiple wires for output */
        Wire in = new Wire ( c.getCircuit() );
        Wire out = new Wire ( c.getCircuit() );

        out.setInput ( in );
        in.addConnection ( out );

        getAnchor(0).linkForSimulation ( in, c );
        getAnchor(1).linkForSimulation ( out, c );

        c.getCircuit().addWire ( in );
        c.getCircuit().addWire ( out );

        return out;
    }
}

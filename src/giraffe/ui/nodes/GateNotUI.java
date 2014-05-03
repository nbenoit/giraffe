/*
 * $RCSfile: GateNotUI.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.11 $
 */

package giraffe.ui.nodes;

import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

import giraffe.ui.*;

import giraffe.core.*;

public class GateNotUI extends Node
{
    public static final String CATEGORY = "Logic Gates";
    public static final String NAME = "Not";

    public static final String IMG_NORMAL_NAME = "gate_not.png";
    private static Image img_normal;

    public static final String IMG_SELECTED_NAME = "gate_not_selected.png";
    private static Image img_selected;

    private Image img;

    public GateNotUI ( )
    {
        super ( );

        try
            {
                img_normal = ImageIO.read ( new File ( Node.IMG_PATH + IMG_NORMAL_NAME ) );
                img_selected = ImageIO.read ( new File ( Node.IMG_PATH + IMG_SELECTED_NAME ) );
            }

        catch ( Exception e )
            {
                throw new RuntimeException ( e.getMessage() );
            }

        img = img_normal;

        this.bounds.setSize ( img.getWidth(null)+2+(2*Anchor.WIDTH), img.getHeight(null)+2 );
        this.anchors.add ( new Anchor ( Anchor.IN, 2, (bounds.height/2)-(Anchor.HEIGHT/2)+1, this, 0 ) );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (bounds.height/2)-(Anchor.HEIGHT/2)+1, this, 1 ) );
    }

    public void paint ( Graphics g )
    {
        g.drawImage ( img, bounds.x+2+Anchor.WIDTH, bounds.y+2, null );

        super.paint ( g );
    }

    public void select ( )
    {
        super.select ( );
        this.img = img_selected;
    }

    public void unselect ( )
    {
        super.unselect ( );
        this.img = img_normal;
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
        GateNot g = new GateNot ( in, out );

        getAnchor(0).linkForSimulation ( in, c );
        getAnchor(1).linkForSimulation ( out, c );

        //c.getCircuit().addWire ( in );
        //c.getCircuit().addWire ( out );

        return g;
    }
}

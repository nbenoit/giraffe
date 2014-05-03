/*
 * $RCSfile: Gate2InputsUI.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.9 $
 */

package giraffe.ui.nodes;

import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

import giraffe.ui.*;

import giraffe.core.Actor;
import giraffe.core.Wire;
import giraffe.core.GateTwoInputs;

public abstract class Gate2InputsUI extends Node
{
    public static final String CATEGORY = "Logic Gates";

    public String img_normal_name;
    protected Image img_normal;

    public String img_selected_name;
    protected Image img_selected;

    protected Image img;

    public Gate2InputsUI ( String img_normal_name, String img_selected_name )
    {
        super ( );

        this.img_normal_name = img_normal_name;
        this.img_selected_name = img_selected_name;

        try
            {
                img_normal = ImageIO.read ( new File ( Node.IMG_PATH + img_normal_name ) );
                img_selected = ImageIO.read ( new File ( Node.IMG_PATH + img_selected_name ) );
            }

        catch ( Exception e )
            {
                throw new RuntimeException ( e.getMessage() );
            }

        img = img_normal;

        this.bounds.setSize ( img.getWidth(null)+2+(2*Anchor.WIDTH), img.getHeight(null)+2 );
        this.anchors.add ( new Anchor ( Anchor.IN, 2, (bounds.height/3)-(Anchor.HEIGHT/2)-1, this, 0 ) );
        this.anchors.add ( new Anchor ( Anchor.IN, 2, ((bounds.height/3)*2)-(Anchor.HEIGHT/2)+3, this, 1 ) );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (bounds.height/2)-(Anchor.HEIGHT/2)+1, this, 2 ) );
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

    public abstract String getName ( );

    /**
     * Create a new simulation gate for the current gate
     * @param inA the input 1
     * @param inB the input 2
     * @param out the output
     * @return a valid simulation gate
     */
    public abstract GateTwoInputs newSimulationGate ( Wire inA, Wire inB, Wire out );

    /**
     * Make the node equivalent for simulation
     * @param c the CircuitUI owning the component
     */
    public Actor makeSimulationNode ( CircuitUI c )
    {
        /* todo: core nodes should allow multiple wires for output */
        Wire inA = new Wire ( c.getCircuit() );
        Wire inB = new Wire ( c.getCircuit() );
        Wire out = new Wire ( c.getCircuit() );
        GateTwoInputs g = this.newSimulationGate ( inA, inB, out );

        getAnchor(0).linkForSimulation ( inA, c );
        getAnchor(1).linkForSimulation ( inB, c );
        getAnchor(2).linkForSimulation ( out, c );

        return g;
    }
}

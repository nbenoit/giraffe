/*
 * $RCSfile: InputUI.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.13 $
 */

package giraffe.ui.nodes;

import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

import giraffe.ui.*;

import giraffe.core.Input;
import giraffe.core.Actor;

public abstract class InputUI extends Node implements IONode
{
    public static final String CATEGORY = "I/O";

    public String img_normal_name;
    protected Image img_normal;

    public String img_selected_name;
    protected Image img_selected;

    protected Image img;

    public InputUI ( String img_normal_name, String img_selected_name )
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

        this.resetBounds ( );
    }

    public void resetBounds ( )
    {
        this.bounds.setSize ( img.getWidth(null)+2+Anchor.WIDTH, img.getHeight(null)+2 );
    }

    public void paint ( Graphics g )
    {
        g.drawImage ( img, bounds.x+2, bounds.y+2, null );

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

    public abstract int getCategoryID ( );

    public abstract int getBitsCount ( );

    public abstract String getName ( );

    /**
     * Make the node equivalent for simulation
     * @param c the CircuitUI owning the component
     */
    public Actor makeSimulationNode ( CircuitUI c )
    {
        Input in = new Input ( this.getBitsCount(), c.getCircuit() );

        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            {
                Anchor a = iterAnchors.next ( );
                a.linkForSimulation ( in.getWire(a.getID()), c );
            }

        return in;
    }
}

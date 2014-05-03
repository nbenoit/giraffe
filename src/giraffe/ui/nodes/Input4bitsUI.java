/*
 * $RCSfile: Input4bitsUI.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.4 $
 */

package giraffe.ui.nodes;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import giraffe.ui.Inputable;
import giraffe.ui.NodeDialog;
import giraffe.ui.Node;
import giraffe.ui.Anchor;

public class Input4bitsUI extends InputUI implements Inputable
{
    public static final String NAME = "Input 4 bits";

    public static final String IMG_NORMAL_NAME = "input4bits.png";
    public static final String IMG_SELECTED_NAME = "input4bits_selected.png";

    private String nodeName;
    private boolean nodeNameUpdated;
    private static final int FONT_SIZE = 9;
    private static final Font font = new Font ( "Monospaced", Font.PLAIN, FONT_SIZE );

    public Input4bitsUI ( )
    {
        super ( Input4bitsUI.IMG_NORMAL_NAME, Input4bitsUI.IMG_SELECTED_NAME );

        this.nodeName = "in";
        this.nodeNameUpdated = false;
        this.resetBounds ( );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (img.getHeight(null)/4)-(Anchor.HEIGHT/2)-4, this, 0 ) );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (img.getHeight(null)/4)*2-(Anchor.HEIGHT/2)-4, this, 1 ) );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (img.getHeight(null)/4)*3-(Anchor.HEIGHT/2)-4, this, 2 ) );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (img.getHeight(null)/4)*4-(Anchor.HEIGHT/2)-4, this, 3 ) );
    }

    public void resetBounds ( )
    {
        super.resetBounds ( );
        this.bounds.setSize ( this.bounds.width, this.bounds.height+FONT_SIZE+2 );
    }

    public void paint ( Graphics g )
    {
        if ( this.nodeNameUpdated )
            this.updateBounds ( g );

        super.paint ( g );
        g.setColor ( Color.black );
        g.setFont ( Input4bitsUI.font );
        g.drawString ( this.nodeName, this.bounds.x+3, this.bounds.y+this.bounds.height-3 );
    }

    public void updateBounds ( Graphics g )
    {
        this.nodeNameUpdated = false;

        g.setFont ( Input4bitsUI.font );

        int w = (int) Math.ceil ( g.getFontMetrics().getStringBounds(this.nodeName,g).getWidth ( ) );

        this.resetBounds ( );

        if ( w >= (this.bounds.width-3) )
            this.bounds.setSize ( w+4, this.bounds.height );
    }

    public final String getName ( )
    {
        return NAME;
    }

    public int getBitsCount ( )
    {
        return 4;
    }

    public String getNodeName ( )
    {
        return this.nodeName;
    }

    public void setNodeName ( String name )
    {
        if ( name != null )
            {
                this.nodeName = name;
                this.nodeNameUpdated = true;
            }
    }

    public void openDialog ( giraffe.ui.Frame parent )
    {
        new NodeDialog ( this.getName(), parent, this );
    }

    public int getCategoryID ( )
    {
        return Node.INPUT;
    }
}

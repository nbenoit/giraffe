/*
 * $RCSfile: Input1bitUI.java,v $
 * $Date: 2006/05/22 13:29:00 $ - $Revision: 1.14 $
 */

package giraffe.ui.nodes;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import giraffe.ui.Inputable;
import giraffe.ui.NodeDialog;
import giraffe.ui.Node;
import giraffe.ui.Anchor;

public class Input1bitUI extends InputUI implements Inputable
{
    public static final String NAME = "Input 1 bit";

    public static final String IMG_NORMAL_NAME = "input1bit.png";
    public static final String IMG_SELECTED_NAME = "input1bit_selected.png";

    private String nodeName;
    private boolean nodeNameUpdated;
    private static final int FONT_SIZE = 9;
    private static final Font font = new Font ( "Monospaced", Font.PLAIN, FONT_SIZE );

    public Input1bitUI ( )
    {
        super ( Input1bitUI.IMG_NORMAL_NAME, Input1bitUI.IMG_SELECTED_NAME );

        this.nodeName = "in";
        this.nodeNameUpdated = false;
        this.resetBounds ( );
        this.anchors.add ( new Anchor ( Anchor.OUT, bounds.width-Anchor.WIDTH, (img.getHeight(null)/2)-(Anchor.HEIGHT/2)+2, this, 0 ) );
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
        g.setFont ( Input1bitUI.font );
        g.drawString ( this.nodeName, this.bounds.x+3, this.bounds.y+this.bounds.height-3 );
    }

    public void updateBounds ( Graphics g )
    {
        this.nodeNameUpdated = false;

        g.setFont ( Input1bitUI.font );

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
        return 1;
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

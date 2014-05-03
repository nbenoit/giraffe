/*
 * $RCSfile: Anchor.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.12 $
 */

package giraffe.ui;

import java.awt.*;
import java.util.*;
import org.w3c.dom.* ;

import giraffe.core.Wire;

/**
 * An anchor is a link connector for nodes
 */
public class Anchor
{
    private boolean highlight;
    private static final Color colorHighLight = Color.orange;

    public static final int WIDTH = 9;
    public static final int HEIGHT = 7;

    private int ID;
    private Node parent;
    private Rectangle bounds;
    private int offsetX, offsetY;

    private Vector<Link> links;
    private Iterator<Link> iterLinks;

    public static final int IN = 0;
    public static final int OUT = 1;
    private int type;

    /**
     * Default constructor
     * @param type the type of anchor, should be Anchor.IN or Anchor.OUT.
     * @param offsetX the x offset position of the anchor
     * @param offsetY the y offset position of the anchor
     * @param parent the node that owns the anchor
     * @param ID the ID of the anchor
     */
    public Anchor ( int type, int offsetX, int offsetY, Node parent, int ID )
    {
        super ( );
        this.highlight = false;
        this.type = type;
        this.parent = parent;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.links = new Vector<Link> ( );
        this.bounds = new Rectangle ( this.parent.getBounds().x+this.offsetX, this.parent.getBounds().y+this.offsetY, WIDTH, HEIGHT );
        this.ID = ID;
    }

    /**
     * Paint the anchor
     * @param g the graphics object
     */
    public void paint ( Graphics g, Color c )
    {
        if ( this.highlight )
            {
                g.setColor ( Anchor.colorHighLight );
                g.fillRoundRect ( this.parent.getBounds().x+this.offsetX, this.parent.getBounds().y+this.offsetY, WIDTH-1, HEIGHT-1, 3, 3 );
                g.setColor ( c );
            }

        g.setColor ( Color.black );

        if ( type == IN )
            g.drawLine (  this.parent.getBounds().x+this.offsetX+(WIDTH/2), this.parent.getBounds().y+this.offsetY+(HEIGHT/2),
                          this.parent.getBounds().x+this.offsetX+WIDTH, this.parent.getBounds().y+this.offsetY+(HEIGHT/2) );
        else
            g.drawLine (  this.parent.getBounds().x+this.offsetX, this.parent.getBounds().y+this.offsetY+(HEIGHT/2),
                          this.parent.getBounds().x+this.offsetX+(WIDTH/2), this.parent.getBounds().y+this.offsetY+(HEIGHT/2) );
    }

    /**
     * Highlight the anchor
     */
    public void highlight ( )
    {
        this.highlight = true;
    }

    /**
     * Check if the highlight of the anchor should be updated
     * @param x the x coord of the cursor
     * @param y the y coord of the cursor
     */
    public void updateHighLight ( int x, int y )
    {
        this.bounds.setLocation ( this.parent.getBounds().x+this.offsetX, this.parent.getBounds().y+this.offsetY );
        this.highlight = ( this.bounds.contains ( x, y ) );
    }

    /**
     * Reset highlight to false
     */
    public void clearHighLight (  )
    {
        this.highlight = false;
    }

    /**
     * Is the anchor highlighted ?
     * @return true if yes, false either
     */
    public boolean isHighLighted ( )
    {
        return this.highlight;
    }

    /**
     * Parent accessor
     * @return the node that owns the anchor
     */
    public Node getParent ( )
    {
        return this.parent;
    }

    /**
     * Type accessor
     * @return the type of the anchor
     */
    public int getType ( )
    {
        return this.type;
    }

    /**
     * Add a link to the anchor
     * @param l the new link
     */
    public void addLink ( Link l )
    {
        this.links.add ( l );
    }

    /**
     * Remove a link from the anchor
     * @param l the link that must be removed
     */
    public void removeLink ( Link l )
    {
        this.links.remove ( l );
    }

    /**
     * Detach the anchor from the links
     */
    public void detach ( )
    {
        for ( iterLinks=this.links.iterator(); iterLinks.hasNext(); )
            iterLinks.next().removeLinkedAnchor ( this );
    }

    /**
     * Get the center point of the anchor
     * @return the center point
     */
    public Point getCenter ( )
    {
        return new Point ( this.parent.getBounds().x+this.offsetX+(WIDTH/2), this.parent.getBounds().y+this.offsetY+(HEIGHT/2) );
    }

    /**
     * Get the x coord of the center point of the anchor
     * @return the x coord of the center point
     */
    public int getCenterX ( )
    {
        return ( this.parent.getBounds().x + this.offsetX+(WIDTH/2) );
    }

    /**
     * Get the y coord of the center point of the anchor
     * @return the y coord of the center point
     */
    public int getCenterY ( )
    {
        return ( this.parent.getBounds().y + this.offsetY+(HEIGHT/2) );
    }

    /**
     * Notify the anchor that the node has been moved, so the links could be updated
     */
    public void moved ( )
    {
        for ( iterLinks=this.links.iterator(); iterLinks.hasNext(); )
            iterLinks.next().makeBoundingPolygon ( );
    }

    /**
     * ID Accessor
     * @return the ID of the anchor
     */
    public int getID ( )
    {
        return this.ID;
    }

    /**
     * Link a wire to the wires linked to the anchor
     * @param w the node wire
     * @param c the circuit that will give us the link equivalent wire
     */
    public void linkForSimulation ( Wire w, CircuitUI c )
    {
        if ( type == IN )
            {
                for ( iterLinks=this.links.iterator(); iterLinks.hasNext(); )
                    {
                        Wire in = c.getWireForLink ( iterLinks.next() );
                        w.setInput ( in );
                        in.addConnection ( w );
                    }
            }
        else
            {
                for ( iterLinks=this.links.iterator(); iterLinks.hasNext(); )
                    {
                        Wire out = c.getWireForLink ( iterLinks.next() );
                        w.addConnection ( out );
                        out.setInput ( w );
                    }
            }
    }

    /**
     * Dump the anchor in an XML document
     * @param doc an XML document
     * @param root the root element of the document
     * @return the element that contains the description of the anchor
     */
    public Element dumpToXml ( Document doc, Element root )
    {
        Element e = doc.createElement ( "Anchor" );

        e.setAttribute ( "id", Integer.toString ( this.getID() ) );

        for ( iterLinks=this.links.iterator(); iterLinks.hasNext(); )
            {
                Element l = doc.createElement ( "Link" );
                Link link = iterLinks.next ( );
                l.setAttribute ( "id", Integer.toString(link.getID()) );
                l.setAttribute ( "index", Integer.toString(link.getIndexOfAnchor(this)) );
                e.appendChild ( ( org.w3c.dom.Node ) l );
            }

        root.appendChild ( ( org.w3c.dom.Node ) e );
        return e;
    }
}

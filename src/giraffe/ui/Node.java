/*
 * $RCSfile: Node.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.24 $
 */

package giraffe.ui;

import java.awt.*;
import java.util.*;
import org.w3c.dom.* ;

import giraffe.core.Actor;

/**
 * Abstract class inherited by all nodes
 */
public abstract class Node implements Selectable
{
    public static final String IMG_PATH = giraffe.Giraffe.PATH + "/gfx/nodes/";

    protected Rectangle bounds;
    private int offsetX, offsetY;
    private Color c;
    private boolean framed;
    private boolean selected;
    private boolean dragged;
    private boolean linked;

    protected HashSet<Anchor> anchors;
    protected Iterator<Anchor> iterAnchors;

    public static final Color colorNormal = Color.black;
    public static final Color colorSelected = new Color ( 22, 54, 118 );
    public static final Color colorFrame = Color.red;

    public static final int GATE = 0;
    public static final int COMPOSITE = 1;
    public static final int INPUT = 2;
    public static final int OUTPUT = 3;
    public static final int VALUE = 4;

    /**
     * Default constructor
     */
    public Node ( )
    {
        this.bounds = new Rectangle ( );

        this.c = colorNormal;
        this.framed = false;
        this.selected = false;
        this.dragged = false;

        this.anchors = new HashSet<Anchor> ( );
    }

    /**
     * Set the location of the node
     * @param x the new x coord
     * @param y the new y coord
     */
    public void setLocation ( int x, int y )
    {
        this.bounds.x = x;
        this.bounds.y = y;
    }

    /**
     * Paint the node
     * @param g the graphics object
     */
    public void paint ( Graphics g )
    {
        /** anchors **/
        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            iterAnchors.next().paint ( g, this.c );

        /** frame **/
        if ( this.framed )
            {
                g.setColor ( colorFrame );
                g.drawRect ( this.bounds.x+1, this.bounds.y+1, this.bounds.width-1, this.bounds.height-1 );
            }
    }

    /**
     * Get the anchor with specified ID
     * @param ID the ID of the requested Anchor
     * @return the requested Anchor object
     */
    public Anchor getAnchor ( int ID )
    {
        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            {
                Anchor a = iterAnchors.next ( );

                if ( a.getID() == ID )
                    return a;
            }

        return null;
    }

    /**
     * Test if the node contains the specified point
     * @param x the x coord
     * @param y the y coord
     * @return true if the node contains the specified point, false either
     */
    public boolean contains ( int x, int y )
    {
        return this.bounds.contains ( x, y );
    }

    /**
     * Select the node
     */
    public void select ( )
    {
        this.c = colorSelected;
        this.selected = true;
    }

    /**
     * Unselect the node
     */
    public void unselect ( )
    {
        this.selected = false;
        this.c = colorNormal;
    }

    /**
     * Is the node selected ?
     * @return true if yes, false either
     */
    public boolean isSelected ( )
    {
        return this.selected;
    }

    /**
     * Frame the node
     */
    public void frame ( )
    {
        this.framed = true;
    }

    /**
     * Unframe the node
     */
    public void unframe ( )
    {
        this.framed = false;
    }

    /**
     * Is the node framed ?
     * @return true if yes, false either
     */
    public boolean isFramed ( )
    {
        return this.framed;
    }

    /**
     * Has the node the specified anchor ?
     * @return true if yes, false either
     */
    public boolean hasAnchor ( Anchor a )
    {
        return this.anchors.contains ( a );
    }

    /**
     * Update the state of node's anchors.
     * @param x the x coord of the cursor
     * @param y the y coord of the cursor
     * @param selectedAnchor is an anchor that should not be updated
     * @return the anchor at the specified coords, null if there is no anchor
     */
    public Anchor updateAnchorsState ( int x, int y, Anchor selectedAnchor )
    {
        Anchor a, ret=null;

        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            {
                a = iterAnchors.next ( );
                a.clearHighLight ( );
                a.updateHighLight ( x, y );

                if ( a.isHighLighted() )
                    ret = a;

                if ( a == selectedAnchor )
                    a.highlight ( );
            }

        return ret;
    }

    /**
     * Clear the anchors' state
     */
    public void clearAnchorsState ( )
    {
        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            iterAnchors.next().clearHighLight ( );
    }

    /**
     * Start dragging the node to the specified coords
     * @param x new x coord
     * @param y new y coord
     */
    public void startDragging ( int x, int y )
    {
        this.dragged = true;
        this.offsetX = x - this.bounds.x;
        this.offsetY = y - this.bounds.y;
    }

    /**
     * Get the X coord of the dragging offset
     * @retur the X coord of the dragging offset
     */
    public int getDraggingOffsetX ( )
    {
        return this.offsetX;
    }

    /**
     * Get the Y coord of the dragging offset
     * @retur the Y coord of the dragging offset
     */
    public int getDraggingOffsetY ( )
    {
        return this.offsetY;
    }

    /**
     * Drag the node to the specified coords
     * @param x new x coord
     * @param y new y coord
     */
    public void drag ( int x, int y )
    {
        if ( this.dragged )
            {
                this.setLocation ( x-offsetX, y-offsetY );

                for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
                    iterAnchors.next().moved ( );
            }
    }

    /**
     * End draggin the node at the specified coords
     * @param x final x coord
     * @param y final y coord
     */
    public void endDragging ( int x, int y )
    {
        this.dragged = false;
    }

    /**
     * Is the node being dragged ?
     * @return true if yes, false either
     */
    public boolean isBeingDragged ( )
    {
        return this.dragged;
    }

    /**
     * Start linking the node
     */
    public void startLinking ( )
    {
        this.linked = true;
    }

    /**
     * End linking the node
     */
    public void endLinking ( )
    {
        this.linked = false;
    }

    /**
     * Is the node being linked ?
     * @return true if yes, false either
     */
    public boolean isBeingLinked ( )
    {
        return this.linked;
    }

    /**
     * Get the bounds of the node
     * @return a rectangle around the node
     */
    public Rectangle getBounds ( )
    {
        return this.bounds;
    }

    /**
     * Update the bounds of the node. This is sometimes redefined for dynamic drawings.
     * @param g the graphics object
     */
    public void updateBounds ( Graphics g )
    {
        /* redefined by some children class */
    }

    /**
     * Detach the node from its links
     */
    public void detach ( )
    {
        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            iterAnchors.next().detach ( );
    }

    /**
     * Get the category of the node
     * @return the category name of the node
     */
    public abstract String getCategory ( );

    /**
     * Get the name the node.
     * @return the name of the node
     */
    public abstract String getName ( );

    /**
     * Get the category of the node
     * @return the category ID of the node
     */
    public abstract int getCategoryID ( );

    /**
     * Make the node equivalent for simulation
     * @param c the CircuitUI owning the component
     */
     public abstract Actor makeSimulationNode ( CircuitUI c );

    /**
     * Dump a node in an XML document
     * @param doc an XML document
     * @param root the root element of the document
     * @return the element that contains the description of the node
     */
    public Element dumpToXml ( Document doc, Element root )
    {
        Element e = doc.createElement ( "Node" );

        e.setAttribute ( "class", this.getClass().getName() );
        e.setAttribute ( "x", Integer.toString ( this.bounds.x ) );
        e.setAttribute ( "y", Integer.toString ( this.bounds.y ) );

        for ( iterAnchors=this.anchors.iterator(); iterAnchors.hasNext(); )
            iterAnchors.next().dumpToXml ( doc, e );

        if ( this instanceof giraffe.ui.Nameable )
            e.setAttribute ( "node_name", ((Nameable)this).getNodeName() );

        if ( this instanceof giraffe.ui.CompositeNode )
            e.setAttribute ( "file_name", ((CompositeNode)this).getCircuitFileName() );

        root.appendChild ( ( org.w3c.dom.Node ) e );
        return e;
    }

    /**
     * Copy a node
     * @param g the graphics object
     */
    public Node copy ( Graphics g )
    {
        Node ret = null;

        try
            {
                ret = this.getClass().getConstructor().newInstance ( );
            }

        catch ( Exception ex )
            {
                return null;
            }

        if ( ret != null )
            {
                ret.setLocation ( this.getBounds().x, this.getBounds().y );

                /* todo: fix because it could be another kind of composite */
                if ( this instanceof CompositeNode )
                    {
                        try
                            {
                                ((CompositeNode)ret).load ( new java.io.File(((CompositeNode)this).getCircuitFullPathName()), g );
                            }

                        catch ( Exception ex )
                            {
                                return null;
                            }
                    }

                if ( this instanceof Nameable )
                    ((Nameable)ret).setNodeName ( ((Nameable)this).getNodeName() );
            }

        return ret;
    }
}

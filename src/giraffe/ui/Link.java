/*
 * $RCSfile: Link.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.20 $
 */

package giraffe.ui;

import java.awt.*;
import java.util.*;

/**
 * A Link link two anchors or more.
 */
public class Link implements Selectable
{
    private int ID;
    private Polygon bounds;
    private Color c;
    private boolean framed;
    private boolean selected;

    public static final Color colorNormal = Color.black;
    public static final Color colorSelected = new Color ( 239, 229, 0 );
    public static final Color colorFrame = Color.red;

    private Point linkAdditional;

    private LinkedList<Anchor> linked;
    private Iterator<Anchor> iterLinked;

    /**
     * Constructor with an origin anchor
     * @param orig the origin anchor of the link
     * @param ID the ID of the link
     */
    public Link ( Anchor orig, int ID )
    {
        this.bounds = new Polygon ( );

        this.c = colorNormal;
        this.framed = false;
        this.selected = false;

        this.linkAdditional = null;

        this.linked = new LinkedList<Anchor> ( );
        this.linked.add ( orig );
        this.makeBoundingPolygon ( );
        this.ID = ID;
    }

    /**
     * Default constructor
     * @param ID the ID of the link
     */
    public Link ( int ID )
    {
        this.bounds = new Polygon ( );

        this.c = colorNormal;
        this.framed = false;
        this.selected = false;

        this.linkAdditional = null;

        this.linked = new LinkedList<Anchor> ( );
        this.makeBoundingPolygon ( );
        this.ID = ID;
    }

    /**
     * Paint the link
     * @param g the graphics object
     */
    public void paint ( Graphics g )
    {
        Point p1, p2;
        Anchor a;

        /** draw link **/
        g.setColor ( this.c );

        if ( this.linked.size() != 0 )
            {
                iterLinked = this.linked.iterator ( );
                a = iterLinked.next ( );

                if ( a == null )
                    return;

                p1 = a.getCenter ( );

                for ( ; iterLinked.hasNext(); )
                    {
                        a = iterLinked.next ( );

                        if ( a == null )
                            return;

                        p2 = a.getCenter ( );
                        g.drawLine ( p1.x, p1.y, p2.x, p2.y );
                        p1 = new Point ( (p1.x+p2.x)/2, (p1.y+p2.y)/2 );

                        if ( iterLinked.hasNext() )
                            {
                                g.fillRect ( p1.x-2, p1.y-1, 5, 3 );
                                g.fillRect ( p1.x-1, p1.y-2, 3, 5 );
                            }
                    }

                if ( this.linkAdditional != null )
                    {
                        if ( this.linked.size() > 1 )
                            {
                                g.fillRect ( p1.x-2, p1.y-1, 5, 3 );
                                g.fillRect ( p1.x-1, p1.y-2, 3, 5 );
                            }

                        g.drawLine (  p1.x, p1.y, this.linkAdditional.x, this.linkAdditional.y );
                    }
            }

        if ( this.framed )
            {
                g.setColor ( colorFrame );
                g.drawPolygon ( this.bounds );
            }
    }

    /**
     * Build the bounds of the link. It creates a polygon all around the link.
     */
    public void makeBoundingPolygon ( ) /* dirty :( */
    {
        this.bounds.reset ( );

        if ( this.linked.size() < 1 )
            return;

        Anchor a;
        Stack<Point> s = new Stack<Point> ( );
        double v1x, v1y, v2x, v2y, v3x, v3y, vn1x, vn1y, vn2x, vn2y, n1, n2;
        double x1, y1, x2, y2, x3, y3;
        double theta, tmp;
        boolean loop = true;
        boolean inverse = false;

        iterLinked = this.linked.iterator ( );
        a = iterLinked.next ( );

        if ( a == null )
            return;

        x1 = a.getCenterX();
        y1 = a.getCenterY();

        if ( this.linked.size() == 1 )
            {
                if ( this.linkAdditional == null )
                    return;

                x2 = this.linkAdditional.x;
                y2 = this.linkAdditional.y;
            }
        else
            {
                a = iterLinked.next ( );

                if ( a == null )
                    return;

                x2 = a.getCenterX();
                y2 = a.getCenterY();
            }

        v1x = x2 - x1;
        v1y = y2 - y1;

        if ( v1x != 0.0 )
            {
                v2x = (Math.abs(v1y/v1x)) * Math.signum(v1y);
                v2y = -1.0 * Math.signum(v1x);
            }
        else if ( v1y != 0.0 )
            {
                v2x = 1.0 * Math.signum(v1y);
                v2y = 0.0;
            }
        else
            {
                v2x = 0.0;
                v2y = 0.0;
            }

        n2 = Math.sqrt ( v2x*v2x+v2y*v2y );
        vn2x = v2x / n2;
        vn2y = v2y / n2;
        v2x = vn2x * 4;
        v2y = vn2y * 4; /* perp vect */

        n1 = Math.sqrt ( v1x*v1x+v1y*v1y );
        vn1x = v1x / n1;
        vn1y = v1y / n1;
        v1x = vn1x * 4;
        v1y = vn1y * 4; /* vect */

        theta = -Math.atan2 ( vn1y, vn1x );

        if ( theta < 0.0 ) /* angle of first vector */
            theta += (2 * Math.PI);

        x3 = ( x1+x2 ) / 2;
        y3 = ( y1+y2 ) / 2;

        if ( iterLinked.hasNext() )
            {
                a = iterLinked.next ( ); /* next link vect */

                if ( a == null )
                    return;

                vn2x = a.getCenterX() - x3;
                vn2y = a.getCenterY() - y3;
                n2 = Math.sqrt ( vn2x*vn2x+vn2y*vn2y );
                vn2x = vn2x / n2;
                vn2y = vn2y / n2;
            }
        else
            loop = false;

        tmp = vn2x; /* rotation of next link vect */
        vn2x = ( ( vn2x*Math.cos(theta) ) - ( vn2y*Math.sin(theta) ) );
        vn2y = ( ( tmp*Math.sin(theta) ) + ( vn2y*Math.cos(theta) ) );

        this.bounds.addPoint ( ((int)(x1-v1x+v2x)), ((int)(y1-v1y+v2y)) ); // 1
        s.push ( new Point ( ((int)(x1-v1x-v2x)), ((int)(y1-v1y-v2y)) ) ); // 4

        if ( vn2y >= 0.0 )
            {
                this.bounds.addPoint ( ((int)(x2+v1x+v2x)), ((int)(y2+v1y+v2y)) ); // 2
                this.bounds.addPoint ( ((int)(x2+v1x-v2x)), ((int)(y2+v1y-v2y)) ); // 3
                inverse = true;
            }
        else
            {
                s.push ( new Point ( ((int)(x2+v1x-v2x)), ((int)(y2+v1y-v2y)) ) ); // 3
                s.push ( new Point ( ((int)(x2+v1x+v2x)), ((int)(y2+v1y+v2y)) ) ); // 2
                inverse = false;
            }

        x1 = x3;
        y1 = y3;

        v3x = v1x;
        v3y = v1y;

        x2 = a.getCenterX ( );
        y2 = a.getCenterY ( );

        while ( loop )
            {
                v1x = x2 - x1;
                v1y = y2 - y1;

                if ( v1x != 0.0 )
                    {
                        v2x = (Math.abs(v1y/v1x)) * Math.signum(v1y);
                        v2y = -1.0 * Math.signum(v1x);
                    }
                else if ( v1y != 0.0 )
                    {
                        v2x = 1.0 * Math.signum(v1y);
                        v2y = 0.0;
                    }
                else
                    {
                        v2x = 0.0;
                        v2y = 0.0;
                    }

                n2 = Math.sqrt ( v2x*v2x+v2y*v2y );
                vn2x = v2x / n2;
                vn2y = v2y / n2;
                v2x = vn2x * 4;
                v2y = vn2y * 4; /* perp vect */

                n1 = Math.sqrt ( v1x*v1x+v1y*v1y );
                vn1x = v1x / n1;
                vn1y = v1y / n1;
                v1x = vn1x * 4;
                v1y = vn1y * 4; /* vect */

                x3 = ( x1+x2 ) / 2;
                y3 = ( y1+y2 ) / 2;

                if ( iterLinked.hasNext() )
                    {
                        a = iterLinked.next ( );

                        if ( a == null )
                            return;

                        vn2x = a.getCenterX() - x3;
                        vn2y = a.getCenterY() - y3;
                        n2 = Math.sqrt ( vn2x*vn2x+vn2y*vn2y );
                        vn2x = vn2x / n2;
                        vn2y = vn2y / n2;
                    }
                else
                    loop = false;

                theta = -Math.atan2 ( vn1y, vn1x );

                if ( theta < 0.0 )
                    theta += (2 * Math.PI);

                tmp = vn2x;
                vn2x = ( ( vn2x*Math.cos(theta) ) - ( vn2y*Math.sin(theta) ) );
                vn2y = ( ( tmp*Math.sin(theta) ) + ( vn2y*Math.cos(theta) ) );

                if ( inverse )
                    {
                        this.bounds.addPoint ( ((int)(x1+v1x+v3x)), ((int)(y1+v1y+v3y)) ); // 4
                        s.push ( new Point ( ((int)(x1+v1x-v3x)), ((int)(y1+v1y-v3y)) ) ); // 1
                    }
                else
                    {
                        this.bounds.addPoint ( ((int)(x1+v1x-v3x)), ((int)(y1+v1y-v3y)) ); // 1
                        s.push ( new Point ( ((int)(x1+v1x+v3x)), ((int)(y1+v1y+v3y)) ) ); // 4
                    }

                if ( loop )
                    {
                        if ( vn2y >= 0.0 )
                            {
                                this.bounds.addPoint ( ((int)(x2+v1x+v2x)), ((int)(y2+v1y+v2y)) ); // 2
                                this.bounds.addPoint ( ((int)(x2+v1x-v2x)), ((int)(y2+v1y-v2y)) ); // 3
                                inverse = true;
                            }
                        else
                            {
                                s.push ( new Point ( ((int)(x2+v1x-v2x)), ((int)(y2+v1y-v2y)) ) ); // 3
                                s.push ( new Point ( ((int)(x2+v1x+v2x)), ((int)(y2+v1y+v2y)) ) ); // 2
                                inverse = false;
                            }
                    }
                else
                    {
                        this.bounds.addPoint ( ((int)(x2+v1x+v2x)), ((int)(y2+v1y+v2y)) ); // 2
                        this.bounds.addPoint ( ((int)(x2+v1x-v2x)), ((int)(y2+v1y-v2y)) ); // 3
                    }

                x1 = x3;
                y1 = y3;

                v3x = v1x;
                v3y = v1y;

                if ( loop )
                    {
                        x2 = a.getCenterX();
                        y2 = a.getCenterY();
                    }
            }

        while ( !s.isEmpty() )
            {
                Point p = s.pop ( );
                this.bounds.addPoint ( p.x, p.y );
            }
    }

    /**
     * Get the current additional point.
     * @return the temporary point part of the link.
     */
    public Point getAdditional ( )
    {
        return this.linkAdditional;
    }

    /**
     * Set the current additional point.
     * @param p the new temporary point part of the link.
     */
    public void setAdditional ( Point p )
    {
        this.linkAdditional = p;
        this.makeBoundingPolygon ( );
    }

    /**
     * Add an anchor to the link. It sets the additional point to null.
     * @param a the new anchor
     */
    public boolean addLinkedAnchor ( Anchor a )
    {
        if ( this.linked.contains(a) )
            {
                this.linkAdditional = null;
                this.makeBoundingPolygon ( );
                return false;
            }

        this.linked.add ( a );
        a.addLink ( this );
        this.linkAdditional = null;
        this.makeBoundingPolygon ( );
        return true;
    }

    /**
     * Insert an anchor at a given index.
     * @param a the new anchor
     * @param index the index of the new anchor.
     */
    public boolean addLinkedAnchorAt ( Anchor a, int index )
    {
        if ( this.linked.contains(a) )
            {
                this.linkAdditional = null;
                this.makeBoundingPolygon ( );
                return false;
            }

        if ( index >= this.linked.size() )
            {
                final int lim = ( index - this.linked.size() );

                for ( int i=0; i<lim; ++i )
                    this.linked.add ( null );

                this.linked.add ( a );
            }
        else
            {
                if ( this.linked.get(index) == null )
                    this.linked.set ( index, a );
                else
                    this.linked.add ( index, a );
            }

        this.linkAdditional = null;
        this.makeBoundingPolygon ( );
        return true;
    }

    /**
     * Get the anchors attached to a link
     * @return a vector of anchors
     */
    public Iterator<Anchor> getAnchorsIterator ( )
    {
        return this.linked.iterator ( );
    }

    /**
     * Get the index of an anchor
     * @param a an anchor
     * @return the index of a
     */
    public int getIndexOfAnchor ( Anchor a )
    {
        return ( this.linked.indexOf(a) );
    }

    /**
     * Remove an anchor of the link
     * @param a the anchor to remove
     */
    public void removeLinkedAnchor ( Anchor a )
    {
        this.linked.remove ( a );
        this.makeBoundingPolygon ( );
    }

    /**
     * Select the link
     */
    public void select ( )
    {
        this.c = colorSelected;
        this.selected = true;
    }

    /**
     * Unselect the link
     */
    public void unselect ( )
    {
        this.selected = false;
        this.c = colorNormal;
    }

    /**
     * Frame the link
     */
    public void frame ( )
    {
        this.framed = true;
    }

    /**
     * Unframe the link
     */
    public void unframe ( )
    {
        this.framed = false;
    }

    /**
     * Is the link framed ?
     * @return true if yes, false either
     */
    public boolean isFramed ( )
    {
        return this.framed;
    }

    /**
     * Is the link selected ?
     * @return true if yes, false either
     */
    public boolean isSelected ( )
    {
        return this.selected;
    }

    /**
     * Test if the link contains the specified point
     * @param x the x coord
     * @param y the y coord
     * @return true if the link contains the specified point, false either
     */
    public boolean contains ( int x, int y )
    {
        return this.bounds.contains ( x, y );
    }

    /**
     * Get the bounds of the link.
     * @return a rectangle arround the polygonal bounds of the link
     */
    public Rectangle getBounds ( )
    {
        return this.bounds.getBounds ( );
    }

    /**
     * Detach the link from the other components
     */
    public void detach ( )
    {
        for ( iterLinked=this.linked.iterator(); iterLinked.hasNext(); )
            {
                Anchor a = iterLinked.next ( );

                if ( a == null )
                    continue;

                a.removeLink ( this );
            }

        this.linked.clear ( );
    }

    /**
     * Is the link invalid ?
     * @return true if yes, false either
     */
    public boolean isInvalid ( )
    {
        return ( ( this.linked.size() < 2 ) );
    }

    /**
     * Get the ID of the link
     * @return the ID of the link
     */
    public int getID ( )
    {
        return this.ID;
    }
}

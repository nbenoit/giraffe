/*
 * $RCSfile: CanvasLayout.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.4 $
 */

package giraffe.ui;

import java.awt.*;
import java.util.*;

/**
 * Layout for canvas
 */
public class CanvasLayout implements LayoutManager
{
    private HashSet<Component> content;
    private int width, height;

    /**
     * Default constructor.
     */
    public CanvasLayout ( )
    {
        this.content = new HashSet<Component> ( );
    }

    /**
     * Set the size of the layout.
     * @param width the new width.
     * @param height the new height.
     */
    public void setSize ( int width, int height )
    {
        this.width = width;
        this.height = height;
    }

    /**
     * Add a new component to the layout.
     * @param name the name of the new component.
     * @param comp the new component.
     */
    public void addLayoutComponent ( String name, Component comp )
    {
        this.content.add ( comp );
    }

    /**
     * Layout the parent container. Here, it should layout the nodes on the canvas, but it doesn't.
     * @param parent the parent container of the layout.
     */
    public void layoutContainer ( Container parent )
    {
    }

    /**
     * Get the minimum size of the layout
     * @param parent the parent container of the layout.
     * @return the preferred dimensions of the layout
     */
    public Dimension minimumLayoutSize ( Container parent )
    {
        return new Dimension ( this.width, this.height );
    }

    /**
     * Get the maximum size of the layout
     * @param parent the parent container of the layout.
     * @return the preferred dimensions of the layout
     */
    public Dimension preferredLayoutSize ( Container parent )
    {
        return new Dimension ( this.width, this.height );
    }

    /**
     * Remove a component of the layout.
     * @param comp the component to remove.
     */
    public void removeLayoutComponent ( Component comp )
    {
        this.content.remove ( comp );
    }
}

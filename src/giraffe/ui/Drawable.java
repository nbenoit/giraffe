/*
 * $RCSfile: Drawable.java,v $
 * $Date: 2006/05/04 11:42:12 $ - $Revision: 1.2 $
 */

package giraffe.ui;

import java.awt.Graphics;

/**
 * A drawable component can be painted with a graphics object.
 */
public interface Drawable
{
    /**
     * Paint the element
     * @param g the graphics object
     */
    public void paint ( Graphics g );
}

/*
 * $RCSfile: Selectable.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.5 $
 */

package giraffe.ui;

/**
 * A selectable can be selected and framed. It has bounds and can be separated from other components.
 */
public interface Selectable extends Drawable
{
    /**
     * Frame the component
     */
    public void frame ( );

    /**
     * Unframe the component
     */
    public void unframe ( );

    /**
     * Is the component framed ?
     * @return true if yes, false either
     */
    public boolean isFramed ( );

    /**
     * Select the component
     */
    public void select ( );

    /**
     * Unselect the component
     */
    public void unselect ( );

    /**
     * Is the component selected ?
     * @return true if yes, false either
     */
    public boolean isSelected ( );

    /**
     * Test if the component contains the specified point
     * @param x the x coord
     * @param y the y coord
     * @return true if the component contains the specified point, false either
     */
    public boolean contains ( int x, int y );

    /**
     * Detach the component from the other components
     */
    public void detach ( );
}

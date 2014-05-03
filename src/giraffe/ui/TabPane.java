/*
 * $RCSfile: TabPane.java,v $
 * $Date: 2006/05/03 16:51:15 $ - $Revision: 1.10 $
 */

package giraffe.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;

/**
 * Tabbed panel
 */
public class TabPane extends JTabbedPane implements MouseListener, ActionListener
{
    private static final long serialVersionUID = 0L;

    private JPopupMenu menu;
    private JMenuItem close;
    private giraffe.ui.Frame parent;

    /**
     * Default constructor
     * @param parent the parent Frame
     */
    public TabPane ( giraffe.ui.Frame parent )
    {
        super ( );
        this.parent = parent;
        this.menu = new JPopupMenu ( "actions" );
        this.close = new JMenuItem ( "close" );
        this.close.addActionListener(this);
        this.menu.add ( this.close );
        this.addMouseListener ( this );
        this.addChangeListener ( parent );
    }

    /**
     * Callback for mouse clicks
     * @param e the mouse event object
     */
    public void mouseClicked ( MouseEvent e )
    {
        if ( e.getButton() == MouseEvent.BUTTON3 )
            {
                this.menu.show ( this, e.getX(), e.getY() );
            }
        else
            {
                this.menu.setVisible ( false );
            }
    }

    /**
     * Callback for mouse press events
     * @param e the mouse event object
     */
    public void mousePressed ( MouseEvent e )
    {
    }

    /**
     * Callback for mouse released events
     * @param e the mouse event object
     */
    public void mouseReleased ( MouseEvent e )
    {
    }

    /**
     * Callback for mouse exited events
     * @param e the mouse event object
     */
    public void mouseExited ( MouseEvent e )
    {
    }

    /**
     * Callback for mouse entered events
     * @param e the mouse event object
     */
    public void mouseEntered ( MouseEvent e )
    {
    }

    /**
     * Callback for actions
     * @param e the action event object
     */
    public void actionPerformed ( ActionEvent e )
    {
        if ( e.getSource() == this.close )
            this.closeSelectedTab ( );
    }

    /**
     * Get the currently selected canvas
     * @return the canvas
     */
    public Canvas getSelectedCanvas ( )
    {
        return ((Canvas) this.getSelectedComponent());
    }

    /**
     * Get the canvas contained in this tab pane
     * @return an hashset of the canvas
     */
    public HashSet<Canvas> getCanvas ( )
    {
        HashSet<Canvas> ret = new HashSet<Canvas> ( );
        Component [] c = this.getComponents ( );

        for (  int i=0; i<c.length; ++i )
            ret.add ( ((Canvas)c[i]) );

        return ret;
    }

    /**
     * Close the selected tab
     */
    public void closeSelectedTab ( )
    {
        this.removeChangeListener ( this.parent ); /* don't tell the parent we're closing a tab */
        this.closeTab ( this.getSelectedIndex() );
        this.parent.updateButtonsAndMenu ( );
        this.addChangeListener ( this.parent );
    }

    /**
     * Close the tab with specified index (the user will be prompted for confirmation)
     * @param index the index of the tab that must be closed
     */
    public void closeTab ( int index )
    {
        this.closeTab ( index, true );
    }

    /**
     * Close the tab with specified index
     * @param index the index of the tab that must be closed
     * @param ask must be true if the user must be asked, false either
     */
    public void closeTab ( int index, boolean ask )
    {
        if ( index > -1 )
            {
                if ( ask )
                    {
                        if ( ( JOptionPane.showConfirmDialog ( null, "Close \'" + this.getTitleAt(index) + "\' ?", "Please confirm...",
                                                               JOptionPane.YES_NO_OPTION ) ) == JOptionPane.YES_OPTION )
                            this.remove ( index );
                    }
                else
                    this.remove ( index );
            }
    }
}

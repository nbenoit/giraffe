/*
 * $RCSfile: NodePreview.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.7 $
 */

package giraffe.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Node preview is a visualization widget for nodes
 */
public class NodePreview extends JPanel implements ComponentListener
{
    private Node node;

    private static final long serialVersionUID = 0L;

    public static final int MIN_WIDTH = 64;
    public static final int MIN_HEIGHT = 64;

    /**
     * Default constructor
     */
    public NodePreview ( )
    {
        super ( new CanvasLayout() );

        this.setVisible ( true );
        this.setBackground ( Color.white );
        this.addComponentListener ( this );
        this.setSize ( MIN_WIDTH, MIN_HEIGHT );

        this.node = null;

        ((CanvasLayout)this.getLayout()).setSize ( this.getBounds().width, this.getBounds().height );
    }

    /**
     * Paint the node preview widget
     */
    public void paintComponent ( Graphics g )
    {
        super.paintComponent ( g );

        g.setColor ( Color.gray );
        g.drawRect ( 0, 0, this.getBounds().width-1,this.getBounds().height-1 );

        if ( this.node != null )
            this.node.paint ( g );
    }

    /**
     * Update the size of the preview according to the size of the node
     * @param parent the parent container of the node preview
     */
    public void updateSize ( Container parent )
    {
        if ( ( node != null ) && ( parent.getGraphics() != null ) )
            {
                this.node.updateBounds ( parent.getGraphics() );
                int w = this.node.getBounds().width + 32;
                int h = this.node.getBounds().height + 32;

                if ( w > MIN_WIDTH )
                    {
                        if ( h > MIN_HEIGHT )
                            {
                                this.setSize ( new Dimension ( w, h ) );
                                this.node.setLocation ( 16, 16 );
                            }
                        else
                            {
                                this.setSize ( new Dimension ( w, MIN_HEIGHT ) );
                                this.node.setLocation ( 16, (MIN_HEIGHT-(h-32))/2 );
                            }
                    }
                else
                    {
                        if ( h > MIN_HEIGHT )
                            {
                                this.setSize ( new Dimension ( MIN_WIDTH, h ) );
                                this.node.setLocation ( (MIN_WIDTH-(w-32))/2, 16 );
                            }
                        else
                            {
                                this.setSize ( new Dimension ( MIN_WIDTH, MIN_HEIGHT ) );
                                this.node.setLocation ( (MIN_WIDTH-(w-32))/2, (MIN_HEIGHT-(h-32))/2 );
                            }
                    }
            }
        else
            this.setSize ( new Dimension ( MIN_WIDTH, MIN_HEIGHT ) );

        this.repaint ( );
    }

    /**
     * Callback for component hidden events
     * @param e the ComponentEvent object
     */
    public void componentHidden ( ComponentEvent e )
    {
    }

    /**
     * Callback for component moved events
     * @param e the ComponentEvent object
     */
    public void componentMoved ( ComponentEvent e )
    {
    }

    /**
     * Callback for component resized events
     * @param e the ComponentEvent object
     */
    public void componentResized ( ComponentEvent e )
    {
        ((CanvasLayout)this.getLayout()).setSize ( this.getBounds().width, this.getBounds().height );
        this.setPreferredSize ( new Dimension ( this.getBounds().width, this.getBounds().height ) );
    }

    /**
     * Callback for component shown events
     * @param e the ComponentEvent object
     */
    public void componentShown ( ComponentEvent e )
    {
    }

    /**
     * Set the node to preview
     * @param n the node to preview
     * @param parent the parent container of the widget
     */
    public void setNode ( Node n, Container parent )
    {
        this.node = n;
        this.updateSize ( parent );
    }

    /**
     * Get the node being previewed
     * @return the node being previewed
     */
    public Node getNode ( )
    {
        return this.node;
    }
}

/*
 * $RCSfile: Chronogram.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.17 $
 */

package giraffe.ui.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import giraffe.ui.CanvasLayout;
import giraffe.ui.Node;
import giraffe.ui.IONode;
import giraffe.ui.Nameable;


/**
 * Chronogram of a node
 */
public abstract class Chronogram extends JPanel implements ActionListener, ComponentListener, MouseMotionListener, MouseListener
{
    private static final long serialVersionUID = 0L;

    private SimulationDialog owner;

    public static final int IN = 0;
    public static final int OUT = 1;

    protected int type;
    protected Node node;

    private static final int FONT_SIZE_BIG = 14;
    private static final Font font_big = new Font ( "Monospaced", Font.PLAIN, FONT_SIZE_BIG );

    private static final int FONT_SIZE_SMALL = 9;
    private static final Font font_small = new Font ( "Monospaced", Font.PLAIN, FONT_SIZE_SMALL );

    private Rectangle header_bounds;
    private int name_pos;
    private int type_offset;
    private static int header_end = 65;

    private static int time_end=200;
    private static int time_width;
    private static double time_ratio;

    private Integer cursorX;
    private Integer selectionXstart;
    private Integer selectionXend;
    private static final Color selectionColor = new Color ( 200, 221, 242 );

    private ChronogramMenu menu;

    protected Vector<ChronogramChange> changes;

    /**
     * Internal constructor
     * @param owner the dialog that contains the chronogram
     */
    public Chronogram ( SimulationDialog owner, Node node )
    {
        super ( new CanvasLayout() );
        this.setBackground ( Color.white );
        this.owner = owner;
        this.node = node;
        ((CanvasLayout)this.getLayout()).setSize ( this.getBounds().width, this.getBounds().height );
        this.changes = new Vector<ChronogramChange> ( );
        this.addMouseListener ( this );
        this.addMouseMotionListener ( this );
        this.cursorX = null;
        this.selectionXstart = null;
        this.selectionXend = null;
    }

    /**
     * Initialization
     */
    protected void init ( )
    {
        Rectangle2D r = font_big.getStringBounds ( ((Nameable)node).getNodeName(), ((Graphics2D)this.owner.getGraphicContext()).getFontRenderContext() );
        double w = r.getWidth ( );
        double h = r.getHeight ( );

        if ( this.type == Chronogram.IN )
            r = font_small.getStringBounds( "(input)", ((Graphics2D)this.owner.getGraphicContext()).getFontRenderContext() );
        else
            r = font_small.getStringBounds( "(output)", ((Graphics2D)this.owner.getGraphicContext()).getFontRenderContext() );

        if ( r.getWidth() > w )
            {
                name_pos = ((int)(r.getWidth()-w)) / 2; /* name shorter than type */
                type_offset = -name_pos;
                w = r.getWidth();
            }
        else
            {
                name_pos = 0; /* name longer than type */
                type_offset = ((int)(w-r.getWidth())) / 2;
            }

        h += ( r.getHeight ( ) + 8 );

        this.header_bounds = new Rectangle ( 0, 0, (int)w, (int)h );
        this.menu = new ChronogramMenu ( this );
    }

    /**
     * Paint the chronogram
     * @param g a valid graphics object
     */
    public void paintComponent ( Graphics g )
    {
        super.paintComponent ( g );

        final int offset = (Chronogram.header_end-this.header_bounds.width) / 2;

        /* border */
        g.setColor ( Color.lightGray );
        g.drawRect ( 0, 0, this.getBounds().width-1, this.getBounds().height-1 );
        g.drawLine ( Chronogram.header_end-1, 1, Chronogram.header_end-1, this.getBounds().height-2 );
        g.drawLine ( Chronogram.header_end+1, 1, Chronogram.header_end+1, this.getBounds().height-2 );
        g.setColor ( this.owner.getBackground() );
        g.drawLine ( Chronogram.header_end, 0, Chronogram.header_end, this.getBounds().height-1 );

        /* header */
        g.setColor ( Color.black );
        g.setFont ( font_big );
        g.drawString ( ((Nameable)node).getNodeName(), offset+this.name_pos, FONT_SIZE_BIG+3 );

        g.setFont ( font_small );
        if ( this.type == Chronogram.IN )
            g.drawString ( "(input)", offset+this.name_pos+this.type_offset, FONT_SIZE_BIG+FONT_SIZE_SMALL+11 );
        else
            g.drawString ( "(output)", offset+this.name_pos+this.type_offset, FONT_SIZE_BIG+FONT_SIZE_SMALL+11 );

        /* selection */
        if ( ( this.selectionXstart != null ) && ( this.selectionXend != null ) )
            {
                g.setColor ( selectionColor );
                g.fillRect ( this.selectionXstart+1, 1, this.selectionXend-this.selectionXstart-1, this.getBounds().height-2 );
            }
        else if ( ( this.selectionXstart != null ) && ( this.cursorX != null ) )
            {
                g.setColor ( selectionColor );

                if ( this.cursorX < this.selectionXstart )
                    g.fillRect ( this.cursorX+1, 1, this.selectionXstart-this.cursorX-1, this.getBounds().height-2 );
                else
                    g.fillRect ( this.selectionXstart+1, 1, this.cursorX-this.selectionXstart-1, this.getBounds().height-2 );
            }

        /* values */
        if ( this.changes.size() == 1 )
            {
                this.plotSegment ( this.changes.get(0).time, time_end, this.changes.get(0).value, g );
            }
        else
            {
                int i;

                for ( i=1; i<this.changes.size(); ++i )
                    {
                        if ( this.changes.get(i).time >= time_end )
                            break;

                        this.plotSegment ( this.changes.get(i-1).time, this.changes.get(i).time, this.changes.get(i-1).value, g );
                    }

                this.plotSegment ( this.changes.get(i-1).time, time_end, this.changes.get(i-1).value, g );
            }

        /* cursor */
        if ( this.cursorX != null )
            {
                g.setColor ( Color.black );
                g.drawLine ( this.cursorX, 1, this.cursorX, this.getBounds().height-2 );
            }
    }

    /**
     * Plot a chronogram segment between two time offsets
     * @param start the start offset
     * @param end the end offset
     * @param value the value of the node during the segment
     * @param g a graphics object
     */
    public void plotSegment ( int start, int end, int value, Graphics g )
    {
        if ( value == 0 )
            g.setColor ( getColorLow() );
        else
            g.setColor ( getColorHigh() );

        final int x1 = header_end + 10 + (int) (((double)start) * time_ratio);
        final int x4 = header_end + 10 + (int) (((double)end) * time_ratio);

        int edge_len = 3;

        if ( (x4-x1) <= (2*edge_len) )
            edge_len = 1;

        final int x2 = x1+edge_len;
        final int x3 = x4-edge_len;

        g.setFont ( font_big );
        final int xtext =  (x1+x4-((int)g.getFontMetrics().getStringBounds(""+value,g).getWidth()))/2;
        final int ytext =  (this.getBounds().height+FONT_SIZE_BIG)/2;

        if ( ((IONode)this.node).getBitsCount() == 1 )
            {
                if ( value == 0 )
                    {
                        g.drawLine ( x1, this.getBounds().height/2, x1, 3*this.getBounds().height/4 );   /* edge */
                        g.drawLine ( x1, 3*this.getBounds().height/4, x4, 3*this.getBounds().height/4 ); /* line */
                        g.drawLine ( x4, 3*this.getBounds().height/4, x4, this.getBounds().height/2 );   /* edge */
                    }
                else
                    {
                        g.drawLine ( x1, this.getBounds().height/2, x1, this.getBounds().height/4 ); /* edge */
                        g.drawLine ( x1, this.getBounds().height/4, x4, this.getBounds().height/4 ); /* line */
                        g.drawLine ( x4, this.getBounds().height/4, x4, this.getBounds().height/2 ); /* edge */
                    }

            }
        else
            {
                g.drawLine ( x1, this.getBounds().height/2, x2, this.getBounds().height/4 );  /* edges */
                g.drawLine ( x1, this.getBounds().height/2, x2, 3*this.getBounds().height/4 );
                g.drawLine ( x2, this.getBounds().height/4, x3, this.getBounds().height/4 );  /* lines */
                g.drawLine ( x2, 3*this.getBounds().height/4, x3, 3*this.getBounds().height/4 );
                g.drawLine ( x3, this.getBounds().height/4, x4, this.getBounds().height/2 ); /* edges */
                g.drawLine ( x3, 3*this.getBounds().height/4, x4, this.getBounds().height/2 );
            }

        g.setColor ( Color.black );

        if ( (x4-x1) > 7 )
            g.drawString ( ""+value, xtext, ytext-1 );
    }

    /**
     * Get the header bounds
     * @return the bounds of the header in the chronogram (name + type)
     */
    public Rectangle getHeaderBounds ( )
    {
        return this.header_bounds;
    }

    /**
     * Set the header end coordinate
     * @param x the new header end coordinate
     * @param width the width of the component
     */
    public static void setHeaderEndAt ( int x, int width )
    {
        header_end = x;
        time_width = width - header_end - 20;
        time_ratio = ((double)time_width) / ((double)time_end);
    }

    /**
     * Set the end of the time
     * @param t the new end of the time
     * @param width the width of the component
     */
    public static void setEndOfTimeAt ( int t )
    {
        time_end = t;
        time_ratio = ((double)time_width) / ((double)time_end);
    }

    /**
     * Add a change in the chronogram
     * @param time the time offset of the change
     * @param value the new value of the node
     * @pre time>0
     */
    public void addChange ( int time, int value )
    {
        addChange ( time, value, true );
    }

    /**
     * Add a change in the chronogram
     * @param time the time offset of the change
     * @param value the new value of the node
     * @param clean do we clean the changes record ?
     * @pre time>0
     */
    public void addChange ( int time, int value, boolean clean )
    {
        int i;

        if ( time <= 0 )
            {
                this.changes.get(0).value = value;

                if ( clean )
                    this.cleanChangesRecord ( );

                return;
            }

        for ( i=1; i<this.changes.size(); ++i )
            if ( this.changes.get(i).time > time )
                break;

        if ( time == this.changes.get(i-1).time )
            this.changes.get(i-1).value = value;
        else
            this.changes.add ( i, new ChronogramChange(time,value) );

        if ( clean )
            this.cleanChangesRecord ( );
    }

    /**
     * Clean up the changes record by removing invalid changes
     */
    public void cleanChangesRecord ( )
    {
        int i;

        for ( i=1; i<this.changes.size(); ++i )
            {
                if ( this.changes.get(i).value == this.changes.get(i-1).value )
                    {
                        /* System.out.println ( "removing (clean) change at " + changes.get(i).time + " with val=" + changes.get(i).value ); */
                        this.changes.remove ( i );
                        --i;
                    }
            }
    }

    /**
     * Clear the changes record by removing changes after the initial value
     */
    public void clearChangesRecord ( )
    {
        ChronogramChange cc = this.changes.get ( 0 );
        this.changes.clear ( );
        this.changes.add ( cc );
    }

    /**
     * Get the time at the specified position
     * @param x the x coord of the position
     * @return the time at current position
     */
    private int getTimeAt ( int x )
    {
        return ( (int) ((x-10-header_end) / time_ratio) );
    }

    /**
     * Get the value at the specified time
     * @param time the time index
     * @return the value
     */
    private int getValueAt ( int time )
    {
        int i;

        if ( ( time < 0 ) || ( time > time_end ) )
            return 0;

        int v = this.changes.get(0).value;

        for ( i=0; i<this.changes.size(); ++i )
            {
                if ( this.changes.get(i).time > time )
                    break;

                v = this.changes.get(i).value;
            }

        return v;
    }

     /**
     * Set the value on the whole chronogram
     * @param value the new value
     */
    private void setValueAt ( int value )
    {
        setValueAt ( value, 0, time_end );
    }

    /**
     * Set the value at the specified time
     * @param value the new value
     * @param start the start time index
     * @param end the end time index
     */
    private void setValueAt ( int value, int start, int end )
    {
        setValueAt ( value, start, end, true );
    }

    /**
     * Set the value at the specified time
     * @param value the new value
     * @param start the start time index
     * @param end the end time index
     * @param clean do we clean the changes record ?
     */
    private void setValueAt ( int value, int start, int end, boolean clean )
    {
        boolean ending = false;

        if ( end >= time_end )
            ending = true;

        if ( ((IONode)node).getBitsCount() == 1 )
            {
                if ( value <= 0 )
                    value = 0;
                else
                    value = 1;
            }

        int i;
        int last_value = changes.get(0).value;

        for ( i=1; i<changes.size(); ++i )
            {
                if ( changes.get(i).time >= start )
                    break;
                /* System.out.println ( "browsing after " + changes.get(i).time + " with val=" + changes.get(i).value ); */
                last_value = changes.get(i).value;
            }

        int j;
        for ( j=i; j<changes.size(); ++j )
            {
                if ( changes.get(j).time == end )
                    last_value = changes.get(j).value;

                if ( changes.get(j).time >= end )
                    break;

                if ( ( changes.get(j).time >= start ) && ( changes.get(j).time < end ) )
                    {
                        last_value = changes.get(j).value;
                        /* System.out.println ( "removing (setval) change at " + changes.get(j).time + " with val=" + last_value ); */
                        changes.remove ( j );
                        --j;
                    }
            }

        this.addChange ( start, value, false );

        if ( !ending )
            {
                this.addChange ( end, last_value, clean );
                /* System.out.println ( "adding end change at " + end + " with val=" + last_value ); */
            }

        if ( clean )
            this.cleanChangesRecord ( );
    }

    /**
     * Reset the selection of the chronogram
     */
    public void resetSelection ( )
    {
        this.selectionXstart = null;
        this.selectionXend = null;
    }

    /**
     * Node accessor
     * @return the node attached to this chronogram
     */
    public Node getNode ( )
    {
        return this.node;
    }

    /**
     * Callback for mouse released events
     * @param e the MouseEvent object
     */
    public void mouseReleased ( MouseEvent e )
    {
        if ( e.getButton() == MouseEvent.BUTTON1 ) /* play with selection for first button clicks */
            {
                if ( this.selectionXstart != null )
                    {
                        if ( ( e.getX() >= (header_end+10) ) && ( e.getX() <= (header_end+10+time_width) ) )
                            this.selectionXend = e.getX();
                        else if ( e.getX() < (header_end+10) )
                            {
                                this.selectionXend = (header_end+10);
                                this.cursorX = null;
                            }
                        else if ( e.getX() > (header_end+10+time_width) )
                            {
                                this.selectionXend = (header_end+10+time_width);
                                this.cursorX = null;
                            }

                        if ( ( e.getY() <= 0 ) || ( e.getY() >= this.getBounds().height ) )
                            this.cursorX = null;

                        if ( this.selectionXstart > this.selectionXend )
                            {
                                Integer t = this.selectionXend;
                                this.selectionXend = this.selectionXstart;
                                this.selectionXstart = t;
                            }
                    }
                else
                    this.resetSelection ( );
            }
        else if ( e.getButton() == MouseEvent.BUTTON3 ) /* Open menu for third button clicks */
            {
                if ( ( this.selectionXstart != null ) && ( this.selectionXend != null ) )
                    if ( ( e.getX() < this.selectionXstart ) || ( e.getX() > this.selectionXend ) )
                        this.resetSelection ( );

                this.menu.show ( e.getX(), e.getY() );
            }

        this.repaint ( );
    }

    /**
     * Callback for mouse pressed events
     * @param e the MouseEvent object
     */
    public void mousePressed ( MouseEvent e )
    {
        if ( e.getButton() != MouseEvent.BUTTON3 )
            {
                if ( ( e.getX() >= (header_end+10) ) && ( e.getX() <= (header_end+10+time_width) ) )
                    {
                        this.selectionXstart = e.getX ( );
                        this.selectionXend = null;
                    }
                else
                    {
                        this.resetSelection ( );
                    }
            }

        this.repaint ( );
    }

    /**
     * Callback for mouse clicked events
     * @param e the MouseEvent object
     */
    public void mouseClicked ( MouseEvent e )
    {
    }

    /**
     * Callback for mouse exited events
     * @param e the MouseEvent object
     */
    public void mouseExited ( MouseEvent e )
    {
        this.cursorX = null;
        this.repaint ( );
    }

    /**
     * Callback for mouse entered events
     * @param e the MouseEvent object
     */
    public void mouseEntered ( MouseEvent e )
    {
        this.repaint ( );
    }

    /**
     * Callback for mouse dragged events
     * @param e the MouseEvent object
     */
    public void mouseDragged ( MouseEvent e )
    {
        if ( ( e.getX() >= (header_end+10) ) && ( e.getX() <= (header_end+10+time_width) ) )
            this.cursorX = e.getX ( );
        else if ( e.getX() < (header_end+10) )
            this.cursorX = (header_end+10);
        else
            this.cursorX = (header_end+10+time_width);

        int time = getTimeAt ( this.cursorX );
        this.owner.setLabelInfo ( time, getValueAt(time) );

        this.repaint ( );
    }

    /**
     * Callback for mouse moved events
     * @param e the MouseEvent object
     */
    public void mouseMoved ( MouseEvent e )
    {
        if ( ( e.getX() >= (header_end+10) ) && ( e.getX() <= (header_end+10+time_width) ) )
            {
                this.cursorX = e.getX ( );
                int time = getTimeAt ( e.getX() );
                this.owner.setLabelInfo ( time, getValueAt(time) );
            }
        else
            this.cursorX = null;

        this.repaint ( );
    }

    /**
     * Callback for action performed notices
     * @param e the ActionEvent object
     */
    public void actionPerformed ( ActionEvent e )
    {
        if ( "select all".equals ( e.getActionCommand() ) )
            {
                this.selectionXstart = header_end + 10;
                this.selectionXend = header_end + 10 + time_width;
            }
        else if ( "set value".equals ( e.getActionCommand() ) )
            {
                new SetValueDialog ( "Chronogram of \'"+((Nameable)node).getNodeName()+"\'", this );
            }
        else if ( "set clock".equals ( e.getActionCommand() ) )
            {
                new SetClockDialog ( "Chronogram of \'"+((Nameable)node).getNodeName()+"\'", this );
            }

        this.repaint ( );
    }

    /**
     * Callback when the canvas is hidden
     * @param e the ComponentEvent object
     */
    public void componentHidden ( ComponentEvent e )
    {
    }

    /**
     * Callback when the canvas is moved
     * @param e the ComponentEvent object
     */
    public void componentMoved ( ComponentEvent e )
    {
    }

    /**
     * Callback when the canvas is resized
     * @param e the ComponentEvent object
     */
    public void componentResized ( ComponentEvent e )
    {
        ((CanvasLayout)this.getLayout()).setSize ( this.getBounds().width, this.getBounds().height );
        time_width = this.getBounds().width - header_end - 16;
    }

    /**
     * Callback when the canvas is shown
     * @param e the ComponentEvent object
     */
    public void componentShown ( ComponentEvent e )
    {
    }

    /**
     * Get color for low values
     * @return the color for low values
     */
    public abstract Color getColorLow ( );

    /**
     * Get color for high values
     * @return the color for high values
     */
    public abstract Color getColorHigh ( );

    /**
     * Inner-class for chronogram change storage
     * A chronogram change occur when the value of the node changes
     */
    protected class ChronogramChange
    {
        protected int time;
        protected int value;

        /**
         * Default constructor
         * @param time the time offset of the change in the chronogram
         * @param value the new value of the node
         */
        public ChronogramChange ( int time, int value )
        {
            this.time = time;
            this.value = value;
        }

        /**
         * Readable representation of the chronogram change
         * @return a string
         */
        public String toString ( )
        {
            return new String ( "<time=" + time + ",value=" + value + ">" );
        }
    }

    /**
     * ChronogramMenu is the popup menu displayed when the user clicks with button3
     */
    protected class ChronogramMenu
    {
        private Chronogram owner;

        private JPopupMenu menu;
        private JMenuItem menuSetValue;
        private JMenuItem menuSetClock;
        private JMenuItem menuSelectAll;

        /**
         * Default constructor
         * @param owner the parent chronogram
         */
        public ChronogramMenu ( Chronogram owner )
        {
            this.owner = owner;

            menu = new JPopupMenu ( "actions" );

            menuSetValue = new JMenuItem ( "Set Value", KeyEvent.VK_V );
            menuSetValue.addActionListener ( owner );
            menuSetValue.setActionCommand ( "set value" );
            menu.add ( menuSetValue );

            menuSetClock = new JMenuItem ( "Set Clock", KeyEvent.VK_C );
            menuSetClock.addActionListener ( owner );
            menuSetClock.setActionCommand ( "set clock" );
            menu.add ( menuSetClock );

            menu.addSeparator ( );

            menuSelectAll = new JMenuItem ( "Select All", KeyEvent.VK_A );
            menuSelectAll.addActionListener ( owner );
            menuSelectAll.setActionCommand ( "select all" );
            menu.add ( menuSelectAll );

            if ( type == Chronogram.OUT )
                {
                    menuSetValue.setEnabled ( false );
                    menuSetClock.setEnabled ( false );
                }
        }

        /**
         * Show the menu at specified coords
         * @param x x coord
         * @param y y coord
         */
        public void show ( int x, int y )
        {
            this.menu.show ( this.owner, x, y );
        }

        /**
         * Hide the menu
         */
        public void hide ( )
        {
            this.menu.setVisible ( false );
            owner.resetSelection ( );
            owner.repaint ( );
        }
    }

    /**
     * Dialog for setting values
     */
    protected class SetValueDialog extends JDialog implements ActionListener, WindowListener
    {
        private static final long serialVersionUID = 0L;

        private Chronogram owner;

        private JTextField value;

        /**
         * Default constructor
         * @param title the title of the dialog
         * @param owner the frame that owns the dialog
         */
        public SetValueDialog ( String title, Chronogram owner )
        {
            super ( (JDialog) owner.owner, true );

            this.owner = owner;
            this.setTitle ( title );

            if ( ( selectionXstart != null ) && ( selectionXend != null ) )
                {
                    if ( (selectionXend-selectionXstart) < 2 )
                        {
                            selectionXstart = null;
                            selectionXend = null;
                        }
                    else if ( (selectionXend-selectionXstart) < 24 )
                        {
                            JOptionPane.showMessageDialog ( this, "Unable set value.\nSelection is too small.", "Error", JOptionPane.ERROR_MESSAGE );
                            this.dispose ( );
                            return;
                        }
                }

            this.addWindowListener ( this );
            this.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );
            this.setLayout ( new BorderLayout ( ) );

            JPanel p = new JPanel ( );
            if ( ( selectionXstart != null ) && ( selectionXend != null ) )
                p.add ( new JLabel ( "Setting value from " + getTimeAt(selectionXstart) + " to " + getTimeAt(selectionXend) + "" ) );
            else
                p.add ( new JLabel ( "Setting value from 0 to " + time_end ) );
            this.add ( p, BorderLayout.NORTH );

            this.value = new JTextField ( 20 );

            if ( ( selectionXstart != null ) && ( selectionXend != null ) )
                this.value.setText ( ""+getValueAt( getTimeAt(selectionXstart)) );
            else
                this.value.setText ( "0" );

            p = new JPanel ( );
            p.add ( this.value );
            this.add ( p, BorderLayout.CENTER );

            p = new JPanel ( );
            ((FlowLayout) p.getLayout()).setHgap ( 12 );
            JButton btn = new JButton ( "OK", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/ok.png", "ok") );
            btn.addActionListener ( this );
            btn.setActionCommand ( "ok" );
            p.add ( btn );
            btn = new JButton ( "Cancel", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/cancel.png", "cancel") );
            btn.addActionListener ( this );
            btn.setActionCommand ( "cancel" );
            p.add ( btn );
            this.add ( p, BorderLayout.SOUTH );

            this.pack ( );
            this.setResizable ( false );
            this.setSize ( this.getSize().width+20, this.getSize().height );
            this.setLocationRelativeTo ( this.owner.owner );
            this.setVisible ( true );
        }

        /**
         * Callback for action performed events
         * @param e the ActionEvent object
         */
        public void actionPerformed ( ActionEvent e )
        {
            if ( "cancel".equals ( e.getActionCommand() ) )
                this.cancel ( );
            else if ( "ok".equals ( e.getActionCommand() ) )
                {
                    int v = 0;

                    try
                        {
                            v = new Integer ( this.value.getText() );
                        }

                    catch ( Exception exc )
                        {
                        }

                    if ( ( selectionXstart != null ) && ( selectionXend != null ) )
                        setValueAt ( v, getTimeAt(selectionXstart), getTimeAt(selectionXend) );
                    else
                        setValueAt ( v );

                    this.setVisible ( false );
                }
        }

        /**
         * Callback for window deactivated event
         * @param e the WindowEvent object
         */
        public void windowDeactivated ( WindowEvent e )
        {
        }

        /**
         * Callback for window activated event
         * @param e the WindowEvent object
         */
        public void windowActivated ( WindowEvent e )
        {
        }

        /**
         * Callback for window closed event
         * @param e the WindowEvent object
         */
        public void windowClosed ( WindowEvent e )
        {
        }

        /**
         * Callback for window closing event
         * @param e the WindowEvent object
         */
        public void windowClosing ( WindowEvent e )
        {
            this.cancel ( );
        }

        /**
         * Callback for window opened event
         * @param e the WindowEvent object
         */
        public void windowOpened ( WindowEvent e )
        {
        }

        /**
         * Callback for window iconified event
         * @param e the WindowEvent object
         */
        public void windowIconified ( WindowEvent e )
        {
        }

        /**
         * Callback for window deiconified event
         * @param e the WindowEvent object
         */
        public void windowDeiconified ( WindowEvent e )
        {
        }

        /**
         * Close the window without applying modifications
         */
        private void cancel ( )
        {
            this.setVisible ( false );
            this.dispose ( );
        }
    }

    /**
     * Dialog for setting clocks
     */
    protected class SetClockDialog extends JDialog implements ActionListener, WindowListener
    {
        private static final long serialVersionUID = 0L;

        private Chronogram owner;

        private JTextField period;

        public static final int MIN_PERIOD = 15;

        /**
         * Default constructor
         * @param title the title of the dialog
         * @param owner the frame that owns the dialog
         */
        public SetClockDialog ( String title, Chronogram owner )
        {
            super ( (JDialog) owner.owner, true );

            this.owner = owner;
            this.setTitle ( title );

            if ( ( selectionXstart != null ) && ( selectionXend != null ) )
                {
                    if ( (selectionXend-selectionXstart) < 2 )
                        {
                            selectionXstart = null;
                            selectionXend = null;
                        }
                    else if ( (selectionXend-selectionXstart) < 20 )
                        {
                            JOptionPane.showMessageDialog ( this, "Unable set clock.\nSelection is too small.", "Error", JOptionPane.ERROR_MESSAGE );
                            this.dispose ( );
                            return;
                        }
                }

            this.addWindowListener ( this );
            this.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );
            this.setLayout ( new BorderLayout ( ) );

            JPanel p = new JPanel ( );
            if ( ( selectionXstart != null ) && ( selectionXend != null ) )
                p.add ( new JLabel ( "Setting clock from " + getTimeAt(selectionXstart) + " to " + getTimeAt(selectionXend) + "" ) );
            else
                p.add ( new JLabel ( "Setting clock from 0 to " + time_end ) );
            this.add ( p, BorderLayout.NORTH );

            this.period = new JTextField ( 20 );
            this.period.setText ( ""+((int)(MIN_PERIOD/time_ratio)) );
            p = new JPanel ( );
            p.add ( this.period );
            this.add ( p, BorderLayout.CENTER );

            p = new JPanel ( );
            ((FlowLayout) p.getLayout()).setHgap ( 12 );
            JButton btn = new JButton ( "OK", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/ok.png", "ok") );
            btn.addActionListener ( this );
            btn.setActionCommand ( "ok" );
            p.add ( btn );
            btn = new JButton ( "Cancel", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/cancel.png", "cancel") );
            btn.addActionListener ( this );
            btn.setActionCommand ( "cancel" );
            p.add ( btn );
            this.add ( p, BorderLayout.SOUTH );

            this.pack ( );
            this.setResizable ( false );
            this.setSize ( this.getSize().width+20, this.getSize().height );
            this.setLocationRelativeTo ( this.owner.owner );
            this.setVisible ( true );
        }

        /**
         * Callback for action performed events
         * @param e the ActionEvent object
         */
        public void actionPerformed ( ActionEvent e )
        {
            if ( "cancel".equals ( e.getActionCommand() ) )
                this.cancel ( );
            else if ( "ok".equals ( e.getActionCommand() ) )
                {
                    int p = 0;

                    try
                        {
                            p = new Integer ( this.period.getText() );
                        }

                    catch ( Exception exc )
                        {
                        }

                    if ( p < ((int)(MIN_PERIOD/time_ratio)) )
                        {
                            JOptionPane.showMessageDialog ( this, "Unable set clock.\nPeriod is too small.", "Error", JOptionPane.ERROR_MESSAGE );
                            return;
                        }

                    int i;
                    int v = 0;
                    int t1, t2;

                    if ( ( selectionXstart != null ) && ( selectionXend != null ) )
                        {
                            t1 = getTimeAt(selectionXstart);
                            t2 = getTimeAt(selectionXend);
                        }
                    else
                        {
                            t1 = 0;
                            t2 = time_end;
                            clearChangesRecord ( );
                        }

                    for ( i=t1; i<=t2; i+=p )
                        {
                            if ( (i+p) > t2 )
                                setValueAt ( v, i, t2, false );
                            else
                                setValueAt ( v, i, i+p, false );

                            v = 1 - v;
                        }

                    cleanChangesRecord ( );

                    this.setVisible ( false );
                }
        }

        /**
         * Callback for window deactivated event
         * @param e the WindowEvent object
         */
        public void windowDeactivated ( WindowEvent e )
        {
        }

        /**
         * Callback for window activated event
         * @param e the WindowEvent object
         */
        public void windowActivated ( WindowEvent e )
        {
        }

        /**
         * Callback for window closed event
         * @param e the WindowEvent object
         */
        public void windowClosed ( WindowEvent e )
        {
        }

        /**
         * Callback for window closing event
         * @param e the WindowEvent object
         */
        public void windowClosing ( WindowEvent e )
        {
            this.cancel ( );
        }

        /**
         * Callback for window opened event
         * @param e the WindowEvent object
         */
        public void windowOpened ( WindowEvent e )
        {
        }

        /**
         * Callback for window iconified event
         * @param e the WindowEvent object
         */
        public void windowIconified ( WindowEvent e )
        {
        }

        /**
         * Callback for window deiconified event
         * @param e the WindowEvent object
         */
        public void windowDeiconified ( WindowEvent e )
        {
        }

        /**
         * Close the window without applying modifications
         */
        private void cancel ( )
        {
            this.setVisible ( false );
            this.dispose ( );
        }
    }
}

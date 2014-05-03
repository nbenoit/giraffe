/*
 * $RCSfile: Canvas.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.50 $
 */

package giraffe.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.* ;

/**
 * Canvas allows the user to display and edit circuits.
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, ComponentListener
{
    private static final long serialVersionUID = 0L;

    private CircuitUI circuit;
    private Iterator<Node> iterNodes;
    private Iterator<Link> iterLinks;

    private HashSet<Selectable> selected;
    private Iterator<Selectable> iterSelected;

    private boolean linking;
    private Anchor currentLinkOrig = null;
    private Anchor currentLinkAdditional = null;
    private Link currentLink = null;

    private boolean dragging;
    private boolean dragged;

    private Rectangle selectionRect;

    private giraffe.ui.Frame parent;

    private boolean changed;
    private boolean saved;

    private String file_name=null;

    private CanvasMenu menu;

    public static final int GRID_SIZE = 15;

    /**
     * Default constructor for an empty canvas
     * @param parent the giraffe.ui.Frame owning the canvas
     * @param title the title of the circuit
     */
    public Canvas ( giraffe.ui.Frame parent, String title )
    {
        super ( new CanvasLayout() );

        this.parent = parent;
        this.setVisible ( true );
        this.setBackground ( Color.white );
        this.setAutoscrolls ( true );
        this.addMouseListener ( this );
        this.addMouseMotionListener ( this );
        this.addComponentListener ( this );

        this.circuit = new CircuitUI ( title );
        this.selected = new HashSet<Selectable> ( );

        this.selectionRect = null;

        this.linking = false;
        this.dragging = false;
        this.dragged = false;

        this.changed = false;

        this.menu = new CanvasMenu ( this );

        ((CanvasLayout)this.getLayout()).setSize ( this.getBounds().width, this.getBounds().height );
    }

    /**
     * XML Loading Constructor
     * @param parent the giraffe.ui.Frame owning the canvas
     * @param file the file that contains the XML description of the circuit
     * @throws CircuitLoadingException if the circuit can not be loaded
     */
    public Canvas ( giraffe.ui.Frame parent, File file ) throws CircuitLoadingException
    {
        super ( new CanvasLayout() );

        this.parent = parent;
        this.setVisible ( true );
        this.setBackground ( Color.white );
        this.setAutoscrolls ( true );
        this.addMouseListener ( this );
        this.addMouseMotionListener ( this );
        this.addComponentListener ( this );

        this.circuit = new CircuitUI ( file, parent.getGraphics() );
        this.selected = new HashSet<Selectable> ( );

        this.selectionRect = null;

        this.linking = false;
        this.dragging = false;
        this.dragged = false;

        this.changed = false;
        this.saved = true;

        this.menu = new CanvasMenu ( this );

        this.file_name = file.getAbsolutePath ( );

        ((CanvasLayout)this.getLayout()).setSize ( this.getBounds().width, this.getBounds().height );
    }

    /**
     * Paint the canvas and all its subcomponents
     * @param g a valid graphics object
     */
    public void paintComponent ( Graphics g )
    {
        super.paintComponent ( g );

        if ( this.parent.isGridEnabled() )
            {
                g.setColor ( Color.lightGray );

                for ( int x=Canvas.GRID_SIZE; x<this.getBounds().width; x+=Canvas.GRID_SIZE )
                    g.drawLine ( x, 0, x, this.getBounds().height );

                for ( int y=Canvas.GRID_SIZE; y<this.getBounds().width; y+=Canvas.GRID_SIZE )
                    g.drawLine ( 0, y, this.getBounds().width, y );
            }

        Node r;

        for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
            {
                r = iterNodes.next ( );

                if ( !r.isSelected() )
                    r.paint ( g );
            }

        for ( iterSelected=this.selected.iterator(); iterSelected.hasNext(); )
            iterSelected.next().paint ( g );

        for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
            iterLinks.next().paint ( g );

        if ( this.currentLink != null )
            this.currentLink.paint ( g );

        Point p = getMousePosition ( );

        if ( ( p != null ) && ( this.parent.getTool() == Frame.TOOL_ADD ) )
            {
                Node n = this.parent.getNode ( );

                if ( n != null )
                    {
                        final int x = p.x-(n.getBounds().width/2);
                        final int y = p.y-(n.getBounds().height/2);

                        if ( this.parent.isGridEnabled() )
                            n.setLocation ( x - (x%Canvas.GRID_SIZE), y - (y%Canvas.GRID_SIZE) );
                        else
                            n.setLocation ( x, y );

                        n.paint ( g );
                    }
            }

        if ( this.selectionRect != null )
            {
                Rectangle c = this.makeRegularRect ( this.selectionRect );

                g.setColor ( Color.black );
                g.drawRect ( c.x, c.y, c.width, c.height );
            }
    }

    /**
     * Callback for mouse clicked events
     * @param e the MouseEvent object
     */
    public void mouseClicked ( MouseEvent e )
    {
        /* bypassed by mousePressed for simple clicks */

        if ( ( e.getClickCount() > 1 ) && ( this.parent.getTool() == Frame.TOOL_MOUSE ) && ( e.getButton() == MouseEvent.BUTTON1 ) )
            {
                Node r;
                boolean done = false;
                boolean changed = false;

                for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
                    {
                        r = iterNodes.next ( );

                        if ( r.contains ( e.getX(), e.getY() ) )
                            {
                                if ( r instanceof giraffe.ui.Dialogable )
                                    {
                                        ((Dialogable)r).openDialog ( this.parent );
                                        done = true;
                                        changed = true;
                                    }
                                else if ( r instanceof giraffe.ui.CompositeNode )
                                    {
                                        this.parent.openCircuit ( new File ( ((CompositeNode)r).getCircuitPathName()+
                                                                             "/"+((CompositeNode)r).getCircuitFileName() ) );
                                        done = true;
                                    }
                            }

                        if ( done )
                            {
                                this.repaint ( );

                                if ( changed )
                                    this.setChanged ( );

                                break;
                            }
                    }
            }
    }

    /**
     * Callback for mouse pressed events
     * @param e the MouseEvent object
     */
    public void mousePressed ( MouseEvent e )
    {
        Node r;
        Link l;

        if ( this.parent.getTool() == Frame.TOOL_MOUSE )
            {
                boolean done = false;
                boolean drag_notselection = false; /* drag an element that is being selected at the same time */
                boolean drag_selection = false; /* drag selected elements */
                Vector<Node> dragged = new Vector<Node> ( );

                for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
                    {
                        r = iterNodes.next ( );

                        if ( r.contains ( e.getX(), e.getY() ) )
                            {
                                this.currentLinkOrig = r.updateAnchorsState ( e.getX(), e.getY(), null );

                                if ( this.currentLinkOrig != null )
                                    {
                                        r.startLinking ( );
                                        r.select ( );
                                        this.selected.add ( r );
                                        this.currentLink = new Link ( this.currentLinkOrig, this.circuit.incLinkID() );
                                        this.currentLink.setAdditional ( new Point ( e.getX(),e.getY() ) );
                                        this.linking = true;
                                        dragged.clear ( );
                                        drag_notselection = false;

                                        for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
                                            {
                                                Link link = iterLinks.next ( );
                                                link.unframe ( );

                                                if ( link.isSelected() )
                                                    {
                                                        link.unselect ( );
                                                        this.selected.remove ( link );
                                                    }
                                            }
                                    }
                                else
                                    {
                                        if ( ( !r.isSelected() ) && ( !this.linking ) ) /* element selection and dragging */
                                            {
                                                if ( !e.isControlDown() )
                                                    {
                                                        drag_notselection = true;
                                                        drag_selection = false;
                                                        dragged.clear ( );
                                                    }
                                                else
                                                    {
                                                        drag_selection = true;
                                                        r.select ( );
                                                        this.selected.add ( r );
                                                    }

                                                dragged.add ( r );
                                            }
                                        else if ( ( r.isSelected() ) && ( !this.linking ) )
                                            {
                                                if ( !e.isControlDown() )
                                                    drag_selection = true;
                                                else
                                                    {
                                                        r.unselect ( );
                                                        this.selected.remove ( r );
                                                        drag_selection = true;
                                                    }
                                            }
                                    }

                                done = true;
                            }

                        if ( ( r.isSelected() ) && ( !drag_notselection ) && ( !this.linking ) )
                            dragged.add ( r );
                    }

                if ( dragged.size() != 0 )
                    {
                        if ( drag_selection )
                            {
                                this.dragging = true;
                                for ( iterNodes=dragged.iterator(); iterNodes.hasNext(); )
                                    iterNodes.next().startDragging ( e.getX(), e.getY() );
                            }
                        else if ( drag_notselection )
                            {
                                r = dragged.iterator().next ( );
                                r.startDragging ( e.getX(), e.getY() );
                                unselectAll ( );
                                r.select ( );
                                this.selected.add ( r );
                                this.dragging = true;
                            }
                    }

                if ( !done )
                    {
                        if ( !e.isControlDown() )
                            unselectAll ( );

                        for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
                            {
                                l = iterLinks.next ( );

                                if ( l.contains ( e.getX(), e.getY() ) )
                                    {
                                        this.currentLinkOrig = null;
                                        this.currentLink = l;
                                        this.linking = true;
                                        done = true;

                                        if ( !e.isControlDown() )
                                            {
                                                l.select ( );
                                                this.selected.add ( l );
                                            }
                                    }
                            }
                    }

                if ( ! done )
                    {
                        this.selectionRect = new Rectangle ( e.getX(), e.getY(), 1, 1 );
                    }
            }

        this.repaint ( );
    }

    /**
     * Callback for mouse released events
     * @param e the MouseEvent object
     */
    public void mouseReleased ( MouseEvent e )
    {
        Node r;
        Link l;

        if ( this.parent.getTool() == Frame.TOOL_MOUSE )
            {
                if ( this.linking )
                    {
                        this.linking = false;

                        if ( ( this.currentLinkAdditional != null ) && ( this.currentLinkOrig != null ) )
                            {
                                if ( !( ( this.currentLinkOrig.getParent() == this.currentLinkAdditional.getParent() ) &&
                                        ( this.currentLinkOrig.getType() == this.currentLinkAdditional.getType()) ) )
                                    {   /* adds new link */
                                        this.currentLink.addLinkedAnchor ( this.currentLinkAdditional ); /* additional resets to null */
                                        this.circuit.addLink ( this.currentLink );
                                        this.currentLinkOrig.addLink ( this.currentLink ); /* kept because Link constructor don't do that */
                                        this.currentLinkOrig.getParent().endLinking ( );
                                        this.currentLinkOrig.getParent().unselect ( );
                                        this.selected.remove ( this.currentLinkOrig.getParent() );
                                        this.currentLinkOrig.getParent().unframe ( );
                                        this.currentLinkOrig.getParent().updateAnchorsState ( e.getX(), e.getY(), null );
                                        this.currentLinkAdditional.getParent().unselect ( );
                                        this.currentLinkAdditional.getParent().frame ( );
                                        this.selected.remove ( this.currentLinkAdditional.getParent() );
                                        this.currentLinkAdditional.getParent().updateAnchorsState ( e.getX(), e.getY(), null );
                                        this.setChanged ( );
                                    }
                            }
                        else if ( ( this.currentLinkAdditional != null ) && ( this.currentLinkOrig == null ) )
                            {
                                if ( this.currentLink.addLinkedAnchor(this.currentLinkAdditional) ) /* additional resets to null */
                                    {   /* adds an anchor to currentLink */
                                        this.setChanged ( );
                                    }

                                this.currentLinkAdditional.getParent().unselect ( );
                                this.selected.remove ( this.currentLinkAdditional.getParent() );
                                this.currentLinkAdditional.getParent().updateAnchorsState ( e.getX(), e.getY(), null );
                            }
                        else if ( ( this.currentLinkAdditional == null ) && ( this.currentLinkOrig == null ) )
                            {   /* link selection */
                                if ( !e.isControlDown() )
                                    unselectAll ( );

                                if ( this.currentLink.contains ( e.getX(), e.getY() ) )
                                    this.currentLink.frame ( );
                                else
                                    this.currentLink.unframe ( );

                                if ( e.isControlDown() )
                                    {
                                        if ( this.currentLink.isSelected() )
                                            {
                                                this.selected.remove ( this.currentLink ) ;
                                                this.currentLink.unselect ( );
                                            }
                                        else
                                            {
                                                this.selected.add ( this.currentLink ) ;
                                                this.currentLink.select ( );
                                            }
                                    }
                                else
                                    {
                                        this.selected.add ( this.currentLink ) ;
                                        this.currentLink.select ( );
                                    }

                                this.currentLink.setAdditional ( null );
                            }
                        else
                            {   /* link creation aborted */
                                unselectAll ( );
                                this.currentLink.setAdditional ( null );
                                this.circuit.decLinkID ( );

                                if ( this.currentLinkOrig != null )
                                    {
                                        this.currentLinkOrig.getParent().updateAnchorsState ( e.getX(), e.getY(), null );
                                        this.currentLinkOrig.getParent().endLinking ( );
                                    }
                            }

                        this.currentLink = null;
                        this.currentLinkOrig = null;
                        this.currentLinkAdditional = null;
                    }
                else if ( this.selectionRect != null ) /* selection with a rectangle */
                    {
                        this.unselectAll ( );

                        for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
                            {
                                r = iterNodes.next ( );

                                if ( r.contains ( e.getX(), e.getY() ) )
                                    {
                                        r.frame ( );
                                        r.updateAnchorsState ( e.getX(), e.getY(), null );
                                    }
                                else
                                    {
                                        r.unframe ( );
                                        r.clearAnchorsState ( );
                                    }

                                if ( this.makeRegularRect(this.selectionRect).contains ( r.getBounds() ) )
                                    {
                                        r.select ( );
                                        this.selected.add ( r );
                                    }
                            }

                        for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
                            {
                                l = iterLinks.next ( );

                                if ( l.contains ( e.getX(), e.getY() ) )
                                  l.frame ( );
                                else
                                  l.unframe ( );

                                if ( this.makeRegularRect(this.selectionRect).contains ( l.getBounds() ) )
                                {
                                    l.select ( );
                                    this.selected.add ( l );
                                }
                            }

                        this.selectionRect = null;
                    }
                else if ( this.dragging )
                    {
                        for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
                            {
                                r = iterNodes.next ( );

                                if ( r.isBeingDragged() )
                                    r.endDragging ( e.getX(), e.getY() );
                            }

                        /* no link dragging here */

                        this.dragging = false;

                        if ( this.dragged )
                            {
                                this.setChanged ( );
                                this.dragged = false;
                            }
                    }
            }
        else if ( this.parent.getTool() == Frame.TOOL_ADD )
            this.addNode ( e.getX(), e.getY() );

        this.parent.updateButtonsAndMenu ( );

        /* Open menu for third button clicks */
        if ( ( this.parent.getTool() == Frame.TOOL_MOUSE ) &&  ( e.getButton() == MouseEvent.BUTTON3 ) )
            this.menu.show ( e.getX(), e.getY() );

        this.repaint ( );
    }

    /**
     * Callback for mouse exited events
     * @param e the MouseEvent object
     */
    public void mouseExited ( MouseEvent e )
    {
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
        Node r;
        Link l;

        if ( this.parent.getTool() == Frame.TOOL_MOUSE )
            {
                if ( !this.linking )
                    {
                        if ( this.selectionRect != null ) /* rectangle selection */
                            {
                                this.unselectAll ( );
                                this.selectionRect.setSize ( e.getX()-this.selectionRect.x, e.getY()-this.selectionRect.y );

                                for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
                                    {
                                        r = iterNodes.next ( );

                                        if ( r.contains ( e.getX(), e.getY() ) )
                                            r.frame ( );

                                        if ( this.makeRegularRect(this.selectionRect).contains ( r.getBounds() ) )
                                            {
                                                r.select ( );
                                                r.frame ( );
                                                this.selected.add ( r );
                                            }
                                        else
                                            r.unframe ( );
                                    }

                                for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
                                    {
                                        l = iterLinks.next ( );

                                        if ( l.contains ( e.getX(), e.getY() ) )
                                            l.frame ( );

                                        if ( this.makeRegularRect(this.selectionRect).contains ( l.getBounds() ) )
                                            {
                                                l.select ( );
                                                l.frame ( );
                                                this.selected.add ( l );
                                            }
                                        else
                                            l.unframe ( );
                                    }
                            }
                        else if ( this.dragging )
                            {
                                for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
                                    {
                                        r = iterNodes.next ( );

                                        if ( r.isBeingDragged() )
                                            {
                                                int x = e.getX ( );
                                                int y = e.getY ( );

                                                if ( ( this.parent.isGridEnabled() ) || ( e.isShiftDown() ) )
                                                    {
                                                        x = x - r.getDraggingOffsetX(); /* we must consider the offset for % */
                                                        x = ( x - (x%Canvas.GRID_SIZE) ) + r.getDraggingOffsetX();

                                                        y = y - r.getDraggingOffsetY();
                                                        y = ( y - (y%Canvas.GRID_SIZE) ) + r.getDraggingOffsetY();
                                                    }

                                                r.drag ( x, y );
                                                this.dragged = true;
                                            }
                                    }
                            }
                    }
                else
                    {
                        this.currentLinkAdditional = null;

                        for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
                            {
                                r = iterNodes.next ( );

                                if ( r.contains ( e.getX(), e.getY() ) )
                                    {
                                        r.frame ( );
                                        this.currentLinkAdditional = r.updateAnchorsState ( e.getX(), e.getY(), this.currentLinkOrig );

                                        if ( this.currentLinkAdditional == this.currentLinkOrig )
                                            this.currentLinkAdditional = null;

                                        r.select ( );
                                        this.selected.add ( r );
                                    }
                                else
                                    {
                                        r.updateAnchorsState ( e.getX(), e.getY(), this.currentLinkOrig );

                                        if ( ( r.isSelected() ) && ( !r.hasAnchor(this.currentLinkOrig) ) )
                                            {
                                                r.unselect ( );
                                                r.unframe ( );
                                                this.selected.remove ( r );
                                            }

                                        if ( this.currentLinkOrig != null )
                                            {
                                                if ( r != this.currentLinkOrig.getParent() )
                                                    {
                                                        r.unselect ( );
                                                        this.selected.remove ( r );
                                                    }
                                            }
                                    }
                            }

                        if ( this.currentLink.getAdditional() == null )
                            {
                                this.currentLink.select ( );
                                this.selected.add ( this.currentLink );
                            }

                        this.currentLink.setAdditional ( new Point ( e.getX(),e.getY() ) );
                    }
            }

        this.repaint ( );
    }

    /**
     * Callback for mouse moved events
     * @param e the MouseEvent object
     */
    public void mouseMoved ( MouseEvent e )
    {
        Node r;
        Link l;

        for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
            {
                r = iterNodes.next ( );

                if ( r.contains ( e.getX(), e.getY() ) )
                    {
                        r.frame ( );
                        r.updateAnchorsState ( e.getX(), e.getY(), null );
                    }
                else
                    {
                        r.unframe ( );
                        r.clearAnchorsState ( );
                    }
            }

        for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
            {
                l = iterLinks.next ( );

                if ( l.contains ( e.getX(), e.getY() ) )
                    l.frame ( );
                else
                    l.unframe ( );
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
    }

    /**
     * Callback when the canvas is shown
     * @param e the ComponentEvent object
     */
    public void componentShown ( ComponentEvent e )
    {
    }

    /**
     * Unselect all the components of the canvas
     */
    public void unselectAll ( )
    {
        for ( iterSelected=this.selected.iterator(); iterSelected.hasNext(); )
            iterSelected.next().unselect ( );

        this.selected.clear ( );
    }

    /**
     * Select all the components of the canvas
     */
    public void selectAll ( )
    {
        this.selected.clear ( );

        for ( iterNodes=this.circuit.getNodes().iterator(); iterNodes.hasNext(); )
            {
                Node n = iterNodes.next ( );
                n.select ( );
                this.selected.add ( n );
            }

        for ( iterLinks=this.circuit.getLinks().iterator(); iterLinks.hasNext(); )
            {
                Link l = iterLinks.next ( );
                l.select ( );
                this.selected.add ( l );
            }
    }

    /**
     * Unframe all the components of the canvas
     */
    public void unframeAll ( )
    {
        for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
            iterNodes.next().unframe ( );

        for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
            iterLinks.next().unframe ( );
    }

    /**
     * Add a node to the canvas. The parent Frame tell us which node to add.
     * @param x X coord of the new node
     * @param y Y coord of the new node
     */
    public void addNode ( int x, int y )
    {
        Node r = this.parent.getNode ( );

        if ( r != null )
            {
                Node g = ((Node)r).copy ( this.getGraphics() );

                if ( g != null )
                    {
                        x = ( x-(r.getBounds().width/2) );
                        y = ( y-(r.getBounds().height/2) );

                        if ( parent.isGridEnabled() )
                            {
                                x = x - ( x % Canvas.GRID_SIZE );
                                y = y - ( y % Canvas.GRID_SIZE );
                            }

                        g.setLocation ( x, y );

                        this.circuit.addNode ( g );
                        this.setChanged ( );
                    }
                else
                    JOptionPane.showMessageDialog ( this, "Unable to add node.", "Error", JOptionPane.ERROR_MESSAGE );
            }

        for ( iterNodes=this.circuit.getNodesIterator(); iterNodes.hasNext(); )
            {
                r = iterNodes.next ( );

                if ( r.contains ( x, y ) )
                    {
                        r.frame ( );
                        r.updateAnchorsState ( x, y, null );
                    }
                else
                    {
                        r.unframe ( );
                        r.clearAnchorsState ( );
                    }
            }
    }

    /**
     * Cut the selection and put it into the provided hash set.
     * @param clipboard an hashset to cut the components into
     */
    public void cutTo ( HashSet<Selectable> clipboard )
    {
        this.copyTo ( clipboard );
        this.removeSelection ( );
        this.repaint ( );
    }

    /**
     * Copy the selection and put the result into the provided hash set.
     * @param clipboard an hashset to copy the components into
     */
    public void copyTo ( HashSet<Selectable> clipboard )
    {
        Vector<Link> links = new Vector<Link> ( );
        Iterator<Anchor> iterAnchors;
        Hashtable<Node,Node> nodes = new Hashtable<Node,Node> ( );

        for ( iterSelected=this.selected.iterator(); iterSelected.hasNext(); )
            {
                Selectable s = iterSelected.next ( );

                if ( s instanceof Node )
                    {
                        Node g = ((Node)s).copy ( this.getGraphics() );

                        if ( g != null )
                            {
                                clipboard.add ( g );
                                nodes.put ( (Node)s, g );
                            }
                        else
                            JOptionPane.showMessageDialog ( this, "Unable to copy one of the nodes.", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                else if ( s instanceof Link )
                    {
                        boolean valid = true;
                        iterAnchors = ((Link)s).getAnchorsIterator ( );
                        while ( (iterAnchors.hasNext()) && (valid) )
                            {
                                valid &= this.selected.contains ( iterAnchors.next().getParent() );
                            }

                        if ( valid )
                            links.add ( (Link) s );
                    }
            }

        /* we copy the valid links and update the anchors */
        for ( iterLinks=links.iterator(); iterLinks.hasNext(); )
            {
                Anchor a;
                Link l = iterLinks.next ( );
                iterAnchors = l.getAnchorsIterator ( );
                Link lcpy = new Link ( l.getID() );

                while ( iterAnchors.hasNext() )
                    {
                       a = iterAnchors.next ( );
                       lcpy.addLinkedAnchor ( (nodes.get(a.getParent())).getAnchor(a.getID()) );
                    }

                clipboard.add ( lcpy );
            }
    }

    /**
     * Paste the components contained in clipboard at the mouse cursor position or at (4,4) if the cursor is outside canvas.
     * @param clipboard an hashset to paste the components from
     */
    public void paste ( HashSet<Selectable> clipboard )
    {
        Vector<Link> links = new Vector<Link> ( );
        Iterator<Anchor> iterAnchors;
        Hashtable<Node,Node> nodes = new Hashtable<Node,Node> ( );

        this.unselectAll ( );

        for ( iterSelected=clipboard.iterator(); iterSelected.hasNext(); )
            {
                Selectable s = iterSelected.next ( );

                if ( s instanceof Node )
                    {
                        Node g = ((Node)s).copy ( this.getGraphics() );

                        if ( g != null )
                            {
                                this.circuit.addNode ( g );
                                g.select ( );
                                this.selected.add ( g );
                                nodes.put ( (Node)s, g );
                            }
                        else
                            JOptionPane.showMessageDialog ( this, "Unable to copy one of the nodes.", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                else if ( s instanceof Link )
                    {
                        links.add ( (Link) s );
                    }
            }

        /* we copy the valid links and update the anchors */
        for ( iterLinks=links.iterator(); iterLinks.hasNext(); )
            {
                Anchor a;
                Link l = iterLinks.next ( );
                iterAnchors = l.getAnchorsIterator ( );
                Link lcpy = new Link ( this.circuit.incLinkID() );

                while ( iterAnchors.hasNext() )
                    {
                       a = iterAnchors.next ( );
                       lcpy.addLinkedAnchor ( (nodes.get(a.getParent())).getAnchor(a.getID()) );
                    }

                this.circuit.addLink ( lcpy );
                lcpy.select ( );
                this.selected.add ( lcpy );
            }

        this.setChanged ( );
        this.repaint ( );
    }

    /**
     * Build a displayable Rectangle from the provided rectangle
     * @param r the source rectangle
     */
    private Rectangle makeRegularRect ( Rectangle r )
    {
        if ( this.selectionRect.width < 0 )
            if ( this.selectionRect.height < 0 )
                return new Rectangle ( this.selectionRect.x+this.selectionRect.width, this.selectionRect.y+this.selectionRect.height,
                                       -this.selectionRect.width, -this.selectionRect.height );
            else
                return new Rectangle ( this.selectionRect.x+this.selectionRect.width, this.selectionRect.y,
                                       -this.selectionRect.width, this.selectionRect.height );
        else
            if ( this.selectionRect.height < 0 )
                return new Rectangle ( this.selectionRect.x, this.selectionRect.y+this.selectionRect.height,
                                       this.selectionRect.width, -this.selectionRect.height );
            else
                return new Rectangle ( this.selectionRect.x, this.selectionRect.y,
                                       this.selectionRect.width, this.selectionRect.height );
    }

    /**
     * Get the number of elements currently selected
     * @return the number of elements (nodes and links) selected
     */
    public int getSelectionCount ( )
    {
        return this.selected.size ( );
    }

    /**
     * Delete the selected elements from this canvas
     */
    public void removeSelection ( )
    {
        for ( iterSelected=this.selected.iterator(); iterSelected.hasNext(); )
            {
                Selectable s = iterSelected.next ( );
                s.detach ( );

                if ( s instanceof Link )
                    this.circuit.removeLink ( (Link) s );
                else if ( s instanceof Node )
                    this.circuit.removeNode ( (Node) s );
            }

        Vector<Link> invalids = new Vector<Link> ( ); /* avoid concurrent modification */

        for ( iterLinks=this.circuit.getLinksIterator(); iterLinks.hasNext(); )
            {
                Link l = iterLinks.next ( );

                if ( l.isInvalid() )
                    {
                        l.detach ( );
                        invalids.add ( l );
                    }
            }

        for ( iterLinks=invalids.iterator(); iterLinks.hasNext(); )
            this.circuit.removeLink ( (Link) iterLinks.next() );

        if ( this.selected.size() > 0 )
            this.setChanged ( );

        this.selected.clear ( );
        this.repaint ( );
    }

    /**
     * Get the circuit associated with the canvas
     * @return the circuit
     */
    public CircuitUI getCircuit ( )
    {
        return this.circuit;
    }

    /**
     * Set the title of the circuit
     * @param t the new title
     */
    public void setTitle ( String t )
    {
        this.circuit.setName ( t );
    }

    /**
     * Get the title of the circuit. It contains no path nor file name extension.
     * @return the title of the circuit
     */
    public String getTitle ( )
    {
        return this.circuit.getName();
    }

    /**
     * Has the circuit been modified ?
     * @return true if the circuit has been modified, false either
     */
    public boolean hasChanged ( )
    {
        return this.changed;
    }

    /**
     * Set the state of the canvas to changed. We are not in sync with the circuit file anymore.
     */
    public void setChanged ( )
    {
        this.changed = true;
        this.parent.notifyCanvasChange ( );
    }

    /**
     * Set the state of the canvas to unchanged. We are now in sync with the circuit file.
     */
    public void resetChanged ( )
    {
        this.changed = false;
    }

    /**
     * Has the circuit already been saved ?
     * @return true if yes, false either
     */
    public boolean hasBeenSaved ( )
    {
        return this.saved;
    }

    /**
     * Set the state of the canvas to saved.
     */
    public void setSaved ( )
    {
        this.saved = true;
    }

    /**
     * Set the state of the canvas to unsaved.
     */
    public void resetSaved ( )
    {
        this.saved = false;
    }

    /**
     * Dump the canvas' circuit to an XML file
     * @return true if no error occured, false either
     */
    public boolean dumpToXmlFile ( )
    {
        return this.dumpToXmlFile ( this.file_name );
    }

    /**
     * Dump the canvas' circuit to an XML file
     * @param xml_file the file name
     * @return true if no error occured, false either
     */
    public boolean dumpToXmlFile ( String xml_file )
    {
        OutputStream output;

        if ( xml_file == null )
            {
                System.err.println ( "dumpToXmlFile: file name should not be null." );
                return false;
            }

        this.file_name = xml_file;

        try
            {
                File tmp = new File ( xml_file );
                tmp.delete ( );
                tmp = null;

                output = new FileOutputStream ( new File ( xml_file ) );
            }

        catch ( Exception e )
            {
                System.err.println ( "dumpToXmlFile: " + e.getMessage() );
                return false;
            }

        return this.circuit.dumpToXml ( output );
    }

    /**
     * Reload the composite nodes of the circuit (refresh the circuit).
     * @throws CircuitLoadingException if the circuit can not be reloaded
     */
    public void refresh ( ) throws CircuitLoadingException
    {
        if ( this.getGraphics() != null )
            this.circuit.reloadCompositeNodes ( this.getGraphics () );
    }

    /**
     * Hide the menu of the canvas
     */
    public void hideMenu ( )
    {
        this.menu.hide ( );
    }

    /**
     * CanvasMenu is the popup menu displayed when the user clicks with button3
     */
    public class CanvasMenu
    {
        private Component owner;

        private JPopupMenu menu;
        private JMenuItem menuCut;
        private JMenuItem menuCopy;
        private JMenuItem menuPaste;
        private JMenuItem menuSelectAll;

        /**
         * Default constructor
         * @param owner the parent component
         */
        public CanvasMenu ( Component owner )
        {
            this.owner = owner;

            menu = new JPopupMenu ( "actions" );

            menuCut = new JMenuItem ( "Cut", KeyEvent.VK_C );
            menuCut.addActionListener ( parent );
            menuCut.setActionCommand ( "cut" );
            menu.add ( menuCut );

            menuCopy = new JMenuItem ( "Copy", KeyEvent.VK_C );
            menuCopy.addActionListener ( parent );
            menuCopy.setActionCommand ( "copy" );
            menu.add ( menuCopy );

            menuPaste = new JMenuItem ( "Paste", KeyEvent.VK_P );
            menuPaste.addActionListener ( parent );
            menuPaste.setActionCommand ( "paste" );
            menu.add ( menuPaste );

            menu.addSeparator ( );

            menuSelectAll = new JMenuItem ( "Select All", KeyEvent.VK_A );
            menuSelectAll.addActionListener ( parent );
            menuSelectAll.setActionCommand ( "select all" );
            menu.add ( menuSelectAll );
        }

        /**
         * Show the menu at specified coords
         * @param x x coord
         * @param y y coord
         */
        public void show ( int x, int y )
        {
            if ( getSelectionCount() == 0 )
                {
                    menuCut.setEnabled ( false );
                    menuCopy.setEnabled ( false );
                }
            else
                {
                    menuCut.setEnabled ( true );
                    menuCopy.setEnabled ( true );
                }

            if ( parent.getClipboard().size() != 0 )
                menuPaste.setEnabled ( true );
            else
                menuPaste.setEnabled ( false );

            this.menu.show ( this.owner, x, y );
        }

        /**
         * Hide the menu
         */
        public void hide ( )
        {
            this.menu.setVisible ( false );
            owner.repaint ( );
        }
    }
}

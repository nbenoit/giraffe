/*
 * $RCSfile: NodeFinder.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.21 $
 */

package giraffe.ui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import giraffe.ui.nodes.CompositeNodeUI;

/**
 * Node finder window for selecting a node
 */
public class NodeFinder extends JDialog implements ActionListener, TreeSelectionListener, ComponentListener, WindowListener
{
    private static final long serialVersionUID = 0L;

    private JButton btnOK;
    private NodeTree tree;
    private NodePreview np;
    private JScrollPane sp;

    private Node node;

    private giraffe.ui.Frame owner;

    /**
     * Default constructor
     * @param title the title of the window
     * @param owner the frame that owns the node finder
     */
    public NodeFinder ( String title, giraffe.ui.Frame owner )
    {
        super ( owner, true );

        this.owner = owner;
        this.setTitle ( title );

        this.addWindowListener ( this );
        this.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );
        this.setLayout ( new BoxLayout ( this.getContentPane(), BoxLayout.PAGE_AXIS ) );
        this.addComponentListener ( this );

        JPanel p = new JPanel ( );
        JButton btn;

        ((FlowLayout) p.getLayout()).setHgap ( 32 );
        btn = new JButton ( "Import", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/import.png", "import") );
        btn.setActionCommand ( "import" );
        btn.addActionListener ( this );
        btn.setVisible ( true );
        p.add ( btn );
        btn = new JButton ( "Refresh", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/refresh.png", "refresh") );
        btn.setActionCommand ( "refresh" );
        btn.addActionListener ( this );
        btn.setVisible ( true );
        p.add ( btn );
        this.add ( p );

        p = new JPanel ( );
        ((FlowLayout) p.getLayout()).setHgap ( 12 );
        this.tree = new NodeTree ( this );
        this.tree.addTreeSelectionListener ( this );
        this.sp = new JScrollPane ( this.tree );
        p.add ( this.sp );
        this.np = new NodePreview ( );
        p.add ( this.np );
        this.add ( p );

        p = new JPanel ( );
        ((FlowLayout) p.getLayout()).setHgap ( 12 );
        btn = new JButton ( "OK", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/ok.png", "ok") );
        btn.addActionListener ( this );
        btn.setActionCommand ( "ok" );
        p.add ( btn );
        btn.setVisible ( true );
        this.btnOK = btn;
        btn = new JButton ( "Cancel", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/cancel.png", "cancel") );
        btn.addActionListener ( this );
        btn.setActionCommand ( "cancel" );
        p.add ( btn );
        btn.setVisible ( true );
        this.add ( p );

        this.setSize ( 356+this.np.getSize().width, 560 );
        this.setResizable ( false );

        File lib = new File ( giraffe.Giraffe.PATH + "/lib" );

        if ( ( lib.exists() ) && ( lib.isDirectory() ) )
            {
                File [] files = lib.listFiles ( );
                int i;
                File f;

                for ( i=0; i<files.length; ++i )
                    {
                        f = files[i];

                        if ( ( f.isFile() ) && ( f.getName().endsWith(".xml") ) )
                            {
                                this.owner.setLoadingInfoText ( f.getName() );
                                CompositeNodeUI n = new CompositeNodeUI ( );

                                try
                                    {
                                        n.load ( f, owner.getGraphics() );
                                    }

                                catch ( Exception e )
                                    {
                                        JOptionPane.showMessageDialog ( this, "Unable to open \'" + f.getName() + "\'.", "Error", JOptionPane.ERROR_MESSAGE );
                                        continue;
                                    }

                                this.tree.addLibNode ( n, false );
                            }
                    }
            }

        this.owner.setLoadingInfoText ( null );
        this.tree.refresh ( );
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
            this.setVisible ( false );
        else if ( "refresh".equals ( e.getActionCommand() ) )
            {
                String t = this.getTitle ( );
                this.setTitle ( t+" - Refreshing ..." );
                this.tree.refresh ( );
                this.np.setNode ( null, this );
                this.setSize ( 356+this.np.getSize().width, 560 );
                this.btnOK.setEnabled ( false );
                this.node = null;
                this.setTitle ( t );
            }
        else if ( "import".equals ( e.getActionCommand() ) )
            {
                JFileChooser chooser = new JFileChooser ( );
                chooser.setApproveButtonText ( "Import" );
                chooser.setFileFilter ( new giraffe.ui.Frame.GFileFilter() );
                chooser.setAcceptAllFileFilterUsed ( false );
                chooser.setDialogTitle ( "Import ..." );
                int returnVal = chooser.showOpenDialog ( this );

                if ( returnVal == JFileChooser.APPROVE_OPTION )
                    {
                        CompositeNodeUI n = new CompositeNodeUI ( );

                        try
                            {
                                n.load ( chooser.getSelectedFile(), this.getGraphics() );
                            }

                        catch ( Exception exc )
                            {
                                JOptionPane.showMessageDialog ( this, "Unable to open \'" + chooser.getSelectedFile().getName() + "\'.",
                                                                "Error", JOptionPane.ERROR_MESSAGE );
                                return;
                            }

                        this.np.setNode ( n, this );
                        this.setSize ( 356+this.np.getSize().width, 560 );
                        this.tree.addImportedNode ( n );
                        this.tree.setSelectionTo ( n );
                        this.node = n;
                    }
            }
    }

    /**
     * Callback for value changed events
     * @param e the TreeSelectionEvent object
     */
    public void valueChanged ( TreeSelectionEvent e )
    {
        this.node = this.tree.getNodeWithPath ( e.getPath() );
        this.np.setNode ( this.node, this );
        this.setSize ( 356+this.np.getSize().width, 560 );

        if ( this.node == null )
            this.btnOK.setEnabled ( false );
        else
            this.btnOK.setEnabled ( true );
    }

    /**
     * Open the node finder and wait for a selection
     * @param previous the previously selected node
     * @return the selected node or null
     */
    public Node getNode ( Node previous )
    {
        this.node = previous;

        if ( this.node == null )
            this.btnOK.setEnabled ( false );
        else
            this.btnOK.setEnabled ( true );

        this.np.setNode ( this.node, this );
        this.setSize ( 356+this.np.getSize().width, 560 );
        this.tree.setSelectionTo ( this.node );
        this.setVisible ( true );

        return this.node;
    }

    /**
     * Get the frame that owns this node finder
     * @return the frame that owns this
     */
    public giraffe.ui.Frame getOwner ( )
    {
        return this.owner;
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
        this.tree.setVisibleRowCount ( (this.getHeight()-this.btnOK.getHeight()*3)/this.tree.getRowHeight() );
        this.sp.setPreferredSize ( new Dimension ( this.getWidth()-this.np.getWidth()-NodePreview.MIN_WIDTH,
                                                   this.tree.getVisibleRowCount()*this.tree.getRowHeight()-40 ) );
    }

    /**
     * Callback for component shown events
     * @param e the ComponentEvent object
     */
    public void componentShown ( ComponentEvent e )
    {
        this.tree.setVisibleRowCount ( (this.getHeight()-this.btnOK.getHeight()*3)/this.tree.getRowHeight() );
        this.sp.setPreferredSize ( new Dimension ( this.getWidth()-this.np.getWidth()-NodePreview.MIN_WIDTH,
                                                   this.tree.getVisibleRowCount()*this.tree.getRowHeight()-40 ) );
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
     * Close the node finder without any selection
     */
    private void cancel ( )
    {
        this.node = null;
        this.setVisible ( false );
    }
}

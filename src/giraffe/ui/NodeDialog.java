/*
 * $RCSfile: NodeDialog.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.10 $
 */

package giraffe.ui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * NodeDialog allows to modify nodes attributes such as name and value
 */
public class NodeDialog extends JDialog implements ActionListener, WindowListener, DocumentListener
{
    private static final long serialVersionUID = 0L;

    private JButton btnOK;

    private java.awt.Frame owner;
    private Dialogable node;

    private JTextField name = null;

    private NodePreview np;
    private boolean updateNP;

    /**
     * Default constructor
     * @param title the title of the dialog
     * @param owner the frame that owns the dialog
     * @param node the node that is about to be edited
     */
    public NodeDialog ( String title, java.awt.Frame owner, Dialogable node )
    {
        super ( owner, true );

        this.owner = owner;
        this.node = node;
        this.setTitle ( title );

        this.addWindowListener ( this );
        this.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );
        this.setLayout ( new FlowLayout ( ) );

        this.np = new NodePreview ( );
        Node n;

        try
            {
                n = ((Node) node).getClass().getConstructor().newInstance ( );
            }

        catch ( Exception ex )
            {
                throw ( new RuntimeException ( ex.getMessage() ) );
            }

        this.np.setNode ( n, this );
        this.np.updateSize ( this.owner );
        this.updateNP = true;
        this.add ( this.np );

        JPanel fields = new JPanel ( );
        JPanel p;
        int field_height = 1;
        fields.setLayout ( new BoxLayout ( fields, BoxLayout.PAGE_AXIS ) );

        if ( node instanceof giraffe.ui.Nameable )
            {
                p = new JPanel ( );
                ((FlowLayout) p.getLayout()).setHgap ( 12 );
                p.add ( new JLabel ( "Name" ) );
                this.name = new JTextField ( 20 );
                this.name.getDocument().addDocumentListener ( this );
                this.name.getDocument().putProperty ( "owner", this.name );
                this.name.setText ( ((Nameable)node).getNodeName() );
                field_height += this.name.getPreferredSize().height + 10;
                p.add ( this.name );
                fields.add ( p );
            }

        this.add ( fields );

        JButton btn;
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

        this.setSize ( 300, field_height+btnOK.getPreferredSize().height+5+this.np.getSize().height+50 );
        this.setLocationRelativeTo ( owner );
        this.setVisible ( true );
    }

    /**
     * Paint the dialog
     * @param g the graphics object
     */
    public void paint ( Graphics g )
    {
        if ( this.updateNP )
            {
                this.np.updateSize ( this );
                this.setSize ( this.getSize().width+1, this.getSize().height ); /* ugly hack, we should use doLayout() */
                this.setSize ( this.getSize().width-1, this.getSize().height ); /* but it has no effect */
                this.updateNP = false;
            }

        super.paint ( g );
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
                if ( this.name != null )
                    ((Nameable)this.node).setNodeName ( this.name.getText() );

                this.setVisible ( false );
            }
    }

    /**
     * Callback for text modifications
     * @param e the DocumentEvent object
     */
    public void changedUpdate ( DocumentEvent e )
    {
        if ( e.getDocument().getProperty("owner") == this.name )
            {
                ((Nameable)this.np.getNode()).setNodeName ( this.name.getText() );
                this.np.updateSize ( this.owner );
                this.setSize ( this.getSize().width+1, this.getSize().height ); /* ugly hack, we should use doLayout() */
                this.setSize ( this.getSize().width-1, this.getSize().height ); /* but it has no effect */
            }
    }

    /**
     * Callback for text insertions
     * @param e the DocumentEvent object
     */
    public void insertUpdate ( DocumentEvent e )
    {
        if ( e.getDocument().getProperty("owner") == this.name )
            {
                ((Nameable)this.np.getNode()).setNodeName ( this.name.getText() );
                this.np.updateSize ( this.owner );
                this.setSize ( this.getSize().width+1, this.getSize().height ); /* ugly hack, we should use doLayout() */
                this.setSize ( this.getSize().width-1, this.getSize().height ); /* but it has no effect */
            }
    }

    /**
     * Callback for text removals
     * @param e the DocumentEvent object
     */
    public void removeUpdate ( DocumentEvent e )
    {
        if ( e.getDocument().getProperty("owner") == this.name )
            {
                ((Nameable)this.np.getNode()).setNodeName ( this.name.getText() );
                this.np.updateSize ( this.owner );
                this.setSize ( this.getSize().width+1, this.getSize().height ); /* ugly hack, we should use doLayout() */
                this.setSize ( this.getSize().width-1, this.getSize().height ); /* but it has no effect */
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
    }
}

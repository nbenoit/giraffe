/*
 * $RCSfile: About.java,v $
 * $Date: 2006/05/22 13:29:00 $ - $Revision: 1.10 $
 */

package giraffe.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import giraffe.Giraffe;

/**
 * About dialog
 */
public class About extends JDialog implements ActionListener
{
    private static final long serialVersionUID = 0L;

    private JButton btnClose;

    /**
     * Default constructor
     * @param title the title of the dialog
     * @param owner the frame that owns this about dialog
     */
    public About ( String title, java.awt.Frame owner )
    {
        super ( owner, true );

        this.setTitle ( title );

        this.setLayout ( new BorderLayout() );

        btnClose = new JButton ( "Close", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/close.png", "close") );
        btnClose.addActionListener ( this );
        JPanel p = new JPanel ( );
        p.add ( btnClose );
        this.add ( p, BorderLayout.SOUTH );
        this.btnClose.setVisible ( true );
        p.setVisible ( true );

        JLabel l = new JLabel ( "<html><div align=\"center\"><font size=\"+1\"><b>Giraffe - " + Giraffe.VERSION + "</b></font><br>&nbsp;<br>" + Giraffe.AUTHORS + "<br>&nbsp;<br>" + Giraffe.LICENSE + "</div></html>",
                                new ImageIcon ( giraffe.Giraffe.PATH+"/gfx/logo.png" ),
                                JLabel.CENTER );
        this.add ( l, BorderLayout.CENTER );

        this.setSize ( 400, 500 );
    }

    /**
     * Callback for action performed event
     * @param e the ActionEvent object.
     */
    public void actionPerformed ( ActionEvent e )
    {
        if ( e.getSource() == this.btnClose )
            this.setVisible ( false );
    }
}

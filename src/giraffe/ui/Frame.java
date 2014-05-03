/*
 * $RCSfile: Frame.java,v $
 * $Date: 2006/05/22 13:29:00 $ - $Revision: 1.46 $
 */

package giraffe.ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import giraffe.Giraffe;
import giraffe.ui.simulation.SimulationDialog;

/**
 * Frame is the main-window of the Giraffe application
 */
public class Frame extends JFrame implements ActionListener, WindowListener, KeyEventDispatcher, ChangeListener
{
    private static final long serialVersionUID = 0L;

    private JMenuItem menuNew;
    private JMenuItem menuOpen;
    private JMenuItem menuSave;
    private JMenuItem menuSaveAs;
    private JMenuItem menuClose;
    private JMenuItem menuQuit;
    private JMenuItem menuAbout;

    private JMenuItem menuCut;
    private JMenuItem menuCopy;
    private JMenuItem menuPaste;
    private JMenuItem menuSelectAll;

    private JMenuItem menuMouse;
    private JMenuItem menuAdd;
    private JMenuItem menuRemove;

    private JCheckBoxMenuItem menuGrid;
    private JMenuItem menuRefresh;
    private JMenuItem menuSimulate;

    private JButton btnNew;
    private JButton btnOpen;
    private JButton btnSave;
    private JButton btnSaveAs;
    private JButton btnClose;

    private JButton btnCut;
    private JButton btnCopy;
    private JButton btnPaste;

    private JToggleButton btnMouse;
    private JToggleButton btnAdd;
    private JButton btnRemove;

    private JToggleButton btnGrid;
    private JButton btnRefresh;
    private JButton btnSimulate;

    public static final int TOOL_NONE = 0;
    public static final int TOOL_MOUSE = 1;
    public static final int TOOL_ADD = 2;
    public static final int TOOL_REMOVE = 3;
    public static final int TOOL_OTHER = 4;

    private int tool;
    private Node node;

    private TabPane tabs;

    private NodeFinder nf;
    private About about;

    private JLabel loading_info;

    private int cid = 1;
    private File lastPath;

    private HashSet<Selectable> clipboard;

    /**
     * Default constructor
     * @param title the title of the frame
     */
    public Frame ( String title )
    {
        super ( title );

        this.setLayout ( new BorderLayout() );
        JLabel l = new JLabel ( "<html><div align=\"center\"><font size=\"+1\"><b>Giraffe - " + Giraffe.VERSION + "</b></font><br>&nbsp;<br>" + Giraffe.AUTHORS + "<br>&nbsp;<br>" + Giraffe.LICENSE + "</div></html>",
                                new ImageIcon ( giraffe.Giraffe.PATH+"/gfx/logo.png" ),
                                JLabel.CENTER );
        this.loading_info = new JLabel ( "", null, JLabel.CENTER );
        this.setLoadingInfoText ( null );
        this.add ( l, BorderLayout.CENTER );
        this.add ( this.loading_info, BorderLayout.SOUTH );

        this.setSize ( 400, 400 );
        this.loading_info.setSize ( 380, this.loading_info.getHeight() );
        this.setUndecorated ( true );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize ( );
        this.setLocation ( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

        this.setVisible ( true );

        /* this loading stuff can be long... */
        about = new About ( "About Giraffe", this );
        nf = new NodeFinder ( "Component Finder", this );
        /* loading done, build interface */

        this.setVisible ( false );
        this.remove ( l );
        this.remove ( this.loading_info );
        this.loading_info = null;
        this.dispose ( );
        this.setUndecorated ( false );
        this.setLayout ( new BorderLayout() );
        this.addWindowListener ( this );
        this.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );

        /** menu creation **/
        JMenu menu;
        JMenuBar menubar = new JMenuBar ( );

        menu = new JMenu ( "File" );
        menu.setMnemonic ( KeyEvent.VK_F );
        menubar.add ( menu );

        menuNew = new JMenuItem ( "New", KeyEvent.VK_N );
        menuNew.addActionListener ( this );
        menuNew.setActionCommand ( "new" );
        menu.add ( menuNew );

        menuOpen = new JMenuItem ( "Open ...", KeyEvent.VK_O );
        menuOpen.addActionListener ( this );
        menuOpen.setActionCommand ( "open" );
        menu.add ( menuOpen );

        menuSave = new JMenuItem ( "Save", KeyEvent.VK_S );
        menuSave.addActionListener ( this );
        menuSave.setActionCommand ( "save" );
        menu.add ( menuSave );

        menuSaveAs = new JMenuItem ( "Save As ...", KeyEvent.VK_V );
        menuSaveAs.addActionListener ( this );
        menuSaveAs.setActionCommand ( "save as" );
        menu.add ( menuSaveAs );

        menuClose = new JMenuItem ( "Close", KeyEvent.VK_C );
        menuClose.addActionListener ( this );
        menuClose.setActionCommand ( "close" );
        menu.add ( menuClose );

        menu.addSeparator ( );

        menuQuit = new JMenuItem ( "Quit", KeyEvent.VK_Q );
        menuQuit.addActionListener ( this );
        menuQuit.setActionCommand ( "quit" );
        menu.add ( menuQuit );

        menu = new JMenu ( "Edit" );
        menu.setMnemonic ( KeyEvent.VK_E );
        menubar.add ( menu );

        menuCut = new JMenuItem ( "Cut", KeyEvent.VK_U );
        menuCut.addActionListener ( this );
        menuCut.setActionCommand ( "cut" );
        menu.add ( menuCut );

        menuCopy = new JMenuItem ( "Copy", KeyEvent.VK_C );
        menuCopy.addActionListener ( this );
        menuCopy.setActionCommand ( "copy" );
        menu.add ( menuCopy );

        menuPaste = new JMenuItem ( "Paste", KeyEvent.VK_P );
        menuPaste.addActionListener ( this );
        menuPaste.setActionCommand ( "paste" );
        menu.add ( menuPaste );

        menu.addSeparator ( );

        menuSelectAll = new JMenuItem ( "Select All", KeyEvent.VK_A );
        menuSelectAll.addActionListener ( this );
        menuSelectAll.setActionCommand ( "select all" );
        menu.add ( menuSelectAll );

        menu = new JMenu ( "Tool" );
        menu.setMnemonic ( KeyEvent.VK_T );
        menubar.add ( menu );

        menuMouse = new JMenuItem ( "Mouse", KeyEvent.VK_M );
        menuMouse.addActionListener ( this );
        menuMouse.setActionCommand ( "mouse" );
        menu.add ( menuMouse );

        menuAdd = new JMenuItem ( "Add ...", KeyEvent.VK_A );
        menuAdd.addActionListener ( this );
        menuAdd.setActionCommand ( "add" );
        menu.add ( menuAdd );

        menuRemove = new JMenuItem ( "Remove", KeyEvent.VK_R );
        menuRemove.addActionListener ( this );
        menuRemove.setActionCommand ( "remove" );
        menu.add ( menuRemove );

        menu.addSeparator ( );

        menuGrid = new JCheckBoxMenuItem ( "Show grid" );
        menuGrid.setMnemonic ( KeyEvent.VK_G );
        menuGrid.addActionListener ( this );
        menuGrid.setActionCommand ( "grid" );
        menu.add ( menuGrid );

        menu.addSeparator ( );

        menuRefresh = new JMenuItem ( "Refresh", KeyEvent.VK_F );
        menuRefresh.addActionListener ( this );
        menuRefresh.setActionCommand ( "refresh" );
        menu.add ( menuRefresh );

        menuSimulate = new JMenuItem ( "Simulate", KeyEvent.VK_S );
        menuSimulate.addActionListener ( this );
        menuSimulate.setActionCommand ( "simulate" );
        menu.add ( menuSimulate );

        menu = new JMenu ( "Help" );
        menu.setMnemonic ( KeyEvent.VK_H );
        menubar.add ( menu );

        menuAbout = new JMenuItem ( "About", KeyEvent.VK_B );
        menuAbout.addActionListener ( this );
        menuAbout.setActionCommand ( "about" );
        menu.add ( menuAbout );

        this.setJMenuBar ( menubar );

        /** toolbar **/
        JToolBar toolbar = new JToolBar ( );
        toolbar.setOrientation ( JToolBar.HORIZONTAL );
        toolbar.setFloatable ( false );

        this.btnNew = this.makeToolbarButton ( "new", "New ...", "buttons/new.png" );
        toolbar.add ( this.btnNew );

        this.btnOpen = this.makeToolbarButton ( "open", "Open ...", "buttons/open.png" );
        toolbar.add ( this.btnOpen );

        this.btnSave = this.makeToolbarButton ( "save", "Save", "buttons/save.png" );
        toolbar.add ( this.btnSave );

        this.btnSaveAs = this.makeToolbarButton ( "save as", "Save As ...", "buttons/save_as.png" );
        toolbar.add ( this.btnSaveAs );

        this.btnClose = this.makeToolbarButton ( "close", "Close", "buttons/close.png" );
        toolbar.add ( this.btnClose );

        toolbar.addSeparator ( );

        this.btnCut = this.makeToolbarButton ( "cut", "Cut", "buttons/cut.png" );
        toolbar.add ( this.btnCut );

        this.btnCopy = this.makeToolbarButton ( "copy", "Copy", "buttons/copy.png" );
        toolbar.add ( this.btnCopy );

        this.btnPaste = this.makeToolbarButton ( "paste", "Paste", "buttons/paste.png" );
        toolbar.add ( this.btnPaste );

        toolbar.addSeparator ( );

        this.btnMouse = this.makeToolbarToggleButton ( "mouse", "Mouse", "buttons/mouse.png" );
        toolbar.add ( this.btnMouse );

        this.btnAdd = this.makeToolbarToggleButton ( "add", "Add", "buttons/add.png" );
        toolbar.add ( this.btnAdd );

        this.btnRemove = this.makeToolbarButton ( "remove", "Remove", "buttons/remove.png" );
        toolbar.add ( this.btnRemove );

        toolbar.addSeparator ( );

        this.btnGrid = this.makeToolbarToggleButton ( "grid", "Activate/Deactivate Grid", "buttons/grid.png" );
        toolbar.add ( this.btnGrid );

        this.btnRefresh = this.makeToolbarButton ( "refresh", "Refresh circuit", "buttons/refresh.png" );
        toolbar.add ( this.btnRefresh );

        this.btnSimulate = this.makeToolbarButton ( "simulate", "Simulate circuit", "buttons/exec.png" );
        toolbar.add ( this.btnSimulate );

        this.add ( toolbar, BorderLayout.PAGE_START );

        /** content **/
        this.tabs = new TabPane ( this );
        this.add ( this.tabs, BorderLayout.CENTER );
        this.tabs.setVisible ( true );

        this.setIconImage ( ( new ImageIcon ( giraffe.Giraffe.PATH + "/gfx/icon.png"  ) ).getImage() );
        this.setSize ( 800, 650 );

        this.tool = Frame.TOOL_MOUSE;
        this.node = null;

        this.clipboard = new HashSet<Selectable> ( );

        this.updateButtonsAndMenu ( );

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher ( this );

        this.lastPath = new File ( System.getProperty ( "user.home" ) );

        frameSize = this.getSize ( );
        this.setLocation ( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        this.setVisible ( true );
    }

    /**
     * Update the menu and the buttons according to the state of the canvas
     */
    public void updateButtonsAndMenu ( )
    {
        if ( this.tabs.getTabRunCount() == 0 )
            {
                this.menuNew.setEnabled ( true );
                this.menuOpen.setEnabled ( true );
                this.menuSave.setEnabled ( false );
                this.menuSaveAs.setEnabled ( false );
                this.menuClose.setEnabled ( false );

                this.menuCut.setEnabled ( false );
                this.menuCopy.setEnabled ( false );
                this.menuPaste.setEnabled ( false );
                this.menuSelectAll.setEnabled ( false );

                this.btnNew.setEnabled ( true );
                this.btnOpen.setEnabled ( true );
                this.btnSave.setEnabled ( false );
                this.btnSaveAs.setEnabled ( false );
                this.btnClose.setEnabled ( false );

                this.btnCut.setEnabled ( false );
                this.btnCopy.setEnabled ( false );
                this.btnPaste.setEnabled ( false );

                this.menuMouse.setEnabled ( false );
                this.menuAdd.setEnabled ( false );
                this.menuRemove.setEnabled ( false );

                this.menuGrid.setEnabled ( false );
                this.menuRefresh.setEnabled ( false );
                this.menuSimulate.setEnabled ( false );

                this.btnMouse.setEnabled ( false );
                this.btnAdd.setEnabled ( false );
                this.btnRemove.setEnabled ( false );
                this.btnMouse.setSelected ( false );
                this.btnAdd.setSelected ( false );
                this.btnRemove.setSelected ( false );

                this.btnGrid.setEnabled ( false );
                this.btnRefresh.setEnabled ( false );
                this.btnSimulate.setEnabled ( false );

                this.tool = Frame.TOOL_NONE;
            }
        else
            {
                this.menuNew.setEnabled ( true );
                this.menuOpen.setEnabled ( true );

                if ( this.tabs.getSelectedCanvas().hasChanged() )
                    this.menuSave.setEnabled ( true );
                else
                    this.menuSave.setEnabled ( false );

                this.menuSaveAs.setEnabled ( true );
                this.menuClose.setEnabled ( true );

                if ( this.tabs.getSelectedCanvas().getSelectionCount() == 0 )
                    {
                        this.menuCut.setEnabled ( false );
                        this.menuCopy.setEnabled ( false );
                    }
                else
                    {
                        this.menuCut.setEnabled ( true );
                        this.menuCopy.setEnabled ( true );
                    }

                if ( this.clipboard.size() != 0 )
                    this.menuPaste.setEnabled ( true );
                else
                    this.menuPaste.setEnabled ( false );

                this.menuSelectAll.setEnabled ( true );

                this.btnNew.setEnabled ( true );
                this.btnOpen.setEnabled ( true );

                if ( this.tabs.getSelectedCanvas().hasChanged() )
                    this.btnSave.setEnabled ( true );
                else
                    this.btnSave.setEnabled ( false );

                this.btnSaveAs.setEnabled ( true );
                this.btnClose.setEnabled ( true );

                if ( this.tabs.getSelectedCanvas().getSelectionCount() == 0 )
                    {
                        this.btnCut.setEnabled ( false );
                        this.btnCopy.setEnabled ( false );
                    }
                else
                    {
                        this.btnCut.setEnabled ( true );
                        this.btnCopy.setEnabled ( true );
                    }

                if ( this.clipboard.size() != 0 )
                    this.btnPaste.setEnabled ( true );
                else
                    this.btnPaste.setEnabled ( false );

                this.menuMouse.setEnabled ( true );
                this.menuAdd.setEnabled ( true );
                this.menuRemove.setEnabled ( true );

                this.menuGrid.setEnabled ( true );
                this.menuRefresh.setEnabled ( true );
                this.menuSimulate.setEnabled ( true );

                this.btnMouse.setEnabled ( true );
                this.btnAdd.setEnabled ( true );
                this.btnRemove.setEnabled ( true );

                this.btnGrid.setEnabled ( true );
                this.btnRefresh.setEnabled ( true );
                this.btnSimulate.setEnabled ( true );
            }

        if ( this.tool == Frame.TOOL_MOUSE )
            {
                this.btnMouse.setSelected ( true );
                this.btnAdd.setSelected ( false );
                this.btnRemove.setSelected ( false );
            }
        else if ( this.tool == Frame.TOOL_ADD )
            {
                this.btnMouse.setSelected ( false );
                this.btnAdd.setSelected ( true );
                this.btnRemove.setSelected ( false );
            }
        else if ( this.tool == Frame.TOOL_REMOVE )
            {
                this.btnMouse.setSelected ( false );
                this.btnAdd.setSelected ( false );
                this.btnRemove.setSelected ( true );
            }
        else if ( this.tool == Frame.TOOL_NONE )
            {
                this.btnMouse.setSelected ( false );
                this.btnAdd.setSelected ( false );
                this.btnRemove.setSelected ( false );
            }
    }

    /**
     * Callback for action performed notices
     * @param e the ActionEvent object
     */
    public void actionPerformed ( ActionEvent e )
    {
        if ( "new".equals ( e.getActionCommand() ) )
            {
                Canvas c = new Canvas ( this,"Untitled-"+this.cid );
                this.tabs.addTab ( c.getTitle(), null, c, c.getTitle() );
                c.setChanged ( );
                c.resetSaved ( );
                ++this.cid;
                this.tabs.setSelectedComponent ( c );
                this.tool = Frame.TOOL_MOUSE;
                this.updateTabTitles ( );
            }
        else if ( "open".equals ( e.getActionCommand() ) )
            {
                JFileChooser chooser = new JFileChooser ( );
                chooser.setApproveButtonText ( "Open" );
                chooser.setFileFilter ( new GFileFilter() );
                chooser.setCurrentDirectory ( this.lastPath );
                chooser.setAcceptAllFileFilterUsed ( false );
                chooser.setDialogTitle ( "Open ..." );
                int returnVal = chooser.showOpenDialog ( this );

                if ( returnVal == JFileChooser.APPROVE_OPTION )
                    {
                        this.openCircuit ( chooser.getSelectedFile() );
                        this.lastPath = chooser.getCurrentDirectory ( );
                    }
            }
        else if ( "save".equals ( e.getActionCommand() ) )
            {
                if ( this.tabs.getSelectedCanvas().hasBeenSaved() )
                    {
                        if ( this.tabs.getSelectedCanvas().dumpToXmlFile() )
                            {
                                this.tabs.getSelectedCanvas().resetChanged ( );
                            }
                        else
                            {
                                JOptionPane.showMessageDialog ( this, "Unable to save file...", "Error", JOptionPane.ERROR_MESSAGE );
                            }
                    }
                else
                    {
                        JFileChooser chooser = new JFileChooser ( );
                        chooser.setFileFilter ( new GFileFilter() );
                        chooser.setCurrentDirectory ( this.lastPath );
                        chooser.setAcceptAllFileFilterUsed ( false );
                        chooser.setApproveButtonText ( "Save As" );
                        chooser.setDialogTitle ( "Save As ..." );
                        int returnVal = chooser.showOpenDialog ( this );

                        if ( returnVal == JFileChooser.APPROVE_OPTION )
                            {
                                String old_title = this.tabs.getSelectedCanvas().getTitle ( );
                                this.tabs.getSelectedCanvas().setTitle ( GFileFilter.clean(chooser.getSelectedFile().getName()) );

                                if ( this.tabs.getSelectedCanvas().dumpToXmlFile ( GFileFilter.validate(chooser.getSelectedFile().getAbsolutePath()) ) )
                                    {
                                        this.tabs.getSelectedCanvas().setSaved ( );
                                        this.tabs.getSelectedCanvas().resetChanged ( );
                                        this.lastPath = chooser.getCurrentDirectory ( );
                                    }
                                else
                                    {
                                        JOptionPane.showMessageDialog ( this, "Unable to save file...", "Error", JOptionPane.ERROR_MESSAGE );
                                        this.tabs.getSelectedCanvas().setTitle ( old_title );
                                    }
                            }
                    }

                this.updateTabTitles ( );
            }
        else if ( "save as".equals ( e.getActionCommand() ) )
            {
                JFileChooser chooser = new JFileChooser ( );
                chooser.setFileFilter ( new GFileFilter() );
                chooser.setCurrentDirectory ( this.lastPath );
                chooser.setAcceptAllFileFilterUsed ( false );
                chooser.setApproveButtonText ( "Save As" );
                chooser.setDialogTitle ( "Save As ..." );
                int returnVal = chooser.showOpenDialog ( this );

                if ( returnVal == JFileChooser.APPROVE_OPTION )
                    {
                        this.tabs.getSelectedCanvas().setTitle ( GFileFilter.clean(chooser.getSelectedFile().getName()) );
                        this.tabs.getSelectedCanvas().dumpToXmlFile ( GFileFilter.validate(chooser.getSelectedFile().getAbsolutePath()) );
                        this.lastPath = chooser.getCurrentDirectory ( );
                        this.tabs.getSelectedCanvas().setSaved ( );
                        this.tabs.getSelectedCanvas().resetChanged ( );
                        this.updateTabTitles ( );
                    }
            }
        else if ( "close".equals ( e.getActionCommand() ) )
            {
                this.tabs.closeSelectedTab ( );
            }
        else if ( "quit".equals ( e.getActionCommand() ) )
            {
                this.quit ( );
            }
        else if ( "cut".equals ( e.getActionCommand() ) )
            {
                this.clipboard.clear ( );
                this.tabs.getSelectedCanvas().hideMenu ( );
                this.tabs.getSelectedCanvas().cutTo ( this.clipboard );
            }
        else if ( "copy".equals ( e.getActionCommand() ) )
            {
                this.clipboard.clear ( );
                this.tabs.getSelectedCanvas().hideMenu ( );
                this.tabs.getSelectedCanvas().copyTo ( this.clipboard );
            }
        else if ( "paste".equals ( e.getActionCommand() ) )
            {
                this.tabs.getSelectedCanvas().hideMenu ( );
                this.tabs.getSelectedCanvas().paste ( this.clipboard );
            }
        else if ( "refresh".equals ( e.getActionCommand() ) )
            {
                String t = this.getTitle ( );
                this.setTitle ( t+" - Refreshing ..." );

                try
                    {
                        this.tabs.getSelectedCanvas().refresh ( );
                    }

                catch ( Exception exc )
                    {
                        JOptionPane.showMessageDialog ( this, "Unable to refresh the circuit.", "Error", JOptionPane.ERROR_MESSAGE );
                    }

                this.setTitle ( t );
            }
        else if ( "about".equals ( e.getActionCommand() ) )
            {
                about.setLocationRelativeTo ( this );
                about.setVisible ( true );
            }
        else if ( "mouse".equals ( e.getActionCommand() ) )
            {
                this.tool = Frame.TOOL_MOUSE;
            }
        else if ( "add".equals ( e.getActionCommand() ) )
            {
                int t;
                Node n;

                t = this.tool;
                n = this.node;

                this.tool = Frame.TOOL_ADD;

                this.updateButtonsAndMenu ( );

                nf.setLocationRelativeTo ( this );
                this.node = nf.getNode ( n );

                if ( this.node == null )
                    {
                        this.tool = t;
                        this.node = n;
                    }
            }
        else if ( "remove".equals ( e.getActionCommand() ) )
            {
                int t;
                Node n;

                t = this.tool;
                n = this.node;

                this.tool = Frame.TOOL_REMOVE;
                this.node = null;

                this.updateButtonsAndMenu ( );

                this.tabs.getSelectedCanvas().removeSelection ( );

                this.tool = t;
                this.node = n;
            }
        else if ( "select all".equals ( e.getActionCommand() ) )
            {
                this.tabs.getSelectedCanvas().hideMenu ( );
                this.tabs.getSelectedCanvas().selectAll ( );
            }
        else if ( "grid".equals ( e.getActionCommand() ) )
            {
                if ( e.getSource() == this.menuGrid )
                    this.btnGrid.doClick ( );
                else
                    {
                        this.menuGrid.setState ( this.btnGrid.isSelected() );
                        this.repaint ( );
                    }
            }
        else if ( "simulate".equals ( e.getActionCommand() ) )
            {
                try
                    {
                        new SimulationDialog ( "Simulation of \'"+this.tabs.getSelectedCanvas().getTitle()+"\'", this, this.tabs.getSelectedCanvas().getCircuit() );
                    }

                catch ( Exception exc )
                    {
                        JOptionPane.showMessageDialog ( this, "Unable to simulate the circuit :\n"+exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                    }
            }

        this.updateButtonsAndMenu ( );
    }

    /**
     * Callback for key events
     * @param e the KeyEvent object
     * @return true if the key event was handled, false either
     */
    public boolean dispatchKeyEvent ( KeyEvent e )
    {
        if ( this.isFocused() ) /* we get keys only if we're the focused window */
            {
                if ( e.getID() == KeyEvent.KEY_PRESSED )
                    {
                        if ( ( e.getKeyCode() == KeyEvent.VK_DELETE ) && ( btnRemove.isEnabled() ) )
                            {
                                this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"remove") );
                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_A ) && ( e.isControlDown() ) )
                            {
                                if ( this.tabs.getSelectedComponent() != null )
                                    this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"select all") );

                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_S ) && ( e.isControlDown() ) )
                            {
                                if ( this.tabs.getSelectedComponent() != null )
                                    this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"save") );

                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_N ) && ( e.isControlDown() ) )
                            {
                                this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"new") );
                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_F ) && ( e.isControlDown() ) )
                            {
                                this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"open") );
                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_W ) && ( e.isControlDown() ) )
                            {
                                if ( this.tabs.getSelectedComponent() != null )
                                    this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"close") );

                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_X ) && ( e.isControlDown() ) && ( this.btnCut.isEnabled() ) )
                            {
                                if ( ( this.tabs.getSelectedComponent() != null ) )
                                    this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"cut") );

                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_C ) && ( e.isControlDown() ) && ( this.btnCopy.isEnabled() ) )
                            {
                                if ( this.tabs.getSelectedComponent() != null )
                                    this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"copy") );

                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_V ) && ( e.isControlDown() ) && ( this.btnPaste.isEnabled() ) )
                            {
                                if ( this.tabs.getSelectedComponent() != null )
                                    this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"paste") );

                                return true;
                            }
                        else if ( ( e.getKeyCode() == KeyEvent.VK_E ) && ( e.isControlDown() ) )
                            {
                                if ( this.tabs.getSelectedComponent() != null )
                                    this.actionPerformed ( new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"simulate") );

                                return true;
                            }
                        else if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
                            {
                                if ( this.btnGrid.isEnabled() )
                                    {
                                        this.btnGrid.setSelected ( true );
                                        this.menuGrid.setState ( true );
                                        this.repaint ( );
                                    }
                            }
                    }
                else if ( e.getID() == KeyEvent.KEY_RELEASED )
                    {
                        if ( e.getKeyCode() == KeyEvent.VK_SHIFT )
                            {
                                if ( this.btnGrid.isEnabled() )
                                    {
                                        this.btnGrid.setSelected ( false );
                                        this.menuGrid.setState ( false );
                                        this.repaint ( );
                                    }
                            }
                    }
            }

        return false;
    }

    /**
     * Get the current editing tool used
     * @return the ID of the tool, see constants.
     */
    public int getTool ( )
    {
        return this.tool;
    }

    /**
     * Get the node to add to canvas
     * @return the node
     */
    public Node getNode ( )
    {
        return this.node;
    }

    /**
     * Clipboard accessor
     * @return the clipboard of the frame
     */
    public HashSet getClipboard ( )
    {
        return this.clipboard;
    }

    /**
     * Is grid enabled ?
     * @return true if the grid must be used, false either
     */
    public boolean isGridEnabled ( )
    {
        return this.btnGrid.isSelected ( );
    }

    /**
     * Open a circuit and add it to the tabs
     * @param file the file that contains the XML description of the circuit
     */
    public void openCircuit ( File file )
    {
        Canvas c;

        try
            {
                c = new Canvas ( this, file );
            }

        catch ( Exception e )
            {
                JOptionPane.showMessageDialog ( this, "Unable to open \'" + file.getName() + "\'.", "Error", JOptionPane.ERROR_MESSAGE );
                return;
            }

        int idx = -1;

        for ( Iterator<Canvas>iterCanvas=this.tabs.getCanvas().iterator(); iterCanvas.hasNext(); )
            {
                Canvas canvas = iterCanvas.next ( );
                if ( canvas.getTitle().equals (c.getTitle()) )
                    {
                        idx = this.tabs.indexOfComponent ( canvas );
                        this.tabs.closeTab ( idx, false );
                    }
            }

        if ( idx != -1 )
            this.tabs.insertTab ( c.getTitle(), null, c, c.getTitle(), idx );
        else
            this.tabs.addTab ( c.getTitle(), null, c, c.getTitle() );

        this.tool = Frame.TOOL_MOUSE;
        this.tabs.setSelectedComponent ( c );
        this.updateButtonsAndMenu ( );
    }

    /**
     * Make a tool bar button
     * @param text the text
     * @param toolTipText the tool tip text
     * @param imageName the name of the image, null if no image is needed
     * @return a JButton with the required characteristics
     */
    protected JButton makeToolbarButton ( String text, String toolTipText, String imageName )
    {
        JButton button;
        String imgLocation = null;

        if ( imageName != null )
            imgLocation = giraffe.Giraffe.PATH + "/gfx/" + imageName;

        button = new JButton ( );
        button.setToolTipText ( toolTipText );
        button.addActionListener ( this );
        button.setActionCommand ( text );

        if ( imgLocation != null )
            button.setIcon ( new ImageIcon(imgLocation, text) );
        else
            button.setText ( text );

        return button;
    }

    /**
     * Make a tool bar toggle button
     * @param text the text
     * @param toolTipText the tool tip text
     * @param imageName the name of the image, null if no image is needed
     * @return a JToggleButton with the required characteristics
     */
    protected JToggleButton makeToolbarToggleButton ( String text, String toolTipText, String imageName )
    {
        JToggleButton button;
        String imgLocation = null;

        if ( imageName != null )
            imgLocation = giraffe.Giraffe.PATH + "/gfx/" + imageName;

        button = new JToggleButton ( );
        button.setToolTipText ( toolTipText );
        button.addActionListener ( this );
        button.setActionCommand ( text );

        if ( imgLocation != null )
            button.setIcon ( new ImageIcon(imgLocation, text) );
        else
            button.setText ( text );

        return button;
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
        this.quit ( );
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
     * Callback for state changed events (used for tabpane)
     * @param e the ChangeEvent object
     */
    public void	stateChanged ( ChangeEvent e )
    {
        if ( e.getSource() == this.tabs )
            this.updateButtonsAndMenu ( );
    }

    /**
     * Exit the application. User is prompted for confirmation.
     */
    private void quit ( )
    {
        if ( ( JOptionPane.showConfirmDialog ( this, "Quit ?", "Please confirm...",
                                               JOptionPane.YES_NO_OPTION ) ) == JOptionPane.YES_OPTION )
            System.exit ( 0 );
        else
            this.setVisible ( true );
    }

    /**
     * Callback for canvas change
     */
    public void notifyCanvasChange ( )
    {
        this.updateTabTitles ( ) ;
        this.updateButtonsAndMenu ( );
    }

    /**
     * Update the tab titles
     */
    public void updateTabTitles ( )
    {
        for ( Iterator<Canvas>iter=this.tabs.getCanvas().iterator(); iter.hasNext(); )
            {
                Canvas canvas = iter.next ( );

                if ( canvas.hasChanged() )
                    this.tabs.setTitleAt ( this.tabs.indexOfComponent(canvas), canvas.getTitle()+"*" );
                else
                    this.tabs.setTitleAt ( this.tabs.indexOfComponent(canvas), canvas.getTitle() );
            }
    }

    /**
     * Modificator for the loading info text
     * @param text the new text to put after 'LOADING '
     */
    public void setLoadingInfoText ( String text )
    {
        if ( this.loading_info != null )
            {
                if ( ( text == null ) || ( "".equals(text) ) )
                    this.loading_info.setText ( "<html><div align=\"center\"><font size=\"+1\">loading ...</font><br>&nbsp;</div></html>" );
                else
                    this.loading_info.setText ( "<html><div align=\"center\"><font size=\"+1\">loading " + text + " ...</font><br>&nbsp;</div></html>" );
            }
    }

    /**
     * File filter for Giraffe XML files
     */
    public static class GFileFilter extends FileFilter
    {
        /**
         * Accept a file for selection or not. File should end with '.xml'
         * @param f the file to check
         * @return true if the file is OK, false either
         */
        public boolean accept ( File f )
        {
            if ( f != null )
                {
                    if ( f.isDirectory() )
                        return true;

                    if ( f.getName().endsWith(".xml") )
                        return true;
                }

            return false;
        }

        /**
         * Get the description of the filter
         * @return the description of the filter
         */
        public String getDescription ( )
        {
            return "XML Files";
        }

        /**
         * Add extension
         * @param fname the file name to validate
         * @return a new file name with the appropriate extension
         */
        public static String validate ( String fname )
        {
            if ( fname.endsWith(".xml") )
                return fname;

            return fname + ".xml";
        }

        /**
         * Remove extension
         * @param fname the file name to clean
         * @return a new file name without the extension
         */
        public static String clean ( String fname )
        {
            if ( fname.endsWith(".xml") )
                return fname.substring ( 0, fname.length()-4 );

            return fname;
        }
    }
}

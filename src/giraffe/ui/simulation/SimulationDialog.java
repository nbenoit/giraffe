/*
 * $RCSfile: SimulationDialog.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.19 $
 */

package giraffe.ui.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import giraffe.ui.*;
import giraffe.core.Circuit;
import giraffe.core.SimulationListener;

/**
 * Simulation dialog
 */
public class SimulationDialog extends JDialog implements ActionListener, SimulationListener
{
    private static final long serialVersionUID = 0L;

    private java.awt.Frame owner;
    private String title;
    private CircuitUI circuit = null;
    private Circuit simu = null;

    private JLabel label;

    private HashSet<ChronogramIn> in_chronograms;
    private HashSet<ChronogramOut> out_chronograms;

    private int simu_length = 800;
    private int current_time;

    /**
     * Default constructor
     * @param title the title of the dialog
     * @param owner the frame that owns this about dialog
     * @param c the circuit that must be simulated
     * @throws SimulationDialogException if the circuit can not be simulated
     */
    public SimulationDialog ( String title, java.awt.Frame owner, CircuitUI c ) throws SimulationDialogException
    {
        super ( owner, true );

        this.owner = owner;
        this.title = title;
        this.circuit = c;
        this.setTitle ( title );
        this.setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );

        String t = this.owner.getTitle ( );
        this.owner.setTitle ( t+" - Loading simulation ..." );

        try
            {
                simu = this.circuit.makeSimulationCircuit ( );
                this.loadGUI ( );
            }

        catch ( Exception e )
            {
                this.owner.setTitle ( t );
                throw new SimulationDialogException ( e.getMessage() );
            }

        this.setLocationRelativeTo ( this.owner );
        this.owner.setTitle ( t );
        this.setResizable ( false );
        this.setVisible ( true );
    }

    /**
     * Callback for action performed event
     * @param e the ActionEvent object.
     */
    public void actionPerformed ( ActionEvent e )
    {
        if ( "exec".equals ( e.getActionCommand() ) )
            {
                this.setTitle ( this.title+" - Simulating ..." );

                for ( Iterator<ChronogramIn>iter=this.in_chronograms.iterator(); iter.hasNext(); )
                    {
                        ChronogramIn c = iter.next ( );
                        c.copyChangesRecordTo ( this.circuit.getInputForNode(c.getNode()) );
                    }

                for ( Iterator<ChronogramOut>iter=this.out_chronograms.iterator(); iter.hasNext(); )
                    iter.next().clearChangesRecord ( );

                this.current_time = 0;
                this.simu.initSimulation ( simu_length, this );
                this.simu.runSimulation ( );

                this.repaint ( );

                this.setTitle ( this.title );
            }
        else if ( "zoom in".equals ( e.getActionCommand() ) )
            {
                this.simu_length = (int) (this.simu_length * 0.9);
                this.updateChronograms ( );
            }
        else if ( "zoom out".equals ( e.getActionCommand() ) )
            {
                this.simu_length = (int) (this.simu_length * 1.1);
                this.updateChronograms ( );
            }
        else if ( "zoom fit".equals ( e.getActionCommand() ) )
            {
                this.simu_length = 800;
                this.updateChronograms ( );
            }
        else if ( "close".equals ( e.getActionCommand() ) )
            {
                this.setVisible ( false );
                this.dispose ( );
            }
    }

    /**
     * Initializes the GUI
     * @throws SimulationDialogException if the circuit can not be simulated
     */
    private void loadGUI ( ) throws SimulationDialogException
    {
        if ( this.circuit == null )
            throw new SimulationDialogException ( "Circuit is null (internal error)." );

        HashSet<Inputable> inputs = this.circuit.getInputables ( );
        this.in_chronograms = new HashSet<ChronogramIn> ( );

        HashSet<Outputable> outputs = this.circuit.getOutputables ( );
        this.out_chronograms = new HashSet<ChronogramOut> ( );

        if ( inputs.size() == 0 )
            throw new SimulationDialogException ( "Circuit must have one input at least." );

        if ( outputs.size() == 0 )
            throw new SimulationDialogException ( "Circuit must have one output at least." );

        this.setSize ( 700, (inputs.size()+outputs.size())*46+72+50 );
        this.setLayout ( new BorderLayout() );

        /* zoom buttons */
        JPanel panel = new JPanel ( );
        ((FlowLayout) panel.getLayout()).setHgap ( 32 );
        JButton btn = new JButton ( "Zoom Out", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/zoom_out.png", "zoom_out") );
        btn.setActionCommand ( "zoom out" );
        btn.addActionListener ( this );
        btn.setVisible ( true );
        panel.add ( btn );
        btn = new JButton ( "Reset Zoom", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/zoom_fit.png", "zoom_fit") );
        btn.setActionCommand ( "zoom fit" );
        btn.addActionListener ( this );
        btn.setVisible ( true );
        panel.add ( btn );
        btn = new JButton ( "Zoom In", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/zoom_in.png", "zoom_in") );
        btn.setActionCommand ( "zoom in" );
        btn.addActionListener ( this );
        btn.setVisible ( true );
        panel.add ( btn );
        /* label */
        this.label = new JLabel ( "Time : 0   Value : 0", JLabel.CENTER );
        this.label.setPreferredSize ( new Dimension ( this.label.getPreferredSize().width+30, this.label.getPreferredSize().height ) );
        panel.add ( this.label );
        this.add ( panel, BorderLayout.NORTH );

        /* chronograms */
        panel = new JPanel ( );
        ((FlowLayout) panel.getLayout()).setVgap ( 2 );

        int header_end = 0;
        int i;
        Vector<Integer> ypos = new Vector<Integer> ( );

        Chronogram.setEndOfTimeAt ( this.simu_length );

        for ( Iterator<Inputable>iter=inputs.iterator(); iter.hasNext(); )
            {
                Inputable in = iter.next ( );
                Chronogram c = new ChronogramIn ( this, (Node) in );
                this.in_chronograms.add ( (ChronogramIn) c );
                c.setPreferredSize ( new Dimension(this.getWidth()-24, 44) );
                c.setVisible ( true );

                if ( header_end < c.getHeaderBounds().width )
                    header_end = c.getHeaderBounds().width;

                /* we order inputs according to the Y coord in the circuit */
                int h = ((giraffe.ui.Node) in).getBounds().y;

                for ( i=0; i<ypos.size(); ++i )
                    if ( ypos.get(i) > h )
                        break;

                panel.add ( c, i );
                ypos.add ( i, h );
            }

        ypos.clear ( );

        for ( Iterator<Outputable>iter=outputs.iterator(); iter.hasNext(); )
            {
                Outputable out = iter.next ( );
                Chronogram c = new ChronogramOut ( this, (Node) out );
                this.out_chronograms.add ( (ChronogramOut) c );
                this.circuit.getOutputForNode(c.getNode()).addListener ( (ChronogramOut) c );
                c.setPreferredSize ( new Dimension(this.getWidth()-24, 44) );
                c.setVisible ( true );
                
                if ( header_end < c.getHeaderBounds().width )
                    header_end = c.getHeaderBounds().width;

                /* we order outputs according to the Y coord in the circuit */
                int h = ((giraffe.ui.Node) out).getBounds().y;

                for ( i=0; i<ypos.size(); ++i )
                    if ( ypos.get(i) > h )
                        break;

                panel.add ( c, i+inputs.size() );
                ypos.add ( i, h );
            }

        Chronogram.setHeaderEndAt ( header_end+20, this.getWidth()-24 );

        this.add ( panel, BorderLayout.CENTER );

        /* other buttons */
        panel = new JPanel ( );
        ((FlowLayout) panel.getLayout()).setHgap ( 32 );
        btn = new JButton ( "Simulate", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/exec.png", "exec") );
        btn.setActionCommand ( "exec" );
        btn.addActionListener ( this );
        btn.setVisible ( true );
        panel.add ( btn );
        btn = new JButton ( "Close", new ImageIcon(giraffe.Giraffe.PATH + "/gfx/buttons/close.png", "close") );
        btn.setActionCommand ( "close" );
        btn.addActionListener ( this );
        btn.setVisible ( true );
        panel.add ( btn );
        this.add ( panel, BorderLayout.SOUTH );
    }

    /**
     * Get the graphic context
     * @return the current graphic context
     */
    public Graphics getGraphicContext ( )
    {
        return this.owner.getGraphics ( );
    }

    /**
     * Set the label info
     * @param time the time index
     * @param value the current value
     */
    public void setLabelInfo ( int time, int value )
    {
        this.label.setText ( "Time : " + time + "   Value : " + value );
    }

    /**
     * timeUpdated : called when the time in the has changed
     * @param time the new time
     */
    public void timeUpdated ( int time )
    {
        if ( time != this.current_time )
            {
                this.current_time = time;
                this.setTitle ( this.title+" - Simulating ("+(int)(((double)time/(double)simu_length)*100)+"%)" );
            }
    }

    /**
     * updateChronograms : update the chronograms
     */
    public void updateChronograms ( )
    {
        Chronogram.setEndOfTimeAt ( this.simu_length );
        this.repaint ( );
    }
}

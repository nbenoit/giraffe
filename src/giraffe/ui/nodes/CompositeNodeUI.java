/*
 * $RCSfile: CompositeNodeUI.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.16 $
 */

package giraffe.ui.nodes;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.io.File;

import giraffe.ui.*;

import giraffe.core.Actor;
import giraffe.core.Wire;
import giraffe.core.Circuit;
import giraffe.core.Input;
import giraffe.core.Output;

/**
 * CompositeNode default implementation.
 * This kind of node embed a circuit that was previsously dumped into an XML file.
 */
public class CompositeNodeUI extends giraffe.ui.Node implements CompositeNode
{
    public static final String CATEGORY = "Composite";

    private String file_name;
    private String path_name;
    private CircuitUI circuit;
    private Vector<Inputable> inputs;
    private Vector<Outputable> outputs;
    private Hashtable<Inputable,Vector<Anchor>> input_anchors;
    private Hashtable<Outputable,Vector<Anchor>> output_anchors;
    private Hashtable<Inputable,Rectangle2D> input_bounds;
    private Hashtable<Outputable,Rectangle2D> output_bounds;

    private static final int FONT_SIZE = 9;
    private static final Font font = new Font ( "Monospaced", Font.PLAIN, FONT_SIZE );
    private Color text_color = Color.white;

    private static final int HPADDING = 12;
    private static final int VPADDING = 8;

    private int in_max_w;
    private int in_max_h;
    private int out_max_w;
    private int out_max_h;
    private Rectangle2D name_bounds;

    /**
     * Constructor
     */
    public CompositeNodeUI ( )
    {
        super ( );
        this.circuit = null;
        this.file_name = null;
        this.path_name = null;
    }

    /**
     * Load the circuit of this node
     * @param file the file that contains the XML description of the circuit
     * @param g the graphics that will paint the node
     * @throws CircuitLoadingException if the internal circuit can not be loaded
     */
    public void load ( File file, Graphics g ) throws CircuitLoadingException
    {
        this.file_name = file.getName ( );
        this.path_name = file.getParent ( );
        this.reload ( g );
    }

    /**
     * Reload the circuit of this node
     * @param g the graphics that will paint the node
     * @pre hasBeenInitialized()
     * @pre g != null
     * @throws CircuitLoadingException if the internal circuit can not be loaded
     */
    public void reload ( Graphics g ) throws CircuitLoadingException
    {
        if ( g == null )
            {
                System.err.println ( "CompositeNodeUI: reload(): g should not be null" );
                return;
            }

        if ( !this.hasBeenInitialized() )
            return;

        this.circuit = null;
        this.circuit = new CircuitUI ( new File(this.getCircuitFullPathName()), g );

        in_max_w = 0;
        in_max_h = 0;
        out_max_w = 0;
        out_max_h = 0;
        Rectangle2D r;
        int w;
        int h;
        int nbr_input_anchors = 0;
        int nbr_output_anchors = 0;

        this.inputs = new Vector<Inputable> ( );
        this.input_anchors = new Hashtable<Inputable,Vector<Anchor>> ( );
        this.input_bounds = new Hashtable<Inputable,Rectangle2D> ( );
        for ( Iterator<Inputable> iterIn=this.circuit.getInputablesIterator(); iterIn.hasNext(); )
            {
                Inputable in = iterIn.next ( );
                int i;

                if ( this.inputs.size() == 0 )
                    this.inputs.add ( in );
                else
                    {
                        /* we order inputs according to the Y coord in the circuit */
                        h = ((giraffe.ui.Node) in).getBounds().y;

                        for ( i=0; i<this.inputs.size(); ++i )
                            if ( ((giraffe.ui.Node)this.inputs.get(i)).getBounds().y > h )
                                break;

                        this.inputs.add ( i, in );
                    }

                g.setFont ( CompositeNodeUI.font );

                if ( ((IONode)in).getBitsCount() > 1 )
                    r = g.getFontMetrics().getStringBounds ( in.getNodeName()+(((IONode)in).getBitsCount()-1), g );
                else
                    r = g.getFontMetrics().getStringBounds ( in.getNodeName(), g );

                this.input_bounds.put ( in, r );
                w = (int) Math.ceil (  r.getWidth() );
                h = (int) Math.ceil (  r.getHeight() );

                if ( w > in_max_w )
                    in_max_w = w;

                if ( h > in_max_h )
                    in_max_h = h;
            }

        this.outputs = new Vector<Outputable> ( );
        this.output_anchors = new Hashtable<Outputable,Vector<Anchor>> ( );
        this.output_bounds = new Hashtable<Outputable,Rectangle2D> ( );
        for ( Iterator<Outputable> iterOut=this.circuit.getOutputablesIterator(); iterOut.hasNext(); )
            {
                Outputable out = iterOut.next ( );
                int i;

                if ( this.outputs.size() == 0 )
                    this.outputs.add ( out );
                else
                    {
                        /* we order outputs according to the Y coord in the circuit */
                        h = ((giraffe.ui.Node) out).getBounds().y;

                        for ( i=0; i<this.outputs.size(); ++i )
                            if ( ((giraffe.ui.Node) this.outputs.get(i)).getBounds().y > h )
                                break;

                        this.outputs.add ( i, out );
                    }

                g.setFont ( CompositeNodeUI.font );

                if ( ((IONode)out).getBitsCount() > 1 )
                    r = g.getFontMetrics().getStringBounds ( out.getNodeName()+(((IONode)out).getBitsCount()-1), g );
                else
                    r = g.getFontMetrics().getStringBounds ( out.getNodeName(), g );

                this.output_bounds.put ( out, r );
                w = (int) Math.ceil ( r.getWidth() );
                h = (int) Math.ceil ( r.getHeight() );

                if ( w > out_max_w )
                    out_max_w = w;

                if ( h > out_max_h )
                    out_max_h = h;
            }

        int id = 0;
        h = VPADDING + 2;
        for ( Iterator<Inputable> iterIn=this.inputs.iterator(); iterIn.hasNext(); )
            {
                Inputable in = iterIn.next ( );
                Vector<Anchor> vect = new Vector<Anchor> ( );

                for ( int i=0; i<((IONode)in).getBitsCount(); ++i )
                    {
                        Anchor a = new Anchor ( Anchor.IN, 2, h-(Anchor.HEIGHT/2)+2, this, id );
                        vect.add ( a );
                        this.anchors.add ( a );
                        ++nbr_input_anchors;
                        ++id;
                        h += in_max_h + VPADDING;
                    }

                this.input_anchors.put ( in, vect );
            }

        g.setFont ( CompositeNodeUI.font );
        this.name_bounds = g.getFontMetrics().getStringBounds ( this.circuit.getName(), g );

        w = in_max_w+out_max_w;
        if ( w < ((int)this.name_bounds.getWidth()) )
            w = ((int)this.name_bounds.getWidth());

        h = VPADDING + 2;
        for ( Iterator<Outputable> iterOut=this.outputs.iterator(); iterOut.hasNext(); )
            {
                Outputable out = iterOut.next ( );
                Vector<Anchor> vect = new Vector<Anchor> ( );

                for ( int i=0; i<((IONode)out).getBitsCount(); ++i )
                    {
                        Anchor a = new Anchor ( Anchor.OUT, w+HPADDING+4+Anchor.WIDTH, h-(Anchor.HEIGHT/2)+2, this, id );
                        vect.add ( a );
                        this.anchors.add ( a );
                        ++nbr_output_anchors;
                        ++id;
                        h += out_max_h + VPADDING;
                    }

                this.output_anchors.put ( out, vect );
            }

        this.bounds.setSize ( w+HPADDING+4+Anchor.WIDTH*2,
                              4+Math.max((in_max_h+VPADDING)*nbr_input_anchors+((int)this.name_bounds.getHeight())+4,
                                         (out_max_h+VPADDING)*nbr_output_anchors+((int)this.name_bounds.getHeight())+4) );
    }

    /**
     * Paint the node
     * @param g the graphics object
     */
    public void paint ( Graphics g )
    {
        if ( !this.hasBeenInitialized() )
            return;

        g.setColor ( Color.black );
        g.fillRect ( this.bounds.x+2+Anchor.WIDTH, this.bounds.y+2, this.bounds.width-(Anchor.WIDTH*2)-2, this.bounds.height-2 );

        g.setFont ( CompositeNodeUI.font );
        g.setColor ( this.text_color );

        int h = VPADDING + 2 + (in_max_h/2);
        for ( Iterator<Inputable> iterIn=this.inputs.iterator(); iterIn.hasNext(); )
            {
                Inputable in = iterIn.next ( );

                if ( ((IONode)in).getBitsCount() > 1 )
                    {
                        for ( int i=0; i<((IONode)in).getBitsCount(); ++i )
                            {
                                g.drawString ( in.getNodeName()+i, this.bounds.x+2+Anchor.WIDTH+3, this.bounds.y+h );
                                h += in_max_h + VPADDING;
                            }
                    }
                else
                    {
                        g.drawString ( in.getNodeName(), this.bounds.x+2+Anchor.WIDTH+3, this.bounds.y+h );
                        h += in_max_h + VPADDING;
                    }
            }

        h = VPADDING + 2 + (in_max_h/2);
        for ( Iterator<Outputable> iterOut=this.outputs.iterator(); iterOut.hasNext(); )
            {
                Outputable out = iterOut.next ( );

                if ( ((IONode)out).getBitsCount() > 1 )
                    {
                        for ( int i=0; i<((IONode)out).getBitsCount(); ++i )
                            {
                                g.drawString ( out.getNodeName()+i,
                                               this.bounds.x+this.bounds.width-(2+Anchor.WIDTH+2)-((int)this.output_bounds.get(out).getWidth()), this.bounds.y+h );
                                h += out_max_h + VPADDING;
                            }
                    }
                else
                    {
                        g.drawString ( out.getNodeName(),
                                       this.bounds.x+this.bounds.width-(2+Anchor.WIDTH+2)-((int)this.output_bounds.get(out).getWidth()), this.bounds.y+h );
                        h += out_max_h + VPADDING;
                    }
            }

        g.drawString ( this.circuit.getName(), this.bounds.x+Anchor.WIDTH+2+((this.bounds.width-(Anchor.WIDTH*2)-2-((int)this.name_bounds.getWidth()))/2),
                       this.bounds.y+this.bounds.height-4 );
        super.paint ( g );
    }

    /**
     * Select the node
     */
    public void select ( )
    {
        super.select ( );
        this.text_color = new Color ( 255, 220, 100 );
    }

    /**
     * Unselect the node
     */
    public void unselect ( )
    {
        super.unselect ( );
        this.text_color = Color.white;
    }

    /**
     * Category accessor
     * @return the category of the node
     */
    public final String getCategory ( )
    {
        return CATEGORY;
    }

    /**
     * CategoryID accessor
     * @return the ID of the category
     */
    public final int getCategoryID ( )
    {
        return giraffe.ui.Node.COMPOSITE;
    }

    /**
     * Name accessor
     * @return the name of the node
     */
    public final String getName ( )
    {
        if ( !this.hasBeenInitialized() )
            return CATEGORY;

        return this.circuit.getName();
    }

    /**
     * Initialization accessor
     * @return true if the node has been initialized, false either
     */
    public boolean hasBeenInitialized ( )
    {
        return ( ( this.file_name != null ) || ( this.path_name != null ) );
    }

    /**
     * File name accessor
     * @return the file name that contains the XML description of the circuit
     */
    public String getCircuitFileName ( )
    {
        return this.file_name;
    }

    /**
     * Path name accessor
     * @return the path to the XML description of the circuit
     */
    public String getCircuitPathName ( )
    {
        return this.path_name;
    }

    /**
     * Full name accessor
     * @return the full path to the XML description of the circuit
     */
    public String getCircuitFullPathName ( )
    {
        return ( this.path_name + "/" + this.file_name );
    }

    /**
     * Make the node equivalent for simulation
     * @param c the CircuitUI owning the component
     */
     public Actor makeSimulationNode ( CircuitUI c )
    {
        Circuit internal = this.circuit.makeSimulationCircuit ( );

        for ( Iterator<Wire> iter=internal.getWires(); iter.hasNext(); )
            c.getCircuit().addWire ( iter.next() );

        for ( Iterator<Inputable> iterIn=this.inputs.iterator(); iterIn.hasNext(); )
            {
                Inputable in = iterIn.next ( );
                Input simu_in = this.circuit.getInputForNode ( (giraffe.ui.Node) in );

                for ( int i=0; i<((InputUI)in).getBitsCount(); ++i )
                    {
                        Wire w = simu_in.getWire ( i );
                        c.getCircuit().addWire ( w );
                        Anchor a = this.input_anchors.get(in).get(i);
                        a.linkForSimulation ( w, c );
                    }
            }

        for ( Iterator<Outputable> iterOut=this.outputs.iterator(); iterOut.hasNext(); )
            {
                Outputable out = iterOut.next ( );
                Output simu_out = this.circuit.getOutputForNode ( (giraffe.ui.Node) out );

                for ( int i=0; i<((OutputUI)out).getBitsCount(); ++i )
                    {
                        Wire w = simu_out.getWire ( i );
                        c.getCircuit().addWire ( w );
                        Anchor a = this.output_anchors.get(out).get(i);
                        a.linkForSimulation ( w, c );
                    }
            }

        return null;
    }
}

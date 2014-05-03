/*
 * $RCSfile: CircuitUI.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.8 $
 */

package giraffe.ui;

import java.awt.*;
import java.util.*;
import org.w3c.dom.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import java.io.*;

import giraffe.ui.nodes.OutputUI;

import giraffe.core.Wire;
import giraffe.core.Actor;
import giraffe.core.Input;
import giraffe.core.Output;
import giraffe.core.Circuit;

/**
 * A circuit
 */
public class CircuitUI
{
    private String name;

    private HashSet<Node> nodes;
    private Iterator<Node> iterNodes;

    private HashSet<Link> links;
    private Iterator<Link> iterLinks;

    private Circuit circuit;
    private Hashtable<Link,Wire> wirestable;
    private Hashtable<Node,Input> inputstable;
    private Hashtable<Node,Output> outputstable;

    private int linkID = 0;

    /**
     * Empty circuit constructor
     * @param name the name of the circuit
     */
    public CircuitUI ( String name )
    {
        this.name = name;
        this.wirestable = null;
        this.inputstable = null;
        this.circuit = null;
        this.nodes = new HashSet<Node> ( );
        this.links = new HashSet<Link> ( );
    }

    /**
     * XML Circuit constructor
     * @param file the file that contains the XML description of the circuit
     * @param g the graphics that will paint the node
     * @throws CircuitLoadingException if the internal circuit can not be loaded
     */
    public CircuitUI ( File file, Graphics g ) throws CircuitLoadingException
    {
        this ( "" );

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
        DocumentBuilder builder;
        Document doc;
        Element root;

        Hashtable<Integer,Link> linkstable = new Hashtable<Integer,Link> ( );

        try
            {
                builder = factory.newDocumentBuilder ( );
                doc = builder.parse ( file );
            }

        catch ( SAXException sxe )
            {
                throw new CircuitLoadingException ( "SAX exception raised, invalid XML file." );
            }

        catch ( ParserConfigurationException pce )
            {
                throw new CircuitLoadingException ( "Parser exception raised, parser configuration is invalid." );
            }

        catch ( IOException ioe )
            {
                throw new CircuitLoadingException ( "I/O exception, file cannot be loaded." );
            }

        root = ( Element ) doc.getElementsByTagName ( "Circuit" ).item ( 0 );
        this.setName ( root.getAttribute("name") );

        NodeList nl = root.getElementsByTagName ( "Node" );
        Element e;
        Node n;
        Class cl;

        for ( int i=0; i<nl.getLength(); ++i)
            {
                e = ( Element ) nl.item ( i );

                try
                    {
                        cl = Class.forName ( e.getAttribute("class") );
                    }

                catch ( Exception exc )
                    {
                        System.err.println ( exc.getMessage() );
                        throw new RuntimeException ( "Circuit creation from xml." );
                    }

                try
                    {
                        n = ( (Node) cl.newInstance() );
                    }

                catch ( Exception exc )
                    {
                        System.err.println ( exc.getMessage() );
                        throw new RuntimeException ( "Circuit creation from xml." );
                    }

                this.nodes.add ( n );
                n.setLocation ( new Integer(e.getAttribute("x")), new Integer(e.getAttribute("y")) );

                if ( n instanceof giraffe.ui.Nameable )
                    ((Nameable)n).setNodeName ( e.getAttribute("node_name") );

                if ( n instanceof giraffe.ui.CompositeNode )
                    {
                        try
                            {
                                ((CompositeNode)n).load ( new File(file.getParent()+"/"+e.getAttribute("file_name")), g );
                            }

                        catch ( Exception exc )
                            {
                                /* try to load from the lib */
                                ((CompositeNode)n).load ( new File(giraffe.Giraffe.PATH+"/lib/"+e.getAttribute("file_name")), g );
                            }
                    }

                NodeList nlist = e.getElementsByTagName ( "Anchor" );
                Element el;

                for ( int j=0; j<nlist.getLength(); ++j )
                    {
                        el = ( Element ) nlist.item ( j );

                        Anchor a = n.getAnchor ( new Integer ( el.getAttribute("id") ) );
                        NodeList linklist = el.getElementsByTagName ( "Link" );
                        Element link;
                        Link l;

                        for ( int k=0; k<linklist.getLength(); ++k )
                            {
                                link = ( Element ) linklist.item ( k );
                                int id = new Integer ( link.getAttribute("id") );
                                int index = new Integer ( link.getAttribute("index") );

                                if ( id >= this.linkID )
                                    linkID = id+1;

                                if ( linkstable.containsKey(id) )
                                    {
                                        l = linkstable.get ( id );
                                        l.addLinkedAnchorAt ( a, index );
                                        a.addLink ( l );
                                    }
                                else
                                    {
                                        l = new Link ( id );
                                        l.addLinkedAnchorAt ( a, index );
                                        this.links.add ( l );
                                        linkstable.put ( id, l );
                                        a.addLink ( l );
                                    }
                            }
                    }
            }
    }

    /**
     * Increment the linkID
     * @return the old value of the linkID
     */
    public int incLinkID ( )
    {
        ++this.linkID;
        return ( this.linkID - 1 );
    }

    /**
     * Decrement the linkID
     * @return the old value of the linkID
     */
    public int decLinkID ( )
    {
        --this.linkID;
        return ( this.linkID + 1 );
    }

    /**
     * linkID accessor
     * @return the current linkID (index that the next link should have)
     */
    public int getLinkID ( )
    {
        return this.linkID;
    }

    /**
     * Name accessor
     * @return the name of the circuit
     */
    public String getName ( )
    {
        return this.name;
    }

    /**
     * Name modificator
     * @param name the new name
     */
    public void setName ( String name )
    {
        this.name = name;
    }

    /**
     * Reload the composite nodes of the circuit, this is recursive
     * @param g the graphics that will paint the node
     * @throws CircuitLoadingException if the internal circuit can not be loaded
     */
    public void reloadCompositeNodes ( Graphics g ) throws CircuitLoadingException
    {
        for ( iterNodes=this.nodes.iterator(); iterNodes.hasNext(); )
            {
                Node n = iterNodes.next ( );

                if ( n.getCategoryID() == Node.COMPOSITE )
                    ((CompositeNode)n).reload ( g );
            }
    }

    /**
     * Nodes accessor
     * @return the nodes of the circuit
     */
    public HashSet<Node> getNodes ( )
    {
        return this.nodes;
    }

    /**
     * Nodes accessor through an iterator
     * @return an iterator over the nodes of the circuit
     */
    public Iterator<Node> getNodesIterator ( )
    {
        return this.nodes.iterator();
    }

    /**
     * Add a new node to the circuit
     * @param n the new node
     */
    public void addNode ( Node n )
    {
        this.nodes.add ( n );
    }

    /**
     * Remove a node of the circuit
     * @param n the node to remove
     */
    public void removeNode ( Node n )
    {
        this.nodes.remove ( n );
    }

    /**
     * Get the inputable nodes of the circuit
     * @return an hashset of the inputable nodes of the circuit
     */
    public HashSet<Inputable> getInputables ( )
    {
        HashSet<Inputable> inputs = new HashSet<Inputable> ( );

        for ( iterNodes=getNodesIterator(); iterNodes.hasNext(); )
            {
                Node n = iterNodes.next ( );

                if ( n.getCategoryID() == Node.INPUT )
                    inputs.add ( (Inputable) n );
            }

        return inputs;
    }

    /**
     * Get the inputable nodes of the circuit
     * @return an iterator over the inputable nodes of the circuit
     */
    public Iterator<Inputable> getInputablesIterator ( )
    {
        return this.getInputables().iterator ( );
    }

    /**
     * Get the outputable nodes of the circuit
     * @return an hashset of the outputable nodes of the circuit
     */
    public HashSet<Outputable> getOutputables ( )
    {
        HashSet<Outputable> outputs = new HashSet<Outputable> ( );

        for ( iterNodes=getNodesIterator(); iterNodes.hasNext(); )
            {
                Node n = iterNodes.next ( );

                if ( n.getCategoryID() == Node.OUTPUT )
                    outputs.add ( (Outputable) n );
            }

        return outputs;
    }

    /**
     * Get the outputable nodes of the circuit
     * @return an iterator over the outputable nodes of the circuit
     */
    public Iterator<Outputable> getOutputablesIterator ( )
    {
        return this.getOutputables().iterator ( );
    }

    /**
     * Links accessor
     * @return the links of the circuit
     */
    public HashSet<Link> getLinks ( )
    {
        return this.links;
    }

    /**
     * Links accessor through an iterator
     * @return an iterator over the links of the circuit
     */
    public Iterator<Link> getLinksIterator ( )
    {
        return this.links.iterator();
    }

    /**
     * Add a new link to the circuit
     * @param l the new link
     */
    public void addLink ( Link l )
    {
        this.links.add ( l );
    }

    /**
     * Remove a link of the circuit
     * @param l the link to remove
     */
    public void removeLink ( Link l )
    {
        this.links.remove ( l );
    }

    /**
     * Get the wire equivalent for a link during circuit simulation
     * @return the wire equivalent
     */
    public Wire getWireForLink ( Link l )
    {
        if ( this.wirestable != null )
            return this.wirestable.get ( l );
        else
            return null;
    }

    /**
     * Get the node equivalent for a node during circuit simulation
     * @return the node equivalent
     */
    public Input getInputForNode ( Node n )
    {
        if ( this.inputstable != null )
            return this.inputstable.get ( n );
        else
            return null;
    }

    /**
     * Get the node equivalent for a node during circuit simulation
     * @return the node equivalent
     */
    public Output getOutputForNode ( Node n )
    {
        if ( this.outputstable != null )
            return this.outputstable.get ( n );
        else
            return null;
    }

    /**
     * Get the circuit attached to the circuitUI
     * @return the circuit for simulation
     */
    public Circuit getCircuit ( )
    {
        return this.circuit;
    }

    /**
     * Make the circuit for simulation
     * @return the equivalent circuit
     */
    public Circuit makeSimulationCircuit ( )
    {
        this.circuit = new Circuit ( );
        this.wirestable = new Hashtable<Link,Wire> ( );
        this.inputstable = new Hashtable<Node,Input> ( );
        this.outputstable = new Hashtable<Node,Output> ( );

        for ( iterLinks=this.links.iterator(); iterLinks.hasNext(); )
            this.wirestable.put ( iterLinks.next(), new Wire(this.circuit) );

        for ( iterNodes=this.nodes.iterator(); iterNodes.hasNext(); )
            {
                Node n = iterNodes.next ( );
                Actor a = n.makeSimulationNode(this);
                
                if ( a instanceof Input )
                    {
                        this.inputstable.put ( n, (Input) a );
                        this.circuit.addInput ( (Input) a );
                    }
                else if ( n instanceof OutputUI )
                    {
                        this.outputstable.put ( n, (Output) a );
                        this.circuit.addOutput ( (Output) a );
                    }
            }

        return this.circuit;
    }

    /**
     * Save the XML description of the circuit
     * @param output an output stream to write in
     * @return true if the dump was successful, false either
     */
    public boolean dumpToXml ( OutputStream output )
    {
        Document doc;
        Element root;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
        DocumentBuilder builder;

        try
            {
                builder = factory.newDocumentBuilder ( );
                doc = builder.newDocument ( );
            }

        catch ( ParserConfigurationException pce )
            {
                System.err.println ( "dumpToXmlFile: unable to write XML save file." );
                return false;
            }

        root = doc.createElement ( "Circuit" );
        root.setAttribute ( "name", this.getName() );

        for ( iterNodes=this.nodes.iterator(); iterNodes.hasNext(); )
            iterNodes.next().dumpToXml ( doc, root );

        root.normalize ( );
        doc.appendChild ( root );

        try
            {
                TransformerFactory tffactory = TransformerFactory.newInstance ( );
                Transformer transformer = tffactory.newTransformer ( );
                transformer.setOutputProperty ( OutputKeys.INDENT, "yes" );
                DOMSource source = new DOMSource ( doc );
                StreamResult result = new StreamResult ( output );
                transformer.transform ( source, result );
            }

        catch ( TransformerConfigurationException tce )
            {
                System.err.println ( "dumpToXmlFile:  Configuration Transformer exception." );
                return false;
            }

        catch ( TransformerException te )
            {
                System.err.println ( "dumpToXmlFile: Transformer exception." );
                return false;
            }

        return true;
    }
}

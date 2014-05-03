/*
 * $RCSfile: NodeTree.java,v $
 * $Date: 2006/05/22 13:25:00 $ - $Revision: 1.17 $
 */

package giraffe.ui;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.lang.reflect.Modifier;

import giraffe.ui.nodes.CompositeNodeUI;

/**
 * NodeTree displays nodes in a tree with categories
 */
public class NodeTree extends JTree
{
    private static final long serialVersionUID = 0L;

    private DefaultMutableTreeNode root;
    private DefaultTreeModel model;
    private Hashtable<String,DefaultMutableTreeNode> categories;
    private Hashtable<DefaultMutableTreeNode,Node> nodes;
    private Vector<Node> imported;
    private Vector<Node> lib;

    private NodeFinder parent;

    /**
     * Default constructor
     * @param parent the NodeFinder that owns this node tree
     */
    public NodeTree ( NodeFinder parent )
    {
        super ( new DefaultTreeModel ( new DefaultMutableTreeNode("Components") ) );

        this.root = (DefaultMutableTreeNode) this.getModel().getRoot ( );
        this.model = (DefaultTreeModel) this.getModel ( );
        this.getSelectionModel().setSelectionMode ( TreeSelectionModel.SINGLE_TREE_SELECTION );
        this.setLargeModel ( true );
        this.setRowHeight ( 16 );
        this.setEditable ( false );
        this.setShowsRootHandles ( false );
        this.categories = new Hashtable<String,DefaultMutableTreeNode> ( );
        this.nodes = new Hashtable<DefaultMutableTreeNode,Node> ( );
        this.imported = new Vector<Node> ( );
        this.lib = new Vector<Node> ( );
        this.parent = parent;
    }

    /**
     * Get the node with the specified path in the tree
     * @param p the path
     * @return the node
     */
    public Node getNodeWithPath ( TreePath p )
    {
        if ( p != null )
            {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) p.getLastPathComponent ( );
                
                if ( this.nodes.containsKey(n) )
                    return this.nodes.get(n);
            }

        return null;
    }

    /**
     * Set the selection to the specified node in the tree
     * @param n the node to select
     */
    public void setSelectionTo ( Node n )
    {
        if ( n != null )
            {
                Iterator<DefaultMutableTreeNode> cat;
                for ( cat=this.categories.values().iterator(); cat.hasNext(); )
                    {
                        DefaultMutableTreeNode c = cat.next ( );

                        for ( int j=0; j<c.getChildCount(); ++j )
                            {
                                DefaultMutableTreeNode tn = (DefaultMutableTreeNode) c.getChildAt ( j );
                                if ( this.nodes.get(tn).getName().equals(n.getName()) )
                                    this.setSelectionPath ( new TreePath ( tn.getPath() ) );
                            }
                    }
            }
        else
            {
                this.setSelectionPath ( new TreePath(this.root) );
            }
    }

    /**
     * Clear the tree
     */
    public void clear ( )
    {
        this.root.removeAllChildren ( );
        this.model.reload ( );
        this.categories.clear ( );
        this.nodes.clear ( );
    }

    /**
     * Expand all categories of the tree
     */
    public void expandAll ( )
    {
        this.expandPath ( new TreePath ( this.root ) );

        Iterator<DefaultMutableTreeNode> i;

        for ( i=this.categories.values().iterator(); i.hasNext(); )
            this.expandPath ( new TreePath ( i.next().getPath() ) );
    }

    /**
     * Refresh the content of the tree
     */
    public void refresh ( )
    {
        Node [] nodes = this.getAvailableNodes ( );
        Node n;
        String cat;
        DefaultMutableTreeNode tn;

        this.clear ( );

        if ( this.imported.size() != 0 )
            {
                this.addCategory ( this.root, new DefaultMutableTreeNode("Imported"), "Imported" );

                for ( int i=0; i<this.imported.size(); ++i )
                    {
                        CompositeNodeUI cn, updated;
                        n = this.imported.get ( i );

                        try
                            {
                                cn = ( CompositeNodeUI ) n;
                            }

                        catch ( Exception e )
                            {
                                this.imported.remove ( i );
                                this.nodes.remove ( n );
                                --i;
                                continue;
                            }

                        updated = new CompositeNodeUI ( );

                        File f = new File ( cn.getCircuitFullPathName() );
                        if ( !f.exists() )
                            {
                                this.imported.remove ( i );
                                this.nodes.remove ( n );
                                --i;
                                continue;
                            }

                        try
                            {
                                updated.load ( f, this.parent.getOwner().getGraphics() );
                            }

                        catch ( Exception e )
                            {
                                /* be quiet */
                            }

                        this.addNodeToCategory ( this.categories.get("Imported"), new DefaultMutableTreeNode(updated.getName()), updated );
                    }
            }

        if ( this.lib.size() != 0 )
            {
                this.addCategory ( this.root, new DefaultMutableTreeNode("Library"), "Library" );

                for ( int i=0; i<this.lib.size(); ++i )
                    {
                        CompositeNodeUI cn, updated;
                        n = this.lib.get ( i );

                        try
                            {
                                cn = ( CompositeNodeUI ) n;
                            }

                        catch ( Exception e )
                            {
                                this.lib.remove ( i );
                                this.nodes.remove ( n );
                                --i;
                                continue;
                            }

                        updated = new CompositeNodeUI ( );

                        File f = new File ( cn.getCircuitFullPathName() );
                        if ( !f.exists() )
                            {
                                this.lib.remove ( i );
                                this.nodes.remove ( n );
                                --i;
                                continue;
                            }

                        try
                            {
                                updated.load ( f, this.parent.getOwner().getGraphics() );
                            }

                        catch ( Exception e )
                            {
                                /* be quiet */
                            }

                        this.addNodeToCategory ( this.categories.get("Library"), new DefaultMutableTreeNode(updated.getName()), updated );
                    }
            }

        if ( nodes != null )
            {
                for ( int i=0; i<nodes.length; ++i )
                    {
                        n = nodes[i];
                        cat = n.getCategory ( );

                        if ( !this.categories.containsKey(cat) )
                            {
                                tn = new DefaultMutableTreeNode ( cat );
                                this.addCategory ( this.root, tn, cat );
                            }

                        this.addNodeToCategory ( this.categories.get(cat), new DefaultMutableTreeNode ( n.getName() ), n );
                    }

                this.expandAll ( );
            }
    }

    /**
     * Add a category to the tree. It will be alphabetically sorted
     * @param parent the parent tree node of the new category
     * @param child the tree node of the category
     * @param addedCategory the name of the new category
     */
    private void addCategory ( DefaultMutableTreeNode parent, DefaultMutableTreeNode child, String addedCategory )
    {
        DefaultMutableTreeNode node = null;
        int n = parent.getChildCount();

        this.categories.put ( addedCategory, child );

        if ( n == 0 )
            {
                parent.add ( child );
                return;
            }

        for ( int i=0; i<n; ++i )
            {
                node = (DefaultMutableTreeNode) parent.getChildAt ( i );

                if( node.toString().compareTo(child.toString()) > 0 )
                    {
                        parent.insert ( child, i );
                        return;
                    }
            }

        parent.add ( child );
    }

    /**
     * Add an imported node to the tree
     * @param addedNode the node to add
     */
    public void addImportedNode ( Node addedNode )
    {
        this.addImportedNode ( addedNode, true );
    }

    /**
     * Add a node of the library to the tree
     * @param addedNode the node to add
     */
    public void addLibNode ( Node addedNode )
    {
        this.addLibNode ( addedNode, true );
    }

    /**
     * Add an imported node to the tree
     * @param addedNode the node to add
     * @param refreshAfter must be true if the tree can be refreshed after the node addition, false either
     */
    public void addImportedNode ( Node addedNode, boolean refreshAfter )
    {
        if ( !this.imported.contains(addedNode) )
            {
                boolean present = false;

                for ( int i=0; i<this.imported.size(); ++i )
                    if ( this.imported.get(i).getName().equals(addedNode.getName()) )
                        present = true;

                if ( !present )
                    this.imported.add ( addedNode );

                if ( refreshAfter )
                    this.refresh ( );
            }
    }

    /**
     * Add a node of the library to the tree
     * @param addedNode the node to add
     * @param refreshAfter must be true if the tree can be refreshed after the node addition, false either
     */
    public void addLibNode ( Node addedNode, boolean refreshAfter )
    {
        if ( !this.lib.contains(addedNode) )
            {
                boolean present = false;

                for ( int i=0; i<this.lib.size(); ++i )
                    if ( this.lib.get(i).getName().equals(addedNode.getName()) )
                        present = true;

                if ( !present )
                    this.lib.add ( addedNode );

                if ( refreshAfter )
                    this.refresh ( );
            }
    }

    /**
     * Add a node to the tree. It will be alphabetically sorted
     * @param parent the parent tree node of the new node
     * @param child the tree node of the node
     * @param addedNode the node that is being added
     */
    private void addNodeToCategory ( DefaultMutableTreeNode parent, DefaultMutableTreeNode child, Node addedNode )
    {
        DefaultMutableTreeNode node = null;
        int n = parent.getChildCount();

        this.nodes.put ( child, addedNode );

        if ( n == 0 )
            {
                parent.add ( child );
                return;
            }

        for ( int i=0; i<n; ++i )
            {
                node = (DefaultMutableTreeNode) parent.getChildAt ( i );

                if( node.toString().compareTo(child.toString()) > 0 )
                    {
                        parent.insert ( child, i );
                        return;
                    }
            }

        parent.add ( child );
    }

    /**
     * Browse the package giraffe.ui.nodes.* and load the nodes
     * @return an array of selectable nodes
     */
    private Node [] getAvailableNodes ( )
    {
        ArrayList<Node> nodes = new ArrayList<Node> ( );

        File directory = new File ( giraffe.Giraffe.PATH + "/class/giraffe/ui/nodes" );

        if ( directory.exists() )
            {
                String[] files = directory.list ( );

                for ( int i = 0; i<files.length; ++i )
                    {
                        if ( files[i].endsWith(".class") )
                            {
                                Class c;

                                try
                                    {
                                        c = Class.forName ( "giraffe.ui.nodes." + files[i].substring(0, files[i].length() - 6) );
                                    }

                                catch ( Exception e )
                                    {
                                        continue;
                                    }

                                /* Avoid loading of CompositeNode */
                                try
                                    {
                                        if ( "Composite".equals ( c.getDeclaredField("CATEGORY").get(null) ) )
                                            continue;
                                    }

                                catch ( NoSuchFieldException e )
                                    {
                                        /* If the CATEGORY field does not exist, it doesn't matter */
                                    }

                                catch ( Exception e )
                                    {
                                        continue;
                                    }

                                if ( ( !Modifier.isAbstract(c.getModifiers()) ) && ( !c.isInterface() ) )
                                    {
                                        try
                                            {
                                                Node n = ( (Node) c.newInstance() );
                                                nodes.add ( n );
                                            }

                                        catch ( Exception e )
                                            {
                                                System.err.println ( "NodeTree: \'" + c.getSimpleName() + "\' seems invalid" );
                                            }
                                    }
                            }
                    }
            }
        else
            {
                System.err.println ( "NodeTree: package seems invalid (empty ?)" );
                return null;
            }

        Node[] nodesA = new Node [ nodes.size() ];
        nodes.toArray ( nodesA );
        return nodesA;
    }
}

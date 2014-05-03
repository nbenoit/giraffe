/*
 * $RCSfile: Giraffe.java,v $
 * $Date: 2006/06/03 13:47:54 $ - $Revision: 1.12 $
 */

package giraffe;

import java.net.URI;
import java.io.File;
import giraffe.ui.Frame;

/**
 * Giraffe launcher
 */
public class Giraffe
{
    public static final String VERSION = "1.0";
    public static final String AUTHORS = "Nicolas BENOIT<br>Pierre-Louis CABELGUEN";
    public static final String LICENSE = "Released under GNU-GPL";

    public static String PATH = "";

    /**
     * Default constructor.
     * Launches Giraffe GUI.
     */
    public Giraffe ( )
    {
        try
            {
                PATH = (new File(new URI(this.getClass().getResource("Giraffe.class").toString())).getParentFile().getParentFile().getParentFile()).getAbsolutePath ( );
            }

        catch ( Exception e )
            {
                PATH = "";
            }

        new Frame ( "Giraffe - " + Giraffe.VERSION );
    }
}

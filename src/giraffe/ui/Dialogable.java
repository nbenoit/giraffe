/*
 * $RCSfile: Dialogable.java,v $
 * $Date: 2006/05/04 11:42:12 $ - $Revision: 1.2 $
 */

package giraffe.ui;

/**
 * A dialogable component have properties that can be changed in a dialog
 */
public interface Dialogable
{
    /**
     * Open the dialog
     * @param parent the parent frame of the new dialog
     */
    public void openDialog ( giraffe.ui.Frame parent );
}

package japrc2012;

public interface SimGUIInterface
{
    /** 
     * Tells the GUI that something about the simulation has changed (and hence the GUI may need to redraw itself) 
     * 
     */
    void notifySimHasChanged();
}

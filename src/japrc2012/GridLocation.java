package japrc2012;

/** A class for representing integer positions in 2D space. Immutable.*/
public final class GridLocation {
    private final int x;
    private final int y;
    
    public GridLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
        
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        else 
        {
            if (o instanceof GridLocation)
            {
                GridLocation g = (GridLocation) o;
                if (this.x == g.x && this.y == g.y)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }else {
                return false;
            }
        }
    }
    
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}
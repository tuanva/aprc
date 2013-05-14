package japrc2012;

public interface AirportInterface
{
    /** 
     * Returns the IATA code of the airport (e.g. "LHR")
     * 
     * @return the IATA code of the plane
     */
    public String getCode();
               
            
    /** 
     * Returns the full name of the airport (e.g. "London Heathrow")
     * 
     * @return the full name of the airport
     */
    public String getName();
    
    /** 
     * Returns the grid location of the airport
     * 
     * @return the grid location of the airport
     */
    public GridLocation getLocation();
    
    /** 
     * Returns a boolean indicating whether the airport is open
     * 
     * @return a boolean indicating whether the airport is open
     */
    public boolean getOpen();
    
    /** 
     * Tells the airport that one tick of simulation time has passed
     * 
     */
    public void tick();    
    
    /** 
     * Sets the probability per tick that a new plane will takeoff from this airport (except at night, when no planes may takeoff)
     * 
     */
    public void setTakeOffProb(double prob);
}

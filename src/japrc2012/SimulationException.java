package japrc2012;

public final class SimulationException extends RuntimeException
{
    private String msg;

    public SimulationException(String message){
        this.msg = message;
    }
    
    public String toString(){
        return msg;
    }
}

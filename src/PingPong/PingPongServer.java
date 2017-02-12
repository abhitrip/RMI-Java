package PingPong;
import rmi.*;
public class PingPongServer implements PingPongRemoteInterface{
    public String ping(int idNum) throws RMIException{
        StringBuilder sb = new StringBuilder( );
        sb.append("Pong ");
        sb.append(idNum);
        return sb.toString();   
    }
}



/*
@ Remote interface for Factory of ping-pong server
authors : Abhijit Tripathy and Prateek Khurana
 */
package rmi;

public interface PingPongFactoryInterface{
    public PingPongRemoteInterface makePingServer()throws RMIException;
}
/*
@ This is the remote interface for Ping Pong server
 */
package rmi;
public interface PingPongRemoteInterface{
    public String ping(int idNum) throws RMIException;
   
}
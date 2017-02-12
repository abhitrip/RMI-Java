package PingPong;
import rmi.*;

import rmi.Skeleton;

import java.net.InetSocketAddress;

public class PingPongServerFactory implements PingPongFactoryInterface{
    public static void main(String[] args) {
        
        if(args.length<1){
            System.out.print("You forgot to give the port number");
            System.exit(0);
        }
        int portNum = Integer.parseInt(args[0]);
        InetSocketAddress address = new InetSocketAddress(portNum); // Create socket only on portNum
        PingPongServerFactory pingPongServerFactory = new PingPongServerFactory();
        // Now lets create a skeleton instance of the remote interface
        Skeleton<PingPongFactoryInterface> skeletonFactory = new Skeleton<PingPongFactoryInterface>(
        PingPongFactoryInterface.class,
        pingPongServerFactory,             
        address
        );
        try {
            skeletonFactory.start();
            System.out.print("Yay the server factory has started");
        } catch (RMIException e) {
            //TODO: handle exception
            e.printStackTrace();
            System.exit(1);
        }

    }
     public PingPongRemoteInterface makePingServer()throws RMIException{
        PingPongServer pingPongServer = new PingPongServer();
        Skeleton<PingPongRemoteInterface> skeleton = new Skeleton<PingPongRemoteInterface>(
            PingPongRemoteInterface.class,
            pingPongServer);
        try {
            skeleton.start();
        } catch (RMIException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        try {
            PingPongRemoteInterface stub = Stub.create(PingPongRemoteInterface.class, skeleton);
            return stub;
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
       
     }

}



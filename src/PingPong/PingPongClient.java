/*
@ This is the file for ping-pong client.
Author : Abhijit
*/
package PingPong;

import rmi.PingPongRemoteInterface;
import rmi.PingPongFactoryInterface;
import rmi.Stub;
import rmi.RMIException;


import java.net.InetSocketAddress;

public class PingPongClient{
    public static void main(String[] args)
    {
        //if(args.length<2) // Uncomment this in case of docker
        if(args.length<1)
        {
            System.out.println("Usage: PingPongClient: hostName PortNum");
            System.exit(0);
        }
        //String hostName = args[0]; // Uncomment this in case of Docker simulation
        //int portNum = Integer.parseInt(args[1]);// Uncomment this in case of Docker simulation
        int portNum = Integer.parseInt(args[0]);// comment this in case of Docker simulation
        //InetSocketAddress address = new InetSocketAddress(hostName,portNum); // Uncomment this in case of docker
        InetSocketAddress address = new InetSocketAddress(portNum); // Comment this in case of docker

        PingPongFactoryInterface stubFactory = Stub.create(PingPongFactoryInterface.class,
        address);
        PingPongRemoteInterface stubPing = null; 
        try{
             stubPing = stubFactory.makePingServer();
        }
        catch (RMIException e){
            e.printStackTrace();
            System.exit(1); // Some error occured and so exit
        }

        int totalPassed = 0;
        int totalFailed  = 0;
        for(int i=1;i<=4;i++)
        {
            try{
                    String resultData = stubPing.ping(i);
                    System.out.println("got: "+ resultData);
                    if(resultData.equals("Pong "+i))
                        totalPassed+=1;
                    else
                        totalFailed+=1;
                }
            catch (RMIException e){
             e.printStackTrace();
             System.exit(1);
            }
                
        }

        System.out.print("Number of tests passed = "+ totalPassed + " and number of tests failed = "+ totalFailed);
        


    }
}
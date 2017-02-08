/**
 * 
 */
package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author Prateek
 *
 */
public class ServerThread<T> extends Thread implements Serializable, Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1980162099632946370L;
	private Socket socket;
	private Class<T> c;
	private T server;
	Skeleton<T> skeleton;
	public ServerThread(Socket socket, Class<T> c, T server, Skeleton<T> skeleton) {
		this.socket = socket;
		this.c = c;
		this.server = server;
		this.skeleton = skeleton;
	}
	
	@Override
	public void run() {
		
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		try
		{   objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectOutputStream.flush();
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			String methodName = (String) objectInputStream.readObject();
			Class<T>[] params = (Class<T>[]) objectInputStream.readObject();
			Object[] args = (Object[]) objectInputStream.readObject();
			Method method = c.getMethod(methodName, params);
			Object result = method.invoke(server, args);
			objectOutputStream.writeObject(true);
			objectOutputStream.writeObject(result);
		
		} catch (IOException e)
		{
			skeleton.service_error(new RMIException(e.getMessage(), e));
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			skeleton.service_error(new RMIException(e.getMessage(), e));
		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			try
			{
				objectOutputStream.writeObject(false);
				objectOutputStream.writeObject(e.getTargetException());
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally {
				try
				{
					if (objectOutputStream != null)
						objectOutputStream.close();
					
					if (objectInputStream != null) objectInputStream.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
	}

}

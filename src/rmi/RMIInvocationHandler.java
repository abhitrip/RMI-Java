/**
 * 
 */
package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Prateek
 *
 */
public class RMIInvocationHandler implements InvocationHandler, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5458017240993189570L;
	private InetSocketAddress address;
	private Class<?> c;
	
	public RMIInvocationHandler(Class<?> c, InetSocketAddress address)
	{
		this.c = c;
		this.address = address;
	}
	
	public InetSocketAddress getAddress() {
		return address;
	}
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if (!isRemoteMethod(method)) {
			return invokeLocalMethod(proxy, method, args);
		}
		return invokeRemoteMethod(proxy, method, args);
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	private Object invokeRemoteMethod(Object proxy, Method method, Object[] args) throws Throwable
	{
		Socket socket = null;
		boolean valueReturned = false;
		Object value = null;
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream inputStream = null;
		try
		{
			socket = new Socket(address.getHostName(), address.getPort());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectOutputStream.flush();
			
			
			objectOutputStream.writeObject(method.getName());
			objectOutputStream.writeObject(method.getParameterTypes());
			objectOutputStream.writeObject(args);
			
			//stops here!
			inputStream = new ObjectInputStream(socket.getInputStream());
			valueReturned = (boolean) inputStream.readObject();
			value = inputStream.readObject();
		} catch (IOException e)
		{
			throw new RMIException(e);
		} 
		finally {
			try
			{
				if (!socket.isClosed()) {
					if (objectOutputStream != null)
						objectOutputStream.close();
					
					if (inputStream != null) inputStream.close();
					if (socket != null)
						socket.close();
				}
			} catch (IOException e)
			{
				System.out.println("Error closeing socket: " + e.getMessage());
			}
		}
		if (!valueReturned) {
			throw (Throwable)value;
		}
		return value;
	}

	/**
	 * @param method
	 * @return
	 */
	private boolean isRemoteMethod(Method method)
	{
		Class<?>[] exceptionTypes = method.getExceptionTypes();
		for (Class<?> e : exceptionTypes) {
			if (e.equals(RMIException.class)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	private Object invokeLocalMethod(Object proxy, Method method, Object[] args)
	{
		if (method.getName().equals("equals"))
			return equals(proxy, method, args);
		
		if (method.getName().equals("toString"))
			return toString(proxy, method, args);
		
		if (method.getName().equals("hashCode"))
			return hashCode(proxy, method, args);
		try
		{
			return method.invoke(Proxy.getInvocationHandler(proxy), args);
		} catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	private Object equals(Object proxy, Method method, Object[] args)
	{
		if (args[0] == null)
			return false;
		if (!proxy.getClass().equals(args[0].getClass()))
			return false;
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(args[0]);
		if (! (invocationHandler instanceof RMIInvocationHandler))
			return false;
		if ( !((RMIInvocationHandler)invocationHandler).getAddress().equals(address))
			return false;
		return true;
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	private Object toString(Object proxy, Method method, Object[] args)
	{
		return c.getSimpleName() + " " + address.getHostName() + " " + address.getPort();
	}

	/**
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	private Object hashCode(Object proxy, Method method, Object[] args)
	{
		return address == null ? 0 : address.hashCode();
	}

}

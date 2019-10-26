import java.rmi.*;
import java.net.*;

public interface IRegistrador extends Remote{
	public void registerStation(ISonda estacion) throws RemoteException, MalformedURLException ;
} 
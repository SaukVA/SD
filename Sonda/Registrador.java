import java.rmi.*;
import java.net.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.io.*;

public class Registrador extends UnicastRemoteObject implements IRegistrador{
    public Registry registry;

	public Registrador(Registry r) throws RemoteException {
		super();
		registry = r;
	}

    public void registerStation(ISonda s) throws RemoteException, MalformedURLException {
		int id;
		try {
			id = s.getId();
			Naming.rebind(Integer.toString(id), s);
		} catch (RemoteException re) {
			re.printStackTrace();
			throw re;
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			throw mue;
		}
		System.out.println("Se acaba de registrar la sonda con ID: " + id);
	}

    public static void main(String[] args) throws Exception {
		
		final Registry registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
		Registrador r = new Registrador(registry);
		registry.rebind("/Sonda", r);
		
	}
}
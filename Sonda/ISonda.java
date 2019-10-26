import java.rmi.Remote;

public interface ISonda extends Remote{

    public int getId() throws java.rmi.RemoteException;
    public void setVolumen(int v) throws java.rmi.RemoteException;
    public void setLuz(int l) throws java.rmi.RemoteException;
    public void setUltimaFecha(String f) throws java.rmi.RemoteException;
    public int getVolumen () throws java.rmi.RemoteException;
    public int getLuz() throws java.rmi.RemoteException;
    public String getUltimaFecha () throws java.rmi.RemoteException;
    public String getFecha() throws java.rmi.RemoteException;
    public void RecogerDatos() throws Exception, java.rmi.RemoteException;   
}
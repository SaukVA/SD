import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.rmi.registry.*;
import java.io.Serializable;  
import java.rmi.*;
import java.rmi.server.*;


public class Sonda extends UnicastRemoteObject implements ISonda,  Serializable{

    private int id;
    private int vol;
    private String ulFecha;
    private int luz;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); 

    public Sonda (int id) throws RemoteException{
        super();
        this.id = id;
        try{
            RecogerDatos();
        }
        catch(Exception e){
            this.vol = 0;
            this.ulFecha = LocalDateTime.now().format(format); 
            this.luz = 0;
        }
    }

    public int getId(){return id;}
    
    public void RecogerDatos() throws Exception{

		try{
			FileReader f = new FileReader("./sensor" + this.id + ".txt");
	        BufferedReader b = new BufferedReader(f);
	        String cadena = "";
	        int i = 0;

	        while((cadena = b.readLine())!=null) {
	            if(i == 0){
	            	setVolumen(Integer.parseInt(cadena));
	            }
	            if(i == 1){
	            	setUltimaFecha(cadena);
	            }
	            if(i == 2){
	            	setLuz(Integer.parseInt(cadena));
	            }
	            i++;
	        }
	        b.close();
	        f.close();
	    }
	    catch(Exception ex){
	    	throw new Exception();
	    }
	}

    public void ColocarDatos(){

		try{
            File f = new File("./sensor" + this.id + ".txt");
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(f));
			String contenido = vol + "\n" + ulFecha + "\n" + luz;
			bw.write(contenido);
			bw.close(); 
		}
		catch(Exception ex){
			System.out.println("No se ha podido leer/crear el archibo. Error: " + ex.toString());
		}
	}

    public void setVolumen(int v){
        if(v>=0 && v<=100){this.vol = v;}
        else{ this.vol = 90;}
        ColocarDatos();
    }

    public void setLuz(int l){
        if(l>=0 && l<=65535){this.luz = l;}
        else{ this.luz = 0;}
        ColocarDatos();
    }

    public void setUltimaFecha(String f){
        try{ulFecha = f;}
        catch(Exception e){ ulFecha = LocalDateTime.now().format(format); }
        ColocarDatos();
    }

    public int getVolumen (){
        try{RecogerDatos();}
        catch(Exception ex){
			System.out.println("No se ha podido leer desde la pantalla el fichero: "+ex);
		}   
        return vol;
    }

    public int getLuz(){
        try{RecogerDatos();}
        catch(Exception ex){
			System.out.println("No se ha podido leer desde la pantalla el fichero: "+ex);
		}   
        return luz;
    }

    public String getUltimaFecha (){
        try{RecogerDatos();}
        catch(Exception ex){
			System.out.println("No se ha podido leer desde la pantalla el fichero: "+ex);
		}   
        return ulFecha;
    }

    public String getFecha(){
        return LocalDateTime.now().format(format);
    }

    public static void main(String[] args) throws Exception {
        try{
            if (args.length < 2) {
                System.out.println("ERROR: java -cp . -Djava.rmi.server.hostname=[ip_sonda] Sonda [ip_registrador] [id_sonda]");
                System.exit(1);
            }
            final int id = Integer.parseInt(args[1]);
            final String host = args[0];

            final Registry registry = LocateRegistry.getRegistry(host,Registry.REGISTRY_PORT);
            final Sonda s = new Sonda(id);

            final IRegistrador master = (IRegistrador)registry.lookup("/Sonda");
            master.registerStation(s);

            System.out.println("Se ha registrado la sonda " + id + " en el servidor de nombres.");
        }
        catch(Exception e){
            System.out.println("No se ha podido registrar la sonda");
            System.exit(1);
        }
    }

}
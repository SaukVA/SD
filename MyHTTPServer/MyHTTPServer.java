import java.net.*;
import java.io.*;


public class MyHTTPServer{

    public static void OpenServer(int puerto, int hilos) {

        try{
			ServerSocket skServidor = new ServerSocket(puerto);
			System.out.println("Estamos escuchando en el puerto: " + puerto + ".");

			for(;;){
                Socket skCliente = skServidor.accept();
                System.out.println("Sirviendo cliente...");
                if(Thread.activeCount () < hilos){
                    //Thread t = new HiloServidor(skCliente, puerto);
			        //t.start();
                }
			}
		}
		catch(Exception e){
			System.out.println("No se ha podido iniciar el ServidorHttp. Error: " + e.toString());
		}
    }

    public static void main(String[] args) {
        String puerto = "";
        String hilos = "";

        try {
            if (args.length < 2) {
                System.out.println("ERROR: ./MyHTTPServer [puerto_servidor] [numero_hilos]");
                System.exit(1);
            }

            puerto = args[0];
            hilos = args[1];
            System.out.println("INICIANDO MYHTTPSERVER");
            OpenServer(Integer.parseInt(puerto), Integer.parseInt(puerto));
        }
        catch(Exception e){
            System.out.println("Error: " + e.toString());
        }
    }
}
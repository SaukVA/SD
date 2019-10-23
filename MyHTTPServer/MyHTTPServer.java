import java.net.*;
import java.io.*;


public class MyHTTPServer{

    public static void OpenServer(int puerto, int hilos, String ip_C, int puerto_C) {

        try{
			ServerSocket skServidor = new ServerSocket(puerto);
			System.out.println("Estamos escuchando en el puerto: " + puerto + ".");

			for(;;){
                //Thread t = null;
                Socket skCliente = skServidor.accept();
                if(Thread.activeCount () < hilos){
                    Thread t = new Hilo(skCliente, ip_C, puerto_C);
			        t.start();
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
        String puerto_C = "";
        String ip_C = "";

        try {
            if (args.length < 4) {
                System.out.println("ERROR: ./MyHTTPServer [puerto_servidor] [numero_hilos] [ip_controler] [puerto_controler]");
                System.exit(1);
            }

            puerto = args[0];
            hilos = args[1];
            ip_C = args[2];
            puerto_C = args[3];
            System.out.println("INICIANDO MYHTTPSERVER");
            OpenServer(Integer.parseInt(puerto), Integer.parseInt(hilos), ip_C, Integer.parseInt(puerto_C));
        }
        catch(Exception e){
            System.out.println("Error: " + e.toString());
        }
    }
}
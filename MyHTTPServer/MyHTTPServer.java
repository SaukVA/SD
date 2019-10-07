import java.net.*;
import java.io.*;


public class MyHTTPServer{

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static final String ANSI_RESET = "\u001B[0m";

    public static String ObtenerPeticion(int entrada, byte[] bytedata){

        String cad = "";
        if (entrada>0){cad=new String(bytedata,0,(entrada-1));}
        String[] Cadenas = cad.split("\n"); 

        System.out.println(Cadenas[0]);

        return null;
    }

    public static void OpenServer(int puerto) {
        try{
            DataInputStream entrada=null;
            String peticion = "";
            int leido = 0;
            byte bytedata[]=new byte[256]; 

			ServerSocket skServidor = new ServerSocket(puerto);
			System.out.println("Estamos escuchando en el puerto: " + puerto + ".");

			for(;;){
                Socket skCliente = skServidor.accept();
                entrada=new DataInputStream(skCliente.getInputStream());
                leido=entrada.read(bytedata);
                peticion = ObtenerPeticion(leido, bytedata);
                System.out.println(ANSI_GREEN + "Entrada GET" + ANSI_BLACK);
                //Sacamos lo que hemos recibido desde el cliente
                //System.out.println("TODO BIEN");
			   	//Thread t = new HiloServidor(skCliente, puerto);
			    //t.start();
			}
		}
		catch(Exception e){
			System.out.println(ANSI_RED + "No se ha podido iniciar el ServidorHttp. Error: " + e.toString() + ANSI_RESET );
		}
    }

    public static void main(String[] args) {
        String puerto = "";

        try {
            if (args.length < 1) {
                System.out.println(ANSI_RED + "ERROR: " + ANSI_RESET + "./MyHTTPServer puerto_servidor");
                System.exit(1);
            }

            puerto = args[0];
            System.out.println(ANSI_GREEN + "INICIANDO MYHTTPSERVER" + ANSI_RESET);
            System.out.print(ANSI_WHITE_BACKGROUND + ANSI_BLACK);
            OpenServer(Integer.parseInt(puerto));
            System.out.print(ANSI_RESET);
        }
        catch(Exception e){
            System.out.println(ANSI_RED + "Error: " + e.toString() + ANSI_RESET);
        }
    }
}
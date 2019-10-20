import java.io.*;
import java.net.*;

public class Controller {

    public String leeSocket (Socket p_sk, String p_Datos){
		try
		{
			InputStream aux = p_sk.getInputStream();
			DataInputStream flujo = new DataInputStream( aux );
			p_Datos = new String();
			p_Datos = flujo.readUTF();
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
      return p_Datos;
	}

	public void escribeSocket (Socket p_sk, String p_Datos){
		try
		{
			OutputStream aux = p_sk.getOutputStream();
			DataOutputStream flujo= new DataOutputStream( aux );
			flujo.writeUTF(p_Datos);      
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		return;
	}

    public String imprimirIndice(){
        String pagina = "";

        try{
            BufferedReader br1 = new BufferedReader(new FileReader("./index.html"));
            String data = "";

            pagina += "HTTP/1.1 200 OK\n";
            pagina += "Server: MyHTTPServer\n";			
            pagina += "Content-Type: text/html; charset=utf-8\n";
            pagina += "\n";

            data = br1.readLine();
            while (data != null) {
                pagina += data + "\n";
                data = br1.readLine();
            }
        }
        catch(Exception e){
            System.out.println("ERROR");
            pagina = "";
        }
        return pagina;
    }

    public String imprimirError(){
        String pagina = "";

        try{
            BufferedReader br1 = new BufferedReader(new FileReader("./error.html"));
            String data = "";

            pagina += "HTTP/1.1 404 Not Found\n";
            pagina += "Server: MyHTTPServer\n";			
            pagina += "Content-Type: text/html; charset=utf-8\n";
            pagina += "\n";

            data = br1.readLine();
            while (data != null) {
                pagina += data + "\n";
                data = br1.readLine();
            }
        }
        catch(Exception e){
            System.out.println("ERROR");
            pagina = "";
        }
        return pagina;
    }

    public String generarPagina(String solicitu){
        String pagina = "";
        
        if(solicitu.equals("/index")){
            pagina = imprimirIndice();
        }
        else /*if(solicitu.equals("/error"))*/{
            pagina = imprimirError();
        }
        return pagina;
    }

    public static void main(String[] args) {

        String cadena = "";
        String pagina = "";
        
        try{
            Controller cont = new Controller();
             if (args.length < 1) {
                System.out.println("ERROR: ./Controller [puerto_Controller]");
                System.exit(1);
            }
            ServerSocket skController = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("CONTROLLER ENCENDIDO");
            System.out.println("Escucho el puerto " + args[0]);

            for(;;){
                Socket skCliente = skController.accept();
                
                cadena = cont.leeSocket(skCliente,cadena);
                System.out.println("Sirviendo peticion: " + cadena);
                pagina = cont.generarPagina(cadena);
                cont.escribeSocket(skCliente,pagina);

                skCliente.close();
            }
        }
        catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
    }
}
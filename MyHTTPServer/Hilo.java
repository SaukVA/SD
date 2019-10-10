import java.lang.Exception;
import java.net.*;
import java.io.*;

public class Hilo extends Thread{
    private Socket cliente;
    private final Object lock = new Object();

    public Hilo (Socket cliente){
        this.cliente = cliente;
    }

    public String leer (Socket cliente, String mensaje)
	{
		try
		{
			InputStream aux = cliente.getInputStream();
			DataInputStream flujo = new DataInputStream( aux );
			mensaje = new String();
			mensaje = flujo.readLine();
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
      return mensaje;
	}

    public void escribe (Socket cliente, String mensaje)
	{
		try
		{
			OutputStream aux = cliente.getOutputStream();
			DataOutputStream flujo= new DataOutputStream( aux );
			flujo.writeUTF(mensaje);      
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		return;
	}

     public String obtenerPagina(String Cadena){
        String [] partes = Cadena.split(" ");
        String pagina = "";

        if(!partes[0].equals("GET")){
            pagina = "error"; 
        }
        else if (partes.length < 2 || partes[1].equals("/") || partes[1].equals("/index")){
            pagina = "index";
        }
        else{
            pagina = partes[1];
        }

        return pagina;
    }

    public void paginaSimple(Socket cliente){

        try{
            PrintWriter out = new PrintWriter(cliente.getOutputStream());
            BufferedReader br1 = new BufferedReader(new FileReader("./index.html"));
            String data = "";

            out.println("HTTP/1.1 200 OK");
			out.println("Content-Type: text/html; charset=utf-8");
			out.println("Server: MyHTTPServer");
            out.println("");
		    data = br1.readLine();
		    while (data != null) {
		       	out.println(data);
	        	data = br1.readLine();
	        }

            out.flush();
            out.close();
        }
        catch(Exception e){
            System.out.println("Error: " + e.toString());
        }
    }

    public void run(){
        String pagina = "";
        String Cadena = "";

        try{
            Cadena = this.leer(this.cliente,Cadena);
            pagina = this.obtenerPagina(Cadena);
            if(!pagina.equals("/favicon.ico")){
                System.out.println("Solicitud: " + pagina);                    this.paginaSimple(this.cliente);
            }
            cliente.close();
        }
        catch(Exception e){
			System.out.println("Error1: " + e.toString());
		}
    }
}
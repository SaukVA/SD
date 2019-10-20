import java.lang.Exception;
import java.net.*;
import java.io.*;

public class Hilo extends Thread{
    private Socket cliente;
    private int puerto_C;
    private String ip_C;

    public Hilo (Socket cliente,String ip_C,int puerto_C){
        this.cliente = cliente;
        this.puerto_C = puerto_C;
        this.ip_C = ip_C;
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

    public void escribeControler (Socket p_sk, String p_Datos)
	{
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

    public String leeControler (Socket p_sk, String p_Datos)
	{
		try
		{
			InputStream aux = p_sk.getInputStream();
			DataInputStream flujo = new DataInputStream( aux );
			p_Datos = flujo.readUTF();
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
      return p_Datos;
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

    public void paginaError(Socket cliente){

        try{
            BufferedReader br1 = new BufferedReader(new FileReader("./errorController.html"));
            PrintWriter out = new PrintWriter(cliente.getOutputStream());
            String data = "";

            out.println("HTTP/1.1 405 Method Not Allowed");
            out.println("Server: MyHTTPServer");
			out.println("Content-Type: text/html; charset=utf-8");
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

    public void ImprimirPagina (String pagina, Socket cliente, String controler, int puerto){
        
        try{
            PrintWriter out = new PrintWriter(cliente.getOutputStream());
            Socket skControler = new Socket(controler,puerto);
            escribeControler(skControler,pagina);
            pagina = "";
            pagina = leeControler(skControler,pagina);
            String [] data = pagina.split("\n");

            for(int i=0; i<data.length; i++){
                out.println(data[i]);
            }
            out.flush();
            out.close();
        }
        catch(Exception e){
            this.paginaError(cliente);
        }
    }

    public void run(){
        String pagina = "";
        String Cadena = "";

        try{
            Cadena = this.leer(this.cliente,Cadena);
            pagina = this.obtenerPagina(Cadena);
            if(!pagina.equals("/favicon.ico")){
                System.out.println("Solicitud: " + pagina);                    
                //this.paginaError(this.cliente);
                this.ImprimirPagina(pagina,this.cliente,this.ip_C,this.puerto_C);
            }
            cliente.close();
        }
        catch(Exception e){
			System.out.println("Error1: " + e.toString());
		}
    }
}
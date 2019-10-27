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

    public String leer (Socket cliente, String mensaje){
		try
		{
			InputStream aux = cliente.getInputStream();
			BufferedReader flujo = new BufferedReader(new InputStreamReader(aux));
			mensaje = flujo.readLine();
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
      return mensaje;
	}

    public void escribe (Socket p_sk, String p_Datos){
        try{
            OutputStream aux = p_sk.getOutputStream();
            PrintWriter flujo= new PrintWriter(new OutputStreamWriter(aux));
            flujo.println(p_Datos); 
            flujo.flush();    
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return;
    }

    public String leeControler (Socket p_sk, String p_Datos){
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

    public void escribeControler (Socket p_sk, String p_Datos){
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

     public String obtenerPagina(String Cadena){
        String [] partes = Cadena.split(" ");
        String pagina = "";

        if(!partes[0].equals("GET")){
            pagina = "/error"; 
        }
        else if (partes.length < 2 || partes[1].equals("/") || partes[1].equals("/index")){
            pagina = "/index";
        }
        else{
            pagina = partes[1];
        }

        return pagina;
    }

    public String addPagina(String file){

        String data = "";
        String pagina = "";

        try{
            FileReader fichero = new FileReader(file);
            BufferedReader br1 = new BufferedReader(fichero);

            data = br1.readLine();
	        while (data != null) {
	           	pagina += data;
		    	data = br1.readLine();
		    }
            br1.close();

        }
        catch(Exception e){
            System.out.println("ERROR: " + e.toString());
        }
        return pagina;
    }

    public String ImprimirPagina (String pagina, Socket cliente, String controler, int puerto){
        
        String resul = "";

        try{
            if(pagina == "/index"){
                resul = "HTTP/1.1 200 OK\r\nServer: MyHTTPServer\r\nContent-Type: text/html; charset=utf-8\r\n\r\n";
                resul += addPagina("./index.html");
            }
            else if(pagina == "/error"){
                resul = "HTTP/1.1 405 Metod not found\nServer: MyHTTPServer\nContent-Type: text/html; charset=utf-8\n\n";
                resul += addPagina("./errorGET.html");
            }
            else{
                Socket skControler = new Socket(controler,puerto);
                this.escribeControler(skControler,pagina);
                pagina = "";
                resul = this.leeControler(skControler,pagina);
            }
        }
        catch(Exception e){
            resul = "HTTP/1.1 409 Conflict\r\nServer: MyHTTPServer\r\nContent-Type: text/html; charset=utf-8\r\n\r\n";
            resul += addPagina("./errorController.html");
        }
        return resul;
    }

    public void run(){
        String pagina = "";
        String Cadena = "";

        try{
            Cadena = this.leer(cliente,Cadena);
            Cadena = this.obtenerPagina(Cadena);
            if(!Cadena.equals("/favicon.ico")){
                System.out.println("Solicitud: " + Cadena);
            }               
            //pagina = this.paginaError(cliente);
            pagina = this.ImprimirPagina(Cadena,cliente,ip_C,puerto_C);
            escribe(cliente, pagina);
            cliente.close();
        }
        catch(Exception e){
			System.out.println("Error1: " + e.toString());
		}
    }
}
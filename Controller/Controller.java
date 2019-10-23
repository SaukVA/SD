import java.io.*;
import java.net.*;

public class Controller {
    String respuestaOK = "HTTP/1.1 200 OK\nServer: MyHTTPServer\nContent-Type: text/html; charset=utf-8\n\n";
    String respuestaError = "HTTP/1.1 404 Not Found\nServer: MyHTTPServer\nContent-Type: text/html; charset=utf-8\n\n";
    
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

    public String imprimirController(){
        String pagina = "";
        pagina += respuestaOK;
        pagina += addPagina("./css.html");
        pagina += "<body>\n"
                    +"<center>\n"
                        +"<h1>SENSORES</h1>\n"
                        +"<ul>\n"
                        +"<li><a class=\"active\" href=\"/index\">Inicio</a></li>\n";

        for(int i =0; i< 4; i++){
            pagina += "<li><a href=\"/controladorSD/all?sonda=" + i + "\">Sensores " + i + "</a></li>";
        }
                    pagina += "</ul>\n"
                    +"</center>\n"
                +"</body>\n"
                +"</html>\n";
        return pagina;
    }

    public String sondaTotal(String num){
        String pagina = "";
        pagina += respuestaOK;
        pagina += addPagina("./css.html");

        pagina += "<body>\n"
                    +"<center>\n"
                        +"<h1><u>SENSOR " + num + "</u></h1>\n"
                        +"<ul>\n"
                        +"<li><a class=\"active\" href=\"/index\">Inicio</a></li>\n"
                        +"<li><a  href=\"/controladorSD/volumen?sonda=" + num + "\">Volumen</a></li>\n"
                        +"<li><a  href=\"/controladorSD/fecha?sonda=" + num + "\">Fecha</a></li>\n"
                        +"<li><a  href=\"/controladorSD/ultimafecha?sonda=" + num + "\">Ultima modificación</a></li>\n"
                        +"<li><a  href=\"/controladorSD/luz?sonda=" + num + "\">LED</a></li>\n"
                        +"<li><a  href=\"/controladorSD\">Atras</a></li>\n"
                        +"</ul>\n"
                    +"</center>\n"
                +"</body>\n"
            +"</html>\n";
        return pagina;
    }

    public String sondaVolumenFechaUltimafechaLuz(String num, String tipo){
        String pagina = "";
        pagina += respuestaOK;
        pagina += addPagina("./css.html");
        pagina += "<body>\n"
                    +"<center>\n"
                        +"<h1><u>SENSOR " + num + "</u></h1>\n"
                        +"<h2>" + tipo + ": </h2>\n"
                        +"<ul>\n"
                        +"<li><a class=\"active\" href=\"/index\">Inicio</a></li>\n"
                        +"<li><a  href=\"/controladorSD\">Sensores</a></li>\n"
                        +"</ul>\n"
                    +"</center>\n"
                +"</body>\n"
            +"</html>\n";
        return pagina;
    }

    public Boolean exiteSensor (int estacion) {
        //implementar
        return true;
    }

    public String generarPagina(String solicitud){
        String pagina = "";
        try{
            if(solicitud.equals("/index")){
                pagina += respuestaOK;
                pagina += addPagina("./index.html");
            }
            else if(solicitud.equals("/error")){
                pagina += respuestaError;
                pagina += addPagina("./error.html");
            }
            else {
                if(solicitud.equals("/controladorSD")){
                    pagina = imprimirController();
                }
                else if(solicitud.startsWith("/controladorSD/all?sonda=")){
                    String [] partes = solicitud.split("=");
                    if(exiteSensor(Integer.parseInt(partes[1]))){
                        pagina = sondaTotal(partes[1]);
                    }
                    else{ 
                        pagina += respuestaError;
                        pagina += addPagina("./error.html"); 
                    }
                }
                else if(solicitud.startsWith("/controladorSD/volumen?sonda=")){
                    String [] partes = solicitud.split("=");
                    if(exiteSensor(Integer.parseInt(partes[1]))){
                        pagina = sondaVolumenFechaUltimafechaLuz(partes[1],"Volumen");
                    }
                    else{ 
                        pagina += respuestaError;
                        pagina += addPagina("./error.html"); 
                    }
                }
                else if(solicitud.startsWith("/controladorSD/fecha?sonda=")){
                    String [] partes = solicitud.split("=");
                    if(exiteSensor(Integer.parseInt(partes[1]))){
                        pagina = sondaVolumenFechaUltimafechaLuz(partes[1], "Fecha");
                    }
                    else{ 
                        pagina += respuestaError;
                        pagina += addPagina("./error.html"); 
                    }
                }
                else if(solicitud.startsWith("/controladorSD/ultimafecha?sonda=")){
                    String [] partes = solicitud.split("=");
                    if(exiteSensor(Integer.parseInt(partes[1]))){
                        pagina = sondaVolumenFechaUltimafechaLuz(partes[1], "Ultima Fecha");
                    }
                    else{ 
                        pagina += respuestaError;             
                        pagina += addPagina("./error.html"); 
                    }
                }
                else if(solicitud.startsWith("/controladorSD/luz?sonda=")){
                    String [] partes = solicitud.split("=");
                    if(exiteSensor(Integer.parseInt(partes[1]))){
                        pagina = sondaVolumenFechaUltimafechaLuz(partes[1], "Led");
                    }
                    else{ 
                        pagina += respuestaError;             
                        pagina += addPagina("./error.html"); 
                    }
                }
                else if(solicitud.startsWith("/controladorSD/setluz=")){
                    String [] partes = solicitud.split("=");
                    if(exiteSensor(Integer.parseInt(partes[1]))){
                        //pagina = imprimirIndice();
                    }
                    else{ 
                        pagina += respuestaError;             
                        pagina += addPagina("./error.html"); 
                    }
                }
                else{
                    pagina += respuestaError;            
                    pagina += addPagina("./error.html");
                }
            }
        }catch(Exception e){
            pagina += respuestaError;             
            pagina += addPagina("./error.html");
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
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Controller {
    private String respuestaOK = "HTTP/1.1 200 OK\nServer: MyHTTPServer\nContent-Type: text/html; charset=utf-8\n\n";
    private String respuestaErrorGET = "HTTP/1.1 405 Metod not found\nServer: MyHTTPServer\nContent-Type: text/html; charset=utf-8\n\n";
    private String respuestaError = "HTTP/1.1 404 Not Found\nServer: MyHTTPServer\nContent-Type: text/html; charset=utf-8\n\n";
    private static String ip_registrador;
    
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
                    try{
                        final Registry registry = LocateRegistry.getRegistry(ip_registrador,Registry.REGISTRY_PORT);
                        final String[] NombresDondas = registry.list();
                        for(String sonda : NombresDondas) {
                            try{
                                Object obj = registry.lookup(sonda);
                                if(obj instanceof ISonda){
                                    final ISonda server = (ISonda)obj;
                                    pagina += "<li><a href=\"/controladorSD/all?sonda=" + server.getId() + "\">Sonda " + server.getId() + "</a></li>\n";
                                }
                            }catch(Exception e){}
                        }
                        pagina += "</ul>\n";
                    }
                    catch(Exception e){
                        pagina += "<li><a  href=\"/controladorSD\">Recargar</a></li>\n"
                            + "</ul>\n"
                            + "<h4>No se ha podido conectar con las Sondas</h4>\n";
                    }
                        
                        pagina +="</center>\n"
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

    public String sondaVolumenFechaUltimafechaLuz(ISonda s, String tipo) throws Exception{
        String pagina = "";
        pagina += respuestaOK;
        pagina += addPagina("./css.html");
        pagina += "<body>\n"
                    +"<center>\n"
                        +"<h1><u>SENSOR " + s.getId() + "</u></h1>\n"
                        +"<h2>" + tipo + ": ";
                        if(tipo == "Volumen"){pagina += s.getVolumen() +"</h2>\n";}
                        else if(tipo == "Fecha"){pagina += s.getFecha() +"</h2>\n";}
                        else if(tipo == "Ultima Fecha"){pagina += s.getUltimaFecha() +"</h2>\n";}
                        else if(tipo == "Volumen"){pagina += s.getVolumen() +"</h2>\n";}
                        else if(tipo == "Led"){
                            String color = "green";
                            if(s.getLuz() > 32768){color = "red";}
                            pagina += s.getLuz() + "</h2>\n"
                                   + "<div class=\"circulo\" style=\"background: " + color + ";\" ></div>\n<h2> </h2>\n";
                        }
                        pagina += "<ul>\n"
                        +"<li><a class=\"active\" href=\"/index\">Inicio</a></li>\n"
                        +"<li><a  href=\"/controladorSD/all?sonda=" + s.getId() + "\">Atras</a></li>\n"
                        +"<li><a  href=\"/controladorSD\">Sensores</a></li>\n"
                        +"</ul>\n"
                    +"</center>\n"
                +"</body>\n"
            +"</html>\n";
        return pagina;
    }

    public ISonda exiteSensor(int s) throws Exception{
		ISonda res = null;
		final Registry registry = LocateRegistry.getRegistry(ip_registrador,Registry.REGISTRY_PORT);
		final String[] NombresDondas = registry.list();

		for(String sonda : NombresDondas) {
			Object obj = registry.lookup(sonda);
			if(obj instanceof ISonda){
				final ISonda server = (ISonda)obj;
				if(server.getId() == s){
					res = server;
				}
			}
		}
		return res;
	}


    public String generarPagina(String solicitud){
        String pagina = "";
        ISonda s = null;
        try{
            if(solicitud.equals("/controladorSD")){
                pagina = imprimirController();
            }
            else if(solicitud.startsWith("/controladorSD/all?sonda=")){
                String [] partes = solicitud.split("=");
                s = exiteSensor(Integer.parseInt(partes[1]));
                if(s != null){
                    pagina = sondaTotal(partes[1]);
                }
                else{ 
                    pagina += respuestaError;
                    pagina += addPagina("./error.html"); 
                }
            }
            else if(solicitud.startsWith("/controladorSD/volumen?sonda=")){
                String [] partes = solicitud.split("=");
                s = exiteSensor(Integer.parseInt(partes[1]));
                if(s != null){
                    pagina = sondaVolumenFechaUltimafechaLuz(s,"Volumen");
                }
                else{ 
                    pagina += respuestaError;
                    pagina += addPagina("./error.html"); 
                }
            }
            else if(solicitud.startsWith("/controladorSD/fecha?sonda=")){
                String [] partes = solicitud.split("=");
                s = exiteSensor(Integer.parseInt(partes[1]));
                if(s != null){
                    pagina = sondaVolumenFechaUltimafechaLuz(s, "Fecha");
                }
                else{ 
                    pagina += respuestaError;
                    pagina += addPagina("./error.html"); 
                }
            }
            else if(solicitud.startsWith("/controladorSD/ultimafecha?sonda=")){
                String [] partes = solicitud.split("=");
                s = exiteSensor(Integer.parseInt(partes[1]));
                if(s != null){
                    pagina = sondaVolumenFechaUltimafechaLuz(s, "Ultima Fecha");
                }
                else{ 
                    pagina += respuestaError;             
                    pagina += addPagina("./error.html"); 
                }
            }
            else if(solicitud.startsWith("/controladorSD/luz?sonda=")){
                String [] partes = solicitud.split("=");
                s = exiteSensor(Integer.parseInt(partes[1]));
                if(s != null){
                    pagina = sondaVolumenFechaUltimafechaLuz(s, "Led");
                }
                else{ 
                    pagina += respuestaError;             
                    pagina += addPagina("./error.html"); 
                }
            }
            else if(solicitud.startsWith("/controladorSD/setluz=")){
                String n = solicitud.substring(22,solicitud.length());
                n = n.replace("?sonda","");
                String [] partes = n.split("=");
                s = exiteSensor(Integer.parseInt(partes[1]));
                if(s != null){
                    s.setLuz(Integer.parseInt(partes[0]));
                    s.setUltimaFecha(s.getFecha());
                    pagina = sondaVolumenFechaUltimafechaLuz(s, "Led");
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
             if (args.length < 2) {
                System.out.println("ERROR: ./Controller [puerto_Controller] [ip_registrador]");
                System.exit(1);
            }
            ServerSocket skController = new ServerSocket(Integer.parseInt(args[0]));
            ip_registrador = args[1];
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
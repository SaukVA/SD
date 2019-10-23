import java.io.*;
import java.net.*;
import java.util.Scanner;
//import org.omg.CosNaming.BindingType;
import java.net.ServerSocket;
import java.net.Socket;


class Httpserver {
    public static void main(String[] args){

        int maxclients = 0;
        int clients = 0;
        int nconexiones =0;
        String pcontroller;
        String puertoServidor;
        String ipServidor;
        String ipcontroller;

        /*
		* Solicitamos al usuario que introduzca el numero de conexiones simultaneas, ip servidor, puerto servidor puerto controller e ip controller
        * 
        */

        String variableString;
        Scanner entrada=new Scanner(System.in);// se declara e inicializa una instancia  de la clase Scanner.

        System.out.print("Numero de conexiones simultaneas (Ej: ' 10 '): \n");
        maxclients = Integer.parseInt(entrada.next());

        System.out.print("IP servidor web (Ej: '127.0.0.1'): \n");
        ipServidor = entrada.next();

        System.out.print("Puerto escucha el MyHTTPSERVER (Ej: '8080'): \n");
        puertoServidor = entrada.next();

        System.out.print("Puerto escucha el Controller (Ej: '8081'): \n");
        pcontroller = entrada.next();
        
        System.out.print("IP controller (Ej: '127.0.0.1'): \n");
        ipcontroller = (entrada.next());

        
        try
        {
            Httpserver sv = new Httpserver();
            ServerSocket httpsocket = new ServerSocket(Integer.parseInt(puertoServidor), maxclients);
            System.out.println("Escuchando puerto " + puertoServidor);
            

            while(true)
            {
                Thread t = null;
                Socket soket = httpsocket.accept();
                System.out.println("Atendiendo peticion :D");
                t = new HiloServidor(soket, ipServidor, pcontroller, ipcontroller);
                t.start();

                nconexiones++;
                if (nconexiones>maxclients)
                {
                    System.exit(0);
                }

            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
}


class HiloServidor extends Thread {

    private String p_controller;
    private String ip_controller;

    private Socket sclient;
    private String ip;

    private String ip_RMI;
    private String p_RMI;

    public HiloServidor(Socket cliente)
    {
        this.sclient = cliente;
        this.ip = "127.0.0.1";
        this.p_controller = "8081";
        this.ip_controller = "127.0.0.1";
    }
    
    public HiloServidor(Socket p_cliente, String ip, String p_controller, String ip_controller)
    {
        this.p_controller = p_controller;
        this.ip_controller = ip_controller;
        this.sclient = p_cliente;
        this.ip = ip;

    }
    
    public String leeSocket (Socket p_sk, String p_Datos)
    {
        try{
            InputStream aux = p_sk.getInputStream();
            BufferedReader flujo = new BufferedReader(new InputStreamReader(aux));
            p_Datos = flujo.readLine();
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
      return p_Datos;
    }

    public void escribeSocket (Socket p_sk, String p_Datos)
    {
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

    //Lee el fichero que le pasamos por parametro
    public String leerFichero(String file){

        String aux = "";
        String aux2 = "";
        String resultado = "";
        

        try
        {
            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            while((aux = b.readLine()) != null){
                resultado += aux;
            }
            b.close();
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        return (resultado);
    }
    
    public void run() {

        String type = "html";
        String cad="";
        String jun = "";
        String result = ""; 
        try
        {
            cad = this.leeSocket(sclient, cad);
            RespuestaHttp req = new RespuestaHttp(cad);
            //Comprobamos que el metodo demandado es GET
            if(req.getMethod().equals("GET")){
                //En el caso de que se pida el indice, se devuelve el archivo.
                /* Si se solicita un recurso dinamico se conecta con Controller (en la url aparece .../controladorSD/...)
                * Existe un Index por defecto al acceder a la IP:puerto del servidor
                */
                if(req.getURL().equals("/") || req.getURL().equals("/index.html")){
                    result = leerFichero("index.html");
                }
                /*else if(!req.getURL().equals("/") || !req.getURL().equals("/index.html") || !req.getURL().equals("/controladorSD") || !req.getURL().equals("/controladorSD/") || !req.getURL().equals("controladorSD")) {
                	result = leerFichero("error.html");
                }*/
                else
                {
                    if( req.getURL().equals("/controladorSD") || req.getURL().equals("/controladorSD/") || req.getURL().equals("controladorSD") ){
                        try
                        {
                            leerFichero("index.html");
                            Socket sClientController = new Socket(ip_controller, Integer.parseInt(p_controller));
                            escribeSocket(sClientController, "/controladorSD");
                            result = leeSocket(sClientController, req.getURL());
                            sClientController.close();
                        }
                        catch(Exception e){
                            System.out.println(e.toString());
                            
                            try
                            {
                                escribeSocket(sclient, result);
                                sclient.close();
                            }
                            catch(Exception ex){
                                System.out.println(ex.toString());
                            }
                        }
                    }
                    
                    else if( req.getURL().equals("/controladorSD/volumen?sonda=1") || req.getURL().equals("/controladorSD/volumen?sonda=1/") || req.getURL().equals("controladorSD/volumen?sonda=1") ){
                        try
                        {
                            leerFichero("index.html");
                            Socket sClientController = new Socket(ip_controller, Integer.parseInt(p_controller));
                            escribeSocket(sClientController, "/controladorSD/volumen?sonda=1");
                            result = leeSocket(sClientController, req.getURL());
                            sClientController.close();
                        }
                        catch(Exception e){
                            System.out.println(e.toString());
                            
                            try
                            {
                                escribeSocket(sclient, result);
                                sclient.close();
                            }
                            catch(Exception ex){
                                System.out.println(ex.toString());
                            }
                        }
                    }
                    
                    else if( req.getURL().equals("/controladorSD/ultimafecha?sonda=1") || req.getURL().equals("/controladorSD/ultimafecha?sonda=1/") || req.getURL().equals("controladorSD/ultimafecha?sonda=1") ){
                        try
                        {
                            leerFichero("index.html");
                            Socket sClientController = new Socket(ip_controller, Integer.parseInt(p_controller));
                            escribeSocket(sClientController, "/controladorSD/ultimafecha?sonda=1");
                            result = leeSocket(sClientController, req.getURL());
                            sClientController.close();
                        }
                        catch(Exception e){
                            System.out.println(e.toString());
                            
                            try
                            {
                                escribeSocket(sclient, result);
                                sclient.close();
                            }
                            catch(Exception ex){
                                System.out.println(ex.toString());
                            }
                        }
                    }
                    
                    else if( req.getURL().equals("/controladorSD/luz?sonda=1") || req.getURL().equals("/controladorSD/luz?sonda=1/") || req.getURL().equals("controladorSD/luz?sonda=1") ){
                        try
                        {
                            leerFichero("index.html");
                            Socket sClientController = new Socket(ip_controller, Integer.parseInt(p_controller));
                            escribeSocket(sClientController, "/controladorSD/luz?sonda=1");
                            result = leeSocket(sClientController, req.getURL());
                            sClientController.close();
                        }
                        catch(Exception e){
                            System.out.println(e.toString());
                            
                            try
                            {
                                escribeSocket(sclient, result);
                                sclient.close();
                            }
                            catch(Exception ex){
                                System.out.println(ex.toString());
                            }
                        }
                    }

                    else{
                        if(req.getURL().contains(".html")){
                            String aux = req.getURL();
                            String aux5 = req.getURL(); 
                            aux = aux.replace("/", "");
                            aux5 = aux5.replace("/", "");
                            result = leerFichero(aux);

                            /*
                            // ESTO ES POR SI QUIERO QUE SEA TXT PARA MOSTRAR EL TEXTO
                            if(aux.contains(".txt")){
                                type = "plain";
                            }
                            */
                        }
                    }
                }
                // Si pasamos un fichero que no existe entonces es controlado
                /*
                * Si se solicita un recurso inexistente se controla el error
                */
                if(result.isEmpty()){
                    //result = "NO EXISTE EL RECURSO AL QUE ESTAS INTENTANDO ACCEDER";
                	result = leerFichero("error.html");
                }
                else{
                    result = "HTTP/1.1 200 OK\r\n" + 
                    "Connection: close\r\n" +
                    "Content-Length: " + result.length() + "\r\n"+
                    "Content-Type: text/" + type + "; charset=UTF-8\r\n"+
                    "Server: HttpServer/4.20\r\n\r\n" + 
                    result;
                }
            }
            // Si el metodo no es GET entonces controlamos el error

            /*
            * Si se utiliza un comando HTTP distinto de GET se controla el error
            */
            else{
                
                result = "<!DOCTYPE html><html><body> <p>Error 405: metodo incorrecto </p> </body></html>";
            }
            escribeSocket(sclient, result);
            sclient.close();
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
      }

     
}


class RespuestaHttp{

    private String method;
    private String URL;
    


    public String getURL(){
        return this.URL;
    }

    public String getMethod(){
        return this.method;
    }

    public RespuestaHttp(String cadena){
        String[] cad = cadena.split(" ");
        String[] aux = cadena.split(" ");
        method = cad[0];
        URL = cad[1];
    }
}
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class app {

    public static void main(String[] args) {

        show_motion(1);

        String subnet = "";
        boolean port_scan = false;
        boolean ip_scan = false;

        int windows_token = 0;
        int linux_token = 0;
        int macos_token = 0;
        int airplay_token = 0;

        ArrayList<Integer> win = new ArrayList<>();
        ArrayList<Integer> linux_server = new ArrayList<>();
        ArrayList<Integer> macos = new ArrayList<>();

        int SMB = 445;
        int Netbios = 139;
        int RDP = 3389;
        int ssh = 22;
        int ipp = 631;
        int AFP = 548;
        int Vnc = 5900;
        int HTTP = 80;
        int HTTPS = 443;
        int smt = 25;
        int nfs = 2049;
        int Airplay = 7000;
        int Airplay_2 = 5000;
        int airplay_mirroring = 7001;
        win.add(SMB);
        win.add(Netbios);
        win.add(RDP);
        linux_server.add(ssh);
        linux_server.add(smt);
        linux_server.add(nfs);
        linux_server.add(HTTPS);
        linux_server.add(HTTP);
        linux_server.add(Vnc);
        macos.add(ipp);
        macos.add(AFP);
        macos.add(Airplay);
        macos.add(Airplay_2);
        macos.add(airplay_mirroring);



        if(args.length>0 && !args[0].equals("-p")){
            subnet = args[0];
            ip_scan = true;
            System.out.println("set subnet : "+subnet);
        }

        if(args.length>0 && args[0].equals("-p")){
            port_scan = true;
            System.out.println("port scan initialised on following ip : ");
            ip_scan = false;

            if(args.length>1){
                subnet = args[1];
                System.out.println(args[1]);
            }
        }
        else{
            System.out.println("no subnet found or wrong command");
        }

        if(ip_scan){
            System.out.println("ip scan activated");
            scan_ip(subnet,port_scan,win,linux_server,macos);
        }

        if(port_scan){

            show_motion(2);

            for(int z = 0; z < win.size();z++){

                if(isPortOpen(subnet,win.get(z),100)){

                    System.out.println("port "+win.get(z)+" is open");

                    String banner = grabBanner(subnet, win.get(z));

                    if(banner != null){
                        System.out.println("banner: "+banner);
                    }

                    windows_token++;

                }
            }

            for(int z = 0; z < linux_server.size();z++){

                if(isPortOpen(subnet,linux_server.get(z),100)){

                    System.out.println("port "+linux_server.get(z)+" is open");

                    String banner = grabBanner(subnet, linux_server.get(z));

                    if(banner != null){
                        System.out.println("banner: "+banner);
                    }

                    if(linux_server.get(z)==80 || linux_server.get(z)==443){

                        String server = grabHTTPServer(subnet, linux_server.get(z));

                        if(server != null){
                            System.out.println(server);
                        }
                    }

                    linux_token++;

                }
            }

            for(int z = 0; z < macos.size();z++){

                if(isPortOpen(subnet,macos.get(z),100)){

                    System.out.println("port "+macos.get(z)+" is open");

                    String banner = grabBanner(subnet, macos.get(z));

                    if(banner != null){
                        System.out.println("banner: "+banner);
                    }

                    macos_token++;

                    if(macos.get(z)==Airplay || macos.get(z)==Airplay_2 || macos.get(z)==airplay_mirroring){
                        airplay_token++;
                    }
                }
            }

            int overall_token = windows_token + linux_token + macos_token;

            System.out.println("random os guessing : ");

            if(overall_token>0){

                System.out.println("windows probability : "+(windows_token*100)/overall_token+"%");
                System.out.println("linux probability : "+(linux_token*100)/overall_token+"%");
                System.out.println("macos probability : "+(macos_token*100)/overall_token+"%");

            }

            if(macos_token>0&&airplay_token>0){
                System.out.println("device uses airplay ports --> could be an apple device");
            }

        }
    }

    public static boolean isPortOpen(String host, int port, int timeout) {

        try{

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();

            return true;

        }catch(Exception e){

            return false;

        }

    }

    public static String grabBanner(String host, int port){

        try{

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host,port),2000);

            socket.setSoTimeout(2000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String banner = reader.readLine();

            socket.close();

            return banner;

        }catch(Exception e){

            return null;

        }

    }

    public static String grabHTTPServer(String host,int port){

        try{

            Socket socket = new Socket(host,port);

            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.print("GET / HTTP/1.1\r\n");
            out.print("Host: "+host+"\r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n");
            out.flush();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){

                if(line.toLowerCase().startsWith("server:")){

                    socket.close();

                    return line;

                }

                if(line.isEmpty()) break;

            }

            socket.close();

        }catch(Exception e){

            return null;

        }

        return null;

    }

    public static int getTTL(String host){

        try{

            Process process = Runtime.getRuntime().exec("ping -c 1 "+host);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){

                if(line.contains("ttl=")){

                    String ttl = line.split("ttl=")[1].split(" ")[0];

                    return Integer.parseInt(ttl);

                }

            }

        }catch(Exception e){

            return -1;

        }

        return -1;

    }

    public static String getMacVendor(String host){

        try{

            Process process = Runtime.getRuntime().exec("arp "+host);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){

                if(line.contains(":")){

                    String[] parts = line.split(" ");

                    for(String part : parts){

                        if(part.contains(":")){
                            return part;
                        }

                    }

                }

            }

        }catch(Exception e){

            return null;

        }

        return null;

    }

    public static void scan_ip(String subnet,boolean skip_port_scan,List<Integer> win,List<Integer> linux_server,List<Integer> macos){

        String yellow = "\u001B[33m";
        String reset = "\u001B[0m";
        String red = "\u001B[31m";

        for(int i = 1; i < 255;i++){

            String host = subnet + i;

            System.out.print("\rProgress: " + i + "/254");

            try{
                Thread.sleep(50);
            }catch(Exception ex){}

            try{

                InetAddress adress = InetAddress.getByName(host);

                if(adress.isReachable(100)){

                    System.out.println("\n"+yellow + "IP : "+  adress.getHostAddress()+" is up"+reset);

                    int ttl = getTTL(host);

                    System.out.println("TTL: "+ttl);

                    if(ttl > 120){
                        System.out.println("maybe Windows or some embedded device like a router or speedport");
                    }

                    if(ttl <= 70 && ttl > 0){
                        System.out.println(red+"os guess : maybe Linux/macOS"+reset);
                    }

                    String mac = getMacVendor(host);

                    if(mac != null){
                        System.out.println("MAC: "+mac);
                    }
                }
            }catch(UnknownHostException e){

                System.out.println("host not reachable "+e.getMessage());

            }catch(IOException e){

                System.out.println(red+"error while pinging : " + e.getMessage()+reset);

            }
            
        }
         System.out.println(yellow+"The os guessing just happaned on the basis of ttl , for more advanced os guessing a port scan can be performed on the target ip and based on the open ports the os can be guessed more accurately, run the program with -p argument to perform a port scan on the target ip and get a more accurate os guessing"+reset);
            

    }

    public static void show_motion(int step){

        String cyan = "\u001B[36m";
        String reset = "\u001B[0m";

        if(step == 1){

            System.out.println(cyan + """
 _   _      _                      _
| \\ | | ___| |___      _____  _ __| | __
|  \\| |/ _ \\ __\\ \\ /\\ / / _ \\| '__| |/ /
| |\\  |  __/ |_ \\ V  V / (_) | |  |   <
|_| \\_|\\___|\\__| \\_/\\_/ \\___/|_|  |_|\\_\\
""" + reset);

        }

        if(step == 2){

            System.out.println(cyan+"port scan activated");
            System.out.println("the following listed ports will be scanned and based on the open ports the os will be guessed"+reset);

        }

    }

}
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;



public class app {
    public static void main(String[] args) {
        show_motion(1);
        //subnet is the first 3 octets of the ip adress, the last octet will be scanned from 1 to 254, subnet given in command line args or default is 192.168.1
        String subnet = "";
        boolean port_scan = false;
        boolean ip_scan = false;
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

        //ip inersted like app 102.168.2.
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
            get_ports(args,win,linux_server,macos);
                for(int z = 0; z < win.size();z++){
                        if(isPortOpen(subnet,win.get(z),100)){
                        System.out.println("port "+win.get(z)+" is open, probaply windows");
                            }
                }
                for(int z = 0; z < linux_server.size();z++){
                    if(isPortOpen(subnet,linux_server.get(z),100)){
                        System.out.println("port "+linux_server.get(z)+" is open, probaply linux server");
                        }
                    }
                for(int z = 0; z < macos.size();z++){
                    if(isPortOpen(subnet,macos.get(z),100)){
                    System.out.println("port "+macos.get(z)+" is open, probaply macos");
                    }
                }
            }
    }

    public static boolean isPortOpen(String host, int port, int timeout) {
    try {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), timeout);
        socket.close();
        return true;
    } catch (Exception e) {
        return false;
    }
} public static void get_ports(String[] arg,List<Integer> win,List<Integer> linux_server,List<Integer> macos) {
        for(int i = 0; i < win.size();i++){
            System.out.println("windows : "+win.get(i));
        }
        for(int i = 0; i < linux_server.size();i++){
            System.out.println("linux server : "+linux_server.get(i));
        }
        for(int i = 0; i < macos.size();i++){
            System.out.println("macos : "+macos.get(i));
        }

}public static void scan_ip(String subnet,boolean skip_port_scan,List<Integer> win,List<Integer> linux_server,List<Integer> macos){
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
                    //check for ports and os
                }
            }catch(UnknownHostException e){
                System.out.println("host not reachable "+e.getMessage());
            }catch(IOException e){
                System.out.println(red+"error while pinging : " + e.getMessage()+reset);
            }
        }
}public static void show_motion(int step){
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
            System.out.println("the following listed ports will be scanned and based on the open ports the os will be guessed, to skip the port scan and just ping the targets use -p flag after the subnet"+reset);      
        }
    }
}

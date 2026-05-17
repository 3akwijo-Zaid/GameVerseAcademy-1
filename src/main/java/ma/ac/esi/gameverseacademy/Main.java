package ma.ac.esi.gameverseacademy;
import org.apache.catalina.startup.Tomcat;
public class Main {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = ServerConfig.configure();
        tomcat.start();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   GameVerse Academy — Serveur demarre        ║");
        System.out.println("║   http://localhost:6060/gameverseacademy/     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        tomcat.getServer().await();
    }
}
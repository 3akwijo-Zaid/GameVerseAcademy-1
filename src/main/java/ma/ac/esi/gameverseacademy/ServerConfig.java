package ma.ac.esi.gameverseacademy;
import ma.ac.esi.gameverseacademy.controller.*;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
public class ServerConfig {
    private static final int PORT = 6060;
    private static final String CONTEXT_PATH = "/gameverseacademy";
    public static Tomcat configure() throws Exception {
        silenceLogs();
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector();
        String webappDir = new File("src/main/webapp").getAbsolutePath();
        if (!new File(webappDir).exists()) webappDir = new File(".").getAbsolutePath();
        Files.createDirectories(Paths.get(webappDir, "uploads"));
        Context ctx = tomcat.addWebapp(CONTEXT_PATH, webappDir);
        ctx.setParentClassLoader(ServerConfig.class.getClassLoader());
        ctx.setAllowCasualMultipartParsing(true);
        registerServlets(ctx);
        return tomcat;
    }
    private static void registerServlets(Context ctx) {
        addServlet(ctx, "LoginController",      new LoginController(),      "/LoginController");
        addServlet(ctx, "LogoutController",     new LogoutController(),     "/LogoutController");
        addServlet(ctx, "ModController",        new ModController(),        "/mods");
        addServlet(ctx, "ModSubmitController",  new ModSubmitController(),  "/ModSubmitController");
        addServlet(ctx, "ModDeleteController",  new ModDeleteController(),  "/ModDeleteController");
        addServlet(ctx, "ModUpdateController",  new ModUpdateController(),  "/ModUpdateController");
        addServlet(ctx, "AdminController",      new AdminController(),      "/admin");
        addServlet(ctx, "GameCardController",   new GameCardController(),   "/cards");
        addServlet(ctx, "PaymentController",    new PaymentController(),    "/payments");
        addServlet(ctx, "RatingController",     new RatingController(),     "/rate");
        addServlet(ctx, "PdfController",        new PdfController(),        "/pdf");
        addServlet(ctx, "DashboardController",  new DashboardController(),  "/dashboard");
        addServlet(ctx, "ProfileController",    new ProfileController(),    "/profile");
        addServlet(ctx, "RawgEnrichController", new RawgEnrichController(), "/rawg");
    }
    private static void addServlet(Context ctx, String name, jakarta.servlet.Servlet servlet, String mapping) {
        Tomcat.addServlet(ctx, name, servlet).setLoadOnStartup(1);
        ctx.addServletMappingDecoded(mapping, name);
    }
    private static void silenceLogs() {
        Logger.getLogger("org.apache").setLevel(Level.WARNING);
        Logger.getLogger("org.apache.catalina").setLevel(Level.WARNING);
        Logger.getLogger("org.apache.coyote").setLevel(Level.WARNING);
        Logger.getLogger("org.apache.jasper").setLevel(Level.WARNING);
        Logger.getLogger("org.apache.tomcat").setLevel(Level.WARNING);
    }
}
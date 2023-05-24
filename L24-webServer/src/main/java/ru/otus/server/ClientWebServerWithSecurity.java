package ru.otus.server;

import com.google.gson.Gson;
import java.util.Arrays;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.helpers.FileSystemHelper;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;
import ru.otus.servlet.AuthorizationFilter;
import ru.otus.servlet.ClientApiServlet;
import ru.otus.servlet.ClientServlet;
import ru.otus.servlet.LoginServlet;

public class ClientWebServerWithSecurity implements ClientWebServer {
    private static final String START_PAGE_NAME = "index.html";
    private static final String COMMON_RESOURCES_DIR = "static";

    private final UserAuthService authService;
    private final DBServiceClient dBServiceClient;
    private final Gson gson;
    protected final TemplateProcessor templateProcessor;
    private final Server server;


    public ClientWebServerWithSecurity(int port, DBServiceClient dBServiceClient,
        UserAuthService authService,
        Gson gson, TemplateProcessor templateProcessor) {
        server = new Server(port);
        this.authService = authService;
        this.dBServiceClient = dBServiceClient;
        this.templateProcessor = templateProcessor;
        this.gson = gson;
    }

    @Override
    public void start() throws Exception {
        if (server.getHandlers().length == 0) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    private Server initContext() {
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();
        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(applySecurity(servletContextHandler, "/client", "/api/client"));
        server.setHandler(handlers);
        return server;
    }

    protected Handler applySecurity(ServletContextHandler servletContextHandler, String ...paths) {
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authService)), "/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths).forEachOrdered(path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));
        return servletContextHandler;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{START_PAGE_NAME});
        resourceHandler.setResourceBase(FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new ClientServlet(templateProcessor, dBServiceClient)), "/client");
        servletContextHandler.addServlet(new ServletHolder(new ClientApiServlet(dBServiceClient, gson)), "/api/client");
        return servletContextHandler;
    }
}

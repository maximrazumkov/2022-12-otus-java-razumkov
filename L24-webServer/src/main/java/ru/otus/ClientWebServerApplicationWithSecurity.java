package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.dao.InMemoryUserDao;
import ru.otus.dao.UserDao;
import ru.otus.server.ClientWebServer;
import ru.otus.server.ClientWebServerWithSecurity;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;

public class ClientWebServerApplicationWithSecurity {

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        new ClientWebServerApplicationWithSecurity().run();
    }

    private final void run() throws Exception {
        DBServiceClient dbServiceClient = initDbServiceClient();
        ClientWebServer clientWebServer = initClientWebServer(dbServiceClient);
        runClientWebServer(clientWebServer);
    }

    private DBServiceClient initDbServiceClient() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class,
            Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        return new DbServiceClientImpl(transactionManager, clientTemplate);
    }

    private ClientWebServer initClientWebServer(DBServiceClient dbServiceClient) throws IOException {
        UserDao userDao = new InMemoryUserDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(userDao);
        return new ClientWebServerWithSecurity(WEB_SERVER_PORT, dbServiceClient, authService, gson, templateProcessor);
    }

    private void runClientWebServer(ClientWebServer clientWebServer) throws Exception {
        clientWebServer.start();
        clientWebServer.join();
    }
}

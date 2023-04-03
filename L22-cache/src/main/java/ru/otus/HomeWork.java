package ru.otus;

import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.HwCache;
import ru.otus.cache.impl.MyCacheFactory;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.crm.service.DbServiceManagerImpl;
import ru.otus.mapper.DataTemplateJdbc;
import ru.otus.mapper.EntityClassMetaData;
import ru.otus.mapper.EntityHandler;
import ru.otus.mapper.EntitySQLMetaData;
import ru.otus.mapper.impl.EntityClassMetaDataFactory;
import ru.otus.mapper.impl.EntityHandlerImpl;
import ru.otus.mapper.impl.EntitySQLMetaDataFactory;

public class HomeWork {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
// Общая часть
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

// Работа с клиентом
        EntityClassMetaData<Client> entityClassMetaDataClient = EntityClassMetaDataFactory.of(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = EntitySQLMetaDataFactory.of(entityClassMetaDataClient);
        EntityHandler<Client> clientEntityHandler = new EntityHandlerImpl<>(entityClassMetaDataClient);
        HwCache<String, Client> clientCache = MyCacheFactory.create();
        var dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataClient, clientEntityHandler, clientCache); //реализация DataTemplate, универсальная

// Код дальше должен остаться
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
        dbServiceClient.saveClient(new Client("dbServiceFirst"));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);
        List<Client> all = dbServiceClient.findAll();
        log.info("clientSecondSelected:{}", all);

// Сделайте тоже самое с классом Manager (для него надо сделать свою таблицу)

        EntityClassMetaData<Manager> entityClassMetaDataManager = EntityClassMetaDataFactory.of(Manager.class);
        EntitySQLMetaData entitySQLMetaDataManager = EntitySQLMetaDataFactory.of(entityClassMetaDataManager);
        EntityHandler<Manager> managerEntityHandler = new EntityHandlerImpl<>(entityClassMetaDataManager);
        HwCache<String, Manager> managerCache = MyCacheFactory.create();
        var dataTemplateManager = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataManager, managerEntityHandler, managerCache);

        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);
        dbServiceManager.saveManager(new Manager("ManagerFirst"));

        var managerSecond = dbServiceManager.saveManager(new Manager("ManagerSecond"));
        var managerSecondSelected = dbServiceManager.getManager(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));
        log.info("managerSecondSelected:{}", managerSecondSelected);
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}

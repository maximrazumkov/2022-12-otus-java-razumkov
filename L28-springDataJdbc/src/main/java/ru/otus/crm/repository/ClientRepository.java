package ru.otus.crm.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {
    List<Client> findAll();
}

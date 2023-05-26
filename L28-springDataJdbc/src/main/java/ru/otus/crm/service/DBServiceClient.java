package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.model.Client;
import ru.otus.dto.ClientDto;

public interface DBServiceClient {
    void saveClient(ClientDto client);
    List<ClientDto> findAll();
}

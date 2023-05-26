package ru.otus.crm.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.repository.AddressRepository;
import ru.otus.crm.repository.ClientRepository;
import ru.otus.dto.ClientDto;
import ru.otus.sessionmanager.TransactionManager;

@Slf4j
@Service
public class DbServiceClientImpl implements DBServiceClient {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final TransactionManager transactionManager;

    public DbServiceClientImpl(TransactionManager transactionManager,
        ClientRepository clientRepository,
        AddressRepository addressRepository) {
        this.transactionManager = transactionManager;
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public void saveClient(ClientDto clientDto) {
       transactionManager.doInTransaction(() -> {
            Address newAddress = addressRepository.save(new Address(clientDto.getAddress()));
            Set<Phone> phoneSet = Collections.singleton(new Phone(clientDto.getPhone()));
            Client client = new Client(clientDto.getName(), newAddress.getId(), phoneSet);
            Client newClient = clientRepository.save(client);
            log.info("save client: {}", newClient);
            return newClient;
        });
    }


    @Override
    public List<ClientDto> findAll() {
        return transactionManager.doInTransaction(() -> {
            var clientList = clientRepository.findAll();
            List<ClientDto> clientDtoList = convertClientListToClientDtoList(clientList);
            log.info("clientListDto:{}", clientDtoList);
            return clientDtoList;
        });
    }

    private List<ClientDto> convertClientListToClientDtoList(List<Client> clientList) {
        return clientList.stream()
            .map(this::convertClientToClientDto)
            .toList();
    }

    private ClientDto convertClientToClientDto(Client client) {
        return ClientDto.builder()
            .name(client.getName())
            .address(getClientStreet(client))
            .phone(getClientNumber(client))
            .build();
    }

    private String getClientStreet(Client client) {
        return addressRepository
            .findById(client.getAddressId())
            .orElse(new Address(""))
            .getStreet();
    }

    private String getClientNumber(Client client) {
        return client.getPhoneSet().stream()
            .findAny()
            .orElse(new Phone(""))
            .getNumber();
    }
}

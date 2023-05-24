package ru.otus.servlet;

import static java.util.Collections.singletonList;
import static ru.otus.util.HttpVariable.BAD_REQUEST_ERROR;
import static ru.otus.util.HttpVariable.INTERNAL_SERVER_ERROR;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import lombok.RequiredArgsConstructor;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.exception.BadRequestException;
import ru.otus.exception.InternalServerException;
import ru.otus.servlet.dto.ClientDto;

@RequiredArgsConstructor
public class ClientApiServlet extends HttpServlet {

    private final DBServiceClient dbServiceClient;
    private final Gson gson;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ClientDto clientDto = getClientDtoFromRequest(req);
        Client client = convertClientDtoToClient(clientDto);
        saveClient(client);
    }

    private ClientDto getClientDtoFromRequest(HttpServletRequest req) throws IOException {
        try (Reader requestBody = new InputStreamReader(req.getInputStream())) {
            return gson.fromJson(requestBody, ClientDto.class);
        } catch (Exception e) {
            throw new BadRequestException(BAD_REQUEST_ERROR, e);
        }
    }

    private Client convertClientDtoToClient(ClientDto clientDto) {
        var client = new Client();
        client.setName(clientDto.getName());
        client.setAddress(new Address(clientDto.getAddress()));
        client.setPhoneList(singletonList(new Phone(clientDto.getPhone())));
        return client;
    }

    private void saveClient(Client client) {
        try {
            dbServiceClient.saveClient(client);
        } catch (Exception e) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, e);
        }
    }
}

package ru.otus.servlet;

import static ru.otus.util.HttpVariable.INTERNAL_SERVER_ERROR;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.exception.InternalServerException;
import ru.otus.services.TemplateProcessor;
import ru.otus.servlet.dto.ClientDto;

public class ClientServlet extends HttpServlet {

    private static final String CLIENT_PAGE_TEMPLATE = "client.html";
    private static final String TEMPLATE_ATTR_RANDOM_CLIENT = "clients";

    private final DBServiceClient dbServiceClient;
    private final TemplateProcessor templateProcessor;

    public ClientServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var clientList = getAllClientList();
        var clientDtoList = convertClientListToClientDtoList(clientList);
        printResponse(resp, clientDtoList);
    }

    private List<ClientDto> convertClientListToClientDtoList(List<Client> clientList) {
        return clientList.stream()
            .map(this::convertClientToClientDto)
            .collect(Collectors.toList());
    }

    private ClientDto convertClientToClientDto(Client client) {
        var phone = client.getPhoneList().isEmpty() ? "" : client.getPhoneList().get(0).getNumber();
        return ClientDto.builder()
            .name(client.getName())
            .address(client.getAddress().getStreet())
            .phone(phone)
            .build();
    }

    private List<Client> getAllClientList() {
        try {
            return dbServiceClient.findAll();
        } catch (Exception e) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, e);
        }
    }

    private void printResponse(HttpServletResponse resp, List<ClientDto> clientDtoList) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(TEMPLATE_ATTR_RANDOM_CLIENT, clientDtoList);
        resp.setContentType("text/html");
        resp.getWriter().println(templateProcessor.getPage(CLIENT_PAGE_TEMPLATE, paramsMap));
    }
}

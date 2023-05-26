package ru.otus.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.dto.ClientDto;

@Controller
@RequiredArgsConstructor
public class ClientController {

    private final DbServiceClientImpl dbServiceClient;

    @GetMapping("/client")
    public String getClientList(Model model) {
        List<ClientDto> all = dbServiceClient.findAll();
        model.addAttribute("clients", all);
        model.addAttribute("client", ClientDto.builder().build());
        return "clients";
    }

    @PostMapping("/client")
    public RedirectView saveClient(@ModelAttribute ClientDto client) {
        dbServiceClient.saveClient(client);
        return new RedirectView("/client", true);
    }
}

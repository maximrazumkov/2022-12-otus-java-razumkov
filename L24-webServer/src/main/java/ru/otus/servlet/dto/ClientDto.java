package ru.otus.servlet.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class ClientDto implements Serializable {
    private final String name;
    private final String address;
    private final String phone;
}

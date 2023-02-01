package ru.otus.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class StatisticDto {
    int success;
    int fail;
}

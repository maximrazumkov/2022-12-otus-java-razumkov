package ru.petrelevich.config;

import java.util.Set;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.petrelevich.component.DeadRooms;
import ru.petrelevich.component.DeadRoomsImpl;

@Setter
@Configuration
@ConfigurationProperties("dead")
public class DeadRoomsConfig {

    private Set<Long> rooms;

    @Bean
    public DeadRooms deadRooms() {
        return new DeadRoomsImpl(rooms);
    }
}

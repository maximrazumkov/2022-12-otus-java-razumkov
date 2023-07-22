package ru.petrelevich.component;

import java.util.List;

public interface DeadRooms {
    List<Long> getRooms();
    boolean contains(Long roomId);
}

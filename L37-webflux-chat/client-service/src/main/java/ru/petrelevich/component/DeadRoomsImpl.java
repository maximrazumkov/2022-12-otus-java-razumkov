package ru.petrelevich.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeadRoomsImpl implements DeadRooms {
    private final Set<Long> rooms;

    @Override
    public List<Long> getRooms() {
        return new ArrayList<>(rooms);
    }

    @Override
    public boolean contains(Long roomId) {
        return rooms.contains(roomId);
    }
}

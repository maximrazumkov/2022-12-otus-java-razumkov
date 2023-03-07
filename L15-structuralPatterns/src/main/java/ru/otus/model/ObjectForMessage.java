package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage implements Cloneable {
    private List<String> data;

    public ObjectForMessage() {
    }

    public ObjectForMessage(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    protected ObjectForMessage clone() {
        return new ObjectForMessage(new ArrayList<>(data));
    }
}

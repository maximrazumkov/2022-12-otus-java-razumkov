package ru.otus.dataprocessor;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.util.Map;

public class FileSerializer implements Serializer {

    private final static String SERIALIZER_ERROR = "Serializer file error. Filename = %s";

    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            new Gson().toJson(data, fileWriter);
        } catch (Exception e) {
            throw new FileProcessException(String.format(SERIALIZER_ERROR, fileName), e);
        }
    }
}

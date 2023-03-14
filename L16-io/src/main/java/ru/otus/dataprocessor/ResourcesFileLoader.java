package ru.otus.dataprocessor;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import ru.otus.model.Measurement;
import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final static String LOAD_ERROR = "Load file error. Filename = %s";

    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (var inputStreamReader = createInputStreamReader(fileName)) {
            var gson = new Gson();
            var type = new TypeToken<List<Measurement>>(){}.getType();
            return gson.fromJson(inputStreamReader, type);
        } catch (Exception e) {
            throw new FileProcessException(String.format(LOAD_ERROR, fileName), e);
        }
    }

    private Reader createInputStreamReader(String fileName) {
        return new InputStreamReader(loadDataFromResourcesAsStream(fileName), UTF_8);
    }

    private InputStream loadDataFromResourcesAsStream(String fileName) {
        return ResourcesFileLoader.class.getClassLoader().getResourceAsStream(fileName);
    }
}

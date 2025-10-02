package server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import task.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GsonCreator {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();

    public static Gson getGson() {
        gsonBuilder.serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(TaskStatus.class, new TaskStatusTypeAdapter())
                .registerTypeAdapter(new TypeToken<List<Integer>>() {}.getType(), new IntegerListTypeAdapter());

        return gsonBuilder.create();
    }

    static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(timeFormatter));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), timeFormatter);
        }
    }

    static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(duration.toMinutes());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(jsonReader.nextLong());
        }
    }

    static class TaskStatusTypeAdapter extends TypeAdapter<TaskStatus> {
        @Override
        public void write(JsonWriter jsonWriter, TaskStatus status) throws IOException {
            if (status == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(status.name());
            }
        }

        @Override
        public TaskStatus read(JsonReader jsonReader) throws IOException {
            try {
                return TaskStatus.valueOf(jsonReader.nextString());
            } catch (IllegalArgumentException e) {
                throw new JsonParseException(String.format("Invalid value %s for TaskStatus field", jsonReader.nextString()));
            }
        }
    }

    static class IntegerListTypeAdapter extends TypeAdapter<List<Integer>> {
        @Override
        public void write(JsonWriter jsonWriter, List<Integer> integerList) throws IOException {
            if (integerList == null) {
                jsonWriter.beginArray().endArray();
                return;
            }
            jsonWriter.beginArray();
            for (Integer value : integerList) {
                jsonWriter.value(value);
            }
            jsonWriter.endArray();
        }

        @Override
        public List<Integer> read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return new ArrayList<>();
            }
            List<Integer> list = new ArrayList<>();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                list.add(jsonReader.nextInt());
            }
            jsonReader.endArray();
            return list;
        }
    }
}

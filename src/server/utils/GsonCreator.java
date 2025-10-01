package server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import task.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonCreator {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();

    public static Gson getGson() {
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(TaskStatus.class, new TaskStatusTypeAdapter());

        return gsonBuilder.create();
    }

    static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(timeFormatter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), timeFormatter);
        }
    }

    static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            jsonWriter.value(duration.toMinutes());
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
}

package ru.leti.filefilter.writer;

import ru.leti.filefilter.model.DataType;
import java.io.*;
import java.nio.file.*;

public class ResultWriter {
    private final String outputPath;
    private final String prefix;
    private final boolean appendMode;

    public ResultWriter(String outputPath, String prefix, boolean appendMode) {
        this.outputPath = outputPath != null ? outputPath : ".";
        this.prefix = prefix != null ? prefix : "";
        this.appendMode = appendMode;
    }

    public void writeData(DataType type, java.util.List<String> data) throws IOException {
        if (data.isEmpty()) {
            return; // Не создаем файл, если нет данных
        }

        String fileName = prefix + type.getDefaultFileName();
        Path filePath = Paths.get(outputPath, fileName);

        // Создаем директории, если их нет
        Files.createDirectories(filePath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                appendMode ? StandardOpenOption.CREATE : StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE,
                appendMode ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING)) {

            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (FileAlreadyExistsException e) {
            // Если файл уже существует и не в режиме добавления
            if (!appendMode) {
                try (BufferedWriter writer = Files.newBufferedWriter(
                        filePath,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
                    for (String line : data) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }
    }
}
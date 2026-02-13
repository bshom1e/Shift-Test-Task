package ru.leti.filefilter;

import ru.leti.filefilter.model.DataType;
import ru.leti.filefilter.model.Statistics;
import ru.leti.filefilter.processor.DataProcessor;
import ru.leti.filefilter.writer.ResultWriter;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileFilterUtility {

    private static class CommandLineArgs {
        String outputPath = null;
        String prefix = null;
        boolean appendMode = false;
        boolean shortStats = false;
        boolean fullStats = false;
        List<String> inputFiles = new ArrayList<>();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Использование: java -jar file-filter-utility.jar [опции] файл1 файл2 ...");
            System.out.println("Опции:");
            System.out.println("  -o <путь>    - путь для выходных файлов");
            System.out.println("  -p <префикс> - префикс для имен выходных файлов");
            System.out.println("  -a           - режим добавления в существующие файлы");
            System.out.println("  -s           - краткая статистика");
            System.out.println("  -f           - полная статистика");
            return;
        }

        CommandLineArgs cmdArgs = parseArguments(args);

        if (cmdArgs.inputFiles.isEmpty()) {
            System.err.println("Ошибка: не указаны входные файлы");
            return;
        }

        // Проверяем существование входных файлов
        List<String> existingFiles = new ArrayList<>();
        for (String file : cmdArgs.inputFiles) {
            if (Files.exists(Paths.get(file))) {
                existingFiles.add(file);
            } else {
                System.err.println("Предупреждение: файл не найден - " + file);
            }
        }

        if (existingFiles.isEmpty()) {
            System.err.println("Ошибка: нет доступных для чтения файлов");
            return;
        }

        // Обрабатываем файлы
        DataProcessor processor = new DataProcessor();

        for (String file : existingFiles) {
            try {
                processFile(file, processor);
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла " + file + ": " + e.getMessage());
            }
        }

        // Записываем результаты
        ResultWriter writer = new ResultWriter(cmdArgs.outputPath, cmdArgs.prefix, cmdArgs.appendMode);

        for (DataType type : DataType.values()) {
            try {
                writer.writeData(type, processor.getDataMap().get(type));
            } catch (IOException e) {
                System.err.println("Ошибка при записи файла для типа " + type + ": " + e.getMessage());
            }
        }

        // Выводим статистику
        printStatistics(processor, cmdArgs.shortStats, cmdArgs.fullStats);
    }

    private static CommandLineArgs parseArguments(String[] args) {
        CommandLineArgs cmdArgs = new CommandLineArgs();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if (i + 1 < args.length) {
                        cmdArgs.outputPath = args[++i];
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        cmdArgs.prefix = args[++i];
                    }
                    break;
                case "-a":
                    cmdArgs.appendMode = true;
                    break;
                case "-s":
                    cmdArgs.shortStats = true;
                    break;
                case "-f":
                    cmdArgs.fullStats = true;
                    break;
                default:
                    if (!args[i].startsWith("-")) {
                        cmdArgs.inputFiles.add(args[i]);
                    }
                    break;
            }
        }

        return cmdArgs;
    }

    private static void processFile(String fileName, DataProcessor processor) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    processor.processLine(line);
                } catch (Exception e) {
                    System.err.println("Ошибка обработки строки в файле " + fileName + ": " + e.getMessage());
                }
            }
        }
    }

    private static void printStatistics(DataProcessor processor, boolean shortStats, boolean fullStats) {
        System.out.println("\n=== Статистика обработки ===");

        for (DataType type : DataType.values()) {
            Statistics stats = processor.getStatisticsMap().get(type);
            if (stats.getCount() > 0) {
                if (shortStats) {
                    stats.printShortStatistics(type);
                }
                if (fullStats) {
                    stats.printFullStatistics(type);
                }
            }
        }
    }
}
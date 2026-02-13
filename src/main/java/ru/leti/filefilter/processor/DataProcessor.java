package ru.leti.filefilter.processor;

import ru.leti.filefilter.model.DataType;
import ru.leti.filefilter.model.Statistics;

import java.util.*;

public class DataProcessor {
    private final Map<DataType, Statistics> statisticsMap = new EnumMap<>(DataType.class);
    private final Map<DataType, List<String>> dataMap = new EnumMap<>(DataType.class);

    public DataProcessor() {
        for (DataType type : DataType.values()) {
            statisticsMap.put(type, new Statistics());
            dataMap.put(type, new ArrayList<>());
        }
    }

    public void processLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        line = line.trim();

        // Пытаемся определить тип данных
        if (isInteger(line)) {
            long value = Long.parseLong(line);
            dataMap.get(DataType.INTEGER).add(line);
            statisticsMap.get(DataType.INTEGER).addInteger(value);
        } else if (isFloat(line)) {
            double value = Double.parseDouble(line.replace(',', '.'));
            dataMap.get(DataType.FLOAT).add(line);
            statisticsMap.get(DataType.FLOAT).addFloat(value);
        } else {
            dataMap.get(DataType.STRING).add(line);
            statisticsMap.get(DataType.STRING).addString(line);
        }
    }

    private boolean isInteger(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFloat(String str) {
        try {
            Double.parseDouble(str.replace(',', '.'));
            return !str.contains(".") || !isInteger(str); // Избегаем дублирования с integer
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Map<DataType, List<String>> getDataMap() {
        return dataMap;
    }

    public Map<DataType, Statistics> getStatisticsMap() {
        return statisticsMap;
    }
}
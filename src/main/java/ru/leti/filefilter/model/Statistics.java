package ru.leti.filefilter.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class Statistics {
    private long count = 0;
    private Optional<Long> minInteger = Optional.empty();
    private Optional<Long> maxInteger = Optional.empty();
    private BigDecimal sumInteger = BigDecimal.ZERO;

    private Optional<Double> minFloat = Optional.empty();
    private Optional<Double> maxFloat = Optional.empty();
    private BigDecimal sumFloat = BigDecimal.ZERO;

    private Optional<Integer> shortestString = Optional.empty();
    private Optional<Integer> longestString = Optional.empty();

    public void addInteger(long value) {
        count++;
        minInteger = Optional.of(Math.min(minInteger.orElse(value), value));
        maxInteger = Optional.of(Math.max(maxInteger.orElse(value), value));
        sumInteger = sumInteger.add(BigDecimal.valueOf(value));
    }

    public void addFloat(double value) {
        count++;
        minFloat = Optional.of(Math.min(minFloat.orElse(value), value));
        maxFloat = Optional.of(Math.max(maxFloat.orElse(value), value));
        sumFloat = sumFloat.add(BigDecimal.valueOf(value));
    }

    public void addString(String value) {
        count++;
        int length = value.length();
        shortestString = Optional.of(Math.min(shortestString.orElse(length), length));
        longestString = Optional.of(Math.max(longestString.orElse(length), length));
    }

    public void printShortStatistics(DataType type) {
        System.out.printf("Тип: %s, Количество элементов: %d%n", type, count);
    }

    public void printFullStatistics(DataType type) {
        printShortStatistics(type);

        switch (type) {
            case INTEGER:
                minInteger.ifPresent(min -> System.out.printf("  Минимальное: %d%n", min));
                maxInteger.ifPresent(max -> System.out.printf("  Максимальное: %d%n", max));
                if (count > 0) {
                    System.out.printf("  Сумма: %f%n", sumInteger);
                    BigDecimal average = sumInteger.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                    System.out.printf("  Среднее: %f%n", average);
                }
                break;
            case FLOAT:
                minFloat.ifPresent(min -> System.out.printf("  Минимальное: %f%n", min));
                maxFloat.ifPresent(max -> System.out.printf("  Максимальное: %f%n", max));
                if (count > 0) {
                    System.out.printf("  Сумма: %f%n", sumFloat);
                    BigDecimal average = sumFloat.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                    System.out.printf("  Среднее: %f%n", average);
                }
                break;
            case STRING:
                shortestString.ifPresent(min -> System.out.printf("  Минимальная длина: %d%n", min));
                longestString.ifPresent(max -> System.out.printf("  Максимальная длина: %d%n", max));
                break;
        }
    }

    public long getCount() {
        return count;
    }
}
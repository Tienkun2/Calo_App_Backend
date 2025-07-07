package com.dev.CaloApp.Enum;

public enum ActivityLevel {
    VERY_LOW(1.2),      // Ít vận động (ít tập thể dục)
    LIGHT(1.375),         // Nhẹ (tập 1-3 lần/tuần)
    MODERATE(1.55),       // Trung bình (tập 3-5 lần/tuần)
    HIGH(1.725),          // Cao (tập 6-7 lần/tuần)
    VERY_HIGH(1.9);       // Rất cao (vận động viên, tập nặng)

    private final double value;

    ActivityLevel(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
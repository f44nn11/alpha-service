package com.alpha.service.util;

import lombok.Getter;

public interface Constants {


    @Getter
    enum SYSTEM_ENVIRONMENT_TYPE {
        PRODUCTION("PRODUCTION"), DEVELOPMENT("DEVELOPMENT");
        private final String value;

        SYSTEM_ENVIRONMENT_TYPE(String methodValue) {
            this.value = methodValue;
        }

    }

    @Getter
    enum BookingStatus {
        OPEN("1"), CLOSE("2"), LOSE("3");

        private final String code;
        BookingStatus(String code) { this.code = code; }

        public static String fromRaw(String input) {
            return switch (input.trim().toUpperCase()) {
                case "1", "OPEN" -> "1";
                case "2", "CLOSE" -> "2";
                case "3", "LOSE" -> "3";
                default -> throw new IllegalArgumentException("Invalid status: " + input);
            };
        }

    }
}

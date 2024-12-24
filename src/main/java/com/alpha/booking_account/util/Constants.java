package com.alpha.booking_account.util;

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
}

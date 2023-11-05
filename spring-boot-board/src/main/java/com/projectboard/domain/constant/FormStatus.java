package com.projectboard.domain.constant;

import lombok.Getter;

public enum FormStatus {
    CREATE("저장",false), UPDATE("수정", true);

    @Getter private final String description;
    @Getter private final boolean update;

    FormStatus(String description, boolean update) {
        this.description = description;
        this.update = update;
    }
}

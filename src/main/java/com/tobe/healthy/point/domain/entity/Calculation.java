package com.tobe.healthy.point.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Calculation implements EnumMapperType {
    PLUS("+") {
        public int apply(int x, int y) {
            return x + y;
        }
    },
    MINUS("-") {
        public int apply(int x, int y) {
            return x - y;
        }
    },;

    private final String description;

    @Override
    public String getCode() {
        return name();
    }

    public abstract int apply(int x, int y);

}

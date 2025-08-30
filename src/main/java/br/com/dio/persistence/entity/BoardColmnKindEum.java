package br.com.dio.persistence.entity;

import java.util.stream.Stream;

public enum BoardColmnKindEum {

    INITIAL, FINAL, CANCEL,PENDING;

    public static BoardColmnKindEum findByName(final String name){
        return Stream.of(BoardColmnKindEum.values())
                .filter(b -> b.name().equals(name))
                .findFirst().orElseThrow();
    }
}

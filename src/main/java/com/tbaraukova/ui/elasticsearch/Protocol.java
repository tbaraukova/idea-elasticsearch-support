package com.tbaraukova.ui.elasticsearch;

import java.util.Arrays;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public enum Protocol {
    HTTP,
    HTTPS;

    public static String[] names() {
        return getStream().map(Protocol::toString).toArray(String[]::new);
    }

    @NotNull
    private static Stream<Protocol> getStream() {
        return Arrays.stream(Protocol.values());
    }

    public static Protocol byOrdinal(int ordinal) {
        return getStream().filter(i -> i.ordinal() == ordinal).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}

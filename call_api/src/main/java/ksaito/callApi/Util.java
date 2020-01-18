package ksaito.callApi;

import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

public interface Util {
    static void print(@Nullable Object obj) {
        System.out.println((Objects.isNull(obj) ? "nullの項目です。" : obj.toString()));
    }

    static void exit(@Nullable Integer exitCode) {
        System.exit(Optional.ofNullable(exitCode).orElse(0));
    }
}

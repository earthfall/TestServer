package verification.invalid;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@RequiredArgsConstructor
class InvalidNullness {
    String invalidReturn() {
        return null;
    }

    int invalidDereference(@Nullable String value) {
        return value.length();
    }

    List<String> invalidGeneric(List<@Nullable String> values) {
        return values;
    }
}

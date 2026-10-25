package verification.valid;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
class ValidNullness {
    int length(@Nullable String value) {
        return value == null ? 0 : value.length();
    }
}

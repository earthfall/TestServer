package org.example.transform;

import org.springframework.stereotype.Component;

@Component
public class Transformer {

    String transformString(String value) {
        return "transformed " + value;
    }
}

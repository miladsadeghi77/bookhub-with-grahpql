package com.miladsadeghi.bookhubwithgraphql.api.input;

import jakarta.validation.constraints.Pattern;

public record RetrieveBookInput(@Pattern(regexp = "\\d+", message = "ID must be numeric") String id) {

}

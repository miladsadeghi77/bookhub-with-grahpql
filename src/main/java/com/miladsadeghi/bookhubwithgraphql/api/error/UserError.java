package com.miladsadeghi.bookhubwithgraphql.api.error;


public record UserError(
    String message,
    String field,
    ErrorCode code
) {

}

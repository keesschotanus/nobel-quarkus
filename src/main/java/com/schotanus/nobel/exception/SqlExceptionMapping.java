package com.schotanus.nobel.exception;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jooq.exception.DataAccessException;

import java.net.HttpURLConnection;
import java.util.UUID;


/**
 * Maps SQL Exceptions to a response.
 * The current implementation is quite simplistic but at least logs the exception
 * and returns an HTTP_INTERNAL_ERROR response to the client.
 */
@Provider
public class SqlExceptionMapping implements ExceptionMapper<DataAccessException> {

    @Override
    public Response toResponse(final DataAccessException exception) {
        final String message = "Something went wrong. Refer to this id when reporting this problem:" + UUID.randomUUID();
        String exceptionMessage = exception.getMessage();
        if (exception.getCause() != null) {
            exceptionMessage = exception.getMessage();
        }
        Log.warn(message + ". " + exceptionMessage);

        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(message).build();
    }
}

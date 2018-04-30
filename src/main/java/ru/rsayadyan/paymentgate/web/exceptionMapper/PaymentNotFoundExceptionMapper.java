package ru.rsayadyan.paymentgate.web.exceptionMapper;

import ru.rsayadyan.paymentgate.domain.payment.exception.PaymentNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class PaymentNotFoundExceptionMapper implements ExceptionMapper<PaymentNotFoundException> {
    @Override
    public Response toResponse(PaymentNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

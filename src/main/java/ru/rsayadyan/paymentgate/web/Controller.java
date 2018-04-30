package ru.rsayadyan.paymentgate.web;

import ru.rsayadyan.paymentgate.domain.payment.IPaymentProcessor;
import ru.rsayadyan.paymentgate.domain.payment.model.Payment;
import ru.rsayadyan.paymentgate.factory.PaymentProcessorFactory;
import ru.rsayadyan.paymentgate.web.dto.ChangePaymentRequest;
import ru.rsayadyan.paymentgate.web.dto.CreatePaymentRequest;
import ru.rsayadyan.paymentgate.web.dto.PaymentDto;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class Controller {

    IPaymentProcessor paymentProcessor = PaymentProcessorFactory.getPaymentProcessor();

    @POST
    @Path("payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PaymentDto createPayment(@Valid CreatePaymentRequest request) {

        Payment payment = paymentProcessor.initPayment(
                request.getAccIn(),
                request.getAccOut(),
                request.getAmount()
        );

        return new PaymentDto(
                payment.getId(),
                payment.getAccIn(),
                payment.getAccOut(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getErrorReason()
        );

    }

    @GET
    @Path("payment/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public PaymentDto getPayment(@PathParam("id") String paymentId) {

        Payment payment = paymentProcessor.get(paymentId);

        return new PaymentDto(
                payment.getId(),
                payment.getAccIn(),
                payment.getAccOut(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getErrorReason()
        );

    }

    @PUT
    @Path("payment/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PaymentDto confirmPayment(@PathParam("id") String paymentId, @Valid ChangePaymentRequest request) {

        Payment payment = paymentProcessor.promoteTo(paymentId, request.getStatus());

        return new PaymentDto(
                payment.getId(),
                payment.getAccIn(),
                payment.getAccOut(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getErrorReason()
        );

    }
}

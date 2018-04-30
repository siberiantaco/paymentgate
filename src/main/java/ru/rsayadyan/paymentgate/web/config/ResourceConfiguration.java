package ru.rsayadyan.paymentgate.web.config;

import org.glassfish.jersey.server.ResourceConfig;
import ru.rsayadyan.paymentgate.web.Controller;
import ru.rsayadyan.paymentgate.web.exceptionMapper.GenericExceptionMapper;
import ru.rsayadyan.paymentgate.web.exceptionMapper.PaymentNotFoundExceptionMapper;

public class ResourceConfiguration extends ResourceConfig {
    public ResourceConfiguration() {
        register(Controller.class);
        register(GenericExceptionMapper.class);
        register(PaymentNotFoundExceptionMapper.class);

    }

}
package ru.rsayadyan.paymentgate.test;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import ru.rsayadyan.paymentgate.daos.IAccountRepository;
import ru.rsayadyan.paymentgate.daos.IPaymentRepository;
import ru.rsayadyan.paymentgate.daos.IWithholdingRepository;
import ru.rsayadyan.paymentgate.daos.TransactionManager;
import ru.rsayadyan.paymentgate.daos.impl.AccountRepository;
import ru.rsayadyan.paymentgate.daos.impl.PaymentRepository;
import ru.rsayadyan.paymentgate.daos.impl.WithholdingRepository;
import ru.rsayadyan.paymentgate.daos.model.enums.PaymentStatus;
import ru.rsayadyan.paymentgate.exception.ErrorCode;
import ru.rsayadyan.paymentgate.web.Controller;
import ru.rsayadyan.paymentgate.web.dto.ChangePaymentRequest;
import ru.rsayadyan.paymentgate.web.dto.CreatePaymentRequest;
import ru.rsayadyan.paymentgate.web.dto.PaymentDto;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;


public class PaymentGateTest extends JerseyTest {

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Controller.class);
    }

    private IAccountRepository accountRepository = new AccountRepository();

    private IPaymentRepository paymentRepository = new PaymentRepository();

    private IWithholdingRepository withholdingRepository = new WithholdingRepository();

    final private String inAccountId = UUID.randomUUID().toString();

    final private String outAccountId = UUID.randomUUID().toString();

    @Test
    public void positiveFlowTest() {
        transfer(outAccountId, inAccountId);
    }

    private void transfer(String from, String to) {

        //init

        PaymentDto paymentDto = initPayment(to, from, "100");

        final String paymentId = paymentDto.getId();

        assertEquals("Payment status INITIAL", PaymentStatus.INITIAL, paymentDto.getStatus());

        //authorize

        paymentDto = authorizePayment(paymentId, 200);

        assertEquals("Payment status AUTHORIZED", PaymentStatus.AUTHORIZED, paymentDto.getStatus());

        assertEquals("100 cents should be withholded",
                "100",
                accountRepository.get(from).getHoldenAmount().toString());

        paymentDto = confirmPayment(paymentId, 200);


        //confirm

        assertEquals("Payment status CONFIRMED", PaymentStatus.CONFIRMED, paymentDto.getStatus());

        assertEquals("Now OUT account has 900",
                "900",
                accountRepository.get(from).getAmount().toString());

        assertEquals("Now IN account has 1100",
                "1100",
                accountRepository.get(to).getAmount().toString());

    }

    @Test
    public void idempotentcyTest() {

        //we create payment two times - that is OK, it should return new ID every time wuth ni side effects

        PaymentDto paymentDto = initPayment(inAccountId, outAccountId, "100");

        paymentDto = initPayment(inAccountId, outAccountId, "100");

        final String paymentId = paymentDto.getId();


        //we mistakenly try to confirm unauthorized payment (even twice) - that is OK, payment should stay in INITIAL

        paymentDto = confirmPayment(paymentId, 200);

        assertEquals("Payment status INITIAL", PaymentStatus.INITIAL, paymentDto.getStatus());

        paymentDto = confirmPayment(paymentId, 200);

        assertEquals("Payment status INITIAL", PaymentStatus.INITIAL, paymentDto.getStatus());


        //eventually we authorize payment

        paymentDto = authorizePayment(paymentId, 200);

        assertEquals("Payment status AUTHORIZED", PaymentStatus.AUTHORIZED, paymentDto.getStatus());

        paymentDto = authorizePayment(paymentId, 200);

        assertEquals("Payment status AUTHORIZED", PaymentStatus.AUTHORIZED, paymentDto.getStatus());


        //and confirm

        paymentDto = confirmPayment(paymentId, 200);

        assertEquals("Payment status CONFIRMED", PaymentStatus.CONFIRMED, paymentDto.getStatus());

        paymentDto = confirmPayment(paymentId, 200);

        assertEquals("Payment status CONFIRMED", PaymentStatus.CONFIRMED, paymentDto.getStatus());

    }

    @Test
    public void holdTest() {

        //init and authorize first payment - OK

        PaymentDto paymentDto = initPayment(inAccountId, outAccountId, "700");

        final String firstPaymentId = paymentDto.getId();

        paymentDto = authorizePayment(firstPaymentId, 200);

        assertEquals("Payment status AUTHORIZED", PaymentStatus.AUTHORIZED, paymentDto.getStatus());


        //init and authorize second payment, but there is 700 cents of 1000 withhold - ERROR: WITHDRAW_NO_FUNDS

        paymentDto = initPayment(inAccountId, outAccountId, "700");

        final String secondPaymentId = paymentDto.getId();

        paymentDto = authorizePayment(secondPaymentId, 200);

        assertEquals("Payment status ERROR", PaymentStatus.ERROR, paymentDto.getStatus());

        assertEquals("Error reason WITHDRAW_NO_FUNDS", new Integer(ErrorCode.WITHDRAW_NO_FUNDS.getCode()), paymentDto.getErrorReason());


        //confirm first payment - OK

        paymentDto = confirmPayment(firstPaymentId, 200);

        assertEquals("Payment status CONFIRMED", PaymentStatus.CONFIRMED, paymentDto.getStatus());


        //confirm second payment, which was filed on authorization - still got ERROR: WITHDRAW_NO_FUNDS

        paymentDto = confirmPayment(secondPaymentId, 200);

        assertEquals("Payment status ERROR", PaymentStatus.ERROR, paymentDto.getStatus());

        assertEquals("Error reason WITHDRAW_NO_FUNDS", new Integer(ErrorCode.WITHDRAW_NO_FUNDS.getCode()), paymentDto.getErrorReason());

    }

    @Test
    public void depositWithdrawingAccount() {
        //init

        PaymentDto paymentDto = initPayment(inAccountId, outAccountId, "200");

        final String paymentId = paymentDto.getId();

        assertEquals("Payment status INITIAL", PaymentStatus.INITIAL, paymentDto.getStatus());

        //authorize

        paymentDto = authorizePayment(paymentId, 200);

        assertEquals("Payment status AUTHORIZED", PaymentStatus.AUTHORIZED, paymentDto.getStatus());

        assertEquals("200 cents should be withholded",
                "200",
                accountRepository.get(outAccountId).getHoldenAmount().toString());

        //suddenly transfer from account which is being deposited
        transfer(inAccountId, outAccountId);

        //confirm

        paymentDto = confirmPayment(paymentId, 200);

        assertEquals("Payment status CONFIRMED", PaymentStatus.CONFIRMED, paymentDto.getStatus());

        //1000 - 200 + 100 = 900
        assertEquals("Now OUT account has 900",
                "900",
                accountRepository.get(outAccountId).getAmount().toString());

        //1000 + 200 - 100 = 1100
        assertEquals("Now IN account has 1100",
                "1100",
                accountRepository.get(inAccountId).getAmount().toString());

    }


    @Before
    public void initAccounts() {
        String sql = "INSERT INTO accounts(id, enabled, amount, transferLimit, holdenAmount) " +
                "VALUES(?, ?, ?, ?, ?)";

        Connection conn = TransactionManager.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,  inAccountId);
            pstmt.setBoolean(2, true);
            pstmt.setString(3, "1000");
            pstmt.setString(4, "300000");
            pstmt.setString(5, "0");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            TransactionManager.close();
        }

        conn = TransactionManager.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,  outAccountId);
            pstmt.setBoolean(2, true);
            pstmt.setString(3, "1000");
            pstmt.setString(4, "300000");
            pstmt.setString(5, "0");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            TransactionManager.close();
        }
    }


    private PaymentDto initPayment(String accIn, String accOut, String amount) {
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest(accIn, accOut, new BigInteger(amount));
        Response createPaymentoutput = target("/api/payment")
                .request()
                .post(Entity.entity(createPaymentRequest, MediaType.APPLICATION_JSON));

        assertEquals("initPayment: Should return status 200", 200, createPaymentoutput.getStatus());

        return createPaymentoutput.readEntity(PaymentDto.class);
    }

    private PaymentDto authorizePayment(String paymentId, int expectedStatus) {
        ChangePaymentRequest authPaymentRequest = new ChangePaymentRequest(PaymentStatus.AUTHORIZED);
        Response authPaymentoutput = target("/api/payment/" + paymentId)
                .request()
                .put(Entity.entity(authPaymentRequest, MediaType.APPLICATION_JSON));

        assertEquals("Should return status 200", expectedStatus, authPaymentoutput.getStatus());

        return authPaymentoutput.readEntity(PaymentDto.class);
    }

    private PaymentDto confirmPayment(String paymentId, int expectedStatus) {
        ChangePaymentRequest confirmPaymentRequest = new ChangePaymentRequest(PaymentStatus.CONFIRMED);
        Response confirmPaymentoutput = target("/api/payment/" + paymentId)
                .request()
                .put(Entity.entity(confirmPaymentRequest, MediaType.APPLICATION_JSON));

        assertEquals("Should return status 200", expectedStatus, confirmPaymentoutput.getStatus());

        return confirmPaymentoutput.readEntity(PaymentDto.class);

    }

}

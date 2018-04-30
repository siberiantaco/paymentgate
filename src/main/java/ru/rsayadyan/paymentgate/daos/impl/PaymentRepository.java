package ru.rsayadyan.paymentgate.daos.impl;

import ru.rsayadyan.paymentgate.daos.IPaymentRepository;
import ru.rsayadyan.paymentgate.daos.TransactionManager;
import ru.rsayadyan.paymentgate.daos.impl.exception.RepositoryException;
import ru.rsayadyan.paymentgate.domain.payment.model.Payment;
import ru.rsayadyan.paymentgate.domain.payment.model.enums.PaymentStatus;

import java.math.BigInteger;
import java.sql.*;

public class PaymentRepository implements IPaymentRepository {
    public PaymentRepository() {
        try {
            Connection connection = connect();
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS payments (\n"
                    + "	id text PRIMARY KEY,\n"
                    + "	accIn text NOT NULL,\n"
                    + "	accOut text NOT NULL,\n"
                    + "	amount text NOT NULL,\n"
                    + "	status text NOT NULL,\n"
                    + "	holdId text,\n"
                    + "	errorReason text\n"
                    + ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Cannot init PaymentRepository", e);
        } finally {
            close();
        }
    }

    private Connection connect() {
        return TransactionManager.getConnection();

    }

    public void save(Payment payment) {
        String sql = "INSERT INTO payments(id, accIn, accOut, amount, status, holdId, errorReason) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try  {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, payment.getId());
            pstmt.setString(2, payment.getAccIn());
            pstmt.setString(3, payment.getAccOut());
            pstmt.setString(4, payment.getAmount().toString());
            pstmt.setString(5, payment.getStatus().toString());
            pstmt.setString(6, payment.getHoldId());
            if (payment.getErrorReason() != null)
                pstmt.setString(7, payment.getErrorReason().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Cannot save Payment", e);
        } finally {
            close();
        }

    }

    public Payment get(String paymentId) {
        String sql = "SELECT id, accIn, accOut, amount,  status, holdId, errorReason "
                + "FROM payments WHERE id = ?";

        try {

            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);

            pstmt.setString(1, paymentId);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {

                return new Payment(rs.getString("id"),
                        rs.getString("accIn"),
                        rs.getString("accOut"),
                        new BigInteger(rs.getString("amount")),
                        PaymentStatus.valueOf(rs.getString("status")),
                        rs.getString("holdId"),
                        rs.getString("errorReason") != null
                                ? Integer.valueOf(rs.getString("errorReason"))
                                : null);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Cannot get Payment", e);
        } finally {
            close();
        }
        return null;
    }

    public void update(Payment payment) {
        String sql =
                "UPDATE payments SET accIn = ?, accOut = ?, amount = ?, status = ?, holdId = ?, errorReason = ?" +
                        "WHERE id = ?";

        try  {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, payment.getAccIn());
            pstmt.setString(2, payment.getAccOut());
            pstmt.setString(3, payment.getAmount().toString());
            pstmt.setString(4, payment.getStatus().toString());
            pstmt.setString(5, payment.getHoldId());
            if (payment.getErrorReason() != null)
                pstmt.setString(6, payment.getErrorReason().toString());
            pstmt.setString(7, payment.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Cannot update Payment", e);
        } finally {
            close();
        }

    }

    private void close() {
        TransactionManager.close();
    }
}

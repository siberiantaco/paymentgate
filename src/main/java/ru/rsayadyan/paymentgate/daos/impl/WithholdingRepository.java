package ru.rsayadyan.paymentgate.daos.impl;

import ru.rsayadyan.paymentgate.daos.IWithholdingRepository;
import ru.rsayadyan.paymentgate.daos.TransactionManager;
import ru.rsayadyan.paymentgate.daos.impl.exception.RepositoryException;
import ru.rsayadyan.paymentgate.domain.account.model.Withholding;

import java.math.BigInteger;
import java.sql.*;

public class WithholdingRepository implements IWithholdingRepository {

    public WithholdingRepository() {
        try {
            Connection connection = connect();
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS withholdings (\n"
                    + "	id VARCHAR PRIMARY KEY,\n"
                    + "	amount VARCHAR NOT NULL,\n"
                    + "	accountId VARCHAR NOT NULL\n"
                    + ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Cannot init WithholdingRepository", e);
        } finally {
            close();
        }
    }

    private Connection connect() {
        return TransactionManager.getConnection();
    }

    public void save(Withholding withholding) {
        String sql = "INSERT INTO withholdings(id, amount, accountId) " +
                "VALUES(?, ?, ?)";

        try  {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, withholding.getId());
            pstmt.setString(2, withholding.getAmount().toString());
            pstmt.setString(3, withholding.getAccountId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Cannot save Withholding", e);
        } finally {
            close();
        }

    }

    public void delete(Withholding withholding) {
        String sql = "DELETE FROM withholdings WHERE id = ?";

        try  {

            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, withholding.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Cannot delete Withholding", e);
        } finally {
            close();
        }

    }

    public Withholding get(String holdId) {
        String sql = "SELECT id, amount, accountId "
                + "FROM withholdings WHERE id = ? FOR UPDATE";

        try {

            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);

            pstmt.setString(1, holdId);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                return new Withholding(rs.getString("id"),
                        new BigInteger(rs.getString("amount")),
                        rs.getString("accountId"));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Cannot get Withholding", e);
        } finally {
            close();
        }
        return null;
    }

    private void close() {
        TransactionManager.close();
    }
}

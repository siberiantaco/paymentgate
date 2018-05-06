package ru.rsayadyan.paymentgate.daos.impl;

import ru.rsayadyan.paymentgate.daos.IAccountRepository;
import ru.rsayadyan.paymentgate.daos.TransactionManager;
import ru.rsayadyan.paymentgate.daos.impl.exception.RepositoryException;
import ru.rsayadyan.paymentgate.domain.account.model.Account;

import java.math.BigInteger;
import java.sql.*;

public class AccountRepository implements IAccountRepository {

    public AccountRepository() {
        try {
            Connection connection = connect();
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS accounts (\n"
                    + "	id VARCHAR PRIMARY KEY,\n"
                    + "	enabled integer NOT NULL,\n"
                    + "	amount VARCHAR NOT NULL,\n"
                    + "	transferLimit VARCHAR NOT NULL,\n"
                    + "	holdenAmount VARCHAR\n"
                    + ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Cannot init AccountRepository", e);
        } finally {
            close();
        }

        initAccounts();
    }

    public void initAccounts() {
        String sql = "INSERT INTO accounts(id, enabled, amount, transferLimit, holdenAmount) " +
                "VALUES(?, ?, ?, ?, ?)";

        Connection conn = TransactionManager.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,  "123");
            pstmt.setBoolean(2, true);
            pstmt.setString(3, "1000");
            pstmt.setString(4, "300000");
            pstmt.setString(5, "0");
            pstmt.executeUpdate();
        } catch (SQLException e) {
        } finally {
            TransactionManager.close();
        }

        conn = TransactionManager.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,  "321");
            pstmt.setBoolean(2, true);
            pstmt.setString(3, "1000");
            pstmt.setString(4, "300000");
            pstmt.setString(5, "0");
            pstmt.executeUpdate();
        } catch (SQLException e) {
        } finally {
            TransactionManager.close();
        }
    }

    private void close() {
        TransactionManager.close();
    }

    private Connection connect() {
            return TransactionManager.getConnection();
    }

    public Account get(String accId) {
        String sql = "SELECT id, enabled, amount, transferLimit, holdenAmount "
                + "FROM accounts WHERE id = ? FOR UPDATE";

        try {

            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);

            pstmt.setString(1, accId);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {

                return new Account(rs.getString("id"),
                        rs.getInt("enabled") == 1,
                        new BigInteger(rs.getString("amount")),
                        new BigInteger(rs.getString("transferLimit")),
                        new BigInteger(rs.getString("holdenAmount")));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Cannot get Account", e);
        } finally {
            close();
        }
        return null;
    }

    public void update(Account account) {
        String sql =
                "UPDATE accounts SET enabled = ?, amount = ?, transferLimit = ?, holdenAmount = ?" +
                        "WHERE id = ?";

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setBoolean(1, account.isEnabled());
            pstmt.setString(2, account.getAmount().toString());
            pstmt.setString(3, account.getTransferLimit().toString());
            pstmt.setString(4, account.getHoldenAmount().toString());
            pstmt.setString(5, account.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Cannot update Account", e);
        } finally {
            close();
        }

    }
}

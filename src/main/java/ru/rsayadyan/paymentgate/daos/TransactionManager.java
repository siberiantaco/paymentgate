package ru.rsayadyan.paymentgate.daos;

import ru.rsayadyan.paymentgate.daos.impl.exception.RepositoryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TransactionManager

{
    private Connection connection = null;
    static ThreadLocal localConnection = new ThreadLocal();
    static ThreadLocal transactionStarted = new ThreadLocal();


    public static void startTransaction()

    {
        Connection con = (Connection) localConnection.get();
        try {
            if (con != null)
                con.close();
            con = DriverManager.
                    getConnection("jdbc:h2:./data/testdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MULTI_THREADED=TRUE");
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException e) {
            throw new RepositoryException("Cannot create transaction", e);
        }
        localConnection.set(con);
        transactionStarted.set(true);

    }

    public static Connection getConnection()

    {
        Connection con = (Connection) localConnection.get();
        if (con == null) {
            try {
                con = DriverManager.
                        getConnection("jdbc:h2:./data/testdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MULTI_THREADED=TRUE");
                con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                localConnection.set(con);
            } catch (SQLException e) {
                throw new RepositoryException("Cannot create connection", e);
            }
        }
        return con;
    }

    public static void commit()
    {
        Connection con = (Connection) localConnection.get();
        if(con != null){
            try {
                con.commit();
                con.close();
                con = null;
                localConnection.set(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        transactionStarted.set(false);

    }

    public static void rollback()
    {
        Connection con = (Connection) localConnection.get();
        if(con != null){
            try {
                con.rollback();
                con.close();
                con = null;
                localConnection.set(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        transactionStarted.set(false);

    }

    public static void close() {
        Boolean started = (Boolean) transactionStarted.get();
        Connection con = (Connection) localConnection.get();

        try {
            if ((started == null) || !started && (con != null)) {
                con.close();
                localConnection.set(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
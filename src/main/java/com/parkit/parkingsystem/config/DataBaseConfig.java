package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;


/**
 * Class of configuration for the database MySql.
 *  @author Mohamed Aymen TLILI
 */
 public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");


    /**
     * method to connect to the database.
     *
     * @return a connection with the database
     * @throws ClassNotFoundException if the Database is not found
     * @throws SQLException if there is problem of connection with the database
     */

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/prod","root","rootroot");
    }


    /**
     * method to close the connection with the database.
     * @param con A connection to the database
     */
    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    /**
     * method to close the preparedStatement to send SQL request.
     * @param ps a preparedStatement to send request to the database
     */

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    /**
     * method to close the reader of result to SQL request.
     * @param rs a ResultSet a list with the answer of a SQL request
     */
    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}

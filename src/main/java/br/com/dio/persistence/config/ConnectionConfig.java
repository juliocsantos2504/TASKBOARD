package br.com.dio.persistence.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {
    public static Connection getConnection() throws SQLException {
        var url = "jdbc:mysql://localhost:3306/taskboard";
        var user = "root";
        var passoword ="root";
        var connection = DriverManager.getConnection(url,user,passoword);
        connection.setAutoCommit(false);
        return connection;
    }
}

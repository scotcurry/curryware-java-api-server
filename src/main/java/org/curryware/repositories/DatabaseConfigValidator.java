package org.curryware.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Component
public class DatabaseConfigValidator {

    private static final Logger logger = LogManager.getLogger(DatabaseConfigValidator.class);

    private final String database;
    private final String password;
    private  String port;
    private String server;
    private final String username;

    public DatabaseConfigValidator() {
        this.database = System.getenv("POSTGRES_DATABASE");
        this.password = System.getenv("POSTGRES_PASSWORD");
        this.port = System.getenv("POSTGRES_PORT");
        this.server = System.getenv("POSTGRES_SERVER");
        this.username = System.getenv("POSTGRES_USERNAME");

        validateEnvVariables();
    }

    private void validateEnvVariables() {
        if (database == null || password == null || port == null || server == null || username == null) {
            logger.warn("One or more Postgres environment variables are missing!");
        }
    }

    public boolean checkNetworkConnectivity(String server, String port) {
        this.server = server;
        this.port = port;
        return checkNetworkConnectivity();
    }

    public boolean checkNetworkConnectivity() {
        if (server == null || port == null) {
            logger.error("Cannot check connectivity: Server or Port is null");
            return false;
        }

        int portInt;
        try {
            portInt = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            logger.error("Invalid port format: {}", port);
            return false;
        }

        try (Socket socket = new Socket()) {
            logger.info("Checking connectivity to {}:{}", server, portInt);
            socket.connect(new InetSocketAddress(server, portInt), 3000); // 3 second timeout
            logger.info("Network connectivity to Postgres server successful.");
            return true;
        } catch (IOException e) {
            logger.error("Failed to connect to Postgres server at {}:{}. Error: {}", server, port, e.getMessage());
            return false;
        }
    }

    public String getDatabase() { return database; }
    public String getPassword() { return password; }
    public String getPort() { return port; }
    public String getServer() { return server; }
    public String getUsername() { return username; }
}
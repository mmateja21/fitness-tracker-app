package com.example.fitnesstrackingapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public final class DatabaseManager {

    // Direktorijum u kojem se cuva baza.

    private static final Path DATABASE_DIRECTORY = Path.of("data");

    // Putanja do SQLite baze

    private static final Path DATABASE_PATH =
            DATABASE_DIRECTORY.resolve("fitness-tracker.db");

    //Adresa baze

    private static final String DATABASE_URL =
            "jdbc:sqlite:" + DATABASE_PATH;

    // Putanja do seme unutar project foldera
    private static final String SCHEMA_RESOURCE = "/db/schema.sql";


    private DatabaseManager() {
    }

    /**
     * Otvara novu konekciju sa bazom i uključuje FK
     *  otvorena  konekcija
     * throws SQLException ako konekcija ne može da se otvori
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    /**
     * Kreira direktorijum baze i izvrsava SQL semu

     * @throws SQLException ako izvrsavanje SQL-a ne uspe
     * @throws IOException ako schema.sql ne moze da se procita
     */
    public static void initializeDatabase()
            throws SQLException, IOException {

        Files.createDirectories(DATABASE_DIRECTORY);
        String schema = readSchema();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            for (String sqlStatement : schema.split(";")) {
                String trimmedStatement = sqlStatement.trim();

                if (!trimmedStatement.isEmpty()) {
                    statement.execute(trimmedStatement);
                }
            }
        }
    }

    /**
     * Ucitava sadrzaj SQL seme iz resources foldera
     *
     * @return kompletan sadrzaj schema.sql fajla
     * @throws IOException ako resurs ne postoji ili ne moze da se procita
     */
    private static String readSchema() throws IOException {
        try (InputStream inputStream =
                     DatabaseManager.class.getResourceAsStream(SCHEMA_RESOURCE)) {

            if (inputStream == null) {
                throw new IOException(
                        "SQL schema nije pronadjena: " + SCHEMA_RESOURCE
                );
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}
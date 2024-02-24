package repositories;

import entities.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightsRepository {
    private static FlightsRepository instance;
    private Connection connection;


    private FlightsRepository() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/planedb4", "postgres", "");
            createFlightsTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized FlightsRepository getInstance() {
        if (instance == null) {
            instance = new FlightsRepository();
        }
        return instance;
    }
    private static void createFlightsTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {

            String createTableQuery = "CREATE TABLE IF NOT EXISTS flights ("
                    + "id SERIAL PRIMARY KEY,"
                    + "destination VARCHAR(50) NOT NULL,"
                    + "departuredate VARCHAR(50) NOT NULL,"
                    + "departuretime VARCHAR(50) NOT NULL,"
                    + "arrivaldate VARCHAR(50) NOT NULL,"
                    + "arrivaltime VARCHAR(50) NOT NULL,"
                    + "price DOUBLE PRECISION NOT NULL)";
            statement.executeUpdate(createTableQuery);
        }
    }
    public void addFlight(Ticket ticket) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO flights (destination, departuredate, departuretime, arrivaldate, arrivaltime, price) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, ticket.getDestination());
            preparedStatement.setString(2, ticket.getDepartureDate());
            preparedStatement.setString(3, ticket.getDepartureTime());
            preparedStatement.setString(4, ticket.getArrivalDate());
            preparedStatement.setString(5, ticket.getArrivalTime());
            preparedStatement.setDouble(6, ticket.getEconomyClassPrice());


            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));
                }
                System.out.println("Ticket added successfully");
            } else {
                System.out.println("Failed to add ticket. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Ticket> getTickets() {
        List<Ticket> ticketList = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM flights");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String destination = resultSet.getString("destination");
                String departuredate = resultSet.getString("departuredate");
                String departuretime = resultSet.getString("departuretime");
                String arrivaldate = resultSet.getString("arrivaldate");
                String arrivaltime = resultSet.getString("arrivaltime");
                double price = resultSet.getDouble("price");

                Ticket ticket = new Ticket(id, destination, departuredate, departuretime, arrivaldate, arrivaltime, price);

                ticketList.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketList;
    }
    public void updateFlight(Ticket ticket) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE flights SET destination = ?, departuredate = ?, departuretime = ?, arrivaldate = ?, arrivaltime = ?, price = ? WHERE id = ?")) {

            preparedStatement.setString(1, ticket.getDestination());
            preparedStatement.setString(2, ticket.getDepartureDate());
            preparedStatement.setString(3, ticket.getDepartureTime());
            preparedStatement.setString(4, ticket.getArrivalDate());
            preparedStatement.setString(5, ticket.getArrivalTime());
            preparedStatement.setDouble(6, ticket.getEconomyClassPrice());
            preparedStatement.setInt(7, ticket.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Flight updated successfully!");
            } else {
                System.out.println("Failed to update flight. Flight not found or update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean deleteFlight(int flightId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM flights WHERE id = ?")) {

            preparedStatement.setInt(1, flightId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

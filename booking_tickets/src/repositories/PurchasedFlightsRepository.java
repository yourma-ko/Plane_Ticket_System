package repositories;

import trash.UserController;
import entities.Ticket;
import entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PurchasedFlightsRepository {
    private static PurchasedFlightsRepository instance;
    private Connection connection;
    private UserController userController;


    private PurchasedFlightsRepository() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/planedb4", "postgres", "");
            createPurchasedFlightsTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized PurchasedFlightsRepository getInstance() {
        if (instance == null) {
            instance = new PurchasedFlightsRepository();
        }
        return instance;
    }
    public String getUserType(User user) {
        String userType = "regular";
        int userId = user.getId();
        System.out.println(userId);
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT user_type FROM users WHERE id = " + userId;
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                userType = resultSet.getString("user_type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(userType);
        return userType;
    }
    private static void createPurchasedFlightsTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS purchasedflights ("
                    + "id SERIAL PRIMARY KEY,"
                    + "user_id INTEGER NOT NULL,"
                    + "destination VARCHAR(50) NOT NULL,"
                    + "departuredate VARCHAR(50) NOT NULL,"
                    + "departuretime VARCHAR(50) NOT NULL,"
                    + "arrivaldate VARCHAR(50) NOT NULL,"
                    + "arrivaltime VARCHAR(50) NOT NULL,"
                    + "price DOUBLE PRECISION NOT NULL,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id))"; // Add foreign key constraint
            statement.executeUpdate(createTableQuery);
        }
    }
    public List<Ticket> getPurchasedTickets(User user) {
        List<Ticket> purchasedTicketList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM purchasedflights WHERE user_id = ?")) {

            preparedStatement.setInt(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String destination = resultSet.getString("destination");
                String departuredate = resultSet.getString("departuredate");
                String departuretime = resultSet.getString("departuretime");
                String arrivaldate = resultSet.getString("arrivaldate");
                String arrivaltime = resultSet.getString("arrivaltime");
                double price = resultSet.getDouble("price");

                Ticket ticket = new Ticket(destination, departuredate, departuretime, arrivaldate, arrivaltime, price);


                purchasedTicketList.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return purchasedTicketList;
    }
    public void addPurchasedTicket(Ticket ticket, User user) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO purchasedflights (user_id, destination, departuredate, departuretime, arrivaldate, arrivaltime, price) VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, ticket.getDestination());
            preparedStatement.setString(3, ticket.getDepartureDate());
            preparedStatement.setString(4, ticket.getDepartureTime());
            preparedStatement.setString(5, ticket.getArrivalDate());
            preparedStatement.setString(6, ticket.getArrivalTime());
            preparedStatement.setDouble(7, ticket.getEconomyClassPrice());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));
                }
                System.out.println("Purchased ticket added successfully");
            } else {
                System.out.println("Failed to add purchased ticket. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean buyTicket(User user, Ticket ticket) {
        String type = getUserType(user);
        System.out.println(user.getId());
        try {
            connection.setAutoCommit(false);

            if(Objects.equals(type, "business")) {
                if (user.getBalance() >= (ticket.getEconomyClassPrice()+10000)) {

                    user.setBalance(user.getBalance() - (ticket.getEconomyClassPrice()+10000));

                    addPurchasedTicket(ticket, user);

                    updateBalanceInDatabase(user);

                    connection.commit();

                    return true;
                } else {
                    System.out.println("Not enough balance to buy the ticket.");
                    return false;
                }
            }else{
                if (user.getBalance() >= (ticket.getEconomyClassPrice())) {

                    user.setBalance(user.getBalance() - (ticket.getEconomyClassPrice()));

                    addPurchasedTicket(ticket, user);

                    updateBalanceInDatabase(user);

                    connection.commit();

                    return true;
                } else {
                    System.out.println("Not enough balance to buy the ticket.");
                    return false;
                }

            }
        } catch (SQLException e) {

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            e.printStackTrace();
            return false;
        } finally {

            try {
                connection.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }
        }
    }

    private void updateBalanceInDatabase(User user) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE users SET balance = ? WHERE id = ?")) {

            preparedStatement.setDouble(1, user.getBalance());
            preparedStatement.setInt(2, user.getId());

            preparedStatement.executeUpdate();
        }
    }
    public boolean cancelTicket(User user, Ticket ticket) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM purchasedflights WHERE user_id = ? AND destination = ? AND departuredate = ? AND departuretime = ? AND arrivaldate = ? AND arrivaltime = ? AND price = ?")) {

            preparedStatement.setInt(1, user.getId());

            preparedStatement.setString(2, ticket.getDestination());
            preparedStatement.setString(3, ticket.getDepartureDate());
            preparedStatement.setString(4, ticket.getDepartureTime());
            preparedStatement.setString(5, ticket.getArrivalDate());
            preparedStatement.setString(6, ticket.getArrivalTime());
            preparedStatement.setDouble(7, ticket.getEconomyClassPrice());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                if(Objects.equals(getUserType(user), "business")) {
                    user.setBalance(user.getBalance() + (ticket.getEconomyClassPrice()+10000));
                }else{
                    user.setBalance(user.getBalance() + (ticket.getEconomyClassPrice()));
                }
                updateBalanceInDatabase(user);
                return true;
            } else {
                System.out.println("Ticket not found or could not be canceled.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}

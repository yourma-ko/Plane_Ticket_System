    package repositories;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Scanner;

    import entities.Ticket;
    import entities.User;

    public class UserRepository {

        private Connection connection;

        public UserRepository(Connection connection) {
            this.connection = connection;
        }

        public void addUser(User user) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (username, age, balance) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, user.getName());
                preparedStatement.setInt(2, user.getAge());
                preparedStatement.setDouble(3, user.getBalance());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                    System.out.println("User added successfully!");
                } else {
                    System.out.println("Failed to add user. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
        public List<User> getUsers() {
            List<User> userList = new ArrayList<>();
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    int age = resultSet.getInt("age");
                    double balance = resultSet.getDouble("balance");

                    User user = new User(id, username, age, balance);
                    userList.add(user);

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return userList;
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
        public boolean buyTicket(User user, Ticket ticket) {
            try {
                connection.setAutoCommit(false);


                if (user.getBalance() >= ticket.getEconomyClassPrice()) {

                    user.setBalance(user.getBalance() - ticket.getEconomyClassPrice());

                    addPurchasedTicket(ticket, user);

                    updateBalanceInDatabase(user);

                    connection.commit();

                    return true;
                } else {
                    System.out.println("Not enough balance to buy the ticket.");
                    return false;
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
                    user.setBalance(user.getBalance() + ticket.getEconomyClassPrice());
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
        public void updateUser(User user) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE users SET username = ?, age = ?, balance = ? WHERE id = ?")) {

                preparedStatement.setString(1, user.getName());
                preparedStatement.setInt(2, user.getAge());
                preparedStatement.setDouble(3, user.getBalance());
                preparedStatement.setInt(4, user.getId());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User updated successfully!");
                } else {
                    System.out.println("Failed to update user. User not found or update failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public boolean deleteUser(int userId) {
            try {
                connection.setAutoCommit(false);

                // Delete purchased flights related to the user
                deletePurchasedFlights(userId);

                // Now delete the user
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM users WHERE id = ?")) {

                    preparedStatement.setInt(1, userId);

                    int rowsAffected = preparedStatement.executeUpdate();

                    connection.commit();

                    return rowsAffected > 0;
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

        private void deletePurchasedFlights(int userId) throws SQLException {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM purchasedflights WHERE user_id = ?")) {

                preparedStatement.setInt(1, userId);

                preparedStatement.executeUpdate();
            }
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

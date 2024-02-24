    package repositories;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    import entities.BusinessClassUser;
    import entities.User;

    public class UserRepository {

    private static UserRepository instance;
        private Connection connection;

        private UserRepository() {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/planedb4", "postgres", "");
                createUsersTable(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static synchronized UserRepository getInstance() {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
        private static void createUsersTable(Connection connection) throws SQLException {
            try (Statement statement = connection.createStatement()) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS users ("
                        + "id SERIAL PRIMARY KEY,"
                        + "username VARCHAR(50) NOT NULL,"
                        + "age INT NOT NULL,"
                        + "balance DOUBLE PRECISION NOT NULL,"
                        + "user_type VARCHAR(10) NOT NULL)";

                statement.executeUpdate(createTableQuery);
            }
        }


        public void addUser(User user) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (username, age, balance, user_type) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, user.getName());
                preparedStatement.setInt(2, user.getAge());
                preparedStatement.setDouble(3, user.getBalance());
                preparedStatement.setString(4, user instanceof BusinessClassUser ? "business" : "regular");

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
        /*public String getUserType(User user) {
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
        }*/


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


    }

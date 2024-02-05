package repositories;

import Controllers.UserController;
//import entities.BusinessClassUser;
import entities.Ticket;
import entities.User;
import repositories.Aeroplanesystem;
import repositories.UserRepository;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/planedb3";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1718";

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            createUsersTable(connection);
            createFlightsTable(connection);
            createPurchasedFlightsTable(connection);

            UserRepository userRepository = new UserRepository(connection);
            UserController userController = new UserController(userRepository);
            runUserManagementApp(userController);

            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
        private static void createUsersTable (Connection connection) throws SQLException {
            try (Statement statement = connection.createStatement()) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS users ("
                        + "id SERIAL PRIMARY KEY,"
                        + "username VARCHAR(50) NOT NULL,"
                        + "age INT NOT NULL,"
                        + "balance DOUBLE PRECISION NOT NULL)";

                statement.executeUpdate(createTableQuery);
            }
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



            private static void runUserManagementApp(UserController userController) {
            Aeroplanesystem aeroplanesystem = new Aeroplanesystem();
            Scanner scanner = new Scanner(System.in);


            while (true) {
                System.out.println("Welcome! What do you want to do?");
                System.out.println("1) To add a flight\n" +
                        "2) To show all available flights\n" +
                        "3) To add a new passenger\n" +
                        "4) To buy a ticket\n" +
                        "5) To cancel a purchase of the ticket.\n" +
                        "6) History of tickets\n" +
                        "7) Update user\n" +
                        "8) Delete user\n" +
                        "9) See all users\n" +
                        "10) Update ticket\n" +
                        "11) Delete ticket\n" +
                        "12) Exit\n");

                int userInput = scanner.nextInt();

                switch (userInput) {
                    case 1:
                        userController.addFlight(scanner);
                        break;

                    case 2:
                        List<Ticket> tickets = userController.getTickets();
                        if (tickets.isEmpty()) {
                            System.out.println("No tickets have been added yet. Please add tickets first.");
                        } else {
                            System.out.println("Information for all created tickets:");
                            for (Ticket ticket : tickets) {
                                System.out.println("ID: " + ticket.getId());
                                System.out.println("Destination: " + ticket.getDestination());
                                System.out.println("Departure Date: " + ticket.getDepartureDate());
                                System.out.println("Departure Time: " + ticket.getDepartureTime());
                                System.out.println("Arrival Date: " + ticket.getArrivalDate());
                                System.out.println("Arrival Time: " + ticket.getArrivalTime());
                                System.out.println("Ticket Price: " + ticket.getEconomyClassPrice());
                                System.out.println();
                            }
                        }
                        break;

                    case 3:
                        userController.addUser(scanner);
                        break;
                    case 4:
                        List<User> usersList = userController.getUsers(); // Rename users to usersList to avoid conflict

                        if (usersList.isEmpty()) {
                            System.out.println("No users available. Please add passengers first.");
                            break;
                        }

                        System.out.println("Enter the user name:");
                        scanner.nextLine();

                        String userName = scanner.nextLine();

                        User selectedUser = null;
                        double userBalance = 0;

                        for (User user : usersList) {
                            if (user.getName().equals(userName)) {
                                selectedUser = user;
                                userBalance = user.getBalance();
                                System.out.println("Your balance is: " + userBalance);
                                break;
                            }
                        }

                        if (selectedUser == null) {
                            System.out.println("User not found.");
                            break;
                        }

                        List<Ticket> availableTickets = userController.getTickets();

                        if (availableTickets.isEmpty()) {
                            System.out.println("No tickets available. Please add flights first.");
                            break;
                        }

                        System.out.println("Available tickets:");

                        for (int i = 0; i < availableTickets.size(); i++) {
                            Ticket ticket = availableTickets.get(i);
                            System.out.println((i + 1) + ". Destination: " + ticket.getDestination() +
                                    " Ticket Price: " + ticket.getEconomyClassPrice());
                        }

                        System.out.println("Enter the number of the ticket to buy:");

                        try {
                            int selectedTicketNumber = scanner.nextInt();

                            if (selectedTicketNumber < 1 || selectedTicketNumber > availableTickets.size()) {
                                System.out.println("Invalid ticket number.");
                                break;
                            }

                            Ticket selectedTicket = availableTickets.get(selectedTicketNumber - 1);

                            boolean success = userController.buyTicket(selectedUser, selectedTicket);

                            if (success) {
                                System.out.println("Ticket purchased successfully!");
                            } else {
                                System.out.println("Failed to purchase the ticket. Insufficient balance.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                            scanner.nextLine(); // Consume the invalid input to avoid an infinite loop
                        }
                        break;


                    case 5:

                        userController.cancelTicket(scanner);
                        break;
                    case 6:
                        List<User> usersListH = userController.getUsers();
                        System.out.println("History of tickets:");
                        System.out.println("Write username to see history");
                        scanner.nextLine();
                        String userNameH = scanner.nextLine();
                        User selectedUserH = null;
                        for (User user : usersListH) {
                            if (user.getName().equals(userNameH)) {
                                selectedUserH = user;
                                break;
                            }
                        }
                        List<Ticket> soldTickets = userController.getPurchasedTickets(selectedUserH);

                        for (int i = 0; i < soldTickets.size(); i++) {
                            Ticket ticket = soldTickets.get(i);

                            System.out.println(
                                    "Destination: " + ticket.getDestination() +
                                      ", Departure date: " + ticket.getDepartureDate() +
                                      ", Departure time: " + ticket.getDepartureDate() +
                                    ", Ticket Price: " + ticket.getEconomyClassPrice());
                            for (User user : aeroplanesystem.getUsers()) {
                                if (user.getName() != null && user.getName().equals(ticket.getUserName())) {
                                    /*if (user instanceof BusinessClassUser) {
                                        BusinessClassUser businessUser = (BusinessClassUser) user;
                                        System.out.println("Additional Baggage: " + businessUser.getAdditionalBaggage() + " kg");
                                    }*/
                                    break;
                                }
                            }

                        }
                        break;
                    case 7:
                        userController.updateUser(scanner);
                        break;
                    case 8:
                        userController.deleteUser(scanner);
                        break;
                    case 9:
                        userController.printUsers();
                        break;
                    case 10:
                        userController.updateFlight(scanner);
                        break;
                    case 11:
                        userController.deleteFlight(scanner);
                        break;
                    case 12:
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a valid option.");
                        break;
                }
            }
        }
    }




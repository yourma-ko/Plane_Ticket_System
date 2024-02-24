package Controllers;

import entities.*;
import repositories.FlightsRepository;
import repositories.PurchasedFlightsRepository;
import repositories.UserRepository;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class PlaneFascadeController {
    private Scanner scanner;
    private FlightsRepository flightsRepository;
    private UserRepository userRepository;
    private PurchasedFlightsRepository purchasedFlightsRepository;

    public PlaneFascadeController() {
        flightsRepository = FlightsRepository.getInstance();
        userRepository = UserRepository.getInstance();
        purchasedFlightsRepository = PurchasedFlightsRepository.getInstance();
        scanner = new Scanner(System.in);
    }
    public void addUser(Scanner scanner) {
        System.out.println("To add a new person");
        System.out.println("How many persons do you want to add?:");
        int userCount = scanner.nextInt();

        for (int i = 0; i < userCount; i++) {
            System.out.println("Write name for person " + (i + 1) + ":");
            scanner.nextLine();
            String userName = scanner.nextLine();
            System.out.println("Write age for person " + (i + 1) + ":");
            int userAge = scanner.nextInt();
            System.out.println("Write balance for person " + (i + 1) + ":");
            double userBalance = scanner.nextDouble();

            System.out.println("Enter user type for person " + (i + 1) + " (1 for Regular User, 2 for Business Class User):");
            int userType = scanner.nextInt();

            double userAdditionalBaggage = 0;

            if (userType == 2) {
                System.out.println("Enter additional baggage for Business Class User:");
                userAdditionalBaggage = scanner.nextDouble();
                if (userAdditionalBaggage > 20) {
                    System.out.println("Additional Baggage exceeds the limit (20 kg). Please enter a valid weight.");
                    continue;
                }
            }

            UserFactory userFactory;
            if (userType == 1) {
                userFactory = new StandardUserFactory();
            } else if (userType == 2) {
                userFactory = new BusinessClassUserFactory();
            } else {
                System.out.println("Invalid user type. Skipping user addition.");
                continue;
            }

            User user = userFactory.createUser(userName, userAge, userBalance, userAdditionalBaggage);
            userRepository.addUser(user);
            System.out.println("You added a person successfully!");
        }
    }


    public List<User> getUsers(){

        return userRepository.getUsers();
    }

    public void updateUser(Scanner scanner) {
        System.out.println("To update a user, enter the user name:");
        scanner.nextLine();
        String updateUserName = scanner.nextLine();

        List<User> usersList = getUsers();
        User updateUser = null;

        for (User user : usersList) {
            if (user.getName().equals(updateUserName)) {
                updateUser = user;
                break;
            }
        }

        if (updateUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("Enter new name for user " + updateUser.getName() + ":");
        String updatedName = scanner.nextLine();
        System.out.println("Enter new age for user " + updateUser.getName() + ":");
        int updatedAge = scanner.nextInt();
        System.out.println("Enter new balance for user " + updateUser.getName() + ":");
        double updatedBalance = scanner.nextDouble();

        updateUser.setName(updatedName);
        updateUser.setAge(updatedAge);
        updateUser.setBalance(updatedBalance);

        userRepository.updateUser(updateUser);
    }
    public void deleteUser(Scanner scanner) {
        System.out.println("To delete a user, enter the user ID:");
        int userId = scanner.nextInt();

        if (userRepository.deleteUser(userId)) {
            System.out.println("User deleted successfully!");
        } else {
            System.out.println("Failed to delete user. User not found or an error occurred.");
        }
    }
    public void printUsers() {
        List<User> userList = userRepository.getUsers();

        if (userList.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("List of Users:");
            for (User user : userList) {
                System.out.println("User ID: " + user.getId());
                System.out.println("Username: " + user.getName());
                System.out.println("Age: " + user.getAge());
                System.out.println("Balance: $" + user.getBalance());
                System.out.println("-------------------------");
            }
        }
    }
    public List<Ticket> getTickets(){
        return flightsRepository.getTickets();
    }
    public void addFlight(Scanner scanner){
        System.out.println("To add a new flight you should add information");
        System.out.println("How many tickets you want to add?:");
        int ticketCount = scanner.nextInt();
        for (int i = 0; i < ticketCount; i++) {
            System.out.println("Write destination for Ticket " + (i + 1) + ":");
            scanner.nextLine();
            String userDest = scanner.nextLine();
            System.out.println("Write the day of departure for Ticket " + (i + 1) + ":");
            String userInputDate = scanner.nextLine();
            System.out.println("Write time of departure for Ticket " + (i + 1) + ":");
            String userInputTime = scanner.nextLine();
            System.out.println("Write the day of arrival for Ticket " + (i + 1) + ":");
            String userInputArrDate = scanner.nextLine();
            System.out.println("Write the arrival time for Ticket " + (i + 1) + ":");
            String userInputArrTime = scanner.nextLine();
            System.out.println("Write price for Ticket " + (i + 1) + ":");
            double userEco = scanner.nextDouble();
            Ticket ticket = new Ticket(userDest, userInputDate, userInputTime, userInputArrDate, userInputArrTime, userEco);
            flightsRepository.addFlight(ticket);
            System.out.println("You added tickets successfully!");
        }
    }
    public void updateFlight(Scanner scanner) {
        System.out.println("To update a flight, enter the flight ID:");
        int flightId = scanner.nextInt();

        List<Ticket> flightsList = getTickets();
        Ticket updateFlight = null;

        for (Ticket flight : flightsList) {
            if (flight.getId() == flightId) {
                updateFlight = flight;
                break;
            }
        }

        if (updateFlight == null) {
            System.out.println("Flight not found.");
            return;
        }

        System.out.println("Enter new destination for flight ID " + updateFlight.getId() + ":");
        scanner.nextLine();
        String updatedDestination = scanner.nextLine();
        System.out.println("Enter new departure date for flight ID " + updateFlight.getId() + ":");
        String updatedDepartureDate = scanner.nextLine();
        System.out.println("Enter new departure time for flight ID " + updateFlight.getId() + ":");
        String updatedDepartureTime = scanner.nextLine();
        System.out.println("Enter new arrival date for flight ID " + updateFlight.getId() + ":");
        String updatedArrivalDate = scanner.nextLine();
        System.out.println("Enter new arrival time for flight ID " + updateFlight.getId() + ":");
        String updatedArrivalTime = scanner.nextLine();
        System.out.println("Enter new price for flight ID " + updateFlight.getId() + ":");
        double updatedPrice = scanner.nextDouble();

        updateFlight.setDestination(updatedDestination);
        updateFlight.setDepartureDate(updatedDepartureDate);
        updateFlight.setDepartureTime(updatedDepartureTime);
        updateFlight.setArrivalDate(updatedArrivalDate);
        updateFlight.setArrivalTime(updatedArrivalTime);
        updateFlight.setEconomyClassPrice(updatedPrice);

        flightsRepository.updateFlight(updateFlight);

    }
    public void deleteFlight(Scanner scanner) {
        System.out.println("To delete a flight, enter the flight ID:");
        int flightId = scanner.nextInt();

        if (flightsRepository.deleteFlight(flightId)) {
            System.out.println("Flight deleted successfully!");
        } else {
            System.out.println("Failed to delete flight. Flight not found or an error occurred.");
        }
    }

    public List<Ticket> getPurchasedTickets(User user){
        return purchasedFlightsRepository.getPurchasedTickets(user);
    }
    public boolean buyTicket(User user, Ticket ticket) {
        if (purchasedFlightsRepository.buyTicket(user, ticket)) {
            System.out.println("Ticket purchased successfully!");
            return true;
        } else {
            System.out.println("Failed to purchase the ticket. Insufficient balance.");
            return false;
        }
    }
    public void cancelTicket(Scanner scanner) {
        System.out.println("To cancel a ticket, enter the user name:");
        scanner.nextLine();
        String cancelUserName = scanner.nextLine();

        List<User> usersList = getUsers();
        User cancelUser = null;

        for (User user : usersList) {
            if (user.getName().equals(cancelUserName)) {
                cancelUser = user;
                break;
            }
        }

        if (cancelUser == null) {
            System.out.println("User not found.");
            return;
        }

        List<Ticket> purchasedTickets = getPurchasedTickets(cancelUser);

        if (purchasedTickets.isEmpty()) {
            System.out.println("No purchased tickets available for this user.");
            return;
        }

        System.out.println("Purchased tickets for user " + cancelUser.getName() + ":");
        for (int i = 0; i < purchasedTickets.size(); i++) {
            Ticket ticket = purchasedTickets.get(i);
            System.out.println((i + 1) + ". Destination: " + ticket.getDestination() +
                    " Ticket Price: " + ticket.getEconomyClassPrice());
        }

        System.out.println("Enter the number of the ticket to cancel:");

        try {
            int cancelTicketNumber = scanner.nextInt();

            if (cancelTicketNumber < 1 || cancelTicketNumber > purchasedTickets.size()) {
                System.out.println("Invalid ticket number.");
                return;
            }

            Ticket canceledTicket = purchasedTickets.get(cancelTicketNumber - 1);

            if (purchasedFlightsRepository.cancelTicket(cancelUser, canceledTicket)) {
                System.out.println("Ticket canceled successfully!");
            } else {
                System.out.println("Failed to cancel the ticket.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

}

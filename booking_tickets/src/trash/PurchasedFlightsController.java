package trash;

import entities.Ticket;
import entities.User;
import repositories.PurchasedFlightsRepository;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class PurchasedFlightsController {
    private Scanner scanner;
    private PurchasedFlightsRepository purchasedFlightsRepository;
    UserController userController = new UserController();

    public PurchasedFlightsController() {
        purchasedFlightsRepository = PurchasedFlightsRepository.getInstance();
        scanner = new Scanner(System.in);
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

        List<User> usersList = userController.getUsers();
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

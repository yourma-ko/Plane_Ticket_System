package trash;

import entities.Ticket;
import repositories.FlightsRepository;
import repositories.UserRepository;

import java.util.List;
import java.util.Scanner;

public class FlightsController {
    private FlightsRepository flightsRepository;
    private Scanner scanner;
    public FlightsController() {
        flightsRepository = FlightsRepository.getInstance();
        scanner = new Scanner(System.in);
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
        scanner.nextLine();  // Consume newline character
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
}

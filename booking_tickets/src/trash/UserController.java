package trash;
import entities.*;
import repositories.PurchasedFlightsRepository;
import repositories.UserRepository;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserController {
    private Scanner scanner;
    private UserRepository userRepository;

    public UserController() {
        userRepository = UserRepository.getInstance();
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
    /*public String getUserType(User user){
        System.out.println(user.getId());
        return userRepository.getUserType(user);
    }*/
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


}

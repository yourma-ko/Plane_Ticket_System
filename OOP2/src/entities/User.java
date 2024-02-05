package entities;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class User {
    private UserRepository userRepository;
    private Integer id;
    private String name;
    private Integer age;
    protected Double balance;
    protected List<Ticket> purchasedTickets = new ArrayList<>();

    public User(Integer id, String name, Integer age, Double balance) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.balance = balance;
    }
    public User( String name, Integer age, Double balance) {
        this.name = name;
        this.age = age;
        this.balance = balance;
    }
    public void setId(int id) {
        this.id = id;
    }
    public  Integer getId(){return id;}
    public  String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Double getBalance() {
        return balance;
    }

    public void setName(String name){this.name = name;}

    public void setAge(Integer age){this.age = age;}

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    /*public boolean buyTicket(Ticket ticket) {
        if (balance >= ticket.getEconomyClassPrice()) {
            balance -= ticket.getEconomyClassPrice();
            userRepository.addPurchasedTicket(user, ticket);
            return true;
        } else {
            System.out.println("Not enough balance to buy the ticket.");
            return false;
        }
    }*/

    public void cancelTicket(Ticket ticket) {
        balance += ticket.getEconomyClassPrice();
        purchasedTickets.remove(ticket);
    }

    public List<Ticket> getPurchasedTickets() {
        return purchasedTickets;
    }
}


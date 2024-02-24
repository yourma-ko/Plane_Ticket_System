package repositories;

import entities.Ticket;
import entities.User;

import java.util.ArrayList;
import java.util.List;

public class Aeroplanesystem {
    private ArrayList<Ticket> ticketList = new ArrayList<>();
    public ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Ticket> soldTickets = new ArrayList<>();


    public List<User> getUsers() {
        return userList;
    }

    public void setTicket(Ticket ticket) {
        ticketList.add(ticket);
    }

    public List<Ticket> getTickets() {
        return ticketList;
    }


    public List<Ticket> getPurchasedTickets(User user) {
        return user.getPurchasedTickets();
    }

    public void cancelTicket(User user, Ticket ticket) {
        user.cancelTicket(ticket);
        soldTickets.remove(ticket);
    }


    public List<Ticket> getSoldTickets() {
        return soldTickets;
    }
}





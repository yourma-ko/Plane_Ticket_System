package  entities;
public class Ticket {
   private String destination;
   private String departureDate;
   private String departureTime;
   private String arrivalDate;
   private String arrivalTime;
   private double economyClassPrice;
   private int id;


   public Ticket(String destination, String departureDate, String departureTime, String arrivalDate, String arrivalTime, double economyClassPrice) {
      this.destination = destination;
      this.departureDate = departureDate;
      this.departureTime = departureTime;
      this.arrivalDate = arrivalDate;
      this.arrivalTime = arrivalTime;
      this.economyClassPrice = economyClassPrice;

   }
   public Ticket(int id,String destination, String departureDate, String departureTime, String arrivalDate, String arrivalTime, double economyClassPrice) {
      this.id =id;
      this.destination = destination;
      this.departureDate = departureDate;
      this.departureTime = departureTime;
      this.arrivalDate = arrivalDate;
      this.arrivalTime = arrivalTime;
      this.economyClassPrice = economyClassPrice;

   }

   public String getDestination() {
      return destination;
   }

   public void setId(int id){
      this.id = id;
   }
   public int getId(){return id;}

   public String getDepartureDate() {
      return departureDate;
   }

   public String getDepartureTime() {
      return departureTime;
   }

   public String getArrivalDate() {
      return arrivalDate;
   }

   public String getArrivalTime() {
      return arrivalTime;
   }

   public double getEconomyClassPrice() {
      return economyClassPrice;
   }

   public void setDestination(String destination) {
      this.destination = destination;
   }

   public void setDepartureDate(String departureDate) {
      this.departureDate = departureDate;
   }

   public void setDepartureTime(String departureTime) {
      this.departureTime = departureTime;
   }

   public void setArrivalDate(String arrivalDate) {
      this.arrivalDate = arrivalDate;
   }

   public void setArrivalTime(String arrivalTime) {
      this.arrivalTime = arrivalTime;
   }

   public void setEconomyClassPrice(double economyClassPrice) {
      this.economyClassPrice = economyClassPrice;
   }

   private String userName;

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getUserName() {
      return userName;
   }

}





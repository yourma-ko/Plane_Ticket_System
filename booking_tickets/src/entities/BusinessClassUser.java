package entities;
public class BusinessClassUser extends User{
    public double additionalBaggage;
    public BusinessClassUser(String name, Integer age, Double balance, Double additionalBaggage){
        super(name, age, balance);
        this.additionalBaggage = additionalBaggage;
        validateBaggage(additionalBaggage);
    }
    private void validateBaggage(double baggage) {
        if (baggage < 0) {
            throw new IllegalArgumentException("Дополнительный багаж не может быть отрицательным.");
        }
    }
    public void setAdditionalBaggage(double additionalBaggage) {
        this.additionalBaggage = additionalBaggage;
    }
    public double getAdditionalBaggage() {
        return additionalBaggage;
    }

}


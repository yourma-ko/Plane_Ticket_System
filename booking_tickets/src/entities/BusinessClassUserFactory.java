package entities;

public class BusinessClassUserFactory implements UserFactory {
    @Override
    public BusinessClassUser createUser(String name, Integer age, Double balance, double additionalBaggage) {
        return new BusinessClassUser(name, age, balance, additionalBaggage);
    }
}
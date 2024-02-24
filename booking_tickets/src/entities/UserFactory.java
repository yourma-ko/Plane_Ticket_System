package entities;

public interface UserFactory {
    User createUser(String name, Integer age, Double balance, double additionalBaggage);
}

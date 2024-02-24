package entities;

public class StandardUserFactory implements UserFactory{
@Override
public User createUser(String name, Integer age, Double balance, double additonalBaggage) {
    return new User(name, age, balance);
}}
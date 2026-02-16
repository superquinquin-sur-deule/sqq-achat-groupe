package fr.sqq.achatgroupe.domain.model.order;

public record CustomerInfo(String firstName, String lastName, String email, String phone) {

    public CustomerInfo {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("Customer first name must not be blank");
        if (lastName == null) throw new IllegalArgumentException("Customer last name must not be null");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Customer email must not be blank");
        if (!email.contains("@")) throw new IllegalArgumentException("Customer email must be valid");
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("Customer phone must not be blank");
    }

    public String fullName() {
        if (lastName.isBlank()) return firstName;
        return firstName + " " + lastName;
    }

    public String displayNameLastFirst() {
        if (lastName.isBlank()) return firstName;
        return lastName.toUpperCase() + " " + firstName;
    }
}

package fr.sqq.achatgroupe.domain.model.order;

public record CustomerInfo(String name, String email, String phone) {

    public CustomerInfo {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Customer name must not be blank");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Customer email must not be blank");
        if (!email.contains("@")) throw new IllegalArgumentException("Customer email must be valid");
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("Customer phone must not be blank");
    }
}

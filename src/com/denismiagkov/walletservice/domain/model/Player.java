package com.denismiagkov.walletservice.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private String firstName;
    private String lastName;
    private String email;
    private Account account;

    public Player() {
    }

    public Player(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Player{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(firstName, player.firstName) && Objects.equals(lastName, player.lastName) && Objects.equals(email, player.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email);
    }
}

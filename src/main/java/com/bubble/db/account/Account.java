package com.bubble.db.account;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Account {

	private final String username;

	private final String firstName;

	private final String lastName;
	
	private final int age;
	
	private final String location;
	
	private final String email;

	public Account(String username,
			String firstName,
			String lastName,
			int age,
			String location,
			String email) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.location = location;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getAge() {
		return age;
	}

	public String getLocation() {
		return location;
	}

	public String getEmail() {
		return email;
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(username).
            append(firstName).
            append(lastName).
            append(age).
            toHashCode();
    }

    @Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        Account rhs = (Account) obj;
        return new EqualsBuilder().
            append(username, rhs.username).
            append(firstName, rhs.firstName).
            append(lastName, rhs.lastName).
            append(age, rhs.age).
            append(location, rhs.location).
            append(email, rhs.email).
            isEquals();
    }
}
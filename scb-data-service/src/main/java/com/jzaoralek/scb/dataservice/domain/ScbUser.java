package com.jzaoralek.scb.dataservice.domain;

import java.util.UUID;

public class ScbUser implements IdentEntity {

	private UUID uuid;
	private String firstname;
	private String lastname;
	private String password;
	private boolean passwordGenerated;
	private ScbUserRole role;
	private Contact contact;
	
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isPasswordGenerated() {
		return passwordGenerated;
	}
	public void setPasswordGenerated(boolean passwordGenerated) {
		this.passwordGenerated = passwordGenerated;
	}
	public ScbUserRole getRole() {
		return role;
	}
	public void setRole(ScbUserRole role) {
		this.role = role;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public UUID getUuid() {
		return uuid;
	}
	
	@Override
	public String toString() {
		return "ScbUser [uuid=" + uuid + ", firstname=" + firstname + ", lastname=" + lastname + ", password="
				+ password + ", passwordGenerated=" + passwordGenerated + ", role=" + role + ", contact=" + contact
				+ "]";
	}
}
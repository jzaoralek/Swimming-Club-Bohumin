package com.jzaoralek.scb.dataservice.domain;

import java.util.Date;
import java.util.UUID;

public class ScbUser implements IdentEntity {

	private UUID uuid;
	private String modifBy;
	private Date modifAt;
	private String username;
	private String password;
	private boolean passwordGenerated;
	private ScbUserRole role;
	private Contact contact;

	public ScbUser() {
		this.contact = new Contact();
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	@Override
	public String getModifBy() {
		return modifBy;
	}
	@Override
	public void setModifBy(String modifBy) {
		this.modifBy = modifBy;
	}
	@Override
	public Date getModifAt() {
		return modifAt;
	}
	@Override
	public void setModifAt(Date modifAt) {
		this.modifAt = modifAt;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		return "ScbUser [uuid=" + uuid + ", modifBy=" + modifBy + ", modifAt=" + modifAt + ", username=" + username
				+ ", password=" + password + ", passwordGenerated=" + passwordGenerated + ", role=" + role
				+ ", contact=" + contact + "]";
	}
}
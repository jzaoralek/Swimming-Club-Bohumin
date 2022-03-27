package com.jzaoralek.scb.dataservice.domain;

import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import com.sportologic.common.model.domain.IdentEntity;

public class ScbUser implements IdentEntity {

	/**
	 * COmparator for sorting by surname.
	 */
	public static final Comparator<ScbUser> PRIJMENI_COMP = 
			new Comparator<ScbUser>() {	
				@Override
				public int compare(ScbUser c1, ScbUser c2) {
				final Collator collator = Collator.getInstance(new Locale("cs","CZ"));
				String c1Prijmeni = c1.getContact().getSurname();
				String c2Prijmeni = c2.getContact().getSurname();
				return collator.compare(c1Prijmeni, c2Prijmeni);
			}
		};
		
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
				+ ", password=" + "***" + ", passwordGenerated=" + passwordGenerated + ", role=" + role
				+ ", contact=" + contact + "]";
	}
}
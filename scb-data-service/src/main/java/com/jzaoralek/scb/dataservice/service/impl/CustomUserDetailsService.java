package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUserProfile;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUserProfileType;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO: (JZAORALE, OV), napojit na service do db
		SecuredUser user = findByUsernameFake(username);

		if (user == null) {
			throw new UsernameNotFoundException("Username not found: " + username);
		}

		return user;
	}

	private static List<GrantedAuthority> getGrantedAuthorities(Set<SecuredUserProfile> profiles){
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (SecuredUserProfile userProfile : profiles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getType()));
		}
		return authorities;
    }

	private static SecuredUser findByUsernameFake(String username) {
		SecuredUser ret = null;
		if ("a.kuder".equals(username)) {
			Set<SecuredUserProfile> profiles = new HashSet<SecuredUserProfile>();
			SecuredUserProfile profile = new SecuredUserProfile();
			profile.setType(SecuredUserProfileType.ADMIN.name());
			profiles.add(profile);
			ret = new SecuredUser(username, "popov", true, true, true, true, getGrantedAuthorities(profiles));
			ret.setUserProfiles(profiles);
			ScbUser scbUser = new ScbUser();
			scbUser.setUsername(username);
			Contact contact = new Contact();
			contact.setFirstname("Adrian");
			contact.setSurname("Kuder");
			scbUser.setContact(contact);
			ret.setScbUser(scbUser);
		}
		return ret;
	}
}
package com.jzaoralek.scb.dataservice.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUserProfile;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUserProfileType;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private ScbUserDao scbUserDa;

	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ScbUser scbUser = scbUserDa.getByUsername(username);
		if (scbUser == null) {
			throw new UsernameNotFoundException("Username not found: " + username);
		}

		return buildSecuredUser(scbUser);
	}

	private static List<GrantedAuthority> getGrantedAuthorities(Set<SecuredUserProfile> profiles){
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (SecuredUserProfile userProfile : profiles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getType()));
		}
		return authorities;
    }

	private SecuredUser buildSecuredUser(ScbUser scbUser) {
		Set<SecuredUserProfile> profiles = new HashSet<SecuredUserProfile>();
		SecuredUserProfile profile = new SecuredUserProfile();
		profile.setType(SecuredUserProfileType.fromScbUserRole(scbUser.getRole()).name());
		profiles.add(profile);
		SecuredUser ret = new SecuredUser(scbUser.getUsername(), scbUser.getPassword(), true, true, true, true, getGrantedAuthorities(profiles));
		ret.setUserProfiles(profiles);
		ret.setScbUser(scbUser);
		return ret;
	}

}
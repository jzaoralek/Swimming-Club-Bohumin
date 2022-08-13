package com.jzaoralek.scb.ui.common.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzaoralek.scb.dataservice.dao.ScbUserDao;
import com.jzaoralek.scb.dataservice.datasource.ClientDatabaseContextHolder;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUser;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUserProfile;
import com.jzaoralek.scb.dataservice.domain.security.SecuredUserProfileType;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	@Autowired
	private ScbUserDao scbUserDa;
	
	@Autowired
	private HttpServletRequest request;

	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOG.info("loadUserByUsername():: username: {}", username);
		
		// z cookie potreba ziskat customer context a nastavit do DatabaseContextHolder
		String custContextCookie = WebUtils.readCookieValue(WebConstants.CUST_URI_COOKIE, request);
		LOG.info("loadUserByUsername():: custContextCookie: {}", custContextCookie);
		ClientDatabaseContextHolder.set(custContextCookie);
		
		ScbUser scbUser = scbUserDa.getByUsername(username);
		if (scbUser == null) {
			throw new UsernameNotFoundException("Username not found: " + username);
		}

		return buildSecuredUser(scbUser);
	}

	private static List<GrantedAuthority> getGrantedAuthorities(Set<SecuredUserProfile> profiles){
        List<GrantedAuthority> authorities = new ArrayList<>();

		for (SecuredUserProfile userProfile : profiles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getType()));
		}
		return authorities;
    }

	private SecuredUser buildSecuredUser(ScbUser scbUser) {
		Set<SecuredUserProfile> profiles = new HashSet<>();
		SecuredUserProfile profile = new SecuredUserProfile();
		profile.setType(SecuredUserProfileType.fromScbUserRole(scbUser.getRole()).name());
		profiles.add(profile);
		SecuredUser ret = new SecuredUser(scbUser.getUsername(), scbUser.getPassword(), true, true, true, true, getGrantedAuthorities(profiles));
		ret.setUserProfiles(profiles);
		ret.setScbUser(scbUser);
		return ret;
	}

}
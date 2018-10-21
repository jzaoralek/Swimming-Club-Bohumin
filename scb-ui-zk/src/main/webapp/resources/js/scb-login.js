/**
 * 
 * @returns
 */
function validateForm() {
	var usernameValid = validateUsername();
	var passwordValid = validatePassword();

	if (!usernameValid || !passwordValid) {
		return false;
	}

	return true;
}

/**
 * 
 * @returns
 */
function validateUsername() {
	var username = document.forms["loginForm"]["username"].value;
	var usernameRequiredSpan = document.getElementById('usernameRequiredSpan');
	var scbInput = document.getElementById('input-username');
	usernameRequiredSpan.setAttribute("style", "visibility: hidden");
	scbInput.setAttribute("validation", "valid");

	var valid = true;
	if (username == null || username == "") {
		usernameRequiredSpan.setAttribute("style", "visibility: visible");
		scbInput.setAttribute("validation", "invalid");
		valid = false;
	}
	return valid;
}

/**
 * 
 * @returns
 */
function validatePassword() {
	var password = document.forms["loginForm"]["password"].value;
	var passwordRequiredSpan = document.getElementById('passwordRequiredSpan');
	var scbInput = document.getElementById('input-password');

	passwordRequiredSpan.setAttribute("style", "visibility: hidden");
	scbInput.setAttribute("validation", "valid");

	var valid = true;
	if (password == null || password == "") {
		passwordRequiredSpan.setAttribute("style", "visibility: visible");
		scbInput.setAttribute("validation", "invalid");
		valid = false;
	}
	return valid;
}



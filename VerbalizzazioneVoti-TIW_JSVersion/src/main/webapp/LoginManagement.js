/**
 * 
 */

(function() {

	document.getElementById("loginButton").addEventListener('click', (e1) => {
		e1.preventDefault();
		let form = e1.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", "CheckLogin", e1.target.closest("form"), function(e2) {
				if (e2.readyState === XMLHttpRequest.DONE) {
					let message = JSON.parse(e2.responseText);
					switch (e2.status) {
						case 200:
							//Saving the username of the user in order to visualize it later
							sessionStorage.setItem("username", message.username);
							window.location.href = message.role === "Lecturer" ? "HomeLecturer.html" : "HomeStudent.html";
							break;
						case 400:
							document.getElementById("errorMessage").textContent = message;
							break;
						case 401: // unauthorized
							document.getElementById("errorMessage").textContent = message;
							break;
						case 500: // server error
							document.getElementById("errorMessage").textContent = message;
							break;
					}
				}
			});
		} else {
			form.reportValidity();
		}
	});

})();
/**
 * 
 */

(function() {
	document.getElementById("id_alert").querySelector("a").addEventListener("click", function(e) {
		e.target.parentNode.classList.add("hidden");
	})
	
	document.getElementById("id_alert").classList.add("hidden");
	document.getElementById("loginButton").addEventListener('click', (e1) => {
		e1.preventDefault();
		let form = e1.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", "CheckLogin", e1.target.closest("form"), function(req) {
				if (req.readyState === XMLHttpRequest.DONE) {
					let message = req.responseText;
					switch (req.status) {
						case 200:
							let user = JSON.parse(req.responseText);
							//Saving the username of the user in order to visualize it later
							sessionStorage.setItem("user", JSON.stringify(user));
							window.location.href = user.role === "Lecturer" ? "HomeLecturer.html" : "HomeStudent.html";
							break;
						case 400:
							document.getElementById("id_alert").classList.remove("hidden");
							document.getElementById("id_alert").querySelector("label").textContent = message;
							break;
						case 401: // unauthorized
							document.getElementById("id_alert").classList.remove("hidden");
							document.getElementById("id_alert").querySelector("label").textContent = message;
							break;
						case 500: // server error
							document.getElementById("id_alert").classList.remove("hidden");
							document.getElementById("id_alert").querySelector("label").textContent = message;
							break;
					}
				}
			});
		} else {
			form.reportValidity();
		}
	});

})();
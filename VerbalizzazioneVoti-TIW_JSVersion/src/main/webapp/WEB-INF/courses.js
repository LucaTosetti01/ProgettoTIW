let coursesList = null;
let callsList = null;
let alertContainer = document.getElementById("id_alert");
let callContainer = document.getElementsById("id_callContainer")
let subscriptionsList = null;

window.addEventListener("load", function() {
	//Eventi bottoni

	coursesList = new CoursesList(
		document.getElementById("id_courseContainer"),
		document.getElementById("id_courseContainerBody")
	);

	callsList = new CallsList(
		document.getElementById("id_callContainer"),
		document.getElementById("id_callContainerBody")
	);

	//Initial display of courses (calls resetted)
	coursesList.show();
	callsList.reset();

	//Set invisible the calls container
	//N.B. non so se sia necessario dato che resetto già la lista di appelli
	callContainer.parentNode.style.visibility = "hidden";
}, false);


function CoursesList(_listContainer, _listContainerBody) {
	this.listContainer = _listContainer;
	this.listContainerBody = _listContainerBody;

	this.show = function() {
		let self = this;
		
		makeCall("GET", "GoToHomeLecturer", null, function(req) {
			if (req.readyState === 4) {
				if (req.status === 200) {
					let coursesToShow = JSON.parse(req.responseText);
					if (coursesToShow.length === 0) {
						alertContainer.textContent = "No courses found!";
						return;
					}
					self.update(coursesToShow);
				} else {
					alertContainer.textContent = req.responseText;
				}
			}
		});
	};

	this.update = function(coursesList) {
		let row;
		let idCell;
		let nameCell;
		let descriptionCell;
		let linkCell;
		//Button cell: let buttonCell;
		//			   let button;
		let anchor;
		this.listContainerBody.innerHTML = "";

		let self = this;
		coursesList.forEach(function(course) {
			row = document.createElement("tr");

			idCell = document.createElement("td");
			idCell.textContent = course.id;
			row.appendChild(idCell);

			nameCell = document.createElement("td");
			nameCell.textContent = course.name;
			row.appendChild(nameCell);

			descriptionCell = document.createElement("td");
			descriptionCell.textContent = course.name;
			row.appendChild(descriptionCell);

			linkCell = document.createElement("td");
			anchor = document.createElement("a");
			linkCell.appendChild(anchor);
			anchor.appendChild(document.createTextNode("Choose"));
			anchor.setAttribute("courseid", course.id);

			anchor.addEventListener("click", function(e) {
				e.preventDefault();
				alertContainer = "";

				//Change css class for the course's which has been chosen by the lecturer
				let selectedCourse = e.target.closest("table").querySelector("TR.selectedCourse");
				if (selectedCourse !== null) {
					selectedCourse.classList.remove("selectedCourse");
				}
				//With target = <a> tag, first parentNode = <td> tag, second parentNode = <tr> tag
				e.target.parentNode.parentNode.classList.add("selectedCourse");

				//Actual effect of the listener -> showing calls of the chosen course
				callsList.reset();
				if (callContainer.parentNode.style.visibility = "hidden") {
					callContainer.parentNode.style.visibility = "visible";
				}
				callsList.show(e.target.getAttribute("courseid"));
			}, false);

			anchor.href = "#";
			row.appendChild(linkCell);
			self.listContainerBody.appendChild(row);

			/*Button cell:
				buttonCell = document.createElement("td");
				button = document.createElement("input");
				button.setAttribute("type", "submit");
				button.setAttribute("value","Choose");
				button.setAttribute("courseid", course.id);
			*/
		});
	};
}

function CallsList(_listContainer, _listContainerBody) {
	this.listContainer = _listContainer;
	this.listContainerBody = _listContainerBody;
	
	this.reset = function () {
		this.listContainer.style.visibility = "hidden";
	};
	
	this.show = function (courseId) {
		let self = this;
		this.listContainer.querySelector("p").textContent = "List of calls associated with the course: " + courseId;
		let urlToCall = "GoToHomeLecturer?courseid=" + courseId;
		
		makeCall("GET",urlToCall, null, function(req) {
			if(req.readyState === 4) {
				if(req.status === 200) {
					let callsToShow = JSON.parse(req.responseText);
					if(callsToShow.length === 0) {
						alertContainer.textContent = "No calls found for the course: " + courseId;
						return;
					}
					self.update(callsToShow);
					self.listContainer.style.visibility = "visible";
				} else {
					alertContainer.textContent = req.responseText;
				}
			}
		});
	};
	
	this.update = function(callsList) {
		let row;
		let idCell;
		let dateCell;
		let timeCell;
		let linkCell;
		let anchor;
		this.listContainerBody.innerHTML = "";
		let self = this;
		
		callsList.forEach(function(call) {
			row = document.createElement("tr");
			
			idCell = document.createElement("td");
			idCell.textContent = call.id;
			row.appendChild(idCell);
			
			dateCell = document.createElement("td");
			dateCell.textContent = call.date;
			row.appendChild(dateCell);
			
			timeCell = document.createElement("td");
			timeCell.textContent = call.time;
			row.appendChild(timeCell);
			
			linkCell = document.createElement("td");
			anchor = document.createElement("a");
			linkCell.appendChild(anchor);
			anchor.appendChild(document.createTextNode("Choose"));
			anchor.setAttribute("callid", call.id);
			
			anchor.addEventListener("click", function(e) {
				e.preventDefault();
				alertContainer = "";
				
				let selectedCall = e.target.closest("table").querySelector("TR.selectedCall");
				if(selectedCall !== null) {
					selectedCall.classList.remove("selectedCall");
				}
				e.target.parentNode.parentNode.classList.add("selectedCall");
				
				subscriptionsList.reset();
				if(subscribersContainer.parentNode.style.visibility === "hidden") {
					subscribersContainer.parentNode.style.visibility = "visible";
				}
				subscriptionsList.show(e.target.getAttribute("callid"));
			}, false);
			
			anchor.href="#";
			row.appendChild(linkCell);
			self.listContainerBody.appendChild(row);
		});
	};
}
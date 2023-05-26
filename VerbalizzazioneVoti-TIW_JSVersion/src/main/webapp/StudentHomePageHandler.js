/**
 * 
 */

let coursesList, callsList, outcome;
let wizard = null;
let pageOrchestrator = new PageOrchestrator(); 	//Main controller

window.addEventListener("load", function() {
	//Eventi bottoni
	if (sessionStorage.getItem("username") == null) {
		window.location.href = "index.html";
	} else {
		pageOrchestrator.start();		//Initialize page components
		pageOrchestrator.refresh();		//Display initial page content
	}
}, false);

//Constructors of the view components

function WelcomeMessage(_username, messageContainer) {
	this.username = _username;
	this.show = function() {
		messageContainer.textContent = this.username;
	}
}

function CoursesList(_alert, _listContainer, _listContainerBody) {
	this.alert = _alert;
	this.listContainer = _listContainer;
	this.listContainerBody = _listContainerBody;

	this.reset = function() {
		this.listContainer.style.visibility = "hidden";
	}

	this.show = function(showDefaultFunction) {
		let self = this;
		makeCall("GET", "GetStudentCourses", null, function(req) {
			if (req.readyState === XMLHttpRequest.DONE) {
				var message = req.responseText;
				if (req.status === 200) {
					var coursesToShow = JSON.parse(req.responseText);
					if (coursesToShow.length == 0) {
						self.alert.textContent = "No courses found";
						return;
					}
					self.update(coursesToShow);
					if (showDefaultFunction) showDefaultFunction();

				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem("username");
				} else {
					self.alert.textContent = message;
				}
			}
		});
	};

	this.update = function(coursesList) {
		let row, idCell, nameCell, descriptionCell, linkCell;
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
			descriptionCell.textContent = course.description;
			row.appendChild(descriptionCell);

			linkCell = document.createElement("td");
			anchor = document.createElement("a");
			linkCell.appendChild(anchor);
			anchor.appendChild(document.createTextNode("Choose"));
			anchor.setAttribute("courseid", course.id);

			anchor.addEventListener("click", function(e) {
				e.preventDefault();

				//Change css class for the course's which has been chosen by the lecturer
				let selectedCourse = e.target.closest("table").querySelector("TR.selectedCourse");
				if (selectedCourse !== null) {
					selectedCourse.classList.remove("selectedCourse");
				}
				//With target = <a> tag, first parentNode = <td> tag, second parentNode = <tr> tag
				e.target.parentNode.parentNode.classList.add("selectedCourse");

				self.alert.textContent = "";
				outcome.reset();
				//Actual effect of the listener -> showing calls of the chosen course
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
		this.listContainer.style.visibility = "visible";
	};

	this.autoClick = function(courseId) {
		var e = new Event("click");
		//Selector needed to search a tag <a> that has an attribute callid = '${callId}'
		var selector = "a[courseid='" + courseId + "']";
		var anchorToClick = (courseId) ? document.querySelector(selector) : this.listContainerBody.querySelectorAll("a")[0];
		if (anchorToClick) {
			anchorToClick.dispatchEvent(e);
		}
	}

}

function CallsList(_alert, _listContainer, _listContainerBody) {
	this.alert = _alert;
	this.listContainer = _listContainer;
	this.listContainerBody = _listContainerBody;

	this.reset = function() {
		this.listContainer.style.visibility = "hidden";
	};

	this.show = function(courseId) {
		let self = this;
		this.listContainer.querySelector("p").textContent = "List of calls associated with the course: " + courseId;
		let urlToCall = "GetCoursesCalls?courseid=" + courseId;

		makeCall("GET", urlToCall, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let callsToShow = JSON.parse(req.responseText);
					if (callsToShow.length === 0) {
						self.alert.textContent = "No calls found for the course: " + courseId;
						//Since I didn't found calls i must reset the content of the 
						//subscribers component otherwhise i would have a error message
						//with the subiscribers of another course (if precedently I've selected
						//a course which had some calls)
						callsList.reset();
						outcome.reset();
						return;
					}
					//self.alert.textContent = "";
					self.update(callsToShow);
					self.listContainer.style.visibility = "visible";
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.textContent = message;
					callsList.reset();
					outcome.reset();
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
				if (selectedCall !== null) {
					selectedCall.classList.remove("selectedCall");
				}
				e.target.parentNode.parentNode.classList.add("selectedCall");

				outcome.show(e.target.getAttribute("callid"));
			}, false);

			anchor.href = "#";
			row.appendChild(linkCell);
			self.listContainerBody.appendChild(row);
		});
	};

	this.autoClick = function(callId) {
		var e = new Event("click");
		//Selector needed to search a tag <a> that has an attribute callid = '${callId}'
		var selector = "a[callid='" + callId + "']";
		var anchorToClick = (callId) ? this.listContainer.querySelector(selector) : this.listContainerBody.querySelectorAll("a")[0];
		if (anchorToClick) {
			anchorToClick.dispatchEvent(e);
		}
	}
}

function Outcome(_alert, _dataContainer, _dataContainerBody, _refuseForm, _closeButton) {
	this.alert = _alert;
	this.dataContainer = _dataContainer;
	this.dataContainerBody = _dataContainerBody;
	this.refuseForm = _refuseForm;
	this.closeButton = _closeButton;

	this.reset = function() {
		this.dataContainer.style.visibility = "hidden";
		this.dataContainerBody.style.visibility = "hidden";
		this.refuseForm.style.visibility = "hidden";
		this.refuseForm.querySelector("input[type='button']").style.visibility = "hidden";
		this.closeButton.style.visibility = "hidden";
		this.dataContainer.querySelector("p").style.visibility = "hidden";
	}

	this.registerEvent = function(orchestrator) {
		this.refuseForm.querySelector("input[type='button']").addEventListener("click", function(e) {
			var form = e.target.closest("form");
			let self = this;
			let currentCourse = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent
			let currentCall = form.querySelector("input[type = 'hidden']").value;
			makeCall("POST", "RefuseMark?callid=" + currentCall, form, function(req) {
				if (req.readyState === 4) {
					var message = req.responseText;
					if (req.status === 200) {
						orchestrator.refresh(currentCourse, currentCall);
					} else if (req.status === 403) {
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						self.alert.textContent = message;
					}
				}
			});
		});
	};

	this.show = function(callId) {
		let self = this;
		let urlToCall = "GetOutcome?callid=" + callId;

		makeCall("GET", urlToCall, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let outcomeToShow = JSON.parse(req.responseText);
					if (outcomeToShow.length === 0) {
						self.alert.textContent = "No calls found for the course: " + courseId;
						//Since I didn't found calls i must reset the content of the 
						//subscribers component otherwhise i would have a error message
						//with the subiscribers of another course (if precedently I've selected
						//a course which had some calls)
						this.reset();
						return;
					}
					self.alert.textContent = "";
					self.update(outcomeToShow);
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.textContent = message;
					outcome.reset();
				}
			}
		});
	}

	this.update = function(outcomeMap) {
		let row, self = this;
		let idStudentCell, idCourseCell, idCallCell;
		let surnameStudentCell, nameCourseCell, dateCallCell;
		let nameStudentCell, descriptionCourseCell, timeCallCell;
		let emailStudentCell, lecturerCourseCell, markCallCell;
		let degreeCourseCell, evaluationStateCell;

		this.dataContainerBody.innerHTML = "";

		let mark = outcomeMap["evaluation"].mark;
		let state = outcomeMap["evaluation"].state;

		if ((state === "Non inserito" || state === "Inserito")) {
			this.dataContainer.querySelector("p").textContent = "Mark not definined yet!";
			this.reset();
			this.dataContainer.querySelector("p").style.visibility = "visible";
		} else {
			this.refuseForm.querySelector("input[type = 'hidden']").value = outcomeMap["call"].id;

			let refuseButton = this.refuseForm.querySelector("input[type = 'button']");
			//Check if mark is between 18 and 30L, in that case set visible the refuse button
			if (((mark > 17 && mark < 31) || mark === "30L") && (state !== "Rifiutato" && state !== "Verbalizzato")) {
				refuseButton.style.visibility = "visible";
			} else {
				refuseButton.style.visibility = "hidden";
			}

			var temp = this.dataContainer.querySelector("input[name='closeWindowOutcome']").addEventListener("click", function(e) {
				outcome.reset();
			})


			this.dataContainer.querySelector("p").textContent = "This is your outcome of the selected call";
			row = document.createElement("tr");

			//First row
			row = document.createElement("tr");

			var temp = document.createElement("td");
			temp.textContent = "ID";
			row.appendChild(temp);
			idStudentCell = document.createElement("td");
			idStudentCell.textContent = outcomeMap["student"].id;
			row.appendChild(idStudentCell);

			var temp = document.createElement("td");
			temp.textContent = "ID";
			row.appendChild(temp);
			idCourseCell = document.createElement("td");
			idCourseCell.textContent = outcomeMap["course"].id;
			row.appendChild(idCourseCell);

			var temp = document.createElement("td");
			temp.textContent = "ID";
			row.appendChild(temp);
			idCallCell = document.createElement("td");
			idCallCell.textContent = outcomeMap["call"].id;
			row.appendChild(idCallCell);

			self.dataContainerBody.appendChild(row);
			//Second row
			row = document.createElement("tr");

			var temp = document.createElement("td");
			temp.textContent = "Surname";
			row.appendChild(temp);
			surnameStudentCell = document.createElement("td");
			surnameStudentCell.textContent = outcomeMap["student"].surname;
			row.appendChild(surnameStudentCell);

			var temp = document.createElement("td");
			temp.textContent = "Name";
			row.appendChild(temp);
			nameCourseCell = document.createElement("td");
			nameCourseCell.textContent = outcomeMap["course"].name;
			row.appendChild(nameCourseCell);

			var temp = document.createElement("td");
			temp.textContent = "Date";
			row.appendChild(temp);
			dateCallCell = document.createElement("td");
			dateCallCell.textContent = outcomeMap["call"].date;
			row.appendChild(dateCallCell);

			self.dataContainerBody.appendChild(row);
			//Third row
			row = document.createElement("tr");

			var temp = document.createElement("td");
			temp.textContent = "Name";
			row.appendChild(temp);
			nameStudentCell = document.createElement("td");
			nameStudentCell.textContent = outcomeMap["student"].id;
			row.appendChild(nameStudentCell);

			var temp = document.createElement("td");
			temp.textContent = "Description";
			row.appendChild(temp);
			descriptionCourseCell = document.createElement("td");
			descriptionCourseCell.textContent = outcomeMap["course"].description;
			row.appendChild(descriptionCourseCell);

			var temp = document.createElement("td");
			temp.textContent = "Time";
			row.appendChild(temp);
			timeCallCell = document.createElement("td");
			timeCallCell.textContent = outcomeMap["call"].time;
			row.appendChild(timeCallCell);

			self.dataContainerBody.appendChild(row);
			//Forth row
			row = document.createElement("tr");

			var temp = document.createElement("td");
			temp.textContent = "Email";
			row.appendChild(temp);
			emailStudentCell = document.createElement("td");
			emailStudentCell.textContent = outcomeMap["student"].email;
			row.appendChild(emailStudentCell);

			var temp = document.createElement("td");
			temp.textContent = "Lecturer";
			row.appendChild(temp);
			lecturerCourseCell = document.createElement("td");
			lecturerCourseCell.textContent = outcomeMap["lecturer"].surname + " " + outcomeMap["lecturer"].name;
			row.appendChild(lecturerCourseCell);

			var temp = document.createElement("td");
			temp.textContent = "Mark";
			row.appendChild(temp);
			markCallCell = document.createElement("td");
			markCallCell.textContent = outcomeMap["evaluation"].mark;
			row.appendChild(markCallCell);

			self.dataContainerBody.appendChild(row);
			//Fifth row
			row = document.createElement("tr");

			var temp = document.createElement("td");
			temp.textContent = "Degree course";
			row.appendChild(temp);
			degreeCourseCell = document.createElement("td");
			degreeCourseCell.textContent = outcomeMap["student"].degreeCourse.name;
			row.appendChild(degreeCourseCell);

			var temp = document.createElement("td");
			temp.textContent = "";
			row.appendChild(temp);
			idStudentCell = document.createElement("td");
			idStudentCell.textContent = "";
			row.appendChild(idStudentCell);

			var temp = document.createElement("td");
			temp.textContent = "Evaluation state";
			row.appendChild(temp);
			evaluationStateCell = document.createElement("td");
			evaluationStateCell.textContent = outcomeMap["evaluation"].state;
			row.appendChild(evaluationStateCell);

			self.dataContainerBody.appendChild(row);

			self.dataContainer.style.visibility = "visible";
			self.dataContainerBody.style.visibility = "visible";
			self.refuseForm.style.visibility = "visible";
			self.closeButton.style.visibility = "visible";
		}

	}
}

function PageOrchestrator() {
	var alertContainer = document.getElementById("id_alert");

	this.start = function() {
		personalMessage = new WelcomeMessage(sessionStorage.getItem("username"), document.getElementById("id_username"));
		personalMessage.show();

		coursesList = new CoursesList(
			alertContainer,
			document.getElementById("id_coursesContainer"),
			document.getElementById("id_coursesContainerBody")
		);

		callsList = new CallsList(
			alertContainer,
			document.getElementById("id_callsContainer"),
			document.getElementById("id_callsContainerBody")
		);

		outcome = new Outcome(
			alertContainer,
			document.getElementById("id_outcomeContainer"),
			document.getElementById("id_outcomeContainerBody"),
			document.getElementById("id_refuseForm"),
			document.getElementById("closeWindowOutcome")
		);
		outcome.registerEvent(this);


		document.querySelector("a[href='Logout']").addEventListener("click", function() {
			window.sessionStorage.removeItem("username");
		})
	};

	this.refresh = function(currentCourse, currentCall) {
		alertContainer.textContent = "";
		coursesList.reset();
		callsList.reset();
		outcome.reset();

		coursesList.show(function() {
			coursesList.autoClick(currentCourse);
		});
		
		if(currentCall != undefined) {
			callsList.autoClick(currentCall);
		}
	}
}
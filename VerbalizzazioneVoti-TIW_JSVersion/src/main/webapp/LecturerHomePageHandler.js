/**
 * 
 */

let coursesList, callsList, subscribersList;
let wizardSingleMark;
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

	this.show = function(showDefaultFunction, currentCall) {
		let self = this;
		makeCall("GET", "GetLecturersCourses", null, function(req) {
			if (req.readyState === XMLHttpRequest.DONE) {
				var message = req.responseText;
				if (req.status === 200) {
					var coursesToShow = JSON.parse(req.responseText);
					if (coursesToShow.length == 0) {
						self.alert.textContent = "No courses found";
						return;
					}
					self.update(coursesToShow, currentCall);
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

	this.update = function(coursesList, currentCall) {
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

				//Actual effect of the listener -> showing calls of the chosen course
				callsList.show(e.target.getAttribute("courseid"), function() {
					callsList.autoClick(currentCall);
				});
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

	this.show = function(courseId, showDefaultFunction) {
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
						//subscribersList.reset();
						return;
					}
					//self.alert.textContent = "";
					self.update(callsToShow);
					if (showDefaultFunction) showDefaultFunction();
					self.listContainer.style.visibility = "visible";
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.textContent = message;
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

				subscribersList.show(e.target.getAttribute("callid"));
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
		var anchorToClick = (callId) ? this.listContainer.querySelector(selector) : undefined;
		if (anchorToClick) {
			anchorToClick.dispatchEvent(e);
		}
	};
}

function SubscribersList(_alert, _listContainer, _listContainerBody) {
	this.alert = _alert;
	this.listContainer = _listContainer;
	this.listContainerBody = _listContainerBody;

	//Register listener only ONE time (if i putted this in show, i would have registered
	//N times the same event where N is the times that this.show() is invoked)
	var headers = Array.from(this.listContainerBody.closest("table").querySelectorAll("th"));
	headers.forEach(function(header) {
		header.addEventListener("click", function(e) {
			sortTable(header.id);
		})
	});
	headers[0].querySelector("span").innerHTML = " &#x25B2;";

	this.reset = function() {
		this.listContainer.style.visibility = "hidden";
		initializeSort();
	};

	this.show = function(callId) {
		//Needed for restarting the sort algoritm always from ID column
		//Registering sorting event to the headers


		let self = this;
		this.listContainer.querySelector("p").textContent = "List of students subscribed to the call: " + callId;
		let urlToCall = "GetSubscriptionToCall?callid=" + callId;

		makeCall("GET", urlToCall, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let subscribersToShow = JSON.parse(req.responseText);
					if (subscribersToShow.length === 0 || subscribersToShow.length == undefined) {
						self.alert.textContent = "No subscribers found for the call: " + callId;
						self.reset();
						return;
					}
					self.alert.textContent = "";
					self.update(subscribersToShow);
					self.listContainer.style.visibility = "visible";
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.textContent = message;
				}
			}
		});
	};

	this.update = function(subscribersList) {
		let row, idCell, surnameCell, nameCell, emailCell, degreeCourseCell, markCell, evaluationStateCell;
		this.listContainerBody.innerHTML = "";
		let self = this;

		subscribersList.forEach(function(studentWithEvaluation) {
			row = document.createElement("tr");

			idCell = document.createElement("td");
			idCell.textContent = studentWithEvaluation[0].id;
			row.appendChild(idCell);

			surnameCell = document.createElement("td");
			surnameCell.textContent = studentWithEvaluation[0].surname;
			row.appendChild(surnameCell);

			nameCell = document.createElement("td");
			nameCell.textContent = studentWithEvaluation[0].name;
			row.appendChild(nameCell);

			emailCell = document.createElement("td");
			emailCell.textContent = studentWithEvaluation[0].email;
			row.appendChild(emailCell);

			degreeCourseCell = document.createElement("td");
			degreeCourseCell.textContent = studentWithEvaluation[0].degreeCourse.name;
			row.appendChild(degreeCourseCell);

			markCell = document.createElement("td");
			markCell.textContent = studentWithEvaluation[1].mark;
			row.appendChild(markCell);

			evaluationStateCell = document.createElement("td");
			evaluationStateCell.textContent = studentWithEvaluation[1].state;
			row.appendChild(evaluationStateCell);

			linkCell = document.createElement("td");
			anchor = document.createElement("a");
			linkCell.appendChild(anchor);
			anchor.appendChild(document.createTextNode("Modify"));
			anchor.setAttribute("callid", studentWithEvaluation[1].call_id);
			anchor.setAttribute("studentid", studentWithEvaluation[0].id);

			anchor.addEventListener("click", function(e) {
				e.preventDefault();
				alertContainer = "";

				let selectedSubscriber = e.target.closest("table").querySelector("TR.selectedSubscriber");
				if (selectedSubscriber !== null) {
					selectedSubscriber.classList.remove("selectedSubscriber");
				}
				e.target.parentNode.parentNode.classList.add("selectedSubscriber");

				wizardSingleMark.show(e.target.getAttribute("studentid"), e.target.getAttribute("callid"));
			}, false);

			anchor.href = "#";
			row.appendChild(linkCell);
			self.listContainerBody.appendChild(row);
		});
	};
}

function WizardSingleMark(_alert, _wizardContainer, _wizard) {
	this.wizardContainer = _wizardContainer;
	this.wizard = _wizard;
	this.alert = _alert;

	this.reset = function() {
		this.wizardContainer.style.visibility = "hidden";
		this.wizard.style.visibility = "hidden";
	}

	this.show = function(studentId, callId) {
		let self = this;
		this.wizard.parentNode.querySelector("p").textContent = "Modify mark of student: " + studentId + ", call: " + callId;
		this.wizard.studentid.value = studentId;
		this.wizard.callid.value = callId;



		makeCall("GET", "GetMarkManagement?studentid=" + studentId + "&callid=" + callId, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let studentDataToShow = JSON.parse(req.responseText);
					if (studentDataToShow === "{}") {
						self.alert.textContent = message;
						self.reset();
						return;
					}
					self.alert.textContent = "";
					self.update(studentDataToShow);
					self.wizard.style.visibility = "visible";
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.textContent = message;
					self.reset();
				}
			}
		});
	};

	this.update = function(studentDataMap) {
		let studentId = studentDataMap["student"].id;
		let studentName = studentDataMap["student"].name;
		let studentSurname = studentDataMap["student"].surname;
		let studentEmail = studentDataMap["student"].email;
		let studentDegreeCourse = studentDataMap["student"].degreeCourse.name;
		let evaluationMark = studentDataMap["evaluation"].mark;
		this.wizard.querySelector("p > #id_IDForm").textContent = studentId;
		this.wizard.querySelector("p > #id_nameForm").textContent = studentName;
		this.wizard.querySelector("p > #id_surnameForm").textContent = studentSurname;
		this.wizard.querySelector("p > #id_emailForm").textContent = studentEmail;
		this.wizard.querySelector("p > #id_degreeCourseForm").textContent = studentDegreeCourse;

		let markSelect = this.wizard.querySelector("p > #id_markForm");
		let possibleMarkValues = ["", "Assente", "Rimandato", "Riprovato", "18", "19",
			"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L"];

		markSelect.innerHTML = "";
		possibleMarkValues.forEach(function(value) {
			let optionTag = document.createElement("option");
			if (evaluationMark === value) {
				optionTag.selected = true;
			}
			optionTag.textContent = value;
			markSelect.appendChild(optionTag);
		});


	}

	this.registerEvent = function(orchestrator) {
		let self = this;
		this.wizard.querySelector("input[type='button']").addEventListener("click", function(e) {
			let form = e.target.closest("form");
			let currentCourse = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent;
			let currentCall = form.querySelector("input[name='callid']").value;
			let currentStudent = form.querySelector("input[name='studentid']").value;

			if (self.wizard.checkValidity()) {
				makeCall("POST", "UpdateStudentMark", form, function(req) {
					if (req.readyState === 4) {
						var message = req.responseText;
						if (req.status === 200) {
							orchestrator.refresh(currentCourse, currentCall, currentStudent)
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
					} else {
						self.alert.textContent = message;
						self.reset();
					}
				});
			} else {
				self.wizard.reportValidity();
			}

		});
	};
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

		subscribersList = new SubscribersList(
			alertContainer,
			document.getElementById("id_subscribersContainer"),
			document.getElementById("id_subscribersContainerBody")
		);

		wizardSingleMark = new WizardSingleMark(
			alertContainer,
			document.getElementById("id_modifyContainer"),
			document.getElementById("id_modifyMarkForm")
		);
		wizardSingleMark.registerEvent(this);

		document.querySelector("a[href='Logout']").addEventListener("click", function() {
			window.sessionStorage.removeItem("username");
		})


	};

	this.refresh = function(currentCourse, currentCall) {
		alertContainer.textContent = "";
		coursesList.reset();
		callsList.reset();
		subscribersList.reset();
		wizardSingleMark.reset();

		//Refresh page
		coursesList.show(function() {
			coursesList.autoClick(currentCourse);
		}, currentCall);

	}
}

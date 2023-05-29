/**
 * 
 */

let coursesList, callsList, subscribersList;
let wizardSingleMark;
let buttonLine;
let modalBlock;
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

function SubscribersList(_alert, _listContainer, _listContainerBody, _buttonLine) {
	this.alert = _alert;
	this.listContainer = _listContainer;
	this.listContainerBody = _listContainerBody;
	this.buttonLine = _buttonLine;


	//Registering sorting event to the headers
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
		this.buttonLine.reset();
		wizardSingleMark.reset();
		//Needed for restarting the sort algoritm always from ID column
		initializeSort();
	};

	this.show = function(callId) {
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
					self.buttonLine.show();
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

function ButtonLine(_alert, _buttonsContainer) {
	this.alert = _alert;
	this.buttonsContainer = _buttonsContainer;

	let openModalButton = this.buttonsContainer.querySelector("input[name='openModalButton']");
	openModalButton.addEventListener("click", function(e) {
		modalBlock.show();
	});
	
	this.reset = function() {
		this.buttonsContainer.style.visibility = "hidden";
	}

	this.show = function() {
		let self = this;
		makeCall("GET", "VerbalizeStudentsMarks", null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let numberOfMarksVerbalizable = JSON.parse(req.responseText);
					if (isNaN(numberOfMarksVerbalizable)) {
						self.alert.textContent = "The value of verbalizable marks retrieved, is not a number";
						return;
					}
					makeCall("GET", "PublishStudentsMarks", null, function(req) {
						if (req.readyState === 4) {
							var message = req.responseText;
							if (req.status === 200) {
								let numberOfMarksPublishable = JSON.parse(req.responseText);
								if (isNaN(numberOfMarksPublishable)) {
									self.alert.textContent = "The value of verbalizable marks retrieved, is not a number";
									return;
								}
								self.update(numberOfMarksVerbalizable, numberOfMarksPublishable);
							} else if (req.status == 403) {
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
							} else {
								self.alert.textContent = message;
							}
						}
					});
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.textContent = message;
				}
			}
		});
	};

	this.update = function(numberOfVerbalizableMarks, numberOfMarksPublishable) {
		let verbalizeButton = this.buttonsContainer.querySelector("input[name='verbalizeButton']");
		let publishButton = this.buttonsContainer.querySelector("input[name='publishButton']");
		

		verbalizeButton.parentNode.querySelector("input[name='callid']").value = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent;
		publishButton.parentNode.querySelector("input[name='callid']").value = document.getElementById("id_callsContainerBody").querySelector("tr.selectedCall > td").textContent;
		

		if (numberOfVerbalizableMarks < 1) {
			verbalizeButton.disabled = true;
		} else {
			verbalizeButton.disabled = false;
		}
		if (numberOfMarksPublishable < 1) {
			publishButton.disabled = true;
		} else {
			publishButton.disabled = false;
		}

		this.buttonsContainer.style.visibility = "visible";
	}

	this.registerEvent = function(orchestrator) {
		let self = this;
		let publishButton = document.getElementById("id_publishForm").querySelector("input[name='publishButton']");

		publishButton.addEventListener('click', function(e) {
			let form = e.target.closest("form");
			let currentCourse = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent;
			let currentCall = document.getElementById("id_callsContainerBody").querySelector("tr.selectedCall > td").textContent;

			makeCall("POST", "PublishStudentsMarks", form, function(req) {
				if (req.readyState === 4) {
					var message = req.responseText;
					if (req.status === 200) {
						orchestrator.refresh(currentCourse, currentCall);
					} else if (req.status == 403) {
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');
					} else {
						self.alert.textContent = message;
					}
				}
			});
		});

		let verbalizeButton = document.getElementById("id_verbalizeForm").querySelector("input[name='verbalizeButton']");

		verbalizeButton.addEventListener('click', function(e) {
			let form = e.target.closest("form");
			let currentCourse = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent;
			let currentCall = document.getElementById("id_callsContainerBody").querySelector("tr.selectedCall > td").textContent;

			makeCall("POST", "VerbalizeStudentsMarks", form, function(req) {
				if (req.readyState === 4) {
					var message = req.responseText;
					if (req.status === 200) {
						orchestrator.refresh(currentCourse, currentCall);
					} else if (req.status == 403) {
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');

					} else {
						self.alert.textContent = message;
					}
				}
			});
		});
	};
}

function ModalBlock(_altert, _modalContainer, _overlay) {
	this.numberOfBoxChecked = 0;
	this.alert = _altert;
	this.modalContainer = _modalContainer;
	this.overlay = _overlay;
	
	this.modalContainer.querySelector("input[name='insertMark']").disabled = true;
	let self = this;
	this.modalContainer.querySelector(".btn-close").addEventListener("click", function(e) {
		self.reset();
	})

	this.reset = function() {
		this.modalContainer.classList.add("hidden");
		this.overlay.classList.add("hidden");
		this.modalContainer.querySelector("input[name='insertMark']").disabled = true;
		this.numberOfBoxChecked = 0;
	};

	this.show = function() {
		let self = this;
		let subscribersContainerBodyRows = Array.from(document.getElementById("id_subscribersContainerBody").querySelectorAll("tr"));
		let subscribersToShow = new Array();
		subscribersContainerBodyRows.forEach(function(row) {
			let subscribersData = Array.from(row.querySelectorAll("td"));
			if (subscribersData[6].textContent === "Non inserito") {
				let subscriber = {
					id: subscribersData[0].textContent,
					surname: subscribersData[1].textContent,
					name: subscribersData[2].textContent,
					email: subscribersData[3].textContent,
					degreeCourseName: subscribersData[4].textContent,
					mark: subscribersData[5].textContent,
					evaluationState: subscribersData[6].textContent,
				};
				subscribersToShow.push(subscriber);
			}
		});
		this.update(subscribersToShow)

	};

	this.update = function(subscribersList) {
		let row, checkBoxCell, checkBox, idCell, surnameCell, nameCell, emailCell, degreeCourseCell, markCell, evaluationStateCell;
		this.modalContainer.querySelector("#id_modalTableBody").innerHTML = "";
		let self = this;

		subscribersList.forEach(function(student) {
			row = document.createElement("tr");
			
			checkBoxCell = document.createElement("td");
			checkBox = document.createElement("input");
			checkBox.setAttribute("type","checkbox");
			checkBox.setAttribute("value",student.id);
			checkBox.setAttribute("name","studentids[]");
			checkBox.addEventListener("change", function(e) {
				if(e.target.checked == true) {
					self.numberOfBoxChecked++;
				} else {
					self.numberOfBoxChecked--;
				}
				
				let submitButton = self.modalContainer.querySelector("input[name='insertMark']");
				if(self.numberOfBoxChecked === 0) {
					submitButton.disabled = true;
				} else {
					submitButton.disabled = false;
				}
			});
			checkBoxCell.appendChild(checkBox);
			row.appendChild(checkBoxCell);

			idCell = document.createElement("td");
			idCell.textContent = student.id;
			row.appendChild(idCell);

			surnameCell = document.createElement("td");
			surnameCell.textContent = student.surname;
			row.appendChild(surnameCell);

			nameCell = document.createElement("td");
			nameCell.textContent = student.name;
			row.appendChild(nameCell);

			emailCell = document.createElement("td");
			emailCell.textContent = student.email;
			row.appendChild(emailCell);

			degreeCourseCell = document.createElement("td");
			degreeCourseCell.textContent = student.degreeCourseName;
			row.appendChild(degreeCourseCell);

			markCell = document.createElement("td");
			markCell.textContent = student.mark;
			row.appendChild(markCell);

			evaluationStateCell = document.createElement("td");
			evaluationStateCell.textContent = student.evaluationState;
			row.appendChild(evaluationStateCell);

			self.modalContainer.querySelector("#id_modalTableBody").appendChild(row);
		});
		
		let markSelect = this.modalContainer.querySelector("p > select");
		let possibleMarkValues = ["", "Assente", "Rimandato", "Riprovato", "18", "19",
			"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L"];

		markSelect.innerHTML = "";
		possibleMarkValues.forEach(function(value) {
			let optionTag = document.createElement("option");
			optionTag.textContent = value;
			markSelect.appendChild(optionTag);
		});
		
		this.modalContainer.classList.remove("hidden");
		this.overlay.classList.remove("hidden");
	};
	
	this.registerEvent = function(orchestrator) {
		let self = this;
		this.modalContainer.querySelector("input[type='insertMark']").addEventListener("click", function(e) {
			let form = e.target.closest("form");
			let currentCourse = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent;
			let currentCall = form.querySelector("input[name='callid']").value;

			if (form.checkValidity()) {
				makeCall("POST", "UpdateMultipleMarks", form, function(req) {
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

		buttonLine = new ButtonLine(
			alertContainer,
			document.getElementById("id_buttonContainer")
		)
		buttonLine.registerEvent(this);
		subscribersList = new SubscribersList(
			alertContainer,
			document.getElementById("id_subscribersContainer"),
			document.getElementById("id_subscribersContainerBody"),
			buttonLine
		);

		wizardSingleMark = new WizardSingleMark(
			alertContainer,
			document.getElementById("id_modifyContainer"),
			document.getElementById("id_modifyMarkForm")
		);
		wizardSingleMark.registerEvent(this);

		modalBlock = new ModalBlock(
			alertContainer,
			document.getElementById("id_modalContainer"),
			document.getElementById("id_blurryOverlay")
		);


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
		modalBlock.reset();

		//Refresh page
		coursesList.show(function() {
			coursesList.autoClick(currentCourse);
		}, currentCall);

	}
}

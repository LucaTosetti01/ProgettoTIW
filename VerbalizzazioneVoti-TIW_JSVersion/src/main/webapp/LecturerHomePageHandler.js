/**
 * 
 */

let coursesList, callsList, subscribersList;
let wizardSingleMark;
let buttonLine;
let modalMultipleMarks, modalVerbalRecap;
let alertHandler;
let pageOrchestrator = new PageOrchestrator(); 	//Main controller

window.addEventListener("load", function() {
	let userLogged = JSON.parse(sessionStorage.getItem("user"));
	if (userLogged == null || userLogged.role !== "Lecturer") {
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

function AlertHandler(_alertContainer) {
	this.alertContainer = _alertContainer;

	//I register the listener on the creation and not with a registerEvent function
	//because the closure of the alert doesn't require the refresh of all the page
	let self = this;
	this.alertContainer.querySelector("a").addEventListener("click", function(e) {
		e.preventDefault();
		self.reset();
	})

	this.reset = function() {
		this.alertContainer.classList.add("hidden");
	}

	this.update = function(message) {
		this.alertContainer.classList.remove("hidden");
		this.alertContainer.querySelector("label").textContent = message;
	}
}

function CoursesList(_alert, _listContainer, _listContainerBody) {
	this.alert = _alert;
	this.listContainer = _listContainer;
	this.listContainerBody = _listContainerBody;

	this.reset = function() {
		this.listContainer.classList.add("hidden");
	}

	this.show = function(showDefaultFunction, currentCall) {
		document.getElementById("id_subscribersModifyContainer").classList.add("hidden");
		let self = this;
		makeCall("GET", "GetLecturersCourses", null, function(req) {
			if (req.readyState === XMLHttpRequest.DONE) {
				var message = req.responseText;
				if (req.status === 200) {
					var coursesToShow = JSON.parse(req.responseText);
					if (coursesToShow.length == 0) {
						self.alert.update("No courses found");
						return;
					}
					self.update(coursesToShow, currentCall);
					if (showDefaultFunction) showDefaultFunction();

				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem("username");
				} else {
					self.alert.update(message);
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
			descriptionCell.classList.add("descriptionText");
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
		this.listContainer.classList.remove("hidden");
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
		this.listContainer.classList.add("hidden");
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
						self.alert.update("No calls found for the course: " + courseId);
						//Since I didn't found calls i must reset the content of the 
						//subscribers component otherwhise i would have a error message
						//with the subiscribers of another course (if precedently I've selected
						//a course which had some calls)
						subscribersList.reset();
						self.reset();
						return;
					}
					self.alert.reset();
					subscribersList.reset();
					self.update(callsToShow);
					if (showDefaultFunction) showDefaultFunction();
					self.listContainer.classList.remove("hidden");
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('user');
				} else {
					self.alert.update(message);
					self.reset();
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
		this.listContainer.classList.add("hidden");
		this.buttonLine.reset();
		wizardSingleMark.reset();
		//Needed for restarting the sort algoritm always from ID column
		initializeSort();
	};

	this.show = function(callId) {
		wizardSingleMark.reset();
		let self = this;
		this.listContainer.querySelector("p").textContent = "List of students subscribed to the call: " + callId;
		let urlToCall = "GetSubscriptionToCall?callid=" + callId;

		makeCall("GET", urlToCall, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let subscribersToShow = JSON.parse(req.responseText);
					if (subscribersToShow.length === 0 || subscribersToShow.length == undefined) {
						self.alert.update("No subscribers found for the call: " + callId);
						self.reset();
						return;
					}
					self.alert.reset();
					document.getElementById("id_subscribersModifyContainer").classList.remove("hidden");
					self.buttonLine.show(callId);
					self.update(subscribersToShow);
					self.listContainer.classList.remove("hidden");
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.update(message);
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

	let self = this;
	this.wizardContainer.querySelector("div > button").addEventListener("click", function(e) {
		self.reset();
	})
	this.reset = function() {
		this.wizardContainer.classList.add("hidden");
		//this.wizard.style.visibility = "hidden";
	}

	this.show = function(studentId, callId) {
		let self = this;
		this.wizard.parentNode.querySelector("h4").textContent = "Modify mark";
		this.wizard.parentNode.querySelector("p").textContent = "Student: " + studentId + "\u2003-\u2003Call: " + callId;
		this.wizard.studentid.value = studentId;
		this.wizard.callid.value = callId;



		makeCall("GET", "GetMarkManagement?studentid=" + studentId + "&callid=" + callId, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let studentDataToShow = JSON.parse(req.responseText);
					if (studentDataToShow === "{}") {
						self.alert.update(message);
						self.reset();
						return;
					}
					self.alert.reset();
					self.update(studentDataToShow);
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.update(message);
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
		this.wizard.querySelector("#id_IDForm").textContent = studentId;
		this.wizard.querySelector("#id_nameForm").textContent = studentName;
		this.wizard.querySelector("#id_surnameForm").textContent = studentSurname;
		this.wizard.querySelector("#id_emailForm").textContent = studentEmail;
		this.wizard.querySelector("#id_degreeCourseForm").textContent = studentDegreeCourse;

		let markSelect = this.wizard.querySelector("#id_markForm");
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

		this.wizardContainer.classList.remove("hidden");
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

	//I put this addEventListener here, because i need to add it only once, when i create
	//the ButtonLine object
	let openModalButton = this.buttonsContainer.querySelector("input[name='openModalButton']");
	openModalButton.addEventListener("click", function(e) {
		modalMultipleMarks.show();
	});
	
	this.reset = function() {
		this.buttonsContainer.style.visibility = "hidden";
	}

	this.show = function(callId) {
		let self = this;
		makeCall("GET", "VerbalizeStudentsMarks?callid=" + callId, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let numberOfMarksVerbalizable = JSON.parse(req.responseText);
					if (isNaN(numberOfMarksVerbalizable)) {
						self.alert.textContent = "The value of verbalizable marks retrieved, is not a number";
						return;
					}
					makeCall("GET", "PublishStudentsMarks?callid=" + callId, null, function(req) {
						if (req.readyState === 4) {
							var message = req.responseText;
							if (req.status === 200) {
								let numberOfMarksPublishable = JSON.parse(req.responseText);
								if (isNaN(numberOfMarksPublishable)) {
									self.alert.update("The value of verbalizable marks retrieved, is not a number");
									return;
								}
								self.update(numberOfMarksVerbalizable, numberOfMarksPublishable);
							} else if (req.status == 403) {
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
							} else {
								self.alert.update(message);
							}
						}
					});
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.update(message);
				}
			}
		});
	};

	this.update = function(numberOfVerbalizableMarks, numberOfMarksPublishable) {
		let verbalizeButton = this.buttonsContainer.querySelector("input[name='verbalizeButton']");
		let publishButton = this.buttonsContainer.querySelector("input[name='publishButton']");


		document.getElementById("id_buttonContainer").querySelector("input[name='callid']").value = document.getElementById("id_callsContainerBody").querySelector("tr.selectedCall > td").textContent;


		if (numberOfVerbalizableMarks < 1) {
			verbalizeButton.classList.add("disabled");
		} else {
			verbalizeButton.classList.remove("disabled");
		}
		if (numberOfMarksPublishable < 1) {
			publishButton.classList.add("disabled");
		} else {
			publishButton.classList.remove("disabled");
		}



		this.buttonsContainer.style.visibility = "visible";
	}

	this.registerEvent = function(orchestrator) {
		let self = this;
		let publishButton = this.buttonsContainer.querySelector("input[name='publishButton']");

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
						self.alert.update(message);
					}
				}
			});
		});

		let verbalizeButton = this.buttonsContainer.querySelector("input[name='verbalizeButton']");

		verbalizeButton.addEventListener('click', function(e) {
			let form = e.target.closest("form");
			let currentCourse = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent;
			let currentCall = document.getElementById("id_callsContainerBody").querySelector("tr.selectedCall > td").textContent;

			makeCall("POST", "VerbalizeStudentsMarks", form, function(req) {
				if (req.readyState === 4) {
					var message = req.responseText;
					if (req.status === 200) {
						let verbalId = JSON.parse(req.responseText);
						modalVerbalRecap.show(verbalId);
						orchestrator.refresh(currentCourse, currentCall);
					} else if (req.status == 403) {
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.removeItem('username');

					} else {
						self.alert.update(message);
					}
				}
			});
		});
	};
}

function ModalMultipleMarks(_alert, _modalContainer, _modalHeader, _modalBody, _backgroundBlurred) {
	this.numberOfMarkInserted = 0;
	this.alert = _alert;
	this.modalContainer = _modalContainer;
	this.modalHeader = _modalHeader;
	this.modalBody = _modalBody;
	this.backgroundBlur = _backgroundBlurred;

	let self = this;
	this.modalHeader.querySelector("button[type='button']").addEventListener("click", function(e) {
		self.reset();
	})

	this.reset = function() {
		this.modalContainer.style.display = "none";
		this.modalContainer.classList.remove("show");
		this.modalContainer.querySelector("input[name='insertMark']").classList.add("disabled");
		this.backgroundBlur.classList.add("hidden");
		this.numberOfBoxChecked = 0;
	};

	this.show = function() {
		let self = this;

		this.modalBody.querySelector("form").callid.value = document.getElementById("id_callsContainerBody").querySelector("tr.selectedCall > td").textContent;

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
		let row, selectCell, selectTag, idCell, surnameCell, nameCell, emailCell, degreeCourseCell, evaluationStateCell;
		this.modalContainer.querySelector(".modalTableBody").innerHTML = "";
		let self = this;

		let possibleMarkValues = ["", "Assente", "Rimandato", "Riprovato", "18", "19",
			"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L"];

		subscribersList.forEach(function(student) {
			row = document.createElement("tr");

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

			evaluationStateCell = document.createElement("td");
			evaluationStateCell.textContent = student.evaluationState;
			row.appendChild(evaluationStateCell);


			selectCell = document.createElement("td");
			selectTag = document.createElement("select");

			possibleMarkValues.forEach(function(value) {
				let optionTag = document.createElement("option");
				optionTag.textContent = value;
				selectTag.appendChild(optionTag);
			});

			selectTag.setAttribute("name", student.id);
			selectTag.addEventListener("focus", function(e) {
				e.target.setAttribute("oldValue", e.target.options[e.target.selectedIndex].textContent);
			})
			selectTag.addEventListener("change", function(e) {
				let selectedValue = e.target.options[e.target.selectedIndex].textContent;
				if (selectedValue !== "") {
					if (e.target.getAttribute("oldValue") === "") {
						self.numberOfMarkInserted++;
					}
				} else {
					self.numberOfMarkInserted--;
				}

				let submitButton = self.modalContainer.querySelector("input[name='insertMark']");
				if (self.numberOfMarkInserted === 0) {
					submitButton.classList.add("disabled");
				} else {
					submitButton.classList.remove("disabled");
				}
			});
			selectCell.appendChild(selectTag);
			row.appendChild(selectCell);

			self.modalContainer.querySelector(".modalTableBody").appendChild(row);
		});



		this.modalContainer.style.display = "block";
		this.modalContainer.classList.add("show");
		this.backgroundBlur.classList.remove("hidden");
	};

	this.registerEvent = function(orchestrator) {
		let self = this;
		this.modalContainer.querySelector("input[name='insertMark']").addEventListener("click", function(e) {
			let form = e.target.closest("form");
			let currentCourse = document.getElementById("id_coursesContainerBody").querySelector("tr.selectedCourse > td").textContent;
			let currentCall = form.querySelector("input[name='callid']").value;

			if (form.checkValidity()) {
				makeCall("POST", "UpdateMultipleMarks", form, function(req) {
					if (req.readyState === 4) {
						var message = req.responseText;
						if (req.status === 200) {
							orchestrator.refresh(currentCourse, currentCall)
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
					} else {
						self.alert.update(message);
						self.reset();
					}
				});
			} else {
				self.wizard.reportValidity();
			}

		});
	};
}

function ModalVerbalRecap(_alert, _modalContainer, _modalHeader, _modalBody, _backgroundBlurred) {
	this.alert = _alert;
	this.modalContainer = _modalContainer;
	this.modalHeader = _modalHeader;
	this.modalBody = _modalBody;
	this.backgroundBlur = _backgroundBlurred;

	let self = this;
	this.modalHeader.querySelector("button[type='button']").addEventListener("click", function(e) {
		self.reset();
	})

	this.reset = function() {
		//Used to hide the modal on the page
		this.modalContainer.style.display = "none";
		this.modalContainer.classList.remove("show");

		this.backgroundBlur.classList.add("hidden");
	};

	this.show = function(verbalId) {
		let self = this;

		makeCall("GET", "GetVerbalData?verbalid=" + verbalId, null, function(req) {
			if (req.readyState === 4) {
				var message = req.responseText;
				if (req.status === 200) {
					let verbalData = JSON.parse(req.responseText);
					self.alert.reset();
					self.update(verbalData);
				} else if (req.status == 403) {
					window.location.href = req.getResponseHeader("Location");
					window.sessionStorage.removeItem('username');
				} else {
					self.alert.update(message);
					self.reset();
				}
			}
		});
	};

	this.update = function(verbalDataMap) {
		let row, self = this;
		let idVerbalCell, idCourseCell, idCallCell;
		let CrationDateVerbalCell, nameCourseCell, dateCallCell;
		let CreationTimeVerbalCell, descriptionCourseCell, timeCallCell;
		let lecturerCourseCell;

		let idStudentCell, surnameStudentCell, nameStudentCell, degreeCourseCell;

		let dataRecapTableBody = this.modalBody.querySelector("table:nth-of-type(1)>tbody");
		let verbalSubscribersBody = this.modalBody.querySelector(".scroll-div-modal>table>tbody");

		dataRecapTableBody.innerHTML = "";
		verbalSubscribersBody.innerHTML = "";

		var temp = this.modalHeader.querySelector("button[type='button']").addEventListener("click", function(e) {
			self.reset();
		})


		//First row
		row = document.createElement("tr");

		var temp = document.createElement("th");
		temp.textContent = "ID";
		row.appendChild(temp);
		idVerbalCell = document.createElement("td");
		idVerbalCell.textContent = verbalDataMap["verbal"].id;
		row.appendChild(idVerbalCell);

		var temp = document.createElement("th");
		temp.textContent = "ID";
		row.appendChild(temp);
		idCourseCell = document.createElement("td");
		idCourseCell.textContent = verbalDataMap["course"].id;
		row.appendChild(idCourseCell);

		var temp = document.createElement("th");
		temp.textContent = "ID";
		row.appendChild(temp);
		idCallCell = document.createElement("td");
		idCallCell.textContent = verbalDataMap["call"].id;
		row.appendChild(idCallCell);

		dataRecapTableBody.appendChild(row);
		//Second row
		row = document.createElement("tr");

		var temp = document.createElement("th");
		temp.textContent = "Creation date";
		row.appendChild(temp);
		CrationDateVerbalCell = document.createElement("td");
		CrationDateVerbalCell.textContent = verbalDataMap["verbal"].creationDate;
		row.appendChild(CrationDateVerbalCell);

		var temp = document.createElement("th");
		temp.textContent = "Name";
		row.appendChild(temp);
		nameCourseCell = document.createElement("td");
		nameCourseCell.textContent = verbalDataMap["course"].name;
		row.appendChild(nameCourseCell);

		var temp = document.createElement("th");
		temp.textContent = "Date";
		row.appendChild(temp);
		dateCallCell = document.createElement("td");
		dateCallCell.textContent = verbalDataMap["call"].date;
		row.appendChild(dateCallCell);

		dataRecapTableBody.appendChild(row);
		//Third row
		row = document.createElement("tr");

		var temp = document.createElement("th");
		temp.textContent = "Creation time";
		row.appendChild(temp);
		CreationTimeVerbalCell = document.createElement("td");
		CreationTimeVerbalCell.textContent = verbalDataMap["verbal"].creationTime;
		row.appendChild(CreationTimeVerbalCell);

		var temp = document.createElement("th");
		temp.textContent = "Description";
		row.appendChild(temp);
		descriptionCourseCell = document.createElement("td");
		descriptionCourseCell.textContent = verbalDataMap["course"].description;
		row.appendChild(descriptionCourseCell);

		var temp = document.createElement("th");
		temp.textContent = "Time";
		row.appendChild(temp);
		timeCallCell = document.createElement("td");
		timeCallCell.textContent = verbalDataMap["call"].time;
		row.appendChild(timeCallCell);

		dataRecapTableBody.appendChild(row);
		//Forth row
		row = document.createElement("tr");

		var temp = document.createElement("td");
		row.appendChild(temp);
		var temp = document.createElement("td");
		row.appendChild(temp);

		var temp = document.createElement("th");
		temp.textContent = "Lecturer";
		row.appendChild(temp);
		lecturerCourseCell = document.createElement("td");
		lecturerCourseCell.textContent = verbalDataMap["lecturer"].surname + " " + verbalDataMap["lecturer"].name;
		row.appendChild(lecturerCourseCell);

		var temp = document.createElement("td");
		row.appendChild(temp);
		var temp = document.createElement("td");
		row.appendChild(temp);

		dataRecapTableBody.appendChild(row);



		let studentsData = verbalDataMap["students"];
		studentsData.forEach(function(student) {
			row = document.createElement("tr");

			idStudentCell = document.createElement("td");
			idStudentCell.textContent = student.id;
			row.appendChild(idStudentCell);

			surnameStudentCell = document.createElement("td");
			surnameStudentCell.textContent = student.surname;
			row.appendChild(surnameStudentCell);

			nameStudentCell = document.createElement("td");
			nameStudentCell.textContent = student.name;
			row.appendChild(nameStudentCell);

			emailStudentCell = document.createElement("td");
			emailStudentCell.textContent = student.email;
			row.appendChild(emailStudentCell);

			degreeCourseCell = document.createElement("td");
			degreeCourseCell.textContent = student.degreeCourse.name;
			row.appendChild(degreeCourseCell);

			verbalSubscribersBody.appendChild(row);
		});

		this.modalContainer.style.display = "block";
		this.modalContainer.classList.add("show");
		this.backgroundBlur.classList.remove("hidden");
	}

};


function PageOrchestrator() {
	this.start = function() {
		personalMessage = new WelcomeMessage(JSON.parse(sessionStorage.getItem("user")).username, document.getElementById("id_username"));
		personalMessage.show();

		var alertContainer = document.getElementById("id_alert");
		alertHandler = new AlertHandler(
			alertContainer
		)

		coursesList = new CoursesList(
			alertHandler,
			document.getElementById("id_coursesContainer"),
			document.getElementById("id_coursesContainerBody")
		);

		callsList = new CallsList(
			alertHandler,
			document.getElementById("id_callsContainer"),
			document.getElementById("id_callsContainerBody")
		);

		buttonLine = new ButtonLine(
			alertHandler,
			document.getElementById("id_buttonContainer")
		)
		buttonLine.registerEvent(this);

		subscribersList = new SubscribersList(
			alertHandler,
			document.getElementById("id_subscribersContainer"),
			document.getElementById("id_subscribersContainerBody"),
			buttonLine
		);

		wizardSingleMark = new WizardSingleMark(
			alertHandler,
			document.getElementById("id_modifyContainer"),
			document.getElementById("id_modifyMarkForm")
		);
		wizardSingleMark.registerEvent(this);

		modalMultipleMarks = new ModalMultipleMarks(
			alertHandler,
			document.getElementById("id_modalMultipleMarkContainer"),
			document.getElementById("id_modalMultipleMarkHeader"),
			document.getElementById("id_modalMultipleMarkBody"),
			document.getElementById("id_blurryOverlay")
		);
		modalMultipleMarks.registerEvent(this);

		modalVerbalRecap = new ModalVerbalRecap(
			alertHandler,
			document.getElementById("id_modalVerbalContainer"),
			document.getElementById("id_modalVerbalHeader"),
			document.getElementById("id_modalVerbalBody"),
			document.getElementById("id_blurryOverlay")
		)

		document.querySelector("a[href='Logout']").addEventListener("click", function() {
			window.sessionStorage.removeItem("user");
		})


	};

	this.refresh = function(currentCourse, currentCall) {
		alertHandler.reset();
		coursesList.reset();
		callsList.reset();
		subscribersList.reset();
		wizardSingleMark.reset();
		modalMultipleMarks.reset();
		modalVerbalRecap.reset();

		//Refresh page
		coursesList.show(function() {
			coursesList.autoClick(currentCourse);
		}, currentCall);

	}
}

let orderType;			
let prevOrderBy;

function initializeSort() {
	orderType = true;		//true -> ascending, false -> descending
	prevOrderBy = "id_ID";
	rowHeaders = document.getElementById("id_subscribersContainerBody").closest("table").querySelectorAll("th");
	resetArrows(rowHeaders);
	rowHeaders[0].querySelector("span").innerHTML = " &#x25B2;";
}

function getCellValue(tr, idx) {
	return tr.children[idx].textContent;
}

function resetArrows(rowHeaders){
  for (let j = 0; j < rowHeaders.length; j++ ){
    var toReset =   rowHeaders[j].querySelector("span");
    if(toReset !== null) {
		toReset.innerHTML = "";
	}
  }
  
}



function changeArrow(th){
  th.querySelector("span:first-child").innerHTML = orderType ? " &#x25B2;" : " &#x25BC;";
}

function createComparer(idx, orderType) {
  return function(rowa, rowb) {				//Per qualche motivo rowb è la seconda riga anche se è
  											//il primo parametro e viceversa per rowa
    // get values to compare at column idx
    // if order is ascending, compare 1st row to 2nd , otherwise 2nd to 1st
    var v1 = getCellValue(orderType ? rowa : rowb, idx),
    v2 = getCellValue(orderType ? rowb : rowa, idx);
    
    // If non numeric value
    if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
      return v1.toString().localeCompare(v2); // lexical comparison
    }

    // If numeric value
    return v1 - v2; // v1 greater than v2 --> true
  };
}

function sortTable(clicked_id) {
  var th = document.getElementById(clicked_id);
  var table = th.closest('table'); // get the closest table tag
  var rowHeaders = table.querySelectorAll('th');
  var columnIdx =  Array.from(rowHeaders).indexOf(th);
  
  // For every row in the table body
  // Use Array.from to build an array from table.querySelectorAll result
  // which is an Array Like Object (see DOM specifications)
  var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
  
  //  Toggle the criterion
  if(prevOrderBy === clicked_id) {
	  orderType =  !orderType;
  } else {
	  orderType = true;
  }
  prevOrderBy = clicked_id;
  
  // sort rows with the comparator function passing
  // index of column to compare, sort criterion asc or desc)
  rowsArray.sort(createComparer(columnIdx, orderType));

  
  // Change arrow colors
  resetArrows(rowHeaders);
  changeArrow(th);
  
  // Append the sorted rows in the table body
  for (var i = 0; i < rowsArray.length; i++) {
    table.querySelector('tbody').appendChild(rowsArray[i]);
    // https://developer.mozilla.org/en-US/docs/Web/API/Node/appendChild
  }
  //rowsArray.forEach(function(row){table.querySelector('tbody').appendChild(row);});
}

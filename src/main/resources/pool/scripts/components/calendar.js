function calAddEvent(calId, year, month, day) {
    eventParam( calId, new Array( year, month, day ) );
    fireWGTEvent( calId, '1' );
}

function calNextPage(calId) {
    fireWGTEvent( calId, '2' );
}

function calLastPage(calId) {
    fireWGTEvent( calId, '3' );
}

function calChangeEvent(calId, year, month, day, evId ){
	eventParam( calId, new Array( year, month, day, evId ) );
    fireWGTEvent( calId, '4' );
}

function calSelectDate(calId, year, month, day, cell) {
	var addDate;
	if (cell.className != null && cell.className.indexOf('highlighted') != -1) {
		cell.className = cell.className.replace(/highlighted/, '');
		addDate = false;
	}
	else {
		cell.className = cell.className + ' highlighted';
		addDate = true;
	}
	
	eventParam( calId, new Array( year, month, day, addDate ) );
    fireWGTEvent( calId, '5' );
}
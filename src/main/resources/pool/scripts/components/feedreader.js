function doListMouseOver(which, element) {
	switch (which) {
		case 1:
			element.style.backgroundColor = '#D7E9FA';
			break;
		case 2:
			element.style.backgroundColor = '#D7E9FA';
			break;
		case 3:
			element.style.backgroundImage = 'url(./images/components/feedreader/bg_new_hover.gif)';
			break;			
	}
}

function doListMouseOut(which, element) {
	switch (which) {
		case 1:
			element.style.backgroundColor = '#F5F7F9';
			break;
		case 2:
			element.style.backgroundColor = '#F5F7F9';
			break;
		case 3:
			element.style.backgroundImage = 'url(./images/components/feedreader/bg_new.gif)';
			break;
	}
}
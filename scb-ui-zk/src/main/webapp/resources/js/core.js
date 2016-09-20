/**
 * Registrace eventu pro ovladani kolecka mysi na strankovani
 * @author: Petr Bienert
 */
function gridMouseWheelEvent() {
	this.$supers('bind_', arguments);
	var node = this.$n();
	if (node.addEventListener) {
		node.addEventListener('DOMMouseScroll', this.proxy(this.onMouseWheel), false);
	}
	node.onmousewheel = this.proxy(this.onMouseWheel);
}

/**
 * Samotna funkce pro paging na otoceni kolecka mysi
 * @param event
 */
function gridMouseWheelEventFnc(event) {
	var delta = 0;
	if (!event) { /* For IE. */
		event = window.event;
	}
	if (event.wheelDelta) { /* IE/Opera. */
		delta = event.wheelDelta / 120;
		if (window.opera) {
			delta = -delta;
		}
	} else if (event.detail) {
		delta = -event.detail / 3;
	}
	if (delta) {
		console.log('delta = ' + delta);
		var currPage = this.getPaginal().getActivePage();
		var nextPage = Math.min(this.getPageCount() - 1, Math.max(0, currPage - delta));
		this.getPaginal().fire('onPaging', nextPage);
	}
	if (event.preventDefault) {
		event.preventDefault();
	}
	event.returnValue = false;
}

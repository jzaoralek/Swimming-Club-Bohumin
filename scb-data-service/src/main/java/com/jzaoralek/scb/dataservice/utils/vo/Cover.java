package com.jzaoralek.scb.dataservice.utils.vo;


/** Pomocna trida, jejimz ucelem je zabraneni vypisu hodnotu do logu pri pouziti toString (logovani
 * aspektem apod.), napr. pro hesla.
 */
public final class Cover<T> {

	private final T obj;

	public Cover(T obj) {
		this.obj = obj;
	}

	public T value() {
		return obj;
	}


	@Override
	public String toString() {
		return "Cover[***]"; //a nevypiseme radeji ani defaultni handle instance a spol.
	}

}

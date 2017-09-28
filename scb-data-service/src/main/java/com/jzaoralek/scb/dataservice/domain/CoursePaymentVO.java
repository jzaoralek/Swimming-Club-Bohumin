package com.jzaoralek.scb.dataservice.domain;

public class CoursePaymentVO {
	
	public enum CoursePaymentState {
		NO_PAYMENT,
		SEMESTER_FIRST_PARTLY,
		SEMESTER_FIRST_PAYED,
		SEMESTER_SECOND_PARTLY,
		SEMESTER_SECOND_PAYED,
		OVERPAYED;
	}
	
	private CoursePaymentState state;
	private long paymentSum;
	private final long priceFirstSemester;
	private final long priceSecondSemester;

	public CoursePaymentVO(long paymentSum, long priceFirstSemester, long priceSecondSemester) {
		super();
		this.paymentSum = paymentSum;
		this.priceFirstSemester = priceFirstSemester;
		this.priceSecondSemester = priceSecondSemester;
		this.state = buildCoursePaymentState(paymentSum, priceFirstSemester, priceSecondSemester);
	}
	
	public void rebuildCoursePaymentState(long paymentSum) {
		this.paymentSum = paymentSum;
		this.state = buildCoursePaymentState(paymentSum, this.priceFirstSemester, this.priceSecondSemester);
	}

	/**
	 * Return payment state by payments and price for first and second semester.
	 * 1. paymentSum == 0                                        NO_PAYMENT
	 * 2. paymentSum > 0 && < semester1                          SEMESTER_FIRST_PARTLY
	 * 3. paymentSum == semester1                                SEMESTER_FIRST_PAYED
	 * 4. paymentSum > semester1 && < (semester1 +semester2)	 SEMESTER_SECOND_PARTLY
	 * 5. paymentSum == (semester1 +semester2)                   SEMESTER_SECOND_PAYED
	 * 6. paymentSum > (semester1 +semester2)                    OVERPAYED
	 * @param paymentSum
	 * @param semesterFirstPrice
	 * @param semesterSecondPrice
	 * @return
	 */
	private CoursePaymentState buildCoursePaymentState(long paymentSum, long semesterFirstPrice, long semesterSecondPrice) {
		CoursePaymentState ret = CoursePaymentState.NO_PAYMENT;
		long totalPrice = semesterFirstPrice+semesterSecondPrice;
		
		if (paymentSum > 0 && paymentSum < semesterFirstPrice) {
			ret = CoursePaymentState.SEMESTER_FIRST_PARTLY;
		} else if (paymentSum == semesterFirstPrice) {
			ret = CoursePaymentState.SEMESTER_FIRST_PAYED;
		} else if (paymentSum > semesterFirstPrice && paymentSum < totalPrice) {
			ret = CoursePaymentState.SEMESTER_SECOND_PARTLY;
		} else if (paymentSum == totalPrice) {
			ret = CoursePaymentState.SEMESTER_SECOND_PAYED;
		} else if (paymentSum > totalPrice) {
			ret = CoursePaymentState.OVERPAYED;
		}
		
		return ret;
	}
	
	public CoursePaymentState getState() {
		return state;
	}

	public long getPaymentSum() {
		return paymentSum;
	}

	public long getPriceFirstSemester() {
		return priceFirstSemester;
	}

	public long getPriceSecondSemester() {
		return priceSecondSemester;
	}	
}
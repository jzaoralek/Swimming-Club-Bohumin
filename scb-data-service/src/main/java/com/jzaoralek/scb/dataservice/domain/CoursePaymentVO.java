package com.jzaoralek.scb.dataservice.domain;

public class CoursePaymentVO {
	
	public enum CoursePaymentState {
		NO_PAYMENT,
		PARTLY,
		PAYED,
		OVERPAYED;
	}
	
	private CoursePaymentState stateFirstSemester;
	private CoursePaymentState stateSecondSemester;
	private CoursePaymentState stateTotal;
	private long paymentSum;
	private long payedFirstSemester;
	private long payedSecondSemester;
	private final long priceFirstSemester;
	private final long priceSecondSemester;
	private long overpayed;

	public CoursePaymentVO(long paymentSum, long priceFirstSemester, long priceSecondSemester) {
		super();
		this.paymentSum = paymentSum;
		this.priceFirstSemester = priceFirstSemester;
		this.priceSecondSemester = priceSecondSemester;
		buildCoursePaymentState(paymentSum, priceFirstSemester, priceSecondSemester);
	}
	
	public void rebuildCoursePaymentState(long paymentSum) {
		this.paymentSum = paymentSum;
		buildCoursePaymentState(paymentSum, this.priceFirstSemester, this.priceSecondSemester);
	}

	/**
	 * Build payment state for first and second semester by payments and price for first and second semester.
	 * 1. paymentSum == 0                                        NO_PAYMENT
	 * 2. paymentSum > 0 && < semester1                          SEMESTER_FIRST_PARTLY
	 * 3. paymentSum == semester1                                SEMESTER_FIRST_PAYED
	 * 4. paymentSum > semester1 && < (semester1 +semester2)	 SEMESTER_SECOND_PARTLY
	 * 5. paymentSum == (semester1 +semester2)                   SEMESTER_SECOND_PAYED
	 * 6. paymentSum > (semester1 +semester2)                    OVERPAYED
	 * @param paymentSum
	 * @param semesterFirstPrice
	 * @param semesterSecondPrice
	 */
	private void buildCoursePaymentState(long paymentSum, long semesterFirstPrice, long semesterSecondPrice) {
		long totalPrice = semesterFirstPrice+semesterSecondPrice;
		
		this.stateFirstSemester = CoursePaymentState.NO_PAYMENT;
		this.stateSecondSemester = CoursePaymentState.NO_PAYMENT;
		this.stateTotal = CoursePaymentState.NO_PAYMENT;
		this.overpayed = 0;
		this.payedFirstSemester = 0;
		this.payedSecondSemester = 0;
		
		if (paymentSum > 0 && paymentSum < semesterFirstPrice) {
			this.stateFirstSemester = CoursePaymentState.PARTLY;
			this.payedFirstSemester = paymentSum;
			this.stateTotal = CoursePaymentState.PARTLY;
		} else if (paymentSum >= semesterFirstPrice) {
			this.stateFirstSemester = CoursePaymentState.PAYED;
			this.payedFirstSemester = semesterFirstPrice;
			this.stateTotal = CoursePaymentState.PARTLY;
		} 
		
		if (paymentSum > semesterFirstPrice && paymentSum < totalPrice) {
			this.stateSecondSemester = CoursePaymentState.PARTLY;
			this.payedSecondSemester = paymentSum - semesterFirstPrice;
			this.stateTotal = CoursePaymentState.PARTLY;
		} else if (paymentSum >= totalPrice) {
			this.stateSecondSemester = CoursePaymentState.PAYED;
			this.payedSecondSemester = priceSecondSemester;
			this.stateTotal = CoursePaymentState.PAYED;
		}
		
		if (paymentSum > totalPrice) {
			this.overpayed = paymentSum - totalPrice;
		}
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
	public CoursePaymentState getStateFirstSemester() {
		return stateFirstSemester;
	}
	public CoursePaymentState getStateSecondSemester() {
		return stateSecondSemester;
	}
	public long getOverpayed() {
		return overpayed;
	}
	public boolean isOverpayed() {
		return overpayed > 0;
	}
	public long getPayedFirstSemester() {
		return payedFirstSemester;
	}
	public CoursePaymentState getStateTotal() {
		return stateTotal;
	}
	public long getPayedSecondSemester() {
		return payedSecondSemester;
	}
	public long getTotalPrice() {
		return this.priceFirstSemester + this.priceSecondSemester;
	}
	
	public long getToPayFirstSemester() {
		return (this.priceFirstSemester - this.payedFirstSemester);
	}
	public long getToPaySecondSemester() {
		return (this.priceSecondSemester - this.payedSecondSemester);
	}
	public long getToPaySum() {
		return (getTotalPrice() - this.paymentSum);
	}
	
	public boolean isFirstSemesterPayed() {
		return this.stateFirstSemester == CoursePaymentState.PAYED;
	}
	
	public boolean isSecondSemesterPayed() {
		return this.stateSecondSemester == CoursePaymentState.PAYED;
	}
	
	public boolean isPayed() {
		return this.stateTotal == CoursePaymentState.PAYED;
	}
}
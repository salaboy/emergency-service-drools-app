package com.wordpress.salaboy.hospital.service;

public class AppointmentService {

	public boolean scheduleAppointment(String patient) {
		System.out.println("Scheduling appointment");
		return true;
	}
	
	public boolean rescheduleAppointment(String patient) {
		System.out.println("RE-Scheduling appointment");
		return true;
	}
	
	public boolean followUp(String patient) {
		System.out.println("Follow up");
		return true;
	}
}

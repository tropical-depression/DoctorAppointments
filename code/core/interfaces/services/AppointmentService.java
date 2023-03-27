package core.interfaces.services;

import java.util.UUID;

import core.exceptions.ResourceNotFoundException;

public interface AppointmentService {
	public void publishAppointmentNotes(UUID appointmentId) throws ResourceNotFoundException;
}

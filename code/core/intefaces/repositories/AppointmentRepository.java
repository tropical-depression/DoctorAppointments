package core.intefaces.repositories;

import java.util.UUID;

import core.models.AppointmentNotes;

public interface AppointmentRepository {
	public AppointmentNotes getById(UUID id);
	public void update(AppointmentNotes appointmentNotes);
}

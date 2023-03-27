package core.intefaces.repositories;

import java.util.UUID;

import core.models.FitNote;

public interface FitNoteRepository {
	public FitNote getByAppointmentId(UUID appointmentId);
}

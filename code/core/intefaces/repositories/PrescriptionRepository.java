package core.intefaces.repositories;

import java.util.UUID;

import core.models.Prescription;

public interface PrescriptionRepository {
	Prescription getByAppointmentId(UUID appointmentId);
}

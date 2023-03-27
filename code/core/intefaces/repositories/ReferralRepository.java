package core.intefaces.repositories;

import java.util.UUID;

import core.models.Referral;

public interface ReferralRepository {
	public Referral getByAppointmentId(UUID appointmentId);
}

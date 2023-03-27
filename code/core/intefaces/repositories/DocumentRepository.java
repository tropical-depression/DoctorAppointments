package core.intefaces.repositories;

import core.models.*;

public interface DocumentRepository {
	public Document uploadAppointmentNotesPdf(AppointmentNotes appointmentNotes, String pdfTemplatePath);
	public Document uploadPrescriptionPdf(Prescription prescription, String pdfTemplatePath);
	public Document uploadReferralPdf(Referral referral, String pdfTemplatePath);
	public Document uploadFitNotePdf(FitNote fitNote, String pdfTemplatePath);
	public Document uploadAppointmentInvoicePdf(AppointmentInvoice appointmentInvoice, String pdfTemplatePath);
}

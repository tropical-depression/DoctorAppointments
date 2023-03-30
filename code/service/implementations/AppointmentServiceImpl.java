package service.implementations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import core.exceptions.ResourceNotFoundException;
import core.intefaces.repositories.*;
import core.interfaces.services.*;
import core.models.*;

public class AppointmentServiceImpl implements AppointmentService {

	private AppointmentRepository appointmentRepository;
	private PrescriptionRepository prescriptionRepository;
	private ReferralRepository referralRepository;
	private FitNoteRepository fitNoteRepository;
	private DocumentRepository documentRepository;

	public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
			PrescriptionRepository prescriptionRepository, ReferralRepository referralRepository,
			FitNoteRepository fitNoteRepository, DocumentRepository documentRepository) {
		super();
		this.appointmentRepository = appointmentRepository;
		this.prescriptionRepository = prescriptionRepository;
		this.referralRepository = referralRepository;
		this.fitNoteRepository = fitNoteRepository;
		this.documentRepository = documentRepository;
	}

	@Override
	public void publishAppointmentNotes(UUID id) throws ResourceNotFoundException {
		AppointmentNotes an = appointmentRepository.getById(id);

		if (an != null) {
			Prescription p = prescriptionRepository.getByAppointmentId(id);
			Referral r = referralRepository.getByAppointmentId(id);
			FitNote fn = fitNoteRepository.getByAppointmentId(id);

			Date appointmentTime = an.getAppointmentTime();
			String patientName = an.getPatientName();
			String deliveryAddress = r != null && r.getType() == ReferralType.IN_PERSON_GP_APPOINTMENT
					&& r.isCoveredByInsurance() ? r.getPracticeAddress() : an.getPatientAddress();

			BigDecimal price = new BigDecimal(50.0);

			if (p != null) {
				price = price.add(Prescription.STANDARD_PRESCRIPTION_PRICE);
			}

			if (r != null && !r.isCoveredByInsurance() && r.getPrice().compareTo(BigDecimal.ZERO) > 0) {
				price = price.add(r.getPrice());
			}

			AppointmentInvoice ai = new AppointmentInvoice(id, price, appointmentTime, patientName, deliveryAddress);

			ArrayList<Document> list = new ArrayList<Document>();

			if (an != null) {
				Document appointmentNotesPdf = documentRepository.uploadAppointmentNotesPdf(an,
						"/pdfTemplates/appointmentNotes.pdftemplate");
				list.add(appointmentNotesPdf);
			}

			if (p != null) {
				String prescriptionPdfTemplatePath = "";
				switch (p.getType()) {
				case ELECTRONIC:
					prescriptionPdfTemplatePath = "/pdfTemplates/prescription/electronic.pdftemplate";
					break;
				case FAX:
					prescriptionPdfTemplatePath = "/pdfTemplates/prescription/fax.pdftemplate";
					break;
				case DELIVERY:
					prescriptionPdfTemplatePath = "/pdfTemplates/prescription/delivery.pdftemplate";
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + p.getType());
				}
				Document prescriptionPdf = documentRepository.uploadPrescriptionPdf(p, prescriptionPdfTemplatePath);
				list.add(prescriptionPdf);
			}

			if (r != null) {
				String referralPdfTemplatePath = "";
				switch (r.getType()) {
				case IN_PERSON_GP_APPOINTMENT:
					referralPdfTemplatePath = "/pdfTemplates/referrals/inPerson";
					break;
				case SPECIALIST_APPOINTMENT:
					referralPdfTemplatePath = "/pdfTemplates/referrals/specialist";
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + r.getType());
				}
				Document referralPdf = documentRepository.uploadReferralPdf(r, referralPdfTemplatePath);
				list.add(referralPdf);
			}

			if (fn != null) {
				Document fitNotePdf = documentRepository.uploadFitNotePdf(fn, "pdfTemplates/fitNote.pdftemplate");
				list.add(fitNotePdf);
			}

			Document appointmentInvoiceDocument = documentRepository.uploadAppointmentInvoicePdf(ai,
					AppointmentInvoice.PDF_TEMPLATE_PATH);
			list.add(appointmentInvoiceDocument);

			an.setAppointmentDocuments(list);

			appointmentRepository.update(an);
		}
		else {
			throw new ResourceNotFoundException("Appointment with provided id has not been found");
		}
	}
}

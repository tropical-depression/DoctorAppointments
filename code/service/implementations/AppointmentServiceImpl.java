package service.implementations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import core.exceptions.ResourceNotFoundException;
import core.intefaces.repositories.*;
import core.interfaces.services.*;
import core.models.*;

public class AppointmentServiceImpl implements AppointmentService {

	private final double DEFAULT_STARTING_PRICE = 50.0;

	private final String PDF_TEMPLATE_PATH = "/pdfTemplates";
	private final String prescription_TEMPLATE_PATH = "/prescription";
	private final String REFERRAL_TEMPLATE_PATH = "/referrals";
	private final String PDF_TEMPLATE_PATH_FOR_APPOINTMENT_NOTES = PDF_TEMPLATE_PATH + "/appointmentNotes.pdftemplate";
	private final String PDF_DELIVERY_TEMPLATE = PDF_TEMPLATE_PATH + prescription_TEMPLATE_PATH + "/delivery.pdftemplate";
	private final String PDF_FAX_TEMPLATE = PDF_TEMPLATE_PATH + prescription_TEMPLATE_PATH + "/fax.pdftemplate";
	private final String PDF_ELECTRONIC_prescription_TEMPLATE = PDF_TEMPLATE_PATH + prescription_TEMPLATE_PATH + "/electronic.pdftemplate";
	private final String PDF_IN_PERSON_REFERRAL_TEMPLATE = PDF_TEMPLATE_PATH + REFERRAL_TEMPLATE_PATH + "/inPerson";
	private final String PDF_SPECIALIST_REFERRAL_TEMPLATE = PDF_TEMPLATE_PATH + REFERRAL_TEMPLATE_PATH + "/specialist";
	private final String PDF_FIT_NOTE_TEMPLATE = PDF_TEMPLATE_PATH + "/fitNote.pdftemplate";

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
		AppointmentNotes appointmentNotes = appointmentRepository.getById(id);

		if (appointmentNotes != null) {
			Prescription prescription = prescriptionRepository.getByAppointmentId(id);
			Referral referral = referralRepository.getByAppointmentId(id);
			FitNote fitNote = fitNoteRepository.getByAppointmentId(id);

			// This part of code collects data needed for creating appointment invoice
			AppointmentInvoice appointmentInvoice = createAppointmentInvoice(appointmentNotes, referral, prescription, id);

			// Uploading the appointment documents
			ArrayList<Document> documentList = new ArrayList<Document>();

			// upload documents for appointment
			if (appointmentNotes != null) {
				Document appointmentNotesPdf = documentRepository.uploadAppointmentNotesPdf(
					appointmentNotes,
					PDF_TEMPLATE_PATH_FOR_APPOINTMENT_NOTES
				);
				documentList.add(appointmentNotesPdf);
			}

			// upload documents for prescription
			if (prescription != null) {
				String prescriptionPdfTemplatePath = "";
				switch (prescription.getType()) {
					case ELECTRONIC:
						prescriptionPdfTemplatePath = PDF_ELECTRONIC_prescription_TEMPLATE;
						break;
					case FAX:
						prescriptionPdfTemplatePath = PDF_FAX_TEMPLATE;
						break;
					case DELIVERY:
						prescriptionPdfTemplatePath = PDF_DELIVERY_TEMPLATE;
						break;
					default:
						throw new IllegalArgumentException("Unexpected value: " + prescription.getType());
				}
				Document prescriptionPdf = documentRepository.uploadPrescriptionPdf(prescription, prescriptionPdfTemplatePath);
				documentList.add(prescriptionPdf);
			}

			// upload documents for referral
			if (referral != null) {
				String referralPdfTemplatePath = "";
				switch (referral.getType()) {
					case IN_PERSON_GP_APPOINTMENT:
						referralPdfTemplatePath = PDF_IN_PERSON_REFERRAL_TEMPLATE;
						break;
					case SPECIALIST_APPOINTMENT:
						referralPdfTemplatePath = PDF_SPECIALIST_REFERRAL_TEMPLATE;
						break;
					default:
						throw new IllegalArgumentException("Unexpected value: " + referral.getType());
				}
				Document referralPdf = documentRepository.uploadReferralPdf(referral, referralPdfTemplatePath);
				documentList.add(referralPdf);
			}

			// upload documents for fitNote
			if (fitNote != null) {
				Document fitNotePdf = documentRepository.uploadFitNotePdf(fitNote, PDF_FIT_NOTE_TEMPLATE);
				documentList.add(fitNotePdf);
			}

			Document appointmentInvoiceDocument = documentRepository.uploadAppointmentInvoicePdf(
				appointmentInvoice,
				AppointmentInvoice.PDF_TEMPLATE_PATH
			);
			documentList.add(appointmentInvoiceDocument);

			appointmentNotes.setAppointmentDocuments(documentList);

			appointmentRepository.update(appointmentNotes);
		}
		else {
			throw new ResourceNotFoundException("Appointment with provided id has not been found");
		}
	}

	private String checkDeliveryAddress(Referral referral, AppointmentNotes appointmentNotes) {
		if(
			referral != null &&
			referral.getType() == ReferralType.IN_PERSON_GP_APPOINTMENT &&
			referral.isCoveredByInsurance()
		) {
			return referral.getPracticeAddress();
		}
		return appointmentNotes.getPatientAddress();
	}

	private BigDecimal calculatePrice(Prescription prescription, Referral referral) {
		BigDecimal price = new BigDecimal(DEFAULT_STARTING_PRICE);

		if (prescription != null) {
			price = price.add(Prescription.STANDARD_PRESCRIPTION_PRICE);
		}

		if (referral != null && !referral.isCoveredByInsurance() && referral.getPrice().compareTo(BigDecimal.ZERO) > 0) { // check if referral it is covered by insurance
			price = price.add(referral.getPrice());
		}

		return price;
	}

	private AppointmentInvoice createAppointmentInvoice(AppointmentNotes appointmentNotes, Referral referral, Prescription prescription, UUID id) {
		Date appointmentTime = appointmentNotes.getAppointmentTime();
		String patientName = appointmentNotes.getPatientName();
		String deliveryAddress = checkDeliveryAddress(referral, appointmentNotes);
		BigDecimal price = calculatePrice(prescription, referral);

		return new AppointmentInvoice(id, price, appointmentTime, patientName, deliveryAddress);
	}
}

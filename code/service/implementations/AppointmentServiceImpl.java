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
	
	private final double BASE_APPOINTMENT_PRICE = 50.0;
	
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
	public void publishAppointmentNotes(UUID appointmentId) throws ResourceNotFoundException {
		
	    BigDecimal appointmentPrice = new BigDecimal(BASE_APPOINTMENT_PRICE);
		AppointmentNotes appointmentNotes = appointmentRepository.getById(appointmentId);
		
		if(appointmentNotes == null) {
			throw new ResourceNotFoundException("Appointment with provided id has not been found");
		}
		
		Prescription prescription = prescriptionRepository.getByAppointmentId(appointmentId);
		Referral referral = referralRepository.getByAppointmentId(appointmentId);
		FitNote fitNote = fitNoteRepository.getByAppointmentId(appointmentId);
		
		AppointmentInvoice invoice = generateAppointmentInvoice(appointmentNotes, prescription, referral);
		
		List<Document> appointmentDocuments = uploadAppointmentDocuments(appointmentNotes, prescription, referral, fitNote, invoice);
		
		appointmentNotes.setAppointmentDocuments(appointmentDocuments);
		
		appointmentRepository.update(appointmentNotes);
	}
	
	private AppointmentInvoice generateAppointmentInvoice(AppointmentNotes appointmentNotes, Prescription prescription,
			Referral referral) {
		
		UUID appointmentId = appointmentNotes.getAppointmentId();
		Date appointmentTime = appointmentNotes.getAppointmentTime();
		String patientName = appointmentNotes.getPatientName();
		String deliveryAddress = getInvoiceDeliveryAddress(appointmentNotes, referral);
		

		BigDecimal price = new BigDecimal(BASE_APPOINTMENT_PRICE);
		
		if(prescription != null) {
			price = price.add(Prescription.STANDARD_PRESCRIPTION_PRICE);
		}
		
		if(isReferralPayedByInsurance(referral)) {
			price = price.add(referral.getPrice());
		}
		
		return new AppointmentInvoice(appointmentId, price, appointmentTime, patientName, deliveryAddress);
	}

	private boolean isReferralPayedByInsurance(Referral referral) {
		
		if(referral == null) {
			return false;
		}
		
		if(referral.isCoveredByInsurance()) {
			return false;
		}
		
		if(referral.getPrice().compareTo(BigDecimal.ZERO) <= 0 ) {
			return false;
		}
		
		return true;
	}

	private String getInvoiceDeliveryAddress(AppointmentNotes appointmentNotes, Referral referral) {
		return referral != null 
				&& referral.getType() == ReferralType.IN_PERSON_GP_APPOINTMENT
				&& referral.isCoveredByInsurance()
					? referral.getPracticeAddress()
					: appointmentNotes.getPatientAddress();
	}

	public List<Document> uploadAppointmentDocuments(AppointmentNotes appointmentNotes, Prescription prescription, Referral referral,
			FitNote fitNote, AppointmentInvoice appointmentInvoice) {
		
	    ArrayList<Document> appointmentDocuments = new ArrayList<Document>();
	    
	    if(appointmentNotes != null) {
	    	Document appointmentNotesPdf = documentRepository.uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
	    	appointmentDocuments.add(appointmentNotesPdf);
	    }
	    
	    if(prescription != null) {
	    	String prescriptionPdfTemplatePath = prescription.getPdfTemplatePath();
	    	Document prescriptionPdf = documentRepository.uploadPrescriptionPdf(prescription, prescriptionPdfTemplatePath);
	    	appointmentDocuments.add(prescriptionPdf);
	    }
	    
	    if(referral != null) {
	    	String referralPdfTemplatePath = referral.getPdfTemplatePath();
	    	Document referralPdf = documentRepository.uploadReferralPdf(referral, referralPdfTemplatePath);
	    	appointmentDocuments.add(referralPdf);
	    }
	    
	    if(fitNote != null) {
	    	Document fitNotePdf = documentRepository.uploadFitNotePdf(fitNote, FitNote.PDF_TEMPLATE_PATH);
	    	appointmentDocuments.add(fitNotePdf);
	    }
	    
		Document appointmentInvoiceDocument = documentRepository.uploadAppointmentInvoicePdf(appointmentInvoice, AppointmentInvoice.PDF_TEMPLATE_PATH);
	    appointmentDocuments.add(appointmentInvoiceDocument);
		
	    return appointmentDocuments;
	}
}

package service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import core.exceptions.ResourceNotFoundException;
import core.intefaces.repositories.AppointmentRepository;
import core.intefaces.repositories.DocumentRepository;
import core.intefaces.repositories.FitNoteRepository;
import core.intefaces.repositories.PrescriptionRepository;
import core.intefaces.repositories.ReferralRepository;
import core.interfaces.services.AppointmentService;
import core.models.AppointmentNotes;
import core.models.Document;
import core.models.FitNote;
import core.models.Prescription;
import core.models.PrescriptionType;
import core.models.Referral;
import core.models.ReferralType;
import service.implementations.AppointmentServiceImpl;

public class AppointmentServiceImplTest {

    private AppointmentRepository appointmentRepositoryMock;
    private PrescriptionRepository prescriptionRepositoryMock;
    private ReferralRepository referralRepositoryMock;
    private FitNoteRepository fitNoteRepositoryMock;
    private DocumentRepository documentRepositoryMock;

    private AppointmentService appointmentService;

    @BeforeEach
    public void setUp() {
        appointmentRepositoryMock = mock(AppointmentRepository.class);
        prescriptionRepositoryMock = mock(PrescriptionRepository.class);
        referralRepositoryMock = mock(ReferralRepository.class);
        fitNoteRepositoryMock = mock(FitNoteRepository.class);
        documentRepositoryMock = mock(DocumentRepository.class);

        appointmentService = new AppointmentServiceImpl(
            appointmentRepositoryMock, 
            prescriptionRepositoryMock, 
            referralRepositoryMock, 
            fitNoteRepositoryMock, 
            documentRepositoryMock
        );
    }
    
    @Test
    public void publishAppointmentNotes_AppointmentNotesDoesNotExist_ResourceNotFoundExceptionThrown() {
    	UUID appointmentId = UUID.randomUUID();
    	ResourceNotFoundException thrownException = assertThrows(
    				ResourceNotFoundException.class,
    	           () -> appointmentService.publishAppointmentNotes(appointmentId),
    	           "Expected publishAppointmentNotes() to throw ResourceNotFoundException, but it didn't"
    	    );
    	
    	assertTrue(thrownException != null);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesAndNoneOtherDocuments_AppointmentInvoiceGenerated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().doubleValue() == 50.0), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadPrescriptionPdf(any(Prescription.class), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadReferralPdf(any(Referral.class), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadFitNotePdf(any(FitNote.class), any(String.class));
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesWithElectronicPrescription_AppointmentInvoiceWithPrescriptionGenerated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        BigDecimal expectedPrice = new BigDecimal(57.0);
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);
        Prescription prescription = new Prescription(UUID.randomUUID(), PrescriptionType.ELECTRONIC, "Acetaminophen", "Take 2 tablets daily");
        when(prescriptionRepositoryMock.getByAppointmentId(appointmentId)).thenReturn(prescription);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().equals(expectedPrice)), any(String.class));
        verify(documentRepositoryMock, times(1)).uploadPrescriptionPdf(prescription, "/pdfTemplates/prescription/electronic.pdftemplate");
        verify(documentRepositoryMock, times(0)).uploadReferralPdf(any(Referral.class), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadFitNotePdf(any(FitNote.class), any(String.class));
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesWithFaxPrescription_AppointmentInvoiceWithPrescriptionGenerated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        BigDecimal expectedPrice = new BigDecimal(57.0);
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);
        Prescription prescription = new Prescription(UUID.randomUUID(), PrescriptionType.FAX, "Acetaminophen", "Take 2 tablets daily");
        when(prescriptionRepositoryMock.getByAppointmentId(appointmentId)).thenReturn(prescription);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().equals(expectedPrice)), any(String.class));
        verify(documentRepositoryMock, times(1)).uploadPrescriptionPdf(prescription, "/pdfTemplates/prescription/fax.pdftemplate");
        verify(documentRepositoryMock, times(0)).uploadReferralPdf(any(Referral.class), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadFitNotePdf(any(FitNote.class), any(String.class));
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesWithDeliveryPrescription_AppointmentInvoiceWithPrescriptionGenerated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        BigDecimal expectedPrice = new BigDecimal(57.0);
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);
        Prescription prescription = new Prescription(UUID.randomUUID(), PrescriptionType.DELIVERY, "Acetaminophen", "Take 2 tablets daily");
        when(prescriptionRepositoryMock.getByAppointmentId(appointmentId)).thenReturn(prescription);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().equals(expectedPrice)), any(String.class));
        verify(documentRepositoryMock, times(1)).uploadPrescriptionPdf(prescription, "/pdfTemplates/prescription/delivery.pdftemplate");
        verify(documentRepositoryMock, times(0)).uploadReferralPdf(any(Referral.class), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadFitNotePdf(any(FitNote.class), any(String.class));
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesWithInsuranceCoveredInPersonReferral_AppointmentInvoiceWithReferralCreated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        BigDecimal expectedPrice = new BigDecimal(50.0);
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);
        Referral referral = new Referral(UUID.randomUUID(), ReferralType.IN_PERSON_GP_APPOINTMENT, "General Practice", "221B Baker Street", "john.wattson@gmail.com", "headache", true, new BigDecimal(100));
        when(referralRepositoryMock.getByAppointmentId(appointmentId)).thenReturn(referral);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().equals(expectedPrice) 
        		&& x.getDeliveryAddress().equals(referral.getPracticeAddress())), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadPrescriptionPdf(any(), any());
        verify(documentRepositoryMock, times(1)).uploadReferralPdf(referral, Referral.IN_PERSON_REFERRAL_PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock, times(0)).uploadFitNotePdf(any(FitNote.class), any(String.class));
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesWithInsuranceCoveredSpecialistReferral_AppointmentInvoiceWithReferralCreated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        BigDecimal expectedPrice = new BigDecimal(50.0);
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);
        Referral referral = new Referral(UUID.randomUUID(), ReferralType.SPECIALIST_APPOINTMENT, "General Practice", "221B Baker Street", "john.wattson@gmail.com", "headache", true, new BigDecimal(100));
        when(referralRepositoryMock.getByAppointmentId(appointmentId)).thenReturn(referral);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().equals(expectedPrice)
        		&& x.getDeliveryAddress().equals(appointmentNotes.getPatientAddress())), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadPrescriptionPdf(any(), any());
        verify(documentRepositoryMock, times(1)).uploadReferralPdf(referral, Referral.SPECIALIST_APPOINTMENT_REFERRAL_PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock, times(0)).uploadFitNotePdf(any(FitNote.class), any(String.class));
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesWithInPersonNotCoveredByInsurance_AppointmentInvoiceWithReferralCreated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        BigDecimal expectedPrice = new BigDecimal(150.0);
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);
        Referral referral = new Referral(UUID.randomUUID(), ReferralType.IN_PERSON_GP_APPOINTMENT, "General Practice", "221B Baker Street", "john.wattson@gmail.com", "headache", false, new BigDecimal(100));
        when(referralRepositoryMock.getByAppointmentId(appointmentId)).thenReturn(referral);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().equals(expectedPrice)
        		&& x.getDeliveryAddress().equals(appointmentNotes.getPatientAddress())), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadPrescriptionPdf(any(), any());
        verify(documentRepositoryMock, times(1)).uploadReferralPdf(referral, Referral.IN_PERSON_REFERRAL_PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock, times(0)).uploadFitNotePdf(any(FitNote.class), any(String.class));
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
    
    @Test
    public void publishAppointmentNotes_ValidAppointmentNotesWithFitNote_AppointmentInvoiceWithFitNoteCreated() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        BigDecimal expectedPrice = new BigDecimal(50.0);
        AppointmentNotes appointmentNotes = new AppointmentNotes(appointmentId, "Headache", "Migrena", new Date(), new ArrayList<Document>(), "John Doe", "123 Main St");
        when(appointmentRepositoryMock.getById(appointmentId)).thenReturn(appointmentNotes);
        FitNote fitNote = new FitNote(UUID.randomUUID(), "Headache", 3, "");
        when(fitNoteRepositoryMock.getByAppointmentId(appointmentId)).thenReturn(fitNote);

        appointmentService.publishAppointmentNotes(appointmentId);

        verify(documentRepositoryMock, times(1)).uploadAppointmentNotesPdf(appointmentNotes, AppointmentNotes.PDF_TEMPLATE_PATH);
        verify(documentRepositoryMock, times(1)).uploadAppointmentInvoicePdf(argThat(x -> x.getPrice().equals(expectedPrice)), any(String.class));
        verify(documentRepositoryMock, times(0)).uploadPrescriptionPdf(any(), any());
        verify(documentRepositoryMock, times(0)).uploadReferralPdf(any(), any());
        verify(documentRepositoryMock, times(1)).uploadFitNotePdf(fitNote, FitNote.PDF_TEMPLATE_PATH);
        verify(appointmentRepositoryMock, times(1)).update(appointmentNotes);
    }
}
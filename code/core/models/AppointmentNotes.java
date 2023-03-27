package core.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AppointmentNotes {
	private UUID appointmentId;
	private String healthConcern;
	private String diagnosis;
	private Date appointmentTime;
	private String patientName;
	private String patientAddress;
	private List<Document> appointmentDocuments;
	
	public static final String PDF_TEMPLATE_PATH = "/pdfTemplates/appointmentNotes.pdftemplate";
	
	public AppointmentNotes(UUID appointmentId, String healthConcern, String diagnosis, Date appointmentTime,
			List<Document> appointmentDocuments, String patientName, String patientAddress) {
		super();
		this.appointmentId = appointmentId;
		this.healthConcern = healthConcern;
		this.diagnosis = diagnosis;
		this.appointmentTime = appointmentTime;
		this.appointmentDocuments = appointmentDocuments;
		this.patientName = patientName;
		this.patientAddress = patientAddress;
	}
	public AppointmentNotes() {
	}
	public UUID getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(UUID appointmentId) {
		this.appointmentId = appointmentId;
	}
	public String getHealthConcern() {
		return healthConcern;
	}
	public void setHealthConcern(String healthConcern) {
		this.healthConcern = healthConcern;
	}
	public String getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	public Date getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(Date appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public List<Document> getAppointmentDocuments() {
		return appointmentDocuments;
	}
	public void setAppointmentDocuments(List<Document> appointmentDocuments) {
		this.appointmentDocuments = appointmentDocuments;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientAddress() {
		return patientAddress;
	}
	public void setPatientAddress(String patientAddress) {
		this.patientAddress = patientAddress;
	}
}

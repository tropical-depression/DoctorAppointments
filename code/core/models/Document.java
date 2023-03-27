package core.models;

import java.util.UUID;

public class Document {
	private UUID id;
	private DocumentType type;
	private UUID appointmentId;
	
	public Document(UUID id, DocumentType type, UUID appointmentId) {
		super();
		this.id = id;
		this.type = type;
		this.appointmentId = appointmentId;
	}
	public Document() {
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public DocumentType getType() {
		return type;
	}
	public void setType(DocumentType type) {
		this.type = type;
	}
	public UUID getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(UUID appointmentId) {
		this.appointmentId = appointmentId;
	}
	
	
}

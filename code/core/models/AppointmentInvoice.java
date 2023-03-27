package core.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class AppointmentInvoice {
	public static final String PDF_TEMPLATE_PATH = "pdfTemplates/appointmentInvoice.pdftemplate";
	
	private UUID appointmentId;
	private BigDecimal price;
	private Date appointmentTime;
	private String patientName;
	private String deliveryAddress;
	
	public AppointmentInvoice(UUID appointmentId, BigDecimal price, Date appointmentTime, String patientName,
			String deliveryAddress) {
		super();
		this.appointmentId = appointmentId;
		this.price = price;
		this.appointmentTime = appointmentTime;
		this.patientName = patientName;
		this.deliveryAddress = deliveryAddress;
	}
	public UUID getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(UUID appointmentId) {
		this.appointmentId = appointmentId;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Date getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(Date appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
}

package core.models;

import java.math.BigDecimal;
import java.util.UUID;

public class Prescription {
	public static final BigDecimal STANDARD_PRESCRIPTION_PRICE = new BigDecimal(7.0);
	private final String ELECTRONIC_PRESCRIPTION_PDF_TEMPLATE_PATH = "/pdfTemplates/prescription/electronic.pdftemplate";
	private final String FAX_PRESCRIPTION_PDF_TEMPLATE_PATH = "/pdfTemplates/prescription/fax.pdftemplate";
	private final String DELIVERY_PRESCRIPTION_PDF_TEMPLATE_PATH = "/pdfTemplates/prescription/delivery.pdftemplate";
	private UUID id;
	private PrescriptionType type;
	private String medication;
	private String dose;
	
	public Prescription(UUID id, PrescriptionType type, String medication, String dose) {
		super();
		this.id = id;
		this.type = type;
		this.medication = medication;
		this.dose = dose;
	}
	public Prescription() {
		// TODO Auto-generated constructor stub
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public PrescriptionType getType() {
		return type;
	}
	public void setType(PrescriptionType type) {
		this.type = type;
	}
	public String getMedication() {
		return medication;
	}
	public void setMedication(String medication) {
		this.medication = medication;
	}
	public String getDose() {
		return dose;
	}
	public void setDose(String dose) {
		this.dose = dose;
	}
	
	public String getPdfTemplatePath() {
		switch (this.type) {
		case ELECTRONIC: 
			return ELECTRONIC_PRESCRIPTION_PDF_TEMPLATE_PATH;
		case FAX:
			return FAX_PRESCRIPTION_PDF_TEMPLATE_PATH;
		case DELIVERY:
			return DELIVERY_PRESCRIPTION_PDF_TEMPLATE_PATH;
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}
}

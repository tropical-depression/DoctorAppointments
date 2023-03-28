package core.models;

import java.math.BigDecimal;
import java.util.UUID;

public class Referral {
	public static final String IN_PERSON_REFERRAL_PDF_TEMPLATE_PATH = "/pdfTemplates/referrals/inPerson";
	public static final String SPECIALIST_APPOINTMENT_REFERRAL_PDF_TEMPLATE_PATH = "/pdfTemplates/referrals/specialist";
	
	private UUID id;
	private ReferralType type;
	private String practiceName;
	private String practiceAddress;
	private String practiceEmail;
	private String healthConcern;
	private boolean isCoveredByInsurance;
	private BigDecimal price;
	
	public Referral(UUID id, ReferralType type, String practiceName, String practiceAddress, String practiceEmail,
			String healthConcern, boolean isCoveredByInsurance, BigDecimal price) {
		super();
		this.id = id;
		this.type = type;
		this.practiceName = practiceName;
		this.practiceAddress = practiceAddress;
		this.practiceEmail = practiceEmail;
		this.healthConcern = healthConcern;
		this.isCoveredByInsurance = isCoveredByInsurance;
		this.price = price;
	}
	
	public Referral() {
	}

	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public ReferralType getType() {
		return type;
	}
	public void setType(ReferralType type) {
		this.type = type;
	}
	public String getPracticeName() {
		return practiceName;
	}
	public void setPracticeName(String practiceName) {
		this.practiceName = practiceName;
	}
	public String getPracticeAddress() {
		return practiceAddress;
	}
	public void setPracticeAddress(String practiceAddress) {
		this.practiceAddress = practiceAddress;
	}
	public String getPracticeEmail() {
		return practiceEmail;
	}
	public void setPracticeEmail(String practiceEmail) {
		this.practiceEmail = practiceEmail;
	}
	public String getHealthConcern() {
		return healthConcern;
	}
	public void setHealthConcern(String healthConcern) {
		this.healthConcern = healthConcern;
	}
	public boolean isCoveredByInsurance() {
		return isCoveredByInsurance;
	}
	public void setCoveredByInsurance(boolean isCoveredByInsurance) {
		this.isCoveredByInsurance = isCoveredByInsurance;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}

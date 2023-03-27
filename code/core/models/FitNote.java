package core.models;

import java.util.UUID;

public class FitNote {
	public static final String PDF_TEMPLATE_PATH = "pdfTemplates/fitNote.pdftemplate";
	
	private UUID id;
	private String healthConcern;
	private int numberOfDaysOff;
	private String comments;
	
	public FitNote(UUID id, String healthConcern, int numberOfDaysOff, String comments) {
		super();
		this.id = id;
		this.healthConcern = healthConcern;
		this.numberOfDaysOff = numberOfDaysOff;
		this.comments = comments;
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getHealthConcern() {
		return healthConcern;
	}
	public void setHealthConcern(String healthConcern) {
		this.healthConcern = healthConcern;
	}
	public int getNumberOfDaysOff() {
		return numberOfDaysOff;
	}
	public void setNumberOfDaysOff(int numberOfDaysOff) {
		this.numberOfDaysOff = numberOfDaysOff;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}

package mygpstracker.android.mygpstracker.DB;

public class ReviewInfoWrapper {

    private String ID;
    private String review;
    private String dateOfVisit;
    private String companion;
    private String frequency;
    private String purpose;
    private String rate;

    public ReviewInfoWrapper(String ID, String review, String dateOfVisit, String companion, String frequency, String purpose, String rate) {
        this.ID = ID;
        this.review = review;
        this.dateOfVisit = dateOfVisit;
        this.companion = companion;
        this.frequency = frequency;
        this.purpose = purpose;
        this.rate = rate;
    }

    public String getID() {
        return ID;
    }

    public String getReview() {
        return review;
    }

    public String getDateOfVisit() {
        return dateOfVisit;
    }

    public String getCompanion() {
        return companion;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getRate() {
        return rate;
    }
}

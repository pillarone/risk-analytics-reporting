package org.pillarone.riskanalytics.reporting.gira.databeans;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoBean {

    private String underwritingSegmentName;
    private double premium;
    private double numberOfPolicies;
    private double maxSumInsured;
    private double averageSumInsured;

    public UnderwritingInfoBean(String underwritingSegmentName, double premium, double numberOfPolicies, double maxSumInsured, double averageSumInsured) {
        this.underwritingSegmentName = underwritingSegmentName;
        this.premium = premium;
        this.numberOfPolicies = numberOfPolicies;
        this.maxSumInsured = maxSumInsured;
        this.averageSumInsured = averageSumInsured;
    }

    public String getUnderwritingSegmentName() {
        return underwritingSegmentName;
    }

    public void setUnderwritingSegmentName(String underwritingSegmentName) {
        this.underwritingSegmentName = underwritingSegmentName;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public double getNumberOfPolicies() {
        return numberOfPolicies;
    }

    public void setNumberOfPolicies(double numberOfPolicies) {
        this.numberOfPolicies = numberOfPolicies;
    }

    public double getMaxSumInsured() {
        return maxSumInsured;
    }

    public void setMaxSumInsured(double maxSumInsured) {
        this.maxSumInsured = maxSumInsured;
    }

    public double getAverageSumInsured() {
        return averageSumInsured;
    }

    public void setAverageSumInsured(double averageSumInsured) {
        this.averageSumInsured = averageSumInsured;
    }
}

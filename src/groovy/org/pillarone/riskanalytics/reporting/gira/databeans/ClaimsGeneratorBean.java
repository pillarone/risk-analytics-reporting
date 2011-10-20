package org.pillarone.riskanalytics.reporting.gira.databeans;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(rpa): how to handle distributions defined in a table?
public class ClaimsGeneratorBean {

    private String perilName;
    private String claimType;
    private String frequencyDistribution;
    private String frequencyDistributionParam1;
    private String frequencyDistributionParam2;
    private String frequencyDistributionParam3;
    private String frequencyDistributionValue1;
    private String frequencyDistributionValue2;
    private String frequencyDistributionValue3;
    private String severityDistribution;
    private String severityDistributionParam1;
    private String severityDistributionParam2;
    private String severityDistributionParam3;
    private String severityDistributionValue1;
    private String severityDistributionValue2;
    private String severityDistributionValue3;

    private String claimTypeLabel = "Claim Type";
    private String relevantPortionLabel = "Relevant Portion";
    private String relevantPortion = "100 %";

    public ClaimsGeneratorBean(String perilName, String claimType, String frequencyDistribution, String frequencyDistributionParam1,
                               String frequencyDistributionParam2, String frequencyDistributionParam3,
                               String frequencyDistributionValue1, String frequencyDistributionValue2,
                               String frequencyDistributionValue3, String severityDistribution,
                               String severityDistributionParam1, String severityDistributionParam2,
                               String severityDistributionParam3, String severityDistributionValue1,
                               String severityDistributionValue2, String severityDistributionValue3) {
        this.perilName = perilName;
        this.claimType = claimType;
        this.frequencyDistribution = frequencyDistribution;
        this.frequencyDistributionParam1 = frequencyDistributionParam1;
        this.frequencyDistributionParam2 = frequencyDistributionParam2;
        this.frequencyDistributionParam3 = frequencyDistributionParam3;
        this.frequencyDistributionValue1 = frequencyDistributionValue1;
        this.frequencyDistributionValue2 = frequencyDistributionValue2;
        this.frequencyDistributionValue3 = frequencyDistributionValue3;
        this.severityDistribution = severityDistribution;
        this.severityDistributionParam1 = severityDistributionParam1;
        this.severityDistributionParam2 = severityDistributionParam2;
        this.severityDistributionParam3 = severityDistributionParam3;
        this.severityDistributionValue1 = severityDistributionValue1;
        this.severityDistributionValue2 = severityDistributionValue2;
        this.severityDistributionValue3 = severityDistributionValue3;
    }

    public ClaimsGeneratorBean(String perilName, String severityDistribution, String severityDistributionParam1,
                               String severityDistributionValue1, String claimType) {
        this.perilName = perilName;
        this.severityDistribution = severityDistribution;
        this.severityDistributionParam1 = severityDistributionParam1;
        this.severityDistributionValue1 = severityDistributionValue1;
        this.claimType = claimType;
    }

    public ClaimsGeneratorBean(String perilName, String severityDistribution, String severityDistributionParam1,
                               String severityDistributionParam2, String severityDistributionParam3, 
                               String severityDistributionValue1, String severityDistributionValue2,
                               String severityDistributionValue3, String claimType) {
        this.perilName = perilName;
        this.severityDistribution = severityDistribution;
        this.severityDistributionParam1 = severityDistributionParam1;
        this.severityDistributionParam2 = severityDistributionParam2;
        this.severityDistributionParam3 = severityDistributionParam3;
        this.severityDistributionValue1 = severityDistributionValue1;
        this.severityDistributionValue2 = severityDistributionValue2;
        this.severityDistributionValue3 = severityDistributionValue3;
        this.claimType = claimType;
    }

    public String getPerilName() {
        return perilName;
    }

    public void setPerilName(String perilName) {
        this.perilName = perilName;
    }

    public Boolean hasFrequencyDistribution() {
        return frequencyDistribution != null;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getFrequencyDistribution() {
        return frequencyDistribution;
    }

    public void setFrequencyDistribution(String frequencyDistribution) {
        this.frequencyDistribution = frequencyDistribution;
    }

    public String getFrequencyDistributionParam1() {
        return frequencyDistributionParam1;
    }

    public void setFrequencyDistributionParam1(String frequencyDistributionParam1) {
        this.frequencyDistributionParam1 = frequencyDistributionParam1;
    }

    public String getFrequencyDistributionParam2() {
        return frequencyDistributionParam2;
    }

    public void setFrequencyDistributionParam2(String frequencyDistributionParam2) {
        this.frequencyDistributionParam2 = frequencyDistributionParam2;
    }

    public String getFrequencyDistributionParam3() {
        return frequencyDistributionParam3;
    }

    public void setFrequencyDistributionParam3(String frequencyDistributionParam3) {
        this.frequencyDistributionParam3 = frequencyDistributionParam3;
    }

    public String getFrequencyDistributionValue1() {
        return frequencyDistributionValue1;
    }

    public void setFrequencyDistributionValue1(String frequencyDistributionValue1) {
        this.frequencyDistributionValue1 = frequencyDistributionValue1;
    }

    public String getFrequencyDistributionValue2() {
        return frequencyDistributionValue2;
    }

    public void setFrequencyDistributionValue2(String frequencyDistributionValue2) {
        this.frequencyDistributionValue2 = frequencyDistributionValue2;
    }

    public String getFrequencyDistributionValue3() {
        return frequencyDistributionValue3;
    }

    public void setFrequencyDistributionValue3(String frequencyDistributionValue3) {
        this.frequencyDistributionValue3 = frequencyDistributionValue3;
    }

    public String getSeverityDistribution() {
        return severityDistribution;
    }

    public void setSeverityDistribution(String severityDistribution) {
        this.severityDistribution = severityDistribution;
    }

    public String getSeverityDistributionParam1() {
        return severityDistributionParam1;
    }

    public void setSeverityDistributionParam1(String severityDistributionParam1) {
        this.severityDistributionParam1 = severityDistributionParam1;
    }

    public String getSeverityDistributionParam2() {
        return severityDistributionParam2;
    }

    public void setSeverityDistributionParam2(String severityDistributionParam2) {
        this.severityDistributionParam2 = severityDistributionParam2;
    }

    public String getSeverityDistributionParam3() {
        return severityDistributionParam3;
    }

    public void setSeverityDistributionParam3(String severityDistributionParam3) {
        this.severityDistributionParam3 = severityDistributionParam3;
    }

    public String getSeverityDistributionValue1() {
        return severityDistributionValue1;
    }

    public void setSeverityDistributionValue1(String severityDistributionValue1) {
        this.severityDistributionValue1 = severityDistributionValue1;
    }

    public String getSeverityDistributionValue2() {
        return severityDistributionValue2;
    }

    public void setSeverityDistributionValue2(String severityDistributionValue2) {
        this.severityDistributionValue2 = severityDistributionValue2;
    }

    public String getSeverityDistributionValue3() {
        return severityDistributionValue3;
    }

    public void setSeverityDistributionValue3(String severityDistributionValue3) {
        this.severityDistributionValue3 = severityDistributionValue3;
    }

    public String getClaimTypeLabel() {
        return claimTypeLabel;
    }

    public void setClaimTypeLabel(String claimTypeLabel) {
        this.claimTypeLabel = claimTypeLabel;
    }

    public String getRelevantPortion() {
        return relevantPortion;
    }

    public void setRelevantPortion(String relevantPortion) {
        this.relevantPortion = relevantPortion;
    }

    public String getRelevantPortionLabel() {
        return relevantPortionLabel;
    }

    public void setRelevantPortionLabel(String relevantPortionLabel) {
        this.relevantPortionLabel = relevantPortionLabel;
    }
}

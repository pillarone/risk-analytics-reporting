package org.pillarone.riskanalytics.reporting.gira.databeans;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(rpa): how to handle distributions defined in a table?
public class ClaimsGeneratorBean implements Comparable<ClaimsGeneratorBean> {

    String perilName
    String claimType
    String frequencyDistribution
    String frequencyDistributionParam1
    String frequencyDistributionParam2
    String frequencyDistributionParam3
    String frequencyDistributionValue1
    String frequencyDistributionValue2
    String frequencyDistributionValue3
    String severityDistribution
    String severityDistributionParam1
    String severityDistributionParam2
    String severityDistributionParam3
    String severityDistributionValue1
    String severityDistributionValue2
    String severityDistributionValue3
    String severityDistributionModification

    String claimTypeLabel = "Claim Type"
    String relevantPortionLabel = "Relevant Portion"
    String relevantPortion = "100 %"



    public Boolean hasFrequencyDistribution() {
        frequencyDistribution != null
    }

    int compareTo(ClaimsGeneratorBean o) {
        return perilName.compareTo(o.perilName)
    }

}

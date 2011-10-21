package org.pillarone.riskanalytics.reporting.gira.databeans

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoBean implements Comparable<UnderwritingInfoBean> {

    String underwritingSegmentName
    String premium
    String numberOfPolicies
    String maxSumInsured
    String averageSumInsured

    int compareTo(UnderwritingInfoBean o) {
        return underwritingSegmentName.compareTo(o.underwritingSegmentName)
    }
}

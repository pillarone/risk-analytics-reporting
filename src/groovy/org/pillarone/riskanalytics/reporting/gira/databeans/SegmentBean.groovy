package org.pillarone.riskanalytics.reporting.gira.databeans;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SegmentBean {
    String segmentName
    Map portionPerClaimsGenerator = new HashMap<ClaimsGeneratorBean, String>()
    Map portionPerRiskBand = new HashMap<UnderwritingInfoBean, String>()

    @Override
    String toString() {
        segmentName
    }


}

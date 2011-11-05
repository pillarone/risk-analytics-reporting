package org.pillarone.riskanalytics.reporting.gira.databeans

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SegmentBean {

    String segmentName
    Map portionPerClaimsGenerator = new HashMap<ClaimsGeneratorBean, String>()
    Map portionPerUnderwritingInfo = new HashMap<UnderwritingInfoBean, String>()
    Map portionPerRiskBand = new HashMap<UnderwritingInfoBean, String>()
    JRMapCollectionDataSource results

    JRBeanCollectionDataSource getClaimsGenerators() {
        new JRBeanCollectionDataSource(portionPerClaimsGenerator.keySet())
    }

    JRBeanCollectionDataSource getRiskBands() {
        new JRBeanCollectionDataSource(portionPerUnderwritingInfo.keySet())
    }

    @Override
    String toString() {
        segmentName
    }
}

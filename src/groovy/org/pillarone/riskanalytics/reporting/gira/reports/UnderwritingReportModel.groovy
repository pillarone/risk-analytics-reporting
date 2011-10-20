package org.pillarone.riskanalytics.reporting.gira.reports;

import models.gira.GIRAModel
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import org.pillarone.riskanalytics.core.report.ReportUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.reporting.gira.GIRAParameterReportingUtils
import org.pillarone.riskanalytics.reporting.gira.databeans.ReinsuranceContractBean
import org.pillarone.riskanalytics.reporting.gira.databeans.SegmentBean
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import org.pillarone.riskanalytics.reporting.gira.databeans.ClaimsGeneratorBean

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingReportModel implements IReportModel {

    private final static String reportDirectory = "/reports/gira/underwriting"

    public String getName() {
        'Underwriting Report'
    }

    public URL getMainReportFile() {
        getClass().getResource("$reportDirectory/UnderwritingReport.jasper")
    }

    // todo(sku): add something more senseful
    public JRDataSource getDataSource(IReportData reportData) {
        JRMapCollectionDataSource dummyDataSource = new JRMapCollectionDataSource([['dummy' : 'dummy']])
        return dummyDataSource
    }

    public Map getParameters(IReportData reportData) {

        Parameterization parameterization = (Parameterization) ReportUtils.getSingleModellingItem(reportData)

        if (!parameterization.isLoaded()) {
            parameterization.load(true)
        }

        GIRAModel model = new GIRAModel()
        GIRAParameterReportingUtils.initModelForParameterReporting(model, parameterization)

        List<ClaimsGeneratorBean> claimsGeneratorBeans = GIRAParameterReportingUtils.getClaimsGenerators(model);
        JRBeanCollectionDataSource claimsGeneratorsDataSource = new JRBeanCollectionDataSource(claimsGeneratorBeans)

//        List<SegmentBean> segmentBeans = GIRAParameterReportingUtils.getSegments(model);
//        JRBeanCollectionDataSource segmentsDataSource = new JRBeanCollectionDataSource(segmentBeans)
//
//        List<ReinsuranceContractBean> reinsuranceContractBeans = GIRAParameterReportingUtils.getReinsuranceContracts(model)
//        JRBeanCollectionDataSource reinsuranceContractsDataSource = new JRBeanCollectionDataSource(reinsuranceContractBeans)

//        Populate parameter map with information
        [
                "SUBREPORT_DIR": getClass().getResource(reportDirectory),
                "LOGO_DIR": getClass().getResource(reportDirectory),

                "PARAMETERIZATION_NAME": parameterization.getName(),
                "P14N_VERSION": "v" + parameterization.versionNumber.toString(),

                "CLAIMS_GENERATORS": claimsGeneratorsDataSource,
//                "SEGMENTS": segmentsDataSource,
//                "REINSURANCE_CONTRACTS": reinsuranceContractsDataSource

        ]
    }

    public String getDefaultReportFileNameWithoutExtension(IReportData reportData) {
        Parameterization parameterization = (Parameterization) ReportUtils.getSingleModellingItem(reportData)
        getName() + " of ${parameterization.name}"
    }

    public boolean isValidFormatAndData(ReportFactory.ReportFormat reportFormat, IReportData reportData) {
        ReportUtils.isSingleItem(reportData, Parameterization)
    }
}

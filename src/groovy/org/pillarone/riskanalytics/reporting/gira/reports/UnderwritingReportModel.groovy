package org.pillarone.riskanalytics.reporting.gira.reports;

import models.gira.GIRAModel
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import org.pillarone.riskanalytics.core.report.ReportUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.reporting.gira.databeans.ClaimsGeneratorBean
import org.pillarone.riskanalytics.reporting.gira.*
import org.pillarone.riskanalytics.reporting.gira.databeans.UnderwritingInfoBean
import org.pillarone.riskanalytics.reporting.gira.databeans.SegmentBean
import org.pillarone.riskanalytics.reporting.gira.databeans.ReinsuranceContractBean

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

    public JRDataSource getDataSource(IReportData reportData) {
        Simulation simulation = (Simulation)ReportUtils.getSingleModellingItem(reportData)

        if(!simulation.isLoaded()) {
            simulation.load()
        }

        GIRAModel model = new GIRAModel()
        ReportUtils.loadAndApplyParameterizationToModel(model, simulation.parameterization)

        String modelName = GIRAReportUtils.parseModelName(GIRAModel.simpleName)
        ResultPathParser parser = new ResultPathParser(modelName, ResultAccessor.getDistinctPaths(simulation.getSimulationRun())*.path.pathName.toList())
        List<SegmentBean> segmentBeans = GIRAParameterReportingUtils.getSegements(model, simulation, parser)

        List currentValues = []
        currentValues << ['segments': new JRBeanCollectionDataSource(segmentBeans)]

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }


    public Map getParameters(IReportData reportData) {

        Simulation simulation = (Simulation) ReportUtils.getSingleModellingItem(reportData)
        Parameterization parameterization = simulation.parameterization

        GIRAModel model = new GIRAModel()
        ReportUtils.loadAndApplyParameterizationToModel(model, parameterization)

        List<ReinsuranceContractBean> reinsuranceContractBeans = GIRAParameterReportingUtils.getReinsuranceContracts(model)
        JRBeanCollectionDataSource reinsuranceContractsDataSource = new JRBeanCollectionDataSource(reinsuranceContractBeans)

        [
                "SUBREPORT_DIR": getClass().getResource(reportDirectory),
                "LOGO_DIR": getClass().getResource(reportDirectory),

                "PARAMETERIZATION_NAME": parameterization.getName(),
                "P14N_VERSION": "v" + parameterization.versionNumber.toString(),

                "REINSURANCE_CONTRACTS": reinsuranceContractsDataSource
        ]
    }

    public String getDefaultReportFileNameWithoutExtension(IReportData reportData) {
        Simulation simulation = (Simulation) ReportUtils.getSingleModellingItem(reportData)
        getName() + " of ${simulation.name}" + System.currentTimeMillis()
    }

    public boolean isValidFormatAndData(ReportFactory.ReportFormat reportFormat, IReportData reportData) {
        ReportUtils.isSingleItem(reportData, Simulation)
    }
}

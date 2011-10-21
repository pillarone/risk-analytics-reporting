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
        String modelName = GIRAReportUtils.parseModelName(GIRAModel.simpleName)
        ResultPathParser parser = new ResultPathParser(modelName, ResultAccessor.getPaths(simulation.getSimulationRun()))

        List currentValues = []
        for (List<List<String>> componentPaths in getResultPaths(parser).values()) {
            currentValues << ['segmentResults' : new SegmentResultDataSourceFactory(simulation, parser).getSegmentResultDataSource(componentPaths)]
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    /**
     * @param parser
     * @return PathType relates to the component (claims generator, reinsurance contract, ...)
     *          the nested list contains all out channels (inner list) per component (outer list)
     */
    protected Map<PathType, List<List<ResultAccessorInformation>>> getResultPaths(ResultPathParser parser) {
        Map result = [:]
        IPathFilter segmentsFilter = PathFilter.getFilter(parser.getComponentPath(PathType.SEGMENTS),
            ['outUnderwritingInfoGross', 'outClaimsGross'])
        GIRAReportUtils.addList(result, PathType.SEGMENTS, parser.getComponentPaths(PathType.SEGMENTS, segmentsFilter))
        result[PathType.SEGMENTS] = ResultPathParser.mergeSplittedPathsWithMainList(result[PathType.SEGMENTS], 'claimsGenerators', 'GIRA:segments', 2)
        result
    }

    public Map getParameters(IReportData reportData) {

        Simulation simulation = (Simulation) ReportUtils.getSingleModellingItem(reportData)
        Parameterization parameterization = simulation.parameterization

        GIRAModel model = new GIRAModel()
        GIRAParameterReportingUtils.initModelForParameterReporting(model, parameterization)

        List<ClaimsGeneratorBean> claimsGeneratorBeans = GIRAParameterReportingUtils.getClaimsGenerators(model)
        JRBeanCollectionDataSource claimsGeneratorsDataSource = new JRBeanCollectionDataSource(claimsGeneratorBeans)

        List<UnderwritingInfoBean> underwritingInfoBeans = GIRAParameterReportingUtils.getUnderwritingInformation(model);
        JRBeanCollectionDataSource underwritingInfoDataSource = new JRBeanCollectionDataSource(underwritingInfoBeans)
//
//        List<ReinsuranceContractBean> reinsuranceContractBeans = GIRAParameterReportingUtils.getReinsuranceContracts(model)
//        JRBeanCollectionDataSource reinsuranceContractsDataSource = new JRBeanCollectionDataSource(reinsuranceContractBeans)

        [
                "SUBREPORT_DIR": getClass().getResource(reportDirectory),
                "LOGO_DIR": getClass().getResource(reportDirectory),

                "PARAMETERIZATION_NAME": parameterization.getName(),
                "P14N_VERSION": "v" + parameterization.versionNumber.toString(),

                "UW_SEGMENTS": underwritingInfoDataSource,
                "CLAIMS_GENERATORS": claimsGeneratorsDataSource,
//                "REINSURANCE_CONTRACTS": reinsuranceContractsDataSource

        ]
    }

    public String getDefaultReportFileNameWithoutExtension(IReportData reportData) {
        Simulation simulation = (Simulation) ReportUtils.getSingleModellingItem(reportData)
        getName() + " of ${simulation.name}"
    }

    public boolean isValidFormatAndData(ReportFactory.ReportFormat reportFormat, IReportData reportData) {
        ReportUtils.isSingleItem(reportData, Simulation)
    }
}

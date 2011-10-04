package org.pillarone.riskanalytics.reporting.gira

import models.gira.GIRAModel
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.report.ReportUtils

class GIRAReportModel implements IReportModel {

    private static final String NAME = "GIRA Report"


    JRDataSource getDataSource(IReportData reportData) {
        Simulation simulation = ReportUtils.getSingleSimulation(reportData)

        if(!simulation.isLoaded()) {
            simulation.load()
        }
        String modelName = GIRAReportUtils.parseModelName(GIRAModel.simpleName)
        ResultPathParser parser = new ResultPathParser(modelName, ResultAccessor.getPaths(simulation.getSimulationRun()))

        List currentValues = []
        for (Map.Entry<PathType, List<List<String>>> entry in getPaths(parser).entrySet()) {
            PathType pathType = entry.key
            List<List<String>> componentPaths = entry.value

            Map map = [:]
            List<ReportWaterfallDataBean> beans = AbstractWaterfallChart.getInstance(pathType, simulation, parser).getBeans(parser.getPathsByPathType(componentPaths, pathType))
            JRMapCollectionDataSource waterfallDataSource = GIRAReportUtils.getRendererDataSource("waterfallChart", JasperChartUtils.generateWaterfallChart(beans))
            map["PDFCharts"] = new ChartDataSourceFactory(simulation, parser).getPDFChartsDataSource(componentPaths, waterfallDataSource, pathType)
            currentValues << map
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected Map<PathType, List<List<String>>> getPaths(ResultPathParser parser) {
        Map result = [:]
        IPathFilter claimsFilter = PathFilter.getFilter(parser.getComponentPath(PathType.CLAIMSGENERATORS), ResultPathParser.CLAIMS_SUFFIX_LIST)
        IPathFilter reinsuranceFilter = PathFilter.getFilter(parser.getComponentPath(PathType.REINSURANCE), ResultPathParser.REINSURANCE_TABLE_SUFFIX_LIST)
        GIRAReportUtils.addList(result, PathType.CLAIMSGENERATORS, parser.getComponentPaths(PathType.CLAIMSGENERATORS, claimsFilter))
        GIRAReportUtils.addList(result, PathType.REINSURANCE, parser.getComponentPaths(PathType.REINSURANCE, reinsuranceFilter))
        return result
    }

    String getName() {
        return NAME
    }

    URL getMainReportFile() {
        return getClass().getResource("/reports/gira/GiraReport.jasper")
    }

    Map getParameters(IReportData reportData) {
        Simulation simulation = ReportUtils.getSingleSimulation(reportData)
        Map params = new HashMap()
        params["charts"] = getDataSource(reportData)
        params["title"] = "Example Report"
        params["footer"] = GiraReportHelper.getFooter()
        params["infos"] = GIRAReportUtils.getItemInfo(simulation)
        Person currentUser = UserManagement.getCurrentUser()
        params["currentUser"] = currentUser ? currentUser.username : ""
        params["itemInfo"] = UIUtils.getText(GiraReportHelper.class, Simulation.simpleName + "Info")
        params["_file"] = "GiraReport"
        params["SUBREPORT_DIR"] = getClass().getResource("/reports/gira")
        params["Comment"] = "Comment"
        params["p1Icon"] = getClass().getResource(UIUtils.ICON_DIRECTORY + "application.png")
        params["p1Logo"] = getClass().getResource(UIUtils.ICON_DIRECTORY + "pillarone-logo-transparent-background-report.png")
        return params
    }

    String getDefaultReportFileNameWithoutExtension(IReportData reportData) {
        Simulation simulation = ReportUtils.getSingleSimulation(reportData)
        return "${name} of ${simulation.name}"
    }



}

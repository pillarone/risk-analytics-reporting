package org.pillarone.riskanalytics.reporting.gira

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import org.pillarone.riskanalytics.application.ui.util.UIUtils

import org.pillarone.riskanalytics.core.simulation.item.Simulation
import static org.pillarone.riskanalytics.reporting.gira.GiraReportHelper.format
import static org.pillarone.riskanalytics.reporting.gira.GiraReportHelper.formatPercentage
import org.pillarone.riskanalytics.reporting.gira.reports.UnderwritingReportModel
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SegmentResultDataSourceFactory {

    protected ResultPathParser parser
    protected GiraReportHelper reportHelper
    protected MeanStDevVaR200yCValuesBean valuesBean
    String modelName

    public SegmentResultDataSourceFactory(Simulation simulation, ResultPathParser parser) {
        this.parser = parser
        modelName = simulation.modelClass.simpleName
        reportHelper = new GiraReportHelper(simulation: simulation)

    }

    // todo(sku): move premium and total claim in a separate bean
    JRMapCollectionDataSource getSegmentResultDataSource(List<List<ResultAccessorInformation>> resultAccessorInformations) {
        List simpleMasterList = new ArrayList();
        for (List<ResultAccessorInformation> resultAccessorInformation: resultAccessorInformations) {
            simpleMasterList << ['segmentResult' : getFieldFunctionValues(resultAccessorInformation)]
        }
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    private JRBeanCollectionDataSource getFieldFunctionValues(List<ResultAccessorInformation> resultAccessorInformations) {
        int periodIndex = 0
        Collection currentValues = new ArrayList()
        double premium = getTotalPremium(resultAccessorInformations)
        String segmentName = 'segment name missing'
        resultAccessorInformations.each {ResultAccessorInformation resultAccessorInformation ->
            if (resultAccessorInformation.collector.equals(AggregatedCollectingModeStrategy.IDENTIFIER)) {
                segmentName = resultAccessorInformation.descriptor
            }
            valuesBean = new MeanStDevVaR200yCValuesBean(reportHelper.getSimulationRun(), resultAccessorInformation.collector)
            String path = resultAccessorInformation.path
            for (String fieldName : resultAccessorInformation.fields) {
                currentValues << [
                    'rowLabel': resultAccessorInformation.descriptor,
                    'meanValue': format(valuesBean.getMean(path, fieldName, periodIndex)),
                    'meanRatio': formatPercentage(valuesBean.getMeanRatio(path, fieldName, periodIndex, premium)),
                    'stdDevValue': format(valuesBean.getStdDev(path, fieldName, periodIndex)),
                    'stdDevRatio': formatPercentage(valuesBean.getStdDevRatio(path, fieldName, periodIndex, premium)),
                    'varValue': format(valuesBean.getVar(path, fieldName, periodIndex, 99.5)),
                    'varRatio': formatPercentage(valuesBean.getVarRatio(path, fieldName, periodIndex, 99.5, premium)),
                    '200yearValue': format(valuesBean.getPercentile(path, fieldName, periodIndex, 99.5)),
                    '200yearRatio': formatPercentage(valuesBean.getPercentileRatio(path, fieldName, periodIndex, 99.5, premium)),
                    'totalPremium': format(premium),
                    'segmentName' : segmentName]
            }
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    private double getTotalPremium(List<ResultAccessorInformation> resultAccessorInformations) {
        double totalPremium = 0
        int periodIndex = 0
        for (ResultAccessorInformation resultAccessorInformation : resultAccessorInformations) {
            if (resultAccessorInformation.collector.equals(AggregatedCollectingModeStrategy.IDENTIFIER)) {
                for (String fieldName : resultAccessorInformation.fields) {
                    if (fieldName.equals('premiumWritten')) {
                        valuesBean = new MeanStDevVaR200yCValuesBean(reportHelper.getSimulationRun(), resultAccessorInformation.collector)
                        totalPremium += valuesBean.getMean(resultAccessorInformation.path, fieldName, periodIndex)
                    }
                }
            }
        }
        totalPremium
    }

}
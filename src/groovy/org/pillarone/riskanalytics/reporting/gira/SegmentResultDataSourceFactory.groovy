package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.simulation.item.Simulation
import static org.pillarone.riskanalytics.reporting.gira.GiraReportHelper.format
import static org.pillarone.riskanalytics.reporting.gira.GiraReportHelper.formatPercentage
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

    List getSegmentResultDataSource(List<List<ResultAccessorInformation>> resultAccessorInformations) {
        List simpleMasterList = new ArrayList();
        for (List<ResultAccessorInformation> resultAccessorInformation: resultAccessorInformations) {
            simpleMasterList << getFieldFunctionValues(resultAccessorInformation)
        }
        simpleMasterList
    }

    private List getFieldFunctionValues(List<ResultAccessorInformation> resultAccessorInformations) {
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
                if (fieldName.equals('premiumWritten')) continue
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
        currentValues
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
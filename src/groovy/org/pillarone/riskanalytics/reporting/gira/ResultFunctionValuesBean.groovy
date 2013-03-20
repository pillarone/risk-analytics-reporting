package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.dataaccess.PostSimulationCalculationAccessor
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.reporting.gira.util.ReportResultAccessor
import org.pillarone.riskanalytics.core.output.QuantilePerspective

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultFunctionValuesBean {

    SimulationRun simulationRun
    String collectorName
    private Map<String, List> cachedValues
    private Map<String, Double> cachedStdDevValues
    private Map<String, Double> cachedMeanValues


    public ResultFunctionValuesBean(SimulationRun simulationRun, String collectorName) {
        this.simulationRun = simulationRun
        this.collectorName = collectorName
        initValues()
    }

    public initValues() {
        cachedValues = ReportResultAccessor.getAllValues(simulationRun, collectorName)
        cachedStdDevValues = PostSimulationCalculationAccessor.getKeyFigureResults(simulationRun, collectorName, PostSimulationCalculation.STDEV)
        cachedMeanValues = ReportResultAccessor.getMeans(simulationRun, collectorName)
    }

    public Double getStdDev(String path, String fieldName, int periodIndex) {
        if (!cachedStdDevValues[getKey(path, fieldName, periodIndex)]) {
            cachedStdDevValues[getKey(path, fieldName, periodIndex)] = ResultAccessor.getStdDev(simulationRun, periodIndex, path, collectorName, fieldName)
        }
        return cachedStdDevValues[getKey(path, fieldName, periodIndex)]
    }

    public Double getVar(String path, String fieldName, int periodIndex, double severity) {
        try {
            ResultAccessor.getTvar(simulationRun, periodIndex, path, collectorName, fieldName, severity, QuantilePerspective.LOSS)
        } catch (Exception ex) {
            return null
        }
    }

    public Double getTvar(String path, String fieldName, int periodIndex, double severity) {
        try {
            ResultAccessor.getTvar(simulationRun, periodIndex, path, collectorName, fieldName, severity, QuantilePerspective.LOSS)
        } catch (Exception ex) {
            return null
        }
    }


    public Double getMean(String path, String fieldName, int periodIndex) {
        if (!cachedMeanValues[getKey(path, fieldName, periodIndex)]) {
            cachedMeanValues[getKey(path, fieldName, periodIndex)] = ReportResultAccessor.getAvgOfSingleValueResult(simulationRun, periodIndex, path, collectorName, fieldName)
        }
        return cachedMeanValues[getKey(path, fieldName, periodIndex)]
    }

    public List getValues(String path, String fieldName, int periodIndex) {
        return cachedValues[getKey(path, fieldName, periodIndex)]
    }

    String getKey(String path, String fieldName, int periodIndex) {
        return path + ":" + fieldName + ":" + periodIndex
    }
}

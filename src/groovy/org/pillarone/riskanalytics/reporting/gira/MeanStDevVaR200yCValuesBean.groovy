package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.dataaccess.PostSimulationCalculationAccessor
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.reporting.gira.util.ReportResultAccessor
import org.pillarone.riskanalytics.core.output.QuantilePerspective

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class MeanStDevVaR200yCValuesBean {

    SimulationRun simulationRun
    String collectorName
    private Map<String, List> cachedValues
    private Map<String, Double> cachedStdDevValues
    private Map<String, Double> cachedMeanValues


    public MeanStDevVaR200yCValuesBean(SimulationRun simulationRun, String collectorName) {
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

    public Double getPercentile(String path, String fieldName, int periodIndex, double severity) {
        try {
            ResultAccessor.getPercentile(simulationRun, periodIndex, path, collectorName, fieldName, severity, QuantilePerspective.LOSS)
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

    public Double getMeanRatio(String path, String fieldName, int periodIndex, double total) {
        Double mean = getMean(path, fieldName, periodIndex)
        if (mean == null) return null
        total == 0d ? 0d : mean / total
    }

    public Double getStdDevRatio(String path, String fieldName, int periodIndex, double total) {
        Double stdDev = getStdDev(path, fieldName, periodIndex)
        if (stdDev == null) return null
        total == 0d ? 0d : stdDev / total
    }

    public Double getVarRatio(String path, String fieldName, int periodIndex, double severity, double total) {
        Double var = getVar(path, fieldName, periodIndex, severity)
        if (var == null) return null
        total == 0d ? 0d : var / total
    }

    public Double getTVaRRatio(String path, String fieldName, int periodIndex, double severity, double total) {
        Double tVar = getTvar(path, fieldName, periodIndex, severity)
        if (tVar == null) return null
        total == 0d ? 0d : tVar / total
    }

    public Double getPercentileRatio(String path, String fieldName, int periodIndex, double severity, double total) {
        Double percentile = getPercentile(path, fieldName, periodIndex, severity)
        if (percentile == null) return null
        total == 0d ? 0d : percentile / total
    }

    public List getValues(String path, String fieldName, int periodIndex) {
        return cachedValues[getKey(path, fieldName, periodIndex)]
    }

    String getKey(String path, String fieldName, int periodIndex) {
        return path + ":" + fieldName + ":" + periodIndex
    }
}

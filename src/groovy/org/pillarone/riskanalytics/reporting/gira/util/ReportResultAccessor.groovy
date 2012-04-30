package org.pillarone.riskanalytics.reporting.gira.util

import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.dataaccess.PostSimulationCalculationAccessor


abstract class ReportResultAccessor {

    static Map<String, List> getAllValues(SimulationRun simulationRun, String collectorName) {
        Map<String, List> valuesMap = [:]
        def singleValueResults = SingleValueResult.executeQuery("SELECT s.path.pathName, s.field.fieldName, s.period,s.value FROM org.pillarone.riskanalytics.core.output.SingleValueResult as s " +
                " WHERE s.collector.collectorName = ? and s.simulationRun.id = ? ORDER BY s.iteration ", [collectorName, simulationRun.id])
        for (def s : singleValueResults) {
            String key = s[0] + ":" + s[1] + ":" + s[2]
            List values = valuesMap[key]
            if (!values) values = []
            values << s[3]
            valuesMap[key] = values
        }
        return valuesMap
    }

    static Map<String, Double> getMeans(SimulationRun simulationRun, String collectorName) {
        return PostSimulationCalculationAccessor.getKeyFigureResults(simulationRun, collectorName, PostSimulationCalculation.MEAN)
    }

    static Double getAvgOfSingleValueResult(SimulationRun simulationRun, int periodIndex = 0, String pathName, String collectorName, String fieldName) {
        def res = SingleValueResult.executeQuery("SELECT AVG(value) FROM org.pillarone.riskanalytics.core.output.SingleValueResult as s " +
                " WHERE s.path.pathName = ? AND " +
                "s.collector.collectorName = ? AND " +
                "s.field.fieldName = ? AND " +
                "s.period = ? AND " +
                "s.simulationRun.id = ?", [pathName, collectorName, fieldName, periodIndex, simulationRun.id])
        return res[0]
    }
}

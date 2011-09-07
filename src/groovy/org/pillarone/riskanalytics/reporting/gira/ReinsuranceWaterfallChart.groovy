package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ReinsuranceWaterfallChart extends AbstractWaterfallChart {

     @Override
    protected double evaluate(SimulationRun run, int periodIndex, String path, String collector, String field) {
        return ResultAccessor.getMean(run, periodIndex, path, collector, field)
    }

    @Override
    protected void addDivDataBean(List<ReportWaterfallDataBean> beans, Double divValue) {

    }


}

package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ClaimsWaterfallChart extends AbstractWaterfallChart {

    @Override
    protected double evaluate(SimulationRun run, int periodIndex, String path, String collector, String field) {
        return ResultAccessor.getVar(run, periodIndex, path, collector, field, 99.5, QuantilePerspective.LOSS)
    }


}

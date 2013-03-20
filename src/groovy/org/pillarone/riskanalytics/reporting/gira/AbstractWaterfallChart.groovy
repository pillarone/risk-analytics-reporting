package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractWaterfallChart {

    protected ResultPathParser parser
    protected Simulation simulation

    protected List<ReportWaterfallDataBean> getBeans(List<String> paths) {
        if (paths.size() == 0) return []
        List<ReportWaterfallDataBean> beans = []
        Double totalValue = 0
        String parent = paths[0]
        int periodIndex = 0
        double sum = 0
        // create a ReportWaterfallDataBean for each column within the waterfall chart using the 95% as value and the path as name
        paths.eachWithIndex {String path, int index ->
            if (!parser.isParentPath(path)) {
                try {
                    Double var95 = evaluate(simulation.getSimulationRun(), periodIndex, path, AggregatedCollectingModeStrategy.IDENTIFIER, "ultimate")
                    sum += var95
                    beans << new ReportWaterfallDataBean(line: ResultViewUtils.getResultNodePathShortDisplayName(simulation?.modelClass, path), value: var95)
                } catch (Exception ex) {}
            }

        }

        totalValue =  evaluate(simulation.getSimulationRun(), periodIndex, parent, AggregatedCollectingModeStrategy.IDENTIFIER, "ultimate")
        // diversification effect
        addDivDataBean(beans, totalValue - sum)
        // total var
        addTotalDataBean(beans, totalValue)
        return beans
    }

    protected void addTotalDataBean(List<ReportWaterfallDataBean> beans, Double totalValue) {
        ReportWaterfallDataBean total = new ReportWaterfallDataBean(line: "total", value: totalValue)
        beans << total
    }

    protected void addDivDataBean(List<ReportWaterfallDataBean> beans, Double divValue) {
        try {
            ReportWaterfallDataBean div = new ReportWaterfallDataBean(line: "diversification", value: divValue)
            beans << div
        } catch (Exception ex) {}
    }

    protected abstract double evaluate(SimulationRun run, int periodIndex, String path, String collector, String field)

    public static AbstractWaterfallChart getInstance(PathType pathType, Simulation simulation, ResultPathParser parser) {
        AbstractWaterfallChart instance
        switch (pathType) {
            case PathType.CLAIMSGENERATORS: instance = new ClaimsWaterfallChart(); break
            case PathType.REINSURANCE: instance = new ReinsuranceWaterfallChart(); break
        }
        instance.simulation = simulation
        instance.parser = parser
        return instance
    }

    public static String getTitle(PathType pathType){
         switch (pathType) {
            case PathType.CLAIMSGENERATORS: return "Overview  VaR 99.5% (ultimate)"
            case PathType.REINSURANCE: return "Overview  Mean (ultimate)"
        }
    }
}

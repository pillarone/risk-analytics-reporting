package org.pillarone.riskanalytics.reporting.gira

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MultiPathFilter implements IPathFilter {

    List<IPathFilter> filters

    MultiPathFilter(List<IPathFilter> filters) {
        this.filters = filters
    }

    boolean accept(String path) {
        for (IPathFilter filter: filters) {
            if (!filter.accept(path))
                return false
        }
        return true
    }

    List<String> beginningOfPath() {
        filters*.beginningOfPath()
    }

}

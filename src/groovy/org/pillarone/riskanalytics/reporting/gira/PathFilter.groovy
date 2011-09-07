package org.pillarone.riskanalytics.reporting.gira

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PathFilter implements IPathFilter {

    String start

    PathFilter(String start) {
        this.start = start
    }

    boolean accept(String path) {
        return path.startsWith(start)
    }

    public static IPathFilter getFilter(String pathStart, List<String> suffixes) {
        return new MultiPathFilter([new PathFilter(pathStart), new SuffixPathFilter(suffixes)])
    }


}

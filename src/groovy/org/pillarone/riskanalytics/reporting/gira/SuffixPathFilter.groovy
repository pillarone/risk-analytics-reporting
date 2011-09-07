package org.pillarone.riskanalytics.reporting.gira

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SuffixPathFilter implements IPathFilter {

    List<String> suffixes

    SuffixPathFilter(List<String> suffixes) {
        this.suffixes = suffixes
    }

    boolean accept(String path) {
        for (String suffix: suffixes) {
            if (path.endsWith(":" + suffix)) {
                return true
            }

        }
        return false
    }
}

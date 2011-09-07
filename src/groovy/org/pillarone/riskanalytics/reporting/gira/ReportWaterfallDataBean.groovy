package org.pillarone.riskanalytics.reporting.gira

class ReportWaterfallDataBean implements Comparable  {

    double value
    String line

    public int compareTo(Object o) {
        value - o.value
    }
}
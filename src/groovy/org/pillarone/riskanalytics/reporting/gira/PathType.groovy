package org.pillarone.riskanalytics.reporting.gira


public enum PathType {

    CLAIMSGENERATORS, LINESOFBUSINESS, REINSURANCE

    String getDispalyName() {
        switch (this) {
            case CLAIMSGENERATORS: return "Claims Generators"
            case LINESOFBUSINESS: return "Line of Business"
            case REINSURANCE: return "Reinsurance"
        }
    }
}
package org.pillarone.riskanalytics.reporting.gira


public enum PathType {

    CLAIMSGENERATORS, SEGMENTS, REINSURANCE

    String getDispalyName() {
        switch (this) {
            case CLAIMSGENERATORS: return "Claims Generators"
            case SEGMENTS: return "Segments"
            case REINSURANCE: return "Reinsurance"
        }
    }
}
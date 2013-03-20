package org.pillarone.riskanalytics.reporting.gira

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public interface IPathFilter {

    public boolean accept(String path)

    public List<String> beginningOfPath()

}
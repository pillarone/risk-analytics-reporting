package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.report.ReportFactory
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO


class ReportTests extends GroovyTestCase {

    void setUp() {
        FileImportService.importModelsIfNeeded(['GIRA'])
    }

    void testCreateReport() {
        Simulation simulation = new Simulation("simulation")
        simulation.modelClass = GIRAModel
        simulation.parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName("Developed Claims"))
        simulation.template = ModellingItemFactory.getResultConfiguration(ResultConfigurationDAO.findByName("Aggregate Gross Claims without Index Collection"))
        simulation.periodCount = 1
        simulation.numberOfIterations = 100
        simulation.save()

//        todo(sku): not working on server due to missing fonts, https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1858
//        ReportFactory.createPDFReport(new GIRAReportModel(), new ModellingItemReportData(simulation))
    }
}

package org.pillarone.riskanalytics.reporting.gira

import org.pillarone.riskanalytics.core.report.ReportFactory
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.report.impl.ModellingItemReportData
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.reporting.gira.reports.UnderwritingReportModel
import org.pillarone.riskanalytics.core.report.IReportData


class ReportTests extends GroovyTestCase {

    void setUp() {
        FileImportService.importModelsIfNeeded(['GIRA'])
    }

    void testCreateGIRAReport() {
        Simulation simulation = new Simulation("simulation")
        simulation.modelClass = GIRAModel
        simulation.parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName("CapitalEagle NP+ALL50"))
        simulation.template = ModellingItemFactory.getResultConfiguration(ResultConfigurationDAO.findByName("Details, Aggregated, CY Split"))
        simulation.periodCount = 1
        simulation.numberOfIterations = 100
        simulation.save()

        ReportFactory.createPDFReport(new GIRAReportModel(), new ModellingItemReportData(simulation))
    }

    void testCreateUnderwritingReport() {
//        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName("Multi Company"))
//
//        IReportData reportData = new ModellingItemReportData(parameterization)
//        byte[] report = ReportFactory.createPDFReport(new UnderwritingReportModel(), reportData)
//        // todo(sku): ask msp how to modify functionality in CreateReportAction.saveReport
//        use FileOutputStream
//        saveReport(report, reportData)
    }

}

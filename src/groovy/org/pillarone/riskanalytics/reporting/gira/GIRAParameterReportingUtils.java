package org.pillarone.riskanalytics.reporting.gira;

import models.gira.GIRAModel;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.model.Model;
import org.pillarone.riskanalytics.core.parameterization.ParameterApplicator;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.item.Parameterization;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution;
import org.pillarone.riskanalytics.reporting.gira.databeans.ClaimsGeneratorBean;
import org.pillarone.riskanalytics.reporting.gira.databeans.ReinsuranceContractBean;
import org.pillarone.riskanalytics.reporting.gira.databeans.SegmentBean;
import org.pillarone.riskanalytics.reporting.gira.databeans.UnderwritingInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GIRAParameterReportingUtils {

    public static List<UnderwritingInfoBean> getUnderwritingInformation(GIRAModel model) {
        ArrayList<UnderwritingInfoBean> underwritingInformationBeans = new ArrayList<UnderwritingInfoBean>();
        List<Component> underwritingSegments = model.getUnderwritingSegments().getComponentList();

        for (Component underwritingSegment : underwritingSegments) {
            RiskBands riskBands = (RiskBands) underwritingSegment;

            TableMultiDimensionalParameter tableUWInfo = riskBands.getParmUnderwritingInformation();
            int tableRowCount = tableUWInfo.getValueRowCount();
            String segmentName = riskBands.getNormalizedName();
            int columnIndexMaxSumInsured = tableUWInfo.getColumnIndex(RiskBands.MAXIMUM_SUM_INSURED);
            int columnIndexAverageSumInsured = tableUWInfo.getColumnIndex(RiskBands.AVERAGE_SUM_INSURED);
            int columnIndexPremium = tableUWInfo.getColumnIndex(RiskBands.PREMIUM);
            int columnIndexNumberOfPolicies = tableUWInfo.getColumnIndex(RiskBands.NUMBER_OF_POLICIES);

            for (int row = tableUWInfo.getTitleColumnCount(); row < tableRowCount; row++) {
                double premium = InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexPremium));
                double numberOfPolicies = InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexNumberOfPolicies));
                double maxSumInsured = InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexMaxSumInsured));
                double averageSumInsured = InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexAverageSumInsured));

                UnderwritingInfoBean bean = new UnderwritingInfoBean(segmentName, premium, numberOfPolicies, maxSumInsured, averageSumInsured);
                underwritingInformationBeans.add(bean);
            }
        }
        return underwritingInformationBeans;
    }

    public static List<ClaimsGeneratorBean> getClaimsGenerators(GIRAModel model) {
        ArrayList<ClaimsGeneratorBean> claimsGeneratorBeans = new ArrayList<ClaimsGeneratorBean>();
        List<Component> claimsGenerators = model.getClaimsGenerators().getComponentList();

        for (Component component : claimsGenerators) {
            ClaimsGenerator claimsGenerator = (ClaimsGenerator) component;

            ClaimType claimType = claimsGenerator.getParmClaimsModel().claimType();

                RandomDistribution severityDistribution = (RandomDistribution) claimsGenerator.getParmClaimsModel().getParameters().get("claimsSizeDistribution");
                List<String> parameterNames = new ArrayList<String>(severityDistribution.getParameters().keySet());
                List<Number> parameterValues = new ArrayList<Number>(severityDistribution.getParameters().values());
                ClaimsGeneratorBean bean = new ClaimsGeneratorBean(
                        claimsGenerator.getNormalizedName(),
                        severityDistribution.getType().getTypeName(),
                        parameterNames.get(0),
                        parameterValues.get(0).toString(),
                        claimType.toString()
                );
                if (parameterNames.size() > 1) {
                    bean.setSeverityDistributionParam2(parameterNames.get(1));
                    bean.setSeverityDistributionValue2(parameterValues.get(1).toString());
                }
                if (parameterNames.size() > 2) {
                    bean.setSeverityDistributionParam3(parameterNames.get(2));
                    bean.setSeverityDistributionValue3(parameterValues.get(2).toString());
                }
                claimsGeneratorBeans.add(bean);
            if (!claimType.equals(ClaimType.ATTRITIONAL)) {
                RandomFrequencyDistribution frequencyDistribution = (RandomFrequencyDistribution) claimsGenerator.getParmClaimsModel().getParameters().get("frequencyDistribution");
                bean.setFrequencyDistribution(frequencyDistribution.getType().getTypeName());
                List<String> freqParameterNames = new ArrayList<String>(frequencyDistribution.getParameters().keySet());
                List<Number> freqParameterValues = new ArrayList<Number>(frequencyDistribution.getParameters().values());
                bean.setFrequencyDistributionParam1(freqParameterNames.get(0));
                bean.setFrequencyDistributionValue1(freqParameterValues.get(0).toString());
                if (freqParameterNames.size() > 1) {
                    bean.setFrequencyDistributionParam1(freqParameterNames.get(1));
                    bean.setFrequencyDistributionValue1(freqParameterValues.get(1).toString());
                }
                if (freqParameterNames.size() > 2) {
                    bean.setFrequencyDistributionParam1(freqParameterNames.get(2));
                    bean.setFrequencyDistributionValue1(freqParameterValues.get(2).toString());
                }
            }

        }
        return claimsGeneratorBeans;
    }

    public static List<ReinsuranceContractBean> getReinsuranceContracts(GIRAModel model) {
        return null;
    }

    public static List<SegmentBean> getSegments(GIRAModel model) {
        return null;
    }

    // todo(sku): move to ReportUtils in core plugin, discuss with msp
    public static void initModelForParameterReporting(Model model, Parameterization parameterization) {
        model.init();

        ParameterApplicator applicator = new ParameterApplicator();
        applicator.setModel(model);
        parameterization.load(true);
        applicator.setParameterization(parameterization);
        applicator.init();
        applicator.applyParameterForPeriod(0);
    }
}

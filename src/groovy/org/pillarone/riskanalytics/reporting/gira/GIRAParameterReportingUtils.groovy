package org.pillarone.riskanalytics.reporting.gira


import models.gira.GIRAModel
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterApplicator
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution
import org.pillarone.riskanalytics.reporting.gira.databeans.ClaimsGeneratorBean
import org.pillarone.riskanalytics.reporting.gira.databeans.ReinsuranceContractBean
import org.pillarone.riskanalytics.reporting.gira.databeans.SegmentBean
import org.pillarone.riskanalytics.reporting.gira.databeans.UnderwritingInfoBean

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GIRAParameterReportingUtils {

    public static List<UnderwritingInfoBean> getUnderwritingInformation(GIRAModel model) {
        ArrayList<UnderwritingInfoBean> underwritingInformationBeans = new ArrayList<UnderwritingInfoBean>()
        List<Component> underwritingSegments = model.underwritingSegments.componentList

        for (Component underwritingSegment : underwritingSegments) {
            RiskBands riskBands = (RiskBands) underwritingSegment

            TableMultiDimensionalParameter tableUWInfo = riskBands.parmUnderwritingInformation
            int tableRowCount = tableUWInfo.valueRowCount
            String segmentName = riskBands.normalizedName
            int columnIndexMaxSumInsured = tableUWInfo.getColumnIndex(RiskBands.MAXIMUM_SUM_INSURED)
            int columnIndexAverageSumInsured = tableUWInfo.getColumnIndex(RiskBands.AVERAGE_SUM_INSURED)
            int columnIndexPremium = tableUWInfo.getColumnIndex(RiskBands.PREMIUM)
            int columnIndexNumberOfPolicies = tableUWInfo.getColumnIndex(RiskBands.NUMBER_OF_POLICIES)
            Double premium = 0
            Double numberOfPolicies = 0
            Double maxSumInsured = 0
            Double averageSumInsured = 0
            for (int row = tableUWInfo.getTitleRowCount(); row <= tableRowCount; row++) {
                premium += InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexPremium))
                numberOfPolicies += InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexNumberOfPolicies))
                maxSumInsured += InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexMaxSumInsured))
                averageSumInsured += InputFormatConverter.getDouble(tableUWInfo.getValueAt(row, columnIndexAverageSumInsured))
                // some of the sums do not make much of sense now ...
            }
            UnderwritingInfoBean bean = new UnderwritingInfoBean(
                        underwritingSegmentName: segmentName,
                        premium: premium.toString(),
                        numberOfPolicies: numberOfPolicies.toString(),
                        maxSumInsured: maxSumInsured.toString(),
                        averageSumInsured: averageSumInsured.toString())
            underwritingInformationBeans << bean
        }
        // todo(rpa): here could a Bean for Total UW info follow -- or do we have a better solution?

        Collections.sort(underwritingInformationBeans)
        underwritingInformationBeans
    }

    public static List<ClaimsGeneratorBean> getClaimsGenerators(GIRAModel model) {
        ArrayList<ClaimsGeneratorBean> claimsGeneratorBeans = new ArrayList<ClaimsGeneratorBean>()
        List<Component> claimsGenerators = model.claimsGenerators.componentList

        for (Component component : claimsGenerators) {
            ClaimsGenerator claimsGenerator = (ClaimsGenerator) component

            ClaimType claimType = claimsGenerator.parmClaimsModel.claimType()

            RandomDistribution severityDistribution = (RandomDistribution) claimsGenerator.parmClaimsModel.parameters['claimsSizeDistribution']
            List<String> parameterNames = new ArrayList<String>(severityDistribution.parameters.keySet())
            List<Number> parameterValues = new ArrayList<Number>(severityDistribution.parameters.values())
            ClaimsGeneratorBean bean = new ClaimsGeneratorBean(
                    perilName: claimsGenerator.normalizedName,
                    severityDistribution: severityDistribution.getType().getTypeName(),
                    severityDistributionParam1: parameterNames[0],
                    severityDistributionValue1: parameterValues[0].toString(),
                    claimType: claimsGenerator.parmClaimsModel.type.toString(),
                    severityDistributionModification: claimsGenerator.parmClaimsModel.claimsSizeModification.type.toString()
            )
            if (parameterNames.size() > 1) {
                bean.severityDistributionParam2 = parameterNames[1]
                bean.severityDistributionValue2 = parameterValues[1].toString()
            }
            if (parameterNames.size() > 2) {
                bean.severityDistributionParam3 = parameterNames.get(2)
                bean.severityDistributionValue3 = parameterValues.get(2).toString()
            }
            if (!claimType.equals(ClaimType.ATTRITIONAL)) {
                RandomFrequencyDistribution frequencyDistribution = (RandomFrequencyDistribution) claimsGenerator.parmClaimsModel.parameters['frequencyDistribution']
                bean.setFrequencyDistribution(frequencyDistribution.getType().getTypeName())
                List<String> freqParameterNames = new ArrayList<String>(frequencyDistribution.parameters.keySet())
                List<Number> freqParameterValues = new ArrayList<Number>(frequencyDistribution.parameters.values())
                bean.frequencyDistributionParam1 = freqParameterNames[0]
                bean.frequencyDistributionValue1 = freqParameterValues[0].toString()
                if (freqParameterNames.size() > 1) {
                    bean.frequencyDistributionParam1 = freqParameterNames[1]
                    bean.frequencyDistributionValue1 = freqParameterValues[1].toString()
                }
                if (freqParameterNames.size() > 2) {
                    bean.frequencyDistributionParam1 = freqParameterNames[2]
                    bean.frequencyDistributionValue1 = freqParameterValues[2].toString()
                }
            }
            claimsGeneratorBeans << bean
        }
        Collections.sort(claimsGeneratorBeans)
        claimsGeneratorBeans
    }

    public static List<ReinsuranceContractBean> getReinsuranceContracts(GIRAModel model) {
        null
    }

    public static List<SegmentBean> getSegments(GIRAModel model) {
        null
    }

    // todo(sku): move to ReportUtils in core plugin, discuss with msp
    public static void initModelForParameterReporting(Model model, Parameterization parameterization) {
        model.init()

        ParameterApplicator applicator = new ParameterApplicator()
        applicator.setModel(model)
        parameterization.load(true)
        applicator.setParameterization(parameterization)
        applicator.init()
        applicator.applyParameterForPeriod(0)
    }
}

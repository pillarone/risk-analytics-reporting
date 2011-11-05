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
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GIRAParameterReportingUtils {

    public static Map<String, UnderwritingInfoBean> getUnderwritingInformation(GIRAModel model) {
        SortedMap<String, UnderwritingInfoBean> underwritingInformationBeans = new TreeMap<String, UnderwritingInfoBean>()
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
            underwritingInformationBeans[segmentName] = bean
        }
        // todo(rpa): here could a Bean for Total UW info follow -- or do we have a better solution?
        underwritingInformationBeans
    }

    public static Map<String, ClaimsGeneratorBean> getClaimsGeneratorBeans(GIRAModel model) {
        SortedMap<String, ClaimsGeneratorBean> claimsGeneratorBeans = new TreeMap<String, ClaimsGeneratorBean>()
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
            if (bean.severityDistributionModification != 'none') {
                DistributionModified severityModification = (DistributionModified) claimsGenerator.parmClaimsModel.parameters['claimsSizeModification']
                List<String> parameterModificationNames = new ArrayList<String>(severityModification.parameters.keySet())
                List<Number> parameterModificationValues = new ArrayList<Number>(severityModification.parameters.values())

                bean.severityDistributionModificationParam1 = parameterModificationNames[0]
                bean.severityDistributionModificationParam2 = parameterModificationNames[1]
                bean.severityDistributionModificationValue1 = parameterModificationValues[0]
                bean.severityDistributionModificationValue2 = parameterModificationValues[1]
            }
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
            claimsGeneratorBeans[bean.perilName] = bean
        }
        claimsGeneratorBeans
    }

    public static List<SegmentBean> getSegements(GIRAModel model, Simulation simulation, ResultPathParser parser) {
        Map<String, ClaimsGeneratorBean> claimsGeneratorBeans = getClaimsGeneratorBeans(model)
        Map<String, UnderwritingInfoBean> underwritingInfoBeans = getUnderwritingInformation(model)

        ArrayList<SegmentBean> segmentBeans = new ArrayList<SegmentBean>()
        List<Component> segments = model.segments.componentList
        Map<String, JRMapCollectionDataSource> resultsPerSegment = getSegmentResults(simulation, parser)

        for (Component component : segments) {
            Segment segment = (Segment) component
            SegmentBean segmentBean = new SegmentBean(segmentName: segment.normalizedName)
            int portionColumnIndex = segment.parmClaimsPortions.getColumnIndex('Portion')
            int perilColumnIndex = segment.parmClaimsPortions.getColumnIndex('Claims Generator')
            for (int row = segment.parmClaimsPortions.titleRowCount; row < segment.parmClaimsPortions.rowCount; row++) {
                Double portion = InputFormatConverter.getDouble(segment.parmClaimsPortions.getValueAt(row, portionColumnIndex))
                String peril = segment.parmClaimsPortions.getValueAt(row, perilColumnIndex)
                ClaimsGeneratorBean claimsGeneratorBean = claimsGeneratorBeans[peril]
                claimsGeneratorBean.relevantPortion = GiraReportHelper.formatPercentage(portion)
                segmentBean.portionPerClaimsGenerator.put(claimsGeneratorBean, portion)
            }
            portionColumnIndex = segment.parmUnderwritingPortions.getColumnIndex('Portion')
            int riskBandColumnIndex = segment.parmUnderwritingPortions.getColumnIndex('Underwriting')
            for (int row = segment.parmUnderwritingPortions.titleRowCount; row < segment.parmUnderwritingPortions.rowCount; row++) {
                Double portion = InputFormatConverter.getDouble(segment.parmClaimsPortions.getValueAt(row, portionColumnIndex))
                String riskBand = segment.parmClaimsPortions.getValueAt(row, riskBandColumnIndex)
                UnderwritingInfoBean underwritingInfoBean = underwritingInfoBeans[riskBand]
                segmentBean.portionPerUnderwritingInfo.put(underwritingInfoBean, portion)
            }
            segmentBean.results = resultsPerSegment[segmentBean.segmentName]
            segmentBeans << segmentBean
        }
        segmentBeans
    }

    public static Map<String, JRMapCollectionDataSource> getSegmentResults(Simulation simulation, ResultPathParser parser) {
        Map<String, JRMapCollectionDataSource> segmentResults = [:]
        // loop over segments
        for (List<List<String>> componentPaths in getResultPaths(parser).values()) {
            List segments = new SegmentResultDataSourceFactory(simulation, parser).getSegmentResultDataSource(componentPaths);
            for (List segment : segments) {
                segmentResults[segment[0].segmentName] = new JRMapCollectionDataSource([['segmentResult' : new JRMapCollectionDataSource(segment)]])
            }
        }
        segmentResults
    }

    public static List<ReinsuranceContractBean> getReinsuranceContracts(GIRAModel model) {
        ArrayList<ReinsuranceContractBean> reinsuranceContractBeans = new ArrayList<ReinsuranceContractBean>()
        List<Component> reinsuranceContracts = model.reinsuranceContracts.componentList

        for (Component component : reinsuranceContracts) {
            ReinsuranceContract reinsuranceContract = (ReinsuranceContract) component

//            ClaimType claimType = claimsGenerator.parmClaimsModel.claimType()

            ReinsuranceContractBean bean = new ReinsuranceContractBean(
                    contractName: reinsuranceContract.normalizedName,
                    contractType: reinsuranceContract.parmContractStrategy.type.toString()
            )
            reinsuranceContractBeans << bean
        }
        reinsuranceContractBeans
    }

    /**
     * @param parser
     * @return PathType relates to the component (claims generator, reinsurance contract, ...)
     *          the nested list contains all out channels (inner list) per component (outer list)
     */
    public static Map<PathType, List<List<ResultAccessorInformation>>> getResultPaths(ResultPathParser parser) {
        Map result = [:]
        IPathFilter segmentsFilter = PathFilter.getFilter(parser.getComponentPath(PathType.SEGMENTS),
            ['outUnderwritingInfoGross', 'outClaimsGross'])
        GIRAReportUtils.addList(result, PathType.SEGMENTS, parser.getComponentPaths(PathType.SEGMENTS, segmentsFilter))
        result[PathType.SEGMENTS] = ResultPathParser.mergeSplittedPathsWithMainList(result[PathType.SEGMENTS], 'claimsGenerators', 'GIRA:segments', 2)
        result
    }
}

package org.pillarone.riskanalytics.reporting.gira

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitPerSourceCollectingModeStrategy
import org.pillarone.riskanalytics.core.components.ComponentUtils


class ResultPathParser {

    static String CLAIMS_GENERATORS = "claimsGenerators"
    static String SEGMENTS = "segments"
    static String REINSURANCE = "reinsuranceContracts"
    static String CLAIMS = "outClaims"
    static String CLAIMS_NET = "outClaimsNet"
    static String CLAIMS_GROSS = "outClaimsGross"
    static String CLAIMS_CEDED = "outClaimsCeded"
    static List CLAIMS_SUFFIX_LIST = [CLAIMS]
    static List REINSURANCE_CHART_SUFFIX_LIST = [CLAIMS_GROSS, CLAIMS_NET]
    static List REINSURANCE_TABLE_SUFFIX_LIST = [CLAIMS_GROSS, CLAIMS_NET, CLAIMS_CEDED]

    String modelName
    List<String> paths
    Map<PathType, List> cachedMap = [:]

    public ResultPathParser(String modelName, List paths) {
        this.modelName = modelName
        this.paths = paths
    }

    String getComponentPath(PathType pathType) {
        switch (pathType) {
            case PathType.CLAIMSGENERATORS: return modelName + ":" + CLAIMS_GENERATORS
            case PathType.SEGMENTS: return modelName + ":" + SEGMENTS
            case PathType.REINSURANCE: return modelName + ":" + REINSURANCE
        }
        return null
    }

    List<List<String>> getComponentPaths(PathType pathType, IPathFilter filter) {
        if (!cachedMap[pathType]) {
            paths.sort()
            List foundPaths = []
            Map<String, Set> map = [:]
            for (String path: paths) {
                if (filter.accept(path)) {
                    String key = path.substring(0, path.lastIndexOf(":"))
                    Set items = map[key]
                    if (!items) items = [] as Set
                    items << path
                    map[key] = items
                }
            }

            map.values().each {Set items ->
                foundPaths << (items as List)
            }
            cachedMap[pathType] = foundPaths
        }
        return cachedMap[pathType]
    }

    /**
     * Searches for nested lists containing elements containing splittedPathElement. They are merged into the list
     * containing the shorter path if the path element at sameElement is the same.
     * @param result
     * @param splittedPathElement
     * @param pathStart
     * @param sameElement
     */
    static List<List<ResultAccessorInformation>> mergeSplittedPathsWithMainList(List<List<String>> result,
                                                                                String splittedPathElement, String pathStart,
                                                                                int sameElement) {
        List<List<ResultAccessorInformation>> mergedResult = []
        ListMultimap<String, ResultAccessorInformation> pathsForSameElement = ArrayListMultimap.create()
        String colon = ':'
        for (List<String> pathList: result) {
            for (String path: pathList) {
                String samePath = path.split(colon)[sameElement]
                String collector
                String descriptor
                if (path.contains(splittedPathElement)) {
                    collector = AggregateSplitPerSourceCollectingModeStrategy.IDENTIFIER
                    descriptor = ComponentUtils.getNormalizedName(path.split(colon)[-2])
                }
                else {
                    collector = AggregatedCollectingModeStrategy.IDENTIFIER
                    descriptor = ComponentUtils.getNormalizedName(samePath)
                }
                List<String> fieldNames = []
                if (path.contains('Underwriting')) {
                    fieldNames << 'premiumWritten'
                }
                else if (path.contains('Claims')) {
                    fieldNames << 'ultimate'
                }
                pathsForSameElement.put(samePath, new ResultAccessorInformation(path: path, collector: collector,
                        fields: fieldNames, descriptor: descriptor))
            }
        }
        for (List<String> paths : pathsForSameElement.asMap().values()) {
            mergedResult << paths
        }
        mergedResult
    }

    List<String> appyFilter(List<String> paths) {
        if (paths.size() == 0) return []
        List<String> results = []
        IPathFilter filter = new SuffixPathFilter(getAllowedSuffixes(getPathType(paths[0])))
        for (String path: paths) {
            if (filter.accept(path))
                results << path
        }
        return results
    }

    /**
     * @param paths
     * @param pathType
     * @return paths extended with the field
     */
    List<String> getPathsByPathType(List<List<String>> paths, PathType pathType) {
        List<String> suffixPaths = []
        String suffix = PathType.CLAIMSGENERATORS == pathType ? CLAIMS : CLAIMS_CEDED
        for (List<String> componentPaths: paths) {
            suffixPaths.addAll(componentPaths.findAll { it.endsWith(suffix)})
        }
        return suffixPaths.sort()
    }

    PathType getPathType(String path) {
        if (path.startsWith(modelName + ":" + CLAIMS_GENERATORS)) return PathType.CLAIMSGENERATORS
        if (path.startsWith(modelName + ":" + SEGMENTS)) return PathType.SEGMENTS
        if (path.startsWith(modelName + ":" + REINSURANCE)) return PathType.REINSURANCE
        return null
    }

    boolean isParentPath(String path) {
        def nodes = path.split(":")
        if (!nodes || nodes.size() < 3) return false
        return nodes[2].startsWith("out")
    }

    public List getAllowedSuffixes(PathType pathType) {
        switch (pathType) {
            case PathType.CLAIMSGENERATORS: return CLAIMS_SUFFIX_LIST
            case PathType.REINSURANCE: return REINSURANCE_CHART_SUFFIX_LIST
        }
    }
}

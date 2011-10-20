package org.pillarone.riskanalytics.reporting.gira


class ResultPathParser {

    static String CLAIMS_GENERATORS = "claimsGenerators"
    static String LINE_OF_BUSINESS = "linesOfBusiness"
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
            case PathType.LINESOFBUSINESS: return modelName + ":" + LINE_OF_BUSINESS
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
        if (path.startsWith(modelName + ":" + LINE_OF_BUSINESS)) return PathType.LINESOFBUSINESS
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

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {
    inherits ("global") { // inherit Grails' default dependencies
        excludes "grails-docs" // itext library of grails is outdated.
    }
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()

        mavenCentral()
        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
        mavenRepo("https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public-snapshot/") {
            updatePolicy System.getProperty('snapshotUpdatePolicy') ?: 'daily'
        }
        mavenRepo "http://repo.spring.io/milestone/" //needed for spring-security-core 2.0-rc2 plugin
    }

    String ulcVersion = "7.2.0.5"

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:3.6.10.3"
        runtime ":joda-time:0.5"
        runtime ":release:3.0.1"
        runtime ":quartz:1.0.1"
        runtime ":spring-security-core:2.0-RC2"
        runtime ":tomcat:7.0.42"

        compile "com.canoo:ulc:${ulcVersion}"
        runtime ("org.pillarone:pillar-one-ulc-extensions:1.3") { transitive = false }

        test ":code-coverage:1.2.7"
        compile ":excel-import:1.0.0"


        if (appName == "risk-analytics-reporting") {
            runtime "org.pillarone:risk-analytics-core:1.9-SNAPSHOT"
            runtime ("org.pillarone:risk-analytics-application:1.9-SNAPSHOT") { transitive = false }
            runtime ("org.pillarone:risk-analytics-pc-cashflow:1.9-SNAPSHOT") { transitive = false }
            runtime ("org.pillarone:risk-analytics-commons:1.9-SNAPSHOT") { transitive = false }
        }
    }
    dependencies {
        compile (group:'com.lowagie', name:'itext', version:'2.1.7');
        compile (group:'org.apache.poi', name:'poi', version:'3.9');
        compile (group:'org.apache.poi', name:'poi', version:'3.9');
        compile (group:'org.apache.poi', name:'poi-ooxml', version:'3.9') {
            excludes 'xmlbeans'
        }
    }
}

grails.project.dependency.distribution = {
    String password = ""
    String user = ""
    String scpUrl = ""
    try {
        Properties properties = new Properties()
        String version = new GroovyClassLoader().loadClass('RiskAnalyticsReportingGrailsPlugin').newInstance().version
        properties.load(new File("${userHome}/deployInfo.properties").newInputStream())
        user = properties.get("user")
        password = properties.get("password")

        if (version?.endsWith('-SNAPSHOT')){
            scpUrl = properties.get("urlSnapshot")
        }else {
            scpUrl = properties.get("url")
        }
    	remoteRepository(id: "pillarone", url: scpUrl) {
        	authentication username: user, password: password
    	}
    } catch (Throwable t) {
    }
}

coverage {
    exclusions = [
            'models/**',
            '**/*Test*',
            '**/com/energizedwork/grails/plugins/jodatime/**',
            '**/grails/util/**',
            '**/org/codehaus/**',
            '**/org/grails/**',
            '**GrailsPlugin**',
            '**TagLib**'
    ]

}
reportFolders = [new File("./src/java/reports/gira")]


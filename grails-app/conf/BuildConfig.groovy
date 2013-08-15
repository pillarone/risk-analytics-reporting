grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()

        mavenCentral()
        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
    }

    String ulcVersion = "ria-suite-2013-2"

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:2.2.1"
        runtime ":joda-time:0.5"
        runtime ":maven-publisher:0.7.5", {
            excludes "groovy"
        }
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:1.2.7.3"
        runtime ":tomcat:2.2.1"

        compile "com.canoo:ulc:${ulcVersion}"
        runtime "org.pillarone:pillar-one-ulc-extensions:1.0"

        test ":code-coverage:1.2.4"
        compile ":excel-import:1.0.0"


        if (appName == "risk-analytics-reporting") {
            runtime "org.pillarone:risk-analytics-core:1.8-a4"
            runtime ("org.pillarone:risk-analytics-application:1.8-a2") { transitive = false }
            runtime ("org.pillarone:risk-analytics-pc-cashflow:1.7") { transitive = false }
            runtime ("org.pillarone:risk-analytics-commons:1.7") { transitive = false }
        }
    }
}

grails.project.dependency.distribution = {
    String password = ""
    String user = ""
    String scpUrl = ""
    try {
        Properties properties = new Properties()
        properties.load(new File("${userHome}/deployInfo.properties").newInputStream())

        user = properties.get("user")
        password = properties.get("password")
        scpUrl = properties.get("url")
    } catch (Throwable t) {
    }
    remoteRepository(id: "pillarone", url: scpUrl) {
        authentication username: user, password: password
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


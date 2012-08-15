grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()

        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
    }

    String ulcVersion = "ria-suite-u5"

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:2.0.1"
        runtime ":joda-time:0.5"
        runtime(":release:2.0.3") { excludes "groovy" }
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:1.2.7.3"
        runtime ":tomcat:2.0.1"

        compile "com.canoo:ulc:${ulcVersion}"
        runtime "org.pillarone:pillar-one-ulc-extensions:0.3"

        test ":code-coverage:1.2.4"
        runtime(":excel-import:0.9.6") { excludes "xmlbeans" }


        if (appName == "risk-analytics-reporting") {
            runtime "org.pillarone:risk-analytics-core:1.6-ALPHA-4.12-2.1.0"
            runtime("org.pillarone:risk-analytics-application:1.6-ALPHA-4.4-2.1.0") { transitive = false }
            runtime("org.pillarone:risk-analytics-pc-cashflow:0.4.10-2.1.0") { transitive = false }
            runtime("org.pillarone:risk-analytics-commons:0.4.6-2.1.0") { transitive = false }
        }
    }

    dependencies {
        compile group: 'canoo', name: 'ulc-applet-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-base-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-base-trusted', version: ulcVersion
        compile group: 'canoo', name: 'ulc-jnlp-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-servlet-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-standalone-client', version: ulcVersion
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


pipeline {
    agent any

    stages {
        stage('Run mvn test') {
            steps {
                sh 'mvn package'
            }
        }
        stage ('OWASP Dependency-Check Vulnerabilities') {
            steps {
                dependencyCheck additionalArguments: '''
                    -o "./"
                    -s "./"
                    --nvdApiKey "2efa4c6c-9503-44fe-9dc4-dc351711ad13"
                    -f "ALL"
                    --prettyPrint''', odcInstallation: 'OWASP-DC'

                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
            }
        }
    }
}

pipeline {
    agent any

    stages {
        stage('Run mvn test') {
            steps {
                sh 'mvn package'
            }
        }
        stage('dependencyTrackPublisher') {
            steps {
                dependencyCheck additionalArguments: '', nvdCredentialsId: 'NVD-ID', odcInstallation: 'OWASP-DC'
                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
            }
        }
    }
}

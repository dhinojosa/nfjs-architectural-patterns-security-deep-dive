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
                dependencyTrackPublisher artifact: 'target/bom.xml', projectId: 'd6fccdbb-fdf6-441b-830e-3f3225603e58', synchronous: true
            }
        }
    }
}

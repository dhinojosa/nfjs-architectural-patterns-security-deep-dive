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
                try {
                    dependencyTrackPublisher artifact: 'target/bom.xml', projectId: 'a65ea72b-5b77-40c5-8b19-fb83525f40eb', synchronous: true
                } catch (e) {
                    echo 'failed'
                }
            }
        }
    }
}

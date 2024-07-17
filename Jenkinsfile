pipeline {
    agent any

    stages {
        stage('Run mvn test') {
            steps {
                sh 'mvn package'
            }
        }
    }
}

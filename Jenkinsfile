pipeline {
    agent any
    
    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['SmokeTests', 'RegressionTests', 'DataDrivenTests'],
            description: 'Select test suite to run'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome'],
            description: 'Select browser for testing'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['staging', 'dev', 'prod'],
            description: 'Select environment'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m'
        CI_ENVIRONMENT = 'true'
    }
    
    tools {
        maven 'Maven-3.9.0'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "Checked out code from repository"
            }
        }
        
        stage('Setup Environment') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            echo "Setting up Linux environment"
                            # Install Chrome if not present
                            if ! command -v google-chrome &> /dev/null; then
                                wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
                                echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" | sudo tee /etc/apt/sources.list.d/google-chrome.list
                                sudo apt-get update
                                sudo apt-get install -y google-chrome-stable
                            fi
                            google-chrome --version
                        '''
                    } else {
                        bat '''
                            echo "Setting up Windows environment"
                            echo "Chrome should be pre-installed on Windows agents"
                        '''
                    }
                }
            }
        }
        
        stage('Generate Test Data') {
            steps {
                script {
                    try {
                        if (isUnix()) {
                            sh 'mvn compile exec:java -Dexec.mainClass="org.example.utils.ExcelDataGenerator"'
                        } else {
                            bat 'mvn compile exec:java -Dexec.mainClass="org.example.utils.ExcelDataGenerator"'
                        }
                        echo "Test data generated successfully"
                    } catch (Exception e) {
                        echo "Warning: Could not generate test data - ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    def testCommand = "mvn clean test -Dtest.suite=${params.TEST_SUITE} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS} -Dci.environment=true"
                    
                    try {
                        if (isUnix()) {
                            sh testCommand
                        } else {
                            bat testCommand
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'UNSTABLE'
                        echo "Tests completed with failures: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                script {
                    // Archive test results
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    
                    // Archive ExtentReports
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'test-output/ExtentReports',
                        reportFiles: '*.html',
                        reportName: 'ExtentReports',
                        reportTitles: 'Automation Test Report'
                    ])
                }
            }
        }
    }
    
    post {
        always {
            // Archive artifacts
            archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo '✅ Pipeline executed successfully!'
            script {
                if (env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'master') {
                    // Send success notification
                    emailext (
                        subject: "✅ Automation Tests Passed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        body: """
                        <h2>Test Execution Successful</h2>
                        <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                        <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                        <p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>
                        <p><strong>Browser:</strong> ${params.BROWSER}</p>
                        <p><strong>Environment:</strong> ${params.ENVIRONMENT}</p>
                        <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        """,
                        to: "${env.CHANGE_AUTHOR_EMAIL}",
                        mimeType: 'text/html'
                    )
                }
            }
        }
        
        failure {
            echo '❌ Pipeline failed!'
            script {
                // Send failure notification
                emailext (
                    subject: "❌ Automation Tests Failed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                    <h2>Test Execution Failed</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                    <p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>
                    <p><strong>Browser:</strong> ${params.BROWSER}</p>
                    <p><strong>Environment:</strong> ${params.ENVIRONMENT}</p>
                    <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p>Please check the console output and test reports for details.</p>
                    """,
                    to: "${env.CHANGE_AUTHOR_EMAIL}",
                    mimeType: 'text/html'
                )
            }
        }
        
        unstable {
            echo '⚠️ Pipeline completed with test failures!'
            script {
                // Send unstable notification
                emailext (
                    subject: "⚠️ Automation Tests Unstable - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                    <h2>Test Execution Completed with Failures</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                    <p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>
                    <p><strong>Browser:</strong> ${params.BROWSER}</p>
                    <p><strong>Environment:</strong> ${params.ENVIRONMENT}</p>
                    <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p>Some tests failed. Please check the test reports for details.</p>
                    """,
                    to: "${env.CHANGE_AUTHOR_EMAIL}",
                    mimeType: 'text/html'
                )
            }
        }
    }
}

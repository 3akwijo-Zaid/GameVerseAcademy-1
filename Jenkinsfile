pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-21'
    }

    environment {
        APP_NAME       = 'gameverseacademy'
        APP_VERSION    = '0.0.1-SNAPSHOT'
        DOCKER_IMAGE   = "${APP_NAME}:${BUILD_NUMBER}"

        NEXUS_URL          = 'http://nexus:8081'
        NEXUS_DOCKER_REG   = 'localhost:8082'          // Docker registry on Nexus (host port)
        NEXUS_CREDS        = credentials('nexus-credentials')

        SONAR_HOST     = 'http://localhost:9000'

        EMAIL_RECIPIENT = 'team@esi.ac.ma'
    }

    triggers {
        // Poll every 5 min as fallback if GitHub webhook is not configured
        pollSCM('H/5 * * * *')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        // ─────────────────────────────────────────
        stage('Checkout') {
        // ─────────────────────────────────────────
            steps {
                checkout scm
                echo "Branch: ${env.GIT_BRANCH} | Commit: ${env.GIT_COMMIT?.take(8)}"
            }
        }

        // ─────────────────────────────────────────
        stage('Compile') {
        // ─────────────────────────────────────────
            steps {
                sh 'mvn clean compile --no-transfer-progress'
            }
        }

        // ─────────────────────────────────────────
        stage('Test & Coverage') {
        // ─────────────────────────────────────────
            steps {
                sh 'mvn test --no-transfer-progress'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco(
                        execPattern:   'target/jacoco.exec',
                        classPattern:  'target/classes',
                        sourcePattern: 'src/main/java'
                    )
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Code Quality') {
        // ─────────────────────────────────────────
            parallel {

                stage('SonarQube') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh """
                                mvn sonar:sonar \
                                  --no-transfer-progress \
                                  -Dsonar.projectKey=GameVerseAcademy \
                                  -Dsonar.projectName='GameVerseAcademy' \
                                  -Dsonar.host.url=${SONAR_HOST} \
                                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                            """
                        }
                    }
                }

                stage('Checkstyle & PMD') {
                    steps {
                        sh 'mvn checkstyle:check pmd:check --no-transfer-progress || true'
                    }
                    post {
                        always {
                            recordIssues(
                                tools: [
                                    checkStyle(pattern: 'target/checkstyle-result.xml'),
                                    pmdParser(pattern: 'target/pmd.xml')
                                ]
                            )
                        }
                    }
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Quality Gate') {
        // ─────────────────────────────────────────
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Package') {
        // ─────────────────────────────────────────
            steps {
                sh 'mvn package -DskipTests --no-transfer-progress'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // ─────────────────────────────────────────
        stage('Deploy to Nexus') {
        // ─────────────────────────────────────────
            steps {
                sh """
                    mvn deploy -DskipTests --no-transfer-progress \
                      -Dnexus.url=${NEXUS_URL} \
                      -s /var/jenkins_home/.m2/settings.xml
                """
            }
        }

        // ─────────────────────────────────────────
        stage('Docker Build') {
        // ─────────────────────────────────────────
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        // ─────────────────────────────────────────
        stage('Trivy Security Scan') {
        // ─────────────────────────────────────────
            steps {
                sh """
                    docker run --rm \
                      -v /var/run/docker.sock:/var/run/docker.sock \
                      aquasec/trivy image \
                        --exit-code 0 \
                        --severity HIGH,CRITICAL \
                        --format table \
                        ${DOCKER_IMAGE}
                """
                // exit-code 0 → reports but doesn't fail; change to 1 to enforce hard gate
            }
        }

        // ─────────────────────────────────────────
        stage('Push to Nexus Docker Registry') {
        // ─────────────────────────────────────────
            steps {
                sh """
                    docker login ${NEXUS_DOCKER_REG} \
                      -u ${NEXUS_CREDS_USR} \
                      -p ${NEXUS_CREDS_PSW}

                    docker tag ${DOCKER_IMAGE} \
                        ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}

                    docker push ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}
                """
            }
        }

        // ─────────────────────────────────────────
        stage('Deploy to k3s via Helm') {
        // ─────────────────────────────────────────
            steps {
                sh """
                    helm upgrade --install ${APP_NAME} ./charts/${APP_NAME} \
                      --namespace production \
                      --create-namespace \
                      --set image.repository=${NEXUS_DOCKER_REG}/${APP_NAME} \
                      --set image.tag=${BUILD_NUMBER} \
                      --wait --timeout 3m
                """
            }
        }
    }

    // ─────────────────────────────────────────────
    post {
    // ─────────────────────────────────────────────
        success {
            echo "BUILD OK — ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }

        failure {
            echo "BUILD ECHOUE — ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }

        unstable {
            echo "BUILD INSTABLE — ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }

        always {
            deleteDir()
        }
    }
}

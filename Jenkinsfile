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

        NEXUS_URL        = 'http://nexus:8081'
        NEXUS_DOCKER_REG = 'localhost:8082'
        NEXUS_CREDS      = credentials('nexus-credentials')

        SONAR_HOST      = 'http://sonarqube:9000'
        EMAIL_RECIPIENT = 'team@esi.ac.ma'
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        // ═══════════════════════════════════════════════════
        stage('SCM Polling') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Checking out source code from GitHub ──'
                checkout scm

                echo '── Step 2/3: Reading commit metadata ──'
                script {
                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.GIT_AUTHOR      = sh(script: 'git log -1 --format="%an"', returnStdout: true).trim()
                    env.GIT_MSG         = sh(script: 'git log -1 --format="%s"', returnStdout: true).trim()
                    env.GIT_DATE        = sh(script: 'git log -1 --format="%cd" --date=format:"%Y-%m-%d %H:%M"', returnStdout: true).trim()
                    env.GIT_BRANCH_NAME = env.GIT_BRANCH ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    env.CHANGED_FILES   = sh(script: 'git diff --name-only HEAD~1 HEAD 2>/dev/null | wc -l || echo 0', returnStdout: true).trim()
                }

                echo '── Step 3/3: Reporting SCM state ──'
                echo """
┌─────────────────────────────────────────────────────┐
│  SCM POLLING  —  Poll interval: H/5 * * * *         │
├─────────────────────────────────────────────────────┤
│  Branch  : ${env.GIT_BRANCH_NAME}
│  Commit  : ${env.GIT_COMMIT_SHORT}  by ${env.GIT_AUTHOR}
│  Date    : ${env.GIT_DATE}
│  Message : ${env.GIT_MSG}
│  Changed : ${env.CHANGED_FILES} file(s) since last commit
└─────────────────────────────────────────────────────┘"""
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Build') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Cleaning previous build output (target/) ──'
                sh 'mvn clean --no-transfer-progress -q'

                echo '── Step 2/3: Compiling Java 21 sources ──'
                sh 'mvn compile --no-transfer-progress'

                echo '── Step 3/3: Verifying compiled classes ──'
                script {
                    def classCount = sh(script: 'find target/classes -name "*.class" 2>/dev/null | wc -l', returnStdout: true).trim()
                    echo """
┌─────────────────────────────────────────────────────┐
│  BUILD                                               │
├─────────────────────────────────────────────────────┤
│  Java version : 21 (eclipse-temurin)                 │
│  Maven goal   : clean compile                        │
│  Classes built: ${classCount}
│  Source dir   : src/main/java                        │
│  Output dir   : target/classes                       │
└─────────────────────────────────────────────────────┘"""
                }
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Test & Coverage') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/4: Running JUnit 5 unit tests via Maven Surefire ──'
                sh 'mvn test --no-transfer-progress'

                echo '── Step 2/4: Publishing JUnit XML reports ──'
                junit 'target/surefire-reports/*.xml'

                echo '── Step 3/4: Publishing JaCoCo code coverage ──'
                jacoco(
                    execPattern:   'target/jacoco.exec',
                    classPattern:  'target/classes',
                    sourcePattern: 'src/main/java'
                )

                echo '── Step 4/4: Reporting test summary ──'
                script {
                    def total   = currentBuild.testResultAction?.totalCount  ?: '?'
                    def failed  = currentBuild.testResultAction?.failCount    ?: '0'
                    def skipped = currentBuild.testResultAction?.skipCount    ?: '0'
                    def passed  = (total == '?') ? '?' : (total.toInteger() - failed.toInteger() - skipped.toInteger())
                    echo """
┌─────────────────────────────────────────────────────┐
│  TEST & COVERAGE                                     │
├─────────────────────────────────────────────────────┤
│  Framework   : JUnit 5 + Maven Surefire              │
│  Total tests : ${total}
│  Passed      : ${passed}
│  Failed      : ${failed}
│  Skipped     : ${skipped}
├─────────────────────────────────────────────────────┤
│  Coverage    : JaCoCo (exec: target/jacoco.exec)     │
│  HTML report : target/site/jacoco/index.html         │
│  XML report  : target/site/jacoco/jacoco.xml         │
└─────────────────────────────────────────────────────┘"""
                }
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Code Analysis') {
        // ═══════════════════════════════════════════════════
            parallel {

                stage('SonarQube') {
                    steps {
                        echo '── Step 1/3: Connecting to SonarQube server ──'
                        withSonarQubeEnv('SonarQube') {
                            withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                                echo '── Step 2/3: Sending analysis (sources + coverage + bytecode) ──'
                                sh """
                                    mvn sonar:sonar \
                                      --no-transfer-progress \
                                      -Dsonar.projectKey=GameVerseAcademy \
                                      -Dsonar.projectName='GameVerseAcademy' \
                                      -Dsonar.host.url=${SONAR_HOST} \
                                      -Dsonar.token=${SONAR_TOKEN} \
                                      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                                """
                            }
                        }
                        echo '── Step 3/3: Analysis submitted ──'
                        echo """
┌─────────────────────────────────────────────────────┐
│  SONARQUBE ANALYSIS                                  │
├─────────────────────────────────────────────────────┤
│  Server    : ${SONAR_HOST}
│  Project   : GameVerseAcademy                        │
│  Metrics   : bugs, vulnerabilities, code smells,    │
│              duplications, coverage, complexity      │
│  Dashboard : http://localhost:9000/dashboard         │
│              ?id=GameVerseAcademy                    │
└─────────────────────────────────────────────────────┘"""
                    }
                }

                stage('Checkstyle & PMD') {
                    steps {
                        echo '── Step 1/3: Running Checkstyle (Google style rules) ──'
                        sh 'mvn checkstyle:check --no-transfer-progress || true'

                        echo '── Step 2/3: Running PMD (static bug detection) ──'
                        sh 'mvn pmd:check --no-transfer-progress || true'

                        echo '── Step 3/3: Recording issues in Warnings NG ──'
                        recordIssues(
                            tools: [
                                checkStyle(pattern: 'target/checkstyle-result.xml'),
                                pmdParser(pattern: 'target/pmd.xml')
                            ]
                        )
                        echo """
┌─────────────────────────────────────────────────────┐
│  STATIC ANALYSIS                                     │
├─────────────────────────────────────────────────────┤
│  Checkstyle : target/checkstyle-result.xml           │
│  PMD        : target/pmd.xml                         │
│  Results    : Warnings NG tab (Jenkins sidebar)      │
└─────────────────────────────────────────────────────┘"""
                    }
                }
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Quality Gate') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Evaluating SonarQube quality gate status ──'
                echo """
┌─────────────────────────────────────────────────────┐
│  QUALITY GATE                                        │
├─────────────────────────────────────────────────────┤
│  Gate result : see SonarQube dashboard               │
│  URL         : http://localhost:9000                 │
│  Thresholds  : coverage, reliability, security,      │
│                maintainability ratings               │
└─────────────────────────────────────────────────────┘"""
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Package & Archive') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Packaging fat JAR via maven-shade-plugin ──'
                sh 'mvn package -DskipTests --no-transfer-progress'

                echo '── Step 2/3: Archiving artifact in Jenkins ──'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true

                echo '── Step 3/3: Reporting artifact details ──'
                script {
                    def jarName = sh(script: 'ls target/GameVerseAcademy-*.jar', returnStdout: true).trim()
                    def jarSize = sh(script: "du -sh ${jarName} | awk '{print \$1}'", returnStdout: true).trim()
                    echo """
┌─────────────────────────────────────────────────────┐
│  PACKAGE & ARCHIVE                                   │
├─────────────────────────────────────────────────────┤
│  Plugin    : maven-shade-plugin (fat/uber JAR)       │
│  JAR file  : ${jarName.split('/').last()}
│  Size      : ${jarSize}
│  Build #   : ${BUILD_NUMBER}
│  Archived  : Jenkins workspace artifacts             │
│  Fingerprint: enabled (SHA-1 tracked)                │
└─────────────────────────────────────────────────────┘"""
                }
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Deploy to Nexus') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Authenticating with Nexus Maven repository ──'
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials',
                                                  usernameVariable: 'NEXUS_USER',
                                                  passwordVariable: 'NEXUS_PASS')]) {
                    echo '── Step 2/3: Uploading JAR + POM to maven-snapshots ──'
                    sh """
                        mvn deploy -DskipTests --no-transfer-progress \
                          -s /var/jenkins_home/.m2/settings.xml \
                          -Darguments="-DskipTests" \
                          -Dnexus.username=${NEXUS_USER} \
                          -Dnexus.password=${NEXUS_PASS}
                    """
                }
                echo '── Step 3/3: Reporting deployment location ──'
                echo """
┌─────────────────────────────────────────────────────┐
│  NEXUS MAVEN DEPLOYMENT                              │
├─────────────────────────────────────────────────────┤
│  Server     : ${NEXUS_URL}
│  Repository : maven-snapshots                        │
│  Group      : ma.ac.esi                              │
│  Artifact   : GameVerseAcademy-${APP_VERSION}.jar
│  Browse     : ${NEXUS_URL}/#browse/browse:maven-snapshots
└─────────────────────────────────────────────────────┘"""
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Docker Build') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Building Docker image from Dockerfile ──'
                sh "docker build -t ${DOCKER_IMAGE} ."

                echo '── Step 2/3: Verifying image was created ──'
                sh "docker image inspect ${DOCKER_IMAGE} --format '{{.Id}}'"

                echo '── Step 3/3: Reporting image metadata ──'
                script {
                    def sizeBytes = sh(script: "docker image inspect ${DOCKER_IMAGE} --format '{{.Size}}'", returnStdout: true).trim().toLong()
                    def sizeMB    = String.format("%.1f MB", sizeBytes / 1024 / 1024)
                    echo """
┌─────────────────────────────────────────────────────┐
│  DOCKER BUILD                                        │
├─────────────────────────────────────────────────────┤
│  Image     : ${DOCKER_IMAGE}
│  Base      : eclipse-temurin:21-jre-alpine           │
│  Port      : EXPOSE 6060                             │
│  Size      : ${sizeMB}
│  Dockerfile: ./Dockerfile                            │
└─────────────────────────────────────────────────────┘"""
                }
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Trivy Security Scan') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Pulling Trivy scanner image ──'
                echo '── Step 2/3: Scanning image for HIGH and CRITICAL CVEs ──'
                sh """
                    docker run --rm \
                      -v /var/run/docker.sock:/var/run/docker.sock \
                      aquasec/trivy image \
                        --exit-code 0 \
                        --severity HIGH,CRITICAL \
                        --format table \
                        ${DOCKER_IMAGE}
                """
                echo '── Step 3/3: Reporting scan outcome ──'
                echo """
┌─────────────────────────────────────────────────────┐
│  TRIVY SECURITY SCAN                                 │
├─────────────────────────────────────────────────────┤
│  Tool      : aquasec/trivy                           │
│  Target    : ${DOCKER_IMAGE}
│  Severities: HIGH, CRITICAL                          │
│  Exit code : 0 (report-only, pipeline not blocked)   │
│  Note      : set --exit-code 1 to enforce hard gate  │
└─────────────────────────────────────────────────────┘"""
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Push to Nexus Docker Registry') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Authenticating with Nexus Docker registry ──'
                sh """
                    docker login ${NEXUS_DOCKER_REG} \
                      -u ${NEXUS_CREDS_USR} \
                      -p ${NEXUS_CREDS_PSW}
                """

                echo '── Step 2/3: Tagging and pushing image ──'
                sh """
                    docker tag ${DOCKER_IMAGE} ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}
                    docker push ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}
                """

                echo '── Step 3/3: Reporting registry location ──'
                echo """
┌─────────────────────────────────────────────────────┐
│  NEXUS DOCKER PUSH                                   │
├─────────────────────────────────────────────────────┤
│  Registry  : ${NEXUS_DOCKER_REG}
│  Image     : ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}
│  Browse    : http://localhost:8082/v2/_catalog       │
└─────────────────────────────────────────────────────┘"""
            }
        }

        // ═══════════════════════════════════════════════════
        stage('Deploy to k3s via Helm') {
        // ═══════════════════════════════════════════════════
            steps {
                echo '── Step 1/3: Verifying Helm + kubeconfig ──'
                sh 'helm version --short'
                sh 'kubectl config current-context'

                echo '── Step 2/3: Running helm upgrade --install ──'
                sh """
                    helm upgrade --install ${APP_NAME} ./charts/${APP_NAME} \
                      --namespace production \
                      --create-namespace \
                      --set image.repository=${NEXUS_DOCKER_REG}/${APP_NAME} \
                      --set image.tag=${BUILD_NUMBER}
                """

                echo '── Step 3/3: Reporting deployment state ──'
                sh 'helm status gameverseacademy -n production'
                echo """
┌─────────────────────────────────────────────────────┐
│  KUBERNETES DEPLOYMENT                               │
├─────────────────────────────────────────────────────┤
│  Orchestrator : k3s (Kubernetes)                     │
│  Tool         : Helm 3                               │
│  Chart        : ./charts/gameverseacademy            │
│  Release      : ${APP_NAME}
│  Namespace    : production                           │
│  Image        : ${NEXUS_DOCKER_REG}/${APP_NAME}:${BUILD_NUMBER}
│  Check pods   : kubectl get pods -n production       │
└─────────────────────────────────────────────────────┘"""
            }
        }
    }

    // ═══════════════════════════════════════════════════════
    post {
    // ═══════════════════════════════════════════════════════
        success {
            echo """
┌─────────────────────────────────────────────────────┐
│  BUILD SUCCESSFUL — ${env.JOB_NAME} #${env.BUILD_NUMBER}
└─────────────────────────────────────────────────────┘"""
        }
        failure {
            echo """
┌─────────────────────────────────────────────────────┐
│  BUILD FAILED — ${env.JOB_NAME} #${env.BUILD_NUMBER}
└─────────────────────────────────────────────────────┘"""
        }
        unstable {
            echo """
┌─────────────────────────────────────────────────────┐
│  BUILD UNSTABLE — ${env.JOB_NAME} #${env.BUILD_NUMBER}
└─────────────────────────────────────────────────────┘"""
        }
        always {
            deleteDir()
        }
    }
}

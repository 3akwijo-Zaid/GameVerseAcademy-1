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
        NEXUS_DOCKER_REG   = 'localhost:8082'
        NEXUS_CREDS        = credentials('nexus-credentials')

        SONAR_HOST     = 'http://sonarqube:9000'

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

        // ─────────────────────────────────────────
        stage('SCM Polling') {
        // ─────────────────────────────────────────
            steps {
                checkout scm
                script {
                    def commitHash    = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    def commitAuthor  = sh(script: 'git log -1 --format="%an <%ae>"', returnStdout: true).trim()
                    def commitMessage = sh(script: 'git log -1 --format="%s"', returnStdout: true).trim()
                    def commitDate    = sh(script: 'git log -1 --format="%cd" --date=format:"%Y-%m-%d %H:%M:%S"', returnStdout: true).trim()
                    def changedFiles  = sh(script: 'git diff --name-only HEAD~1 HEAD 2>/dev/null | head -20 || echo "(first commit)"', returnStdout: true).trim()
                    def branch        = env.GIT_BRANCH ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()

                    echo """
╔══════════════════════════════════════════════════════╗
║              SCM — Source Control Info               ║
╠══════════════════════════════════════════════════════╣
║  Trigger   : Poll SCM (every 5 min) / Webhook        ║
║  Branch    : ${branch.padRight(38)}║
║  Commit    : ${commitHash.padRight(38)}║
║  Author    : ${commitAuthor.take(38).padRight(38)}║
║  Date      : ${commitDate.padRight(38)}║
╠══════════════════════════════════════════════════════╣
║  Message   : ${commitMessage.take(38).padRight(38)}║
╠══════════════════════════════════════════════════════╣
║  Changed files:                                      ║
${changedFiles.split('\n').collect { "║    • ${it.take(48).padRight(48)}║" }.join('\n')}
╚══════════════════════════════════════════════════════╝"""
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Compile') {
        // ─────────────────────────────────────────
            steps {
                echo '── Compiling sources with Maven (Java 21) ──'
                sh 'mvn clean compile --no-transfer-progress'
                echo '── Compilation successful ──'
            }
        }

        // ─────────────────────────────────────────
        stage('Test & Coverage') {
        // ─────────────────────────────────────────
            steps {
                echo '── Running unit tests + JaCoCo coverage ──'
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
                    script {
                        def testResults = ''
                        try {
                            def passed  = currentBuild.testResultAction?.totalCount ?: '?'
                            def failed  = currentBuild.testResultAction?.failCount   ?: '0'
                            def skipped = currentBuild.testResultAction?.skipCount   ?: '0'
                            testResults = "Passed: ${passed}  |  Failed: ${failed}  |  Skipped: ${skipped}"
                        } catch (e) {
                            testResults = 'See Test Results tab for details'
                        }
                        echo """
╔══════════════════════════════════════════════════════╗
║                   Test Results                       ║
╠══════════════════════════════════════════════════════╣
║  ${testResults.padRight(52)}║
║  Coverage report : target/site/jacoco/index.html     ║
║  Surefire reports: target/surefire-reports/          ║
╚══════════════════════════════════════════════════════╝"""
                    }
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Code Quality') {
        // ─────────────────────────────────────────
            parallel {

                stage('SonarQube') {
                    steps {
                        echo '── Sending analysis to SonarQube ──'
                        withSonarQubeEnv('SonarQube') {
                            withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
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
                        echo "── SonarQube dashboard: http://localhost:9000/dashboard?id=GameVerseAcademy ──"
                    }
                }

                stage('Checkstyle & PMD') {
                    steps {
                        echo '── Running Checkstyle + PMD static analysis ──'
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
                            echo '── Static analysis issues recorded — see Warnings NG tab ──'
                        }
                    }
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Quality Gate') {
        // ─────────────────────────────────────────
            steps {
                echo """
╔══════════════════════════════════════════════════════╗
║                   Quality Gate                       ║
╠══════════════════════════════════════════════════════╣
║  SonarQube : http://localhost:9000                   ║
║  Project   : GameVerseAcademy                        ║
║  Status    : See SonarQube dashboard for gate result ║
╚══════════════════════════════════════════════════════╝"""
            }
        }

        // ─────────────────────────────────────────
        stage('Package') {
        // ─────────────────────────────────────────
            steps {
                echo '── Packaging fat JAR (maven-shade-plugin) ──'
                sh 'mvn package -DskipTests --no-transfer-progress'
                script {
                    def jarFile = sh(script: 'ls -lh target/GameVerseAcademy-*.jar | awk \'{print $5, $9}\'', returnStdout: true).trim()
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    echo """
╔══════════════════════════════════════════════════════╗
║                 Artifact Archived                    ║
╠══════════════════════════════════════════════════════╣
║  JAR       : ${jarFile.take(38).padRight(38)}║
║  Archived  : target/*.jar (fingerprinted)            ║
║  Build     : #${BUILD_NUMBER.padRight(37)}║
╚══════════════════════════════════════════════════════╝"""
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Deploy to Nexus') {
        // ─────────────────────────────────────────
            steps {
                echo '── Deploying artifact to Nexus maven-snapshots ──'
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials',
                                                  usernameVariable: 'NEXUS_USER',
                                                  passwordVariable: 'NEXUS_PASS')]) {
                    sh """
                        mvn deploy -DskipTests --no-transfer-progress \
                          -s /var/jenkins_home/.m2/settings.xml \
                          -Darguments="-DskipTests" \
                          -Dnexus.username=${NEXUS_USER} \
                          -Dnexus.password=${NEXUS_PASS}
                    """
                }
                echo "── Artifact available at: ${NEXUS_URL}/#browse/browse:maven-snapshots ──"
            }
        }

        // ─────────────────────────────────────────
        stage('Docker Build') {
        // ─────────────────────────────────────────
            steps {
                echo "── Building Docker image: ${DOCKER_IMAGE} ──"
                sh "docker build -t ${DOCKER_IMAGE} ."
                script {
                    def imageSize = sh(script: "docker image inspect ${DOCKER_IMAGE} --format '{{.Size}}' | awk '{printf \"%.1f MB\", \$1/1024/1024}'", returnStdout: true).trim()
                    echo """
╔══════════════════════════════════════════════════════╗
║                  Docker Image Built                  ║
╠══════════════════════════════════════════════════════╣
║  Image     : ${DOCKER_IMAGE.padRight(38)}║
║  Base      : eclipse-temurin:21-jre-alpine           ║
║  Size      : ${imageSize.padRight(38)}║
║  Port      : 6060                                    ║
╚══════════════════════════════════════════════════════╝"""
                }
            }
        }

        // ─────────────────────────────────────────
        stage('Trivy Security Scan') {
        // ─────────────────────────────────────────
            steps {
                echo '── Scanning Docker image for vulnerabilities (HIGH/CRITICAL) ──'
                sh """
                    docker run --rm \
                      -v /var/run/docker.sock:/var/run/docker.sock \
                      aquasec/trivy image \
                        --exit-code 0 \
                        --severity HIGH,CRITICAL \
                        --format table \
                        ${DOCKER_IMAGE}
                """
                echo '── Trivy scan complete — exit-code 0 (report only, no gate) ──'
            }
        }

        // ─────────────────────────────────────────
        stage('Push to Nexus Docker Registry') {
        // ─────────────────────────────────────────
            steps {
                echo "── Pushing ${DOCKER_IMAGE} to Nexus Docker registry ──"
                sh """
                    docker login ${NEXUS_DOCKER_REG} \
                      -u ${NEXUS_CREDS_USR} \
                      -p ${NEXUS_CREDS_PSW}

                    docker tag ${DOCKER_IMAGE} \
                        ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}

                    docker push ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}
                """
                echo "── Image pushed: ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE} ──"
            }
        }

        // ─────────────────────────────────────────
        stage('Deploy to k3s via Helm') {
        // ─────────────────────────────────────────
            steps {
                echo '── Deploying to Kubernetes (k3s) via Helm ──'
                sh """
                    helm upgrade --install ${APP_NAME} ./charts/${APP_NAME} \
                      --namespace production \
                      --create-namespace \
                      --set image.repository=${NEXUS_DOCKER_REG}/${APP_NAME} \
                      --set image.tag=${BUILD_NUMBER}
                """
                echo """
╔══════════════════════════════════════════════════════╗
║              Deployed to Kubernetes                  ║
╠══════════════════════════════════════════════════════╣
║  Namespace : production                              ║
║  Release   : ${APP_NAME.padRight(38)}║
║  Image     : ${(NEXUS_DOCKER_REG + '/' + APP_NAME + ':' + BUILD_NUMBER).take(38).padRight(38)}║
║  Check     : kubectl get pods -n production          ║
╚══════════════════════════════════════════════════════╝"""
            }
        }
    }

    // ─────────────────────────────────────────────
    post {
    // ─────────────────────────────────────────────
        success {
            echo """
╔══════════════════════════════════════════════════════╗
║  BUILD OK — ${(env.JOB_NAME + ' #' + env.BUILD_NUMBER).take(40).padRight(40)}║
╚══════════════════════════════════════════════════════╝"""
        }

        failure {
            echo """
╔══════════════════════════════════════════════════════╗
║  BUILD FAILED — ${(env.JOB_NAME + ' #' + env.BUILD_NUMBER).take(37).padRight(37)}║
╚══════════════════════════════════════════════════════╝"""
        }

        unstable {
            echo """
╔══════════════════════════════════════════════════════╗
║  BUILD UNSTABLE — ${(env.JOB_NAME + ' #' + env.BUILD_NUMBER).take(35).padRight(35)}║
╚══════════════════════════════════════════════════════╝"""
        }

        always {
            deleteDir()
        }
    }
}

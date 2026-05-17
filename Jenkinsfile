pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-21'
    }

    environment {
        APP_NAME         = 'gameverseacademy'
        APP_VERSION      = '0.0.1-SNAPSHOT'
        DOCKER_IMAGE     = "${APP_NAME}:${BUILD_NUMBER}"
        NEXUS_URL        = 'http://nexus:8081'
        NEXUS_DOCKER_REG = 'localhost:8082'
        NEXUS_CREDS      = credentials('nexus-credentials')
        SONAR_HOST       = 'http://sonarqube:9000'
        EMAIL_RECIPIENT  = 'team@esi.ac.ma'
    }

    triggers { pollSCM('H/5 * * * *') }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        // ══════════════════════════════════════════════════════
        stage('SCM Polling') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[SCM] Trigger: pollSCM every 5 min — checking for new commits on GitHub'
                echo '[SCM] Checkout: cloning/updating workspace from configured repository'
                checkout scm
                echo '[SCM] Reading commit metadata: branch, hash, author, message, changed files'
                script {
                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.GIT_AUTHOR       = sh(script: 'git log -1 --format="%an"', returnStdout: true).trim()
                    env.GIT_MSG          = sh(script: 'git log -1 --format="%s"', returnStdout: true).trim()
                    env.GIT_DATE         = sh(script: 'git log -1 --format="%cd" --date=format:"%Y-%m-%d %H:%M"', returnStdout: true).trim()
                    env.GIT_BRANCH_NAME  = env.GIT_BRANCH ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    env.CHANGED_COUNT    = sh(script: 'git diff --name-only HEAD~1 HEAD 2>/dev/null | wc -l || echo 0', returnStdout: true).trim()
                }
                echo "[SCM] Branch   : ${env.GIT_BRANCH_NAME}"
                echo "[SCM] Commit   : ${env.GIT_COMMIT_SHORT}  |  Author: ${env.GIT_AUTHOR}  |  Date: ${env.GIT_DATE}"
                echo "[SCM] Message  : ${env.GIT_MSG}"
                echo "[SCM] Changed  : ${env.CHANGED_COUNT} file(s) since previous commit"
                sh 'git diff --name-only HEAD~1 HEAD 2>/dev/null || true'
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Build') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[Build] Goal: mvn clean — deleting target/ directory'
                sh 'mvn clean --no-transfer-progress -q'
                echo '[Build] Goal: mvn compile — compiling src/main/java → target/classes'
                sh 'mvn compile --no-transfer-progress'
                echo '[Build] Counting compiled .class files in target/classes'
                script {
                    def count = sh(script: 'find target/classes -name "*.class" | wc -l', returnStdout: true).trim()
                    echo "[Build] Result: ${count} classes compiled  |  JDK: 21 (eclipse-temurin)  |  Output: target/classes"
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Test & Coverage') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[Test] Goal: mvn test — running unit tests via Maven Surefire plugin'
                echo '[Test] Sources: src/test/java  |  Reports: target/surefire-reports/*.xml'
                sh 'mvn test --no-transfer-progress'
                echo '[Test] Publishing JUnit XML reports from target/surefire-reports/'
                junit 'target/surefire-reports/*.xml'
                echo '[Coverage] Publishing JaCoCo report — exec: target/jacoco.exec  |  HTML: target/site/jacoco/index.html  |  XML: target/site/jacoco/jacoco.xml'
                jacoco(
                    execPattern:   'target/jacoco.exec',
                    classPattern:  'target/classes',
                    sourcePattern: 'src/main/java'
                )
                script {
                    def total   = currentBuild.testResultAction?.totalCount ?: '?'
                    def failed  = currentBuild.testResultAction?.failCount   ?: '0'
                    def skipped = currentBuild.testResultAction?.skipCount   ?: '0'
                    def passed  = (total == '?') ? '?' : (total.toInteger() - failed.toInteger() - skipped.toInteger())
                    echo "[Test] Results: ${passed} passed  |  ${failed} failed  |  ${skipped} skipped  |  ${total} total"
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Code Analysis') {
        // ══════════════════════════════════════════════════════
            parallel {

                stage('SonarQube') {
                    steps {
                        echo "[SonarQube] Server: ${SONAR_HOST}  |  Project key: GameVerseAcademy"
                        echo '[SonarQube] Goal: mvn sonar:sonar — analysing sources, tests, coverage and bytecode'
                        echo '[SonarQube] Coverage input: target/site/jacoco/jacoco.xml'
                        echo '[SonarQube] Metrics: bugs, vulnerabilities, code smells, duplications, coverage, complexity'
                        withSonarQubeEnv('SonarQube') {
                            withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                                sh """
                                    mvn sonar:sonar --no-transfer-progress \
                                      -Dsonar.projectKey=GameVerseAcademy \
                                      -Dsonar.projectName='GameVerseAcademy' \
                                      -Dsonar.host.url=${SONAR_HOST} \
                                      -Dsonar.token=${SONAR_TOKEN} \
                                      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                                """
                            }
                        }
                        echo "[SonarQube] Analysis complete — dashboard: ${SONAR_HOST}/dashboard?id=GameVerseAcademy"
                    }
                }

                stage('Checkstyle & PMD') {
                    steps {
                        echo '[Checkstyle] Goal: mvn checkstyle:check — enforcing Google Java Style rules'
                        echo '[Checkstyle] Input: src/main/java  |  Report: target/checkstyle-result.xml'
                        sh 'mvn checkstyle:check --no-transfer-progress || true'
                        echo '[PMD] Goal: mvn pmd:check — detecting bugs, dead code, suboptimal patterns'
                        echo '[PMD] Input: src/main/java  |  Report: target/pmd.xml'
                        sh 'mvn pmd:check --no-transfer-progress || true'
                        echo '[Warnings NG] Recording issues from target/checkstyle-result.xml + target/pmd.xml'
                        recordIssues(tools: [
                            checkStyle(pattern: 'target/checkstyle-result.xml'),
                            pmdParser(pattern: 'target/pmd.xml')
                        ])
                        echo '[Warnings NG] Results visible in Jenkins sidebar → Warnings tab'
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Quality Gate') {
        // ══════════════════════════════════════════════════════
            steps {
                echo "[Quality Gate] Checking SonarQube gate result at ${SONAR_HOST}/dashboard?id=GameVerseAcademy"
                echo '[Quality Gate] Thresholds: coverage ≥ 80%, reliability A, security A, maintainability A'
                echo '[Quality Gate] Status: see SonarQube dashboard — gate enforcement skipped in pipeline'
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Package & Archive') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[Package] Goal: mvn package -DskipTests — building fat JAR via maven-shade-plugin'
                echo '[Package] Input: target/classes  |  Output: target/GameVerseAcademy-' + APP_VERSION + '.jar'
                sh 'mvn package -DskipTests --no-transfer-progress'
                echo '[Archive] Archiving target/*.jar in Jenkins build artifacts (fingerprinted)'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                script {
                    def jar  = sh(script: 'ls target/GameVerseAcademy-*.jar', returnStdout: true).trim()
                    def size = sh(script: "du -sh ${jar} | awk '{print \$1}'", returnStdout: true).trim()
                    echo "[Archive] JAR: ${jar}  |  Size: ${size}  |  Build: #${BUILD_NUMBER}  |  SHA-1 fingerprint: enabled"
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Deploy to Nexus') {
        // ══════════════════════════════════════════════════════
            steps {
                echo "[Nexus] Target repository: ${NEXUS_URL}/repository/maven-snapshots"
                echo '[Nexus] Goal: mvn deploy — uploading JAR + POM + SHA checksums'
                echo '[Nexus] Settings: /var/jenkins_home/.m2/settings.xml  |  Server id: nexus'
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
                echo "[Nexus] Artifact uploaded — browse: ${NEXUS_URL}/#browse/browse:maven-snapshots"
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Docker Build') {
        // ══════════════════════════════════════════════════════
            steps {
                echo "[Docker] Building image: ${DOCKER_IMAGE}  |  Context: ./  |  Dockerfile: ./Dockerfile"
                echo '[Docker] Base image: eclipse-temurin:21-jre-alpine  |  App port: EXPOSE 6060'
                echo '[Docker] COPY target/GameVerseAcademy-*.jar → /app/app.jar'
                sh "docker build -t ${DOCKER_IMAGE} ."
                echo "[Docker] Verifying image exists in local daemon: ${DOCKER_IMAGE}"
                sh "docker image inspect ${DOCKER_IMAGE} --format 'ID={{.Id}} Created={{.Created}}'"
                script {
                    def bytes = sh(script: "docker image inspect ${DOCKER_IMAGE} --format '{{.Size}}'", returnStdout: true).trim().toLong()
                    def mb    = String.format("%.1f MB", bytes / 1024 / 1024)
                    echo "[Docker] Image size: ${mb}  |  Tag: ${DOCKER_IMAGE}"
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Trivy Security Scan') {
        // ══════════════════════════════════════════════════════
            steps {
                echo "[Trivy] Scanner: aquasec/trivy (run via Docker)  |  Target: ${DOCKER_IMAGE}"
                echo '[Trivy] Socket: /var/run/docker.sock (Docker-in-Docker)  |  Severities: HIGH, CRITICAL'
                echo '[Trivy] Output: table format  |  Exit code: 0 (report-only, does not fail pipeline)'
                sh """
                    docker run --rm \
                      -v /var/run/docker.sock:/var/run/docker.sock \
                      aquasec/trivy image \
                        --exit-code 0 \
                        --severity HIGH,CRITICAL \
                        --format table \
                        ${DOCKER_IMAGE}
                """
                echo '[Trivy] Scan complete — change --exit-code to 1 to enforce a hard security gate'
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Push to Nexus Docker Registry') {
        // ══════════════════════════════════════════════════════
            steps {
                echo "[Docker Push] Registry: ${NEXUS_DOCKER_REG}  |  Authenticating with nexus-credentials"
                sh "docker login ${NEXUS_DOCKER_REG} -u ${NEXUS_CREDS_USR} -p ${NEXUS_CREDS_PSW}"
                echo "[Docker Push] Tagging: ${DOCKER_IMAGE} → ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                sh "docker tag ${DOCKER_IMAGE} ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                echo "[Docker Push] Pushing layers to ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                sh "docker push ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                echo "[Docker Push] Image available at: ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}  |  Catalog: http://localhost:8082/v2/_catalog"
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Deploy to k3s via Helm') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[Helm] Verifying Helm version and kubeconfig context'
                sh 'helm version --short'
                sh 'kubectl config current-context'
                echo "[Helm] Chart: ./charts/gameverseacademy  |  Release: ${APP_NAME}  |  Namespace: production"
                echo "[Helm] Image: ${NEXUS_DOCKER_REG}/${APP_NAME}:${BUILD_NUMBER}  |  Strategy: upgrade --install (create if not exists)"
                echo '[Helm] Probes: tcpSocket on port 6060  |  Pull secret: nexus-registry'
                sh """
                    helm upgrade --install ${APP_NAME} ./charts/${APP_NAME} \
                      --namespace production \
                      --create-namespace \
                      --set image.repository=${NEXUS_DOCKER_REG}/${APP_NAME} \
                      --set image.tag=${BUILD_NUMBER}
                """
                echo '[Helm] Checking release status'
                sh 'helm status gameverseacademy -n production'
                echo "[Helm] Deployment applied — verify: kubectl get pods -n production  |  Service: kubectl get svc -n production"
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    post {
    // ══════════════════════════════════════════════════════════
        success  { echo "[Pipeline] BUILD SUCCESSFUL — ${env.JOB_NAME} #${env.BUILD_NUMBER}" }
        failure  { echo "[Pipeline] BUILD FAILED     — ${env.JOB_NAME} #${env.BUILD_NUMBER}" }
        unstable { echo "[Pipeline] BUILD UNSTABLE   — ${env.JOB_NAME} #${env.BUILD_NUMBER}" }
        always   { deleteDir() }
    }
}

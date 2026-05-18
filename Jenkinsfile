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

    triggers {
        githubPush()                   // fires instantly on GitHub push via webhook
        pollSCM('H/5 * * * *')        // fallback poll in case webhook is missed
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        // ══════════════════════════════════════════════════════
        stage('SCM Polling') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[SCM] Trigger  : pollSCM — interval H/5 * * * * (every 5 min)'
                echo '[SCM] Checkout : cloning/updating workspace from GitHub'
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.GIT_AUTHOR       = sh(script: 'git log -1 --format="%an <%ae>"', returnStdout: true).trim()
                    env.GIT_MSG          = sh(script: 'git log -1 --format="%s"', returnStdout: true).trim()
                    env.GIT_DATE         = sh(script: 'git log -1 --format="%cd" --date=format:"%Y-%m-%d %H:%M"', returnStdout: true).trim()
                    env.GIT_BRANCH_NAME  = env.GIT_BRANCH ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    env.CHANGED_COUNT    = sh(script: 'git diff --name-only HEAD~1 HEAD 2>/dev/null | wc -l || echo 0', returnStdout: true).trim()
                }
                echo "[SCM] Branch  : ${env.GIT_BRANCH_NAME}"
                echo "[SCM] Commit  : ${env.GIT_COMMIT_SHORT}  |  Author: ${env.GIT_AUTHOR}  |  Date: ${env.GIT_DATE}"
                echo "[SCM] Message : ${env.GIT_MSG}"
                echo "[SCM] Changed : ${env.CHANGED_COUNT} file(s) since previous commit"
                sh 'git diff --name-status HEAD~1 HEAD 2>/dev/null || true'
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Build') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[Build › Clean]   Goal: mvn clean — deleting target/ directory'
                sh 'mvn clean --no-transfer-progress -q'

                echo '[Build › Compile] Goal: mvn compile'
                echo '[Build › Compile] Input : src/main/java  |  Output: target/classes  |  JDK: 21'
                sh 'mvn compile --no-transfer-progress'

                script {
                    def count = sh(script: 'find target/classes -name "*.class" | wc -l', returnStdout: true).trim()
                    echo "[Build › Result]  ${count} classes compiled in target/classes"
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Test & Coverage') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[Test › Unit Tests] Framework: JUnit 5  |  Plugin: Maven Surefire'
                echo '[Test › Unit Tests] Goal: mvn test  |  Sources: src/test/java  |  Reports: target/surefire-reports/*.xml'
                sh 'mvn test --no-transfer-progress'

                echo '[Test › JUnit]     Publishing XML reports from target/surefire-reports/'
                junit 'target/surefire-reports/*.xml'
                script {
                    try {
                        def tr = currentBuild.rawBuild.getAction(hudson.tasks.junit.TestResultAction.class)
                        if (tr) {
                            echo "[Test › JUnit]     Passed: ${tr.passCount}  |  Failed: ${tr.failCount}  |  Skipped: ${tr.skipCount}  |  Total: ${tr.totalCount}"
                        }
                    } catch (e) {
                        echo '[Test › JUnit]     Results published — see Test Results tab'
                    }
                }

                echo '[Test › JaCoCo]    Exec: target/jacoco.exec  |  Classes: target/classes  |  Sources: src/main/java'
                echo '[Test › JaCoCo]    HTML: target/site/jacoco/index.html  |  XML: target/site/jacoco/jacoco.xml'
                jacoco(
                    execPattern:   'target/jacoco.exec',
                    classPattern:  'target/classes',
                    sourcePattern: 'src/main/java'
                )
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Code Analysis') {
        // ══════════════════════════════════════════════════════
            parallel {

                stage('SonarQube') {
                    stages {
                        stage('SonarQube › Analyse') {
                            steps {
                                echo "[Sonar] Server : ${SONAR_HOST}  |  Project: GameVerseAcademy"
                                echo '[Sonar] Goal   : mvn sonar:sonar'
                                echo '[Sonar] Input  : src/main/java + target/classes + target/site/jacoco/jacoco.xml'
                                echo '[Sonar] Metrics: bugs, vulnerabilities, code smells, duplications, coverage'
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
                            }
                        }
                        stage('SonarQube › Report') {
                            steps {
                                echo "[Sonar] Dashboard: ${SONAR_HOST}/dashboard?id=GameVerseAcademy"
                                echo '[Sonar] Metrics  : bugs, vulnerabilities, code smells, coverage, duplications'
                            }
                        }
                    }
                }

                stage('Static Analysis') {
                    stages {
                        stage('Static › Checkstyle') {
                            steps {
                                echo '[Checkstyle] Goal  : mvn checkstyle:check'
                                echo '[Checkstyle] Rules : Google Java Style'
                                echo '[Checkstyle] Input : src/main/java  |  Report: target/checkstyle-result.xml'
                                sh 'mvn checkstyle:check --no-transfer-progress || true'
                            }
                        }
                        stage('Static › PMD') {
                            steps {
                                echo '[PMD] Goal   : mvn pmd:check'
                                echo '[PMD] Detects: dead code, empty blocks, unused vars, suboptimal patterns'
                                echo '[PMD] Input  : src/main/java  |  Report: target/pmd.xml'
                                sh 'mvn pmd:check --no-transfer-progress || true'
                            }
                        }
                        stage('Static › Record Issues') {
                            steps {
                                echo '[Warnings NG] Sources: target/checkstyle-result.xml + target/pmd.xml'
                                echo '[Warnings NG] Results visible in Jenkins sidebar → Warnings tab'
                                recordIssues(tools: [
                                    checkStyle(pattern: 'target/checkstyle-result.xml'),
                                    pmdParser(pattern: 'target/pmd.xml')
                                ])
                            }
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Quality Gate') {
        // ══════════════════════════════════════════════════════
            steps {
                echo "[Quality Gate] SonarQube: ${SONAR_HOST}/dashboard?id=GameVerseAcademy"
                echo '[Quality Gate] Thresholds: coverage ≥ 80% | reliability A | security A | maintainability A'
                echo '[Quality Gate] Status: see SonarQube dashboard — gate enforcement skipped in pipeline'
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Package & Archive') {
        // ══════════════════════════════════════════════════════
            steps {
                echo '[Package › Build JAR] Goal  : mvn package -DskipTests'
                echo '[Package › Build JAR] Plugin: maven-shade-plugin (uber/fat JAR)'
                echo "[Package › Build JAR] Output: target/GameVerseAcademy-${APP_VERSION}.jar"
                sh 'mvn package -DskipTests --no-transfer-progress'

                echo '[Package › Archive]   Pattern    : target/*.jar → Jenkins build artifacts'
                echo '[Package › Archive]   Fingerprint: SHA-1 hash tracked per build'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                script {
                    def jar  = sh(script: 'ls target/GameVerseAcademy-*.jar', returnStdout: true).trim()
                    def size = sh(script: "du -sh ${jar} | awk '{print \$1}'", returnStdout: true).trim()
                    echo "[Package › Archive]   File: ${jar}  |  Size: ${size}  |  Build: #${BUILD_NUMBER}"
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Publish & Containerise') {
        // ══════════════════════════════════════════════════════
            parallel {

                stage('Deploy to Nexus') {
                    stages {
                        stage('Nexus › Upload') {
                            steps {
                                echo "[Nexus] Server    : ${NEXUS_URL}"
                                echo '[Nexus] Repository: maven-snapshots'
                                echo '[Nexus] Goal      : mvn deploy -DskipTests'
                                echo '[Nexus] Settings  : /var/jenkins_home/.m2/settings.xml  |  Server ID: nexus'
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
                            }
                        }
                        stage('Nexus › Verify') {
                            steps {
                                echo "[Nexus] Artifact uploaded: GameVerseAcademy-${APP_VERSION}.jar"
                                echo "[Nexus] Browse: ${NEXUS_URL}/#browse/browse:maven-snapshots"
                            }
                        }
                    }
                }

                stage('Javadoc') {
                    stages {
                        stage('Javadoc › Generate') {
                            steps {
                                echo '[Javadoc] Goal   : mvn javadoc:javadoc'
                                echo '[Javadoc] Config : show=private  |  doclint=none  |  failOnError=false'
                                echo '[Javadoc] Input  : src/main/java'
                                echo '[Javadoc] Output : target/site/apidocs/'
                                sh 'mvn javadoc:javadoc --no-transfer-progress'
                            }
                        }
                        stage('Javadoc › Publish') {
                            steps {
                                echo '[Javadoc] Publishing HTML report → Jenkins Javadoc tab'
                                javadoc javadocDir: 'target/site/apidocs', keepAll: true
                                echo '[Javadoc] Archiving javadoc JAR: target/*-javadoc.jar'
                                archiveArtifacts artifacts: 'target/*-javadoc.jar', allowEmptyArchive: true, fingerprint: true
                                echo '[Javadoc] Available in Jenkins sidebar → Javadoc tab'
                            }
                        }
                    }
                }

                stage('Docker Pipeline') {
                    stages {
                        stage('Docker › Build') {
                            steps {
                                echo "[Docker] Image     : ${DOCKER_IMAGE}"
                                echo '[Docker] Dockerfile: ./Dockerfile'
                                echo '[Docker] Base      : eclipse-temurin:21-jre-alpine'
                                echo '[Docker] COPY      : target/GameVerseAcademy-*.jar → /app/app.jar'
                                echo '[Docker] EXPOSE    : 6060'
                                sh "docker build -t ${DOCKER_IMAGE} ."
                                script {
                                    def bytes = sh(script: "docker image inspect ${DOCKER_IMAGE} --format '{{.Size}}'", returnStdout: true).trim().toLong()
                                    echo "[Docker] Size: ${String.format('%.1f MB', bytes / 1024 / 1024)}  |  Tag: ${DOCKER_IMAGE}"
                                }
                            }
                        }
                        stage('Docker › Trivy Scan') {
                            steps {
                                echo "[Trivy] Target    : ${DOCKER_IMAGE}"
                                echo '[Trivy] Socket    : /var/run/docker.sock'
                                echo '[Trivy] Severities: HIGH, CRITICAL  |  Format: table  |  Exit code: 0 (report-only)'
                                sh """
                                    docker run --rm \
                                      -v /var/run/docker.sock:/var/run/docker.sock \
                                      aquasec/trivy image \
                                        --exit-code 0 \
                                        --severity HIGH,CRITICAL \
                                        --format table \
                                        ${DOCKER_IMAGE}
                                """
                            }
                        }
                        stage('Docker › Push') {
                            steps {
                                echo "[Docker Push] Registry: ${NEXUS_DOCKER_REG}  |  Auth: nexus-credentials"
                                sh "docker login ${NEXUS_DOCKER_REG} -u ${NEXUS_CREDS_USR} -p ${NEXUS_CREDS_PSW}"
                                echo "[Docker Push] Tag: ${DOCKER_IMAGE} → ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                                sh "docker tag ${DOCKER_IMAGE} ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                                echo "[Docker Push] Pushing to ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                                sh "docker push ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                                echo "[Docker Push] Catalog: http://localhost:8082/v2/_catalog"
                            }
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Deploy to k3s via Helm') {
        // ══════════════════════════════════════════════════════
            stages {
                stage('Helm › Verify Environment') {
                    steps {
                        echo '[Helm] Verifying Helm CLI and kubeconfig'
                        sh 'helm version --short'
                        echo '[kubectl] Kubeconfig: /var/jenkins_home/.kube/config → https://192.168.1.9:6443'
                        sh 'kubectl config current-context'
                    }
                }
                stage('Helm › Deploy') {
                    steps {
                        echo "[Helm] Release   : ${APP_NAME}  |  Namespace: production"
                        echo '[Helm] Chart     : ./charts/gameverseacademy'
                        echo "[Helm] Image     : ${NEXUS_DOCKER_REG}/${APP_NAME}:${BUILD_NUMBER}"
                        echo '[Helm] Strategy  : upgrade --install'
                        echo '[Helm] Pull secret: nexus-registry  |  Probes: tcpSocket port 6060'
                        sh """
                            helm upgrade --install ${APP_NAME} ./charts/${APP_NAME} \
                              --namespace production \
                              --create-namespace \
                              --set image.repository=${NEXUS_DOCKER_REG}/${APP_NAME} \
                              --set image.tag=${BUILD_NUMBER}
                        """
                    }
                }
                stage('Helm › Verify Deployment') {
                    steps {
                        echo '[Helm] Release status:'
                        sh 'helm status gameverseacademy -n production'
                        echo '[kubectl] Pods in production namespace:'
                        sh 'kubectl get pods -n production'
                        echo '[kubectl] Services in production namespace:'
                        sh 'kubectl get svc -n production'
                    }
                }
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

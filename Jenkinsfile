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
            stages {

                stage('Checkout Source Code') {
                    steps {
                        echo '[SCM] Trigger  : pollSCM — interval: H/5 * * * * (every 5 min)'
                        echo '[SCM] Source   : GitHub repository (configured in Jenkins job)'
                        echo '[SCM] Workspace: ${WORKSPACE}'
                        checkout scm
                    }
                }

                stage('Read Commit Metadata') {
                    steps {
                        echo '[SCM] Extracting branch, commit hash, author, date, changed files'
                        script {
                            env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                            env.GIT_AUTHOR       = sh(script: 'git log -1 --format="%an <%ae>"', returnStdout: true).trim()
                            env.GIT_MSG          = sh(script: 'git log -1 --format="%s"', returnStdout: true).trim()
                            env.GIT_DATE         = sh(script: 'git log -1 --format="%cd" --date=format:"%Y-%m-%d %H:%M"', returnStdout: true).trim()
                            env.GIT_BRANCH_NAME  = env.GIT_BRANCH ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                            env.CHANGED_COUNT    = sh(script: 'git diff --name-only HEAD~1 HEAD 2>/dev/null | wc -l || echo 0', returnStdout: true).trim()
                        }
                        echo "[SCM] Branch   : ${env.GIT_BRANCH_NAME}"
                        echo "[SCM] Commit   : ${env.GIT_COMMIT_SHORT}"
                        echo "[SCM] Author   : ${env.GIT_AUTHOR}"
                        echo "[SCM] Date     : ${env.GIT_DATE}"
                        echo "[SCM] Message  : ${env.GIT_MSG}"
                        echo "[SCM] Changed  : ${env.CHANGED_COUNT} file(s)"
                    }
                }

                stage('List Changed Files') {
                    steps {
                        echo '[SCM] Files modified since previous commit:'
                        sh 'git diff --name-status HEAD~1 HEAD 2>/dev/null || echo "(no previous commit)"'
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Build') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Clean Workspace') {
                    steps {
                        echo '[Build] Goal   : mvn clean'
                        echo '[Build] Deletes: target/ directory and all previous build output'
                        sh 'mvn clean --no-transfer-progress -q'
                    }
                }

                stage('Compile Sources') {
                    steps {
                        echo '[Build] Goal   : mvn compile'
                        echo '[Build] Input  : src/main/java'
                        echo '[Build] Output : target/classes'
                        echo '[Build] JDK    : 21 (eclipse-temurin)'
                        sh 'mvn compile --no-transfer-progress'
                    }
                }

                stage('Verify Build Output') {
                    steps {
                        echo '[Build] Counting compiled classes in target/classes'
                        script {
                            def count = sh(script: 'find target/classes -name "*.class" | wc -l', returnStdout: true).trim()
                            echo "[Build] Classes: ${count} .class files produced"
                        }
                        sh 'find target/classes -name "*.class" | sort'
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Test & Coverage') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Run Unit Tests') {
                    steps {
                        echo '[Test] Framework : JUnit 5'
                        echo '[Test] Plugin    : Maven Surefire'
                        echo '[Test] Goal      : mvn test'
                        echo '[Test] Sources   : src/test/java'
                        echo '[Test] Reports   : target/surefire-reports/*.xml'
                        sh 'mvn test --no-transfer-progress'
                    }
                }

                stage('Publish Test Reports') {
                    steps {
                        echo '[Test] Publishing JUnit XML reports → Jenkins Test Results tab'
                        echo '[Test] Pattern: target/surefire-reports/*.xml'
                        junit 'target/surefire-reports/*.xml'
                        script {
                            def total   = currentBuild.testResultAction?.totalCount ?: '?'
                            def failed  = currentBuild.testResultAction?.failCount   ?: '0'
                            def skipped = currentBuild.testResultAction?.skipCount   ?: '0'
                            def passed  = (total == '?') ? '?' : (total.toInteger() - failed.toInteger() - skipped.toInteger())
                            echo "[Test] Passed : ${passed}  |  Failed: ${failed}  |  Skipped: ${skipped}  |  Total: ${total}"
                        }
                    }
                }

                stage('Publish Coverage Report') {
                    steps {
                        echo '[Coverage] Tool   : JaCoCo'
                        echo '[Coverage] Exec   : target/jacoco.exec'
                        echo '[Coverage] Classes: target/classes'
                        echo '[Coverage] Sources: src/main/java'
                        echo '[Coverage] HTML   : target/site/jacoco/index.html'
                        echo '[Coverage] XML    : target/site/jacoco/jacoco.xml'
                        jacoco(
                            execPattern:   'target/jacoco.exec',
                            classPattern:  'target/classes',
                            sourcePattern: 'src/main/java'
                        )
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Code Analysis') {
        // ══════════════════════════════════════════════════════
            parallel {

                stage('SonarQube') {
                    stages {

                        stage('Run SonarQube Analysis') {
                            steps {
                                echo "[Sonar] Server  : ${SONAR_HOST}"
                                echo '[Sonar] Project : GameVerseAcademy'
                                echo '[Sonar] Goal    : mvn sonar:sonar'
                                echo '[Sonar] Input   : src/main/java + target/classes + target/site/jacoco/jacoco.xml'
                                echo '[Sonar] Metrics : bugs, vulnerabilities, code smells, duplications, coverage, complexity'
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

                        stage('Report SonarQube Results') {
                            steps {
                                echo "[Sonar] Analysis submitted to ${SONAR_HOST}"
                                echo "[Sonar] Dashboard: ${SONAR_HOST}/dashboard?id=GameVerseAcademy"
                                echo '[Sonar] Quality Gate result visible in SonarQube UI'
                            }
                        }
                    }
                }

                stage('Checkstyle & PMD') {
                    stages {

                        stage('Run Checkstyle') {
                            steps {
                                echo '[Checkstyle] Goal  : mvn checkstyle:check'
                                echo '[Checkstyle] Rules : Google Java Style'
                                echo '[Checkstyle] Input : src/main/java'
                                echo '[Checkstyle] Report: target/checkstyle-result.xml'
                                sh 'mvn checkstyle:check --no-transfer-progress || true'
                            }
                        }

                        stage('Run PMD') {
                            steps {
                                echo '[PMD] Goal  : mvn pmd:check'
                                echo '[PMD] Detects: dead code, unused variables, empty blocks, suboptimal patterns'
                                echo '[PMD] Input : src/main/java'
                                echo '[PMD] Report: target/pmd.xml'
                                sh 'mvn pmd:check --no-transfer-progress || true'
                            }
                        }

                        stage('Record Issues') {
                            steps {
                                echo '[Warnings NG] Aggregating: target/checkstyle-result.xml + target/pmd.xml'
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
            stages {

                stage('Evaluate Gate') {
                    steps {
                        echo "[Quality Gate] SonarQube server: ${SONAR_HOST}"
                        echo '[Quality Gate] Thresholds: coverage ≥ 80% | reliability A | security A | maintainability A'
                        echo '[Quality Gate] Result: see SonarQube dashboard (gate enforcement skipped in pipeline)'
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Package & Archive') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Build Fat JAR') {
                    steps {
                        echo '[Package] Goal  : mvn package -DskipTests'
                        echo '[Package] Plugin: maven-shade-plugin (uber/fat JAR)'
                        echo '[Package] Input : target/classes + dependencies'
                        echo "[Package] Output: target/GameVerseAcademy-${APP_VERSION}.jar"
                        sh 'mvn package -DskipTests --no-transfer-progress'
                    }
                }

                stage('Archive Artifact') {
                    steps {
                        echo '[Archive] Pattern    : target/*.jar'
                        echo '[Archive] Destination: Jenkins build artifacts'
                        echo '[Archive] Fingerprint: SHA-1 hash tracked per build'
                        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                        script {
                            def jar  = sh(script: 'ls target/GameVerseAcademy-*.jar', returnStdout: true).trim()
                            def size = sh(script: "du -sh ${jar} | awk '{print \$1}'", returnStdout: true).trim()
                            echo "[Archive] File : ${jar}"
                            echo "[Archive] Size : ${size}"
                            echo "[Archive] Build: #${BUILD_NUMBER}"
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Deploy to Nexus') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Upload to maven-snapshots') {
                    steps {
                        echo "[Nexus] Server    : ${NEXUS_URL}"
                        echo '[Nexus] Repository: maven-snapshots'
                        echo '[Nexus] Goal      : mvn deploy -DskipTests'
                        echo '[Nexus] Settings  : /var/jenkins_home/.m2/settings.xml'
                        echo '[Nexus] Server ID : nexus (matches distributionManagement in pom.xml)'
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

                stage('Verify Upload') {
                    steps {
                        echo "[Nexus] Artifact uploaded successfully"
                        echo "[Nexus] Browse: ${NEXUS_URL}/#browse/browse:maven-snapshots"
                        echo "[Nexus] Group  : ma.ac.esi  |  Artifact: GameVerseAcademy  |  Version: ${APP_VERSION}"
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Docker Build') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Build Image') {
                    steps {
                        echo "[Docker] Image     : ${DOCKER_IMAGE}"
                        echo '[Docker] Context   : ./'
                        echo '[Docker] Dockerfile: ./Dockerfile'
                        echo '[Docker] Base image: eclipse-temurin:21-jre-alpine'
                        echo '[Docker] COPY      : target/GameVerseAcademy-*.jar → /app/app.jar'
                        echo '[Docker] EXPOSE    : 6060'
                        echo '[Docker] ENTRYPOINT: java -jar /app/app.jar'
                        sh "docker build -t ${DOCKER_IMAGE} ."
                    }
                }

                stage('Inspect Image') {
                    steps {
                        echo "[Docker] Verifying image: ${DOCKER_IMAGE}"
                        sh "docker image inspect ${DOCKER_IMAGE} --format 'ID={{.Id}}  Created={{.Created}}  OS={{.Os}}/{{.Architecture}}'"
                        script {
                            def bytes = sh(script: "docker image inspect ${DOCKER_IMAGE} --format '{{.Size}}'", returnStdout: true).trim().toLong()
                            def mb    = String.format("%.1f MB", bytes / 1024 / 1024)
                            echo "[Docker] Size: ${mb}  |  Tag: ${DOCKER_IMAGE}"
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Trivy Security Scan') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Scan Image') {
                    steps {
                        echo "[Trivy] Scanner   : aquasec/trivy (latest)"
                        echo "[Trivy] Target    : ${DOCKER_IMAGE}"
                        echo '[Trivy] Socket    : /var/run/docker.sock (Docker-in-Docker)'
                        echo '[Trivy] Severities: HIGH, CRITICAL'
                        echo '[Trivy] Format    : table'
                        echo '[Trivy] Exit code : 0 → report-only (set to 1 to block pipeline on CVEs)'
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

                stage('Report Vulnerabilities') {
                    steps {
                        echo '[Trivy] Scan complete — review CVE table above'
                        echo '[Trivy] To enforce a hard gate: change --exit-code 0 → --exit-code 1'
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Push to Nexus Docker Registry') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Login to Registry') {
                    steps {
                        echo "[Docker Push] Registry: ${NEXUS_DOCKER_REG}"
                        echo '[Docker Push] Auth    : nexus-credentials (Jenkins credential store)'
                        echo '[Docker Push] Realm   : Docker Bearer Token Realm (enabled in Nexus)'
                        sh "docker login ${NEXUS_DOCKER_REG} -u ${NEXUS_CREDS_USR} -p ${NEXUS_CREDS_PSW}"
                    }
                }

                stage('Tag Image') {
                    steps {
                        echo "[Docker Push] Source: ${DOCKER_IMAGE}"
                        echo "[Docker Push] Target: ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                        sh "docker tag ${DOCKER_IMAGE} ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                    }
                }

                stage('Push Image') {
                    steps {
                        echo "[Docker Push] Pushing layers to ${NEXUS_DOCKER_REG}"
                        sh "docker push ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                        echo "[Docker Push] Image available: ${NEXUS_DOCKER_REG}/${DOCKER_IMAGE}"
                        echo "[Docker Push] Catalog       : http://localhost:8082/v2/_catalog"
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        stage('Deploy to k3s via Helm') {
        // ══════════════════════════════════════════════════════
            stages {

                stage('Verify Environment') {
                    steps {
                        echo '[Helm] Verifying Helm CLI and kubeconfig'
                        sh 'helm version --short'
                        echo '[kubectl] Current cluster context:'
                        sh 'kubectl config current-context'
                        echo '[kubectl] Kubeconfig: /var/jenkins_home/.kube/config → https://192.168.1.9:6443'
                    }
                }

                stage('Helm Upgrade Install') {
                    steps {
                        echo "[Helm] Release   : ${APP_NAME}"
                        echo '[Helm] Chart     : ./charts/gameverseacademy'
                        echo '[Helm] Namespace : production'
                        echo "[Helm] Image     : ${NEXUS_DOCKER_REG}/${APP_NAME}:${BUILD_NUMBER}"
                        echo '[Helm] Strategy  : upgrade --install (creates release if it does not exist)'
                        echo '[Helm] Pull secret: nexus-registry (pre-created in production namespace)'
                        echo '[Helm] Probes    : tcpSocket on port 6060'
                        sh """
                            helm upgrade --install ${APP_NAME} ./charts/${APP_NAME} \
                              --namespace production \
                              --create-namespace \
                              --set image.repository=${NEXUS_DOCKER_REG}/${APP_NAME} \
                              --set image.tag=${BUILD_NUMBER}
                        """
                    }
                }

                stage('Verify Deployment') {
                    steps {
                        echo '[Helm] Fetching release status'
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

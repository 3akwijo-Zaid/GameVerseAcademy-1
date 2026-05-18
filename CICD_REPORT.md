# CI/CD Pipeline Report — GameVerseAcademy

**Project:** GameVerseAcademy  
**Institution:** École Supérieure d'Informatique (ESI) Rabat  
**Stack:** Java 21 · Maven · Embedded Tomcat · PostgreSQL (Render)  
**Date:** 2026-05-18

---

## 1. Executive Summary

This report documents the design, implementation, and operation of a complete CI/CD pipeline for the GameVerseAcademy web application. The pipeline automates every step from source code commit to live deployment on a Kubernetes cluster, enforcing code quality, security scanning, and artifact management at each stage.

The pipeline is fully operational and delivers:
- **Automated builds** triggered by GitHub webhook (with poll fallback)
- **107 unit tests** executed and reported on every build
- **Static analysis** via SonarQube, Checkstyle, and PMD
- **Artifact storage** in Sonatype Nexus (Maven + Docker registries)
- **Container security scanning** via Trivy
- **Zero-downtime deployment** to Kubernetes (k3s) via Helm

---

## 2. Architecture Overview

```
Developer
    │
    │  git push
    ▼
GitHub Repository
    │
    │  Webhook (instant) / pollSCM fallback (every 5 min)
    ▼
┌─────────────────────────────────────────────────────────────────┐
│                     CI/CD Infrastructure                         │
│                   (Docker Compose Network)                       │
│                                                                  │
│  ┌─────────────┐   ┌─────────────┐   ┌──────────────────────┐  │
│  │   Jenkins   │──▶│  SonarQube  │   │   Sonatype Nexus 3   │  │
│  │  :8090      │   │  :9000      │   │  Maven repos  :8081  │  │
│  │  (LTS JDK21)│   │  + Postgres │   │  Docker reg   :8082  │  │
│  └──────┬──────┘   └─────────────┘   └──────────────────────┘  │
│         │                                                        │
└─────────┼────────────────────────────────────────────────────────┘
          │
          │  Helm upgrade --install
          ▼
┌─────────────────────────┐
│   k3s Kubernetes        │
│   Namespace: production │
│   NodePort: 30606       │
│                         │
│  ┌─────────────────┐    │
│  │ gameverseacademy│    │
│  │ Pod (1 replica) │    │
│  │ Port: 6060      │    │
│  └─────────────────┘    │
└─────────────────────────┘
          │
          │  http://localhost:30606/gameverseacademy/
          ▼
       Browser
```

---

## 3. Infrastructure Stack

### 3.1 CI/CD Services (Docker Compose)

| Service | Image | Port | Role |
|---|---|---|---|
| Jenkins | `jenkins/jenkins:lts-jdk21` | 8090 | Pipeline orchestrator |
| SonarQube | `sonarqube:lts-community` | 9000 | Static analysis server |
| SonarQube DB | `postgres:16-alpine` | — | SonarQube backend database |
| Nexus | `sonatype/nexus3:latest` | 8081 / 8082 | Maven + Docker artifact registry |

All services run on a shared Docker bridge network (`gameverseacademy_cicd`), allowing inter-service communication by hostname (`nexus:8081`, `sonarqube:9000`).

Jenkins has `/var/run/docker.sock` mounted to enable Docker-in-Docker for image building and Trivy scanning.

### 3.2 Deployment Target

| Component | Detail |
|---|---|
| Orchestrator | k3s (lightweight Kubernetes) |
| Namespace | `production` |
| Service type | NodePort |
| Exposed port | 30606 → 6060 |
| Package manager | Helm 3 |
| Image pull secret | `nexus-registry` |

### 3.3 Jenkins Plugins

| Plugin | Purpose |
|---|---|
| Git / GitHub | SCM checkout and webhook trigger |
| Maven Integration | Maven tool management |
| SonarQube Scanner | SonarQube analysis integration |
| Warnings Next Generation | Checkstyle + PMD issue recording |
| JaCoCo | Code coverage publishing |
| Docker Pipeline | Docker build and push steps |
| Kubernetes CLI | kubectl access from pipeline |
| Blue Ocean | Pipeline visualization |
| Credentials Binding | Secure secret injection |
| Timestamper | Build log timestamps |

---

## 4. Pipeline Design

### 4.1 Trigger Strategy

```groovy
triggers {
    githubPush()           // fires instantly on git push via GitHub webhook
    pollSCM('H/5 * * * *') // fallback poll every 5 minutes
}
```

The dual-trigger strategy ensures builds fire immediately on push (webhook) while the poll fallback guarantees no commit is missed if the webhook delivery fails.

### 4.2 Pipeline Graph (Blue Ocean)

```
SCM Polling → Build → Test & Coverage → Code Analysis ──────────────────────── → Quality Gate → Package & Archive → Publish & Containerise ──────────────────────── → Deploy to k3s via Helm
                                              │                                                                              │
                                              ├─ SonarQube                                                                  ├─ Deploy to Nexus
                                              │    ├─ SonarQube › Analyse                                                   │    ├─ Nexus › Upload
                                              │    └─ SonarQube › Report                                                    │    └─ Nexus › Verify
                                              │                                                                              │
                                              └─ Static Analysis                                                             └─ Docker Pipeline
                                                   ├─ Static › Checkstyle                                                        ├─ Docker › Build
                                                   ├─ Static › PMD                                                               ├─ Docker › Trivy Scan
                                                   └─ Static › Record Issues                                                     └─ Docker › Push
```

---

## 5. Pipeline Stages — Detailed

### Stage 1 — SCM Polling

**Purpose:** Detect changes and establish build context.

**Steps:**
1. Checkout source code from GitHub via `checkout scm`
2. Extract commit metadata: branch, hash, author, date, commit message
3. Count and list files changed since the previous commit

**Output:** Build context variables (`GIT_COMMIT_SHORT`, `GIT_AUTHOR`, `GIT_BRANCH_NAME`, `CHANGED_COUNT`)

---

### Stage 2 — Build

**Purpose:** Compile the Java 21 source code.

**Steps:**
1. `mvn clean` — delete previous `target/` directory
2. `mvn compile` — compile `src/main/java` → `target/classes`
3. Count produced `.class` files to confirm successful compilation

**Maven goal:** `clean compile`  
**Input:** `src/main/java`  
**Output:** `target/classes/*.class`

---

### Stage 3 — Test & Coverage

**Purpose:** Execute unit tests and measure code coverage.

**Steps:**
1. `mvn test` — run all JUnit 5 tests via Maven Surefire plugin
2. Publish JUnit XML reports from `target/surefire-reports/*.xml`
3. Publish JaCoCo coverage report from `target/jacoco.exec`

**Results:**
- **107 tests** across 5 test classes — all passing
- Coverage report: `target/site/jacoco/index.html`
- XML coverage: `target/site/jacoco/jacoco.xml` (consumed by SonarQube)

**Maven goal:** `test`  
**Input:** `src/test/java`  
**Output:** `target/surefire-reports/`, `target/jacoco.exec`, `target/site/jacoco/`

---

### Stage 4 — Code Analysis (parallel)

Two analysis branches run simultaneously:

#### Branch A — SonarQube Analysis

**Purpose:** Comprehensive code quality analysis with centralized dashboard.

**Steps:**
1. Connect to SonarQube server at `http://sonarqube:9000`
2. Run `mvn sonar:sonar` with authentication token
3. Send: sources, bytecode, JaCoCo XML coverage, test results

**Metrics analysed:**
- Bugs and code smells
- Security vulnerabilities
- Code duplication percentage
- Cyclomatic complexity
- Test coverage percentage

**Dashboard:** `http://localhost:9000/dashboard?id=GameVerseAcademy`

#### Branch B — Static Analysis (Checkstyle + PMD)

**Purpose:** Enforce coding standards and detect common defects.

| Tool | Goal | Rules | Report |
|---|---|---|---|
| Checkstyle | `mvn checkstyle:check` | Google Java Style | `target/checkstyle-result.xml` |
| PMD | `mvn pmd:check` | Default ruleset | `target/pmd.xml` |

**Current findings:** 35 existing issues (0 new) recorded in Warnings NG tab.  
Both tools configured with `|| true` — issues are **reported but do not block the build**.

---

### Stage 5 — Quality Gate

**Purpose:** Evaluate SonarQube quality gate result.

Configured thresholds:
- Coverage ≥ 80%
- Reliability rating: A
- Security rating: A
- Maintainability rating: A

> Gate enforcement is currently set to report-only. To enforce: replace the `echo` step with `waitForQualityGate abortPipeline: true` and configure a SonarQube webhook.

---

### Stage 6 — Package & Archive

**Purpose:** Build the deployable artifact and store it in Jenkins.

**Steps:**
1. `mvn package -DskipTests` — invoke maven-shade-plugin to produce fat JAR
2. Archive `target/*.jar` in Jenkins with SHA-1 fingerprinting

**Plugin:** `maven-shade-plugin` (uber/fat JAR — all dependencies bundled)  
**Output:** `target/GameVerseAcademy-0.0.1-SNAPSHOT.jar` (~90 MB)  
**Retained builds:** 10 (configured via `logRotator`)

---

### Stage 7 — Publish & Containerise (parallel)

Two deployment branches run simultaneously after packaging:

#### Branch A — Deploy to Nexus (Maven)

**Purpose:** Publish the versioned JAR to Nexus for traceability and reuse.

**Steps:**
1. Authenticate with Nexus using `nexus-credentials` from Jenkins credential store
2. `mvn deploy` uploads JAR + POM + SHA checksums to `maven-snapshots`
3. Verify: artifact browsable at `http://nexus:8081/#browse/browse:maven-snapshots`

**Configuration:** `distributionManagement` in `pom.xml` points to `http://nexus:8081/repository/maven-snapshots`  
**Settings:** `/var/jenkins_home/.m2/settings.xml` provides server credentials

#### Branch B — Docker Pipeline (Build → Scan → Push)

**Step 1 — Docker › Build**

Build a minimal production Docker image:

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/GameVerseAcademy-*.jar app.jar
COPY src/main/webapp src/main/webapp
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

- Base image: `eclipse-temurin:21-jre-alpine` (minimal JRE, Alpine Linux)
- `src/main/webapp` copied so embedded Tomcat can serve HTML/JSP files
- Final image size: ~95 MB

**Step 2 — Docker › Trivy Scan**

```
aquasec/trivy image --severity HIGH,CRITICAL --exit-code 0 gameverseacademy:<build>
```

- Scanner: Aqua Security Trivy (run via Docker socket)
- Severities: HIGH and CRITICAL CVEs only
- Exit code 0: scan results are reported but do not fail the pipeline
- To enforce: change `--exit-code` to `1`

**Step 3 — Docker › Push**

1. Login to Nexus Docker registry at `localhost:8082`
2. Tag image: `gameverseacademy:<build>` → `localhost:8082/gameverseacademy:<build>`
3. Push all layers to Nexus hosted Docker repository
4. Image catalogued at `http://localhost:8082/v2/_catalog`

---

### Stage 8 — Deploy to k3s via Helm

**Purpose:** Deploy the new image to the Kubernetes production namespace.

**Steps:**

1. **Verify Environment** — confirm Helm version and active kubeconfig context
2. **Helm Deploy** — run `helm upgrade --install`
3. **Verify Deployment** — check release status, pod state, and service

**Helm command:**
```bash
helm upgrade --install gameverseacademy ./charts/gameverseacademy \
  --namespace production \
  --create-namespace \
  --set image.repository=localhost:8082/gameverseacademy \
  --set image.tag=<BUILD_NUMBER>
```

**Helm chart values:**

| Parameter | Value |
|---|---|
| `replicaCount` | 1 |
| `image.pullPolicy` | IfNotPresent |
| `service.type` | NodePort |
| `service.port` | 6060 |
| `service.nodePort` | 30606 |
| `imagePullSecrets` | nexus-registry |
| `resources.requests` | 250m CPU, 512Mi RAM |
| `resources.limits` | 1000m CPU, 1Gi RAM |
| `livenessProbe` | tcpSocket:6060, delay 30s |
| `readinessProbe` | tcpSocket:6060, delay 20s |

**Kubeconfig:** `/var/jenkins_home/.kube/config` → `https://192.168.1.9:6443`  
**Access URL after deployment:** `http://localhost:30606/gameverseacademy/LoginController`

---

## 6. Security

### Credential Management
All secrets are stored in the Jenkins credential store — never hardcoded in the Jenkinsfile:

| Credential ID | Type | Used for |
|---|---|---|
| `nexus-credentials` | Username/Password | Nexus Maven deploy + Docker push |
| `sonarqube-token` | Secret Text | SonarQube analysis authentication |
| `nexus-registry` | K8s Docker Secret | k3s image pull from Nexus |

### Container Security
- Trivy scans every built image for HIGH and CRITICAL CVEs before push
- Base image `eclipse-temurin:21-jre-alpine` minimises attack surface (JRE only, no JDK)
- Docker socket access scoped to the Jenkins container only

### Network Isolation
- All CI/CD services communicate on an isolated Docker bridge network
- No CI/CD service ports are exposed beyond the host machine

---

## 7. Artifact Management

### Maven Artifacts (Nexus)

| Repository | Type | Content |
|---|---|---|
| `maven-snapshots` | Hosted | `GameVerseAcademy-0.0.1-SNAPSHOT.jar` per build |
| `maven-central` | Proxy | Upstream dependency cache |

### Docker Images (Nexus)

| Registry | Image | Tag strategy |
|---|---|---|
| `localhost:8082` | `gameverseacademy` | Jenkins `BUILD_NUMBER` (immutable per build) |

Every successful build produces a uniquely tagged, immutable Docker image stored in Nexus. k3s always pulls the exact build number set by the Helm `--set image.tag` override.

---

## 8. Results

| Metric | Value |
|---|---|
| Unit tests | 107 passing, 0 failing |
| Test classes | 5 |
| Build time (avg) | ~8–12 minutes end-to-end |
| JAR size | ~90–95 MB (fat JAR) |
| Docker image size | ~95 MB |
| PMD issues | 35 (pre-existing, 0 new) |
| Checkstyle issues | reported, 0 blocking |
| SonarQube | ANALYSIS SUCCESSFUL |
| Nexus Maven | Deployed to maven-snapshots |
| Nexus Docker | Image pushed per build |
| k3s deployment | Running in `production` namespace |
| App URL | http://localhost:30606/gameverseacademy/ |

---

## 9. File Structure

```
GameVerseAcademy/
├── Jenkinsfile                          # Declarative pipeline definition
├── Dockerfile                           # Container image build instructions
├── docker-compose.yml                   # CI/CD infrastructure stack
├── pom.xml                              # Maven build + plugin configuration
├── sonar-project.properties             # SonarQube project configuration
├── charts/
│   └── gameverseacademy/
│       ├── Chart.yaml                   # Helm chart metadata
│       ├── values.yaml                  # Deployment configuration
│       └── templates/
│           ├── deployment.yaml          # Kubernetes Deployment manifest
│           └── service.yaml             # Kubernetes Service manifest
└── src/
    ├── main/
    │   ├── java/                        # Application source code
    │   └── webapp/                      # Static files, JSPs, WEB-INF
    └── test/
        └── java/                        # JUnit 5 test classes
```

---

## 10. Conclusion

The GameVerseAcademy CI/CD pipeline implements a complete DevOps workflow covering the full software delivery lifecycle:

1. **Source control integration** — GitHub webhook triggers instant builds on every push
2. **Automated quality enforcement** — tests, coverage, and static analysis run on every commit
3. **Secure artifact management** — versioned JARs and Docker images stored in Nexus
4. **Container security** — Trivy vulnerability scanning before every push
5. **Automated deployment** — Helm manages zero-configuration Kubernetes rollouts
6. **Full observability** — Blue Ocean pipeline view, SonarQube dashboard, Warnings NG, JaCoCo coverage, and JUnit reports provide complete build insight

The pipeline follows industry best practices: immutable artifact tagging, secret injection via credential store, parallel execution to minimize build time, and infrastructure-as-code for all configuration (Jenkinsfile, Helm chart, Docker Compose, Dockerfile — all versioned in Git).

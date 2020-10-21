properties([
    parameters ([
        booleanParam(name: 'CLEAN_WORKSPACE', defaultValue: true, description: 'Clean the workspace at the end of the run'),
        string(name: 'DOCKER_REGISTRY_DOWNLOAD_URL', defaultValue: 'nexus-docker-private-group.ossim.io', description: 'Repository of docker images')
    ]),
    pipelineTriggers([
            [$class: "GitHubPushTrigger"]
    ]),
    [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/ossimlabs/omar-avro'],
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '3', daysToKeepStr: '', numToKeepStr: '20')),
    disableConcurrentBuilds()
])
podTemplate(
  containers: [
    containerTemplate(
      name: 'docker',
      image: 'docker:19.03.8',
      ttyEnabled: true,
      command: 'cat',
      privileged: true
    ),
    containerTemplate(
      image: "${DOCKER_REGISTRY_DOWNLOAD_URL}/omar-builder:latest",
      name: 'builder',
      command: 'cat',
      ttyEnabled: true
    )
  ],
  volumes: [
    hostPathVolume(
      hostPath: '/var/run/docker.sock',
      mountPath: '/var/run/docker.sock'
    ),
  ]
)
{
  node(POD_LABEL){

      stage("Checkout branch")
      {
        scmVars = checkout(scm)
        
        GIT_BRANCH_NAME = scmVars.GIT_BRANCH
        BRANCH_NAME = """${sh(returnStdout: true, script: "echo ${GIT_BRANCH_NAME} | awk -F'/' '{print \$2}'").trim()}"""
        sh """
        touch buildVersion.txt
        grep buildVersion gradle.properties | cut -d "=" -f2 > "buildVersion.txt"
        """
        preVERSION = readFile "buildVersion.txt"
        VERSION = preVERSION.substring(0, preVERSION.indexOf('\n'))

        GIT_TAG_NAME = "omar-avro" + "-" + VERSION
        ARTIFACT_NAME = "ArtifactName"

        script {
          if (BRANCH_NAME != 'master') {
            buildName "${VERSION} - ${BRANCH_NAME}-SNAPSHOT"
          } else {
            buildName "${VERSION} - ${BRANCH_NAME}"
          }
        }
      }

      stage("Load Variables")
      {
        withCredentials([string(credentialsId: 'o2-artifact-project', variable: 'o2ArtifactProject')]) {
          step ([$class: "CopyArtifact",
            projectName: o2ArtifactProject,
            filter: "common-variables.groovy",
            flatten: true])
          }
          load "common-variables.groovy"
          
        switch (BRANCH_NAME) {
        case "master":
          TAG_NAME = VERSION
          break

        case "dev":
          TAG_NAME = "latest"
          break

        default:
          TAG_NAME = BRANCH_NAME
          break
        }

        DOCKER_IMAGE_PATH = "${DOCKER_REGISTRY_PRIVATE_UPLOAD_URL}/omar-avro"
      }
      
      stage('Build') {
        container('builder') {
          sh """
          ./gradlew assemble \
              -PossimMavenProxy=${MAVEN_DOWNLOAD_URL}
              cp plugins/*/build/libs/*.jar docker/
          """
          archiveArtifacts "plugins/*/build/libs/*.jar"
        }
      }
    
    stage('Docker build') {
      container('docker') {
        withDockerRegistry(credentialsId: 'dockerCredentials', url: "https://${DOCKER_REGISTRY_DOWNLOAD_URL}") {
          sh """
            docker build -t "${DOCKER_REGISTRY_PUBLIC_UPLOAD_URL}"/omar-avro:${VERSION} ./docker
          """
        }
      }
      stage('Docker push'){
        container('docker') {
          withDockerRegistry(credentialsId: 'dockerCredentials', url: "https://${DOCKER_REGISTRY_PUBLIC_UPLOAD_URL}") {
          sh """
              docker push "${DOCKER_REGISTRY_PUBLIC_UPLOAD_URL}"/omar-avro:${VERSION}
          """
          }
        }
      }
    }
    stage("Clean Workspace"){
      if ("${CLEAN_WORKSPACE}" == "true")
        step([$class: 'WsCleanup'])
    }
  }
}

@Library('mcapi-jenkins-pipeline-libs')
import sendEmail

pipeline {
    agent {
        docker {
            image 'artifactory.dev.mastercard.int:6555/com-mastercard-api/sdk-gradle:develop'
        }
    }

    options {
        overrideIndexTriggers(false)
    }

    stages{
        stage('Test') {
            steps {
                sh "gradle clean test -i"
            }
        }

        stage('Publish to Artifactory') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'mcapi_ci_vcs_user', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                    sh """
                        gradle artifactory \
                            -Penv=mc-jenkins \
							-Pmc_artifactory_user=${USER} \
							-Pmc_artifactory_password=${PASS}
                    """
                }
            }
        }

        stage('Publish To Sonatype') {
            when {
                branch 'master'
            }
            steps {
                figlet 'Sonatype'

                configFileProvider([configFile(fileId: 'mcapi-sonatype-maven-settings-overrides', variable: 'MAVEN_SETTINGS')]) {
                    withCredentials([
                            file(credentialsId: 'mcapi-artifact-secret-gpg-keyring', variable: 'GPG_SECRET_KEY_RING'),
                            file(credentialsId: 'mcapi-artifact-public-gpg-keyring', variable: 'GPG_PUBLIC_KEY_RING')] ) {
                        sh "${env.GRADLE4}/bin/gradle uploadArchives"
                    }
                }
            }
        }

        stage('Push to GitHub') {
            when {
                branch 'master'
            }
            steps {
                figlet 'GitHub'

                script {
                    def gradleProperties = readProperties file: 'gradle.properties'
                    env.PROJECT_VERSION = gradleProperties.version
                }

                withCredentials([sshUserPrivateKey(credentialsId: 'mcapi-github-private-key', keyFileVariable: 'KEY_FILE')]) {
                    sh """
                        eval `ssh-agent -s`
                        ssh-add ${KEY_FILE}

                        git config user.email '${env.GITHUB_EMAIL}'
                        git config user.name '${env.GITHUB_USERNAME}'
                        git remote add github git@github.com:MasterCard/sdk-core-java.git
                        git branch --set-upstream master origin/master
                        git reset --hard
                        git push -f -u github master
                        git tag -f ${env.PROJECT_VERSION}
                        git push -f -u github ${env.PROJECT_VERSION}
                    """
                }
            }
        }
    }

    post {
        always {
            junit 'build/test-results/test/*.xml'
        }

        changed {
            sendEmail(currentBuild, env)
        }
    }
}
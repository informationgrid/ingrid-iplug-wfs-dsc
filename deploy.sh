set -e

# this file is used on the ingrid build server, set javaHome to hard coded value
export JAVA_HOME=/opt/jdk1.6.0_11

cd core
maven clean:clean jar:deploy

cd ../ui
maven clean war:deploy

#todo: deploy snapshot of distribution
cd ..

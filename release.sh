set -e

cd core
maven clean:clean jar:deploy

cd ../ui
maven clean:clean war:deploy

cd ../distribution
sh release.sh

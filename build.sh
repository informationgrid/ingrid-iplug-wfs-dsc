set -e

cd core
maven clean:clean jar:install

cd ../ui
maven clean:clean war:install

cd ../distribution
maven clean:clean bundle -o

cd ..

#!/usr/bin/env bash

appName=order
packagePath=sbc

artifactName="service-${appName}-app-xyy-4.0.9-SNAPSHOT"
fromName="./service-${appName}-app/target/${artifactName}.jar"
toServer="10.130.8.202"
toName="/data/${packagePath}/${appName}/bin/${appName}-app.jar"
logFile="/data/${packagePath}/${appName}/log/springboot.out"

function login() {
     ssh root@${toServer}
}

function log()
{
  ssh root@${toServer} "tail -n 100 -f ${logFile}"
}

function package() {
    mvn clean install
    if [[ ! -f "${fromName}" ]]
    then
        echo "jar file does not exist..."
        exit 1
    fi
}

function copy() {
    scp ${fromName}  root@${toServer}:${toName}
}

function copyr() {
     copy
     restart
     log
}

function restart() {
    ssh root@${toServer} "/data/${packagePath}/${appName}/bin/shutdown.sh && /data/${packagePath}/${appName}/bin/startup.sh"
}

function deploy() {
  package
  copy
  restart
  log
}

function xpush() {
    git push
    deploy
}


echo 参数1是: ${1}

if [[ x$1 != x ]]
then
  if [[ "$(type -t $1)" = "function" ]] ; then
    echo "function $1 exists"
    $1
  else
    echo "function $1 does not exist"
  fi
fi
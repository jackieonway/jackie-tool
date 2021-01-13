#!/bin/bash
version = $(cat updateVersion.txt)
echo 'Update project version to $version'
cd ..
mvn versions:set -DnewVersion=$version
echo 'Update version to $version success'
echo 'Update child modules version to $version'
mvn versions:update-child-modules
echo 'Update child modules version to $version success'
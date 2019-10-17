#!/bin/bash
cd ..
mvn clean deploy -Dmaven.test.skip=true

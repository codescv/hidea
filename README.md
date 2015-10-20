# Hbase Plugin for IntelliJ IDEA

## Description
provide simple querying of hbase tables

## Debugging instructions
1. set up development environment according to instructions [here](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html)
2. run 'Plugin' target from intellij idea

## Building with maven (to be supported soon)
1. install following libs to your local maven repo:

        mvn install:install-file -Dfile=forms_rt.jar -DgroupId=com.intellij -DartifactId=forms_rt -Dversio=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=forms_rt.jar -DgroupId=com.intellij -DartifactId=forms_rt -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=openapi.jar -DgroupId=com.intellij -DartifactId=openapi -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=jna.jar -DgroupId=com.intellij -DartifactId=jna -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=util.jar -DgroupId=com.intellij -DartifactId=util -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=idea.jar -DgroupId=com.intellij -DartifactId=idea -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=extensions.jar -DgroupId=com.intellij -DartifactId=extensions -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=resources.jar -DgroupId=com.intellij -DartifactId=resources -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=resources_en.jar -DgroupId=com.intellij -DartifactId=resources_en -Dversion=14.1 -Dpackaging=jar
        mvn install:install-file -Dfile=icons.jar -DgroupId=com.intellij -DartifactId=icons -Dversion=14.1 -Dpackaging=jar
        
2. set up properties intellij.sdk.name, intellij.sdk.version, intellij.sdk.path, etc in pom.xml according to your configuration.
3. mvn clean package

## TODO
1. Support querying by row prefix
1. Support server settings
1. Add maven build
1. Support editing values
1. Support deleting rows

## Acknowlegement
[mongo4idea](https://github.com/dboissier/mongo4idea) a great reference of Intellij Idea plugin development.


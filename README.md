# Read Me First
The following was discovered as part of building this project:

* The JVM level was changed from '1.8' to '17', review the [JDK Version Range](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range) on the wiki for more details.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.0/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.0/gradle-plugin/reference/html/#build-image)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

# Commands that are used
### load-test-entity-extraction
#### load-test-entity-extraction <rosette_url> <path_to_data_source> <number_of_threads> <time_to_execute_in_minutes> <url_which_gets_machine_statistics> <load_in_memory> <doEE> <doSentiment> <useRest> <languages>
load-test-entity-extraction take some files from <path_to_data_source> and sends them over and over again until <time_to_execute_in_minutes> passes
* <url_which_gets_machine_statistics> url to catvisor
* <load_in_memory> all data to be loaded into memory and every time if file needs to be sent it will be preloaded into memory not read from FS
* <languages> send files only on language that are part of the list
* <useRest> if true it will use rest, If false It will use rosette api


| command                     | rosette_url                     | path_to_data_source | number_of_threads | time_to_execute_in_minutes | url_which_gets_machine_statistics | load_in_memory | doEE | doSentiment | useRest | languages       |
|-----------------------------|---------------------------------|---------------------|-------------------|----------------------------|-----------------------------------|----------------|------|-------------|---------|-----------------|
| load-test-entity-extraction | http://10.161.0.5:8181/rest/v1  | data                | 16                | 1                          | http://10.161.0.5:8098            | true           | true | false       | false   | Arabic, Chinese |

Example
`java -jar rosette-load-test-0.0.1-SNAPSHOT.jar load-test-entity-extraction http://10.161.0.5:8181/rest/v1 data 16 1 http://10.161.0.5:8098 true true false true`
### do-nlp-on-zips
### do-nlp-on-zips <rosette_url> <path_to_data_source> <number_of_threads> <url_which_gets_machine_statistics>
The command takes zips exported from probe in path <path_to_data_source> and sends them to rosette for entity extraction and sentiment

| command                     | rosette_url                     | path_to_data_source | number_of_threads | url_which_gets_machine_statistics |
|-----------------------------|---------------------------------|---------------------|-------------------|-----------------------------------|
| load-test-entity-extraction | http://10.161.0.5:8181/rest/v1  | data                | 16                | http://10.161.0.5:8098            |

Example
`java -jar rosette-load-test-0.0.1-SNAPSHOT.jar do-nlp-on-zips  http://10.161.0.5:8181/rest/v1 zips 16 http://10.161.0.5:8098`

[//]: # ()
[//]: # (load-test-entity-extraction  http://10.161.0.5:8181/rest/v1 data 500 1 http://10.161.0.5:8098 true true true)

[//]: # (load-test-entity-extraction  http://10.161.0.5:8181/rest/v1 data 500 1 http://10.161.0.5:8098 true true true true)

[//]: # (load-test-entity-extraction  http://10.161.0.5:8181/rest/v1 data 500 1 http://10.161.0.5:8098 true true false false)

[//]: # (load-test-entity-extraction  http://10.161.0.5:8181/rest/v1 data 500 1 http://10.161.0.5:8098 true false true true)

[//]: # (load-test-entity-extraction  http://10.161.0.5:8181/rest/v1 data 16 1 http://10.161.0.5:8098 true true false true)

[//]: # (do-nlp-on-zips  http://10.161.0.5:8181/rest/v1 zips 16 http://10.161.0.5:8098)
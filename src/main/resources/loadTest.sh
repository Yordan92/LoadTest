java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true false true Arabic > results/Arabic_sentiment.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false Arabic > results/Arabic_entity_extraction.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false Arabic > results/Arabic_sentiment_entity_extraction.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true false true Chinese > results/Chinese_sentiment.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false Chinese > results/Chinese_entity_extraction.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false Chinese > results/Chinese_sentiment_entity_extraction.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true false true French > results/French_sentiment.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false French > results/French_entity_extraction.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false French > results/French_sentiment_entity_extraction.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true false true > results/ALL_sentiment.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false > results/ALL_entity_extraction.json &&\
java -jar rosette-load-test-0.0.1-SNAPSHOT.jar  load-test-entity-extraction http://10.161.0.5:8098/rosette/rest/v1 data 500 1 http://10.161.0.5:8098 true true false > results/ALL_sentiment_entity_extraction.json

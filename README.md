# idea-elasticsearch-support
A plugin to connect to the ElasticSearch instance and perform a set of basic operations.

Installation:
1. Download or clone the sources.
2. Execute the "gradlew clean build" command in a root project folder console.
3. In the IDE window go to File -> Settings -> Plugins -> Install plugin from disk.
4. Select a ZIP file from the "<projectRoot>/build/distributions" folder.
5. Press OK and restart the IDE

Usage:
1. In the IDE go to the Elasticsearch -> Connect to Elasticsearch
2. Enter a host name;
3. Enter a port;
4. Optionally enter a scheme (protocol); if empty, "http" will be used by default to connect;
5. In case of success, after information messages about a successful connection establishment, the "elasticsearch-query.json" scratch file for requests will be opened.
6. Enter a query into the opened scratch file.
7. Select a part of the query to evaluate and go to Elasticsearch -> Evaluate selected text on path.
8. Enter a path to evaluate a query on.
9. Optionally enter an HTTP method to use (POST will be used by default).
10. Press OK to execute the request.

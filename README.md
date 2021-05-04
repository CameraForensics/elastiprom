# elastiprom
A spring-boot application that calls elasticsearch's cluster and node REST endpoints and converts them to prometheus-friendly metric formats.

* [Settings](#settings)
* [Run](#run)
  * [Docker](#run-docker)
  * [SystemD](#run-systemd)
  * [IntelliJ](#run-intellij)
* [Acknowledgements](#acknowledgements)

### <a name="settings"></a>Settings
The following settings need to be set as environment variables in the context of the running application.

For example, if running the application in docker, you'll need to set values via the `--env` or `-e` switches in the docker run command,
or set them in the `environment` section of your docker-compose file (or in the associated `.env` file if you use it).

#### LOGGING_LEVEL
Values can be:
* `ERROR`
* `WARN`
* `INFO` (default)
* `DEBUG`
* `TRACE`

#### LOGGING_PATH
The path to where you want the logs stored.

Default: `logs`

The log file is called: `log.log`

See 
* `src/main/resources/application.properties` and 
* `src/main/resources/logback-spring.xml` 

for log settings.

#### ES_HOST
This is the host that you want stat requests to go to.

Default: `localhost`

#### ES_PORT
This should be the port that you want to talk to your ES cluster on.

Default: `9200`

#### ES_SCHEME
Values can be:
* `http` (default)
* `https`

If you set an incorrect value, the system will likely error and fail.

#### ES_AUTH
Values can be:
* `basic:USERNAME:PASSWORD`
* `none` (default)

If anything other than the above two values are set, the default setting applies.

# <a name="run"></a>Run

## <a name="run-docker"></a>Docker

### Container as a SystemD Service
1. Pull the built container
2. Copy the `elastiprom.service` file to `/etc/systemd/system/`
3. Create a `.env` file with your settings in it in `/usr/share/elastiprom/`
4. Reload systemctl: `sudo systemctl daemon-reload`
5. Start elastiprom: `sudo service elastiprom restart`

### Docker Compose
1. Pull the built container
2. `docker-compose up --env-file=env`

### Docker Run
1. Build the container (in project root): `docker build -t elastiprom:latest .`
2. Run it:
```
docker run --rm -it -p 8080:8080 --name elastiprom --env-file env elastiprom:latest
```

## <a name="run-systemd"></a>As a SystemD Service

1. Upload the jar file to `/usr/share/elastiprom/`.
2. Copy the `elastiprom.service` file to `/etc/systemd/system/`
3. Create a `.env` file with your settings in it in `/usr/share/elastiprom/`
4. Reload systemctl: `sudo systemctl daemon-reload`
5. Start elastiprom: `sudo service elastiprom restart`

## <a name="run-intellij"></a>Via IntelliJ
Make sure that you configure IntelliJ to use the gradle wrapper when running the project.

1. Open the gradle tool tab
2. Navigate to `tasks` -> `application` -> `bootRun`
3. Right click and choose: `Edit 'elastiprom [bootRun]'`
4. In the resulting dialog, under the `Configuration` tab, click on the icon at the right of the `Environment variables` input
5. Enter your environment settings (`ES_HOST`, `ES_PORT` etc)

Now you can run this task and it will start up with the right settings in place.

# <a name="acknowledgements"></a>Acknowledgements
The main acknowlegement must go to [Jacek S](https://github.com/jsuchenia/elasticsearch-prometheus-metrics), where most of this code comes from.

After attempting to fork that repo's elasticsearch-plugin and write a version for the latest elastic, and realising that elastic had protected a bunch of methods required for it to work, then 
I decided to separate it from elasticsearch and run it as a spring-boot instead.
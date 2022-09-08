# Websockets chess application (Backend)
[![Generic badge](https://img.shields.io/badge/Made%20with-Spring%20Boot%202.7.3-1abc9c.svg)](https://www.java.com/en/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Build%20with-Gradle-green.svg)](https://maven.apache.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Packaging-War%20-brown.svg)](https://maven.apache.org/)&nbsp;&nbsp;
<br><br>
Server layer of an application that enables the game of chess and checkers. Written using the Spring Boot framework 
along with several other dependencies to enable the websocket protocol. The application allows you to create an account, 
log in and track statistics of your games.

## Table of content
* [Clone and install](#clone-and-install)
* [Prepare development configuration](#prepare-development-configuration)
* [Prepare production configuration](#prepare-production-configuration)
* [Author](#author)
* [Project status](#project-status)
* [License](#license)

<a name="clone-and-install"></a>
## Clone and install

To install the program on your computer, use the command below (or use the build-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/Websockets_Chess_Application_Backend
```

<a name="prepare-development-configuration"></a>
## Prepare development configuration
1. The application in debug mode uses the default H2 database. The database is configured and after cloning the 
repository, all you have to do is go to `http://127.0.0.1:7575/h2-console` and log in (username: `sa`, without password).
2. Download the XAMPP installer from [this link](https://www.apachefriends.org/download.html).
3. Install XAMPP (make sure you install XAMPP together with the Mercury mail agent).
4. Click on the `Admin` tab to open the Mercury configuration window.
5. Create reciever account. Go to `Configuration -> Manage local users` and click `Add`. Insert:
```
Username: root
Personal Name: root
Mail password: admin
APOP secret: admin
```
6. Create sender account. Go to `Configuration -> Manage local users` and click `Add`. Insert:
```
Username: noreply-dev
Personal Name: noreply-dev
Mail password: admin
APOP secret: admin
```
7. Go to `Configuration -> Mercury Core Module Configuration` and click in `General` and insert in `Internet name for
this system: localhost`.<br>
8. Map localhost domain. Go to `Local domains` and click `Add new doman`. Your domain translation table should be like this:

| Local host or server | Internet name |
|----------------------|---------------|
| localhost            | localhost     |
| localhost            | localhost.com |
| localhost            | 127.0.0.1     |

9. Configure SMTP server. Go to `Configuration -> MercuryS SMTP Server -> General` and filled with data below:
```
Announce myself as: 127.0.0.1
Listen on TCP/IP port: 25
```
10. Configure SMTP client. Go to `Configuration -> MercuryE SMTP Client -> General` and set value of `Identity myself as`
to `127.0.0.1`.
11. Return to XAMPP control panel, and start Mercury via `Start` button.
> NOTE: For more info about XAMPP Mercury SMTP server configuration, go 
> [under this link](https://www.c-sharpcorner.com/UploadFile/c8aa13/send-mail-on-local-host-via-mercury-with-xampp/).
12. Run Java application with `--dev` switch. Application should be available on `http://127.0.0.1:7575/javabean/v1/app`.
> NOTE: If you do not use any switch, the application will not start correctly.

<a name="prepare-production-configuration"></a>
## Prepare production configuration

1. Prepare the relational database server and the SMTP mail server (log-in details should be entered according to the 
instructions below). Also check that both servers provide an SSL encrypted connection.
2. Create `application-prod.yml`, file for production configuration and place in `/resources` directory.
3. Fill in the content shown below:
```yaml
config:
  # front-end layer address (for CORS policy)
  frontend-cors-url: https://chess.miloszgilga.pl/
  # front-end browser shorten address (unsing mainly in email templates)
  frontend-name: chess.miloszgilga.pl
  authorization:
    # bearer salt key
    jwt-key: xxxxx

spring:
  # enable/disable h2 console
  h2.console.enabled: false
  datasource:
    # database connection string
    url: jdbc:[dbType]://[dbHost]:[dbPort]/[dbName]
    username: xxxxx
    password: xxxxx
    # database driver class (example for MySQL database)
    driver-class-name: com.mysql.cj.jdbc.Driver
  mail:
    protocol: smtp
    # for secure connections 465, for non-secure 587
    port: 465
    # email agent host, ex. example.mail.net
    host: xxxxx
    username: xxxxx
    password: xxxxx
    properties:
      # true, if your mail agent allows to TLS connection
      smtp.starttls.required: true
      mail.smtp:
        # true, if your mail agent has enable SSL connection
        ssl.enable: true
        auth: true
        starttls.enable: true

logging:
  # output directory for logging to file by log4j (location: src/prod-logs...)
  file.name: prod-logs/chessappbackend.log
```
4. To run application in production mode, use `--prod` switch.
> NOTE: If you do not use any switch, the application will not start correctly.

<a name="author"></a>
## Author
Created by Miłosz Gilga. If you have any questions about the application send message:
[personal@miloszgilga.pl](mailto:personal@miloszgilga.pl).

<a name="project-status"></a>
## Project status
Project is still in development.

<a name="license"></a>
## License
This application is on MIT License [terms of use](https://en.wikipedia.org/wiki/MIT_License).

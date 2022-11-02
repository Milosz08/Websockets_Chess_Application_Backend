# Websockets chess application (Backend)
[![Generic badge](https://img.shields.io/badge/Made%20with-Spring%20Boot%202.7.3-1abc9c.svg)](https://www.java.com/en/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Build%20with-Gradle-green.svg)](https://gradle.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Packaging-Jar%20-brown.svg)](https://gradle.org/)&nbsp;&nbsp;
<br><br>
Server layer of an application that enables the game of chess and checkers. Written using the Spring Boot framework 
along with several other dependencies to enable the websocket protocol. The application allows you to create an account, 
log in and track statistics of your games.

See live application at: [chess.miloszgilga.pl](https://chess.miloszgilga.pl/)<br>
See frontend (client layer): [Websockets Chess Application Frontend](https://github.com/Milosz08/Websockets_Chess_Application_Frontend)

> NOTE! This project requires the latest version of the [JMPS library](https://github.com/Milosz08/JMPS_Library). At the moment, the library is in the testing phase and is not available in maven central, but can be attached to the project using Maven Local. For more information, [go here](https://github.com/Milosz08/JMPS_Library).

## Table of content
* [Clone and install](#clone-and-install)
* [Prepare development configuration](#prepare-development-configuration)
* [Prepare production configuration](#prepare-production-configuration)
* [Generate OpenSSH key](#generate-openssh-key)
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
repository, all you have to do is go to `http://127.0.0.1:9595/h2-console` and log in (username: `sa`, without password).
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
12. Create simple static resources server on your local machine (ex. via VirtualBox) and generate OpenSSH public and private key with `known_hosts.dat` file via [this tutorial](#generate-openssh-key).
13. Create `.env` file or add environment server variables (file must be located in root application directory).
14. Fill in the content shown below:
```properties
# OAuth2 authentication tokens
DEV_OAUTH2_GOOGLE_CLIENT_SECRET         = xxxxx -> <your-google-client-secret>
DEV_OAUTH2_GOOGLE_CLIENT_ID             = xxxxx -> <your-google-client-id>
DEV_OAUTH2_FACEBOOK_CLIENT_SECRET       = xxxxx -> <your-facebook-client-secret>
DEV_OAUTH2_FACEBOOK_CLIENT_ID           = xxxxx -> <your-facebook-client-id>

# OAuth2 authentication miscs
DEV_OAUTH2_TOKEN_SECRET                 = xxxxx -> ex. [[G879789N9QVWC897V0F094NMODF]]
DEV_PROD_OAUTH2_PASSWORD_REPLACER       = xxxxx -> ex. [[5m2vFmwfi7]]

# SSH/SFTP connection info
DEV_PROD_SSH_SOCKET_HOST                = xxxxx -> ex. [[192.168.100.9]]
DEV_PROD_SSH_SOCKET_LOGIN               = xxxxx
DEV_PROD_ROOT_SERVER_PATH               = xxxxx -> ex. /path/to/static/resources/directory
DEV_PROD_SFTP_SERVER_URL                = xxxxx -> static resources server, ex. [[https://cdn.exampledomain.pl]]
```
15. Run Java application with `--dev` switch. Application should be available on `http://127.0.0.1:9595/javabean/app/v1`.
> NOTE: If you do not use any switch, the application will not start correctly.

<a name="prepare-production-configuration"></a>
## Prepare production configuration

1. Prepare the relational database server and the SMTP mail server (log-in details should be entered according to the 
instructions below). Also check that both servers provide an SSL encrypted connection.
In addition, prepare a static file server (pseudo CDN), such as S3, and make sure it has support for RSS authentication (public and private key).
2. Generate OpenSSH public and private key with `known_hosts.dat` file via [this tutorial](#generate-openssh-key).
3. Create `.env` file or add environment server variables (file must be located in root application directory).
4. Fill in the content shown below:
```properties
# JWT and CORS policy
PROD_BASE_URL                           = xxxxx -> ex. [[https://api.exampledomain.pl/]]
PROD_FRONTEND_CORS_AGENT                = xxxxx -> ex. [[https://exampledomain.pl/]]
PROD_JWT_KEY                            = xxxxx
PROD_FRONTEND_WITH_JWT_ISSUER           = xxxxx -> ex. [[exampledomain.pl]]

# SMTP email service
PROD_SMTP_MAIL_AGENT                    = xxxxx -> ex. [[mail.example.net]]
PROD_SMTP_MAIL_USERNAME                 = xxxxx
PROD_SMTP_MAIL_PASSWORD                 = xxxxx

# Database connectivity
PROD_DB_CONNECTION_STRING               = jdbc:[dbProvider]://[dbHost]:[dbPort]/[dbName]
PROD_DB_USERNAME                        = xxxxx
PROD_DB_PASSWORD                        = xxxxx

# OAuth2 authentication tokens
PROD_OAUTH2_GOOGLE_CLIENT_SECRET        = xxxxx -> <your-google-client-secret>
PROD_OAUTH2_GOOGLE_CLIENT_ID            = xxxxx -> <your-google-client-id>
PROD_OAUTH2_FACEBOOK_CLIENT_SECRET      = xxxxx -> <your-facebook-client-secret>
PROD_OAUTH2_FACEBOOK_CLIENT_ID          = xxxxx -> <your-facebook-client-id>

# OAuth2 authentication miscs
PROD_OAUTH2_TOKEN_SECRET                = xxxxx -> ex. [[9B8VWI3JHJ3KOHO5O9OQODNNV89]]
DEV_PROD_OAUTH2_PASSWORD_REPLACER       = xxxxx -> ex. [[5m2vFmwfi7]]

# SSH/SFTP connection info
DEV_PROD_SSH_SOCKET_HOST                = xxxxx -> ex. [[192.168.100.9]]
DEV_PROD_SSH_SOCKET_LOGIN               = xxxxx
DEV_PROD_ROOT_SERVER_PATH               = xxxxx -> ex. /path/to/static/resources/directory
DEV_PROD_SFTP_SERVER_URL                = xxxxx -> static resources server, ex. [[https://cdn.exampledomain.pl]]
```
5. Rest of values you can change in `application.properties`, `application-dev.properties` or `application-prod.properties`
   accordingly for the selected server mode.
6. To run application in production mode, use `--prod` switch.
> NOTE: If you do not use any switch, the application will not start correctly.

<a name="generate-openssh-key"></a>
## Generate OpenSSH key
1. Go to project directory.
2. Generate `known_hosts.dat` file via following command, where `login` is your login into SSH/SFTP server service and `server` is the server address, ex. 192.168.100.9:
```
$ ssh -o HostKeyAlgorithms=ssh-ed25519 -o UserKnownHostsFile=known_hosts.dat login@server
```
3. Generate private and public key via OpenSSH (don't change anythink, set to default)
```
$ ssh-keygen -t rsa
```
4. Move public key to your SSH/SFTP server, where `login` is your login to server and `server` is the server address:
```
$ ssh-copy-id -i ~/.ssh/id_rsa.pub login@server
```
5. Move public and private key into ROOT project directory.

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

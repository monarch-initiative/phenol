# Installation


##  Maven Central 



!!! note
    This is the recommended way of installing for normal users.

Simply use the following snippet for your ``pom.xml`` for using phenol modules in your Maven project.

```xml
<dependencies>
  <dependency>
    <groupId>org.monarchinitiative.phenol</groupId>
    <artifactId>phenol-core</artifactId>
    <version>${project.version}</version>
  </dependency>
  <dependency>
    <groupId>org.monarchinitiative.phenol</groupId>
    <artifactId>phenol-io</artifactId>
    <version>${project.version}</version>
  </dependency>
</dependencies>
```

### Install from Source


!!! note
    You only need to install from source if you want to develop Phenol in Java yourself.

### Prerequisites

For building Phenol, you will need

- Java JDK 11 (or higher) for compiling phenol,
- [Maven 3](http://maven.apache.org/) for building phenol, and
- [Git](http://git-scm.com/) for getting the sources.

## Git Checkout and maven build


The following code snippet downloads the phenol sources and builds them.

```bash
git clone https://github.com/monarch-initiative/phenol
cd phenol
mvn package
```


Maven Proxy Settings
====================

If you are behind a proxy, you will get problems with Maven downloading dependencies.
If you run into problems, make sure to also delete ``~/.m2/repository``.
Then, execute the following commands to fill ``~/.m2/settings.xml``.

.. code-block:: console

    $ mkdir -p ~/.m2
    $ test -f ~/.m2/settings.xml || cat >~/.m2/settings.xml <<END
    <settings>
      <proxies>
       <proxy>
          <active>true</active>
          <protocol>http</protocol>
          <host>proxy.example.com</host>
          <port>8080</port>
          <nonProxyHosts>*.example.com</nonProxyHosts>
        </proxy>
      </proxies>
    </settings>
    END


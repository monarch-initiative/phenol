.. _installation:

============
Installation
============

--------------------------
Use Maven Central Binaries
--------------------------

.. note::

    This is the recommended way of installing for normal users.

Simply use the following snippet for your ``pom.xml`` for using OntoLib modules in your Maven project.

.. code-block:: xml

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

.. _install_from_source:

-------------------
Install from Source
-------------------

.. note::

    You only need to install from source if you want to develop Phenol in Java yourself.

Prerequisites
=============

For building Phenol, you will need

#. `Java JDK 8 or higher <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ for compiling OntoLib,
#. `Maven 3 <http://maven.apache.org/>`_ for building OntoLib, and
#. `Git <http://git-scm.com/>`_ for getting the sources.

Git Checkout
============

In this tutorial, we will download the OntoLib sources and build them in ``~/Development/ontolib``.

.. code-block:: console

   ~ # mkdir -p ~/Development
   ~ # cd ~/Development
   Development # git clone https://github.com/phenomics/ontolib.git ontolib
   Development # cd ontolib

Maven Proxy Settings
====================

If you are behind a proxy, you will get problems with Maven downloading dependencies.
If you run into problems, make sure to also delete ``~/.m2/repository``.
Then, execute the following commands to fill ``~/.m2/settings.xml``.

.. code-block:: console

    ontolib # mkdir -p ~/.m2
    ontolib # test -f ~/.m2/settings.xml || cat >~/.m2/settings.xml <<END
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

Building
========

You can build Phenol using ``mvn package``.
This will automatically download all dependencies, build OntoLib, and run all tests.

.. code-block:: console

    phenol # mvn package

In case that you have non-compiling test, you can use the `-DskipTests=true` parameter for skipping them.

.. code-block:: console

    phenol # mvn install -DskipTests=true

Creating Eclipse Projects
=========================

Maven can be used to generate Eclipse projects that can be imported by the Eclipse IDE.
This can be done calling ``mvn eclipse:eclipse`` command after calling ``mvn install``:

.. code-block:: console

    phenol # mvn install
    phenol # mvn eclipse:eclipse

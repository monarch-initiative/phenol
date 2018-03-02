.. _release_howto:

================================
How-To: Release on Maven Central
================================

This page describes the steps to release Phenol on Maven Central. TODO update me

------------------------
Read the following first
------------------------

- http://java.dzone.com/articles/deploy-maven-central
- http://central.sonatype.org/pages/apache-maven.html

------------------------------
Update the ``README.rst`` file
------------------------------

Change the version in the ``README.rst``.

---------------------------------
Update the ``CHANGELOG.rst`` file
---------------------------------

- Update the ``CHANGELOG.rst`` file to reflect the new version.
- Create a new commit with this version.
- Do not create a git tag as this will be done by Maven below.

--------------------------------
Update the demo ``pom.xml`` file
--------------------------------

- Ensure that the version OntoLib dependency is set to the non-``SNAPSHOT`` version.

-------------------------------
Prepare the Release using Maven
-------------------------------

  .. code-block:: shell

    mvn release:prepare

Answer with the default everywhere but use "vMAJOR.MINOR" for giving the
tag name, e.g. "v0.15". Eventually, this will update the versions, create
a tag for the version and also push the tag to Github.

-------------------
Perform the Release
-------------------

  .. code-block:: shell

    mvn release:perform

Create the release and push it to Maven central/Sonatype.

------------------------
Releasing the Deployment
------------------------

Read this:

- http://central.sonatype.org/pages/releasing-the-deployment.html

The publisher backend to Maven Central is here:

- https://oss.sonatype.org/

-----------------------
Update README CHANGELOG
-----------------------

Open README.md and CHANGELOG.md and adjust the files to include the header for the next ``SNAPSHOT`` version.

--------------------------------
Update the demo ``pom.xml`` file
--------------------------------

- Ensure that the dependencies to OntoLib version is set to the non-``SNAPSHOT`` version.

--------------
Maven comments
--------------

* ``mvn versions:set`` is useful for bumping versions

/**
 * Classes for reading and writing empirical score distributions.
 *
 * <p>Currently, score distributions can be read from and written to text files and H2 database
 * files. When generating scores, the text format is useful, the distributions can then be merged
 * into a table in an H2 database files. The final apps can then use the H2 database for smaller
 * file size and high query performance.
 *
 * <h4>H2 Dependency Notes</h4>
 *
 * <p>The class itself only uses JDBC. Thus, the ontolib module does not depend on H2 via maven but
 * your calling code has to depend on H2, e.g., as follows.
 *
 * <pre>
 *  &lt;-- In your pom.xml file --&gt;
 *  &lt;dependency&gt;
 *    &lt;groupId&gt;com.h2database&lt;/groupId&gt;
 *    &lt;artifactId&gt;h2&lt;/artifactId&gt;
 *    &lt;version&gt;${h2.version}&lt;/version&gt;
 *  &lt;/dependency&gt;
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
package org.monarchinitiative.phenol.io.scoredist;

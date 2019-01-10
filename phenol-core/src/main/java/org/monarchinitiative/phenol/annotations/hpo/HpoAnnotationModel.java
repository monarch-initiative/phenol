package org.monarchinitiative.phenol.annotations.hpo;

import com.google.common.collect.ImmutableList;


import java.util.*;

/**
 * This class represents one disease-entity annotation consisting usually of multiple annotations lines, and using
 * the new format introduced in 2018. Colloquially, these files have been called "small files". This class
 * is meant to be used for parsing the files, and does not perform any kind of analysis. THe main use case
 * is to hold the data from one HPO Annotation file, such as {@code OMIM-100200.tab}, which in turn will be
 * use to create the aggregated file called {@code phenotype.hpoa} (the "big-file").
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * Created by peter on 1/20/2018.
 */
public class HpoAnnotationModel {
    /** The base name of the HPO Annotation file. */
    private final String basename;
    /** List of {@link HpoAnnotationEntry} objects representing the original lines of the small file */
    private final List<HpoAnnotationEntry> entryList;
    /** These are the databases currently represented in our data resource. */
    private enum Database {OMIM,DECIPHER,UNKNOWN}
    /** What is the source of the current HpoAnnotationModel? */
    private final Database database;

    /** @return The base name of the HPO Annotation file.*/
    public String getBasename() { return basename; }

    /** The constructor creates an immutable copy of the original list of {@link HpoAnnotationEntry} objects
     * provided by the parser
     * @param name Name of the "small file"
     * @param entries List of {@link HpoAnnotationEntry} objects -- one per line of the small file.
     */
    public HpoAnnotationModel(String name, List<HpoAnnotationEntry> entries) {
        basename=name;
        entryList = ImmutableList.copyOf(entries);
        if (basename.contains("OMIM")) this.database=Database.OMIM;
        else if (basename.contains("DECIPHER")) this.database=Database.DECIPHER;
        else this.database=Database.UNKNOWN;
    }


    public boolean isOMIM(){ return this.database.equals(Database.OMIM); }
    public boolean isDECIPHER() { return this.database.equals(Database.DECIPHER);}


    /** @return the {@link HpoAnnotationEntry} objects -- one per line of the small file.*/
    public List<HpoAnnotationEntry> getEntryList() {
        return entryList;
    }

    public int getNumberOfAnnotations() { return entryList.size(); }

}

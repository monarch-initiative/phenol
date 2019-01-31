package org.monarchinitiative.phenol.annotations.hpo;


import com.google.common.collect.ImmutableSet;
import org.monarchinitiative.phenol.base.HpoAnnotationModelException;
import org.monarchinitiative.phenol.base.ObsoleteTermIdException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by peter on 1/20/2018.
 * This class represents the contents of a single annotation line.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class HpoAnnotationEntry {
    private final static String EMPTY_STRING="";
    /** The CURIE of the disease, e.g., OMIM:600201 (Field #0). */
    private final String diseaseID;
    /** Field #2 */
    private final String diseaseName;
    /** Field #3 */
    private final TermId phenotypeId;
    /** Field #4 */
    private final String phenotypeName;
    /** Field #5 */
    private final String ageOfOnsetId;
    /** Field #6 */
    private final String ageOfOnsetName;
    /** Field #7 */
    private final String evidenceCode;
    /** Field #8 can be one of N/M, X% or a valid frequency term Id */
    private final String frequencyModifier;
    /** Field #9 */
    private final String sex;
    /** Field #10 */
    private final String negation;
    /** Field #11 */
    private final String modifier;
    /** Field #12 */
    private final String description;
    /** Field #13 */
    private final String publication;
    /** Field #14 */
    private final String biocuration;

    private final static String [] expectedFields ={"#diseaseID",
            "diseaseName",
            "phenotypeID",
            "phenotypeName",
            "onsetID",
            "onsetName",
            "frequency",
            "sex",
            "negation",
            "modifier",
            "description",
            "publication",
            "evidence",
            "biocuration"};
    /** Number of tab-separated expectedFields in a valid small file. */
    private static final int NUMBER_OF_FIELDS=expectedFields.length;


    private final static Set<String> validDatabases = ImmutableSet.of("OMIM","DECIPHER","ORPHA");
    /** A set with all of the TermIds for frequency. */
    private final static Set<TermId> frequencySubhierarcyTermIds = ImmutableSet.of(TermId.of("HP:0003674"),TermId.of("HP:0040280"),
            TermId.of("HP:0040281"), TermId.of("HP:0040282"),TermId.of("HP:0040283"),TermId.of("HP:0040284"),
            TermId.of("HP:0040285"));
    /** A set with all of the TermIds for age of onset. */
    private final static Set<TermId> onsetSubhierarcyTermIds = ImmutableSet.of( TermId.of("HP:0003674"), TermId.of("HP:0011460"),
            TermId.of("HP:0003581"), TermId.of("HP:0003596"), TermId.of("HP:0003584"), TermId.of("HP:0011462"),
             TermId.of("HP:0003577"), TermId.of("HP:0003623"), TermId.of("HP:0410280"), TermId.of("HP:0011463"),
             TermId.of("HP:0003593"), TermId.of("HP:0003621"), TermId.of("HP:0030674"), TermId.of("HP:0011461"));

    /** Set of allowable evidence codes. */
    private static final Set<String> EVIDENCE_CODES = ImmutableSet.of("IEA","TAS","PCS");

    private static final Set<String> VALID_CITATION_PREFIXES = ImmutableSet.of("PMID","OMIM","http","https","DECIPHER",
           "ORPHA", "ISBN", "ISBN-10","ISBN-13");
    /** regex for patterns such as HPO:skoehler[2018-09-22] */
    private static final String biocurationRegex = "(\\w+:\\w+)\\[(\\d{4}-\\d{2}-\\d{2})]";
    /** The pattern that corresponds to {@link #biocurationRegex}. */
    private static final Pattern biocurationPattern = Pattern.compile(biocurationRegex);

    private static final TermId EXCLUDED = TermId.of("HP:0040285");

    public String getDiseaseID() {
        return diseaseID;
    }

    /** The disease ID has two parts, the database (before the :) and the id (after the :).
     * @return the database part of the diseaseID. */
    public String getDB() {
        return TermId.of(diseaseID).getPrefix();
    }
    /** The disease ID has two parts, the database (before the :) and the id (after the :).
     * @return the object_ID part of the diseaseID. */
    public String getDB_Object_ID() {
        return TermId.of(diseaseID).getId();
    }
    /** @return the disease name, e.g., Noonan syndrome. */
    public String getDiseaseName() {
        return diseaseName;
    }
    /** @return HPO id of this annotation. */
    public TermId getPhenotypeId() {
        return phenotypeId;
    }
    /** @return HPO term label of this annotation. */
    public String getPhenotypeLabel() {
        return phenotypeName;
    }
    /** @return HPO Id of the age of onset, or null. */
    public String getAgeOfOnsetId() {
        return ageOfOnsetId;
    }
    /** @return HPO term label of age of onset or empty string. */
    public String getAgeOfOnsetLabel() {
        return ageOfOnsetName!=null?ageOfOnsetName:EMPTY_STRING;
    }
    /** @return evidence for this annotation (one of IEA, PCS, TAS). */
    public String getEvidenceCode() {
        return evidenceCode;
    }
    /** @return String representing the frequency modifier. */
    public String getFrequencyModifier() {
        return frequencyModifier!=null?frequencyModifier:EMPTY_STRING;
    }
    /** @return String represeting the sex (MALE or FEMALE) or Empty string. */
    public String getSex() {
        return sex!=null?sex:EMPTY_STRING;
    }
    /** @return the String "NOT" or the empty string. */
    public String getNegation() {
        return negation!=null?negation:EMPTY_STRING;
    }
    /** @return list of one or more HPO term ids (as a semicolon-separated String), or emtpry string. */
    public String getModifier() {
        return modifier!=null?modifier:EMPTY_STRING;
    }
    /** @return (optional) free text description. */
    public String getDescription() {
        return description!=null?modifier:EMPTY_STRING;
    }
    /** @return the citation supporting the annotation, e.g., a PubMed ID. */
    public String getPublication() {
        return publication;
    }
    /** @return a string representing the biocuration history. */
    public String getBiocuration() {
        return biocuration;
    }


  /**
   * This constructor is package-private so that we can use it for merging in
   * {@link HpoAnnotationModel}
   */
    HpoAnnotationEntry(String disID,
                               String diseaseName,
                               TermId phenotypeId,
                               String phenotypeName,
                               String ageOfOnsetId,
                               String ageOfOnsetName,
                               String frequencyString,
                               String sex,
                               String negation,
                               String modifier,
                               String description,
                               String publication,
                               String evidenceCode,
                               String biocuration) {
        this.diseaseID=disID;
        this.diseaseName=diseaseName;
        this.phenotypeId=phenotypeId;
        this.phenotypeName=phenotypeName;
        this.ageOfOnsetId=ageOfOnsetId;
        this.ageOfOnsetName=ageOfOnsetName;
        this.frequencyModifier =frequencyString;
        this.sex=sex;
        this.negation=negation;
        this.modifier=modifier;
        this.description=description;
        this.publication=publication;
        this.evidenceCode=evidenceCode;
        this.biocuration=biocuration;
    }


    /** @return the row that will be written to the V2 file for this entry. */
    @Override public String toString() { return getRow();}

    /**
     * Return the row that will be used to write the V2 small files entries to a file. Note that
     * we replace null strings (which are a signal for no data available) with the empty string
     * to avoid the string "null" being written.
     * @return One row of the "big" file corresponding to this entry
     */
    public String getRow() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                diseaseID,
                diseaseName,
                phenotypeId.getValue(),
                phenotypeName,
                ageOfOnsetId!=null?ageOfOnsetId:EMPTY_STRING,
                ageOfOnsetName!=null?ageOfOnsetName:EMPTY_STRING,
                frequencyModifier!=null?frequencyModifier:EMPTY_STRING,
                sex!=null?sex:EMPTY_STRING,
                negation!=null?negation:EMPTY_STRING,
                modifier!=null?modifier:EMPTY_STRING,
                description!=null?description:EMPTY_STRING,
                publication!=null?publication:EMPTY_STRING,
                evidenceCode!=null?evidenceCode:"",
                biocuration!=null?biocuration:EMPTY_STRING);
    }


    /**
     * Create an {@link HpoAnnotationEntry} object for a line in an HPO Annotation file. By default, we do not
     * replace obsolete term ids here, this should be done with PhenoteFX in the original files.
     * @param line A line from an HPO Annotation file (small file)
     * @param ontology reference to HPO ontology
     * @return corresponding {@link HpoAnnotationEntry} object
     * @throws PhenolException if there were Q/C problems with the line.
     */
    public static HpoAnnotationEntry fromLine(String line, Ontology ontology) throws PhenolException {
        String A[] = line.split("\t");
        if (A.length!= NUMBER_OF_FIELDS) {
            throw new HpoAnnotationModelException(String.format("We were expecting %d expectedFields but got %d for line %s",NUMBER_OF_FIELDS,A.length,line ));
        }
        String diseaseID=A[0];
        String diseaseName=A[1];
        TermId phenotypeId = TermId.of(A[2]);
        String phenotypeName=A[3];
        String ageOfOnsetId=A[4];
        String ageOfOnsetName=A[5];
        String frequencyString=A[6];
        String sex=A[7];
        String negation=A[8];
        String modifier=A[9];
        String description=A[10];
        String publication=A[11];
        String evidenceCode=A[12];
        String biocuration=A[13];

        HpoAnnotationEntry entry = new HpoAnnotationEntry(diseaseID,
                 diseaseName,
                 phenotypeId,
                 phenotypeName,
                 ageOfOnsetId,
                 ageOfOnsetName,
                 frequencyString,
                 sex,
                 negation,
                 modifier,
                 description,
                 publication,
                 evidenceCode,
                 biocuration);
        // if the following method does not throw an Exception, we are good to go!
        performQualityControl(entry, ontology);
        return entry;
    }


  /**
   * Create an {@link HpoAnnotationEntry} object for a line in an HPO Annotation file. By default, we do not
   * replace obsolete term ids here, this should be done with PhenoteFX in the original files.
   * @param line A line from an HPO Annotation file (small file)
   * @param ontology reference to HPO ontology
   * @return corresponding {@link HpoAnnotationEntry} object
   */
  public static Optional<HpoAnnotationEntry> fromLineReplaceObsoletePhenotypeData(String line, Ontology ontology)
   {
    String A[] = line.split("\t");
    if (A.length!= NUMBER_OF_FIELDS) {
      return Optional.empty();
    }
    String diseaseID=A[0];
    String diseaseName=A[1];
    TermId phenotypeId = TermId.of(A[2]);
    String phenotypeName=A[3];
    // replace if out of data
    TermId currentPhenotypeId = ontology.getPrimaryTermId(phenotypeId);
    if (currentPhenotypeId!=null) {
      String currentLabel=ontology.getTermMap().get(currentPhenotypeId).getName();
      phenotypeId=currentPhenotypeId;
      phenotypeName=currentLabel;
    }
    String ageOfOnsetId=A[4];
    String ageOfOnsetName=A[5];
    String frequencyString=A[6];
    String sex=A[7];
    String negation=A[8];
    String modifier=A[9];
    String description=A[10];
    String publication=A[11];
    String evidenceCode=A[12];
    String biocuration=A[13];

    HpoAnnotationEntry entry = new HpoAnnotationEntry(diseaseID,
      diseaseName,
      phenotypeId,
      phenotypeName,
      ageOfOnsetId,
      ageOfOnsetName,
      frequencyString,
      sex,
      negation,
      modifier,
      description,
      publication,
      evidenceCode,
      biocuration);
    // if the following method does not throw an Exception, we are good to go!
    try {
      performQualityControl(entry, ontology);
    } catch (HpoAnnotationModelException e) {
      e.printStackTrace();
      return Optional.empty();
    }
    return Optional.of(entry);
  }


    /**
     * If the frequency of an HPO term is listed in Orphanet as Excluded (0%), then we encode it as
     * a NOT (negated) term.
     * @param diseaseID Orphanet ID, e.g., ORPHA:99776
     * @param diseaseName Orphanet disease name, e.g., Moasic trisomy 9
     * @param hpoId HPO id (e.g., HP:0001234) as String
     * @param hpoLabel corresponding HPO term Label
     * @param frequency Orphanet frequency data as TermId
     * @param ontology reference to HPO ontology
     * @param biocuration A String to represent provenance from Orphanet, e.g., ORPHA:orphadata[2019-01-05]
     * @param replaceObsoleteTermId if true, correct obsolete term ids and do not throw an exception.
     * @return corresponding HpoAnnotationEntry object
     * @throws HpoAnnotationModelException if there is a Q/C problem with the data
     */
    public static HpoAnnotationEntry fromOrphaData(String diseaseID,
                                                   String diseaseName,
                                                   String hpoId,
                                                   String hpoLabel,
                                                   TermId frequency,
                                                   Ontology ontology,
                                                   String biocuration,
                                                   boolean replaceObsoleteTermId) throws HpoAnnotationModelException {

        if (hpoId==null) {
            throw new HpoAnnotationModelException("Null String passed as hpoId for disease " + (diseaseID!=null?diseaseID:"n/a"));
        }
        TermId phenotypeId = TermId.of(hpoId);
        // replace the frequency termid with its string equivalent
        // except if it is Excluded, which we treat as a negative annotation
        String frequencyString = frequency.equals(EXCLUDED) ? EMPTY_STRING : frequency.getValue();
        String negationString = frequency.equals(EXCLUDED) ? "NOT" : EMPTY_STRING;

        if (replaceObsoleteTermId) {
            TermId currentPhenotypeId  = ontology.getPrimaryTermId(phenotypeId);
            if (currentPhenotypeId !=null && ! currentPhenotypeId.equals(phenotypeId)) {
                String newLabel=ontology.getTermMap().get(phenotypeId).getName();
                System.err.println(String.format("Replacing obsolete TermId %s with current ID %s (and obsolete label %s with current label %s)",
                        hpoId,currentPhenotypeId.getValue(),hpoLabel,newLabel));
                phenotypeId=currentPhenotypeId;
                hpoLabel=newLabel;
            }
            // replace label if needed
            if (currentPhenotypeId!=null) { // we can only get new name if we got the new id!
                String currentPhenotypeLabel = ontology.getTermMap().get(phenotypeId).getName();
                if (currentPhenotypeLabel != null && !hpoLabel.equals(currentPhenotypeLabel)) {
                    System.err.println(String.format("Replacing obsolete Term label \"%s\" with current label \"%s\"",
                            hpoLabel, currentPhenotypeLabel));
                    hpoLabel = currentPhenotypeLabel;
                }
            }
        }

        HpoAnnotationEntry entry = new HpoAnnotationEntry(diseaseID,
                diseaseName,
                phenotypeId,
                hpoLabel,
                EMPTY_STRING,
                EMPTY_STRING,
                frequencyString,
                EMPTY_STRING,
                negationString,
                EMPTY_STRING,
                EMPTY_STRING,
                diseaseID,
                "TAS",
                biocuration);
        // if the following method does not throw an Exception, we are good to go!
        performQualityControl(entry, ontology);

        return entry;
    }


    // Q/C methods

    /**
     * This method checks all of the fields of the HpoAnnotationEntry. If there is an error, then
     * it throws an Exception (upon the first error). If no exception is thrown, then the
     * no errors were found.
     * @param entry The {@link HpoAnnotationEntry} to be tested.
     * @param ontology A reference to an HpoOntology object (needed for Q/C'ing terms).
     * @throws HpoAnnotationModelException if any parse error is encountered
     */
    private static void performQualityControl(HpoAnnotationEntry entry, Ontology ontology) throws HpoAnnotationModelException {
        checkDB(entry);
        checkDiseaseName(entry.getDiseaseName());
        checkPhenotypeFields(entry,ontology);
        checkAgeOfOnsetFields(entry.getAgeOfOnsetId(),entry.getAgeOfOnsetLabel(),ontology);
        checkFrequency(entry.getFrequencyModifier(),ontology);
        checkSexEntry(entry.getSex());
        checkNegation(entry.getNegation());
        checkModifier(entry.getModifier(),ontology);
        // description is free text, nothing to check
        checkPublication(entry.getPublication());
        checkEvidence(entry.getEvidenceCode());
        checkBiocuration(entry.getBiocuration());
    }

    /**
     * Checks if the database string is in the set of valid strings ({@link #validDatabases})
     * @param entry SMallFileEntry to be checked for a database String such as OMIM or ORPHA
     * @throws HpoAnnotationModelException if an invalid database code is used
     */
    private static void checkDB(HpoAnnotationEntry entry) throws HpoAnnotationModelException {
        try {
            String db = entry.getDB();
            if (! validDatabases.contains(db) ) {
                throw new HpoAnnotationModelException(String.format("Invalid database symbol: \"%s\"", db));
            }
        } catch (PhenolRuntimeException r) {
            throw new HpoAnnotationModelException("Could not construct database: "+ r.getMessage());
        }
    }

    /** Check that this disease name is present. */
    private static void checkDiseaseName(String name) throws HpoAnnotationModelException {
        if (name==null || name.isEmpty()) {
            throw new HpoAnnotationModelException("Missing disease name");
        }
    }


    /**
     * Check that the id is not an alt_id (i.e., out of date!)
     * @param entry the {@link HpoAnnotationEntry} to be checked
     */
    private static void checkPhenotypeFields(HpoAnnotationEntry entry, Ontology ontology)
            throws HpoAnnotationModelException {
        TermId id = entry.getPhenotypeId();
        String termLabel = entry.getPhenotypeLabel();
        if (id==null) {
            throw new HpoAnnotationModelException("Phenotype id was for \""+termLabel+"\"");
        } else if ( ! ontology.getTermMap().containsKey(id)) {
            throw new HpoAnnotationModelException(String.format("Could not find HPO term id (\"%s\") for \"%s\"",id,termLabel));
        } else {
            TermId current = ontology.getTermMap().get(id).getId();
            if (! current.equals(id)) {
                throw new ObsoleteTermIdException(String.format("Usage of (obsolete) alt_id %s for %s (%s)",
                        id.getValue(),
                        current.getValue(),
                        ontology.getTermMap().get(id).getName()));
            }
        }
        // if we get here, the TermId of the HPO Term was OK
        // now check that the label corresponds to the TermId
        if (termLabel==null || termLabel.isEmpty()) {
            throw new HpoAnnotationModelException("Missing HPO term label for id="+id.getValue());
        }
        String currentLabel = ontology.getTermMap().get(id).getName();
        if (! currentLabel.equals(termLabel)) {
                String errmsg = String.format("Wrong term label %s instead of %s for %s",
                        termLabel,
                        currentLabel,
                        ontology.getTermMap().get(id).getName());
                throw new HpoAnnotationModelException(errmsg);
        }
    }


    private static void checkAgeOfOnsetFields(String id, String termLabel, Ontology ontology)
            throws HpoAnnotationModelException {
        if (id==null || id.isEmpty() ) {
            // valid, onset is not required, but let's check that there is not a stray label
            if (termLabel !=null && ! termLabel.isEmpty()) {
                throw new HpoAnnotationModelException("Onset ID empty but Onset label present");
            } else {
                return; // OK!
            }
        }
        TermId tid = TermId.of(id);
        if (! ontology.getTermMap().containsKey(tid)) {
            throw new HpoAnnotationModelException(String.format("Onset ID not found: \"%s\"",id));
        }
        TermId current = ontology.getTermMap().get(tid).getId();
        if (! current.equals(tid)) {
            throw new ObsoleteTermIdException(String.format("Usage of (obsolete) alt_id %s for %s (%s)",
                    tid.getValue(),
                    current.getValue(),
                    ontology.getTermMap().get(tid).getName()));
        }
        if (! onsetSubhierarcyTermIds.contains(tid)) {
            throw new HpoAnnotationModelException("Invalid ID in onset ID field: \""+tid.toString()+"\"");
        }
        // if we get here, the Age of onset id was OK
        // now let's check the label
        if (termLabel==null || termLabel.isEmpty()) {
            throw new HpoAnnotationModelException("Missing HPO term label for onset id="+id);
        }
        String currentLabel = ontology.getTermMap().get(tid).getName();
        if (! currentLabel.equals(termLabel)) {
            String errmsg = String.format("Wrong onset term label %s instead of %s for %s",
                    termLabel,
                    currentLabel,
                    ontology.getTermMap().get(tid).getName());
            throw new HpoAnnotationModelException(errmsg);
        }
    }

    private static void checkEvidence(String evi) throws HpoAnnotationModelException {
        if (! EVIDENCE_CODES.contains(evi)) {
           throw new HpoAnnotationModelException(String.format("Invalid evidence code: \"%s\"",evi));
        }
    }


    /** There are 3 correct formats for frequency. For example, 4/7, 32% (or 32.6%), or
     * an HPO term from the frequency subontology. */
    private static void checkFrequency(String freq, Ontology ontology) throws HpoAnnotationModelException {
        // it is ok not to have frequency data
        if (freq==null || freq.isEmpty()) {
            return;
        }
        if (freq.matches("\\d+/\\d+") ||
                freq.matches("\\d{1,3}%") ||
                freq.matches("\\d{1,3}\\.\\d+%")) {
            // valid numerical frequencies!
            return;
        } else if (! freq.matches("HP:\\d{7}")) {
            // cannot be a valid frequency term
            throw new HpoAnnotationModelException(String.format("Malformed frequency term: \"%s\"", freq));
        }
        // if we get here and we can validate that the frequency term comes from the right subontology,
        // then the item is valid
        TermId id;
        try {
            id = TermId.of(freq);
        } catch (PhenolRuntimeException pre) {
            throw new HpoAnnotationModelException(String.format("Could not parse frequency term id: \"%s\"", freq));
        }
        if (! frequencySubhierarcyTermIds.contains(id) ) {
            throw new HpoAnnotationModelException(String.format("Usage of incorrect term for frequency: %s [%s]",
                    ontology.getTermMap().get(id).getName(),
                    ontology.getTermMap().get(id).getId().getValue()));
        }
    }

    /**
     * The sex entry is used for annotations that are specific to either males or females. It is usually
     * empty. If present, it must be either MALE or FEMALE (for now we do no enforce capitalization).
     * @param sex THe sex-specificity entry
     * @throws HpoAnnotationModelException if the sex-specifity field is malformed.
     */
    private static void checkSexEntry(String sex) throws HpoAnnotationModelException {
        if (sex==null || sex.isEmpty()) return; // OK,  not required
        if (! sex.equalsIgnoreCase("MALE") && ! sex.equalsIgnoreCase("FEMALE"))
            throw new HpoAnnotationModelException(String.format("Malformed sex entry: \"%s\"", sex));
    }

    /**
     * The negation string can be null or empty but if it is present it must be "NOT"
     * @param negation Must be either the empty/null String or "NOT"
     */
    private static void checkNegation(String negation) throws HpoAnnotationModelException {
        if ( negation!=null &&  ! negation.isEmpty() && ! negation.equals("NOT")) {
            throw new HpoAnnotationModelException(String.format("Malformed negation entry: \"%s\"", negation));
        }
    }

    private static void checkModifier(String modifierString, Ontology ontology) throws HpoAnnotationModelException {
        if (modifierString==null || modifierString.isEmpty()) return; // OK,  not required
        // If something is present in this field, it must be in the form of
        // HP:0000001;HP:0000002;...
        TermId clinicalModifier = TermId.of("HP:0012823");
        TermId temporalPattern = TermId.of("HP:0011008");
        TermId paceOfProgression = TermId.of("HP:0003679");
        String A[] = modifierString.split(";");
        for (String a: A) {
            try {
                TermId tid = TermId.of(a);
                Set<TermId> ancs = ontology.getAncestorTermIds(tid);
                if (! ancs.contains(clinicalModifier) && ! ancs.contains(temporalPattern) &&
                        ! ancs.contains(paceOfProgression)) {
                    throw new HpoAnnotationModelException(String.format("Use of wrong HPO term in modifier field: %s [%s]",
                            ontology.getTermMap().get(tid).getName(),
                            tid.getValue()));
                }
            } catch (PhenolRuntimeException e) {
                throw new HpoAnnotationModelException(String.format("Malformed modifier term id: \"%s\"",a));
            }
        }
    }


    private static void checkPublication(String pub) throws HpoAnnotationModelException {
        if (pub == null || pub.isEmpty()) {
            throw new HpoAnnotationModelException("Empty citation string");
        }
        int index = pub.indexOf(":");
        if (index <= 0) { // there needs to be a colon in the middle of the string
            throw new HpoAnnotationModelException(String.format("Malformed citation id (not a CURIE): \"%s\"", pub));
        }
        if (pub.contains("::")) { // should only be one colon separating prefix and id
            throw new HpoAnnotationModelException(String.format("Malformed citation id (double colon): \"%s\"", pub));
        }
        if (pub.contains(" ")) {
            throw new HpoAnnotationModelException(String.format("Malformed citation id (contains space): \"%s\"", pub));
        }
        String prefix = pub.substring(0,index);
        if (!VALID_CITATION_PREFIXES.contains(prefix)) {
            throw new HpoAnnotationModelException(String.format("Did not recognize publication prefix: \"%s\" ",  pub));
        }
        int len = pub.length();
        if (len-index < 2) {
            throw new HpoAnnotationModelException(String.format("Malformed publication string: \"%s\" ", pub));
        }
    }

    private static void checkBiocuration(String entrylist) throws HpoAnnotationModelException {
        if (entrylist==null || entrylist.isEmpty()) {
            throw new HpoAnnotationModelException("empty biocuration entry");
        }
        String fields[] = entrylist.split(";");
        for (String f : fields) {
            Matcher matcher = biocurationPattern.matcher(f);
            if (! matcher.find()) {
                throw new HpoAnnotationModelException(String.format("Malformed biocuration entry: \"%s\"",f));
            }

        }
    }


}

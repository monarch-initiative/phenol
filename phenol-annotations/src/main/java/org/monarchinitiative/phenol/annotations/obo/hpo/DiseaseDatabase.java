package org.monarchinitiative.phenol.annotations.obo.hpo;

import java.util.Locale;

public enum DiseaseDatabase {
   OMIM("OMIM"), ORPHANET("ORPHA"), DECIPHER("DECIPHER"), UNKNOWN("UNKNOWN");
   private final String name;

   DiseaseDatabase(String n) {
       this.name = n;
   }

   public static DiseaseDatabase fromString(String s) {
       switch (s.toUpperCase(Locale.ROOT)) {
           case "OMIM": return OMIM;
           case "ORPHA":
           case "ORPHANET": return ORPHANET;
           case "DECIPHER": return DECIPHER;
           default: return UNKNOWN;
       }
   }

}

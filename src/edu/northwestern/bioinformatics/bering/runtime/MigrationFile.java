package edu.northwestern.bioinformatics.bering.runtime;

import java.io.File;

/**
 * @author rsutphin
 */
public abstract class MigrationFile implements Comparable<MigrationFile> {
    private File file;
    private String name;
    private Integer number;

    public MigrationFile(File file) {
        this.file = file;

        // allow nulls for testing
        if (file != null) {
            String filename = stripExtension(file.getName());

            int firstUnderscore = filename.indexOf('_');
            if (firstUnderscore < 0) {
                initIndexAndName(filename, null);
            } else {
                initIndexAndName(
                    filename.substring(0, firstUnderscore),
                    filename.substring(firstUnderscore + 1)
                );
            }
        }
    }

    private String stripExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot < 0 ? filename : filename.substring(0, lastDot);
    }

    protected void initIndexAndName(String indexString, String nameString) {
        name = nameString;
        number = new Integer(indexString);
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public File getFile() {
        return file;
    }

    public int compareTo(MigrationFile o) {
        return getNumber() - o.getNumber();
    }
}

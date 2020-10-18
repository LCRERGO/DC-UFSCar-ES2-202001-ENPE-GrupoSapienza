/*  Copyright (C) 2003-2015 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.sf.jabref.importer.fileformat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.jabref.importer.ImportFormatReader;
import net.sf.jabref.importer.OutputPrinter;
import net.sf.jabref.model.entry.BibEntry;

/**
 * Importer for the CSV format.
 */
public class CSVImporter extends ImportFormat {

    private static final Pattern RECOGNIZED_FORMAT_PATTERN = Pattern.compile("BibliographyType,.*");

    /**
     * Return the name of this import format.
     */
    @Override
    public String getFormatName() {
        return "CSV";
    }

    /*
     *  (non-Javadoc)
     * @see net.sf.jabref.imports.ImportFormat#getCLIId()
     */
    @Override
    public String getCLIId() {
        return "csv";
    }

    /**
     * Check whether the source is in the correct format for this importer.
     */
    @Override
    public boolean isRecognizedFormat(InputStream stream) throws IOException {
        // Our strategy is to look for the "BibliographyType,*" line.
        try (BufferedReader in = new BufferedReader(ImportFormatReader.getReaderDefaultEncoding(stream))) {
            String str;
            while ((str = in.readLine()) != null) {
                if (RECOGNIZED_FORMAT_PATTERN.matcher(str).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Parse the entries in the source, and return a List of BibEntry
     * objects.
     */
    @Override
    public List<BibEntry> importEntries(InputStream stream, OutputPrinter status) throws IOException {
        List<BibEntry> bibEntries = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader in = new BufferedReader(ImportFormatReader.getReaderDefaultEncoding(stream))) {
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str + '\n');
            }
        }
        String[] entries = sb.toString().split("\n");
        String[] fields = entries[0].toString().split(","); // Field Names

        Map<String, String> hm = new HashMap<>();
        String[] entryTypes = {"", "book", "booklet", "proceedings", "", "inbook", "inproceedings", "article", "manual",
                "phdthesis", "misc", "", "", "techreport", "unpublished"}; // OpenOffice values

        for (int i = 1; i < entries.length; i++) {

            hm.clear();
            String[] values = entries[i].split(",");

            if (values.length != fields.length) {
                return Collections.emptyList();
            }

            String entryType = "misc";
            try {
                String type = entryTypes[Integer.parseInt(values[0])];
                if (type != "") {
                    entryType = type;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                entryType = "misc";
            } catch (NumberFormatException e) {
                return Collections.emptyList();
            }

            BibEntry bibEntry = new BibEntry(DEFAULT_BIBTEXENTRY_ID, entryType);

            for (int j = 1; j < fields.length; j++) {
                hm.put(fields[j], values[j].replace("\"", ""));
            }

            bibEntry.setField(hm);
            bibEntries.add(bibEntry);
        }

        return bibEntries;
    }

}

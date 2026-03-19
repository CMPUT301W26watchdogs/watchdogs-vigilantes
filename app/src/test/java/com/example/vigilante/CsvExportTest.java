// testing CSV export logic including field escaping, content building and edge cases US 02.06.05

package com.example.vigilante;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CsvExportTest {

    // verifying simple field without special characters is returned as is US 02.06.05
    @Test
    public void escapeCsvField_simpleText_unchanged() {
        assertEquals("Alice", viewAttendee.escapeCsvField("Alice"));
    }

    // verifying field with comma is wrapped in quotes US 02.06.05
    @Test
    public void escapeCsvField_withComma_wrapped() {
        assertEquals("\"Alice, Bob\"", viewAttendee.escapeCsvField("Alice, Bob"));
    }

    // verifying field with double quote is escaped and wrapped US 02.06.05
    @Test
    public void escapeCsvField_withQuote_escaped() {
        assertEquals("\"She said \"\"hello\"\"\"", viewAttendee.escapeCsvField("She said \"hello\""));
    }

    // verifying field with newline is wrapped in quotes US 02.06.05
    @Test
    public void escapeCsvField_withNewline_wrapped() {
        assertEquals("\"Line1\nLine2\"", viewAttendee.escapeCsvField("Line1\nLine2"));
    }

    // verifying null field returns empty string US 02.06.05
    @Test
    public void escapeCsvField_null_returnsEmpty() {
        assertEquals("", viewAttendee.escapeCsvField(null));
    }

    // verifying empty field returns empty string US 02.06.05
    @Test
    public void escapeCsvField_empty_returnsEmpty() {
        assertEquals("", viewAttendee.escapeCsvField(""));
    }

    // verifying CSV header row is present in output US 02.06.05
    @Test
    public void buildCsvContent_hasHeaderRow() {
        List<Entrant> entrants = new ArrayList<>();
        String csv = viewAttendee.buildCsvContent(entrants);
        assertTrue(csv.startsWith("Name,Email,Status\n"));
    }

    // verifying CSV content includes entrant data rows US 02.06.05
    @Test
    public void buildCsvContent_includesEntrantData() {
        List<Entrant> entrants = new ArrayList<>();
        Entrant e = new Entrant();
        e.setName("Alice Johnson");
        e.setEmail("alice@test.com");
        e.setStatus("accepted");
        entrants.add(e);

        String csv = viewAttendee.buildCsvContent(entrants);
        assertTrue(csv.contains("Alice Johnson,alice@test.com,accepted"));
    }

    // verifying CSV with multiple entrants has correct number of lines US 02.06.05
    @Test
    public void buildCsvContent_multipleEntrants_correctLineCount() {
        List<Entrant> entrants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Entrant e = new Entrant();
            e.setName("User " + i);
            e.setEmail("user" + i + "@test.com");
            e.setStatus("accepted");
            entrants.add(e);
        }

        String csv = viewAttendee.buildCsvContent(entrants);
        String[] lines = csv.split("\n");
        // 1 header + 5 data rows
        assertEquals(6, lines.length);
    }

    // verifying CSV handles entrant with null name gracefully US 02.06.05
    @Test
    public void buildCsvContent_nullName_handledGracefully() {
        List<Entrant> entrants = new ArrayList<>();
        Entrant e = new Entrant();
        e.setName(null);
        e.setEmail("test@test.com");
        e.setStatus("accepted");
        entrants.add(e);

        String csv = viewAttendee.buildCsvContent(entrants);
        assertTrue(csv.contains(",test@test.com,accepted"));
    }

    // verifying CSV with special characters in name is properly escaped US 02.06.05
    @Test
    public void buildCsvContent_specialCharInName_escaped() {
        List<Entrant> entrants = new ArrayList<>();
        Entrant e = new Entrant();
        e.setName("O'Brien, James");
        e.setEmail("james@test.com");
        e.setStatus("accepted");
        entrants.add(e);

        String csv = viewAttendee.buildCsvContent(entrants);
        assertTrue(csv.contains("\"O'Brien, James\""));
    }

    // verifying empty entrant list produces only header US 02.06.05
    @Test
    public void buildCsvContent_emptyList_onlyHeader() {
        List<Entrant> entrants = new ArrayList<>();
        String csv = viewAttendee.buildCsvContent(entrants);
        assertEquals("Name,Email,Status\n", csv);
    }
}

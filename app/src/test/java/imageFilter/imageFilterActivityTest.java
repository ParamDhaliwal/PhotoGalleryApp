package imageFilter;

import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class imageFilterActivityTest {


    @Test
    public void filterFilePathsByCriteria_emptyFilePathList() throws ParseException {
        imageFilterActivity imageFilterActivity = new imageFilterActivity(new String[] {}, "961028-961029", "Date");
        assertEquals(new ArrayList<String>(), imageFilterActivity.filterFilePathsByCriteria());
    }

    @Test
    public void filterFilePathsByCriteria_nonExistentSearchType() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {"name1_961028.jpg", "name2_961029.jpg"}, "961028-961029", "NonExistent");
        assertEquals(new ArrayList<String>(), imageFilterActivity.filterFilePathsByCriteria());
    }

    @Test
    public void filterFilePathsByCriteria_basicDateSearch() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {"name1_961028.jpg"}, "961027-961029", "Date");
        assertEquals(new ArrayList<>(Arrays.asList("name1_961028.jpg")), imageFilterActivity.filterFilePathsByCriteria());
    }

    @Test
    public void filterFilePathsByDate_basicDateSearch() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {"name1_961028.jpg"}, "961027-961029", "Date");
        assertEquals(new ArrayList<>(Arrays.asList("name1_961028.jpg")), imageFilterActivity.filterFilePathsByDate());
    }

    @Test
    public void filterFilePathsByDate_emptyFilePathList() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {}, "961027-961029", "Date");
        assertEquals(new ArrayList<String>(), imageFilterActivity.filterFilePathsByDate());
    }

    @Test
    public void filterFilePathsByDate_basicDateSearchWithMultiDates() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {"name1_961028.jpg", "name2_961030.jpg"}, "961027-961029", "Date");
        assertEquals(new ArrayList<>(Arrays.asList("name1_961028.jpg")), imageFilterActivity.filterFilePathsByDate());
    }

    @Test
    public void filterFilePathsByDate_basicDateSearchInclusiveDate() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {"name1_961028.jpg"}, "961028-961029", "Date");
        assertEquals(new ArrayList<>(Arrays.asList("name1_961028.jpg")), imageFilterActivity.filterFilePathsByDate());
    }

    @Test(expected = RuntimeException.class)
    public void filterFilePathsByDate_NotDateRange() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {"name1_961028.jpg"}, "961028961029", "Date");
        assertEquals(new ArrayList<>(Arrays.asList("name1_961028.jpg")), imageFilterActivity.filterFilePathsByDate());
    }

    @Test
    public void filterFilePathsByCriteria_emptySearchCriteria() throws ParseException {
        imageFilterActivity imageFilterActivity  = new imageFilterActivity(new String[] {"name1_961028.jpg", "name2_961028.jpg"}, "", "Date");
        assertEquals(new ArrayList<>(Arrays.asList("name1_961028.jpg", "name2_961028.jpg")), imageFilterActivity.filterFilePathsByCriteria());
    }


}
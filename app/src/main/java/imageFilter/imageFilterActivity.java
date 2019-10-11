package imageFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;

public class imageFilterActivity {
    private String[] filePaths;
    private String searchCriteria;
    private String searchCriteriaType;


    public imageFilterActivity(String[] filePaths, String searchCriteria, String searchCriteriaType)
    {
        this.filePaths = filePaths;
        this.searchCriteria = searchCriteria;
        this.searchCriteriaType = searchCriteriaType;

    }

    public ArrayList<String> filterFilePathsByCriteria() throws ParseException {
        ArrayList<String> filteredFilePaths = new ArrayList<>();

        if(this.searchCriteria.isEmpty())
        {
            return (new ArrayList<>(Arrays.asList(this.filePaths)));
        }
        switch(this.searchCriteriaType)
        {
            case "Date":
                filteredFilePaths = filterFilePathsByDate();
                break;
            default:
                return filteredFilePaths;
        }
        return filteredFilePaths;
    }

    ArrayList<String> filterFilePathsByDate() throws ParseException {
        int i;
        ArrayList<String> filteredFilePaths = new ArrayList<>();
        String[] filePathParts;
        String[] searchCriteriaParts;
        Date fileDate,dateFrom, dateTo;
        if (this.searchCriteria.contains("-"))
        {
            searchCriteriaParts = this.searchCriteria.split("-");
            dateFrom = new SimpleDateFormat("yyMMdd", Locale.CANADA).parse(searchCriteriaParts[0]);
            dateTo = new SimpleDateFormat("yyMMdd", Locale.CANADA).parse(searchCriteriaParts[1]);
        }
        else
        {
            throw new RuntimeException("\"" + this.searchCriteriaType + "\" is not a date range.");
        }

        for(i = 0; i < this.filePaths.length; i++)
        {
            filePathParts = this.filePaths[i].split("_");
            fileDate = new SimpleDateFormat("yyMMdd", Locale.CANADA).parse(filePathParts[1]);
            assert fileDate != null;
            if(fileDate.compareTo(dateFrom) >= 0 && fileDate.compareTo(dateTo) <= 0)
            {
                filteredFilePaths.add(this.filePaths[i]);
            }
        }
        return filteredFilePaths;
    }

}

package gov.va.api.lighthouse.facilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class AddNewWebsites {
    private static final String WEBSITES_PATH =
            "./facilities/src/main/resources/websites.csv";

    private static final String COVID_PATH =
            "./facilities/src/main/resources/COVID-19-Facility-URLs.csv";

    private static final String NEW_WEBSITES_PATH =
            "C:\\Users\\JamesBell\\Downloads\\02_28_22_Vet_Center_Lighthouse_API_URL_Updates.csv";

    private static final String FAILED_WEBSITES_PATH =
            "./facilities/src/main/resources/failed-websites.csv";

    private static final String FAILED_COVID_SITES_PATH =
            "./facilities/src/main/resources/failed-covid-sites.csv";

    private static final boolean VALIDATE_URLS = true;

    public static TreeMap<String, String> getCSV(String path) throws Exception {
        BufferedReader inputReader = new BufferedReader(new FileReader(path));
        CSVParser rows = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(inputReader);
        TreeMap<String, String> csvMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        rows.forEach(
                row -> {
                    csvMap.put(row.get("id"), row.get("url"));
                });
        inputReader.close();
        rows.close();
        return csvMap;
    }

    public static void getNewWebsites() throws Exception {
        BufferedReader inputReader = new BufferedReader(new FileReader(NEW_WEBSITES_PATH));
        TreeMap<String, String> websiteMap = getCSV(WEBSITES_PATH);
        TreeMap<String, String> covidMap = getCSV(COVID_PATH);
        HashMap<String, String> failedWebsites = new HashMap<>();
        HashMap<String, String> failedCovidSites = new HashMap<>();
        CSVParser rows =
                CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader().parse(inputReader);
        List<String> headers = rows.getHeaderNames();
        boolean containsURLs = headers.contains("URL");
        boolean containsCovidURLs = headers.contains("covid-19-vaccineURL");
        for (CSVRecord row : rows){
            String id = row.get("Facility_ID");
            if(containsURLs){
                String url = row.get("URL");
                if (url != null && !url.equals("")) {
                    int statusCode = getStatusCode(url);
                    if(statusCode == 200){
                        websiteMap.put(id.trim(), url.trim());
                    }else{
                        failedWebsites.put(id.trim(), url.trim());
                    }

                }
            }
            if(containsCovidURLs){
                String covidURL = row.get("covid-19-vaccineURL");
                if (covidURL != null && !covidURL.equals("")) {
                    int statusCode = getStatusCode(covidURL);
                    if(statusCode == 200) {
                        covidMap.put(id.trim(), covidURL.trim());
                    } else{
                        failedCovidSites.put(id.trim(), covidURL.trim());
                    }

                }
            }
        }
        inputReader.close();
        rows.close();
        if(VALIDATE_URLS){
            updateCSV(failedWebsites, FAILED_WEBSITES_PATH);
            updateCSV(failedCovidSites, FAILED_COVID_SITES_PATH);
        }
        updateCSV(websiteMap, WEBSITES_PATH);
        updateCSV(covidMap, COVID_PATH);
    }

    public static void main(String[] args) throws Exception {
        getNewWebsites();
    }

    public static void updateCSV(Map<String, String> csvMap, String path) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write("id,url\n");
        csvMap.forEach(
                (id, url) -> {
                    try {
                        writer.write(id + "," + url + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        writer.close();
    }

    public static int getStatusCode(String siteUrl) throws Exception{
        if(VALIDATE_URLS){
            URL url = new URL(siteUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            con.disconnect();
            return status;
        }else{
            return 200;
        }
    }
}

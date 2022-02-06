package googleSheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Builder;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleSheetsApiController {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static Properties appProp;

    public static ArrayList<String> blocksNames;
    public static ArrayList<String> questions;
    public static ArrayList<String> answers;
    public static ArrayList<String> videos;

    public GoogleSheetsApiController() {
    }

    private static HttpRequestInitializer getCredentials() throws IOException {
        InputStream in = GoogleSheetsApiController.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Файл не найден");
        } else {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(Lists.newArrayList(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets.readonly")));
            return new HttpCredentialsAdapter(credentials);
        }
    }

    public static Properties getProperties() throws IOException {
        if (appProp != null) {
            return appProp;
        } else {
            InputStream in = GoogleSheetsApiController.class.getResourceAsStream("/application.properties");
            if (in == null) {
                throw new FileNotFoundException("Файл не найден");
            } else {
                appProp = new Properties();
                appProp.load(in);
                return appProp;
            }
        }
    }

    public static Sheets service() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
    }

    @SneakyThrows
    public static ArrayList<String> Blocks() {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        String spreadsheetID = getProperties().getProperty("spreadsheet_id");
        Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
        Spreadsheet sheetMetadata = (Spreadsheet) service.spreadsheets().get(spreadsheetID).execute();
        List<Sheet> sheets = sheetMetadata.getSheets();
        blocksNames = new ArrayList<>();
        for (int i = 0; i < sheets.size(); i++) {
            blocksNames.add(sheets.get(i).getProperties().getTitle());
        }
        return blocksNames;
    }

    @SneakyThrows
    public static ArrayList<String> Questions() {
        String spreadsheetID = getProperties().getProperty("spreadsheet_id");
        String range = getProperties().getProperty("cell_range");
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
        Spreadsheet sheetMetadata = (Spreadsheet) service.spreadsheets().get(spreadsheetID).execute();
        List<Sheet> sheets = sheetMetadata.getSheets();
        String range1 = (String) ((SheetProperties) ((Sheet) sheets.get(0)).get("properties")).get("title");
        ValueRange response = (ValueRange) service.spreadsheets().values().get(spreadsheetID, range1 + range).execute();
        List<List<Object>> values = response.getValues();
        Iterator<List<Object>> var10 = values.iterator();
        questions = new ArrayList<>();
        while (var10.hasNext()) {
            List row = var10.next();
            if (!row.isEmpty()) {
                questions.add(row.get(0).toString());
            }
        }
        //System.out.println(questions.get(questions.size()-1));
        return questions;
    }

    @SneakyThrows
    public static ArrayList<String> Answers() {
        String spreadsheetID = getProperties().getProperty("spreadsheet_id");
        String range = getProperties().getProperty("cell_range");
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
        Spreadsheet sheetMetadata = (Spreadsheet) service.spreadsheets().get(spreadsheetID).execute();
        List<Sheet> sheets = sheetMetadata.getSheets();
        String range1 = (String) ((SheetProperties) ((Sheet) sheets.get(0)).get("properties")).get("title");
        ValueRange response = (ValueRange) service.spreadsheets().values().get(spreadsheetID, range1 + range).execute();
        List<List<Object>> values = response.getValues();
        Iterator<List<Object>> var10 = values.iterator();
        answers = new ArrayList<String>();
        while (var10.hasNext()) {
            List row = var10.next();
            if (!row.isEmpty()) {
                answers.add(row.get(1).toString());
            }
        }
        //System.out.println(answers.get(answers.size()-1));
        return answers;
    }

    @SneakyThrows
    public static ArrayList<String> Videos() {
        String spreadsheetID = getProperties().getProperty("spreadsheet_id");
        String range = getProperties().getProperty("cell_range");
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
        Spreadsheet sheetMetadata = (Spreadsheet) service.spreadsheets().get(spreadsheetID).execute();
        List<Sheet> sheets = sheetMetadata.getSheets();
        String range1 = (String) ((SheetProperties) ((Sheet) sheets.get(0)).get("properties")).get("title");
        ValueRange response = (ValueRange) service.spreadsheets().values().get(spreadsheetID, range1 + range).execute();
        List<List<Object>> values = response.getValues();
        Iterator<List<Object>> var10 = values.iterator();
        videos = new ArrayList<String>();
        while (var10.hasNext()) {
            List row = var10.next();
            if (!row.isEmpty()) {
                videos.add(row.get(2).toString());
                //System.out.println(videos);
            }
        }
        //System.out.println(videos.get(videos.size()-1));
        return videos;
    }

}

////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package googleSheets;
//
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.Sheets.Builder;
//import com.google.api.services.sheets.v4.model.Sheet;
//import com.google.api.services.sheets.v4.model.SheetProperties;
//import com.google.api.services.sheets.v4.model.Spreadsheet;
//import com.google.api.services.sheets.v4.model.ValueRange;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.common.collect.Lists;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.GeneralSecurityException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Properties;
//
//public class GoogleSheetsApiController {
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static Properties appProp;
//    public static ArrayList<String> blocksNames;
//    public static ArrayList<String> questions;
//    public static ArrayList<String> answers;
//    public static ArrayList<String> videos;
//
//    public GoogleSheetsApiController() {
//    }
//
//    private static HttpRequestInitializer getCredentials() throws IOException {
//        InputStream in = GoogleSheetsApiController.class.getResourceAsStream("/credentials.json");
//        if (in == null) {
//            throw new FileNotFoundException("Файл не найден");
//        } else {
//            GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(Lists.newArrayList(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets.readonly")));
//            return new HttpCredentialsAdapter(credentials);
//        }
//    }
//
//    public static Properties getProperties() throws IOException {
//        if (appProp != null) {
//            return appProp;
//        } else {
//            InputStream in = GoogleSheetsApiController.class.getResourceAsStream("/application.properties");
//            if (in == null) {
//                throw new FileNotFoundException("Файл не найден");
//            } else {
//                appProp = new Properties();
//                appProp.load(in);
//                return appProp;
//            }
//        }
//    }
//
//    public static Sheets service() throws GeneralSecurityException, IOException {
//        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        return (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
//    }
//
//    public static ArrayList<String> Blocks() {
//        try {
//            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//            String spreadsheetID = getProperties().getProperty("spreadsheet_id");
//            Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
//            Spreadsheet sheetMetadata = (Spreadsheet)service.spreadsheets().get(spreadsheetID).execute();
//            List<Sheet> sheets = sheetMetadata.getSheets();
//            blocksNames = new ArrayList();
//            Iterator var5 = sheets.iterator();
//
//            while(var5.hasNext()) {
//                Sheet sheet = (Sheet)var5.next();
//                blocksNames.add(sheet.getProperties().getTitle());
//            }
//
//            return blocksNames;
//        } catch (Throwable var7) {
//            throw var7;
//        }
//    }
//
//    public static ArrayList<String> Questions() {
//        try {
//            String spreadsheetID = getProperties().getProperty("spreadsheet_id");
//            String range = getProperties().getProperty("cell_range");
//            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//            Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
//            Spreadsheet sheetMetadata = (Spreadsheet)service.spreadsheets().get(spreadsheetID).execute();
//            List<Sheet> sheets = sheetMetadata.getSheets();
//            String range1 = (String)((SheetProperties)((Sheet)sheets.get(0)).get("properties")).get("title");
//            ValueRange response = (ValueRange)service.spreadsheets().values().get(spreadsheetID, range1 + range).execute();
//            List<List<Object>> values = response.getValues();
//            Iterator<List<Object>> var10 = values.iterator();
//            questions = new ArrayList();
//
//            while(var10.hasNext()) {
//                List<Object> row = (List)var10.next();
//                if (!row.isEmpty()) {
//                    questions.add(row.get(0).toString());
//                }
//            }
//
//            return questions;
//        } catch (Throwable var11) {
//            throw var11;
//        }
//    }
//
//    public static ArrayList<String> Answers() {
//        try {
//            String spreadsheetID = getProperties().getProperty("spreadsheet_id");
//            String range = getProperties().getProperty("cell_range");
//            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//            Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
//            Spreadsheet sheetMetadata = (Spreadsheet)service.spreadsheets().get(spreadsheetID).execute();
//            List<Sheet> sheets = sheetMetadata.getSheets();
//            String range1 = (String)((SheetProperties)((Sheet)sheets.get(0)).get("properties")).get("title");
//            ValueRange response = (ValueRange)service.spreadsheets().values().get(spreadsheetID, range1 + range).execute();
//            List<List<Object>> values = response.getValues();
//            Iterator<List<Object>> var10 = values.iterator();
//            answers = new ArrayList();
//
//            while(var10.hasNext()) {
//                List<Object> row = (List)var10.next();
//                if (!row.isEmpty()) {
//                    answers.add(row.get(1).toString());
//                }
//            }
//
//            return answers;
//        } catch (Throwable var11) {
//            throw var11;
//        }
//    }
//
//    public static ArrayList<String> Videos() {
//        try {
//            String spreadsheetID = getProperties().getProperty("spreadsheet_id");
//            String range = getProperties().getProperty("cell_range");
//            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//            Sheets service = (new Builder(httpTransport, JSON_FACTORY, getCredentials())).setApplicationName("Google API").build();
//            Spreadsheet sheetMetadata = (Spreadsheet)service.spreadsheets().get(spreadsheetID).execute();
//            List<Sheet> sheets = sheetMetadata.getSheets();
//            String range1 = (String)((SheetProperties)((Sheet)sheets.get(0)).get("properties")).get("title");
//            ValueRange response = (ValueRange)service.spreadsheets().values().get(spreadsheetID, range1 + range).execute();
//            List<List<Object>> values = response.getValues();
//            Iterator<List<Object>> var10 = values.iterator();
//            videos = new ArrayList();
//
//            while(var10.hasNext()) {
//                List<Object> row = (List)var10.next();
//                if (!row.isEmpty()) {
//                    videos.add(row.get(2).toString());
//                }
//            }
//
//            return videos;
//        } catch (Throwable var11) {
//            throw var11;
//        }
//    }
//}
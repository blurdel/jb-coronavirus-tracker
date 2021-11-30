package com.blurdel.coronaTracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.blurdel.coronaTracker.model.LocationStats;

/*
 * Building a Coronavirus tracker app with Spring Boot and Java - Java Brains Tutorial
 * 
 * https://www.youtube.com/watch?v=8hjNG9GZGnQ
 * 
 */

@Service
public class VirusDataService {

	private static String URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *") // Every 1st hour of a day
	public void fetchData() throws IOException, InterruptedException {
		
		List<LocationStats> newStats = new ArrayList<>();
        
		HttpClient client = HttpClient.newHttpClient();
		
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(httpResponse.body());
        
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new StringReader(httpResponse.body()));
        
        for (CSVRecord record : records) {
//        	System.out.println(record.get("Province/State"));
        	
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
            
//            System.out.println(locationStat);
            newStats.add(locationStat);
        }

        this.allStats = newStats;
	}


	public List<LocationStats> getAllStats() {
		return allStats;
	}
	
}

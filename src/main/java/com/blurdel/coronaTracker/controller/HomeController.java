package com.blurdel.coronaTracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.blurdel.coronaTracker.model.LocationStats;
import com.blurdel.coronaTracker.services.VirusDataService;


@Controller
public class HomeController {

	@Autowired
	VirusDataService dataService;
	
	
	@GetMapping("/")
	public String home(Model model) {
		
		List<LocationStats> allStats = dataService.getAllStats();
		
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
				
		model.addAttribute("locationStats", dataService.getAllStats());
		
		
		return "home";
	}
	
}

package datasa.domain.dto;


import java.util.List;

public record MyPageUserDetailsResponse(
		ApplicationCounts counts,
		List<MyTourItem> myTours,
		List<MyApplicationItem> myApplications)
{}

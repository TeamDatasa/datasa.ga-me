package datasa.service;

import datasa.domain.dto.MyHostedTripDto;
import datasa.domain.entity.Trip;
import datasa.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageHostService {

    private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public List<MyHostedTripDto> getMyHostedTours(Long hostUserId) {

        return tripRepository
                .findByHostUser_UserIdOrderByTripIdDesc(hostUserId)
                .stream()
                .map(MyHostedTripDto::from)
                .toList();
    }


}


package datasa.repository;

import datasa.domain.entity.TripCancel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TripCancelRepository extends JpaRepository<TripCancel, Long> {
	
	Optional<TripCancel> findByApplication_ApplicationId(Long applicationId);
	
	List<TripCancel> findByApplication_ApplicationIdIn(Collection<Long> applicationIds);
}
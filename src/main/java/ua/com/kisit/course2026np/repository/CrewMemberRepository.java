package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.CrewMember;

import java.util.List;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    List<CrewMember> findByCrewId(Long crewId);
    List<CrewMember> findByCrewIdAndRole(Long crewId, String role);
    long countByCrewIdAndRole(Long crewId, String role);
    @Query("SELECT cm FROM CrewMember cm WHERE cm.crew.flight.id = :flightId")
    List<CrewMember> findByFlightId(@Param("flightId") Long flightId);
    List<CrewMember> findByLastNameContainingIgnoreCase(String lastName);
}
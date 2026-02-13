package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.CrewMember;
import ua.com.kisit.course2026np.entity.CrewRole;

import java.util.List;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Integer> {
    List<CrewMember> findByCrewId(Integer crewId);
    List<CrewMember> findByCrewIdAndRole(Integer crewId, String role);
    long countByCrewIdAndRole(Integer crewId, String role);
    @Query("SELECT cm FROM CrewMember cm WHERE cm.crew.flight.id = :flightId")
    List<CrewMember> findByFlightId(@Param("flightId") Integer flightId);
    List<CrewMember> findByLastNameContainingIgnoreCase(String lastName);
}
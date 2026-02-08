package ua.com.kisit.course2026np.repository;

import ua.com.kisit.course2026np.entity.CrewMember;
import ua.com.kisit.course2026np.entity.CrewRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    List<CrewMember> findByRole(CrewRole role);
    List<CrewMember> findByExperienceYearsGreaterThan(int years);
    List<CrewMember> findByCrewId(Long crewId);
}
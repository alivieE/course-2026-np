package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.CrewMember;
import ua.com.kisit.course2026np.entity.CrewRole;

import java.util.List;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    List<CrewMember> findByCrewId(Long crewId);

    List<CrewMember> findByRole(CrewRole role);

    List<CrewMember> findByExperienceYearsGreaterThanEqual(Integer years);

    @Query("SELECT cm FROM CrewMember cm WHERE cm.role = :role " +
            "AND cm.experienceYears >= :minExperience")
    List<CrewMember> findExperiencedByRole(@Param("role") CrewRole role,
                                           @Param("minExperience") Integer minExperience);
}
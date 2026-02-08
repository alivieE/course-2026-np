package ua.com.kisit.course2026np.service;

import ua.com.kisit.course2026np.entity.Crew;
import ua.com.kisit.course2026np.entity.CrewMember;
import ua.com.kisit.course2026np.entity.CrewRole;
import ua.com.kisit.course2026np.repository.CrewRepository;
import ua.com.kisit.course2026np.repository.CrewMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewService {
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Crew createCrew() {
        return crewRepository.save(new Crew());
    }

    @Transactional
    public Crew assignMemberToCrew(Long crewId, CrewMember member) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("Crew not found"));
        member.setCrew(crew);
        crewMemberRepository.save(member);
        return crew;
    }

    public List<CrewMember> getAvailableCrewMembersByRole(CrewRole role) {
        return crewMemberRepository.findByRole(role);
    }

    @Transactional
    public void removeMemberFromCrew(Long memberId) {
        CrewMember member = crewMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setCrew(null);
        crewMemberRepository.save(member);
    }
}
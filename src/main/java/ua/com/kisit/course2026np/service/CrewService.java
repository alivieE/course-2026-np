package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Crew;
import ua.com.kisit.course2026np.entity.CrewMember;
import ua.com.kisit.course2026np.entity.CrewRole;
import ua.com.kisit.course2026np.repository.CrewMemberRepository;
import ua.com.kisit.course2026np.repository.CrewRepository;

import java.util.List;

@Service
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public CrewService(CrewRepository crewRepository,
                       CrewMemberRepository crewMemberRepository) {
        this.crewRepository = crewRepository;
        this.crewMemberRepository = crewMemberRepository;
    }

    public Crew getCrewByFlight(Long flightId) {
        return crewRepository.findByFlightId(flightId)
                .orElseThrow(() ->
                        new RuntimeException("Бригаду не знайдено для рейсу: " + flightId));
    }

    @Transactional
    public Crew createCrew(Long flightId) {
        if (crewRepository.existsByFlightId(flightId)) {
            throw new IllegalStateException(
                    "Бригада для рейсу " + flightId + " вже існує");
        }
        Crew crew = new Crew();
        return crewRepository.save(crew);
    }

    @Transactional
    public void disbandCrew(Long flightId) {
        crewRepository.deleteByFlightId(flightId);
    }

    public List<CrewMember> getMembers(Long crewId) {
        return crewMemberRepository.findByCrewId(crewId);
    }

    public List<CrewMember> getMembersByRole(Long crewId, CrewRole role) {
        return crewMemberRepository.findByCrewId(crewId).stream()
                .filter(m -> m.getRole() == role)
                .toList();
    }

    @Transactional
    public CrewMember addMember(Long crewId, CrewMember member) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() ->
                        new RuntimeException("Бригаду не знайдено: " + crewId));
        member.setCrew(crew);
        return crewMemberRepository.save(member);
    }

    @Transactional
    public CrewMember updateMember(Long memberId, CrewMember updated) {
        return crewMemberRepository.findById(memberId).map(m -> {
            m.setFirstName(updated.getFirstName());
            m.setLastName(updated.getLastName());
            m.setRole(updated.getRole());
            m.setExperienceYears(updated.getExperienceYears());
            return crewMemberRepository.save(m);
        }).orElseThrow(() ->
                new RuntimeException("Члена екіпажу не знайдено: " + memberId));
    }

    @Transactional
    public void removeMember(Long memberId) {
        if (!crewMemberRepository.existsById(memberId)) {
            throw new RuntimeException("Члена екіпажу не знайдено: " + memberId);
        }
        crewMemberRepository.deleteById(memberId);
    }
}
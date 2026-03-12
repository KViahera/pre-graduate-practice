package org.example.backend.service;

import org.example.backend.dto.ContestDTO;
import org.example.backend.entity.Contest;
import org.example.backend.repository.ContestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContestService {

    private final ContestRepository contestRepository;

    public ContestService(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    @Transactional(readOnly = true)
    public Page<ContestDTO> getContests(String search, Pageable pageable) {
        Page<Contest> page = (search != null && !search.isBlank())
            ? contestRepository.findByTitleContainingIgnoreCase(search, pageable)
            : contestRepository.findAll(pageable);

        return page.map(this::mapEntityToDto);
    }

    @Transactional(readOnly = true)
    public ContestDTO getContestById(Integer id) {
        Contest contest = contestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contest not found"));

        return mapEntityToDto(contest);
    }

    @Transactional
    public ContestDTO createContest(ContestDTO dto) {
        Contest contest = new Contest();

        updateEntityFromDto(contest, dto);
        contest = contestRepository.save(contest);

        return mapEntityToDto(contest);
    }

    @Transactional
    public void updateContest(Integer id, ContestDTO dto) {
        Contest contest = contestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contest not found"));

        updateEntityFromDto(contest, dto);
        contestRepository.save(contest);
    }

    @Transactional
    public void deleteContest(Integer id) {
        if (!contestRepository.existsById(id)) {
            throw new RuntimeException("Contest not found");
        }

        contestRepository.deleteById(id);
    }

    private ContestDTO mapEntityToDto(Contest contest) {
        return new ContestDTO(
            contest.getId(),
            contest.getTitle(),
            contest.getStartTime(),
            contest.getDurationMinutes()
        );
    }

    private void updateEntityFromDto(Contest contest, ContestDTO dto) {
        contest.setTitle(dto.name());
        contest.setStartTime(dto.startTime());
        contest.setDurationMinutes(dto.duration());
    }
}
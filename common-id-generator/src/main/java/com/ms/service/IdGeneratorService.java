package com.ms.service;

import com.ms.model.IdSequence;
import com.ms.repository.IdSequenceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class IdGeneratorService {

    private final IdSequenceRepository sequenceRepository;

    @Transactional
    public String generateId(String entityKey, String prefix, int width) {
        IdSequence sequence = sequenceRepository.findById(entityKey)
                .orElseGet(() -> new IdSequence(entityKey, 0L));
        sequence.setValue(sequence.getValue() + 1);
        sequenceRepository.save(sequence);
        return String.format("%s-%0" + width + "d", prefix, sequence.getValue());
    }

    @Transactional
    public String generateDateBasedId(String entityKey, String prefix) {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        String key = entityKey + ":" + datePart;
        IdSequence sequence = sequenceRepository.findById(key)
                .orElseGet(() -> new IdSequence(key, 0L));
        sequence.setValue(sequence.getValue() + 1);
        sequenceRepository.save(sequence);

        return String.format("%s-%s-%08d", prefix, datePart, sequence.getValue());
    }

    @Transactional
    public Long generateLongId(String entityKey) {
        IdSequence sequence = sequenceRepository.findById(entityKey)
                .orElseGet(() -> new IdSequence(entityKey, 0L));
        sequence.setValue(sequence.getValue() + 1);
        sequenceRepository.save(sequence);
        return sequence.getValue();
    }

}


/*



     // Generates a date-based ID like: ORD-26062025-001
/**

@Transactional
public String generateDateBasedId(String entityKey, String prefix) {
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    String dateKey = entityKey + ":" + date;

    IdSequence sequence = sequenceRepository.findById(dateKey)
            .orElseGet(() -> IdSequence.builder().key(dateKey).value(0L).build());

    sequence.setValue(sequence.getValue() + 1);
    sequenceRepository.save(sequence);

    return String.format("%s-%s-%03d", prefix, date, sequence.getValue());
}

   @Transactional
    public String generateDateBasedId(String entityKey, String prefix) {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        IdSequence sequence = sequenceRepository.findById(entityKey + ":" + datePart)
                .orElseGet(() -> new IdSequence(entityKey + ":" + datePart, 0L));
        sequence.setValue(sequence.getValue() + 1);
        sequenceRepository.save(sequence);

        return String.format("%s%s-%03d", prefix, datePart, sequence.getValue());
    }
 */


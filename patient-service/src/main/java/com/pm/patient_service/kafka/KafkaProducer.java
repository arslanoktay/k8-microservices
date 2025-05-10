package com.pm.patient_service.kafka;

import com.pm.patient_service.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate; // kafkatemplate ile mesaj tipimizi belirticez

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        // diğer servislere göndermek istediğimiz her şeyi event e veriyoruz
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED") // event adı ile gönderiyoruz böylece bu eventi isteyenler çeksin diye
                .build();

        try{
            kafkaTemplate.send("patient",event.toByteArray());
        } catch (Exception e) {
            log.info("Error sending PatientCreated event: {}",event);
        }
    }

}

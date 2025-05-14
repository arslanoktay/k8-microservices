package com.example.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    // Belirtilen topicden veri dinleme işlemi bu metot sayesinde olacak. Sanırım Consumer Grouplar bu ıd göre belirlenecek
    // servis ayağa kalktığında oto burayı dinleyecek
    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try { // eğer gelen event ve PatientEvent eşlezmezse diye bunu kullanıyoruz
            PatientEvent patientEvent = PatientEvent.parseFrom(event);

            // ... perform business related logic
            log.info("Received Patient Event: [PatientId={}, PatientName={}, PatientEmail={} ]",
                    patientEvent.getPatientId(),patientEvent.getName(),patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}",e.getMessage());
        }
    }
}

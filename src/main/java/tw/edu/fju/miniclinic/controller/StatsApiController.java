package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.edu.fju.miniclinic.model.StatsResponse;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.HashMap;
import java.util.Map;

@RestController 
@RequestMapping("/api")
public class StatsApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;
    
    @Autowired
    private DoctorRepository doctorRepo;
    
    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getApiStats() {
        int totalDoctors = Math.toIntExact(doctorRepo.count());
        int totalPatients = Math.toIntExact(patientRepo.count());
        int totalAppointments = Math.toIntExact(appointmentRepo.count());

        Map<String, Integer> byStatus = new HashMap<>();
        byStatus.put("BOOKED", Math.toIntExact(appointmentRepo.countByStatus("BOOKED")));
        byStatus.put("COMPLETED", Math.toIntExact(appointmentRepo.countByStatus("COMPLETED")));
        byStatus.put("CANCELLED", Math.toIntExact(appointmentRepo.countByStatus("CANCELLED")));

        StatsResponse response = new StatsResponse(totalDoctors, totalPatients, totalAppointments, byStatus);
        return ResponseEntity.ok(response);
    }
}
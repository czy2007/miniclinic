package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByApptDate(LocalDate apptDate);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    long countByApptDateBetween(LocalDate from, LocalDate to); 
    long countByDoctor(Doctor doctor);
    long countByPatient(Patient patient);

    List<Appointment> findByDoctorAndApptDate(Doctor doctor, LocalDate apptDate);

    // 【為了功能二新增這一行】讓 Spring Data JPA 自動幫你依狀態統計數量
    long countByStatus(String status); // 💡 如果狀態是 Enum，請改成：long countByStatus(AppointmentStatus status);

    @Query("SELECT a.doctor.department, COUNT(a) FROM Appointment a GROUP BY a.doctor.department")
    List<Object[]> countAppointmentsByDepartment();
    
}
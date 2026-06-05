package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;


public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByApptDate(LocalDate apptDate);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    long countByApptDateBetween(LocalDate from, LocalDate to); // 確保這裡沒有打錯字
    long countByDoctor(Doctor doctor);
    long countByPatient(Patient patient);

    List<Appointment> findByDoctorAndApptDate(Doctor doctor, LocalDate apptDate);

    // 專業建議：直接在資料庫層級進行分組統計，效能更好
    @Query("SELECT a.doctor.department, COUNT(a) FROM Appointment a GROUP BY a.doctor.department")
    List<Object[]> countAppointmentsByDepartment();
}
package tw.edu.fju.miniclinic.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

@Controller
public class DashboardController {
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object loggedInDoctorId = session.getAttribute("loggedInDoctorId");
        
        if (loggedInDoctorId == null) {
            return "redirect:/login";
        }

        Doctor doctor = doctorRepo.findById(loggedInDoctorId.toString()).orElse(null);
        if (doctor == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // 撈取該醫師的所有預約資料送往前端
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointmentRepo.findByDoctor(doctor));
        model.addAttribute("loggedInDoctorName", session.getAttribute("loggedInDoctorName"));

        return "dashboard"; // 返回 templates/dashboard.html
    }
}
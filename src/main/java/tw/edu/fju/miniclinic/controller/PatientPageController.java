package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;
import java.util.Optional;

@Controller
public class PatientPageController {

    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/patients")
    public String listPatients(Model model) {
        model.addAttribute("patients", patientRepo.findAll());
        return "patients";
    }

    @GetMapping("/patients/{chartNo}")
    public String patientDetail(@PathVariable String chartNo, Model model) {
        Optional<Patient> patient = patientRepo.findById(chartNo);

        if (patient.isEmpty()) {
            return "redirect:/"; // 找不到病人就回首頁
        }

        model.addAttribute("patient", patient.get());
        return "patient-detail";
    }
}

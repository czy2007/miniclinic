package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tw.edu.fju.miniclinic.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller // 注意：網頁畫面用 @Controller，不是 @RestController
public class StatsController {

    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/stats")
    public String showStatsPage(Model model) {
        // 1. 撈出基本統計資訊
        long doctorCount = doctorRepo.count();
        long patientCount = patientRepo.count();
        long appointmentCount = appointmentRepo.count();

        // 將數據塞入 model，讓前端 HTML 可以用 Thymeleaf 語法（如 ${doctorCount}）讀取
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("appointmentCount", appointmentCount);

        // 2. 進階：依科別分組統計掛號數
        // 運用 Repository 的 @Query 能力 (直接在資料庫統計，效能較好)
        List<Object[]> results = appointmentRepo.countAppointmentsByDepartment();

        // 將 List<Object[]> 轉換為 Map<String, Long> 方便 Thymeleaf 讀取
        Map<String, Long> deptStats = results.stream()
                .collect(Collectors.toMap(
                        res -> (String) res[0], 
                        res -> (Long) res[1]
                ));

        model.addAttribute("deptStats", deptStats);

        return "stats"; // 這會去 templates 資料夾找 stats.html
    }
}
package tw.edu.fju.miniclinic.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import jakarta.validation.Valid;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentForm;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

@Controller
public class AppointmentController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // GET：顯示表單
    @GetMapping("/appointment/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("form", new AppointmentForm());
        model.addAttribute("doctors", doctorRepo.findAll());
        return "appointment-new";
    }

    // POST：接收表單
    @PostMapping("/appointment/new")
    public String submitAppointment(
            @Valid @ModelAttribute AppointmentForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
		model.addAttribute("form", form);
		model.addAttribute("doctors", doctorRepo.findAll());
		return "appointment-new";
	}

        // 1. 驗證輸入是否為空，避免 findById 或 parse 出錯
        if (form.getChartNo() == null || form.getChartNo().isBlank() ||
            form.getDoctorId() == null || form.getDoctorId().isBlank() ||
            form.getApptDate() == null || form.getApptDate().isBlank() ||
            form.getTimeSlot() == null || form.getTimeSlot().isBlank()) {
            return returnToFormWithError(model, form, "請填寫所有必填欄位");
        }

        // 步驟 1：用表單的字串 ID，從資料庫查出真正的物件
        Patient patient = patientRepo.findById(form.getChartNo()).orElse(null);
        Doctor  doctor  = doctorRepo.findById(form.getDoctorId()).orElse(null);

        // 步驟 2：驗證——找不到就回表單顯示錯誤
        if (patient == null || doctor == null) {
            return returnToFormWithError(model, form, "查無此病歷號或醫師，請確認後重試");
        }

        try {
            // 步驟 3：建立 Appointment Entity，設定關聯物件
            Appointment appt = new Appointment();
            appt.setPatient(patient);
            appt.setDoctor(doctor);
            appt.setApptDate(LocalDate.parse(form.getApptDate()));  // 可能因格式錯誤拋出異常
            appt.setTimeSlot(form.getTimeSlot());
            appt.setStatus("BOOKED");

            // 步驟 4：存入資料庫
            Appointment saved = appointmentRepo.save(appt);

            // 步驟 5：把儲存後的物件交給結果頁面
            model.addAttribute("appointment", saved);
            model.addAttribute("form", form); // 補上這行，讓結果頁面可以讀取到 form.chartNo 等資訊
            return "appointment-result";
        } catch (DateTimeParseException e) {
            return returnToFormWithError(model, form, "日期格式錯誤，請選擇正確的日期");
        } catch (Exception e) {
            return returnToFormWithError(model, form, "預約失敗：" + e.getMessage());
        }
    }

    // 封裝重複的錯誤處理邏輯
    private String returnToFormWithError(Model model, AppointmentForm form, String errorMsg) {
        model.addAttribute("error", errorMsg);
        model.addAttribute("form", form);
        model.addAttribute("doctors", doctorRepo.findAll());
        return "appointment-new";
    }
}

package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PasswordForm;

// 注意：這裡必須是 @Controller，絕對不能是 @RestController
@org.springframework.stereotype.Controller
public class DoctorPasswordController {

    @org.springframework.beans.factory.annotation.Autowired
    private DoctorRepository doctorRepo;

    /**
     * GET /password : 顯示修改密碼網頁
     */
    @GetMapping("/password")
    public String showPasswordPage(HttpSession session, org.springframework.ui.Model model) {
        // 從 Session 拿出登入的醫生姓名
        String loggedInDoctorName = (String) session.getAttribute("loggedInDoctorName");
        
        // 放入 Model 供前端 password.html 渲染
        model.addAttribute("loggedInDoctorName", loggedInDoctorName);
        
        // 必須放入一個空的 PasswordForm 物件，供 Thymeleaf 的 th:object 綁定
        model.addAttribute("passwordForm", new PasswordForm());
        
        // 這裡要對應到 templates/password.html
        return "password"; 
    }

    /**
     * POST /password : 處理密碼變更與 5 大驗證步驟
     */
    @PostMapping("/password")
    public String handlePasswordChange(
            @org.springframework.web.bind.annotation.ModelAttribute PasswordForm form,
            HttpSession session,
            org.springframework.ui.Model model) {

        // 先把醫生名字放回去，免得驗證失敗頁面重刷時名字不見
        String loggedInDoctorName = (String) session.getAttribute("loggedInDoctorName");
        model.addAttribute("loggedInDoctorName", loggedInDoctorName);

        // 1. 以 Session 中的 loggedInDoctorId 查詢醫師
        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");
        if (loggedInDoctorId == null) {
            return "redirect:/login"; // 如果沒登入，踢回登入頁
        }
        
        Doctor doctor = doctorRepo.findById(loggedInDoctorId).orElse(null);
        if (doctor == null) {
            model.addAttribute("errorMessage", "找不到該醫師帳號");
            return "password";
        }

        // 2. BCrypt.checkpw 驗證舊密碼
        if (!BCrypt.checkpw(form.getOldPassword(), doctor.getPasswordHash())) {
            model.addAttribute("errorMessage", "舊密碼錯誤");
            return "password";
        }

        // 3. 新密碼與確認密碼不一致
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("errorMessage", "兩次密碼不相符");
            return "password";
        }

        // 4. 新密碼長度少於 8 碼
        if (form.getNewPassword() == null || form.getNewPassword().length() < 8) {
            model.addAttribute("errorMessage", "密碼至少需要 8 個字元");
            return "password";
        }

        // 5. 全部驗證通過：加密、存檔並重導向
        String hashedPw = BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt());
        doctor.setPasswordHash(hashedPw);
        doctorRepo.save(doctor);

        // 成功後重導向回 Dashboard
        return "redirect:/dashboard";
    }
}
package tw.edu.fju.miniclinic.model;

import java.util.Map;

public class StatsResponse {
    // 1. 定義與 JSON 規格完全相同的欄位名稱
    private int totalDoctors;
    private int totalPatients;
    private int totalAppointments;
    private Map<String, Integer> byStatus; // 巢狀物件用 Map 來呈現

    // 2. 建立方便傳入資料的建構子 (Constructor)
    public StatsResponse(int totalDoctors, int totalPatients, int totalAppointments, Map<String, Integer> byStatus) {
        this.totalDoctors = totalDoctors;
        this.totalPatients = totalPatients;
        this.totalAppointments = totalAppointments;
        this.byStatus = byStatus;
    }

    // 3. 建立 Getter 與 Setter (Spring Boot 序列化成 JSON 必備)
    public int getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(int totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Map<String, Integer> getByStatus() {
        return byStatus;
    }

    public void setByStatus(Map<String, Integer> byStatus) {
        this.byStatus = byStatus;
    }
}
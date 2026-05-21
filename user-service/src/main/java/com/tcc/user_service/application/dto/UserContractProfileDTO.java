package com.tcc.user_service.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserContractProfileDTO {
    private Long id;
    private String contractType;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyFee;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(BigDecimal monthlyFee) { this.monthlyFee = monthlyFee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

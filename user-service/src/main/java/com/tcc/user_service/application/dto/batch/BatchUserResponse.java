package com.tcc.user_service.application.dto.batch;

import com.tcc.user_service.application.dto.UserResponse;

import java.util.List;

public class BatchUserResponse {
    private List<UserResponse> data;
    private List<DeniedUser> denied;
    private boolean partial;

    public BatchUserResponse() {}

    public BatchUserResponse(List<UserResponse> data, List<DeniedUser> denied) {
        this.data = data;
        this.denied = denied;
        this.partial = !denied.isEmpty();
    }

    public List<UserResponse> getData() { return data; }
    public void setData(List<UserResponse> data) { this.data = data; }
    public List<DeniedUser> getDenied() { return denied; }
    public void setDenied(List<DeniedUser> denied) { this.denied = denied; }
    public boolean isPartial() { return partial; }
    public void setPartial(boolean partial) { this.partial = partial; }
}

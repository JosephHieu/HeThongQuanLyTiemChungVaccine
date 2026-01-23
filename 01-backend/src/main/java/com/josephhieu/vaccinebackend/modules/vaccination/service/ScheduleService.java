package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.ScheduleResponse;

public interface ScheduleService {

    ScheduleResponse createScheduleService(ScheduleCreationRequest request);
}

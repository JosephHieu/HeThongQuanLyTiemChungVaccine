package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.BatchSummaryResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.ScheduleResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleService {

    ScheduleResponse createScheduleService(ScheduleCreationRequest request);

    PageResponse<ScheduleResponse> getAllSchedules(int page, int size, String search, LocalDate start, LocalDate end);

    ScheduleResponse updateSchedule(UUID id, ScheduleCreationRequest request);

    void deleteSchedule(UUID id);

    ScheduleResponse getScheduleByDateAndShift(LocalDate date, String shift);

    List<LocalDate> getActiveDatesInPeriod(LocalDate start, LocalDate end);

    PageResponse<RegistrationResponse> getRegistrationsByDate(LocalDate date, int page, int size);

    List<BatchSummaryResponse> getAvailableBatches();
}

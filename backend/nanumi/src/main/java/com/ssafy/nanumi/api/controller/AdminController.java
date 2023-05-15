package com.ssafy.nanumi.api.controller;

import com.ssafy.nanumi.api.request.AdminLoginDTO;
import com.ssafy.nanumi.api.request.ReportUserDTO;
import com.ssafy.nanumi.api.request.UserBanDTO;
import com.ssafy.nanumi.api.response.AdminLoginResDTO;
import com.ssafy.nanumi.api.response.ReportAllDTO;
import com.ssafy.nanumi.api.service.AdminService;
import com.ssafy.nanumi.config.response.CustomDataResponse;
import com.ssafy.nanumi.config.response.CustomResponse;
import com.ssafy.nanumi.config.response.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ssafy.nanumi.config.response.exception.CustomSuccessStatus.RESPONSE_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ResponseService responseService;

    /* 관리자 로그인 */
    @PostMapping("/admin/login")
    public CustomDataResponse<AdminLoginResDTO> adminLogin(@RequestBody AdminLoginDTO adminLoginDTO) {

        // TODO : 관리자 ID 검증부분, password match해주는 로직 추가

        AdminLoginResDTO adminLoginResDTO = adminService.adminLogin(adminLoginDTO.getEmail(), adminLoginDTO.getPassword());

        return responseService.getDataResponse(adminLoginResDTO, RESPONSE_SUCCESS);
    }

    /* 신고 목록 조회 */
    @GetMapping("/admin")
    public CustomDataResponse<List<ReportAllDTO>> findReportList() {

        // TODO : 아직 처리되지 않은 신고 목록만 뿌려줘야하는지 고민

        List<ReportAllDTO> reportAllDTOS = adminService.findReportAll();

        return responseService.getDataResponse(reportAllDTOS, RESPONSE_SUCCESS);
    }

    /* 유저 제재 */
    @PatchMapping("/admin")
    public CustomResponse banUser(@RequestBody UserBanDTO userBanDTO) {

        adminService.banUser(userBanDTO);

        return responseService.getSuccessResponse();
    }

    /* 사용자 신고 */
    @PostMapping("/reports/{user-id}")
    public CustomResponse reportUser(@PathVariable("user-id") long userId, @RequestBody ReportUserDTO reportUserDTO) {

        // TODO : OAuth에서 userId(reporterId) 받아와야 함.

        long reporterId = userId;

        adminService.reportUser(reporterId, reportUserDTO);

        return responseService.getSuccessResponse();
    }
}

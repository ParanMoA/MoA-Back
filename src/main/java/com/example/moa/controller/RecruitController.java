package com.example.moa.controller;

import com.example.moa.domain.Recruit;
import com.example.moa.dto.recruit.RecruitModifyDto;
import com.example.moa.dto.recruit.RecruitCreateRequestDto;
import com.example.moa.dto.recruit.RecruitCreateResponseDto;
import com.example.moa.service.recruit.RecruitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruit")
@RequiredArgsConstructor
public class RecruitController{

    @Autowired
    private final RecruitService recruitService;

    @GetMapping
    public ResponseEntity<List<RecruitCreateResponseDto>> getAllRecruits() {
        List<RecruitCreateResponseDto> recruits = recruitService.findAllDesc();
        return ResponseEntity.ok().body(recruits);
    }


    @PostMapping("/create")
    public ResponseEntity<RecruitCreateResponseDto> createRecruit(HttpServletRequest httpServletRequest, @RequestBody RecruitCreateRequestDto requestDto) {
        String email = (String) httpServletRequest.getAttribute("email");
        requestDto.setWriterEmail(email);

        Recruit savedRecruit = recruitService.saveRecruit(requestDto);

        return ResponseEntity.ok().body(RecruitCreateResponseDto.from(savedRecruit));
    }


    @PostMapping("/modify/{id}")
    public ResponseEntity<RecruitCreateResponseDto>  modifyRecruit(HttpServletRequest httpServletRequest, @PathVariable Long id, @RequestBody RecruitModifyDto modifyDto){
        modifyDto.setRecruitId(id);
        String email = (String) httpServletRequest.getAttribute("email");
        modifyDto.setWriterEmail(email);

        Recruit updateRecruit = recruitService.update(modifyDto);
        return ResponseEntity.ok().body(RecruitCreateResponseDto.from(updateRecruit));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecruit(HttpServletRequest httpServletRequest, @PathVariable Long id){
        recruitService.delete(id);
        return ResponseEntity.ok()
                .body(new ApiResponse("삭제 되었습니다.", "200"));
    }
}

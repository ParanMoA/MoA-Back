package com.example.moa.controller;

import com.example.moa.domain.Role;
import com.example.moa.dto.ingredient.IngredientResponseDto;
import com.example.moa.service.recruit.RecruitParticipateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recruit/participate/{id}")
@RequiredArgsConstructor
public class RecruitParticipateController {
    @Autowired
    private final RecruitParticipateService participateService;

    @PostMapping
    public ResponseEntity<?> possibleParticipate(@PathVariable Long id){
        if(!participateService.isMaxPeople(id)){
            return ResponseEntity.badRequest().body("인원 초과입니다.");
        }
        return ResponseEntity.ok()
                .body(new ApiResponse("참여 가능합니다.", "200"));
    }

    @PostMapping("/enter")
    public ResponseEntity<?> participateRecruit(HttpServletRequest httpServletRequest, @PathVariable Long id){
        String email = participateService.getEmailFromToken(httpServletRequest);
        if(!participateService.isMaxPeople(id)){
            return ResponseEntity.badRequest().body("인원 초과입니다.");
        }
        participateService.participationDuplicate(id,email);
        participateService.saveRecruitUser(id,email, Role.USER);
        return new ResponseEntity<>("참여 신청 완료 되었습니다.", HttpStatus.CREATED); //201
    }

    @PostMapping("/ingredients")
    public ResponseEntity<List<IngredientResponseDto>> participateIngredient(HttpServletRequest httpServletRequest){
        String email = participateService.getEmailFromToken(httpServletRequest);
        List<IngredientResponseDto> ingredients = participateService.getIngredientsByEmail(email);
        return ResponseEntity.ok().body(ingredients);
    }
}

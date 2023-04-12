package com.example.moa.dto;

import com.example.moa.domain.Recruit;
import com.example.moa.domain.RecruitUser;
import com.example.moa.domain.User;
import com.example.moa.service.UserService;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class RecruitRequestDto {

    private String foodName;

    private List<String> ingredients;

    private int maxPeople;

    private String recruitDate;

    private String writerEmail;

    private String title;

    private String content;

    public Recruit toEntity(User writer) {

        LocalDateTime localDateTime = LocalDateTime.now();
        String createdAt = localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

        return Recruit.builder()
                .foodName(foodName)
                .ingredients(ingredients)
                .maxPeople(maxPeople)
                .recruitDate(recruitDate)
                .writer(writer)
                .title(title)
                .content(content)
                .createdAt(createdAt)
                .build();
    }
}
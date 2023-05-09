package com.example.moa.service.recruit;

import com.example.moa.domain.*;
import com.example.moa.dto.ingredient.IngredientResponseDto;
import com.example.moa.dto.recruit.RecruitUserDto;
import com.example.moa.exception.DuplicateEmailException;
import com.example.moa.exception.NotFindException;
import com.example.moa.exception.UserNoIngredientException;
import com.example.moa.repository.IngredientRepository;
import com.example.moa.repository.RecruitRepository;
import com.example.moa.repository.RecruitUserRepository;
import com.example.moa.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitParticipateServiceImpl implements RecruitParticipateService{

    @Autowired
    private final RecruitRepository recruitRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RecruitUserRepository recruitUserRepository;
    @Autowired
    private final IngredientRepository ingredientRepository;


    @Override
    public void saveRecruitUser(RecruitUserDto recruitUserDto) {

        Recruit recruit = recruitRepository.findById(recruitUserDto.getRecruitId())
                .orElseThrow( () -> new IllegalArgumentException("Invalid recruit Id"));

        User user = userRepository.findByEmail(recruitUserDto.getUserEmail())
                .orElseThrow( () -> new IllegalArgumentException("Invalid user email"));

        List<Ingredient> ingredients = new ArrayList<>();

        participationDuplicate(recruit,user);

        for(Long i : recruitUserDto.getId()){
            ingredients.add(ingredientRepository.findById(i)
                    .orElseThrow(()->new UserNoIngredientException("No have ingredient")));
        }

        recruit.getRecruitUsers().add(
                RecruitUser.builder()
                .recruit(recruit)
                .user(user)
                .ingredients(ingredients)
                        .build()
        );
    }

    @Override
    public void allowRecruitUser(Long id) {
        RecruitUser recruitUser = recruitUserRepository.findById(id)
                .orElseThrow(() -> new NotFindException("Not find recruitUser"));

        Recruit recruit = recruitUser.getRecruit();
        User user = recruitUser.getUser();

        recruit.addUsers(user);

        recruit.getRecruitUsers().remove(recruitUser);
        recruitUserRepository.delete(recruitUser);
    }

    @Override
    public void participationDuplicate(Recruit recruit,User user) {
        List<RecruitUser> recruitUsers= recruitUserRepository.findByRecruit(recruit);
        recruitUsers.stream()
                .filter(recruitUser -> recruitUser.getUser().equals(user))
                .findFirst()
                .ifPresent(m -> {
                    throw new DuplicateEmailException("Duplicate participate");
                });
    }

    @Override
    public boolean isMaxPeople(Long id){
        Recruit recruit = recruitRepository.findById(id)
                .orElseThrow(()->new NotFindException(id + " recruit not found"));;
        return recruit.getMaxPeople() > recruit.getUsers().size() ? true : false ;
    }


    @Override
    public List<IngredientResponseDto> getIngredientsByEmail(String email) {
        User user= userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNoIngredientException("No have ingredient"));

        for(Ingredient i : user.getIngredients()){
            System.out.println(i.getName());
        }
        return user.getIngredients()
                .stream()
                .map(IngredientResponseDto::from)
                .collect(Collectors.toList());
    }

}

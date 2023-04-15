package com.example.moa.service;

import com.example.moa.domain.Ingredient;
import com.example.moa.domain.User;
import com.example.moa.dto.IngredientDto;
import com.example.moa.repository.IngredientRepository;
import com.example.moa.repository.UserRepository;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Service
@Transactional
@RequiredArgsConstructor
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Translate translate;

    public String uploadReceiptImage(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        //서버에 사진을 저장할 경로 지정
        String url = "C:/Users/tlsss/Desktop/imageMoa/receipt/" + originalFilename;

        multipartFile.transferTo(new File(url));

        return url;
    }

    public String uploadImage(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        //서버에 사진을 저장할 경로 지정
        String url = "C:/Users/tlsss/Desktop/imageMoa/ingredient/" + originalFilename;

        multipartFile.transferTo(new File(url));

        return url;
    }

    public List<String> getLabelsFromImage(String imgFilePath) throws Exception {

        AtomicReference<String> labels = new AtomicReference<>("");

        ImageAnnotatorClient client = ImageAnnotatorClient.create();

        List<String> result;

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            // Reads the image file into memory
            Path path = Paths.get(imgFilePath);
            byte[] data = Files.readAllBytes(path);
            ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder()
                            .addFeatures(feat)
                            .setImage(img)
                            .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            result = new ArrayList<>();

            for (AnnotateImageResponse res : responses) {
                for (EntityAnnotation annotation : res.getLabelAnnotationsList())
                    result.add(annotation.getDescription());
            }
        }
        return result;
    }

    public String translate(String text, String sourceLanguage, String targetLanguage) {
        Translation translation = translate.translate(text, Translate.TranslateOption.sourceLanguage(sourceLanguage), Translate.TranslateOption.targetLanguage(targetLanguage));
        return translation.getTranslatedText();
    }

    public List<String> translateWords(List<String> words, String sourceLanguage, String targetLanguage) {
        List<String> translations = new ArrayList<>();
        for (String word : words) {
            String translation = translate(word, sourceLanguage, targetLanguage);
            translations.add(translation);
        }
        return translations;
    }

    public void register(IngredientDto ingredientDto, User user) {
        LocalDate now = LocalDate.now();

        Ingredient ingredient = Ingredient.builder()
                .name(ingredientDto.getName())
                .user(user)
                .registeredDate(now)
                .purchasedDate(ingredientDto.getPurchasedDate())
                .expirationDate(ingredientDto.getExpirationDate())
                .ingredientImage(ingredientDto.getIngredientImage())
                .receiptImage(ingredientDto.getReceiptImage())
                .build();

        ingredientRepository.save(ingredient);
    }
}
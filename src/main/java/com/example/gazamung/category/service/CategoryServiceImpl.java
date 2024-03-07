package com.example.gazamung.category.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.category.dto.CategoryDto;
import com.example.gazamung.category.entity.Category;
import com.example.gazamung.category.repository.CategoryRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;



    @Override
    public boolean deleteCategory(Long categoryId, Long memberIdx) {
        Optional<Member> memberOpt = memberRepository.findById(memberIdx);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            if (member.getRole().equals(1)) {
                categoryRepository.deleteById(categoryId);
                return true;
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        } else {
            throw new CustomException(CustomExceptionCode.NOT_FOUND);
        }
    }

    @Override
    public List<CategoryDto> getCategoryList() {

        List<Category> categoryList = categoryRepository.findAll();

         return convertToCategoryDtoList(categoryList);

    }


    /**
     * CategoryList 값을 DTO 형태로 변환 메서드  (시영)
     * @param categoryList
     * @return
     */
    private List<CategoryDto> convertToCategoryDtoList(List<Category> categoryList) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categoryList) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setCategoryId(category.getCategoryId());
            categoryDto.setCategoryName(category.getCategoryName());
            // 여기에 필요한 다른 속성을 추가할 수 있습니다.
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

}
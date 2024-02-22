package com.example.gazamung.category.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.category.dto.CategoryCreateRequest;
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
    public boolean createCategory(CategoryCreateRequest req) {
        // 중복된 카테고리 코드 또는 이름이 있는지 확인
        if (categoryRepository.existsByCategoryCodeOrCategoryName(req.getCategoryCode(), req.getCategoryName())) {
            throw new CustomException();
        }

        // 부모 카테고리 정보 가져오기
        Category parent = Optional.ofNullable(req.getParentId())
                .map(id -> categoryRepository.findById(id).orElseThrow(CustomException::new))
                .orElse(null);

        // 새로운 카테고리 생성 및 저장
        categoryRepository.save(new Category(req.getCategoryCode(), req.getCategoryName(), parent));
        return true;
    }


    @Override
    public List<CategoryDto> getCategoryList() {
        // 최상위 계층의 카테고리 (parent_id == null) 를 불러온다.
        List<Category> categories = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();
        return buildCategoryHierarchy(categories, null);
    }


    private List<CategoryDto> buildCategoryHierarchy(List<Category> categories, Long parentId) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        // 재귀 사용
        for (Category category : categories) {
            // 현재 카테고리가 최상위 카테고리인가
            if ((parentId == null && category.getParent() == null)
                    // 현재 카테고리의 parent_id가 parent 와 일치하는가
                    || (category.getParent() != null && category.getParent().getCategoryId().equals(parentId))) {
                CategoryDto categoryDto = CategoryDto.convertToDto(category);
                categoryDto.setChildren(buildCategoryHierarchy(categories, category.getCategoryId()));
                categoryDtos.add(categoryDto);
            }
        }
        return categoryDtos;
    }


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

}
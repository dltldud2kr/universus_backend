package com.example.gazamung.category.entity;

import com.example.gazamung.moim.entity.Moim;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long categoryId;

    private String categoryName;

    // 필요한가?
    private String categoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children;

    public Category(String categoryCode, String categoryName, Category parent){
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.parent = parent;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }


    public List<Moim> getMoimsInSubcategories() {
        List<Moim> moims = new ArrayList<>();
        for (Category child : children) {
            moims.addAll(child.getMoimsInSubcategories());
        }
        moims.addAll(moims);
        return moims;
    }

}

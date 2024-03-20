package com.example.gazamung.universityFetcher;

import java.util.HashMap;
import java.util.Map;

public class RegionCodeMapper {
    private static final Map<String, String> regionCodeMap = new HashMap<>();
    private static final Map<String, String> regionMap = new HashMap<>();

    static {
        regionCodeMap.put("서울특별시", "100260");
        regionCodeMap.put("부산광역시", "100267");
        regionCodeMap.put("인천광역시", "100269");
        regionCodeMap.put("대전광역시", "100271");
        regionCodeMap.put("대구광역시", "100272");
        regionCodeMap.put("울산광역시", "100273");
        regionCodeMap.put("광주광역시", "100275");
        regionCodeMap.put("경기도", "100276");
        regionCodeMap.put("강원특별자치도", "100278");
        regionCodeMap.put("충청북도", "100280");
        regionCodeMap.put("충청남도", "100281");
        regionCodeMap.put("전북특별자치도", "100282");
        regionCodeMap.put("전라남도", "100283");
        regionCodeMap.put("경상북도", "100285");
        regionCodeMap.put("경상남도", "100291");
        regionCodeMap.put("제주특별자치도", "100292");
    }

    static {
        // 맵 초기화
        regionMap.put("서울특별시", "서울");
        regionMap.put("부산광역시", "부산");
        regionMap.put("인천광역시", "인천");
        regionMap.put("대전광역시", "대전");
        regionMap.put("대구광역시", "대구");
        regionMap.put("울산광역시", "울산");
        regionMap.put("광주광역시", "광주");
        regionMap.put("경기도", "경기");
        regionMap.put("강원특별자치도", "강원");
        regionMap.put("충청북도", "충북");
        regionMap.put("충청남도", "충남");
        regionMap.put("전북특별자치도", "전북");
        regionMap.put("전라남도", "전남");
        regionMap.put("경상북도", "경북");
        regionMap.put("경상남도", "경남");
        regionMap.put("제주특별자치도", "제주");
    }

    public static String getRegionCode(String region) {
        return regionCodeMap.get(region);
    }

    public static String getRegion(String region) {
        return regionMap.get(region);
    }
}
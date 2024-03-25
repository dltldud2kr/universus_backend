package com.example.gazamung.university;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/school")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "학교생성 API", description = "")
public class UniversityFetcherController {

    private final UniversityRepository universityRepository;

    private static final String API_URL = "http://www.career.go.kr/cnet/openapi/getOpenApi.json";


    @GetMapping("/fetchInfo")
    public ResponseEntity<String> fetchSchoolInfo(@RequestParam(required = false) String region) {
        String apiKey = "4c8f6e00c7e9190c585019daba121c2f";

        // API 요청 URL 조합
        String finalUrl = buildApiUrl(apiKey, region);

        // 학교 정보 조회
        try {
            String schoolInfo = fetchSchoolInfoFromAPI(finalUrl);
            return ResponseEntity.ok().body(schoolInfo);
        } catch (Exception e) {
            log.error("Error occurred while fetching school data", e);
            return ResponseEntity.status(500).body("학교 정보를 조회하는 도중에 오류가 발생했습니다.");
        }
    }

    @GetMapping("/saveInfo")
    public ResponseEntity<String> saveSchoolInfoDB(@RequestParam(required = false) String region) {
        String apiKey = "4c8f6e00c7e9190c585019daba121c2f";

        // API 요청 URL 조합
        String finalUrl = buildApiUrl(apiKey, region);

        // 학교 정보 조회 및 저장
        try {
            String schoolInfo = fetchSchoolInfoFromAPI(finalUrl);
            saveSchoolInfoToDB(schoolInfo);
            return ResponseEntity.ok().body("학교 정보를 성공적으로 저장했습니다.");
        } catch (Exception e) {
            log.error("Error occurred while saving school data to database", e);
            return ResponseEntity.status(500).body("학교 정보를 저장하는 도중에 오류가 발생했습니다.");
        }
    }

    private String buildApiUrl(String apiKey, String region) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("svcType", "API")
                .queryParam("svcCode", "SCHOOL")
                .queryParam("gubun", "univ_list")
                .queryParam("contentType", "json")
                .queryParam("perPage", "500")
                .queryParam("apiKey", apiKey);

        if (region != null && !region.isEmpty()) {
            builder.queryParam("region", region);
        }

        return builder.toUriString();
    }


private String fetchSchoolInfoFromAPI(String apiUrl) throws Exception {
    // HTTP 클라이언트 생성
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
        // HTTP GET 요청 생성
        HttpGet httpGet = new HttpGet(apiUrl);

        // HTTP 요청 보내고 응답 받기
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            // 응답의 본문을 문자열로 변환
            String responseBody = EntityUtils.toString(response.getEntity());

            // 응답의 필요한 부분만 추출
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray contentArray = jsonObject.getAsJsonObject("dataSearch").getAsJsonArray("content");
            JsonArray extractedArray = new JsonArray();
            for (JsonElement element : contentArray) {
                JsonObject school = new JsonObject();
                school.addProperty("schoolName", element.getAsJsonObject().get("schoolName").getAsString());
//                school.addProperty("totalCount", element.getAsJsonObject().get("totalCount").getAsString());
                school.addProperty("region", element.getAsJsonObject().get("region").getAsString());
//                school.addProperty("link", element.getAsJsonObject().get("link").getAsString());
                extractedArray.add(school);
            }
            jsonObject.getAsJsonObject("dataSearch").add("content", extractedArray);
            return jsonObject.toString();
        }
    }
}

    private void saveSchoolInfoToDB(String responseBody) {
        // 학교 정보 저장
        saveSchoolInfo(responseBody);
    }

    private void saveSchoolInfo(String responseBody) {
        // JSON 파싱
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(responseBody).getAsJsonObject();
        JsonArray contentArray = jsonObject.getAsJsonObject("dataSearch").getAsJsonArray("content");

        // 각 항목을 순회하며 학교 정보 저장
        for (JsonElement element : contentArray) {
            JsonObject contentObject = element.getAsJsonObject();
            String schoolName = contentObject.get("schoolName").getAsString();
//            String link = contentObject.get("link").getAsString();
//            String totalCount = contentObject.get("totalCount").getAsString();
            String region = contentObject.get("region").getAsString();

            // 학교 정보 저장
            University university = new University();
            university.setSchoolName(schoolName);
//            school.setTotalCount(totalCount);
            university.setRegionCode(RegionCodeMapper.getRegionCode(region)); // 지역에 해당하는 코드 설정
            university.setRegion(RegionCodeMapper.getRegion(region));
//            school.setLink(link);
            universityRepository.save(university);
        }
    }
}
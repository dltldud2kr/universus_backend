
import re

# 읽어올 파일명
university_filename = 'university.txt'
image_filename = 'image.txt'

# 결과를 저장할 파일명
result_filename = 'result.txt'

# university.txt 읽기
with open(university_filename, 'r', encoding='utf-8') as uni_file:
    university_data = uni_file.readlines()

# image.txt 읽기
with open(image_filename, 'r', encoding='utf-8') as img_file:
    image_data = img_file.readlines()

# image 파일 이름을 매칭하기 위한 딕셔너리 생성
image_mapping = {}
for line in image_data:
    school_name, image_name = line.strip().split(',')
    image_mapping[school_name] = image_name

# 결과 파일 생성
with open(result_filename, 'w', encoding='utf-8') as result_file:
    for query in university_data:
        match = re.match(r"INSERT INTO university \(id, school_name, region, region_code\) VALUES \(university_sequence.nextval,'(.+)', '(.+)', '(.+)'\);", query)
        if match:
            school_name = match.group(1)
            region = match.group(2)
            region_code = match.group(3)
            # image_mapping에서 매칭된 이미지 파일명 찾기
            logo_image = image_mapping.get(school_name, "")
            # 결과 파일에 쓰기
            if logo_image:
                result_file.write(f"INSERT INTO university (id, school_name, region, region_code, logo_img) VALUES (university_sequence.nextval,'{school_name}', '{region}', '{region_code}', '{logo_image}');\n")
            else:
                result_file.write(query)  # 이미지 파일이 없으면 원래의 쿼리문 그대로 쓰기
        else:
            result_file.write(query)  # 쿼리 형식이 아닌 경우 그대로 쓰기

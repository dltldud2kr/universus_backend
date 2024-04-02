import re

# 입력 파일 이름
input_file = "univ.txt"
# 출력 파일 이름
output_file = "univresult.txt"

# 변환할 문자열 패턴
pattern = r"INSERT INTO university \(school_name, region, region_code\) VALUES \('(.+)', '(.+)', '(.+)'\);"

# 대체할 문자열 패턴
replace_pattern = r"INSERT INTO university (id, school_name, region, region_code) VALUES (university_sequence.nextval,'\1', '\2', '\3');"

# 파일 열기 (UTF-8로)
with open(input_file, 'r', encoding='utf-8') as f_in, open(output_file, 'w', encoding='utf-8') as f_out:
    # 각 줄에 대해 반복
    for line in f_in:
        # 정규 표현식을 사용하여 패턴을 찾고 변경 후 출력 파일에 쓰기
        modified_line = re.sub(pattern, replace_pattern, line)
        f_out.write(modified_line)

print("변환 완료")

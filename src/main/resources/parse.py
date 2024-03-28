def read_universities(filename):
    universities = {}
    with open(filename, 'r', encoding='utf-8') as file:
        for line in file:
            parts = line.strip().split(',')
            index = int(parts[0])
            university_name = parts[-1].strip()
            universities[university_name] = index
    print("Universities =", universities)
    return universities

def process_departments(universities, filename):
    with open(filename, 'r', encoding='utf-8') as file:
        with open('department_processed.txt', 'w', encoding='utf-8') as output_file:
            for line in file:
                parts = line.strip().split(',')
                school_name = parts[-2].strip().replace("'", "")  # school_name 추출
                if school_name in universities:  # university.txt에 있는 학교만 처리
                    univ_id = universities[school_name]  # 대학교 인덱스 가져오기
                    dept_name = parts[-1].strip().replace("');", "").replace("'", "")  # 학과 이름 추출 및 작은 따옴표 이스케이프 처리 수정
                    output_file.write(f"INSERT INTO department (dept_id, univ_id, dept_name) VALUES (department_sequence.NEXTVAL, {univ_id}, '{dept_name}');\n")
                else:
                    print(f"University not found in the universities list: {school_name}")
    
    print("Processing departments completed")

    with open('department_processed.txt', 'r', encoding='utf-8') as check_file:
        content = check_file.read()
        if content:
            print("Data saved to department_processed.txt")
        else:
            print("No data saved to department_processed.txt")

def main():
    universities = read_universities('university.txt')
    process_departments(universities, 'department.txt')
    print("Writing to department_processed.txt is complete.")

if __name__ == "__main__":
    main()

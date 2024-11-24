# 학습지 관리 시스템 - 기술 검토 문서
## 1. 위험 요소 및 해결 방안
### 1.1 동시성 문제
위험 요소

여러 교사가 동시에 같은 학생에게 학습지 할당
동시에 여러 학생이 같은 문제에 대한 답안 제출
학습지 통계 집계 시 데이터 정합성

해결 방안

학습지 할당
- 유니크 인덱스로 중복 할당 방지
CREATE UNIQUE INDEX uk_student_workbook ON student_workbooks (student_id, workbook_id);

답안 제출
- Optimistic Lock으로 동시성 제어

통계 집계
- 읽기 트렌젝션

### 1.2 성능 문제
위험 요소

대량의 문제 조회 시 성능 저하
많은 학생이 동시에 학습지 조회
통계 집계 시 대량 데이터 처리

해결 방안

문제 조회 최적화
- 캐시 적용

인덱스 최적화
- 조회 성능 향상을 위한 인덱스
CREATE INDEX idx_problem_search ON problems (unit_code, level, problem_type, deleted_at);
CREATE INDEX idx_workbook_teacher ON workbooks (teacher_id, deleted_at);

통계 배치 처리
- 스프링 스케쥴 / cron job 등을 통한 배치 처리

## 2. ERD
![Untitled diagram-2024-11-24-232232](https://github.com/user-attachments/assets/d868d781-9032-4514-a0c7-c693654b211d)


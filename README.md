# SPRING DB2
- 인프런 김영한님의 스프링 DB2를 복습하며 정리하는 리포지토리입니다.
- Master Branch 코드 참고

## 정리 
- 데이터 접근 기술 - JdbcTemplate 정리 (https://ojt90902.tistory.com/923)
- 데이터 접근 기술 - Test 정리 (https://ojt90902.tistory.com/924)
- 데이터 접근 기술 - MyBatis 정리 (https://ojt90902.tistory.com/925)
- 데이터 접근 기술 - JPA 정리 (https://ojt90902.tistory.com/1311)
- 데이터 접근 기술 - Spring Data JPA 정리(https://ojt90902.tistory.com/1312)
- 데이터 접근 기술 - QueryDSL JPA 정리(https://ojt90902.tistory.com/1313)
- 데이터 접근 기술 - 활용 방안 정리(https://ojt90902.tistory.com/1315)
- 데이터 접근 기술 - Spring Transaction 기본(https://ojt90902.tistory.com/1316)


## 각 챕터 간략 정리 (한계)
- `JdbcTemplate`
  - SQL 기반으로 개발을 한다. RDBMS - 자바 객체의 패러다임 불일치에서 벗어날 수 없음.
  - 패러다임 불일치 때문에 Repository 계층에서 Service 계층으로 데이터가 전송되어도, 어떤 범위까지의 데이터가 전달되었는지 알 수 없다. 따라서 Service 계층의 코드를 작성하기 위해 Repository 계층의 코드를 살펴봐야한다. 물리적으로 Repository / Service 계층은 분리되어있으나 논리적으로는 분리되어있지 않음. 
  - 동적 쿼리 작성에 큰 문제가 존재한다.
  - 모든 쿼리는 String을 기반으로 작성됨. → 디버깅의 어려움.
- `DB Test`
- `MyBatis`
  - JdbcTemplate에서 반복되는 많은 코드를 줄여주지만 SQL 기반으로 개발한다. RDBMS - 자바 객체의 패러다임 불일치에서 벗어날 수 없음.
  - 패러다임 불일치 때문에 Repository 계층에서 Service 계층으로 데이터가 전송되어도, 어떤 범위까지의 데이터가 전달되었는지 알 수 없다. 따라서 Service 계층의 코드를 작성하기 위해 Repository 계층의 코드를 살펴봐야한다. 물리적으로 Repository / Service 계층은 분리되어있으나 논리적으로는 분리되어있지 않음. 
  - 동적쿼리 작성에서는 장점이 있다. 
- `JPA`
  - 반복성 단순 쿼리들이 많이 존재한다. findById()...
  - 동적 쿼리 작성 시, 코드량이 많이 증가한다. 
- `Spring Data JPA`

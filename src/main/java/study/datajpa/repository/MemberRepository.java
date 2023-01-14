package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    // 리파지터리 메소드에 쿼리 직접 정의하기
    // NamedQuery 상위호환
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findMember(@Param("username") String username, @Param("age") int age);

    // Member.username의 리스트 가져오기
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // MemberDto 로 반환
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names")List<String> names);

    // 반환 타입
    List<Member> findListByUsername(String name); //컬렉션
    Member findMemberByUsername(String name); //단건
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    /**
     * 스프링 데이터 JPA의 페이징과 정렬
     * 메소드 명으로 쿼리 기능 + 파라미터 : Pageable 인터페이스
     * */
    // Page 반환 타입
    Page<Member> findByAge(int age, Pageable pageable); // count 쿼리 사용
    // Slice 반환 타입
//    Slice<Member> findByAge(int name, Pageable pageable); //count 쿼리 사용 안함
    // count 쿼리 분리
//    @Query(value = "select m from Member m left join m.team t",
//            countQuery = "select count(m) from Member m")
//    Page<Member> findByAge(int age, Pageable pageable);
}

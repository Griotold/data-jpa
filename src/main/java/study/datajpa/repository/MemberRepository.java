package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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
    /**
     * 벌크성 수정 쿼리 - 스프링 데이터 JPA
     * */
    @Modifying(clearAutomatically = true) // 이게 있어야 executeUpdate() 호출, 자동으로 em.clear() 호출
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * @EntityGraph
     * */
    // 페치 조인 - JPQL을 직접 짜는 버전
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // @EntityGraph - 페치 조인이 자동으로 적용
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL + @EntityGraph
//    @EntityGraph("Member.all")
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 메소드명으로 쿼리 기능 + @EntityGraph
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * QueryHint
     * */
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value = "true"))
    List<Member> findReadOnlyByUsername(String username);

    /**
     * Lock
     * */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    /**
     * Projections : username만 딱 가져오고 싶은 상황
     * */
    <T> List<T> findProjectionsByUsername(String username, Class<T> type);

    /**
     * 네이티브 쿼리
     * */
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    // Projections활용 - 네이티브 쿼리 // ANSI 표준 SQL 문법
    @Query(value = "select m.member_id as id, m.username, t.name as teamName" +
            " from member m left join team t ON m.team_id = t.team_id",
            countQuery = "select coutn(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}

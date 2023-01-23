package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(savedMember.getId());
        Member findedMember = findMember.get();

        assertThat(findedMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findedMember.getId()).isEqualTo(member.getId());
        assertThat(findedMember).isEqualTo(member);
    }

    // MemberJPARepository에서 한 거 그대로 복붙해서 잘 되는지 확인
    @Test 
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        // 1. 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 2. 리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 3. 카운트 메소드
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 4. 삭제 메소드
        memberRepository.delete(member1);
        long countAfterDelete = memberRepository.count();
        assertThat(countAfterDelete).isEqualTo(1);

        // then
    }

    @Test
    public void 이름_나이_스프링_데이터_JPA() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("AAA", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.get(1).getAge()).isEqualTo(30);
        assertThat(result.size()).isEqualTo(2);
    }
    // 스프링 데이터 JPA로 네임드쿼리
    @Test
    public void namedQueryTest() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("AAA", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        // when
        List<Member> result = memberRepository.findByUsername("AAA");

        // then

        assertThat(result.get(1).getAge()).isEqualTo(20);
    }
    // 리파지터리 메소드에 쿼리 정의하기
    @Test
    public void 리파지터리_메소드에_쿼리_직접() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("AAA", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        // when
        List<Member> result = memberRepository.findMember("AAA", 20);

        // then

        assertThat(result.get(0)).isEqualTo(m2);
    }

    // @Query로 값 가져오기
    @Test
    public void 값_가져오기() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        // when
        List<String> usernameList = memberRepository.findUsernameList();

        // then
        assertThat(usernameList.get(0)).isEqualTo("AAA");
        assertThat(usernameList.get(1)).isEqualTo("BBB");
        assertThat(usernameList.get(2)).isEqualTo("CCC");
    }

    // @Query로 MemberDto 가져오기
    @Test
    public void memberDto_가져오기()throws Exception {
        // given
        Team t1 = new Team("team1");
        teamRepository.save(t1);

        Member m1 = new Member("member1", 10);
        m1.changeTeam(t1);
        memberRepository.save(m1);

        // when
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        // then
        assertThat(memberDto.get(0).getTeamName()).isEqualTo(t1.getName());
        assertThat(memberDto.get(0).getUsername()).isEqualTo("member1");
        assertThat(memberDto.get(0).getId()).isEqualTo(m1.getId());
    }
    // 컬렉션 파라미터 바인딩
    @Test
    public void 컬렉션_파라미터_바인딩()throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        // when
        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB", "CCC"));

        // then
        assertThat(members.get(0)).isEqualTo(m1);
        assertThat(members.get(1)).isEqualTo(m2);
        assertThat(members.get(2)).isEqualTo(m3);
    }
    // 반환 타입
    @Test
    public void 반환_타입() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        
        // when
        List<Member> result = memberRepository.findListByUsername("fwefwefwafwfgwwb");
        
        // then
        System.out.println("result.size() = " + result.size());
    }
    // 반환타입 - 단건 조회일 때 파라미터가 이상하면 null
    @Test
    public void 반환_타입_2() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        // when
        Member findMember = memberRepository.findMemberByUsername("wfwefwefewf");
        Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("wfwefewfw");

        // then
        assertThat(findMember).isNull();
        System.out.println("findOptionalMember = " + findOptionalMember);
    }
    
    // 스프링 데이터 JPA 페이징과 정렬 검증
    @Test
    public void 페이징_스프링_데이터_JPA() throws Exception {
        // given
        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 10));
        memberRepository.save(new Member("m3", 10));
        memberRepository.save(new Member("m4", 10));
        memberRepository.save(new Member("m5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // 반환타입이 Page이면, totalCount까지 같이 가져옴

        Page<MemberDto> MemberDtos = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        // then


        assertThat(page.getTotalElements()).isEqualTo(5); // totalCount
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 개수
        assertThat(page.isFirst()).isTrue(); // 첫 페이지인가?
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
        assertThat(page.isLast()).isFalse(); // 마지막 페이지인가?
    }

    // slice : limit + 1
    @Test
    public void slice_check() throws Exception {
        // given
        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 10));
        memberRepository.save(new Member("m3", 10));
        memberRepository.save(new Member("m4", 10));
        memberRepository.save(new Member("m5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findByAge(age, pageRequest); // 반환타입이 Page이면, totalCount까지 같이 가져옴
//        long totalCount = memberRepository.totalCount(age);

        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5); // totalCount
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
//        assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 개수
        assertThat(page.isFirst()).isTrue(); // 첫 페이지인가?
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
        assertThat(page.isLast()).isFalse(); // 마지막 페이지인가?
    }
    // 벌크성 수정 쿼리 with 스프링 데이터 JPA
    // Best Practice : 벌크 연산 이후 영속성 컨텍스트를 비워줘야 한다.
    @Test
    public void 벌크_수정_스프링_데이터_JPA() throws Exception {
        // given
        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 19));
        memberRepository.save(new Member("m3", 20));
        memberRepository.save(new Member("m4", 21));
        memberRepository.save(new Member("m5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush(); // 혹시 남아 있는 쿼리 날려주기
//        em.clear(); // 영속성 컨텍스트 비우기

        List<Member> result = memberRepository.findByUsername("m5");
        Member member = result.get(0);
        System.out.println("member = " + member); // 얘는 40살일까 41살일까?

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    // 엔티티 그래프 실습
    // 페치 조인 적용
    @Test
    public void findMemberLazy() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 10, teamB);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        // when N + 1 문제
        // select Member -> 1의 해당
//        List<Member> all = memberRepository.findMemberFetchJoin();
        List<Member> all = memberRepository.findAll();

        for (Member member : all) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            // select Team -> N의 해당
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        // then
    }

    // 쿼리 힌트 실습
    @Test
    public void queryHint() throws Exception {
        // given
        Member member = new Member("m1", 10);
        memberRepository.save(member);

        em.flush();
        em.clear();

        List<Member> memberList = memberRepository.findReadOnlyByUsername("m1");
        Member findMember = memberList.get(0);
        findMember.changeUserName("m2");
        
        em.flush(); // 변경 감지

        // when

        // then
    }

    // Lock 실습
    @Test
    public void lock() throws Exception {
        // given
        Member member = new Member("m1", 10);
        memberRepository.save(member);

        em.flush();
        em.clear();

        List<Member> m1 = memberRepository.findLockByUsername("m1");
        // 쿼리 결과 확인하기

        // when

        // then
    }

    // 사용자 정의 리파지토리
    @Test
    public void callCustom() throws Exception {
        // given
        List<Member> memberCustom = memberRepository.findMemberCustom();
        // when

        // then
    }
    
    // Projections
    @Test
    public void projections() throws Exception {
        // given
        Team team1 = new Team("team1");
        teamRepository.save(team1);

        Member m1 = new Member("m1", 10, team1);
        Member m2 = new Member("m2", 10, team1);
        memberRepository.save(m1);
        memberRepository.save(m2);
        
        em.flush();
        em.clear();
        
        // when
        List<NestedClosedProjection> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjection.class);
        for (NestedClosedProjection nestedClosedProjection : result) {
            System.out.println("nestedClosedProjection = " + nestedClosedProjection);
        }

        // then
    }

    // 네이티브 쿼리 테스트
    @Test
    public void nativeQ() throws Exception {
        // given
        Team team1 = new Team("team1");
        teamRepository.save(team1);

        Member m1 = new Member("m1", 10, team1);
        Member m2 = new Member("m2", 10, team1);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        // when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }

        // then
    }
}
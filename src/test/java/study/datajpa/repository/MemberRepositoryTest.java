package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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
}
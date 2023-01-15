package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJPARepository {
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }
    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByusernameAndAgeGreaterThan(String username, int age){
        return em.createQuery("select m from Member m where m.username =: username and m.age >: age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    // NamedQuery
    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }
    /**
     * 순수 JPA 페이징과 정렬
     * */
    // offset : 몇 번쨰 부터 // limit : 몇 개를 가져와
    public List<Member> findByPage(int age, int offset, int limit){
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset) // 몇 번째 부터
                .setMaxResults(limit) // 몇 개를 가져올건데
                .getResultList();
    }
    // Total count는 보통 세트로 가져온다.
    // 여기에는 sorting condition, 즉 order by desc가 없다. 필요가 없으니 뺴줬고 성능 최적화
    public long totlaCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }
    /**
     * 벌크성 수정 쿼리
     * 순수 JPA로 구현
     *
     * */
    // age보다 크거나 같은 애들의 age를 +1
    public int bulkAgePlus(int age){
        int resultCount = em.createQuery("update Member m set m.age = m.age + 1" +
                        " where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate(); // 업데이트 쿼리 날릴 때 // 리턴타입은 int
        return resultCount;
    }
}

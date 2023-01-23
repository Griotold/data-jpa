package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    // 오픈 프로젝션 // 스프링의 SpEL 문법도 지원
    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
    String getUsername();
}

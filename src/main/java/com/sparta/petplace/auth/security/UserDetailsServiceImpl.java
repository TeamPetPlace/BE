package com.sparta.petplace.auth.security;


import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(Error.NOT_EXIST_USER));
        return new UserDetailsImpl(findMember, findMember.getEmail());
    }
}

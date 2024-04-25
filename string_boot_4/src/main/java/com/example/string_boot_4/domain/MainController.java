package com.example.string_boot_4.domain;

import com.example.string_boot_4.user.PasswordForm;
import com.example.string_boot_4.user.SiteUser;
import com.example.string_boot_4.user.UserSecurityService;
import com.example.string_boot_4.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.OpenSSLEngine;
import org.codehaus.groovy.transform.SourceURIASTTransformation;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.beans.Encoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static java.util.regex.Pattern.matches;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final MainService mainService;
    private final HttpSession session;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserSecurityService userDetailsService;

    @GetMapping("/")
    public String root(Authentication authentication){

        System.out.println(authentication);

        return "redirect:/question/list";

    }

    @GetMapping("/kakao-login")
    public String kakao(){
        return "kakao_login";
    }

    @GetMapping("/auth-test")
    public String atest() {

        String encodedPassword = passwordEncoder.encode("1234");
        Authentication authentication = new UsernamePasswordAuthenticationToken("hihi", encodedPassword);
//                    Authentication authentication = new TestToken(user.getUsername(), encodedPassword, new ArrayList<SimpleGrantedAuthority>());
//                    authentication.setAuthenticated(true);
        // SecurityContextHolder를 사용하여 인증 정보를 설정합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";
    }

    @GetMapping("/kakao_callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpServletRequest request) {
        // 여기에 카카오 콜백 처리 로직을 추가합니다.
        String access_Token = mainService.getKakaoAccessToken(code);
        HashMap<String, Object> userInfo = mainService.getUserInfo(access_Token);
        System.out.println("login Controller : " + userInfo);

        //    클라이언트의 닉네임이 존재할 때 세션에 해당 이메일과 토큰 등록
        if (userInfo.get("nickname") != null) {
            session.setAttribute("nickname", userInfo.get("nickname"));
            session.setAttribute("access_Token", access_Token);

            String nickname = (String) session.getAttribute("nickname");
            SiteUser user = userService.getUser(nickname);
            if (user == null) {
                System.out.println("not");
                userService.createkakao();
                user = userService.getUser(nickname);
            }

            if (user != null) {
                try {
                    // UserDetailsService를 사용하여 사용자 정보를 가져옵니다.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

                    // 사용자의 자격 증명이 올바른 경우, Authentication 객체를 생성합니다.
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    System.out.println(authenticationToken);

//                    WebAuthenticationDetails details = new WebAuthenticationDetails(request);
                    authenticationToken.setDetails(userDetails);
                    System.out.println(authenticationToken);

                    // SecurityContextHolder에 인증 정보를 설정합니다.
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // SecurityContextHolder에서 인증 정보 가져오기
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    // 세션에 인증 정보 저장
                    request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                    // 로그인 성공 후 리다이렉트합니다.
                    return "redirect:/";
                } catch (BadCredentialsException e) {
                    // 비밀번호가 올바르지 않은 경우
                    System.out.println("Authentication failed: Bad credentials");
                    return "redirect:/user/login?error";
                } catch (DisabledException e) {
                    // 계정이 비활성화된 경우
                    System.out.println("Authentication failed: Account disabled");
                    return "redirect:/user/login?error";
                } catch (AccountExpiredException e) {
                    // 계정이 만료된 경우
                    System.out.println("Authentication failed: Account expired");
                    return "redirect:/user/login?error";
                } catch (CredentialsExpiredException e) {
                    // 자격 증명이 만료된 경우
                    System.out.println("Authentication failed: Credentials expired");
                    return "redirect:/user/login?error";
                } catch (AuthenticationException e) {
                    // 기타 인증 실패
                    System.out.println("Authentication failed: " + e.getMessage());
                    return "redirect:/user/login?error";
                }
            }
        }
        return "redirect:/";
    }
    @RequestMapping(value="/kakao_logout")
    public String logout(HttpSession session) {
        mainService.kakaoLogout((String)session.getAttribute("access_Token"));
        session.removeAttribute("access_Token");
        session.removeAttribute("userId");
        return "redirect:/";
    }

}

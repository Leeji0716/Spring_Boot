package com.example.string_boot_4.domain;

import com.example.string_boot_4.user.SiteUser;
import com.example.string_boot_4.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.OpenSSLEngine;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.beans.Encoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final MainService mainService;
    private final HttpSession session;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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

        //    클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
        if (userInfo.get("nickname") != null) {
            session.setAttribute("nickname", userInfo.get("nickname"));
            session.setAttribute("access_Token", access_Token);

            String nickname = (String) session.getAttribute("nickname");
            System.out.println(nickname);
            SiteUser user = userService.getUser(nickname);

            if(user != null){
                System.out.println("user : " + user.getUsername());

                try {
                    // 사용자 인증 정보를 생성합니다.
                    String encodedPassword = passwordEncoder.encode(user.getPassword());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), encodedPassword);
//                    Authentication authentication = new TestToken(user.getUsername(), encodedPassword, new ArrayList<SimpleGrantedAuthority>());
//                    authentication.setAuthenticated(true);
                    // SecurityContextHolder를 사용하여 인증 정보를 설정합니다.
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 인증 정보를 세션에 저장합니다.
                    HttpSession session = request.getSession(true);
                    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                    System.out.println("Authentication success");
                    System.out.println(user.getUsername());
                    System.out.println(user.getPassword());

                    return "redirect:/";
                } catch (AuthenticationException e) {
                    // 사용자 인증 실패 시 처리
                    System.out.println("Authentication failed: " + e.getMessage());
                    e.printStackTrace();
                    return "redirect:/login?error";
                }
            }else {
                System.out.println("not");
                userService.createkakao();
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

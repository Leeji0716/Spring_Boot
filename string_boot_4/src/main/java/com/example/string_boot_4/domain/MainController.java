package com.example.string_boot_4.domain;

import com.example.string_boot_4.user.SiteUser;
import com.example.string_boot_4.user.UserSecurityService;
import com.example.string_boot_4.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;


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
        return "redirect:/question/list";
    }
    @GetMapping("/naver-login")
    public String naver(){
        String clientId = "kCAY2j3mmJWd2xUR5V7V";
        String redirectUri = "http://localhost:8088/naver_callback";
        String url = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri + "&state=1234";
        return "redirect:" + url;
    }

    @GetMapping("/naver-login2")
    public String naver2(){
        String clientId = "kCAY2j3mmJWd2xUR5V7V";
        String redirectUri = "http://localhost:8088/naver_callback";
        String url = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri + "&state=1234";
        return "redirect:" + url;
    }

    @GetMapping("/kakao-login")
    public String kakaoLogin() {
        String clientId = "74369d3656e0458ace28ba20d8a75a5b";
        String redirectUri = "http://localhost:8088/kakao_callback";
        String url = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=code&scope=profile_nickname profile_image";
        return "redirect:" + url;
    }

    @GetMapping("/naver_callback")
    public String naverCallback(@RequestParam("code") String code,@RequestParam("state") String state, HttpServletRequest request){
        String access_Token = mainService.getNaverAccessToken(code);
        HashMap<String, Object> userInfo = mainService.getNaverUserInfo(access_Token);
//        System.out.println("login Controller : " + userInfo);

        //    클라이언트의 닉네임이 존재할 때 세션에 해당 이메일과 토큰 등록
        if (userInfo.get("nickname") != null) {
            session.setAttribute("nickname", userInfo.get("nickname"));
            session.setAttribute("email", userInfo.get("email"));
            session.setAttribute("id", userInfo.get("id"));
            session.setAttribute("access_Token", access_Token);

            String id = (String) session.getAttribute("id");
            SiteUser user = userService.getUser(id);
            if (user == null) {
                userService.createnaver();
                user = userService.getUser(id);
            }

            if (user != null) {
                try {
                    // UserDetailsService를 사용하여 사용자 정보를 가져옵니다.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

                    // 사용자의 자격 증명이 올바른 경우, Authentication 객체를 생성합니다.
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(userDetails);

                    // SecurityContextHolder에 인증 정보를 설정합니다.
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // SecurityContextHolder에서 인증 정보 가져오기
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    // 세션에 인증 정보 저장
                    request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                    return "redirect:/";
                } catch (BadCredentialsException e) {
                    // 비밀번호가 올바르지 않은 경우
                    System.out.println("Authentication failed: Bad credentials");
                    return "redirect:/user/login?error";
                }
            }
        }
        return "redirect:/";
    }

    @GetMapping("/kakao_callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpServletRequest request) {
        String access_Token = mainService.getKakaoAccessToken(code);
        HashMap<String, Object> userInfo = mainService.getKakaoUserInfo(access_Token);
//        System.out.println("login Controller : " + userInfo);

        //    클라이언트의 닉네임이 존재할 때 세션에 해당 이메일과 토큰 등록
        if (userInfo.get("nickname") != null) {
            session.setAttribute("nickname", userInfo.get("nickname"));
            session.setAttribute("id", userInfo.get("id"));
            session.setAttribute("access_Token", access_Token);

            String id = (String) session.getAttribute("id");
            SiteUser user = userService.getUser(id);
            if (user == null) {
                userService.createkakao();
                user = userService.getUser(id);
            }

            if (user != null) {
                try {
                    // UserDetailsService를 사용하여 사용자 정보를 가져옵니다.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

                    // 사용자의 자격 증명이 올바른 경우, Authentication 객체를 생성합니다.
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(userDetails);

                    // SecurityContextHolder에 인증 정보를 설정합니다.
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // SecurityContextHolder에서 인증 정보 가져오기
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    // 세션에 인증 정보 저장
                    request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

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

}

package com.example.string_boot_4.login;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Controller
public class KakaoLoginController {
    @GetMapping("/kakao-callback") // 리디렉션 URI로 설정한 엔드포인트
    public String handleKakaoCallback(@RequestParam("code") String code) {
        // 카카오에서 전달받은 인증 코드 처리 로직
        // 여기서 인증 코드(code)를 이용하여 토큰을 요청하고, 사용자 정보를 가져오는 등의 작업을 수행합니다.

        return "redirect:/"; // 작업 완료 후 리디렉션할 페이지로 설정합니다.
    }
}

package com.example.backend.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * packageName : com.example.backend.dto.request
 * fileName : ChangePasswordRequest
 * author : hyuk
 * date : 2023/01/23
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/23         hyuk          최초 생성
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank
    private String password;
}

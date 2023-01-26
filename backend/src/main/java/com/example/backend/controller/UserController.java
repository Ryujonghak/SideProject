package com.example.backend.controller;

import com.example.backend.dto.ResponseMessageDto;
import com.example.backend.dto.request.SignupRequest;
import com.example.backend.dto.response.UserRoleDto;
import com.example.backend.model.ERole;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.service.RoleService;
import com.example.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * packageName : com.example.backend.controller
 * fileName : UserController
 * author : hyuk
 * date : 2023/01/23
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/23         hyuk          최초 생성
 */
@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost")
@RequestMapping("/api")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/user")
    public ResponseEntity<Object> getUsers(@RequestParam(required = false) String username,
                                             @RequestParam(required = false) String email,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "3") int size
                                             )
    {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<UserRoleDto> userRoleDtoPage;

            Page<User> userPage;

//            TODO: 일단 이게 원본 코드
            userRoleDtoPage = userService.findAllByUsernameContaining(username, pageable);

//            TODO: 이건 username / email 받았을 때 따라서 함수 바뀌게 설계할거임
//            if (!username.isEmpty()) userRoleDtoPage = userService.findAllByUsernameContaining(username, pageable);
//            if (!email.isEmpty()) userRoleDtoPage = userService.findByEmail(email, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("user", userRoleDtoPage.getContent());
            response.put("currentPage", userRoleDtoPage.getNumber());
            response.put("totalItems", userRoleDtoPage.getTotalElements());
            response.put("totalPage", userRoleDtoPage.getTotalPages());

            if (userRoleDtoPage.isEmpty() == false) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<Object> saveUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userService.existByEmail(signupRequest.getUsername())){
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseMessageDto("에러 : 아이디가 이미 사용 중입니다."));
        }

        if (userService.existByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseMessageDto("에러 : 이메일이 이미 사용 중입니다."));
        }

//        passwordEncoder.encode(signupRequest.getPassword());
//        User user = signupRequest;

        User user = new User(signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getEmail(),
                signupRequest.getName(),
                signupRequest.getBirth(),
                signupRequest.getGender(),
                signupRequest.getPhone(),
                signupRequest.getAddress()
        );

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleService.findByRoleName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("에러 : 권한을 찾을 수 없습니다."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleService.findByRoleName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("에러 : 권한을 찾을 수 없습니다."));
                        roles.add(adminRole);

                        break;

                    default:
                        Role userRole = roleService.findByRoleName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("에러 : 권한을 찾을 수 없습니다."));
                        roles.add(userRole);
                }
            });
        }

        user.setRole(roles);
        userService.save(user);

        return ResponseEntity.ok(new ResponseMessageDto("새로운 유저 생성에 성공했습니다."));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Object> updateUser(@RequestParam long id, @RequestParam SignupRequest signupRequest) {

        String password = "";

        log.debug("signupRequest {}", signupRequest);

        if (signupRequest.isChangePwd()) {
            password = passwordEncoder.encode(signupRequest.getPassword());
        } else {
            password = signupRequest.getPassword();
        }

        User user = new User(id,
                signupRequest.getUsername(),
                password,
                signupRequest.getName(),
                signupRequest.getEmail(),
                signupRequest.getBirth(),
                signupRequest.getPhone(),
                signupRequest.getAddress(),
                signupRequest.getGender());

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleService.findByRoleName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("에러 : 권한을 찾을 수 없습니다."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleService.findByRoleName(ERole.ROLE_ADMIN)
                                .orElseThrow(()-> new RuntimeException("에러 : 권한을 찾을 수 없습니다."));
                        roles.add(adminRole);

                        break;

                    default:
                        Role userRole = roleService.findByRoleName(ERole.ROLE_USER)
                                .orElseThrow(()-> new RuntimeException("에러 : 권한을 찾을 수 없습니다."));
                        roles.add(userRole);
                }
            });
        }

        user.setRole(roles);
        userService.save(user);

        return ResponseEntity.ok(new ResponseMessageDto("유저 정보 수정에 성공했습니다."));
    }

    @DeleteMapping("/user/deletion/{id}")
    public ResponseEntity<Object> deleteUser(@RequestParam long id) {
        try {
            boolean success = userService.removeById(id);

            if (success == true) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/deletion/all")
    public ResponseEntity<Object> deleteAll() {
        try {
            userService.removeAll();

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

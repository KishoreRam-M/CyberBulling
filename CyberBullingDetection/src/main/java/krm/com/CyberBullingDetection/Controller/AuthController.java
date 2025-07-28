package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Configuration.JwtProvider;
import krm.com.CyberBullingDetection.Impl.CustomUserDetailsImpl;
import krm.com.CyberBullingDetection.Model.User;
import krm.com.CyberBullingDetection.Repo.UserRepo;
import krm.com.CyberBullingDetection.Request.LoginRequest;
import krm.com.CyberBullingDetection.Response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsImpl customUserDetails;
    @PostMapping("/signup")

    public ResponseEntity<AuthResponse> signUp(@RequestBody User user) throws Exception { String  email = user.getEmail();
        if(email !=null)
        {
            throw new Exception("Email already exists with another account");

        }
        User myuser = new User();
        myuser.setEmail(email);
        myuser.setName(user.getName());
        myuser.setPassword(user.getPassword());
       User savedUser= userRepo.save(myuser);
        Authentication authentication=authenticate(user.getEmail(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse(token
                , "Signup successful");

        return ResponseEntity.ok(authResponse);
    }
private  Authentication authenticate (String name,String password)
{
    UserDetails userDetails = customUserDetails.loadUserByUsername(name);

    if (userDetails == null) {
        throw new BadCredentialsException("User not found");
    }

    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
        throw new BadCredentialsException("Invalid password");
    }
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

}
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse(jwt, "Signin successful");

        return ResponseEntity.ok(authResponse);
    }
}

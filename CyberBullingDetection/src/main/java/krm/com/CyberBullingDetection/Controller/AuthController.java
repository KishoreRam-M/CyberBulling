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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            String email = user.getEmail();
            User existingUser = userRepo.findByEmail(email);

            if (existingUser != null) {
                return ResponseEntity.badRequest().body("User already exists with email: " + email);
            }

            User myUser = new User();
            myUser.setEmail(email);
            myUser.setName(user.getName());
            myUser.setPassword(passwordEncoder.encode(user.getPassword()));

            // Default role is USER unless specified otherwise
            myUser.setRole(user.getRole() != null ? user.getRole() : "ROLE_USER");

            User savedUser = userRepo.save(myUser);

            Authentication authentication = authenticate(email, user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = JwtProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse(token, "Signup successful");

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Signup failed: " + e.getMessage());
        }
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetails.loadUserByUsername(email);

        if (userDetails == null) {
            throw new BadCredentialsException("User not found");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            Authentication authentication = authenticate(email, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = JwtProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse(jwt, "Signin successful");

            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Signin failed: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Internal error during signin: " + ex.getMessage());
        }
    }
}

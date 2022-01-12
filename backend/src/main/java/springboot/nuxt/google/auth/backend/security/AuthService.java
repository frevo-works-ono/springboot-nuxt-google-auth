package springboot.nuxt.google.auth.backend.security;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import springboot.nuxt.google.auth.backend.repository.UserRepository;
import springboot.nuxt.google.auth.backend.util.FirebaseClient;

public class AuthService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private FirebaseClient firebaseClient;
    private UserRepository userRepository;

    public AuthService(FirebaseClient firebaseAuthClient, UserRepository userRepository) {
        this.firebaseClient = firebaseAuthClient;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {

        // AuthFilterから渡されたトークンを取得
        final var credential = token.getCredentials().toString();

        // 空の場合は認証エラーとする
        if (credential.isEmpty()) {
            throw new BadCredentialsException("トークンが空です");
        }

        try {

            // トークンの検証
            FirebaseToken firebaseToken = firebaseClient.verify(credential);

            return userRepository.findByEmail(firebaseToken.getEmail())
                    .map(user -> new User(user.email(), "", AuthorityUtils.NO_AUTHORITIES))
                    .orElseThrow(() -> new UsernameNotFoundException("該当するユーザが存在しません"));

        } catch (FirebaseException e) {
            throw new BadCredentialsException("トークンの検証エラー", e);
        }
    }
}

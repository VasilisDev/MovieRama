package gr.assignment.movierama.core.security;

public interface PasswordEncryptor {
    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}

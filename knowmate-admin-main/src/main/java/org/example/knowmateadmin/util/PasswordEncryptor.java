package org.example.knowmateadmin.util;

public class PasswordEncryptor {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java PasswordEncryptor <password>");
            return;
        }
        String rawPassword = args[0];
        String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
package com.routeplanner.backend.service;

public interface OAuthVerifierService {
    /** Verilen ID token'ı doğrular ve kullanıcı bilgisini döner. Geçersizse exception fırlatır. */
    OAuthUserInfo verifyGoogleIdToken(String idToken);

    /** Verilen Apple ID token'ı doğrular ve kullanıcı bilgisini döner. Geçersizse exception fırlatır. */
    OAuthUserInfo verifyAppleIdToken(String idToken);
}
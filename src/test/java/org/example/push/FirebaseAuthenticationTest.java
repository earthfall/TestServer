package org.example.push;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FirebaseAuthenticationTest {
    @Test
    void refreshesExpiredCredentialsAndReusesValidToken() {
        var credentials = new RefreshingCredentials();
        var beans = new DefaultListableBeanFactory();
        beans.registerSingleton("credentials", credentials);
        var provider = new FirebaseClientConfiguration()
            .firebaseAccessTokenProvider(beans.getBeanProvider(GoogleCredentials.class));

        assertThat(provider.accessToken()).isEqualTo("refreshed-token");
        assertThat(provider.accessToken()).isEqualTo("refreshed-token");
        assertThat(credentials.refreshes).isEqualTo(1);
    }

    @Test
    void wrapsCredentialFailureForConsistentUpstreamErrorHandling() {
        var beans = new DefaultListableBeanFactory();
        beans.registerSingleton("credentials", new GoogleCredentials() {
            @Override
            public AccessToken refreshAccessToken() throws IOException {
                throw new IOException("credential failure");
            }
        });
        var provider = new FirebaseClientConfiguration()
            .firebaseAccessTokenProvider(beans.getBeanProvider(GoogleCredentials.class));
        assertThatThrownBy(provider::accessToken).isInstanceOf(RestClientException.class)
            .hasMessage("Firebase authentication failed");
    }

    static class RefreshingCredentials extends GoogleCredentials {
        int refreshes;
        @Override
        public AccessToken refreshAccessToken() {
            refreshes++;
            return new AccessToken("refreshed-token", new Date(System.currentTimeMillis() + 3_600_000));
        }
    }
}

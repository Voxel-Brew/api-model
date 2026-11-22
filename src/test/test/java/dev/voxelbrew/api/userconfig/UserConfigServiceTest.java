package dev.voxelbrew.api.userconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UserConfigServiceTest {
    @Test
    void backupsFollowTheConfiguredApplicationHome(@TempDir Path home) {
        var service = (UserConfigService) Proxy.newProxyInstance(
                UserConfigService.class.getClassLoader(),
                new Class<?>[] {UserConfigService.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("homeFolder")) return home;
                    return InvocationHandler.invokeDefault(proxy, method, args);
                });
        assertThat(service.backupsFolder()).isEqualTo(home.resolve("backups")).doesNotExist();
    }
}

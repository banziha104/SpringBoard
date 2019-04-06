package com.board.springboard.oauth;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class ClientResources {
    @NestedConfigurationProperty // 해당 필드가 단일 값이 아닌 중복으로 바인딩된다고 표시하는 어노테이션
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails(); // client기준으로 하위의 키/값을 매핑 시켜주는 대상 객체

    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties(); // oauth2 리소스값을 매핑하는 데 사용

    public AuthorizationCodeResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }
}

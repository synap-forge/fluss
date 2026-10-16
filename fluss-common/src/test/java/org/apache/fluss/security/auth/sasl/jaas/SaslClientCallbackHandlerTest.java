/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.fluss.security.auth.sasl.jaas;

import org.junit.jupiter.api.Test;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests for {@link SaslClientCallbackHandler}. */
class SaslClientCallbackHandlerTest {

    @Test
    void testCredentialsFromCurrentSubject() {
        Subject subject = new Subject();
        subject.getPublicCredentials().add("test-user");
        subject.getPrivateCredentials().add("test-password");
        NameCallback name = new NameCallback("Username", "default-user");
        PasswordCallback password = new PasswordCallback("Password", false);

        Subject.callAs(
                subject,
                () -> {
                    new SaslClientCallbackHandler().handle(new Callback[] {name, password});
                    return null;
                });

        assertThat(name.getName()).isEqualTo("test-user");
        assertThat(password.getPassword()).isEqualTo("test-password".toCharArray());
    }

    @Test
    void testDefaultNameWithoutSubject() {
        NameCallback name = new NameCallback("Username", "default-user");

        Subject.callAs(
                null,
                () -> {
                    new SaslClientCallbackHandler().handle(new Callback[] {name});
                    return null;
                });

        assertThat(name.getName()).isEqualTo("default-user");
    }

    @Test
    void testMissingPasswordDoesNotPromptUser() {
        Subject.callAs(
                new Subject(),
                () -> {
                    PasswordCallback password = new PasswordCallback("Password", false);
                    assertThatThrownBy(
                                    () ->
                                            new SaslClientCallbackHandler()
                                                    .handle(new Callback[] {password}))
                            .isInstanceOf(UnsupportedCallbackException.class)
                            .hasMessageContaining(
                                    "does not currently support obtaining a password");
                    return null;
                });
    }
}

package com.kset.common.utils.sign;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KsetSignUtilTest {

    @Test
    void shouldSignAndVerifySha1() {
        KsetSignUtil signer = KsetSignUtil.of("my-secret");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("appId", "10001");
        params.put("timestamp", "1710000000");

        String sign = signer.signSha1(params);
        params.put(KsetSignUtil.DEFAULT_SIGN_FIELD, sign);

        assertTrue(signer.verifySha1(params));
        assertTrue(signer.checkSign(params));
    }

    @Test
    void shouldSignAndVerifyMd5() {
        KsetSignUtil signer = KsetSignUtil.of("my-secret");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("nonce", "abc");
        params.put("userId", "42");

        String sign = signer.signMd5(params);
        params.put("sign", sign);

        assertTrue(signer.verifyMd5(params));
    }

    @Test
    void shouldRejectTamperedSign() {
        KsetSignUtil signer = KsetSignUtil.of("my-secret");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("appId", "10001");
        params.put("sign", signer.signSha1(params));

        params.put("appId", "10002");
        assertFalse(signer.verifySha1(params));
    }
}

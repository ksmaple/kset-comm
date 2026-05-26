package com.kset.common.utils.sign;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 常见 OpenAPI 签名：参数按 key 字典序拼接，secret 首尾包裹后做摘要。
 *
 * <p>与 bobo {@code BoBoSignUtil} 的 SHA-1 规则兼容。
 */
public final class KsetSignUtil {

    public static final String DEFAULT_SIGN_FIELD = "sign";

    private final String secret;
    private final String signField;

    private KsetSignUtil(String secret, String signField) {
        if (StringUtils.isBlank(secret)) {
            throw new IllegalArgumentException("secret 不能为空");
        }
        this.secret = secret;
        this.signField = StringUtils.isBlank(signField) ? DEFAULT_SIGN_FIELD : signField;
    }

    public static KsetSignUtil of(String secret) {
        return new KsetSignUtil(secret, DEFAULT_SIGN_FIELD);
    }

    public static KsetSignUtil of(String secret, String signField) {
        return new KsetSignUtil(secret, signField);
    }

    /**
     * 兼容旧命名。
     */
    public static KsetSignUtil build(String secret) {
        return of(secret);
    }

    public String signSha1(Map<String, String> params) {
        return digest(params, "SHA-1");
    }

    public boolean verifySha1(Map<String, String> params) {
        return verify(params, signSha1(params));
    }

    public String signMd5(Map<String, String> params) {
        return digest(params, "MD5");
    }

    public boolean verifyMd5(Map<String, String> params) {
        return verify(params, signMd5(params));
    }

    /**
     * 兼容 bobo {@code checkSign}（SHA-1）。
     */
    public boolean checkSign(Map<String, String> params) {
        return verifySha1(params);
    }

    /**
     * 兼容 bobo {@code sign}（SHA-1）。
     */
    public String sign(Map<String, String> params) {
        return signSha1(params);
    }

    private boolean verify(Map<String, String> params, String expected) {
        if (params == null) {
            return false;
        }
        String actual = params.get(signField);
        return StringUtils.isNotBlank(actual)
                && StringUtils.isNotBlank(expected)
                && actual.equalsIgnoreCase(expected);
    }

    private String digest(Map<String, String> params, String algorithm) {
        if (params == null) {
            throw new IllegalArgumentException("params 不能为空");
        }
        String plain = buildPlainText(params);
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algorithm + " 算法不可用", e);
        }
    }

    private String buildPlainText(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        keys.remove(signField);
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder(secret.length() * 2 + params.size() * 16);
        sb.append(secret);
        for (String key : keys) {
            String value = params.get(key);
            if (value != null) {
                sb.append(key).append(value);
            }
        }
        sb.append(secret);
        return sb.toString();
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

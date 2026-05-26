package com.kset.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Set;

/**
 * 日志敏感数据脱敏工具。
 *
 * <p>支持 JSON 结构化脱敏与纯文本回退脱敏。对匹配敏感字段名的值按规则处理：
 * <ul>
 *   <li>密码/密钥/Token → {@code [REDACTED]}</li>
 *   <li>手机号 → 保留前3后4，中间 ****</li>
 *   <li>邮箱 → 首字符 + *** + @domain</li>
 *   <li>身份证号 → 保留前6后4，中间 ********</li>
 *   <li>银行卡 → 保留前4后4，中间 ****</li>
 *   <li>地址 → 保留前12字符 + ...</li>
 * </ul>
 */
public class LogMaskingUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Set<String> REDACTED_KEYS = Set.of(
            "password", "pwd", "passwd", "secret", "token",
            "apikey", "api_key", "auth", "credential",
            "key", "privatekey", "private_key", "accesskey", "access_key"
    );

    private static final Set<String> PHONE_KEYS = Set.of("phone", "mobile", "tel", "telephone");
    private static final Set<String> EMAIL_KEYS = Set.of("email", "mail");
    private static final Set<String> IDCARD_KEYS = Set.of("idcard", "id_card", "identity", "idnumber", "id_number");
    private static final Set<String> BANK_KEYS = Set.of("bankcard", "bank_card", "bankno", "bank_no");
    private static final Set<String> ADDRESS_KEYS = Set.of("address", "addr");

    private LogMaskingUtil() {
    }

    /**
     * 对 JSON 字符串进行敏感字段脱敏。非合法 JSON 时回退到文本正则脱敏。
     */
    public static String maskJson(String json) {
        if (json == null || json.isBlank()) {
            return json;
        }
        try {
            JsonNode node = MAPPER.readTree(json);
            maskNode(node);
            return MAPPER.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            return maskText(json);
        }
    }

    private static void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            obj.fields().forEachRemaining(entry -> {
                String key = entry.getKey().toLowerCase();
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    obj.set(entry.getKey(), TextNode.valueOf(maskValue(key, value.asText())));
                } else if (value.isObject() || value.isArray()) {
                    maskNode(value);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                JsonNode elem = arr.get(i);
                if (elem.isObject() || elem.isArray()) {
                    maskNode(elem);
                }
            }
        }
    }

    private static String maskValue(String key, String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (keyMatches(key, REDACTED_KEYS)) {
            return "[REDACTED]";
        }
        if (keyMatches(key, PHONE_KEYS)) {
            return maskPhone(value);
        }
        if (keyMatches(key, EMAIL_KEYS)) {
            return maskEmail(value);
        }
        if (keyMatches(key, IDCARD_KEYS)) {
            return maskIdCard(value);
        }
        if (keyMatches(key, BANK_KEYS)) {
            return maskBankCard(value);
        }
        if (keyMatches(key, ADDRESS_KEYS)) {
            return maskAddress(value);
        }
        return value;
    }

    private static boolean keyMatches(String key, Set<String> patterns) {
        for (String p : patterns) {
            if (key.contains(p)) {
                return true;
            }
        }
        return false;
    }

    private static String maskPhone(String phone) {
        if (phone.length() < 7) {
            return "****";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return "****" + email.substring(at);
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    private static String maskIdCard(String id) {
        if (id.length() < 10) {
            return "************";
        }
        return id.substring(0, 6) + "********" + id.substring(id.length() - 4);
    }

    private static String maskBankCard(String card) {
        String digits = card.replaceAll("\\s+", "");
        if (digits.length() < 8) {
            return "****";
        }
        return digits.substring(0, 4) + " **** **** **** " + digits.substring(digits.length() - 4);
    }

    private static String maskAddress(String addr) {
        if (addr.length() <= 12) {
            return addr;
        }
        return addr.substring(0, 12) + "...";
    }

    /**
     * 纯文本回退脱敏：对常见 JSON 键值对做简单正则替换。
     */
    public static String maskText(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String result = text;
        for (String key : REDACTED_KEYS) {
            result = result.replaceAll(
                    "(?i)(\"" + key + "\":\\s*\")[^\"]*\"",
                    "$1[REDACTED]\""
            );
        }
        return result;
    }
}

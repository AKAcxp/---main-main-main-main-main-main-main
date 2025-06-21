package org.example.knowmateadmin.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON类型处理器，用于处理数据库中的JSON字符串和Java对象之间的转换
 */
@MappedTypes({ List.class })
public class JsonTypeHandler extends BaseTypeHandler<List<String>> {

    private static final Logger logger = LoggerFactory.getLogger(JsonTypeHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            // 如果参数为null或空列表，直接存储空数组
            if (parameter == null || parameter.isEmpty()) {
                logger.info("参数为null或空列表，存储为空JSON数组");
                ps.setString(i, "[]");
                return;
            }

            String json = objectMapper.writeValueAsString(parameter);
            logger.info("将Java对象转换为JSON字符串: {} -> {}", parameter, json);
            ps.setString(i, json);
        } catch (JsonProcessingException e) {
            logger.error("Java对象转换为JSON字符串失败: {}", e.getMessage());
            ps.setString(i, "[]");
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJsonToList(rs.getString(columnName), columnName);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJsonToList(rs.getString(columnIndex), "columnIndex:" + columnIndex);
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJsonToList(cs.getString(columnIndex), "columnIndex:" + columnIndex);
    }

    private List<String> parseJsonToList(String json, String columnInfo) {
        logger.info("从数据库读取JSON字符串({}): {}", columnInfo, json);

        // 处理null、空字符串、"null"字符串等情况
        if (json == null || json.trim().isEmpty() || "null".equals(json)) {
            logger.info("JSON字符串为null或空，返回空列表");
            return new ArrayList<>();
        }

        // 处理空数组字符串
        if ("[]".equals(json.trim())) {
            logger.info("JSON字符串为空数组，返回空列表");
            return new ArrayList<>();
        }

        try {
            // 尝试解析为字符串列表
            List<String> result = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));

            // 过滤掉列表中的null和空字符串
            List<String> filteredResult = new ArrayList<>();
            for (String item : result) {
                if (item != null && !item.trim().isEmpty() && !"null".equals(item)) {
                    filteredResult.add(item);
                }
            }

            logger.info("解析JSON字符串成功: {} -> {}", json, filteredResult);
            return filteredResult;
        } catch (Exception e) {
            logger.error("解析JSON字符串失败: {} - {}", json, e.getMessage());

            // 尝试解析为单个字符串并包装为列表
            try {
                String singleValue = objectMapper.readValue(json, String.class);
                if (singleValue != null && !singleValue.trim().isEmpty() && !"null".equals(singleValue)) {
                    List<String> result = new ArrayList<>();
                    result.add(singleValue);
                    logger.info("解析为单个字符串成功: {} -> {}", json, result);
                    return result;
                }
            } catch (Exception ex) {
                logger.error("解析为单个字符串也失败: {}", ex.getMessage());
            }

            // 如果所有尝试都失败，返回空列表
            return new ArrayList<>();
        }
    }
}
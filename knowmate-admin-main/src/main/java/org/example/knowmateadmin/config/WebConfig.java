package org.example.knowmateadmin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置外部文件夹访问路径
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        if (!uploadPath.endsWith("/") && !uploadPath.endsWith("\\")) {
            uploadPath = uploadPath + "/";
        }

        logger.info("配置上传目录资源处理器: {}", uploadPath);

        // 确保目录存在
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    logger.info("上传目录已创建: {}", uploadPath);
                } else {
                    logger.error("无法创建上传目录: {}", uploadPath);
                }
            } else {
                logger.info("上传目录已存在: {}", uploadPath);
                // 检查目录权限
                if (dir.canRead()) {
                    logger.info("上传目录可读");
                } else {
                    logger.error("上传目录不可读");
                }
                if (dir.canWrite()) {
                    logger.info("上传目录可写");
                } else {
                    logger.error("上传目录不可写");
                }
            }

            // 列出目录中的文件
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                logger.info("上传目录中的文件:");
                for (File file : files) {
                    logger.info(" - {}", file.getName());
                }
            } else {
                logger.info("上传目录为空");
            }
        } catch (Exception e) {
            logger.error("创建上传目录失败: {}", e.getMessage(), e);
        }

        // 添加资源映射，确保以file:开头，并且以/结尾
        String resourceLocation = "file:" + uploadPath;
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        logger.info("资源映射: /uploads/** -> {}", resourceLocation);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);

        // 记录静态资源映射配置
        logger.info("已配置静态资源映射: /uploads/** -> {}", resourceLocation);
    }
}
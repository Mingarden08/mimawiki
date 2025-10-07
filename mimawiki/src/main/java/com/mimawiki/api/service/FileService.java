package com.mimawiki.api.service;

import com.mimawiki.api.util.FileUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class FileService {


    @Value("${pkg.imgLocation}")
    String imageStoragePath;


    public String uploadFile(MultipartFile file) throws MalformedURLException, BadRequestException {
        if (file == null) {
            throw new BadRequestException();
        }

        String fileExt = FileUtils.extension(file.getContentType());    // 확장자 추출

        //TODO: - 프로필 확장자 체크 필요 (jpg, jpeg, png만 허용)

        log.debug("** fileUpload originalFileName = {} **", file.getOriginalFilename());
        if (fileExt.equals(".jpg") || fileExt.equals(".jpeg") || fileExt.equals(".png")) {
            String originalFileName = file.getOriginalFilename();
            String fileName = this.getNewFileName(FileUtils.extension(file.getContentType()));
            String url = this.uploadLocal(fileName, file);
            log.info(">>>>  fileUpload fileurl {}**", url);
            return url;
        } else {
            return null;
        }
    }


    private String uploadLocal(String filename, MultipartFile file) throws MalformedURLException {
        String rootPath = imageStoragePath;
        LocalDate nowDate = LocalDate.now();
        String datePath = String.format("%04d", nowDate.getYear()) + "/"
                + String.format("%02d", nowDate.getMonth().getValue()) + "/"
                + String.format("%02d", nowDate.getDayOfMonth());

        FileUtils.hasDirectoryAndMkDir(imageStoragePath+ "/" + datePath);
        try {
            File dir = new File(rootPath + "/" + datePath);
            if (!dir.exists()) {
                boolean mkdir = dir.mkdirs();
                log.debug(" make file success {}" ,mkdir);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, filename));
            BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream);
            stream.write(file.getBytes());
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }

        // File.separator 사용하면 안된다. url 전용 "/" 사용
        String uploadedURLPath = "http://3.38.185.232:8080/images/" + datePath + "/" + filename;
        return uploadedURLPath;
    }

    /* 새 파일명 규칙 적용 */
    public String getNewFileName(String fileExt) {
        String randomStr = UUID.randomUUID().toString().replace("-", "");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formatTime = LocalDateTime.now().format(dateTimeFormatter);
        File file = new File("/home/ubuntu/project/fileUpload/2025/06/10/Miven_d5cc287db9964c5ebcdf89ae1fdee037_20250610_001934_.png");
        log.info(">>>> test = {}", file.exists()); // true 나와야 정상
        log.info(">>>> test = {}", file.canRead());
        log.info("** fileUpload fileName = {} **", "Miven" + "_" + randomStr + "_" + formatTime + "_" + fileExt);
        return "Miven" + "_" + randomStr + "_" + formatTime + "_" + fileExt; // Miven_(random)_20220101_094530_jpg
    }
}

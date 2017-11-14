package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.OriginFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OriginFileRepository extends JpaRepository<OriginFile,Long>{
    @Query(value = "INSERT INTO RNO_ORIGIN_FILE (ID,FILENAME,FILE_TYPE,FILE_SIZE,FULL_PATH,DATA_TYPE,DATA_ATTR,CREATED_USER) VALUES (1,'邻区', 'ZIP', 50727, 'D://tmp', 'lte-ncell-data', '上传', 'lucas')",nativeQuery = true)
    public void saveOriginFile();
}

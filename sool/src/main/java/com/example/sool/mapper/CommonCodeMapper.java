package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CodeGroupDto;
import com.example.sool.dto.CommonCodeDto;

@Mapper
public interface CommonCodeMapper {

    //공통 코드 그룹
    int insertCodeGroup(CodeGroupDto codeGroupDto);

    CodeGroupDto selectCodeGroupById(String group_code);

    List<CodeGroupDto> selectCodeGroupList();

    int updateCodeGroup(CodeGroupDto codeGroupDto);

    int deleteCodeGroup(String group_code);


    //공통 코드
    int insertCommonCode(CommonCodeDto commonCodeDto);

    CommonCodeDto selectCommonCodeById(int code_id);
    
    List<CommonCodeDto> selectCommonCodeList();

    List<CommonCodeDto> selectCommonCodeListByGroupCode(String group_code);

    List<CommonCodeDto> selectCategoryList();

    int updateCommonCode(CommonCodeDto commonCodeDto);

    int deleteCommonCode(int code_id);

}
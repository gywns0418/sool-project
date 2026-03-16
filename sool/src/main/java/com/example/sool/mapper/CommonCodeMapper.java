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

    //홈 카레고리 빠르게 탐색
    List<CommonCodeDto> selectCategoryList();

    //주류 카테고리 리스트 조회
    List<CommonCodeDto> getDrinkCategoryList();

    int insertCommonCode(CommonCodeDto commonCodeDto);

    CommonCodeDto selectCommonCodeById(int code_id);
    
    List<CommonCodeDto> selectCommonCodeList();

    



    int updateCommonCode(CommonCodeDto commonCodeDto);

    int deleteCommonCode(int code_id);

}
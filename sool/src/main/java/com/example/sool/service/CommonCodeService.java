package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.CodeGroupDto;
import com.example.sool.dto.CommonCodeDto;
import com.example.sool.mapper.CommonCodeMapper;

@Service
public class CommonCodeService {

    private final CommonCodeMapper commonCodeMapper;

    public CommonCodeService(CommonCodeMapper commonCodeMapper) {
        this.commonCodeMapper = commonCodeMapper;
    }

    //공통 코드 그룹

    //공통코드 그룹 등록
    public int insertCodeGroup(CodeGroupDto codeGroupDto) {
        return commonCodeMapper.insertCodeGroup(codeGroupDto);
    }

    //공통코드 그룹 단건 조회
    public CodeGroupDto selectCodeGroupById(String group_code) {
        return commonCodeMapper.selectCodeGroupById(group_code);
    }

    //공통코드 그룹 전체 조회
    public List<CodeGroupDto> selectCodeGroupList() {
        return commonCodeMapper.selectCodeGroupList();
    }

    //공통코드 그룹 수정
    public int updateCodeGroup(CodeGroupDto codeGroupDto) {
        return commonCodeMapper.updateCodeGroup(codeGroupDto);
    }

    //공통코드 그룹 삭제
    public int deleteCodeGroup(String group_code) {
        return commonCodeMapper.deleteCodeGroup(group_code);
    }


    //공통 코드

    //홈 카테고리로 빠르게 탐색
    public List<CommonCodeDto> selectCategoryList(){
        return commonCodeMapper.selectCategoryList();
    }

    //주류 카테고리 리스트 조회
    public List<CommonCodeDto> getDrinkCategoryList() {
        return commonCodeMapper.getDrinkCategoryList();
    }


    //공통코드 등록
    public int insertCommonCode(CommonCodeDto commonCodeDto) {
        return commonCodeMapper.insertCommonCode(commonCodeDto);
    }

    //공통코드 단건 조회
    public CommonCodeDto selectCommonCodeById(int code_id) {
        return commonCodeMapper.selectCommonCodeById(code_id);
    }

    //공통코드 전체 조회
    public List<CommonCodeDto> selectCommonCodeList() {
        return commonCodeMapper.selectCommonCodeList();
    }





    //공통코드 수정
    public int updateCommonCode(CommonCodeDto commonCodeDto) {
        return commonCodeMapper.updateCommonCode(commonCodeDto);
    }

    //공통코드 삭제
    public int deleteCommonCode(int code_id) {
        return commonCodeMapper.deleteCommonCode(code_id);
    }
}
package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.mapper.CommonCodeMapper;

@Service
public class CommonCodeService {

    private final CommonCodeMapper commonCodeMapper;

    public CommonCodeService(CommonCodeMapper commonCodeMapper) {
        this.commonCodeMapper = commonCodeMapper;
    }

    //홈 카테고리로 빠르게 탐색
    public List<CommonCodeDto> selectCategoryList(){
        return commonCodeMapper.selectCategoryList();
    }

    //주류 카테고리 리스트 조회
    public List<CommonCodeDto> getDrinkCategoryList() {
        return commonCodeMapper.getDrinkCategoryList();
    }

}
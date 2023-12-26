package com.wanmi.sbc.setting.imonlineservice.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceLimitWordRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.AllCustomerServiceLimitWordResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceLimitWordResponse;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceLimitWordRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceLimitWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerServiceLimitWordService {

    @Autowired
    private CustomerServiceLimitWordRepository customerServiceLimitWordRepository;

    private char[] metacharacter = {'^', '$', '*', '+', '?', '|', '\\'};

    public CustomerServiceLimitWord add(CustomerServiceLimitWordRequest request) {
        CustomerServiceLimitWord customerServiceLimitWord = KsBeanUtil.convert(request, CustomerServiceLimitWord.class);
        parseRegex(customerServiceLimitWord);
        customerServiceLimitWord.setCreateTime(LocalDateTime.now());
        customerServiceLimitWordRepository.save(customerServiceLimitWord);
        return customerServiceLimitWord;
    }

    private void parseRegex(CustomerServiceLimitWord customerServiceLimitWord) {
//        if (customerServiceLimitWord.getWordType().equals(1)) {
//            customerServiceLimitWord.setRegex("[\\d]"+ customerServiceLimitWord.getDigitLength());
//        }
        if (customerServiceLimitWord.getWordType().equals(1)) {
            StringBuffer stringBuffer = new StringBuffer();
            int size = 0;
            for (int i=0; i<customerServiceLimitWord.getWordContent().length(); i++) {
                char item = customerServiceLimitWord.getWordContent().charAt(i);
                if (item == '*') {
                    size ++;
                }
                else {
                    if (size > 0) {
                        stringBuffer.append("\\d{"+size+"}");
                        size = 0;
                    }
                    if (inMetacharacter(item)) {
                        stringBuffer.append("\\");
                    }
                    stringBuffer.append(item);
                }
            }
            if (size > 0) {
                stringBuffer.append("\\d{"+size+"}");
            }
            customerServiceLimitWord.setRegex(stringBuffer.toString());
        }
        else {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i=0; i<customerServiceLimitWord.getWordContent().length(); i++) {
                char item = customerServiceLimitWord.getWordContent().charAt(i);
                if (inMetacharacter(item)) {
                    stringBuffer.append("\\").append(item);
                }
                else {
                    stringBuffer.append(item);
                }
            }
            customerServiceLimitWord.setRegex(stringBuffer.toString());
        }
    }

    private boolean inMetacharacter (char c) {
        for (char item : metacharacter) {
            if (c == item) {
                return true;
            }
        }
        return false;
    }


    public static void main (String[] args) {
        String content = "早上好啊叼毛137$1111$1111朋友叼你137$1111$1112,";
        Pattern pattern = Pattern.compile("叼你|叼毛");
        StringBuffer regexStr = new StringBuffer();
        regexStr.append("[\\d]+");
        for (int i=0; i<11; i++) {
        }
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    public CustomerServiceLimitWord update(CustomerServiceLimitWordRequest request) {
        CustomerServiceLimitWord customerServiceLimitWord = customerServiceLimitWordRepository.findById(request.getWordId()).orElse(null);
        if (customerServiceLimitWord == null) {
            return add(request);
        }
        customerServiceLimitWord.setWordContent(request.getWordContent());
        customerServiceLimitWord.setWordType(request.getWordType());
        customerServiceLimitWord.setWordContent(request.getWordContent());
        customerServiceLimitWord.setDescription(request.getDescription());
        parseRegex(customerServiceLimitWord);
        customerServiceLimitWordRepository.save(customerServiceLimitWord);
        return customerServiceLimitWord;
    }

    public void delete(Long wordId) {
        customerServiceLimitWordRepository.deleteById(wordId);
    }

    public List<CustomerServiceLimitWordResponse> findByWordType(Integer wordType) {
        List<CustomerServiceLimitWord> list = customerServiceLimitWordRepository.findByWordType(wordType);
        return KsBeanUtil.convert(list, CustomerServiceLimitWordResponse.class);
    }

    public AllCustomerServiceLimitWordResponse getAll() {
        List<CustomerServiceLimitWord> list = customerServiceLimitWordRepository.findAll();
        List<CustomerServiceLimitWordResponse> responseList = KsBeanUtil.convert(list, CustomerServiceLimitWordResponse.class);
        AllCustomerServiceLimitWordResponse result = new AllCustomerServiceLimitWordResponse();
        for (CustomerServiceLimitWordResponse response : responseList) {
            if (response.getWordType().equals(1)) {
                result.getNumberList().add(response);
            }
            else if (response.getWordType().equals(2)) {
                result.getWordList().add(response);
            }
            else if (response.getWordType().equals(3)){
                result.getFormatList().add(response);
            }
        }
        return result;
    }

    public List<String> getAllRegex() {
        List<String> regexList = new ArrayList<>();
        List<CustomerServiceLimitWord> wordList = customerServiceLimitWordRepository.findAll();
        StringBuffer stringBuffer = new StringBuffer();
        wordList.forEach(word -> {
            if (word.getWordType().equals(2)) {
                stringBuffer.append(word.getRegex()).append("|");
            }
            else if (word.getWordType().equals(1) && !StringUtils.isEmpty(word.getRegex())) {
                regexList.add(word.getRegex());
            }
        });
        if (stringBuffer.length() > 0) {
            String regex = stringBuffer.substring(0, stringBuffer.length() -1);
            regexList.add(regex);
        }
        return regexList;
    }
}
